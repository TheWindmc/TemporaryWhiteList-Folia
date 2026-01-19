package ru.reosfire.temporarywhitelist.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@SuppressWarnings("unused")
public interface TemporaryWhiteListAPI {

    boolean isWhitelisted(@NotNull String nickname);

    boolean addPlayer(@NotNull String nickname, long timeSeconds);

    boolean addPlayerPermanent(@NotNull String nickname);

    boolean removePlayer(@NotNull String nickname);

    @Nullable
    Long getTimeLeft(@NotNull String nickname);

    @Nullable
    Long getExpirationTime(@NotNull String nickname);

    @NotNull
    Set<String> getActiveWhitelistedPlayers();

    boolean isPermanent(@NotNull String nickname);
}
