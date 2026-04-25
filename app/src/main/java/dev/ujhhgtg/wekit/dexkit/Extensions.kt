package dev.ujhhgtg.wekit.dexkit

import dev.ujhhgtg.wekit.utils.reflection.ClassLoaders
import org.luckypray.dexkit.result.ClassData

@Suppress("NOTHING_TO_INLINE")
inline fun ClassData.toClass(): Class<*> = getInstance(ClassLoaders.HOST)
