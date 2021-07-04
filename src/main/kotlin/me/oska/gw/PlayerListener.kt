package me.oska.gw

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

class PlayerListener: Listener {

    @EventHandler
    fun join(event: AsyncPlayerPreLoginEvent) {
        if (!GoogleWhitelist.instance.isPlayerWhitelisted(event.name)) {
            event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                GoogleWhitelist.instance.noWhitelistMessage.replace("%player%", event.name)
            )
        }
    }

}