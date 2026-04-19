package dev.ujhhgtg.wekit.utils

import android.content.Context
import android.content.res.Configuration
import android.os.UserManager

inline val Context.isDarkMode
    get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

inline val Context.androidUserId: Long
    get() {
        val userManager =
            this.getSystemService(Context.USER_SERVICE) as UserManager
        val userHandle = android.os.Process.myUserHandle()
        return userManager.getSerialNumberForUser(userHandle)
    }
