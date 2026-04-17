package dev.ujhhgtg.wekit.hooks.api.net.models

import org.json.JSONObject

data class SignResult(
    val json: JSONObject,
    val nativeNetScene: Any? = null,
    val onSendSuccess: (() -> Unit)? = null
)
