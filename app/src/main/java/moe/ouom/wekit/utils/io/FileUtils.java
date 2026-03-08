package moe.ouom.wekit.utils.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import de.robv.android.xposed.XposedBridge;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtils {
    /**
     * 文件写入文本
     *
     * @param path     路径
     * @param content  内容
     * @param isAppend 是否追写 不是的话会覆盖
     */
    public static void writeTextToFile(String path, String content, boolean isAppend) {
        try {
            var file = new File(path);
            try {
                // 先创建文件夹
                if (!Objects.requireNonNull(file.getParentFile()).exists())
                    file.getParentFile().mkdirs();
                // 再创建文件 FileOutputStream 会自动创建文件但是不能创建多级目录
                if (!file.exists()) file.createNewFile();
            } catch (IOException ignored) {
            }
            try (var writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, isAppend), StandardCharsets.UTF_8))) {
                writer.write(content);
            } catch (IOException ignored) {
            }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}
