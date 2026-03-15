package moe.ouom.wekit.loader.entry.common

import dev.ujhhgtg.nameof.nameof
import moe.ouom.wekit.loader.abs.IHookBridge
import moe.ouom.wekit.loader.abs.ILoaderService
import moe.ouom.wekit.loader.startup.UnifiedEntryPoint
import moe.ouom.wekit.utils.logging.WeLogger

object ModuleLoader {

    private val TAG = nameof(ModuleLoader)

    @JvmStatic
    val initErrors = ArrayList<Throwable?>(1)
    private var isInitialized = false

    @JvmStatic
    fun initialize(
        hostDataDir: String,
        hostClassLoader: ClassLoader,
        loaderService: ILoaderService,
        hookBridge: IHookBridge?,
        modulePath: String,
        allowDynamicLoad: Boolean
    ) {
        if (isInitialized) return
        isInitialized = true

        WeLogger.i(TAG, "initializing from entry point ${loaderService.entryPointName}")
        UnifiedEntryPoint.entry(loaderService, hostClassLoader, modulePath)
    }
}
