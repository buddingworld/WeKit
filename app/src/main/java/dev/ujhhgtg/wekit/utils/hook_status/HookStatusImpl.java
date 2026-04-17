package dev.ujhhgtg.wekit.utils.hook_status;

/**
 * Hook status detection, NO KOTLIN, NO ANDROIDX!
 */
public class HookStatusImpl {

    static volatile boolean zygoteHookMode = false;
    static volatile String zygoteHookProvider = null;
    static volatile boolean isLsposedDexObfsEnabled = false;
}
