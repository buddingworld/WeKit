package dev.ujhhgtg.wekit.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("NOTHING_TO_INLINE")
inline fun showToast(context: Context, content: String) = Toast.makeText(context, content, Toast.LENGTH_SHORT).show()

@Suppress("NOTHING_TO_INLINE")
inline fun showToast(content: String) = showToast(HostInfo.application, content)

suspend inline fun showToastSuspend(context: Context, content: String) = withContext(Dispatchers.Main) {
    showToast(context, content)
}

suspend inline fun showToastSuspend(content: String) = withContext(Dispatchers.Main) {
    showToast(content)
}
