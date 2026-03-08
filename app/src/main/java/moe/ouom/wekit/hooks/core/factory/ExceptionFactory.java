package moe.ouom.wekit.hooks.core.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.robv.android.xposed.XposedBridge;
import moe.ouom.wekit.core.model.BaseHookItem;
import moe.ouom.wekit.utils.log.LogUtils;


/**
 * 异常处理工厂
 */
public class ExceptionFactory {
    private final static Map<BaseHookItem, List<Throwable>> exceptionMap = new HashMap<>();

    /**
     * 检查是否超过3个或重复
     *
     * @return 为true则已经超过三个或者添加过
     */
    private static boolean check(BaseHookItem item, Throwable throwable) {
        // 每个 item 最多只保存 3 个 Throwable, 不然添加太多会占用太多不必要的内存
        var exceptionsList = exceptionMap.get(item);
        if (exceptionsList == null || exceptionsList.size() < 3) {
            return false;
        }
        // 判断是否已经添加过了 添加过则不再重复添加
        for (var ex : exceptionsList) {
            if (Objects.equals(ex.getMessage(), throwable.getMessage())) {
                return true;
            }
        }
        return false;
    }

    public static void add(BaseHookItem item, Throwable throwable) {
        if (check(item, throwable)) {
            return;
        }
        var exceptionsList = exceptionMap.get(item);
        if (exceptionsList == null) {
            exceptionsList = new ArrayList<>();
        }
        exceptionsList.add(0, throwable);
        exceptionMap.put(item, exceptionsList);
        XposedBridge.log(throwable);
        try {
            LogUtils.addError("item_" + item.getItemName(), throwable);
        } catch (NoClassDefFoundError ignored) {
        }
    }
}
