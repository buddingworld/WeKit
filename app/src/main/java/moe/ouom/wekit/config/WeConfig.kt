package moe.ouom.wekit.config

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import moe.ouom.wekit.constants.Constants
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

abstract class WeConfig protected constructor() : SharedPreferences, SharedPreferences.Editor {

    fun getOrDefault(key: String, def: Any?): Any? {
        if (!containsKey(key)) {
            return def
        }
        return getObject(key)
    }

    fun getBooleanOrFalse(key: String): Boolean {
        return getBooleanOrDefault(key, false)
    }

    fun getBoolPref(key: String): Boolean {
        return getBooleanOrDefault(Constants.PREF_KEY_PREFIX + key, false)
    }

    @OptIn(ExperimentalContracts::class)
    fun getStringPref(key: String, def: String?): String? {
        contract {
            returnsNotNull() implies (def != null)
        }

        return getString(Constants.PREF_KEY_PREFIX + key, def)
    }

    fun getIntPrek(key: String, def: Int): Int {
        return getInt(Constants.PREF_KEY_PREFIX + key, def)
    }

    fun getLongPrek(key: String, def: Long): Long {
        return getLong(Constants.PREF_KEY_PREFIX + key, def)
    }

    fun getBooleanOrDefault(key: String, def: Boolean): Boolean {
        return getBoolean(key, def)
    }

    fun getIntOrDefault(key: String, def: Int): Int {
        return getInt(key, def)
    }


    abstract fun getString(key: String): String?

    fun getStringOrDefault(key: String, defVal: String): String {
        return getString(key, defVal)!!
    }

    fun getStringSetOrDefault(key: String, defVal: MutableSet<String?>): MutableSet<String?> {
        return getStringSet(key, defVal)!!
    }

    abstract fun getObject(key: String): Any?

    fun getBytes(key: String): ByteArray? {
        return getBytes(key, null)
    }

    abstract fun getBytes(key: String, defValue: ByteArray?): ByteArray?

    abstract fun getBytesOrDefault(key: String, defValue: ByteArray): ByteArray

    abstract fun putBytes(key: String, value: ByteArray)

    abstract fun save()

    fun getLongOrDefault(key: String?, i: Long): Long {
        return getLong(key, i)
    }

    abstract fun putObject(key: String, v: Any): WeConfig

    fun containsKey(k: String): Boolean {
        return contains(k)
    }

    override fun edit(): SharedPreferences.Editor {
        return this
    }

    override fun registerOnSharedPreferenceChangeListener(
        listener: OnSharedPreferenceChangeListener
    ): Unit = TODO()

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: OnSharedPreferenceChangeListener
    ): Unit = TODO()

    abstract val isReadOnly: Boolean

    abstract val isPersistent: Boolean

    companion object {
        const val PREFS_NAME = "wekit_prefs"
        const val CACHE_PREFS_NAME = "wekit_cache"

        private var sDefConfig: WeConfig? = null

        @JvmStatic
        @get:Synchronized
        val defaultConfig: WeConfig
            get() {
                if (sDefConfig == null) {
                    sDefConfig = MmkvConfigManagerImpl(PREFS_NAME)
                }
                return sDefConfig!!
            }

        fun dPutBoolean(key: String, b: Boolean) {
            defaultConfig.edit().putBoolean(key, b).apply()
        }

        fun dPutString(key: String, s: String?) {
            defaultConfig.edit().putString(key, s).apply()
        }

        fun dPutInt(key: String, i: Int) {
            defaultConfig.edit().putInt(key, i).apply()
        }

        fun dGetBoolean(key: String): Boolean {
            return defaultConfig.getBooleanOrFalse(key)
        }

        fun dGetBooleanDefTrue(key: String): Boolean {
            return defaultConfig.getBooleanOrDefault(key, true)
        }

        fun dGetString(key: String, d: String): String {
            return defaultConfig.getStringOrDefault(key, d)
        }

        fun dGetInt(key: String, d: Int): Int {
            return defaultConfig.getIntOrDefault(key, d)
        }
    }
}
