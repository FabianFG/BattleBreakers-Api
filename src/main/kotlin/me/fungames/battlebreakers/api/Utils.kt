package me.fungames.battlebreakers.api

import java.io.File
import com.google.gson.Gson


object Utils {
    val DEFAULT_GSON = Gson()

    val CLIENT_LAUNCHER_TOKEN = "M2NmNzhjZDNiMDBiNDM5YTg3NTVhODc4YjE2MGM3YWQ6YjM4M2UwZjQtZjBjYy00ZDE0LTk5ZTMtODEzYzMzZmMxZTlk"

    val cacheDir: String
        get() = cacheDirFile.absolutePath

    val cacheDirFile: File
        get() {
            val tempDir = File(System.getProperty("java.io.tmpdir") + "/BattleBreakers-Api/Http/Cache")
            tempDir.mkdirs()
            return tempDir
        }
}