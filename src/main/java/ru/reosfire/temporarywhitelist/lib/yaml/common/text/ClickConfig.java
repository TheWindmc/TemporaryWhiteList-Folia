package ru.reosfire.temporarywhitelist.lib.yaml.common.text;

import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import ru.reosfire.temporarywhitelist.lib.text.IColorizer;
import ru.reosfire.temporarywhitelist.lib.yaml.YamlConfig;

public class ClickConfig extends YamlConfig
{
    public final ClickEvent.Action Action;
    public final String Value;

    public ClickConfig(ConfigurationSection configurationSection)
    {
        super(configurationSection);
        String actionString = getString("Action").toUpperCase();

        switch (actionString) {
            case "OPEN_URL":
                Action = ClickEvent.Action.OPEN_URL;
                break;
            case "RUN_COMMAND":
                Action = ClickEvent.Action.RUN_COMMAND;
                break;
            case "SUGGEST_COMMAND":
                Action = ClickEvent.Action.SUGGEST_COMMAND;
                break;
            case "CHANGE_PAGE":
                Action = ClickEvent.Action.CHANGE_PAGE;
                break;
            case "COPY_TO_CLIPBOARD":
                Action = ClickEvent.Action.COPY_TO_CLIPBOARD;
                break;
            default:
                throw new IllegalArgumentException("Unknown click action: " + actionString);
        }

        Value = getColoredString("Value");
    }

    @Nullable
    public ClickEvent Unwrap(IColorizer colorizer)
    {
        return ClickEvent.clickEvent(Action, colorizer.colorize(Value));
    }
}