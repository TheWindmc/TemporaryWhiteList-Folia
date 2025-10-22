package ru.reosfire.temporarywhitelist.lib.yaml.common.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ru.reosfire.temporarywhitelist.lib.text.IColorizer;
import ru.reosfire.temporarywhitelist.lib.text.Replacement;
import ru.reosfire.temporarywhitelist.lib.text.Text;
import ru.reosfire.temporarywhitelist.lib.yaml.YamlConfig;

import java.util.List;
import java.util.Locale;

public class TextComponentConfig extends YamlConfig
{
    public final String TextContent;
    public final List<TextComponentConfig> Content;
    public final ClickConfig ClickConfig;
    public final HoverConfig HoverConfig;
    public final TextColor Color;
    public final boolean Bold;
    public final boolean Italic;
    public final boolean Strikethrough;
    public final boolean Underlined;

    public TextComponentConfig(ConfigurationSection configurationSection)
    {
        super(configurationSection);
        if (isList("Content"))
        {
            Content = getList(TextComponentConfig::new, "Content");
            TextContent = null;
        }
        else
        {
            TextContent = getColoredString("Content", null);
            Content = null;
        }

        ConfigurationSection clickSection = getSection("Click", null);
        ClickConfig = clickSection == null ? null : new ClickConfig(clickSection);
        ConfigurationSection hoverSection = getSection("Hover", null);
        HoverConfig = hoverSection == null ? null : new HoverConfig(hoverSection);

        String color = getString("Color");
        Color = color == null ? null : NamedTextColor.NAMES.value(color.toLowerCase(Locale.ROOT));

        Bold = getBoolean("Bold", false);
        Italic = getBoolean("Italic", false);
        Strikethrough = getBoolean("Strikethrough", false);
        Underlined = getBoolean("Underlined", false);
    }

    public void Send(CommandSender receiver, Replacement... replacements)
    {
        if (receiver instanceof Player player)
        {
            player.sendMessage(Unwrap(player, replacements));
        }
        else receiver.sendMessage(toString(replacements));
    }

    public Component Unwrap(OfflinePlayer player, Replacement... replacements)
    {
        return Unwrap(s -> Text.colorize(player, s, replacements));
    }

    public Component Unwrap(IColorizer colorizer)
    {
        TextComponent.Builder builder;

        if (Content == null)
        {
            Component legacy = LegacyComponentSerializer.legacyAmpersand().deserialize(colorizer.colorize(TextContent));
            builder = Component.text().append(legacy);
        }
        else
        {
            builder = Component.text();
            for (TextComponentConfig subComponent : Content)
            {
                builder.append(subComponent.Unwrap(colorizer));
            }
        }

        if (ClickConfig != null) builder.clickEvent(ClickConfig.Unwrap(colorizer));
        if (HoverConfig != null) builder.hoverEvent(HoverConfig.Unwrap(colorizer));
        if (Color != null) builder.color(Color);

        builder.decoration(TextDecoration.BOLD, Bold);
        builder.decoration(TextDecoration.ITALIC, Italic);
        builder.decoration(TextDecoration.UNDERLINED, Underlined);
        builder.decoration(TextDecoration.STRIKETHROUGH, Strikethrough);

        return builder.build();
    }

    public String toString(Replacement... replacements)
    {
        if (TextContent != null) return Text.setColors(Replacement.set(TextContent, replacements));

        StringBuilder resultBuilder = new StringBuilder();

        for (TextComponentConfig subComponent : Content)
        {
            resultBuilder.append(subComponent.toString(replacements));
        }

        return Text.setColors(resultBuilder.toString());
    }

    @Override
    public String toString()
    {
        if (TextContent != null) return TextContent;

        StringBuilder resultBuilder = new StringBuilder();

        for (TextComponentConfig subComponent : Content)
        {
            resultBuilder.append(subComponent.toString());
        }

        return resultBuilder.toString();
    }
}