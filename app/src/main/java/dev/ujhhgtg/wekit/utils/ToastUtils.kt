package dev.ujhhgtg.wekit.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("NOTHING_TO_INLINE")
inline fun showToast(ctx: Context, content: String) = Toast.makeText(ctx, content, Toast.LENGTH_SHORT).show()

@Suppress("NOTHING_TO_INLINE")
inline fun showToast(content: String) = showToast(HostInfo.application, content)

suspend fun showToastSuspend(msg: String) = withContext(Dispatchers.Main) {
    showToast(msg)
}
