package moe.ouom.wekit.utils

import android.os.Environment
import moe.ouom.wekit.BuildConfig
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.div

object ModulePaths {

    val internalStorage: Path by lazy {
        Path(Environment.getExternalStorageDirectory().absolutePath)
    }

    val data: Path by lazy {
        (internalStorage / "Android" / "data" / HostInfo.packageName / "files" / BuildConfig.TAG)
            .createDirectoriesNoThrow()
    }

    val cache: Path by lazy {
        (internalStorage / "Android" / "data" / HostInfo.packageName / "cache" / BuildConfig.TAG)
            .createDirectoriesNoThrow()
    }
}
