#!/usr/bin/env python3
"""Generate the Kotlin protocol layer from the Codex app-server JSON Schema.

The `codex` binary emits its own schema, so the generated types always match the
runtime they will talk to:

    codex app-server generate-json-schema --out <dir>

This mirrors how the reference Python SDK produces `generated/v2_all.py`, and is the
only tractable way to stay at parity: the protocol carries ~570 types across ~85 client
requests, ~10 server requests, and ~66 server notifications.

Usage:
    python3 scripts/generate_protocol.py                  # regenerate from the local binary
    python3 scripts/generate_protocol.py --schema-dir DIR # reuse an existing schema dump
    python3 scripts/generate_protocol.py --check          # report drift, write nothing

`--check` exits 1 when the installed binary's protocol no longer matches the committed
generated code. Run it in CI to be told upstream moved, instead of finding out from a
runtime error.
"""

from __future__ import annotations

import argparse
import glob
import hashlib
import json
import keyword
import os
import re
import shutil
import subprocess
import sys
import tempfile
from collections import OrderedDict
from pathlib import Path

REPO_ROOT = Path(__file__).resolve().parents[1]
OUT_DIR = REPO_ROOT / "kodachi" / "src" / "main" / "kotlin" / "dev" / "kodachi" / "protocol"
PACKAGE = "dev.kodachi.protocol"
BUNDLE_NAME = "codex_app_server_protocol.v2.schemas.json"

# Envelope types that describe the JSON-RPC framing itself. The transport handles
# framing, so these would only add noise.
SKIP_DEFINITIONS = {
    "ClientRequest",
    "ClientNotification",
    "ServerRequest",
    "ServerNotification",
    "ServerRequestPayload",
    "JSONRPCMessage",
    "JSONRPCRequest",
    "JSONRPCResponse",
    "JSONRPCNotification",
    "JSONRPCError",
    "JSONRPCErrorError",
    "JSONRPCBatchRequest",
    "JSONRPCBatchResponse",
    # The bundle's own aggregate root; carries no payload of its own.
    "CodexAppServerProtocol",
}

# Schemas whose shape is a bare scalar union; a hand-picked Kotlin type beats anything
# generic codegen would invent.
TYPE_OVERRIDES = {
    "RequestId": "JsonPrimitive",
    "ThreadListCwdFilter": "JsonElement",
    "FunctionCallOutputBody": "JsonElement",
    "AbsolutePathBuf": "String",
    "LegacyAppPathString": "String",
    "AgentPath": "String",
    "ReasoningEffort": "String",
    "ThreadSource": "String",
}

# Client requests whose response type the naming convention cannot derive.
RESPONSE_OVERRIDES = {
    "config/mcpServer/reload": "McpServerRefreshResponse",
    "account/logout": "LogoutAccountResponse",
    "account/rateLimits/read": "GetAccountRateLimitsResponse",
    "account/usage/read": "GetAccountTokenUsageResponse",
    "config/value/write": "ConfigWriteResponse",
    "config/batchWrite": "ConfigWriteResponse",
}

# First path segment -> Kotlin API class name.
API_GROUPS = {
    "account": "AccountApi",
    "app": "AppsApi",
    "attestation": "AttestationApi",
    "command": "CommandApi",
    "config": "ConfigApi",
    "configRequirements": "ConfigRequirementsApi",
    "experimentalFeature": "ExperimentalFeatureApi",
    "externalAgentConfig": "ExternalAgentConfigApi",
    "feedback": "FeedbackApi",
    "fs": "FsApi",
    "fuzzyFileSearch": "FuzzyFileSearchApi",
    "hooks": "HooksApi",
    "marketplace": "MarketplaceApi",
    "mcpServer": "McpServerApi",
    "mcpServerStatus": "McpServerStatusApi",
    "model": "ModelApi",
    "modelProvider": "ModelProviderApi",
    "permissionProfile": "PermissionProfileApi",
    "plugin": "PluginsApi",
    "review": "ReviewApi",
    "skills": "SkillsApi",
    "thread": "ThreadsApi",
    "turn": "TurnsApi",
    "windowsSandbox": "WindowsSandboxApi",
}

# Handled by the hand-written ergonomic layer instead of a generated API class.
API_SKIP_METHODS = {"initialize"}

KOTLIN_HARD_KEYWORDS = {
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if",
    "in", "interface", "is", "null", "object", "package", "return", "super", "this",
    "throw", "true", "try", "typealias", "typeof", "val", "var", "when", "while",
}


# ---------------------------------------------------------------------------
# Schema loading
# ---------------------------------------------------------------------------

def codex_version(codex_bin: str) -> str:
    """Version string of the binary the schema came from, e.g. `0.146.0`."""
    try:
        out = subprocess.run([codex_bin, "--version"], capture_output=True, text=True, timeout=30)
    except Exception:
        return "unknown"
    text = (out.stdout or out.stderr or "").strip()
    match = re.search(r"(\d+\.\d+\.\d+\S*)", text)
    return match.group(1) if match else (text or "unknown")


def schema_fingerprint(defs: "OrderedDict[str, dict]") -> str:
    """Stable hash of the whole definition universe.

    Lets `--check` tell "upstream moved" apart from "someone edited generated files".
    """
    canonical = json.dumps({k: defs[k] for k in sorted(defs)}, sort_keys=True)
    return "sha256:" + hashlib.sha256(canonical.encode()).hexdigest()[:32]


def generate_schema(out_dir: Path, codex_bin: str) -> Path:
    if out_dir.exists():
        shutil.rmtree(out_dir)
    out_dir.mkdir(parents=True)
    subprocess.run(
        [codex_bin, "app-server", "generate-json-schema", "--out", str(out_dir)],
        check=True,
    )
    return out_dir


def load_definitions(schema_dir: Path) -> "OrderedDict[str, dict]":
    """Merge every schema file into one definition universe.

    The aggregate bundle is canonical and wins on conflict; the per-method files then
    contribute names the bundle does not reach (`InitializeResponse`, for one).
    Conflicts across files are cosmetic (a `$schema` key), verified when this was written.
    """
    defs: "OrderedDict[str, dict]" = OrderedDict()

    bundle = schema_dir / BUNDLE_NAME
    if not bundle.exists():
        raise SystemExit(f"missing schema bundle: {bundle}")
    for name, schema in json.loads(bundle.read_text())["definitions"].items():
        defs.setdefault(name, schema)

    others = [
        Path(p)
        for p in sorted(glob.glob(str(schema_dir / "*.json")) + glob.glob(str(schema_dir / "v2" / "*.json")))
        if Path(p).name != BUNDLE_NAME
    ]
    for path in others:
        doc = json.loads(path.read_text())
        for name, schema in (doc.get("definitions") or {}).items():
            defs.setdefault(name, schema)
        title = doc.get("title")
        if title:
            own = {k: v for k, v in doc.items() if k not in ("$schema", "definitions")}
            defs.setdefault(title, own)

    # The binary emits definition keys in an unstable order between invocations, so sort
    # them. Generated output then depends only on the schema's content, which is what
    # makes `--check` meaningful and keeps regeneration diffs reviewable.
    return OrderedDict(sorted(defs.items()))


def load_envelope(schema_dir: Path, filename: str) -> list[dict]:
    """Return (method, params_type, description) for an envelope's oneOf branches."""
    path = schema_dir / filename
    if not path.exists():
        return []
    rows = []
    for branch in json.loads(path.read_text()).get("oneOf", []):
        props = branch.get("properties") or {}
        method_enum = (props.get("method") or {}).get("enum") or []
        if not method_enum:
            continue
        params_ref = (props.get("params") or {}).get("$ref", "")
        result_ref = (props.get("result") or {}).get("$ref", "")
        rows.append(
            {
                "method": method_enum[0],
                "params": params_ref.split("/")[-1] or None,
                "result": result_ref.split("/")[-1] or None,
                "description": branch.get("description"),
            }
        )
    return rows


# ---------------------------------------------------------------------------
# Naming
# ---------------------------------------------------------------------------

def kotlin_prop_name(wire: str) -> str:
    """Wire name -> idiomatic Kotlin property name."""
    if "_" in wire:
        head, *rest = wire.split("_")
        name = head + "".join(p[:1].upper() + p[1:] for p in rest if p)
    else:
        name = wire
    name = name[:1].lower() + name[1:] if name else name
    if name in KOTLIN_HARD_KEYWORDS or keyword.iskeyword(name):
        return f"`{name}`"
    if not name or not re.match(r"^[A-Za-z_]", name):
        return f"`{wire}`"
    return name


def kotlin_class_name(title: str) -> str:
    """Schema title -> legal Kotlin class name.

    Some titles carry a Rust module path, e.g. `ApiKeyv2::LoginAccountParams`.
    """
    name = re.sub(r"\bv\d+::", "", title)
    name = re.sub(r"[^A-Za-z0-9_]", "", name)
    if name and name[0].isdigit():
        name = "N" + name
    return name


def enum_entry_name(value: str) -> str:
    """Enum wire value -> Kotlin enum entry name."""
    name = re.sub(r"[^A-Za-z0-9]+", "_", value)
    name = re.sub(r"(?<=[a-z0-9])(?=[A-Z])", "_", name)
    name = name.upper().strip("_")
    if not name:
        name = "EMPTY"
    if name[0].isdigit():
        name = "N" + name
    return name


def enum_entry_names(values: list[str]) -> tuple["OrderedDict[str, str]", str]:
    """Wire values -> unique entry names, plus the name left free for the UNKNOWN entry.

    Two wire values can collapse onto one entry name (`on-request` and `on_request` would),
    so the mapping is computed once and shared: anything that has to name an entry — the
    enum itself, a companion constant pointing at it — must agree on the result.
    """
    mapping: "OrderedDict[str, str]" = OrderedDict()
    seen: set[str] = set()
    for value in values:
        entry = enum_entry_name(value)
        while entry in seen:
            entry += "_"
        seen.add(entry)
        mapping[value] = entry
    unknown = "UNKNOWN"
    while unknown in seen:
        unknown += "_"
    return mapping, unknown


def pascal_name(wire: str) -> str:
    """Wire key -> Kotlin type name, e.g. `thread_spawn` -> `ThreadSpawn`."""
    return enum_entry_name(wire).title().replace("_", "")


def escape_kdoc(text: str | None) -> list[str]:
    if not text:
        return []
    cleaned = text.replace("*/", "*&#47;").replace("/*", "&#47;*").strip()
    lines: list[str] = []
    for raw in cleaned.split("\n"):
        lines.extend(_wrap(raw.strip(), 92) or [""])
    return lines


def emit_kdoc(text: str | None, into: list[str], indent: str = "") -> None:
    lines = escape_kdoc(text)
    if not lines:
        return
    into.append(f"{indent}/**")
    into.extend(f"{indent} * {l}" if l else f"{indent} *" for l in lines)
    into.append(f"{indent} */")


def _wrap(text: str, width: int) -> list[str]:
    if not text:
        return []
    words, out, cur = text.split(), [], ""
    for w in words:
        if cur and len(cur) + 1 + len(w) > width:
            out.append(cur)
            cur = w
        else:
            cur = f"{cur} {w}".strip()
    if cur:
        out.append(cur)
    return out


# ---------------------------------------------------------------------------
# Classification
# ---------------------------------------------------------------------------

def is_string_enum(schema: dict) -> bool:
    return schema.get("type") == "string" and isinstance(schema.get("enum"), list)


def one_of_string_enum_values(schema: dict) -> list[str] | None:
    subs = schema.get("oneOf")
    if not isinstance(subs, list) or not subs:
        return None
    values: list[str] = []
    for sub in subs:
        if not isinstance(sub, dict) or sub.get("type") != "string" or not isinstance(sub.get("enum"), list):
            return None
        values.extend(sub["enum"])
    return values


# Discriminator keys seen in the schema, in the order we prefer them.
DISCRIMINATOR_KEYS = ("type", "kind", "mode")


def discriminator_key(subs: list) -> str | None:
    """The property every variant pins to a single literal, i.e. the tag."""
    for key in DISCRIMINATOR_KEYS:
        if all(
            isinstance(sub, dict)
            and ((sub.get("properties") or {}).get(key) or {}).get("enum")
            for sub in subs
        ):
            return key
    return None


def tagged_variants(schema: dict) -> tuple | None:
    """Variants of an internally tagged union, with the name of its discriminator."""
    subs = schema.get("oneOf")
    if not isinstance(subs, list) or not subs:
        return None
    # A schema carrying its own properties alongside oneOf is a hybrid, handled as an object.
    if isinstance(schema.get("properties"), dict):
        return None
    key = discriminator_key(subs)
    return (key, list(subs)) if key else None


def mixed_union(schema: dict) -> dict | None:
    """Externally tagged Rust enum: bare-string variants plus single-key object variants."""
    subs = schema.get("oneOf")
    if not isinstance(subs, list) or not subs:
        return None
    presets: list[str] = []
    keyed: list[tuple[str, dict]] = []
    for sub in subs:
        if not isinstance(sub, dict):
            return None
        if sub.get("type") == "string" and isinstance(sub.get("enum"), list):
            presets.extend(sub["enum"])
            continue
        if sub.get("type") == "object":
            required = sub.get("required") or []
            if len(required) == 1:
                keyed.append((required[0], sub))
                continue
        return None
    if not presets and not keyed:
        return None
    return {"presets": presets, "keyed": keyed}


def classify(name: str, schema: dict) -> str:
    if name in TYPE_OVERRIDES:
        return "alias_override"
    if is_string_enum(schema):
        return "enum"
    if one_of_string_enum_values(schema) is not None:
        return "enum"
    if tagged_variants(schema) is not None:
        return "tagged_union"
    if mixed_union(schema) is not None:
        return "mixed_union"
    if "allOf" in schema:
        return "alias"
    if "anyOf" in schema:
        return "alias"
    t = schema.get("type")
    if t == "object":
        if isinstance(schema.get("properties"), dict):
            return "object"
        if "additionalProperties" in schema:
            return "alias"
        return "object"  # empty object -> data class with no properties
    if t in ("string", "integer", "number", "boolean", "array"):
        return "alias"
    return "alias"


# ---------------------------------------------------------------------------
# Type mapping
# ---------------------------------------------------------------------------

INT_FORMATS_32 = {"int32", "uint32", "uint16", "uint8"}


class Generator:
    def __init__(
        self,
        defs: "OrderedDict[str, dict]",
        schema_dir: Path,
        out_dir: Path = OUT_DIR,
        codex_version: str = "unknown",
    ):
        self.defs = defs
        self.schema_dir = schema_dir
        self.out_dir = out_dir
        self.codex_version = codex_version
        self.fingerprint = schema_fingerprint(defs)
        self.kinds = {n: classify(n, s) for n, s in defs.items() if n not in SKIP_DEFINITIONS}
        self.nested: list[str] = []  # extra top-level declarations emitted for inline objects
        self.emitted_names: set[str] = set(defs) - SKIP_DEFINITIONS
        self.promoted: dict[str, str] = {}  # promoted inline class -> canonical schema JSON
        self.notifications = load_envelope(schema_dir, "ServerNotification.json")
        self.client_requests = load_envelope(schema_dir, "ClientRequest.json")
        self.server_requests = load_envelope(schema_dir, "ServerRequest.json")
        self.client_notifications = load_envelope(schema_dir, "ClientNotification.json")
        self.notification_by_params = {
            row["params"]: row["method"] for row in self.notifications if row["params"]
        }

    # -- type resolution ---------------------------------------------------

    def ref_type(self, ref: str) -> str:
        name = ref.split("/")[-1]
        if name in TYPE_OVERRIDES:
            return TYPE_OVERRIDES[name]
        if name in SKIP_DEFINITIONS or name not in self.defs:
            return "JsonElement"
        return name

    def map_type(self, schema, owner: str, prop: str) -> tuple[str, bool]:
        """Return (kotlin_type, nullable) for a property schema."""
        if schema is True or schema is None:
            return "JsonElement", False
        if schema is False:
            return "JsonElement", False
        if not isinstance(schema, dict):
            return "JsonElement", False

        if "$ref" in schema:
            return self.ref_type(schema["$ref"]), False

        if "allOf" in schema:
            parts = [p for p in schema["allOf"] if isinstance(p, dict)]
            refs = [p for p in parts if "$ref" in p]
            if len(refs) == 1:
                return self.ref_type(refs[0]["$ref"]), False
            if len(parts) == 1:
                return self.map_type(parts[0], owner, prop)
            return "JsonElement", False

        for key in ("anyOf", "oneOf"):
            if key in schema:
                subs = [s for s in schema[key] if isinstance(s, dict)]
                non_null = [s for s in subs if s.get("type") != "null"]
                nullable = len(non_null) != len(subs)
                if len(non_null) == 1:
                    inner, inner_null = self.map_type(non_null[0], owner, prop)
                    return inner, nullable or inner_null
                # A union of scalars/arrays: keep the raw element rather than guess.
                return "JsonElement", nullable

        t = schema.get("type")
        if isinstance(t, list):
            nullable = "null" in t
            rest = [x for x in t if x != "null"]
            if len(rest) == 1:
                inner, inner_null = self.map_type({**schema, "type": rest[0]}, owner, prop)
                return inner, nullable or inner_null
            return "JsonElement", nullable

        if t == "array":
            items = schema.get("items")
            inner, _ = self.map_type(items, owner, prop) if items is not None else ("JsonElement", False)
            return f"List<{inner}>", False

        if t == "object":
            if isinstance(schema.get("properties"), dict):
                # Inline anonymous object: promote it to a named class.
                nested_name = schema.get("title") or f"{owner}{prop[:1].upper()}{prop[1:]}"
                return self.promote_object(nested_name, schema), False
            additional = schema.get("additionalProperties")
            if isinstance(additional, dict):
                inner, _ = self.map_type(additional, owner, prop)
                return f"Map<String, {inner}>", False
            if additional is True:
                return "JsonObject", False
            return "JsonObject", False

        if t == "string":
            if isinstance(schema.get("enum"), list) and len(schema["enum"]) == 1:
                return "String", False  # discriminator literal
            return "String", False
        if t == "integer":
            return ("Int" if schema.get("format") in INT_FORMATS_32 else "Long"), False
        if t == "number":
            return "Double", False
        if t == "boolean":
            return "Boolean", False
        if t == "null":
            return "JsonElement", True

        return "JsonElement", False

    def reserve_name(self, name: str) -> str:
        """Claim a top-level Kotlin name, suffixing until it is free.

        Every schema definition already holds its own name, so anything the generator
        invents on the side has to go through here or it silently shadows a real type.
        """
        while name in self.emitted_names:
            name += "_"
        self.emitted_names.add(name)
        return name

    def promote_object(self, name: str, schema: dict) -> str:
        """Emit an inline object as its own top-level class, returning the name used.

        One property can be mapped twice — a tagged union probes its variants' types before
        emitting them — so promotion has to be idempotent for an identical schema, and take a
        fresh name when a different schema wants one already spoken for.
        """
        canonical = json.dumps(schema, sort_keys=True)
        if self.promoted.get(name) == canonical:
            return name
        name = self.reserve_name(name)
        self.promoted[name] = canonical
        self.emit_object(name, schema, into=self.nested)
        return name

    def default_for(self, kotlin_type: str, nullable: bool, schema) -> str | None:
        """Default value for an optional property, or None to force it required."""
        if nullable:
            return "null"
        if isinstance(schema, dict) and "default" in schema:
            d = schema["default"]
            if isinstance(d, bool):
                return "true" if d else "false"
            if isinstance(d, (int, float)) and kotlin_type in ("Int", "Long", "Double"):
                return str(d)
            if isinstance(d, str) and kotlin_type == "String":
                return json.dumps(d)
            if isinstance(d, list) and kotlin_type.startswith("List<"):
                return "emptyList()" if not d else None
            if isinstance(d, dict) and kotlin_type.startswith("Map<"):
                return "emptyMap()" if not d else None
        if kotlin_type.startswith("List<"):
            return "emptyList()"
        if kotlin_type.startswith("Map<"):
            return "emptyMap()"
        return "null"  # fall back to nullable; caller widens the type

    # -- emitters ----------------------------------------------------------

    def emit_object(
        self,
        name: str,
        schema: dict,
        into: list[str],
        implements: list[str] | None = None,
        overrides: dict[str, str] | None = None,
        extra_members: list[str] | None = None,
        discriminator: tuple[str, str] | None = None,
    ) -> None:
        props: dict = dict(schema.get("properties") or {})
        required = set(schema.get("required") or [])
        overrides = overrides or {}

        # Hybrid schema: own properties PLUS a oneOf of variants. Emitting only the shared
        # half silently drops each variant's payload — for the MCP elicitation request that
        # is the message, the mode and the requested schema, i.e. all of the content. Fold
        # them in as optional fields: flatter than a nested union, but lossless.
        variant_only: list[str] = []
        for branch in schema.get("oneOf") or []:
            if not isinstance(branch, dict):
                continue
            for wire, ps in (branch.get("properties") or {}).items():
                if wire not in props:
                    props[wire] = ps
                    variant_only.append(wire)
        # A variant field only appears for its own variant, so it can never be required.
        required -= set(variant_only)

        required_params: list[str] = []
        optional_params: list[str] = []

        for wire, prop_schema in props.items():
            if discriminator and wire == discriminator[0]:
                continue  # emitted last, with its literal default
            ktype, nullable = self.map_type(prop_schema, name, wire)
            is_required = wire in required
            if not is_required:
                nullable = nullable or not (ktype.startswith("List<") or ktype.startswith("Map<"))
            kname = kotlin_prop_name(wire)
            prefix = "override " if wire in overrides else ""
            serial = f'@SerialName("{wire}") ' if kname.strip("`") != wire else ""
            decl_type = f"{ktype}?" if nullable else ktype

            if is_required:
                required_params.append(f"    {serial}{prefix}val {kname}: {decl_type},")
            else:
                default = self.default_for(ktype, nullable, prop_schema)
                if default is None:
                    # No Kotlin literal for this default; absence becomes an explicit null.
                    default = "null"
                if default == "null" and not nullable:
                    decl_type = f"{ktype}?"
                optional_params.append(f"    {serial}{prefix}val {kname}: {decl_type} = {default},")

        if discriminator:
            wire, literal = discriminator
            prefix = "override " if wire in overrides else ""
            # Must survive encodeDefaults=false: the server rejects a variant with no tag.
            optional_params.append("    @EncodeDefault(EncodeDefault.Mode.ALWAYS)")
            optional_params.append(f'    {prefix}val {wire}: String = "{literal}",')

        params = required_params + optional_params
        impl = f" : {', '.join(implements)}" if implements else ""

        emit_kdoc(schema.get("description"), into)
        into.append("@Serializable")
        if not params:
            # A no-field payload still has to be a class so it can carry future fields.
            into.append(f"public class {name}{impl}")
            if extra_members:
                into[-1] += " {"
                into.extend(extra_members)
                into.append("}")
            into.append("")
            return

        into.append(f"public data class {name}(")
        into.extend(params)
        if extra_members:
            into.append(f"){impl} {{")
            into.extend(extra_members)
            into.append("}")
        else:
            into.append(f"){impl}")
        into.append("")

    def emit_enum(self, name: str, schema: dict, into: list[str]) -> tuple[dict[str, str], str]:
        """Emit a forward-compatible enum; return its wire -> entry map and UNKNOWN entry."""
        values = schema.get("enum") if is_string_enum(schema) else one_of_string_enum_values(schema)
        values = list(OrderedDict.fromkeys(values or []))
        entries, unknown = enum_entry_names(values)
        emit_kdoc(schema.get("description"), into)
        into.append(f"@Serializable(with = {name}Serializer::class)")
        into.append(f"public enum class {name}(override val wire: String) : WireEnum {{")
        for v in values:
            into.append(f'    {entries[v]}("{v}"),')
        into.append("")
        into.append("    /** A value this SDK version does not know. Never produced by [wire] round-trips. */")
        into.append(f'    {unknown}(""),')
        into.append("    ;")
        into.append("")
        into.append("    public companion object {")
        into.append("        private val BY_WIRE: Map<String, " + name + "> =")
        into.append("            entries.associateBy { it.wire }")
        into.append("")
        into.append(f"        /** Decode a wire value, falling back to [{unknown}] for anything newer. */")
        into.append(f"        public fun fromWire(value: String): {name} = BY_WIRE[value] ?: {unknown}")
        into.append("    }")
        into.append("}")
        into.append("")
        into.append(
            f"internal object {name}Serializer : "
            f'WireEnumSerializer<{name}>("{name}", {name}::fromWire)'
        )
        into.append("")
        return entries, unknown

    def emit_tagged_union(self, name: str, schema: dict, into: list[str]) -> None:
        tag_key, variants = tagged_variants(schema) or ("type", [])
        # Interface members: required properties every variant shares with one type.
        common: dict[str, str] = {}
        first = True
        for variant in variants:
            props = variant.get("properties") or {}
            req = set(variant.get("required") or [])
            local: dict[str, str] = {}
            for wire, ps in props.items():
                if wire == tag_key or wire not in req:
                    continue
                ktype, nullable = self.map_type(ps, name, wire)
                if nullable or ktype not in ("String", "Long", "Int", "Boolean"):
                    continue
                local[wire] = ktype
            if first:
                common, first = local, False
            else:
                common = {k: v for k, v in common.items() if local.get(k) == v}

        emit_kdoc(schema.get("description"), into)
        into.append(f"@Serializable(with = {name}Serializer::class)")
        into.append(f"public sealed interface {name} {{")
        into.append('    /** Wire discriminator for this variant (`' + tag_key + '`). */')
        into.append('    public val ' + tag_key + ': String')
        for wire, ktype in common.items():
            into.append(f"    public val {kotlin_prop_name(wire)}: {ktype}")
        into.append("}")
        into.append("")

        overrides = {tag_key: tag_key, **{w: w for w in common}}
        tags: list[tuple[str, str]] = []
        for variant in variants:
            tag = ((variant.get("properties") or {}).get(tag_key) or {}).get("enum")[0]
            raw_title = variant.get("title") or f"{name}{enum_entry_name(tag).title().replace('_', '')}"
            vname = kotlin_class_name(raw_title) or f"{name}Variant{len(tags)}"
            while vname in self.emitted_names:
                vname = f"{vname}_"
            self.emitted_names.add(vname)
            tags.append((tag, vname))
            self.emit_object(
                vname,
                variant,
                into=into,
                implements=[name],
                overrides=overrides,
                discriminator=(tag_key, tag),
            )

        # Some unions already declare a variant literally named `Unknown…`
        # (CommandAction does), so the fallback carrier needs a free name.
        unknown = f"Unknown{name}"
        if unknown in self.emitted_names:
            unknown = f"Unrecognized{name}"
        while unknown in self.emitted_names:
            unknown += "_"
        self.emitted_names.add(unknown)
        into.append("/**")
        into.append(f" * A [{name}] variant this SDK version does not model. [raw] keeps the payload")
        into.append(" * so nothing is lost, and no unrecognized variant can fail a decode.")
        into.append(" */")
        into.append(f"@Serializable(with = {unknown}Serializer::class)")
        into.append(f"public data class {unknown}(override val raw: JsonObject) : {name}, RawPayload {{")
        into.append('    override val ' + tag_key + ': String get() = raw.stringOrEmpty("' + tag_key + '")')
        for wire, ktype in common.items():
            accessor = self._raw_accessor(wire, ktype)
            into.append(f"    override val {kotlin_prop_name(wire)}: {ktype} get() = {accessor}")
        into.append("}")
        into.append("")
        into.append(
            f"internal object {unknown}Serializer : RawPayloadSerializer<{unknown}>({{ {unknown}(it) }})"
        )
        into.append("")
        into.append(
            f"internal object {name}Serializer : "
            f"TaggedUnionSerializer<{name}>({name}::class, \"{tag_key}\", mapOf("
        )
        for tag, vname in tags:
            into.append(f'    "{tag}" to {vname}.serializer(),')
        into.append(f"), {unknown}Serializer)")
        into.append("")

    def _raw_accessor(self, wire: str, ktype: str) -> str:
        if ktype == "String":
            return f'raw.stringOrEmpty("{wire}")'
        if ktype in ("Long", "Int"):
            return f'raw.numberOrZero("{wire}").to{ktype}()'
        if ktype == "Boolean":
            return f'raw.booleanOrFalse("{wire}")'
        if ktype.startswith("List<"):
            return "emptyList()"
        return f'raw.stringOrEmpty("{wire}")'

    def emit_mixed_union(self, name: str, schema: dict, into: list[str]) -> None:
        """Externally tagged enum: either a bare string, or a single-key object.

        Emitted as a sealed hierarchy, not a raw-JSON wrapper, so a caller can `when` over
        the variants and have the compiler check the branches. The bare-string presets become
        their own enum: they are the closed part of the union, and separating them keeps the
        object variants free to carry typed payloads.
        """
        info = mixed_union(schema) or {"presets": [], "keyed": []}

        preset_enum = self.reserve_name(f"{name}Preset")
        self.reserve_name(f"{preset_enum}Serializer")  # claimed by emit_enum's serializer
        union_serializer = self.reserve_name(f"{name}Serializer")
        unknown_serializer = self.reserve_name(f"Unknown{name}Serializer")

        # The presets are the closed half of the union; emit them as a normal generated enum
        # so they get the same UNKNOWN entry and `fromWire` lookup as every other one, and
        # take the entry names it chose rather than deriving them a second time.
        enum_lines: list[str] = []
        entries, unknown_entry = self.emit_enum(
            preset_enum,
            {
                "type": "string",
                "enum": info["presets"],
                "description": (
                    f"The bare-string presets of [{name}].\n\nSplit out so [{name}.Preset] can be "
                    f"matched exhaustively while the union's object variants keep their payloads."
                ),
            },
            enum_lines,
        )

        # Variant classes nest inside the interface, so they only have to be unique in there.
        taken = {"Preset", "Unknown", "Companion"}
        variants: list[dict] = []
        for key, branch in info["keyed"]:
            vname = pascal_name(key) or f"Variant{len(variants)}"
            while vname in taken:
                vname += "_"
            taken.add(vname)
            payload = (branch.get("properties") or {}).get(key)
            ptype, nullable = self.map_type(payload, name, vname)
            variants.append(
                {
                    "key": key,
                    "name": vname,
                    "prop": kotlin_prop_name(key),
                    "type": f"{ptype}?" if nullable else ptype,
                    "desc": branch.get("description"),
                }
            )

        desc = escape_kdoc(schema.get("description"))
        into.append("/**")
        for l in desc:
            into.append(f" * {l}" if l else " *")
        if desc:
            into.append(" *")
        into.append(" * On the wire this is either one of the bare-string presets or a single-key object,")
        into.append(" * which is why it is sealed rather than one class: `when` over it and the compiler")
        into.append(" * checks that every variant is handled. A variant a newer app-server introduces")
        into.append(" * decodes to [Unknown] instead of failing, and encodes back exactly as it arrived.")
        into.append(" */")
        into.append(f"@Serializable(with = {union_serializer}::class)")
        into.append(f"public sealed interface {name} {{")

        into.append("    /** One of the closed set of presets. Encodes as a bare string, never an object. */")
        into.append("    @Serializable")
        into.append(f"    public data class Preset(public val value: {preset_enum}) : {name}")

        for v in variants:
            into.append("")
            # One KDoc block, not two: a second block would leave the first one dangling.
            wire_form = f'Wire form: `{{"{v["key"]}": …}}`.'
            emit_kdoc(
                f'{v["desc"]}\n\n{wire_form}' if v["desc"] else wire_form,
                into,
                indent="    ",
            )
            into.append("    @Serializable")
            serial = f'@SerialName("{v["key"]}") ' if v["prop"].strip("`") != v["key"] else ""
            into.append(
                f'    public data class {v["name"]}({serial}public val {v["prop"]}: {v["type"]}) : {name}'
            )

        into.append("")
        into.append("    /**")
        into.append(f"     * A [{name}] variant this SDK version does not model — a preset or an object key")
        into.append("     * added upstream. [raw] keeps it intact so nothing is lost in a round trip.")
        into.append("     */")
        into.append(f"    @Serializable(with = {unknown_serializer}::class)")
        into.append(f"    public data class Unknown(override val raw: JsonElement) : {name}, RawValue")
        into.append("")
        into.append("    public companion object {")
        # Typed as the union, not as [Preset]: these constants are what callers assign and
        # pass, so widening them here is what keeps that code compiling.
        for preset in info["presets"]:
            into.append(f'        /** The `{preset}` preset. */')
            into.append(
                f"        public val {entries[preset]}: {name} = Preset({preset_enum}.{entries[preset]})"
            )
        into.append("")
        into.append("        /**")
        into.append("         * Wrap a bare-string preset, including one this SDK version does not model:")
        into.append("         * an unrecognized value becomes [Unknown] carrying that exact string, rather")
        into.append(f"         * than collapsing onto [{preset_enum}.{unknown_entry}] and encoding as empty.")
        into.append("         */")
        into.append(f"        public fun of(preset: String): {name} {{")
        into.append(f"            val known = {preset_enum}.fromWire(preset)")
        into.append(f"            return if (known == {preset_enum}.{unknown_entry}) {{")
        into.append("                Unknown(JsonPrimitive(preset))")
        into.append("            } else {")
        into.append("                Preset(known)")
        into.append("            }")
        into.append("        }")
        into.append("    }")
        into.append("}")
        into.append("")
        into.extend(enum_lines)

        into.append(
            f"internal object {unknown_serializer} : "
            f"RawValueSerializer<{name}.Unknown>({{ {name}.Unknown(it) }})"
        )
        into.append("")

        # The `when` over `value` is what makes this safe: adding a variant without teaching
        # the encoder about it is a compile error, not a request the server silently rejects.
        into.append(f"internal object {union_serializer} : MixedUnionSerializer<{name}>(")
        into.append(f"    fromPreset = {{ {name}.of(it) }},")
        if variants:
            into.append("    fromKeyed = { json, key, payload ->")
            into.append("        when (key) {")
            for v in variants:
                into.append(f'            "{v["key"]}" -> {name}.{v["name"]}(')
                into.append(f'                json.decodeFromJsonElement<{v["type"]}>(payload),')
                into.append("            )")
            into.append("            else -> null")
            into.append("        }")
            into.append("    },")
        else:
            into.append("    fromKeyed = { _, _, _ -> null },")
        into.append(f"    fromUnknown = {{ {name}.Unknown(it) }},")
        into.append(f"    toElement = {{ {'json' if variants else '_'}, value ->")
        into.append("        when (value) {")
        into.append(f"            is {name}.Preset -> JsonPrimitive(value.value.wire)")
        for v in variants:
            into.append(f'            is {name}.{v["name"]} -> buildJsonObject {{')
            into.append(f'                put("{v["key"]}", json.encodeToJsonElement(value.{v["prop"]}))')
            into.append("            }")
        into.append(f"            is {name}.Unknown -> value.raw")
        into.append("        }")
        into.append("    },")
        into.append(")")
        into.append("")

    def emit_alias(self, name: str, schema: dict, into: list[str]) -> None:
        if name in TYPE_OVERRIDES:
            target = TYPE_OVERRIDES[name]
        else:
            target, nullable = self.map_type(schema, name, "value")
            if nullable:
                target = f"{target}?"
        emit_kdoc(schema.get("description"), into)
        into.append(f"public typealias {name} = {target}")
        into.append("")

    # -- notification wiring ----------------------------------------------

    def notification_members(self, params_name: str, schema: dict) -> tuple[list[str], dict[str, str]]:
        """Interface plumbing so a notification payload can route itself."""
        method = self.notification_by_params.get(params_name)
        if not method:
            return [], {}

        props = schema.get("properties") or {}
        required = set(schema.get("required") or [])
        overrides: dict[str, str] = {}
        members: list[str] = []
        if "method" in props:
            # A payload that carries its own `method` satisfies the interface directly.
            overrides["method"] = "method"
        else:
            members.append(f'    override val method: String get() = "{method}"')

        if "threadId" in props:
            overrides["threadId"] = "threadId"
        else:
            members.append("    override val threadId: String? get() = null")

        if "turnId" in props:
            overrides["turnId"] = "turnId"
        elif "turn" in props and required and "turn" in required:
            turn_type, _ = self.map_type(props["turn"], params_name, "turn")
            if turn_type == "Turn":
                # turn/started and turn/completed nest the id one level down.
                members.append("    override val turnId: String get() = turn.id")
            else:
                members.append("    override val turnId: String? get() = null")
        else:
            members.append("    override val turnId: String? get() = null")

        return members, overrides

    # -- top-level generation ---------------------------------------------

    def run(self) -> dict[str, int]:
        enums: list[str] = []
        unions: list[str] = []
        models: list[str] = []
        aliases: list[str] = []

        counts = {"enum": 0, "tagged_union": 0, "mixed_union": 0, "object": 0, "alias": 0}

        for name, schema in self.defs.items():
            if name in SKIP_DEFINITIONS:
                continue
            kind = self.kinds[name]
            if kind == "alias_override":
                continue  # represented directly by TYPE_OVERRIDES
            if kind == "enum":
                self.emit_enum(name, schema, enums)
                counts["enum"] += 1
            elif kind == "tagged_union":
                self.emit_tagged_union(name, schema, unions)
                counts["tagged_union"] += 1
            elif kind == "mixed_union":
                self.emit_mixed_union(name, schema, unions)
                counts["mixed_union"] += 1
            elif kind == "object":
                members, overrides = self.notification_members(name, schema)
                self.emit_object(
                    name,
                    schema,
                    into=models,
                    implements=["CodexNotification"] if members else None,
                    overrides=overrides,
                    extra_members=members or None,
                )
                counts["object"] += 1
            else:
                self.emit_alias(name, schema, aliases)
                counts["alias"] += 1

        write_kotlin(self.out_dir / "GeneratedEnums.kt", enums, "Enum types.")
        write_kotlin(
            self.out_dir / "GeneratedUnions.kt",
            unions,
            "Discriminated unions.",
            # The mixed unions decode and encode their variant payloads by reified type, which
            # resolves through whichever serializer the payload declares.
            extra_imports=[
                "kotlinx.serialization.json.decodeFromJsonElement",
                "kotlinx.serialization.json.encodeToJsonElement",
            ],
        )
        write_kotlin(
            self.out_dir / "GeneratedModels.kt",
            models + ([""] + self.nested if self.nested else []),
            "Object payloads, including every notification payload.",
        )
        write_kotlin(self.out_dir / "GeneratedAliases.kt", aliases, "Scalar and container aliases.")
        self.write_registry()
        self.write_methods()
        self.write_protocol_info(counts)
        counts.update(self.write_api())
        counts["nested"] = len(self.nested)
        return counts

    def write_registry(self) -> None:
        lines: list[str] = []
        lines.append("/**")
        lines.append(" * Every server notification method this protocol version defines, mapped to the")
        lines.append(" * serializer for its payload. Methods absent here decode to [UnknownNotification].")
        lines.append(" */")
        lines.append("internal val NotificationSerializers: Map<String, KSerializer<out CodexNotification>> = mapOf(")
        for row in self.notifications:
            params = row["params"]
            if not params or params in SKIP_DEFINITIONS:
                continue
            if self.kinds.get(params) != "object":
                continue
            lines.append(f'    "{row["method"]}" to {params}.serializer(),')
        lines.append(")")
        lines.append("")
        lines.append("/** Wire method names for every server notification, for tests and diagnostics. */")
        lines.append("public val ALL_SERVER_NOTIFICATION_METHODS: List<String> = listOf(")
        for row in self.notifications:
            lines.append(f'    "{row["method"]}",')
        lines.append(")")
        lines.append("")
        write_kotlin(
            self.out_dir / "GeneratedNotificationRegistry.kt",
            lines,
            "Notification dispatch table.",
            extra_imports=["kotlinx.serialization.KSerializer"],
        )

    def response_type(self, method: str, params: str | None) -> str | None:
        """Response type for a client request, by override then naming convention."""
        override = RESPONSE_OVERRIDES.get(method)
        if override and override in self.defs:
            return override
        if params and params.endswith("Params"):
            candidate = params[: -len("Params")] + "Response"
            if candidate in self.defs:
                return candidate
        parts = method.replace("/", "_").split("_")
        candidate = "".join(w[:1].upper() + w[1:] for w in parts if w) + "Response"
        return candidate if candidate in self.defs else None

    def api_fun_name(self, method: str) -> str:
        """`thread/goal/set` -> `goalSet`; the group already carries the first segment."""
        segments = method.split("/")
        tail = segments[1:] or segments
        name = tail[0] + "".join(p[:1].upper() + p[1:] for p in tail[1:])
        name = name[:1].lower() + name[1:]
        if name in KOTLIN_HARD_KEYWORDS or keyword.iskeyword(name):
            return f"`{name}`"
        return name

    def write_api(self) -> dict[str, int]:
        """Emit one class per method group, covering every client request."""
        groups: "OrderedDict[str, list[dict]]" = OrderedDict()
        for row in self.client_requests:
            if row["method"] in API_SKIP_METHODS:
                continue
            segment = row["method"].split("/")[0]
            groups.setdefault(API_GROUPS.get(segment, segment[:1].upper() + segment[1:] + "Api"), []).append(row)

        lines: list[str] = []
        lines.append("/**")
        lines.append(" * Typed access to every app-server request method, grouped by protocol namespace.")
        lines.append(" * Reach these through the properties on [dev.kodachi.Codex].")
        lines.append(" */")
        lines.append("")

        covered = 0
        used_names: set[str] = set()
        for class_name, rows in groups.items():
            lines.append("/**")
            lines.append(f" * `{rows[0]['method'].split('/')[0]}/…` requests ({len(rows)} methods).")
            lines.append(" */")
            lines.append(f"public class {class_name} internal constructor(")
            lines.append("    private val caller: ProtocolCaller,")
            lines.append(") {")
            for row in rows:
                method, params = row["method"], row["params"]
                result = self.response_type(method, params)
                const = f"ClientRequests.{enum_entry_name(method.replace('/', '_'))}"
                fun = self.api_fun_name(method)
                unique = f"{class_name}.{fun}"
                if unique in used_names:
                    fun = fun + "Request"
                used_names.add(unique)

                ret = result or "JsonElement"
                result_arg = (
                    f"{result}.serializer()" if result else "JsonElement.serializer()"
                )
                emit_kdoc(row.get("description"), lines)
                lines.append(f"    /** `{method}` */")
                if params and params in self.defs:
                    lines.append(f"    public suspend fun {fun}(params: {params}): {ret} =")
                    lines.append(f"        caller.call(")
                    lines.append(f"            {const},")
                    lines.append(f"            encodeParams({params}.serializer(), params),")
                    lines.append(f"            {result_arg},")
                    lines.append("        )")
                else:
                    lines.append(f"    public suspend fun {fun}(): {ret} =")
                    lines.append(f"        caller.call({const}, null, {result_arg})")
                lines.append("")
                covered += 1
            lines.append("}")
            lines.append("")

        write_kotlin(
            self.out_dir / "GeneratedApi.kt",
            lines,
            "Typed request API for every client request method.",
            extra_imports=["kotlinx.serialization.json.JsonElement"],
        )
        return {"api_classes": len(groups), "api_methods": covered}

    def write_protocol_info(self, counts: dict[str, int]) -> None:
        """Record what this generated layer was built from, for drift detection."""
        lines = [
            "/**",
            " * Provenance of the generated protocol layer.",
            " *",
            " * Regenerate with `python3 scripts/generate_protocol.py`, and check for drift",
            " * against an installed binary with `--check` (see the README upgrade runbook).",
            " */",
            "public object ProtocolInfo {",
            "    /** Version of the `codex` binary whose schema produced these types. */",
            f'    public const val CODEX_VERSION: String = "{self.codex_version}"',
            "",
            "    /** Hash of the schema universe; changes whenever upstream changes shape. */",
            f'    public const val SCHEMA_FINGERPRINT: String = "{self.fingerprint}"',
            "",
            "    /** Total schema definitions covered. */",
            f"    public const val DEFINITION_COUNT: Int = {len(self.defs)}",
            "",
            "    /** Server notifications this layer can route. */",
            f"    public const val NOTIFICATION_COUNT: Int = {len(self.notifications)}",
            "",
            "    /** Client request methods this layer can call. */",
            f"    public const val CLIENT_REQUEST_COUNT: Int = {len(self.client_requests)}",
            "",
            "    /** Requests the server may send to the client. */",
            f"    public const val SERVER_REQUEST_COUNT: Int = {len(self.server_requests)}",
            "}",
            "",
        ]
        write_kotlin(self.out_dir / "GeneratedProtocolInfo.kt", lines, "Generated-layer provenance.")

    def write_methods(self) -> None:
        lines: list[str] = []

        def emit_group(title: str, const_name: str, rows: list[dict], with_result: bool = False) -> None:
            lines.append("/**")
            lines.append(f" * {title}")
            lines.append(" */")
            lines.append(f"public object {const_name} {{")
            for row in rows:
                method = row["method"]
                ident = enum_entry_name(method.replace("/", "_"))
                params = row["params"] or "—"
                result = row.get("result") or "—"
                doc = f"params: {params}" + (f", result: {result}" if with_result else "")
                lines.append(f"    /** `{method}` ({doc}) */")
                lines.append(f'    public const val {ident}: String = "{method}"')
            lines.append("}")
            lines.append("")

        emit_group(
            "Every request method the client may send to the app-server.",
            "ClientRequests",
            self.client_requests,
        )
        emit_group(
            "Every request the app-server may send to the client. Each one MUST be answered "
            "or the turn stalls.",
            "ServerRequests",
            self.server_requests,
            with_result=True,
        )
        emit_group(
            "Every notification the client may send to the app-server.",
            "ClientNotifications",
            self.client_notifications,
        )

        lines.append("/** Params type name for each client request, keyed by wire method. */")
        lines.append("public val CLIENT_REQUEST_PARAMS: Map<String, String?> = mapOf(")
        for row in self.client_requests:
            params = f'"{row["params"]}"' if row["params"] else "null"
            lines.append(f'    "{row["method"]}" to {params},')
        lines.append(")")
        lines.append("")

        write_kotlin(self.out_dir / "GeneratedMethods.kt", lines, "Protocol method inventory.")


def write_kotlin(
    path: Path,
    body: list[str],
    summary: str,
    extra_imports: list[str] | None = None,
) -> None:
    imports = [
        "kotlinx.serialization.EncodeDefault",
        "kotlinx.serialization.SerialName",
        "kotlinx.serialization.Serializable",
        "kotlinx.serialization.json.JsonElement",
        "kotlinx.serialization.json.JsonObject",
        "kotlinx.serialization.json.JsonPrimitive",
        "kotlinx.serialization.json.buildJsonObject",
        "kotlinx.serialization.json.put",
    ] + (extra_imports or [])

    out = [
        "// GENERATED FILE — DO NOT EDIT.",
        "// Produced by scripts/generate_protocol.py from the app-server JSON Schema",
        "// (`codex app-server generate-json-schema`). Regenerate instead of editing.",
        f"// {summary}",
        "",
        f"@file:Suppress(\"unused\", \"RedundantVisibilityModifier\", \"LongParameterList\", \"MaxLineLength\")",
        "@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)",
        "",
        f"package {PACKAGE}",
        "",
    ]
    out.extend(f"import {i}" for i in sorted(set(imports)))
    out.append("")
    out.extend(body)
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(out).rstrip() + "\n")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--schema-dir", type=Path, default=None)
    parser.add_argument("--codex-bin", default=os.environ.get("CODEX_BIN", "codex"))
    parser.add_argument(
        "--check",
        action="store_true",
        help="compare against the committed output and exit 1 on drift; writes nothing",
    )
    args = parser.parse_args()

    tmp: tempfile.TemporaryDirectory | None = None
    if args.schema_dir:
        schema_dir = args.schema_dir
        version = codex_version(args.codex_bin)
    else:
        tmp = tempfile.TemporaryDirectory()
        schema_dir = generate_schema(Path(tmp.name) / "schema", args.codex_bin)
        version = codex_version(args.codex_bin)

    check_dir: tempfile.TemporaryDirectory | None = None
    try:
        defs = load_definitions(schema_dir)
        if args.check:
            check_dir = tempfile.TemporaryDirectory()
            out_dir = Path(check_dir.name)
        else:
            out_dir = OUT_DIR
        gen = Generator(defs, schema_dir, out_dir=out_dir, codex_version=version)
        counts = gen.run()

        if args.check:
            return report_drift(out_dir, version, gen)
    finally:
        if tmp:
            tmp.cleanup()
        if check_dir:
            check_dir.cleanup()

    print(f"codex {version}")
    print(f"fingerprint {gen.fingerprint}")
    print(f"definitions: {len(defs)}")
    for k, v in counts.items():
        print(f"  {k}: {v}")
    print(f"notifications: {len(gen.notifications)}")
    print(f"client requests: {len(gen.client_requests)}")
    print(f"server requests: {len(gen.server_requests)}")
    print(f"wrote Kotlin into {OUT_DIR}")
    return 0


def report_drift(fresh_dir: Path, version: str, gen: "Generator") -> int:
    """Compare freshly generated output against what is committed."""
    fresh = {p.name: p.read_text() for p in sorted(fresh_dir.glob("Generated*.kt"))}
    committed = {p.name: p.read_text() for p in sorted(OUT_DIR.glob("Generated*.kt"))}

    added = sorted(set(fresh) - set(committed))
    removed = sorted(set(committed) - set(fresh))
    changed = sorted(n for n in set(fresh) & set(committed) if fresh[n] != committed[n])

    print(f"installed codex: {version}")
    print(f"schema fingerprint: {gen.fingerprint}")
    print(f"notifications: {len(gen.notifications)}  client requests: {len(gen.client_requests)}")

    if not (added or removed or changed):
        print("\nIN SYNC — committed protocol layer matches the installed binary.")
        return 0

    print("\nDRIFT DETECTED — regenerate with: python3 scripts/generate_protocol.py")
    for name in added:
        print(f"  + {name} (new generated file)")
    for name in removed:
        print(f"  - {name} (no longer generated)")
    for name in changed:
        old_lines = committed[name].splitlines()
        new_lines = fresh[name].splitlines()
        print(f"  ~ {name} ({len(old_lines)} -> {len(new_lines)} lines)")

    # Name what actually appeared or vanished, which is what a reviewer needs to see.
    def declarations(text: str) -> set[str]:
        return set(re.findall(r"^public (?:data class|class|enum class|sealed interface|object|typealias) (\w+)", text, re.M))

    old_decls = set().union(*(declarations(t) for t in committed.values())) if committed else set()
    new_decls = set().union(*(declarations(t) for t in fresh.values())) if fresh else set()
    gained, lost = sorted(new_decls - old_decls), sorted(old_decls - new_decls)
    if gained:
        print(f"\n  new types ({len(gained)}): {', '.join(gained[:15])}{' …' if len(gained) > 15 else ''}")
    if lost:
        print(f"  removed types ({len(lost)}): {', '.join(lost[:15])}{' …' if len(lost) > 15 else ''}")
        print("  NOTE: removed types are a breaking upstream change; expect hand-written code to need edits.")
    return 1


if __name__ == "__main__":
    sys.exit(main())
