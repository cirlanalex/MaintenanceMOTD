package me.rednero.maintenancemotd.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class abstractCommand extends Command implements TabExecutor {
    public abstractCommand(String command) {
        super(command, "maintenancemotd.maintenance", "mm", "maintenancemotd");
    }

    public abstract void executeCommand(CommandSender sender, String[] args);

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String[] args) {
        executeCommand(sender, args);
    }

    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
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
