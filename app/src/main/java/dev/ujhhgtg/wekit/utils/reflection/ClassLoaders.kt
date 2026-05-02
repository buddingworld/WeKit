package dev.ujhhgtg.wekit.utils.reflection

import android.content.Context
import com.highcapable.kavaref.extension.ClassLoaderProvider
import dev.ujhhgtg.wekit.loader.utils.HybridClassLoader

object ClassLoaders {

    inline val HOST: ClassLoader get() = ClassLoaderProvider.classLoader!!

    inline val MODULE: ClassLoader get() = ClassLoaders.javaClass.classLoader!!

    inline val BOOT: ClassLoader get() = Context::class.java.classLoader!!

    inline val HYBRID: ClassLoader get() = HybridClassLoader

    val HYBRID_HOST_FIRST by lazy {
        object : ClassLoader(HOST) {
            override fun findClass(name: String): Class<*> = MODULE.loadClass(name)
        }
    }
}
