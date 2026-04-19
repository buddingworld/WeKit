package dev.ujhhgtg.wekit.dexkit

import com.highcapable.kavaref.extension.ClassLoaderProvider
import org.luckypray.dexkit.result.ClassData

@Suppress("NOTHING_TO_INLINE")
inline fun ClassData.toClass(): Class<*> = getInstance(ClassLoaderProvider.classLoader!!)
