package me.oska.gw

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class GoogleWhitelist: JavaPlugin() {

    companion object {
        lateinit var instance: GoogleWhitelist
    }

    lateinit var noWhitelistMessage: String
    private lateinit var seperator: String
    private lateinit var url: String
    private lateinit var local: String
    private var column: Int = 0
    private val whitelistedPlayers = hashSetOf<String>()

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        column = config.getInt("config.column")
        noWhitelistMessage = config.getString("message.no_whitelist")!!
        url = config.getString("config.url")!!
        local = config.getString("config.file_locate")!!
        seperator = config.getString("config.seperator")!!

        val hasAutoUpdate = config.getBoolean("config.auto_update")
        if (hasAutoUpdate) {
            val updateTimer = config.getInt("config.update_timer")
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, {
                downloadFile()
                reloadWhitelist()
            }, 0L, 20L * 60 * updateTimer)
        }

        server.pluginManager.registerEvents(PlayerListener(), this)

        Bukkit.getScheduler().runTaskAsynchronously(this, Runnable {
            downloadFile()
            reloadConfig()
            reloadWhitelist()
        })
    }

    fun isPlayerWhitelisted(name: String): Boolean {
        return whitelistedPlayers.contains(name)
    }

    private fun downloadFile() {
        val input = BufferedInputStream(URL(url).openStream())
        Files.copy(input, Paths.get(local), StandardCopyOption.REPLACE_EXISTING)
    }

    private fun reloadWhitelist() {
        val pendingWhitelistedPlayers = hashSetOf<String>()
        val bufferedRead = BufferedReader(FileReader(getLocalFile()))

        bufferedRead.lines().parallel().forEach {
            val array = it.split(seperator)
            pendingWhitelistedPlayers.add(array[column - 1])
        }

        whitelistedPlayers.clear()
        whitelistedPlayers.addAll(pendingWhitelistedPlayers)
    }

    private fun getLocalFile(): File {
        val localFile = File(local)
        if (!localFile.exists()) {
            localFile.createNewFile()
        }
        return localFile
    }


}