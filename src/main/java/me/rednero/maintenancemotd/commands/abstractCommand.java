package me.rednero.maintenancemotd.commands;

import me.rednero.maintenancemotd.MaintenanceMOTD;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class abstractCommand implements CommandExecutor, TabCompleter {
    public abstractCommand(String command) {
        PluginCommand pluginCommand = MaintenanceMOTD.getInstance().getCommand(command);
        if (pluginCommand != null) {
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    public abstract void executeCommand(CommandSender sender, String label, String[] args);

    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        executeCommand(sender, label, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return filter(complete(sender, args), args);
    }

    private List<String> filter(List<String> list, String[] args) {
        if (list == null) {
            return null;
        }
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for (String arg : list) {
            if (arg.toLowerCase().startsWith(last.toLowerCase())) {
                result.add(arg);
            }
        }
        return result;
    }
}
