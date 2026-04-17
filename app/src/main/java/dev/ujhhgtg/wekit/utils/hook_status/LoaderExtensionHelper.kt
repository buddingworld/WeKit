package dev.ujhhgtg.wekit.utils.hook_status

import dev.ujhhgtg.wekit.loader.startup.StartupInfo

object LoaderExtensionHelper {

    const val CMD_GET_XPOSED_BRIDGE_CLASS: String = "GetXposedBridgeClass"

    fun getXposedBridgeClass(): Class<*>? {
        val loaderService = StartupInfo.loaderService
        return loaderService.queryExtension(CMD_GET_XPOSED_BRIDGE_CLASS) as Class<*>?
    }
}
