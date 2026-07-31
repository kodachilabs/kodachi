package dev.kodachi

import dev.kodachi.protocol.ImageUserInput
import dev.kodachi.protocol.LocalImageUserInput
import dev.kodachi.protocol.MentionUserInput
import dev.kodachi.protocol.SkillUserInput
import dev.kodachi.protocol.TextUserInput
import dev.kodachi.protocol.UserInput

/*
 * Turn input is a list of [UserInput], the generated protocol union. The functions below
 * are shorthands for its variants; construct the generated types directly when you need a
 * field these do not expose (image detail, text elements).
 */

/** Plain text from the user. */
public fun textInput(text: String): UserInput = TextUserInput(text = text)

/** An image supplied as a data or remote URL. */
public fun imageInput(url: String): UserInput = ImageUserInput(url = url)

/** An image supplied as a path on the machine running Codex. */
public fun localImageInput(path: String): UserInput = LocalImageUserInput(path = path)

/** An explicit skill reference. */
public fun skillInput(name: String, path: String): UserInput =
    SkillUserInput(name = name, path = path)

/** An `@`-style mention of a file or resource. */
public fun mentionInput(name: String, path: String): UserInput =
    MentionUserInput(name = name, path = path)

/** Wrap a prompt as single-item turn input. */
public fun promptInput(prompt: String): List<UserInput> = listOf(textInput(prompt))
