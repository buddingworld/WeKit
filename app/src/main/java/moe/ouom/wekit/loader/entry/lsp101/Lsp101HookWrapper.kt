package moe.ouom.wekit.loader.entry.lsp101

import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.annotations.XposedApiMin
import moe.ouom.wekit.loader.abs.IHookBridge.IMemberHookCallback
import moe.ouom.wekit.loader.abs.IHookBridge.MemberUnhookHandle
import java.lang.reflect.Member

@XposedApiMin(101)
object Lsp101HookWrapper {

    var self: XposedModule? = null

    fun hookAndRegisterMethodCallback(
        method: Member,
        callback: IMemberHookCallback,
        priority: Int
    ): MemberUnhookHandle {
        throw UnsupportedOperationException("not implemented")
    }

    val hookCounter: Int
        get() {
            throw UnsupportedOperationException("not implemented")
        }

    val hookedMethodsRaw: Set<Member?>
        get() {
            throw UnsupportedOperationException("not implemented")
        }
}
