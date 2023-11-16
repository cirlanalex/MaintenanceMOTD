package me.rednero.maintenancemotd.listeners;

import me.rednero.maintenancemotd.MaintenanceMOTD;
import me.rednero.maintenancemotd.utils.MOTD;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Iterator;

public class pingListener implements Listener {
    private final LegacyComponentSerializer legacyComponentSerializer;

    public pingListener() {
        legacyComponentSerializer = MaintenanceMOTD.getInstance().getLegacyComponentSerializer();
    }

    @EventHandler
    public void onListPing(ServerListPingEvent event) {
        if (MaintenanceMOTD.getInstance().isMaxPlayersEnabled()) {
            event.setMaxPlayers(MaintenanceMOTD.getInstance().getMaxPlayers());
        }
        int fakeplayers = 0;
        if (MaintenanceMOTD.getInstance().isFakePlayersEnabled()) {
            fakeplayers = MaintenanceMOTD.getInstance().getFakePlayers();
        }
        int onlinePlayers = MaintenanceMOTD.getInstance().getServer().getOnlinePlayers().size();
        onlinePlayers += fakeplayers;
        // if maintenance mode is enabled, set the motd to the maintenance motd
        if (MaintenanceMOTD.getInstance().isMaintenanceMode()) {
            if (MaintenanceMOTD.getInstance().getUntil() != 0 && MaintenanceMOTD.getInstance().getUntil() - System.currentTimeMillis() <= 0) {
                MaintenanceMOTD.getInstance().setMaintenanceMode(false);
                MaintenanceMOTD.getInstance().setUntil(0);
                MaintenanceMOTD.getInstance().setReason("");
                MaintenanceMOTD.getInstance().saveConfig();
            } else {
                if (MaintenanceMOTD.getInstance().isCustomMaintenanceIconEnabled()) {
                    if (MaintenanceMOTD.getInstance().isCustomMaintenanceIconRandomOrder()) {
                        int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getCustomMaintenanceIcons().size());
                        Iterator<String> iterator = MaintenanceMOTD.getInstance().getCustomMaintenanceIcons().keySet().iterator();
                        for (int i = 0; i < index; i++) {
                            iterator.next();
                        }
                        event.setServerIcon(MaintenanceMOTD.getInstance().getCustomMaintenanceIcons().get(iterator.next()));
                    } else {
                        event.setServerIcon(MaintenanceMOTD.getInstance().getDefaultMaintenanceIcon());
                    }
                }
                if (MaintenanceMOTD.getInstance().isRandomMaintenanceMotd()) {
                    int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenanceMotds().size());
                    MOTD motd = MaintenanceMOTD.getInstance().getMaintenanceMotds().get(index);
                    event.setMotd(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), onlinePlayers, event.getMaxPlayers(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))));
                    return;
                }
                event.setMotd(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(1)), onlinePlayers, event.getMaxPlayers(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))));
                return;
            }
        }
        // if maintenance mode is disabled, set the motd to the normal motd
        if (MaintenanceMOTD.getInstance().isCustomIconEnabled()) {
            if (MaintenanceMOTD.getInstance().isCustomIconRandomOrder()) {
                int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getCustomIcons().size());
                Iterator<String> iterator = MaintenanceMOTD.getInstance().getCustomIcons().keySet().iterator();
                for (int i = 0; i < index; i++) {
                    iterator.next();
                }
                event.setServerIcon(MaintenanceMOTD.getInstance().getCustomIcons().get(iterator.next()));
            } else {
                event.setServerIcon(MaintenanceMOTD.getInstance().getDefaultIcon());
            }
        }
        if (MaintenanceMOTD.getInstance().isRandomNormalMotd()) {
            int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalMotds().size());
            MOTD motd = MaintenanceMOTD.getInstance().getNormalMotds().get(index);
            event.setMotd(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), onlinePlayers, event.getMaxPlayers(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))));
            return;
        }
        event.setMotd(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getNormalMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getNormalMotd().get(1)), onlinePlayers, event.getMaxPlayers(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))));
    }
}
