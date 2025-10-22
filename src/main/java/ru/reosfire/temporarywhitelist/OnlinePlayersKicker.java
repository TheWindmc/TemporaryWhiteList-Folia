package ru.reosfire.temporarywhitelist;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.reosfire.temporarywhitelist.configuration.Config;
import ru.reosfire.temporarywhitelist.configuration.localization.MessagesConfig;
import ru.reosfire.temporarywhitelist.data.PlayerDatabase;
import ru.reosfire.temporarywhitelist.lib.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class OnlinePlayersKicker {
    private final TemporaryWhiteList pluginInstance;
    private final Config configuration;
    private final PlayerDatabase database;
    private final MessagesConfig messages;

    private ScheduledTask checkerTask;
    private ScheduledTask kickerTask;
    private final ConcurrentLinkedQueue<UUID> toKick = new ConcurrentLinkedQueue<>();

    public OnlinePlayersKicker(TemporaryWhiteList pluginInstance) {
        this.pluginInstance = pluginInstance;
        configuration = pluginInstance.getConfiguration();
        database = pluginInstance.getDatabase();
        messages = pluginInstance.getMessages();
    }

    public void start() {
        toKick.clear();
        runCheckerTask();
        runKickerTask();
    }

    public void stop() {
        if (checkerTask != null) checkerTask.cancel();
        if (kickerTask != null) kickerTask.cancel();
        toKick.clear();
    }

    private void runCheckerTask() {
        checkerTask = Bukkit.getAsyncScheduler().runAtFixedRate(pluginInstance, scheduledTask ->
        {
            List<PlayerInfo> potentialKickPlayersNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp()) {
                    continue;
                }
                if (player.hasPermission("TemporaryWhitelist.Bypass")) {
                    continue;
                }
                potentialKickPlayersNames.add(new PlayerInfo(player));
            }

            Bukkit.getAsyncScheduler().runNow(pluginInstance, scheduledTask1 -> {
                for (PlayerInfo player : potentialKickPlayersNames) {
                    if (database.canJoin(player.name)) continue;
                    toKick.add(player.uuid);
                }
            });
        }, 0, configuration.SubscriptionEndCheckTicks, TimeUnit.MILLISECONDS);
    }

    private void runKickerTask() {
        kickerTask = Bukkit.getAsyncScheduler().runAtFixedRate(pluginInstance, scheduledTask ->
        {
            while (!toKick.isEmpty()) {
                Player player = Bukkit.getPlayer(toKick.poll());
                if(player != null) {
                    if (!player.isOnline()) {
                        continue;
                    }
                    Component kickMessage = Component.text(String.join("\n", Text.colorize(player, messages.Kick.WhilePlaying)));
                    player.kick(kickMessage);
                }
            }
        }, configuration.SubscriptionEndCheckTicks / 2, configuration.SubscriptionEndCheckTicks, TimeUnit.MILLISECONDS);
    }

    private static class PlayerInfo {
        private final String name;
        private final UUID uuid;

        private PlayerInfo(Player player) {
            this.name = player.getName();
            this.uuid = player.getUniqueId();
        }
    }
}