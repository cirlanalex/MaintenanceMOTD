package me.rednero.maintenancemotd.bukkit.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import me.rednero.maintenancemotd.bukkit.MaintenanceMOTD;
import me.rednero.maintenancemotd.bukkit.utils.Downsampler;
import me.rednero.maintenancemotd.bukkit.utils.MOTD;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.UUID;

public class protocolLibListener extends PacketAdapter {
    LegacyComponentSerializer legacyComponentSerializer;

    public protocolLibListener(Plugin plugin, ListenerPriority listenerPriority, PacketType... types) {
        super(plugin, listenerPriority, types);
        legacyComponentSerializer = MaintenanceMOTD.getInstance().getLegacyComponentSerializer();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        try {
            WrappedServerPing ping = event.getPacket().getServerPings().read(0);
            if (MaintenanceMOTD.getInstance().isFakePlayersEnabled()) {
                ping.setPlayersOnline(ping.getPlayersOnline() + MaintenanceMOTD.getInstance().getFakePlayers());
            }
            if (MaintenanceMOTD.getInstance().isMaintenanceMode()) {
                if (MaintenanceMOTD.getInstance().isHigherVersion()) {
                    if (ProtocolLibrary.getProtocolManager().getProtocolVersion(event.getPlayer()) < 341) {
                        if (MaintenanceMOTD.getInstance().isRandomMaintenanceMotd()) {
                            int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenanceMotds().size());
                            MOTD motd = MaintenanceMOTD.getInstance().getMaintenanceMotds().get(index);
                            ping.setMotD(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))));
                        } else {
                            ping.setMotD(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(1)), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))));
                        }
                    }
                }
                if (MaintenanceMOTD.getInstance().isMaintenancePlayerCountEnabled()) {
                    if (MaintenanceMOTD.getInstance().isRandomMaintenancePlayerCount()) {
                        int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenancePlayerCounts().size());
                        ping.setVersionProtocol(-1);
                        ping.setVersionName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getMaintenancePlayerCounts().get(index), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
                    } else {
                        ping.setVersionProtocol(-1);
                        ping.setVersionName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getMaintenancePlayerCount(), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
                    }
                }
                if (MaintenanceMOTD.getInstance().isMaintenancePlayerCustomHover()) {
                    ArrayList<WrappedGameProfile> players = new ArrayList<>();
                    if (MaintenanceMOTD.getInstance().isRandomMaintenancePlayerCountHover()) {
                        int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenancePlayerCountHovers().size());
                        for (String line : MaintenanceMOTD.getInstance().getMaintenancePlayerCountHovers().get(index)) {
                            players.add(new WrappedGameProfile(new UUID(0, 0), MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§")));
                        }
                    } else {
                        for (String line : MaintenanceMOTD.getInstance().getMaintenancePlayerCountHover()) {
                            players.add(new WrappedGameProfile(new UUID(0, 0), MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§")));
                        }
                    }
                    ping.setPlayers(players);
                    if (MaintenanceMOTD.getInstance().isMaintenancePlayerCountEnabled()) {
                        ping.setPlayersOnline(0);
                    }
                } else {
                    if (MaintenanceMOTD.getInstance().isFakePlayersEnabled()) {
                        if (MaintenanceMOTD.getInstance().isFakePlayersAddInList()) {
                            ArrayList<WrappedGameProfile> players = new ArrayList<>();
                            for (WrappedGameProfile player : ping.getPlayers()) {
                                players.add(player);
                            }
                            int i = players.size();
                            if (MaintenanceMOTD.getInstance().isFakePlayersRandomOrder()) {
                                MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers().sort((o1, o2) -> {
                                    if (Math.random() > 0.5) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                });
                                for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers()) {
                                    if (i >= 10 || i >= ping.getPlayersOnline()) {
                                        break;
                                    }
                                    players.add(new WrappedGameProfile(new UUID(0, 0), player_name));
                                    i++;
                                }
                            } else {
                                for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getSortedFakePlayers()) {
                                    if (i >= 10 || i >= ping.getPlayersOnline()) {
                                        break;
                                    }
                                    players.add(new WrappedGameProfile(new UUID(0, 0), player_name));
                                    i++;
                                }
                            }
                            ping.setPlayers(players);
                        }
                    }
                }
            } else {

                if (MaintenanceMOTD.getInstance().isHigherVersion()) {
                    if (ProtocolLibrary.getProtocolManager().getProtocolVersion(event.getPlayer()) < 341) {
                        if (MaintenanceMOTD.getInstance().isRandomNormalMotd()) {
                            int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalMotds().size());
                            MOTD motd = MaintenanceMOTD.getInstance().getNormalMotds().get(index);
                            ping.setMotD(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))));
                        } else {
                            ping.setMotD(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getNormalMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getNormalMotd().get(1)), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))));
                        }
                    }
                }
                if (MaintenanceMOTD.getInstance().isNormalPlayerCountEnabled()) {
                    if (MaintenanceMOTD.getInstance().isRandomNormalPlayerCount()) {
                        int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalPlayerCounts().size());
                        ping.setVersionProtocol(-1);
                        ping.setVersionName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getNormalPlayerCounts().get(index), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
                    } else {
                        ping.setVersionProtocol(-1);
                        ping.setVersionName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getNormalPlayerCount(), ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
                    }
                }
                if (MaintenanceMOTD.getInstance().isNormalPlayerCustomHover()) {
                    ArrayList<WrappedGameProfile> players = new ArrayList<>();
                    if (MaintenanceMOTD.getInstance().isRandomNormalPlayerCountHover()) {
                        int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalPlayerCountHovers().size());
                        for (String line : MaintenanceMOTD.getInstance().getNormalPlayerCountHovers().get(index)) {
                            players.add(new WrappedGameProfile(new UUID(0, 0), MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§")));
                        }
                    } else {
                        for (String line : MaintenanceMOTD.getInstance().getNormalPlayerCountHover()) {
                            players.add(new WrappedGameProfile(new UUID(0, 0), MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, ping.getPlayersOnline(), ping.getPlayersMaximum(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§")));
                        }
                    }
                    ping.setPlayers(players);
                    if (MaintenanceMOTD.getInstance().isNormalPlayerCountEnabled()) {
                        ping.setPlayersOnline(0);
                    }
                } else {
                    if (MaintenanceMOTD.getInstance().isFakePlayersEnabled()) {
                        if (MaintenanceMOTD.getInstance().isFakePlayersAddInList()) {
                            ArrayList<WrappedGameProfile> players = new ArrayList<>();
                            for (WrappedGameProfile player : ping.getPlayers()) {
                                players.add(player);
                            }
                            int i = players.size();
                            if (MaintenanceMOTD.getInstance().isFakePlayersRandomOrder()) {
                                MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers().sort((o1, o2) -> {
                                    if (Math.random() > 0.5) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                });
                                for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers()) {
                                    if (i >= 10 || i >= ping.getPlayersOnline()) {
                                        break;
                                    }
                                    players.add(new WrappedGameProfile(new UUID(0, 0), player_name));
                                    i++;
                                }
                            } else {
                                for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getSortedFakePlayers()) {
                                    if (i >= 10 || i >= ping.getPlayersOnline()) {
                                        break;
                                    }
                                    players.add(new WrappedGameProfile(new UUID(0, 0), player_name));
                                    i++;
                                }
                            }
                            ping.setPlayers(players);
                        }
                    }
                }
            }
            event.getPacket().getServerPings().write(0, ping);
        } catch (Exception err) {
            MaintenanceMOTD.getInstance().getLogger().warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to send the ping packet.");
            // err.printStackTrace();
        }
    }
}
