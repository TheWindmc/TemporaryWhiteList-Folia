package ru.reosfire.temporarywhitelist.commands.subcommands;

import org.bukkit.command.CommandSender;
import ru.reosfire.temporarywhitelist.configuration.localization.commandResults.DisableCommandResultsConfig;
import ru.reosfire.temporarywhitelist.lib.commands.CommandName;
import ru.reosfire.temporarywhitelist.lib.commands.CommandNode;
import ru.reosfire.temporarywhitelist.lib.commands.CommandPermission;
import ru.reosfire.temporarywhitelist.TemporaryWhiteList;

import java.util.logging.Level;
import java.util.logging.Logger;

@CommandName("disable")
@CommandPermission("TemporaryWhitelist.Administrate.EnableDisable")
public class DisableCommand extends CommandNode
{
    private static final Logger LOGGER = Logger.getLogger("DisableCommand");

    private final TemporaryWhiteList plugin;
    private final DisableCommandResultsConfig commandResults;

    public DisableCommand(TemporaryWhiteList pluginInstance)
    {
        super(pluginInstance.getMessages().NoPermission);
        commandResults = pluginInstance.getMessages().CommandResults.Disable;
        plugin = pluginInstance;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args)
    {
        try
        {
            if (plugin.disable()) commandResults.Success.Send(sender);
            else commandResults.NothingChanged.Send(sender);
        }
        catch (Exception e)
        {
            commandResults.Error.Send(sender);
            LOGGER.log(Level.SEVERE, "Error disabling whitelist", e);
        }
        return true;
    }
}