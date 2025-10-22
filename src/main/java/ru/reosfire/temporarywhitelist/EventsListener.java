package ru.reosfire.temporarywhitelist;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import ru.reosfire.temporarywhitelist.configuration.localization.MessagesConfig;
import ru.reosfire.temporarywhitelist.data.PlayerDatabase;
import ru.reosfire.temporarywhitelist.lib.text.Text;

import java.util.HashSet;
import java.util.UUID;

public class EventsListener implements Listener
{
    private final MessagesConfig messages;
    private final PlayerDatabase database;
    private final TemporaryWhiteList pluginInstance;
    private final HashSet<UUID> bypassedByPreLogin = new HashSet<>();

    public EventsListener(MessagesConfig messages, PlayerDatabase database, TemporaryWhiteList pluginInstance)
    {
        this.messages = messages;
        this.database = database;
        this.pluginInstance = pluginInstance;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event)
    {
        if (!pluginInstance.isWhiteListEnabled()) return;

        if (database.canJoin(event.getName()))
        {
            bypassedByPreLogin.add(event.getUniqueId());
            return;
        }

        if (Bukkit.getOfflinePlayer(event.getUniqueId()).isOp())
            bypassedByPreLogin.add(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event)
    {
        if (!pluginInstance.isWhiteListEnabled()) return;
        Player player = event.getPlayer();

        if (bypassedByPreLogin.remove(player.getUniqueId())) return;
        if (player.hasPermission("TemporaryWhitelist.Bypass")) return;

        String messageText = String.join("\n", Text.colorize(player, messages.Kick.Connecting));
        Component kickMessage = Component.text(messageText);
        event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, kickMessage);
    }
}