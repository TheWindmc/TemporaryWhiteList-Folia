package ru.reosfire.temporarywhitelist.lib.yaml;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.reosfire.temporarywhitelist.lib.text.Text;
import ru.reosfire.temporarywhitelist.lib.yaml.common.text.MultilineMessage;
import ru.reosfire.temporarywhitelist.lib.yaml.common.text.TextComponentConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class YamlConfig
{
    private static final Logger LOGGER = Logger.getLogger("YamlConfig");

    public static YamlConfiguration loadOrCreate(String resultFileName, String defaultConfigurationResource,
                                                 JavaPlugin plugin) throws IOException, InvalidConfigurationException
    {
        YamlConfiguration config = new YamlConfiguration();
        config.load(loadOrCreateFile(resultFileName, defaultConfigurationResource, plugin));
        return config;
    }

    public static YamlConfiguration loadOrCreate(File file) throws IOException, InvalidConfigurationException
    {
        YamlConfiguration config = new YamlConfiguration();
        config.load(file);
        return config;
    }

    public static YamlConfiguration loadOrCreate(String fileName, JavaPlugin plugin) throws IOException,
            InvalidConfigurationException
    {
        return loadOrCreate(fileName, fileName, plugin);
    }

    public static File loadOrCreateFile(String resultFileName, String defaultConfigurationResource,
                                        JavaPlugin plugin) throws IOException
    {
        File configFile = new File(plugin.getDataFolder(), resultFileName);

        if (!configFile.exists())
        {
            if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs())
                throw new RuntimeException("Can't create directory for " + resultFileName);

            try(InputStream resource = plugin.getResource(defaultConfigurationResource))
            {
                assert resource != null;
                Files.copy(resource, configFile.toPath());
            }
        }
        return configFile;
    }

    public static File loadOrCreateFile(String fileName, JavaPlugin plugin) throws IOException
    {
        return loadOrCreateFile(fileName, fileName, plugin);
    }

    protected final ConfigurationSection configurationSection;

    public ConfigurationSection getSection()
    {
        return configurationSection;
    }

    public YamlConfig(ConfigurationSection configurationSection)
    {
        if (configurationSection == null) throw new NullPointerException("configurationSection");
        this.configurationSection = configurationSection;
    }

    private <T extends YamlConfig> List<T> getNestedConfigs(IConfigCreator<T> creator, ConfigurationSection section)
    {
        ArrayList<T> result = new ArrayList<>();
        for (String key : section.getKeys(false))
        {
            try
            {
                result.add(creator.Create(section.getConfigurationSection(key)));
            }
            catch (Exception e)
            {
                LOGGER.log(Level.SEVERE, "Error loading nested config: " + key, e);
            }
        }
        return result;
    }

    public <T extends YamlConfig> List<T> getList(IConfigCreator<T> creator, String path)
    {
        List<?> list = getSection().getList(path);
        if (list == null) return null;

        MemoryConfiguration tempConfig = new MemoryConfiguration();
        for (int i = 0; i < list.size(); i++)
        {
            tempConfig.createSection(Integer.toString(i), (Map<?, ?>) list.get(i));
        }

        return getNestedConfigs(creator, tempConfig);
    }

    public String getString(String path)
    {
        return configurationSection.getString(path);
    }

    public String getString(String path, String def)
    {
        return configurationSection.getString(path, def);
    }

    public String getColoredString(String path)
    {
        return ChatColor.translateAlternateColorCodes('&', getString(path));
    }

    public String getColoredString(String path, String def)
    {
        return ChatColor.translateAlternateColorCodes('&', getString(path, def));
    }

    public int getInt(String path)
    {
        return configurationSection.getInt(path);
    }

    public int getInt(String path, int def)
    {
        return configurationSection.getInt(path, def);
    }

    public long getLong(String path)
    {
        return configurationSection.getLong(path);
    }

    public long getLong(String path, long def)
    {
        return configurationSection.getLong(path, def);
    }

    public boolean getBoolean(String path, boolean def)
    {
        return configurationSection.getBoolean(path, def);
    }

    public ConfigurationSection getSection(String path)
    {
        ConfigurationSection result = this.configurationSection.getConfigurationSection(path);
        if (result == null) {
            LOGGER.log(Level.WARNING, "Path not found: " + getSection().getCurrentPath() + "." + path + ", creating empty section");
            return configurationSection.createSection(path);
        }
        return result;
    }

    public ConfigurationSection getSection(String path, ConfigurationSection def)
    {
        ConfigurationSection result = this.configurationSection.getConfigurationSection(path);
        if (result == null) return def;
        return result;
    }

    public List<String> getStringList(String path)
    {
        List<String> stringList = configurationSection.getStringList(path);
        if (stringList.isEmpty())
        {
            String string = getString(path);
            if (string != null)
            {
                ArrayList<String> strings = new ArrayList<>();
                strings.add(string);
                return strings;
            }
        }
        return stringList;
    }

    public List<String> getColoredStringList(String path)
    {
        return Text.setColors(getStringList(path));
    }

    public boolean isList(String path)
    {
        return getSection().isList(path);
    }

    public MultilineMessage getMultilineMessage(String path)
    {
        return new MultilineMessage(getList(TextComponentConfig::new, path));
    }
}