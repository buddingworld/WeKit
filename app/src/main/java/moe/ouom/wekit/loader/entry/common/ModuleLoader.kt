package moe.ouom.wekit.loader.entry.common

import android.util.Log
import dev.ujhhgtg.nameof.nameof
import moe.ouom.wekit.BuildConfig
import moe.ouom.wekit.loader.abc.IHookBridge
import moe.ouom.wekit.loader.abc.ILoaderService
import moe.ouom.wekit.loader.startup.UnifiedEntryPoint

object ModuleLoader {

    private val TAG = nameof(ModuleLoader)
    private var isInitialized = false

    @JvmStatic
    fun init(
        hostDataDir: String,
        hostClassLoader: ClassLoader,
        loaderService: ILoaderService,
        hookBridge: IHookBridge?,
        modulePath: String,
        allowDynamicLoad: Boolean
    ) {
        if (isInitialized) return
        isInitialized = true

        Log.i(BuildConfig.TAG, "$TAG: initializing from entry point ${loaderService.entryPointName}")
        UnifiedEntryPoint.entry(loaderService, hostClassLoader, modulePath)
    }
}
