package me.rednero.maintenancemotd.bukkit.listeners;

import me.rednero.maintenancemotd.bukkit.MaintenanceMOTD;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Date;

public class onPlayerJoinListener implements Listener {
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final MiniMessage miniMessage;

    public onPlayerJoinListener() {
        legacyComponentSerializer = MaintenanceMOTD.getInstance().getLegacyComponentSerializer();
        miniMessage = MaintenanceMOTD.getInstance().getMinimessage();
    }

    @EventHandler
    public void onPlayerJoin(PlayerLoginEvent event) {
        if (MaintenanceMOTD.getInstance().isMaintenanceMode()) {
            if (event.getPlayer().hasPermission("maintenancemotd.bypass")) {
                return;
            }
            if (MaintenanceMOTD.getInstance().getReason().isEmpty()) {
                if (MaintenanceMOTD.getInstance().getUntil() == 0) {
                    event.setKickMessage(MaintenanceMOTD.getInstance().getMessages().getKickMessage());
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                } else {
                    long timeMillis = MaintenanceMOTD.getInstance().getUntil() - System.currentTimeMillis();
                    if (timeMillis <= 0) {
                        MaintenanceMOTD.getInstance().setMaintenanceMode(false);
                        MaintenanceMOTD.getInstance().setUntil(0);
                        MaintenanceMOTD.getInstance().setReason("");
                        MaintenanceMOTD.getInstance().saveChanges();
                        return;
                    }
                    event.setKickMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(MaintenanceMOTD.getInstance().getMessages().replaceTime(MaintenanceMOTD.getInstance().getMessages().getKickMessageUntil().replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()), timeMillis))));
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                }
            } else {
                if (MaintenanceMOTD.getInstance().getUntil() == 0) {
                    event.setKickMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(MaintenanceMOTD.getInstance().getMessages().getKickMessageReason().replace("%reason%", MaintenanceMOTD.getInstance().getReason()))));
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                } else {
                    long timeMillis = MaintenanceMOTD.getInstance().getUntil() - System.currentTimeMillis();
                    if (timeMillis <= 0) {
                        MaintenanceMOTD.getInstance().setMaintenanceMode(false);
                        MaintenanceMOTD.getInstance().setUntil(0);
                        MaintenanceMOTD.getInstance().setReason("");
                        MaintenanceMOTD.getInstance().saveChanges();
                        return;
                    }
                    event.setKickMessage(legacyComponentSerializer.serialize(miniMessage.deserialize(MaintenanceMOTD.getInstance().getMessages().replaceTime(MaintenanceMOTD.getInstance().getMessages().getKickMessageReasonUntil().replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()).replace("%reason%", MaintenanceMOTD.getInstance().getReason()), timeMillis))));
                    event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                }
            }
        }
    }
}
