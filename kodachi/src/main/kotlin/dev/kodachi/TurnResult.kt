package dev.kodachi

import dev.kodachi.protocol.ThreadItem
import dev.kodachi.protocol.ThreadTokenUsage
import dev.kodachi.protocol.TurnError
import dev.kodachi.protocol.TurnStatus

/**
 * Everything a completed turn produced.
 *
 * @property status how the turn ended; [TurnStatus.COMPLETED] is the only clean outcome
 * @property finalResponse the last assistant message, or empty if the turn produced none
 * @property messages every assistant message in order, for turns that emit several
 * @property items the full transcript the turn appended to the thread
 * @property usage token accounting as of the end of the turn
 * @property error set when the turn failed or was rejected
 * @property diff the turn's cumulative workspace diff, when it changed any file
 */
public data class TurnResult(
    val threadId: String,
    val turnId: String,
    val status: TurnStatus,
    val finalResponse: String,
    val messages: List<String>,
    val items: List<ThreadItem>,
    val usage: ThreadTokenUsage?,
    val error: TurnError?,
    val diff: String? = null,
) {
    /** True when the turn ran to completion without an error. */
    public val isSuccess: Boolean get() = status == TurnStatus.COMPLETED && error == null
}
