package moe.ouom.wekit.loader.entry.xp51;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.robv.android.xposed.XposedBridge;
import moe.ouom.wekit.loader.entry.common.ModuleLoader;

public class Xp51ExtCmd {

    private Xp51ExtCmd() {
    }

    public static Object handleQueryExtension(@NonNull String cmd, @Nullable Object[] arg) {
        Objects.requireNonNull(cmd, "cmd");
        switch (cmd) {
            case "GetXposedBridgeClass":
                return XposedBridge.class;
            case "GetLoadPackageParam":
                return Xp51HookEntry.getLoadPackageParam();
            case "GetInitZygoteStartupParam":
                return Xp51HookEntry.getInitZygoteStartupParam();
            case "GetInitErrors":
                return ModuleLoader.getInitErrors();
            default:
                return null;
        }
    }

}
