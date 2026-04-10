package dev.ujhhgtg.wekit.utils

import android.content.SharedPreferences

object RuntimeConfig {

    lateinit var mmPrefs: SharedPreferences

    val loggedInWxId: String
        get() = mmPrefs.getString("login_weixin_username", "") ?: ""
}
