package dev.ujhhgtg.wekit.ui.utils

import android.content.Context
import android.content.ContextWrapper

class CommonContextWrapper private constructor(base: Context?) : ContextWrapper(base) {

    override fun getClassLoader(): ClassLoader {
        return javaClass.classLoader!!
    }

    companion object {
        fun create(base: Context): Context {
            return CommonContextWrapper(base)
        }
    }
}
