package me.rednero.maintenancemotd.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import me.rednero.maintenancemotd.MaintenanceMOTD;
import me.rednero.maintenancemotd.listeners.protocolLibListener;

public class LoadProtocolLib {
    public static void loadProtocolLib() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new protocolLibListener(MaintenanceMOTD.getInstance(), ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO));
    }
}
