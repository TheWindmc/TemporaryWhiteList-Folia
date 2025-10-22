package ru.reosfire.temporarywhitelist.commands.subcommands.importTypes;

import org.bukkit.command.CommandSender;
import ru.reosfire.temporarywhitelist.configuration.localization.commandResults.ImportCommandResultConfig;
import ru.reosfire.temporarywhitelist.data.exporters.EasyWhitelist;
import ru.reosfire.temporarywhitelist.data.exporters.IDataExporter;
import ru.reosfire.temporarywhitelist.data.PlayerDatabase;
import ru.reosfire.temporarywhitelist.lib.commands.CommandName;
import ru.reosfire.temporarywhitelist.lib.commands.CommandNode;
import ru.reosfire.temporarywhitelist.TemporaryWhiteList;
import ru.reosfire.temporarywhitelist.TimeConverter;

import javax.management.ReflectionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandName("easy-whitelist")
public class EasyWhitelistImportCommand extends CommandNode
{
    private static final Logger LOGGER = Logger.getLogger("EasyWhitelistImportCommand");

    private final ImportCommandResultConfig commandResults;
    private final PlayerDatabase database;
    private final TimeConverter timeConverter;

    public EasyWhitelistImportCommand(TemporaryWhiteList pluginInstance)
    {
        super(pluginInstance.getMessages().NoPermission);
        commandResults = pluginInstance.getMessages().CommandResults.Import;
        database = pluginInstance.getDatabase();
        timeConverter = pluginInstance.getTimeConverter();
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args)
    {
        if (sendMessageIf(args.length != 2, commandResults.EasyWhiteListUsage, sender)) return true;

        AtomicReference<Long> defaultTime = new AtomicReference<>();
        if (tryParse(timeConverter::parseTime, args[0], defaultTime))
        {
            commandResults.IncorrectTime.Send(sender);
            return true;
        }

        AtomicReference<Boolean> defaultPermanent = new AtomicReference<>();
        if (tryParse(Boolean::parseBoolean, args[1], defaultPermanent))
        {
            commandResults.IncorrectPermanent.Send(sender);
            return true;
        }

        try
        {
            IDataExporter dataExporter = new EasyWhitelist(defaultTime.get(), defaultPermanent.get());
            dataExporter.exportAsyncAndHandle(database, commandResults, sender);
            commandResults.SuccessfullyStarted.Send(sender);
        }
        catch (ReflectionException e)
        {
            commandResults.EasyWhiteListPluginNotFound.Send(sender);
            LOGGER.log(Level.SEVERE, "EasyWhitelist plugin not found or reflection error", e);
        }
        return true;
    }
}