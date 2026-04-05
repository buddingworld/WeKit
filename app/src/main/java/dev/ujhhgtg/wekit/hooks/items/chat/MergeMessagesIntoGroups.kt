package dev.ujhhgtg.wekit.hooks.items.chat

import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import de.robv.android.xposed.XC_MethodHook
import dev.ujhhgtg.wekit.hooks.api.core.WeMessageApi
import dev.ujhhgtg.wekit.hooks.api.core.model.MessageInfo
import dev.ujhhgtg.wekit.hooks.api.core.model.MessageType
import dev.ujhhgtg.wekit.hooks.api.ui.WeChatMessageViewApi
import dev.ujhhgtg.wekit.hooks.core.HookItem
import dev.ujhhgtg.wekit.hooks.core.SwitchHookItem
import java.lang.reflect.Field

@HookItem(path = "聊天/合并消息显示", description = "将同来源的连续多条消息合并为一组消息显示")
object MergeMessagesIntoGroups : SwitchHookItem(), WeChatMessageViewApi.ICreateViewListener {

    override fun onEnable() {
        WeChatMessageViewApi.addListener(this)
    }

    override fun onDisable() {
        WeChatMessageViewApi.removeListener(this)
    }

    private var avatarField: Field? = null
    private var displayNameField: Field? = null

    private fun ensureFields(tag: Any) {
        if (avatarField == null) {
            avatarField = tag.asResolver()
                .firstField {
                    name = "avatarIV"
                    superclass()
                }.self
        }
        if (displayNameField == null) {
            displayNameField = tag.asResolver()
                .firstField {
                    name = "userTV"
                    superclass()
                }.self
        }
    }

    override fun onCreateView(param: XC_MethodHook.MethodHookParam, view: View) {
        val tag = view.tag

        val msgInfo = WeChatMessageViewApi.getMsgInfoFromParam(param)
        if (msgInfo.isSend != 0) return

        // Only meaningful in group chats where multiple senders exist.
        if (!msgInfo.isInGroupChat) return

        // System / PAT messages have no meaningful "sender"; leave them as-is.
        if (msgInfo.isType(MessageType.SYSTEM) || msgInfo.isType(MessageType.PAT)) return

        val currentSender = msgInfo.sender

        val position = param.args[2] as Int

        val adapter = param.thisObject.asResolver()
            .firstField { type = WeMessageApi.classChattingDataAdapter.clazz }
            .get() ?: return

        val prevSender = senderAt(adapter, position - 1)
        val nextSender = senderAt(adapter, position + 1)

        val isFirstInGroup = prevSender != currentSender
        val isLastInGroup  = nextSender != currentSender

        ensureFields(tag)

        // Avatar: keep layout space (INVISIBLE) when not the bottom of the group
        // so the message text column stays aligned across the whole conversation.
        (avatarField?.get(tag) as? View)?.let { avatar ->
            // For sent messages WeChat sometimes wraps the avatar in an extra
            // container that carries the right-margin; walk up one level so the
            // gap also collapses.
            val avatarContainer = avatar.parent as? View ?: avatar
            avatarContainer.visibility =
                if (isLastInGroup) View.VISIBLE else View.INVISIBLE
        }

        // Display Name: collapse entirely (GONE) for all but the first bubble so
        // vertical space between consecutive bubbles is tight.
        (displayNameField?.get(tag) as? View)?.visibility =
            if (isFirstInGroup) View.VISIBLE else View.GONE
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /**
     * Returns the sender wxid of the message at [position] in [adapter],
     * or `null` if the position is out of bounds or the message has no sender
     * (e.g. a system notification).
     */
    private fun senderAt(adapter: Any, position: Int): String? = runCatching {
        val raw = adapter.asResolver()
            .firstMethod { name = "getItem" }
            .invoke(position) ?: return null
        MessageInfo(raw).sender
    }.getOrNull()
}
