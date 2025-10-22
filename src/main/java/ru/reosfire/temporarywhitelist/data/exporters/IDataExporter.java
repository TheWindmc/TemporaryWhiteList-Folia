package ru.reosfire.temporarywhitelist.data.exporters;

import org.bukkit.command.CommandSender;
import ru.reosfire.temporarywhitelist.configuration.localization.commandResults.ImportCommandResultConfig;
import ru.reosfire.temporarywhitelist.data.ExportResult;
import ru.reosfire.temporarywhitelist.data.IUpdatable;
import ru.reosfire.temporarywhitelist.data.PlayerData;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface IDataExporter
{
    Logger LOGGER = Logger.getLogger("IDataExporter");

    List<PlayerData> getAll();

    default ExportResult exportTo(IUpdatable updatable)
    {
        List<PlayerData> players = getAll();
        ExportResult exportResult = new ExportResult(players);

        CompletableFuture<?>[] updates = new CompletableFuture<?>[players.size()];

        for (int i = 0; i < players.size(); i++)
        {
            PlayerData playerData = players.get(i);

            updates[i] = updatable.update(playerData).handle((res, ex) ->
            {
                if (ex == null) exportResult.addWithoutError(playerData);
                else LOGGER.log(Level.SEVERE, "Error exporting player data: " + playerData, ex);
                return null;
            });
        }

        CompletableFuture.allOf(updates).join();

        return exportResult;
    }

    default CompletableFuture<ExportResult> exportToAsync(IUpdatable provider)
    {
        return CompletableFuture.supplyAsync(() -> exportTo(provider));
    }

    default void ExportAndHandle(IUpdatable updatable, ImportCommandResultConfig commandResults, CommandSender sender)
    {
        try
        {
            ExportResult exportResult = exportTo(updatable);
            commandResults.Success.Send(sender, exportResult.getReplacements());
        }
        catch (Exception e)
        {
            commandResults.Error.Send(sender);
            LOGGER.log(Level.SEVERE, "Error during export", e);
        }
    }

    default void exportAsyncAndHandle(IUpdatable updatable, ImportCommandResultConfig commandResults, CommandSender sender)
    {
        exportToAsync(updatable).handle((res, ex) ->
        {
            if (ex == null) commandResults.Success.Send(sender, res.getReplacements());
            else
            {
                commandResults.Error.Send(sender);
                LOGGER.log(Level.SEVERE, "Error during async export", ex);
            }
            return null;
        });
    }
}