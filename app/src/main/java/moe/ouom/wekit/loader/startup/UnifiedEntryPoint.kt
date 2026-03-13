package moe.ouom.wekit.loader.startup

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.highcapable.kavaref.extension.ClassLoaderProvider
import com.highcapable.kavaref.extension.toClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import dev.ujhhgtg.nameof.nameof
import moe.ouom.wekit.loader.hookapi.IHookBridge
import moe.ouom.wekit.loader.hookapi.ILoaderService
import moe.ouom.wekit.utils.log.WeLogger

object UnifiedEntryPoint {

    private val TAG = nameof(UnifiedEntryPoint)

    private var initialized = false

    fun entry(
        modulePath: String,
        loaderService: ILoaderService,
        hostClassLoader: ClassLoader,
        hookBridge: IHookBridge?
    ) {
        check(!initialized) { "UnifiedEntryPoint already initialized" }
        initialized = true
        // fix up the class loader
        val loader = HybridClassLoader.INSTANCE
        val self = checkNotNull(UnifiedEntryPoint::class.java.classLoader)
        val parent = self.parent
        HybridClassLoader.setLoaderParentClassLoader(parent)
        injectClassLoader(self, loader)

        try {
            XposedHelpers.findAndHookMethod(
                "com.tencent.mm.app.Application",
                hostClassLoader,
                "attachBaseContext",
                Context::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val context = param.thisObject as Context
                        val currentClassLoader = context.classLoader

                        // Hook Instrumentation.callApplicationOnCreate 以处理 Tinker 热更新场景
                        try {
                            hookInstrumentationForTinker(
                                currentClassLoader,
                                modulePath,
                                loaderService,
                                hookBridge
                            )
                        } catch (t: Throwable) {
                            WeLogger.e(
                                "Failed to hook Instrumentation.callApplicationOnCreate",
                                t
                            )
                        }
                    }
                }
            )
            WeLogger.i("Hook applied: waiting for Application.attachBaseContext")
        } catch (t: Throwable) {
            WeLogger.e("Failed to hook Shell Application", t)
        }
    }

    /**
     * Hook Instrumentation.callApplicationOnCreate 以确保在 Tinker 热更新完成后再进行延迟初始化
     * 这可以解决某些模块在热更新环境下找不到入口的问题
     */
    private fun hookInstrumentationForTinker(
        hostClassLoader: ClassLoader,
        modulePath: String,
        loaderService: ILoaderService,
        hookBridge: IHookBridge?
    ) {
        try {
            val instrumentationClass = "android.app.Instrumentation".toClass(hostClassLoader)
            XposedHelpers.findAndHookMethod(
                instrumentationClass,
                "callApplicationOnCreate",
                Application::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val application = param.args[0] as Application
                        val hostApp = param.args[0] as Application?
                        StartupInfo.setHostApp(hostApp)

                        val hostClassLoader = application.baseContext.classLoader
                        ClassLoaderProvider.classLoader = hostClassLoader

                        try {
                            StartupAgent.startup(
                                modulePath,
                                loaderService,
                                hookBridge
                            )
                        } catch (e: Throwable) {
                            WeLogger.e(TAG, "StartupAgent failed", e)
                        }
                    }
                }
            )
        } catch (e: Throwable) {
            WeLogger.e(TAG, "Failed to hook Instrumentation.callApplicationOnCreate", e)
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun injectClassLoader(self: ClassLoader, newParent: ClassLoader?) {
        try {
            val fParent = ClassLoader::class.java.getDeclaredField("parent")
            fParent.isAccessible = true
            fParent.set(self, newParent)
        } catch (e: Exception) {
            WeLogger.e("injectClassLoader failed", e)
        }
    }
}
