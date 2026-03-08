package moe.ouom.wekit.loader.startup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import dev.ujhhgtg.nameof.nameof
import moe.ouom.wekit.BuildConfig
import moe.ouom.wekit.loader.hookapi.IHookBridge
import moe.ouom.wekit.loader.hookapi.ILoaderService
import moe.ouom.wekit.utils.log.WeLogger
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.io.File
import java.lang.reflect.Field

object StartupAgent {

    private val TAG = nameof(StartupAgent)
    private var sInitialized = false

    fun startup(
        modulePath: String,
        loaderService: ILoaderService,
        hostClassLoader: ClassLoader,
        hookBridge: IHookBridge?
    ) {
        if (sInitialized) {
            WeLogger.w(TAG, "already initialized")
            return
        }
        sInitialized = true

        if (System.getProperty(StartupAgent::class.java.name) == "true") {
            WeLogger.e(TAG, "WeKit reloaded??")
            return
        }

        System.setProperty(StartupAgent::class.java.name, "true")
        StartupInfo.setModulePath(modulePath)
        StartupInfo.setLoaderService(loaderService)
        StartupInfo.setHookBridge(hookBridge)
        StartupInfo.setInHostProcess(true)

        ensureHiddenApiAccess()
        checkWriteXorExecuteForModulePath(modulePath)
        val ctx = getBaseApplication(hostClassLoader)

        StartupHook.initializeAfterAppCreate(ctx)
    }

    private fun checkWriteXorExecuteForModulePath(modulePath: String) {
        val moduleFile = File(modulePath)
        if (moduleFile.canWrite()) {
            Log.w(BuildConfig.TAG, "Module path is writable: $modulePath")
            Log.w(BuildConfig.TAG, "This may cause issues on Android 15+, please check your Xposed framework")
        }
    }

    fun getBaseApplication(classLoader: ClassLoader): Context {
        try {
            val tinkerAppClz = classLoader.loadClass("com.tencent.tinker.loader.app.TinkerApplication")
            val getInstanceMethod = tinkerAppClz.getMethod("getInstance")
            val app = getInstanceMethod.invoke(null) as? Context
            if (app != null) return app
        } catch (e: Throwable) {
            Log.w(BuildConfig.TAG, "Failed to call TinkerApplication.getInstance()", e)
        }

        try {
            @SuppressLint("PrivateApi")
            val activityThreadClz = classLoader.loadClass("android.app.ActivityThread")
            @SuppressLint("DiscouragedPrivateApi")
            val currentAppMethod = activityThreadClz.getDeclaredMethod("currentApplication")
            currentAppMethod.isAccessible = true
            val app = currentAppMethod.invoke(null) as? Context
            if (app != null) return app
        } catch (e: Exception) {
            WeLogger.e("getBaseApplication: ActivityThread fallback failed", e)
        }

        throw UnsupportedOperationException("Failed to retrieve Application instance.")
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun ensureHiddenApiAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !isHiddenApiAccessible()) {
            Log.w(BuildConfig.TAG, "Hidden API access not accessible, SDK_INT is ${Build.VERSION.SDK_INT}")
            HiddenApiBypass.setHiddenApiExemptions("L")
        }
    }

    @SuppressLint("BlockedPrivateApi", "PrivateApi")
    fun isHiddenApiAccessible(): Boolean {
        val kContextImpl = try {
            Class.forName("android.app.ContextImpl")
        } catch (_: ClassNotFoundException) {
            return false
        }

        var mActivityToken: Field? = null
        var mToken: Field? = null

        try { mActivityToken = kContextImpl.getDeclaredField("mActivityToken") } catch (_: NoSuchFieldException) {}
        try { mToken = kContextImpl.getDeclaredField("mToken") } catch (_: NoSuchFieldException) {}

        return mActivityToken != null || mToken != null
    }
}
