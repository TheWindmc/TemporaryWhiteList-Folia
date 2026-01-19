package ru.reosfire.temporarywhitelist.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.reosfire.temporarywhitelist.TemporaryWhiteList;
import ru.reosfire.temporarywhitelist.data.PlayerData;
import ru.reosfire.temporarywhitelist.data.PlayerDatabase;

import java.util.Set;
import java.util.stream.Collectors;

public class TemporaryWhiteListAPIImpl implements TemporaryWhiteListAPI {

    private final PlayerDatabase database;

    public TemporaryWhiteListAPIImpl(@NotNull TemporaryWhiteList plugin) {
        this.database = plugin.getDatabase();
    }

    @Override
    public boolean isWhitelisted(@NotNull String nickname) {
        return database.canJoin(nickname);
    }

    @Override
    public boolean addPlayer(@NotNull String nickname, long timeSeconds) {
        try {
            database.add(nickname, timeSeconds).join();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean addPlayerPermanent(@NotNull String nickname) {
        try {
            database.setPermanent(nickname).join();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean removePlayer(@NotNull String nickname) {
        try {
            return database.remove(nickname).join();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Nullable
    public Long getTimeLeft(@NotNull String nickname) {
        PlayerData data = database.getPlayerData(nickname);
        if (data == null || !data.canJoin()) {
            return null;
        }
        if (data.Permanent) {
            return null;
        }
        return data.timeLeft();
    }

    @Override
    @Nullable
    public Long getExpirationTime(@NotNull String nickname) {
        PlayerData data = database.getPlayerData(nickname);
        if (data == null || !data.canJoin()) {
            return null;
        }
        if (data.Permanent) {
            return null;
        }
        return data.endTime();
    }

    @Override
    @NotNull
    public Set<String> getActiveWhitelistedPlayers() {
        return database.activeList().stream()
                .map(playerData -> playerData.Name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isPermanent(@NotNull String nickname) {
        PlayerData data = database.getPlayerData(nickname);
        if (data == null) {
            return false;
        }
        return data.Permanent;
    }
}
