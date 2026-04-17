package dev.ujhhgtg.wekit.utils.paths

import java.nio.file.Path
import kotlin.io.path.createDirectories

@Suppress("NOTHING_TO_INLINE")
inline fun Path.createDirectoriesNoThrow(): Path {
    runCatching { this.createDirectories() }
    return this
}
