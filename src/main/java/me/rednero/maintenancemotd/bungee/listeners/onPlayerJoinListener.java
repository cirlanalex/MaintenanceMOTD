package me.rednero.maintenancemotd.bungee.listeners;

import me.rednero.maintenancemotd.bungee.MaintenanceMOTD;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Date;

public class onPlayerJoinListener implements Listener {
    private final LegacyComponentSerializer legacyComponentSerializer;
    private final MiniMessage miniMessage;

    public onPlayerJoinListener() {
        legacyComponentSerializer = MaintenanceMOTD.getInstance().getLegacyComponentSerializer();
        miniMessage = MaintenanceMOTD.getInstance().getMinimessage();
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        if (MaintenanceMOTD.getInstance().isMaintenanceMode()) {
            if (event.getPlayer().hasPermission("maintenancemotd.bypass")) {
                return;
            }
            if (MaintenanceMOTD.getInstance().getReason().isEmpty()) {
                if (MaintenanceMOTD.getInstance().getUntil() == 0) {
                    event.getPlayer().disconnect(TextComponent.fromLegacyText(MaintenanceMOTD.getInstance().getMessages().getKickMessage()));
                } else {
                    long timeMillis = MaintenanceMOTD.getInstance().getUntil() - System.currentTimeMillis();
                    if (timeMillis <= 0) {
                        MaintenanceMOTD.getInstance().setMaintenanceMode(false);
                        MaintenanceMOTD.getInstance().setUntil(0);
                        MaintenanceMOTD.getInstance().setReason("");
                        MaintenanceMOTD.getInstance().saveChanges();
                        return;
                    }
                    event.getPlayer().disconnect(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(miniMessage.deserialize(MaintenanceMOTD.getInstance().getMessages().replaceTime(MaintenanceMOTD.getInstance().getMessages().getKickMessageUntil().replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()), timeMillis)))));
                }
            } else {
                if (MaintenanceMOTD.getInstance().getUntil() == 0) {
                    event.getPlayer().disconnect(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(miniMessage.deserialize(MaintenanceMOTD.getInstance().getMessages().getKickMessageReason().replace("%reason%", MaintenanceMOTD.getInstance().getReason())))));
                } else {
                    long timeMillis = MaintenanceMOTD.getInstance().getUntil() - System.currentTimeMillis();
                    if (timeMillis <= 0) {
                        MaintenanceMOTD.getInstance().setMaintenanceMode(false);
                        MaintenanceMOTD.getInstance().setUntil(0);
                        MaintenanceMOTD.getInstance().setReason("");
                        MaintenanceMOTD.getInstance().saveChanges();
                        return;
                    }
                    event.getPlayer().disconnect(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(miniMessage.deserialize(MaintenanceMOTD.getInstance().getMessages().replaceTime(MaintenanceMOTD.getInstance().getMessages().getKickMessageReasonUntil().replace("%until%", new Date(MaintenanceMOTD.getInstance().getUntil()).toString()).replace("%reason%", MaintenanceMOTD.getInstance().getReason()), timeMillis)))));
                }
            }
        }
    }
}
