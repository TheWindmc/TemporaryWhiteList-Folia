package ru.reosfire.temporarywhitelist.loaders;

import ru.reosfire.temporarywhitelist.configuration.localization.MessagesConfig;
import ru.reosfire.temporarywhitelist.lib.yaml.YamlConfig;
import ru.reosfire.temporarywhitelist.TemporaryWhiteList;

import java.io.*;
import java.nio.file.Files;
import java.util.logging.Level;

public class LocalizationsLoader
{
    private static final String[] translationsResources = new String[]
            {
                    "en.yml",
                    "ru.yml",
                    "pt.yml",
            };

    private final TemporaryWhiteList plugin;

    public LocalizationsLoader(TemporaryWhiteList pluginInstance)
    {
        plugin = pluginInstance;
    }

    public void copyDefaultTranslations()
    {
        File translationsDirectory = new File(plugin.getDataFolder(), "./translations/");

        if (!translationsDirectory.exists() && !translationsDirectory.mkdir())
            throw new RuntimeException("Directory for translations couldn't created.");

        for (String translationsResource : translationsResources)
        {
            File translationFile = new File(translationsDirectory, translationsResource);
            if (translationFile.exists()) continue;

            try (InputStream resource = plugin.getResource("translations/" + translationsResource))
            {
                assert resource != null;
                Files.copy(resource, translationFile.toPath());
            }
            catch (Exception e)
            {
                plugin.getLogger().log(Level.SEVERE, "Can't load " + translationsResource + " from plugin jar. Is it corrupted?", e);
                throw new RuntimeException("Can't load " + translationsResource + " from plugin jar. Is it corrupted?", e);
            }
        }
    }

    public MessagesConfig loadMessages()
    {
        try
        {
            return new MessagesConfig(YamlConfig.loadOrCreate("translations/" + plugin.getConfiguration().Translation, plugin));
        }
        catch (Exception e)
        {
            plugin.getLogger().log(Level.SEVERE, "Can't load translation file", e);
            throw new RuntimeException("Can't load translation file", e);
        }
    }
}