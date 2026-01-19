# TemporaryWhiteList-Folia

Updated version of [TemporaryWhiteList](https://www.spigotmc.org/resources/temporarywhitelist.99914) with Folia support and extended API for external system integration.

A Minecraft server plugin for managing temporary whitelist based on player nicknames.

## Supported storage types:
- yaml
- mysql

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/twl add <nick> <time/permanent>` | Adds a player to the whitelist for a specified time, or adds time if already present | `TemporaryWhitelist.Administrate.Add` |
| `/twl remove <nick>` | Removes all data about player | `TemporaryWhitelist.Administrate.Remove` |
| `/twl set <nick> <time/permanent>` | Sets player whitelisted for time or permanently | `TemporaryWhitelist.Administrate.Set` |
| `/twl check` | Shows info about your own subscription (available by default) | `TemporaryWhitelist.CheckSelf` |
| `/twl check <nick>` | Shows info about other player subscription | `TemporaryWhitelist.Administrate.CheckOther` |
| `/twl enable/disable` | Enables or disables whitelist | `TemporaryWhitelist.Administrate.EnableDisable` |
| `/twl reload` | Reloads configuration, localization and data files | `TemporaryWhitelist.Administrate.Reload` |
| `/twl list` | Shows all players that can join | `TemporaryWhitelist.Administrate.List` |
| `/twl import <type> [args]` | Imports players from selected storage (minecraft, self-sql, self-yaml, easy-whitelist) | `TemporaryWhitelist.Administrate.Import` |

## Time format examples:
- 100:s,100:m,100:h,100:d,1:y
- 100:s
- 100:s,20:s

## Placeholders:
- %twl_plugin_status% - enabled/disabled
- %twl_player_status% - remaining time/never end/ended.
- %twl_start_time% - date time when player was added to whitelist
- %twl_left_time% - remaining time before player will become not whitelisted
- %twl_end_time% - date time when player will become not whitelisted
- %twl_permanent% - true/false

## License

Based on the [original plugin](https://github.com/reosfire/TemporaryWhiteList) by [reosfire](https://github.com/reosfire).
