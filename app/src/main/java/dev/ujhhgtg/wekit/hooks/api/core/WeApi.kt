package dev.ujhhgtg.wekit.hooks.api.core

import dev.ujhhgtg.wekit.utils.RuntimeConfig

object WeApi {

    private var _selfWxId: String = ""
    val selfWxId: String
        get() {
            if (_selfWxId.isEmpty()) {
                val result = RuntimeConfig.loggedInWxId
                if (result.isNotEmpty()) _selfWxId = result
                return result
            }
            return _selfWxId
        }

    private var _selfCustomWxId: String = ""
    val selfCustomWxId: String
        get() {
            if (_selfCustomWxId.isEmpty()) {
                val result = WeMessageApi.selfCustomWxId
                if (result.isNotEmpty()) _selfCustomWxId = result
                return result
            }
            return _selfCustomWxId
        }
}
