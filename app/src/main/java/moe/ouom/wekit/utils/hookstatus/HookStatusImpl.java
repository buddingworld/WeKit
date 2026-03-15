package moe.ouom.wekit.utils.hookstatus;

/**
 * Hook status detection, NO KOTLIN, NO ANDROIDX!
 */
public class HookStatusImpl {

    static volatile boolean sZygoteHookMode = false;
    static volatile String sZygoteHookProvider = null;
    static volatile boolean sIsLsposedDexObfsEnabled = false;

    private HookStatusImpl() {
    }
}
