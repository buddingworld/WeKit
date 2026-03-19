package moe.ouom.wekit.hooks.utils

import android.content.pm.ApplicationInfo
import dev.ujhhgtg.nameof.nameof
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import moe.ouom.wekit.constants.PreferenceKeys.NO_DEX_RESOLVE
import moe.ouom.wekit.core.model.ApiHookItem
import moe.ouom.wekit.core.model.BaseHookItem
import moe.ouom.wekit.core.model.ClickableHookItem
import moe.ouom.wekit.core.model.SwitchHookItem
import moe.ouom.wekit.dexkit.abc.IResolvesDex
import moe.ouom.wekit.dexkit.cache.DexCacheManager
import moe.ouom.wekit.preferences.WePrefs
import moe.ouom.wekit.ui.content.DexResolverDialogContent
import moe.ouom.wekit.ui.utils.showComposeDialog
import moe.ouom.wekit.utils.HostInfo
import moe.ouom.wekit.utils.RuntimeConfig
import moe.ouom.wekit.utils.TargetProcessUtils
import moe.ouom.wekit.utils.logging.WeLogger
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

object HookItemsLoader {

    private val TAG = nameof(HookItemsLoader)

    fun loadHookItems(process: Int) {
        val appInfo = HostInfo.appInfo
        loadHookItems(process, appInfo)
    }

    fun loadHookItems(
        process: Int,
        appInfo: ApplicationInfo
    ) {
        val allHookItems = HookItemsFactory.getItems()
        val allDexResolvingItems = allHookItems.filterIsInstance<IResolvesDex>()
        val outdatedItems = DexCacheManager.getOutdatedItems(allDexResolvingItems)
        val validItems = allDexResolvingItems.filterNot { outdatedItems.contains(it) }
        WeLogger.i(
            TAG,
            "found ${validItems.size} valid items, ${outdatedItems.size} outdated items"
        )
        val corruptedItems = loadDescriptorsFromCache(validItems)
        val allBrokenItems = (outdatedItems + corruptedItems).distinct()

        if (allBrokenItems.isNotEmpty()) {
            handleBrokenItems(process, appInfo, allBrokenItems)
        }

        val elapsed = measureTime {
            allHookItems.forEach { hookItem ->
                if (hookItem is IResolvesDex && allBrokenItems.contains(hookItem)) {
                    WeLogger.w(
                        TAG,
                        "skipping ${hookItem.path} due to missing or invalid cache"
                    )
                    return@forEach
                }

                when (hookItem) {
                    is ClickableHookItem -> {
                        hookItem.setEnabledSilently(WePrefs.getBoolOrFalse(hookItem.path))
                        if (hookItem.isEnabled || hookItem.alwaysRun) hookItem.enable(process)
                    }

                    is SwitchHookItem -> {
                        hookItem.setEnabledSilently(WePrefs.getBoolOrFalse(hookItem.path))
                        if (hookItem.isEnabled) hookItem.enable(process)
                    }

                    is ApiHookItem -> {
                        hookItem.enable(process)
                    }
                }
            }
        }
        WeLogger.i(TAG, "enabling all hook items took $elapsed")
    }

    private fun handleBrokenItems(
        process: Int,
        appInfo: ApplicationInfo,
        brokenItems: List<IResolvesDex>
    ) {
        if (WePrefs.getBoolOrFalse(NO_DEX_RESOLVE)) return
        if (process != TargetProcessUtils.PROC_MAIN) return

        WeLogger.i(TAG, "launching background coroutine to repair ${brokenItems.size} items")

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            val activity = withTimeoutOrNull(90_000L.milliseconds) {
                while (true) {
                    delay(200.milliseconds)
                    RuntimeConfig.getLauncherUiActivity()?.let { return@withTimeoutOrNull it }
                }
                @Suppress("UNREACHABLE_CODE")
                null
            }

            if (activity == null) {
                WeLogger.e(TAG, "wait for main activity timed out, dex resolution dialog skipped")
                return@launch
            }

            delay(1_500) // allow Activity to finish initializing

            withContext(Dispatchers.Main) {
                showComposeDialog(activity) {
                    DexResolverDialogContent(
                        activity,
                        brokenItems,
                        appInfo,
                        CoroutineScope(Dispatchers.Main + SupervisorJob()),
                        onDismiss = dismiss
                    )
                }
            }
        }
    }

    private fun loadDescriptorsFromCache(items: List<IResolvesDex>): List<IResolvesDex> {
        val failedItems = mutableListOf<IResolvesDex>()

        items.forEach { item ->
            try {
                val cache = DexCacheManager.loadItemCache(item)
                if (cache != null) {
                    item.loadFromCache(cache)
                } else {
                    WeLogger.w(
                        TAG,
                        "cache is null for ${(item as? BaseHookItem)?.path}"
                    )
                    failedItems.add(item)
                }
            } catch (e: Exception) {
                val path = (item as? BaseHookItem)?.path ?: "unknown"
                WeLogger.e(TAG, "cache load failed for $path", e)

                runCatching {
                    DexCacheManager.deleteCache(path)
                }
                failedItems.add(item)
            }
        }

        return failedItems
    }
}