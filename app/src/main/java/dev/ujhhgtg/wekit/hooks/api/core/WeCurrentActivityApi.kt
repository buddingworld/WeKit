package dev.ujhhgtg.wekit.hooks.api.core

import android.app.Activity
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import dev.ujhhgtg.wekit.hooks.core.ApiHookItem
import dev.ujhhgtg.wekit.hooks.core.HookItem
import java.lang.ref.WeakReference

@HookItem(path = "API/当前活动跟踪服务", description = "跟踪当前处于屏幕上的活动")
object WeCurrentActivityApi : ApiHookItem() {

    val activity get() = _activity.get()
    @Volatile
    private lateinit var _activity: WeakReference<Activity>

    override fun onEnable() {
        Activity::class.asResolver().apply {
            firstMethod { name = "onResume" }.hookBefore {
                _activity = WeakReference(thisObject as Activity)
            }
        }
    }
}
