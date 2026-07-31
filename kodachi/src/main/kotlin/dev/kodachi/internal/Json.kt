package dev.kodachi.internal

import kotlinx.serialization.json.Json

/**
 * The app-server adds wire fields between releases, so unknown keys must never be fatal.
 * Nulls and defaults are omitted on the way out: the server treats an explicit `null`
 * override differently from an absent one.
 */
internal val CodexJson: Json = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = false
    isLenient = true
}
