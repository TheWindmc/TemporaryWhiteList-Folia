package ru.reosfire.temporarywhitelist.lib.yaml.common.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;
import ru.reosfire.temporarywhitelist.lib.text.IColorizer;
import ru.reosfire.temporarywhitelist.lib.yaml.YamlConfig;

public class HoverConfig extends YamlConfig
{
    public final String Action;
    public final String Value;

    public HoverConfig(ConfigurationSection configurationSection)
    {
        super(configurationSection);
        Action = getString("Action").toUpperCase();
        Value = getColoredString("Value");
    }

    @Nullable
    public HoverEvent<?> Unwrap(IColorizer colorizer)
    {
        Component hoverComponent = LegacyComponentSerializer.legacyAmpersand()
                .deserialize(colorizer.colorize(Value));

        return switch (Action) {
            case "SHOW_TEXT" -> HoverEvent.showText(hoverComponent);
            case "SHOW_ITEM" -> HoverEvent.hoverEvent(HoverEvent.Action.SHOW_ITEM,
                    HoverEvent.ShowItem.showItem(
                            net.kyori.adventure.key.Key.key("minecraft:stone"), 1));
            case "SHOW_ENTITY" -> null;
            default -> throw new IllegalArgumentException("Unknown hover action: " + Action);
        };
    }
}