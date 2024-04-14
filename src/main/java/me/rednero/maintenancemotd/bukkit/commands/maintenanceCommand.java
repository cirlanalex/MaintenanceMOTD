package me.rednero.maintenancemotd.bukkit.commands;

import com.google.common.collect.Lists;
import me.rednero.maintenancemotd.bukkit.MaintenanceMOTD;
import me.rednero.maintenancemotd.bukkit.utils.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.List;

public class maintenanceCommand extends abstractCommand {
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final MiniMessage miniMessage;

    public maintenanceCommand() {
        super("maintenance");
        legacyComponentSerializer = MaintenanceMOTD.getInstance().getLegacyComponentSerializer();
        miniMessage = MaintenanceMOTD.getInstance().getMinimessage();
    }

    @Override
    public void executeCommand(CommandSender sender, String label, String[] args) {
        Messages messages = MaintenanceMOTD.getInstance().getMessages();

        if (!sender.hasPermission("maintenancemotd.maintenance")) {
            sender.sendMessage(messages.getNoPermissionMessage());
            return;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            for (String line : messages.getHelpMessage()) {
                sender.sendMessage(line);
            }
            return;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            MaintenanceMOTD.getInstance().reloadSettings();
            sender.sendMessage(messages.getReloadedMessage());
            return;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (MaintenanceMOTD.getInstance().isMaintenanceMode()) {
                sender.sendMessage(messages.getAlreadyEnabledMessage());
                return;
            }
            MaintenanceMOTD.getInstance().setMaintenanceMode(true);
            MaintenanceMOTD.getInstance().setUntil(0);
            if (args.length > 1) {
                MaintenanceMOTD.getInstance().setReason(String.join(" ", args).substring(args[0].length() + 1));
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getEnabledMessageReason().replace("%reason%", String.join(" ", args).substring(args[0].length() + 1)))));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("maintenancemotd.bypass")) {
                        continue;
                    }
                    player.kickPlayer(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getKickMessageReason().replace("%reason%", String.join(" ", args).substring(args[0].length() + 1)))));
                }
                return;
            }
            MaintenanceMOTD.getInstance().saveChanges();
            sender.sendMessage(messages.getEnabledMessage());
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("maintenancemotd.bypass")) {
                    continue;
                }
                player.kickPlayer(messages.getKickMessage());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (!MaintenanceMOTD.getInstance().isMaintenanceMode()) {
                sender.sendMessage(messages.getAlreadyDisabledMessage());
                return;
            }
            MaintenanceMOTD.getInstance().setMaintenanceMode(false);
            MaintenanceMOTD.getInstance().setReason("");
            MaintenanceMOTD.getInstance().setUntil(0);
            MaintenanceMOTD.getInstance().saveChanges();
            sender.sendMessage(messages.getDisabledMessage());
            return;
        }

        if (args[0].equalsIgnoreCase("for")) {
            if (MaintenanceMOTD.getInstance().isMaintenanceMode()) {
                sender.sendMessage(messages.getAlreadyEnabledMessage());
                return;
            }
            if (args.length < 2) {
                sender.sendMessage(messages.getUntilUsageMessage());
                return;
            }
            // parse the time (10s is 10seconds, 10m is 10minutes, 10h is 10hours, 10d is 10days)
            String time = args[1];

            if (time.length() < 2) {
                sender.sendMessage(messages.getUntilUsageMessage());
                return;
            }

            // split from last character
            String number = time.substring(0, time.length() - 1);
            String unit = time.substring(time.length() - 1);

            // parse the number
            int timeNumber;
            try {
                timeNumber = Integer.parseInt(number);
            } catch (NumberFormatException e) {
                sender.sendMessage(messages.getUntilUsageMessage());
                return;
            }

            // parse the unit
            long timeUnit;
            switch (unit) {
                case "s":
                    timeUnit = 1000;
                    break;
                case "m":
                    timeUnit = 1000 * 60;
                    break;
                case "h":
                    timeUnit = 1000 * 60 * 60;
                    break;
                case "d":
                    timeUnit = 1000 * 60 * 60 * 24;
                    break;
                default:
                    sender.sendMessage(messages.getUntilUsageMessage());
                    return;
            }

            // calculate the time
            long timeMillis = timeNumber * timeUnit;

            MaintenanceMOTD.getInstance().setMaintenanceMode(true);
            // set the time
            MaintenanceMOTD.getInstance().setUntil(System.currentTimeMillis() + timeMillis);

            if (args.length > 2) {
                MaintenanceMOTD.getInstance().setReason(String.join(" ", args).substring(args[0].length() + args[1].length() + 2));
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.replaceTime(messages.getEnabledMessageReasonUntil().replace("%reason%", String.join(" ", args).substring(args[0].length() + args[1].length() + 2)).replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()), timeMillis))));
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.hasPermission("maintenancemotd.bypass")) {
                        continue;
                    }
                    player.kickPlayer(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.replaceTime(messages.getKickMessageReasonUntil().replace("%reason%", String.join(" ", args).substring(args[0].length() + args[1].length() + 2)).replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()), timeMillis))));
                }
                return;
            }
            MaintenanceMOTD.getInstance().saveChanges();
            sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.replaceTime(messages.getEnabledMessageUntil().replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()), timeMillis))));

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("maintenancemotd.bypass")) {
                    continue;
                }
                player.kickPlayer(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.replaceTime(messages.getKickMessageUntil().replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()), timeMillis))));
            }
            return;
        }

        if (args[0].equalsIgnoreCase("normal-icon")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getNormalIconUsageMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("on")) {
                MaintenanceMOTD.getInstance().setCustomIconEnabled(true);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getNormalIconEnabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("off")) {
                MaintenanceMOTD.getInstance().setCustomIconEnabled(false);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getNormalIconDisabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("random")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getNormalIconUsageRandomMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setCustomIconRandomOrder(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalIconRandomEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setCustomIconRandomOrder(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalIconRandomDisabledMessage());
                    return;
                }
            }
        }

        if (args[0].equalsIgnoreCase("maintenance-icon")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getMaintenanceIconUsageMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("on")) {
                MaintenanceMOTD.getInstance().setCustomMaintenanceIconEnabled(true);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getMaintenanceIconEnabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("off")) {
                MaintenanceMOTD.getInstance().setCustomMaintenanceIconEnabled(false);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getMaintenanceIconDisabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("random")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaintenanceIconUsageRandomMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setCustomMaintenanceIconRandomOrder(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceIconRandomEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setCustomMaintenanceIconRandomOrder(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceIconRandomDisabledMessage());
                    return;
                }
            }
        }

        if (args[0].equalsIgnoreCase("maxplayers")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getMaxPlayersUsageMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("on")) {
                MaintenanceMOTD.getInstance().setMaxPlayersEnabled(true);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getMaxPlayersEnabledMessage());
                return;
            }

            if (args[1].equalsIgnoreCase("off")) {
                MaintenanceMOTD.getInstance().setMaxPlayersEnabled(false);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getMaxPlayersDisabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaxPlayersUsageSetMessage());
                    return;
                }
                try {
                    int maxPlayers = Integer.parseInt(args[2]);
                    if (maxPlayers < 0) {
                        sender.sendMessage(messages.getMustBePositiveMessage());
                        return;
                    }
                    MaintenanceMOTD.getInstance().setMaxPlayers(maxPlayers);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getMaxPlayersSetMessage().replace("%maxplayers%", String.valueOf(maxPlayers)))));
                } catch (NumberFormatException e) {
                    sender.sendMessage(messages.getMaxPlayersUsageSetMessage());
                }
                return;
            }
        }

        if (args[0].equalsIgnoreCase("fakeplayers")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getFakePlayersUsageMessage());
                return;
            }

            if (args[1].equalsIgnoreCase("on")) {
                MaintenanceMOTD.getInstance().setFakePlayersEnabled(true);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getFakePlayersEnabledMessage());
                return;
            }

            if (args[1].equalsIgnoreCase("off")) {
                MaintenanceMOTD.getInstance().setFakePlayersEnabled(false);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getFakePlayersDisabledMessage());
                return;
            }

            if (args[1].equalsIgnoreCase("set")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getFakePlayersUsageSetMessage());
                    return;
                }
                try {
                    int fakePlayers = Integer.parseInt(args[2]);
                    if (fakePlayers < 0) {
                        sender.sendMessage(messages.getMustBePositiveMessage());
                        return;
                    }
                    MaintenanceMOTD.getInstance().setFakePlayers(fakePlayers);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getFakePlayersSetMessage().replace("%fakeplayers%", String.valueOf(fakePlayers)))));
                } catch (NumberFormatException e) {
                    sender.sendMessage(messages.getFakePlayersUsageSetMessage());
                }
                return;
            }

            if (args[1].equalsIgnoreCase("set-add-in-list")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getFakePlayersUsageSetAddInListMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setFakePlayersAddInList(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getFakePlayersAddInListEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setFakePlayersAddInList(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getFakePlayersAddInListDisabledMessage());
                    return;
                }
                sender.sendMessage(messages.getFakePlayersUsageSetAddInListMessage());
                return;
            }

            if (args[1].equalsIgnoreCase("set-random-order")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getFakePlayersUsageSetRandomOrderMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setFakePlayersRandomOrder(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getFakePlayersRandomOrderEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setFakePlayersRandomOrder(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getFakePlayersRandomOrderDisabledMessage());
                    return;
                }
                sender.sendMessage(messages.getFakePlayersUsageSetRandomOrderMessage());
                return;
            }
            sender.sendMessage(messages.getFakePlayersUsageMessage());
            return;
        }

        if (args[0].equalsIgnoreCase("normal-motd")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getNormalMotdUsageMessage());
                return;
            }
            try {
                int line = Integer.parseInt(args[1]);
                if (line < 1 || line > 2) {
                    sender.sendMessage(messages.getInvalidLineMessage());
                    return;
                }
                if (args.length < 3) {
                    MaintenanceMOTD.getInstance().setNormalMotd(line - 1, "");
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getNormalMotdSetCleanMessage().replace("%line%", String.valueOf(line)))));
                    return;
                }
                MaintenanceMOTD.getInstance().setNormalMotd(line - 1, String.join(" ", args).substring(args[0].length() + args[1].length() + 2));
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getNormalMotdSetMessage().replace("%line%", String.valueOf(line)).replace("%text%", String.join(" ", args).substring(args[0].length() + args[1].length() + 2)))));
            } catch (NumberFormatException e) {
                sender.sendMessage(messages.getNormalMotdUsageMessage());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("maintenance-motd")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getMaintenanceMotdUsageMessage());
                return;
            }
            try {
                int line = Integer.parseInt(args[1]);
                if (line < 1 || line > 2) {
                    sender.sendMessage(messages.getInvalidLineMessage());
                    return;
                }
                if (args.length < 3) {
                    MaintenanceMOTD.getInstance().setMaintenanceMotd(line - 1, "");
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getMaintenanceMotdSetCleanMessage().replace("%line%", String.valueOf(line)))));
                    return;
                }
                MaintenanceMOTD.getInstance().setMaintenanceMotd(line - 1, String.join(" ", args).substring(args[0].length() + args[1].length() + 2));
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getMaintenanceMotdSetMessage().replace("%line%", String.valueOf(line)).replace("%text%", String.join(" ", args).substring(args[0].length() + args[1].length() + 2)))));
            } catch (NumberFormatException e) {
                sender.sendMessage(messages.getMaintenanceMotdUsageMessage());
            }
            return;
        }

        if (args[0].equalsIgnoreCase("normal-random")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getNormalRandomUsageMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("motd")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getNormalRandomMotdUsageMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setRandomNormalMotd(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalRandomMotdEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setRandomNormalMotd(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalRandomMotdDisabledMessage());
                    return;
                }
            }
            if (args[1].equalsIgnoreCase("player-count")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getNormalRandomPlayerCountUsageMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setRandomNormalPlayerCount(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalRandomPlayerCountEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setRandomNormalPlayerCount(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalRandomPlayerCountDisabledMessage());
                    return;
                }
            }
            if (args[1].equalsIgnoreCase("hover")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getNormalRandomMotdUsageHoverMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setRandomNormalPlayerCountHover(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalRandomMotdHoverEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setRandomNormalPlayerCountHover(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalRandomMotdHoverDisabledMessage());
                    return;
                }
            }
            sender.sendMessage(messages.getNormalRandomUsageMessage());
            return;
        }

        if (args[0].equalsIgnoreCase("maintenance-random")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getMaintenanceRandomUsageMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("motd")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaintenanceRandomMotdUsageMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setRandomMaintenanceMotd(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceRandomMotdEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setRandomMaintenanceMotd(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceRandomMotdDisabledMessage());
                    return;
                }
            }
            if (args[1].equalsIgnoreCase("player-count")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaintenanceRandomPlayerCountUsageMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setRandomMaintenancePlayerCount(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceRandomPlayerCountEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setRandomMaintenancePlayerCount(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceRandomPlayerCountDisabledMessage());
                    return;
                }
            }
            if (args[1].equalsIgnoreCase("hover")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaintenanceRandomMotdUsageHoverMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setRandomMaintenancePlayerCountHover(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceRandomMotdHoverEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setRandomMaintenancePlayerCountHover(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenanceRandomMotdHoverDisabledMessage());
                    return;
                }
            }
            sender.sendMessage(messages.getMaintenanceRandomUsageMessage());
            return;
        }

        if (args[0].equalsIgnoreCase("normal-player-count")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getNormalPlayerCountUsageMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("on")) {
                MaintenanceMOTD.getInstance().setNormalPlayerCountEnabled(true);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getNormalPlayerCountEnabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("off")) {
                MaintenanceMOTD.getInstance().setNormalPlayerCountEnabled(false);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getNormalPlayerCountDisabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getNormalPlayerCountUsageSetMessage());
                    return;
                }
                MaintenanceMOTD.getInstance().setNormalPlayerCount(String.join(" ", args).substring(args[0].length() + args[1].length() + 2));
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getNormalPlayerCountSetMessage().replace("%text%", String.join(" ", args).substring(args[0].length() + args[1].length() + 2)))));
                return;
            }
            if (args[1].equalsIgnoreCase("remove")) {
                MaintenanceMOTD.getInstance().setNormalPlayerCount("");
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getNormalPlayerCountRemoveMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("hover")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getNormalPlayerCountUsageHoverMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setNormalPlayerCustomHover(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalPlayerCountHoverEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setNormalPlayerCustomHover(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getNormalPlayerCountHoverDisabledMessage());
                    return;
                }
            }
            if (args[1].equalsIgnoreCase("set-hover")) {
                if (args.length < 4) {
                    sender.sendMessage(messages.getNormalPlayerCountUsageSetHoverMessage());
                    return;
                }
                try {
                    int line = Integer.parseInt(args[2]);
                    if (line < 1) {
                        sender.sendMessage(messages.getAtLeastOneMessage());
                        return;
                    }
                    MaintenanceMOTD.getInstance().setNormalPlayerCountHover(line - 1, String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3));
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getNormalPlayerCountHoverSetMessage().replace("%line%", String.valueOf(line)).replace("%text%", String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3)))));
                } catch (NumberFormatException e) {
                    sender.sendMessage(messages.getNormalPlayerCountUsageSetHoverMessage());
                }
                return;
            }
            if (args[1].equalsIgnoreCase("remove-hover")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getNormalPlayerCountUsageRemoveHoverMessage());
                    return;
                }
                try {
                    int line = Integer.parseInt(args[2]);
                    if (line < 1) {
                        sender.sendMessage(messages.getAtLeastOneMessage());
                        return;
                    }
                    if (MaintenanceMOTD.getInstance().removeNormalPlayerCountHover(sender, line - 1)) {
                        MaintenanceMOTD.getInstance().saveChanges();
                        sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getNormalPlayerCountHoverRemoveMessage().replace("%line%", String.valueOf(line)))));
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(messages.getNormalPlayerCountUsageRemoveHoverMessage());
                }
                return;
            }
            sender.sendMessage(messages.getNormalPlayerCountUsageMessage());
            return;
        }

        if (args[0].equalsIgnoreCase("maintenance-player-count")) {
            if (args.length < 2) {
                sender.sendMessage(messages.getMaintenancePlayerCountUsageMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("on")) {
                MaintenanceMOTD.getInstance().setMaintenancePlayerCountEnabled(true);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getMaintenancePlayerCountEnabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("off")) {
                MaintenanceMOTD.getInstance().setMaintenancePlayerCountEnabled(false);
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getMaintenancePlayerCountDisabledMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaintenancePlayerCountUsageSetMessage());
                    return;
                }
                MaintenanceMOTD.getInstance().setMaintenancePlayerCount(String.join(" ", args).substring(args[0].length() + args[1].length() + 2));
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getMaintenancePlayerCountSetMessage().replace("%text%", String.join(" ", args).substring(args[0].length() + args[1].length() + 2)))));
                return;
            }
            if (args[1].equalsIgnoreCase("remove")) {
                MaintenanceMOTD.getInstance().setMaintenancePlayerCount("");
                MaintenanceMOTD.getInstance().saveChanges();
                sender.sendMessage(messages.getMaintenancePlayerCountRemoveMessage());
                return;
            }
            if (args[1].equalsIgnoreCase("hover")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaintenancePlayerCountUsageHoverMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("on")) {
                    MaintenanceMOTD.getInstance().setMaintenancePlayerCustomHover(true);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenancePlayerCountHoverEnabledMessage());
                    return;
                }
                if (args[2].equalsIgnoreCase("off")) {
                    MaintenanceMOTD.getInstance().setMaintenancePlayerCustomHover(false);
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(messages.getMaintenancePlayerCountHoverDisabledMessage());
                    return;
                }
            }
            if (args[1].equalsIgnoreCase("set-hover")) {
                if (args.length < 4) {
                    sender.sendMessage(messages.getMaintenancePlayerCountUsageSetHoverMessage());
                    return;
                }
                try {
                    int line = Integer.parseInt(args[2]);
                    if (line < 1) {
                        sender.sendMessage(messages.getAtLeastOneMessage());
                        return;
                    }
                    MaintenanceMOTD.getInstance().setMaintenancePlayerCountHover(line - 1, String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3));
                    MaintenanceMOTD.getInstance().saveChanges();
                    sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getMaintenancePlayerCountHoverSetMessage().replace("%line%", String.valueOf(line)).replace("%text%", String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3)))));
                } catch (NumberFormatException e) {
                    sender.sendMessage(messages.getMaintenancePlayerCountUsageSetHoverMessage());
                }
                return;
            }
            if (args[1].equalsIgnoreCase("remove-hover")) {
                if (args.length < 3) {
                    sender.sendMessage(messages.getMaintenancePlayerCountUsageRemoveHoverMessage());
                    return;
                }
                try {
                    int line = Integer.parseInt(args[2]);
                    if (line < 1) {
                        sender.sendMessage(messages.getAtLeastOneMessage());
                        return;
                    }
                    if (MaintenanceMOTD.getInstance().removeMaintenancePlayerCountHover(sender, line - 1)) {
                        MaintenanceMOTD.getInstance().saveChanges();
                        sender.sendMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(messages.getMaintenancePlayerCountHoverRemoveMessage().replace("%line%", String.valueOf(line)))));
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(messages.getMaintenancePlayerCountUsageRemoveHoverMessage());
                }
                return;
            }
            sender.sendMessage(messages.getMaintenancePlayerCountUsageMessage());
            return;
        }

        for (String line : messages.getHelpMessage()) {
            sender.sendMessage(line);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("reload", "on", "for", "off", "maxplayers", "fakeplayers", "normal-motd", "maintenance-motd", "normal-player-count", "maintenance-player-count", "normal-random", "maintenance-random", "normal-icon", "maintenance-icon");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("normal-motd") || args[0].equalsIgnoreCase("maintenance-motd")) {
                return Lists.newArrayList("1", "2");
            }
            if (args[0].equalsIgnoreCase("maxplayers") || args[0].equalsIgnoreCase("fakeplayers")) {
                return Lists.newArrayList("on", "off", "set", "set-add-in-list", "set-random-order");
            }
            if (args[0].equalsIgnoreCase("normal-player-count") || args[0].equalsIgnoreCase("maintenance-player-count")) {
                return Lists.newArrayList("on", "off", "set", "remove", "hover", "set-hover", "remove-hover");
            }
            if (args[0].equalsIgnoreCase("normal-random") || args[0].equalsIgnoreCase("maintenance-random")) {
                return Lists.newArrayList("motd", "player-count", "hover");
            }
            if (args[0].equalsIgnoreCase("normal-icon") || args[0].equalsIgnoreCase("maintenance-icon")) {
                return Lists.newArrayList("on", "off", "random");
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("fakeplayers")) {
                if (args[1].equalsIgnoreCase("set-add-in-list") || args[1].equalsIgnoreCase("set-random-order")) {
                    return Lists.newArrayList("on", "off");
                }
            }
            if (args[0].equalsIgnoreCase("normal-player-count") || args[0].equalsIgnoreCase("maintenance-player-count")) {
                if (args[1].equalsIgnoreCase("hover")) {
                    return Lists.newArrayList("on", "off");
                }
            }
            if (args[0].equalsIgnoreCase("normal-random") || args[0].equalsIgnoreCase("maintenance-random")) {
                if (args[1].equalsIgnoreCase("motd") || args[1].equalsIgnoreCase("player-count") || args[1].equalsIgnoreCase("hover")) {
                    return Lists.newArrayList("on", "off");
                }
            }
            if (args[0].equalsIgnoreCase("normal-icon") || args[0].equalsIgnoreCase("maintenance-icon")) {
                if (args[1].equalsIgnoreCase("random")) {
                    return Lists.newArrayList("on", "off");
                }
            }
        }
        return Lists.newArrayList("");
    }
}
