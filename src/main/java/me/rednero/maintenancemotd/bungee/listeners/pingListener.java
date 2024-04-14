package me.rednero.maintenancemotd.bungee.listeners;

import me.rednero.maintenancemotd.bungee.MaintenanceMOTD;
import me.rednero.maintenancemotd.bungee.utils.Downsampler;
import me.rednero.maintenancemotd.bungee.utils.MOTD;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

public class pingListener implements Listener {
    private final LegacyComponentSerializer legacyComponentSerializer;

    public pingListener() {
        legacyComponentSerializer = MaintenanceMOTD.getInstance().getLegacyComponentSerializer();
    }

    @EventHandler
    public void onListPing(ProxyPingEvent event) {
        if (event.getResponse() == null) {
            return;
        }
        ServerPing r = event.getResponse();
        ServerPing response = new ServerPing();

        response.setVersion(r.getVersion());
        response.setPlayers(r.getPlayers());

        ServerPing.Players players = response.getPlayers();
        ServerPing.Protocol version = response.getVersion();

        if (MaintenanceMOTD.getInstance().isMaxPlayersEnabled()) {
            players.setMax(MaintenanceMOTD.getInstance().getMaxPlayers());
        }
        int fakeplayers = 0;
        if (MaintenanceMOTD.getInstance().isFakePlayersEnabled()) {
            fakeplayers = MaintenanceMOTD.getInstance().getFakePlayers();
        }
        int onlinePlayers = ProxyServer.getInstance().getPlayers().size();
        onlinePlayers += fakeplayers;
        players.setOnline(onlinePlayers);
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
                        response.setFavicon(MaintenanceMOTD.getInstance().getCustomMaintenanceIcons().get(iterator.next()));
                    } else {
                        response.setFavicon(MaintenanceMOTD.getInstance().getDefaultMaintenanceIcon());
                    }
                }
                if (MaintenanceMOTD.getInstance().isHigherVersion()) {
                    if (version.getProtocol() < 341) {
                        if (MaintenanceMOTD.getInstance().isRandomMaintenanceMotd()) {
                            int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenanceMotds().size());
                            MOTD motd = MaintenanceMOTD.getInstance().getMaintenanceMotds().get(index);

                            response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))))));
                        } else {
                            response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(1)), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))))));
                        }
                    } else {
                        if (MaintenanceMOTD.getInstance().isRandomMaintenanceMotd()) {
                            int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenanceMotds().size());
                            MOTD motd = MaintenanceMOTD.getInstance().getMaintenanceMotds().get(index);
                            response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), onlinePlayers, players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))))));
                        } else {
                            response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getMaintenanceMotd().get(1)), onlinePlayers, players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))))));
                        }
                    }
                }
                if (MaintenanceMOTD.getInstance().isMaintenancePlayerCountEnabled()) {
                    if (MaintenanceMOTD.getInstance().isRandomMaintenancePlayerCount()) {
                        int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenancePlayerCounts().size());
                        version.setProtocol(-1);
                        version.setName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getMaintenancePlayerCounts().get(index), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
                    } else {
                        version.setProtocol(-1);
                        version.setName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getMaintenancePlayerCount(), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
                    }
                }
                if (MaintenanceMOTD.getInstance().isMaintenancePlayerCustomHover()) {
                    ArrayList<ServerPing.PlayerInfo> playersList = new ArrayList<>();
                    if (MaintenanceMOTD.getInstance().isRandomMaintenancePlayerCountHover()) {
                        int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getMaintenancePlayerCountHovers().size());
                        for (String line : MaintenanceMOTD.getInstance().getMaintenancePlayerCountHovers().get(index)) {
                            playersList.add(new ServerPing.PlayerInfo(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"), new UUID(0, 0)));
                        }
                    } else {
                        for (String line : MaintenanceMOTD.getInstance().getMaintenancePlayerCountHover()) {
                            playersList.add(new ServerPing.PlayerInfo(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"), new UUID(0, 0)));
                        }
                    }
                    players.setSample(playersList.toArray(new ServerPing.PlayerInfo[0]));
                    if (MaintenanceMOTD.getInstance().isMaintenancePlayerCountEnabled()) {
                        players.setOnline(0);
                    }
                } else {
                    if (MaintenanceMOTD.getInstance().isFakePlayersEnabled()) {
                        if (MaintenanceMOTD.getInstance().isFakePlayersAddInList()) {
                            ArrayList<ServerPing.PlayerInfo> playersList;
                            if (players.getSample() != null) {
                                playersList = new ArrayList<>(Arrays.asList(players.getSample()));
                            } else {
                                playersList = new ArrayList<>();
                            }
                            int i = playersList.size();
                            if (MaintenanceMOTD.getInstance().isFakePlayersRandomOrder()) {
                                MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers().sort((o1, o2) -> {
                                    if (Math.random() > 0.5) {
                                        return 1;
                                    } else {
                                        return -1;
                                    }
                                });
                                for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers()) {
                                    if (i >= 10 || i >= players.getOnline()) {
                                        break;
                                    }
                                    playersList.add(new ServerPing.PlayerInfo(player_name, new UUID(0, 0)));
                                    i++;
                                }
                            } else {
                                for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getSortedFakePlayers()) {
                                    if (i >= 10 || i >= players.getOnline()) {
                                        break;
                                    }
                                    playersList.add(new ServerPing.PlayerInfo(player_name, new UUID(0, 0)));
                                    i++;
                                }
                            }
                            players.setSample(playersList.toArray(new ServerPing.PlayerInfo[0]));
                        }
                    }
                }
                event.setResponse(response);
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
                response.setFavicon(MaintenanceMOTD.getInstance().getCustomIcons().get(iterator.next()));
            } else {
                response.setFavicon(MaintenanceMOTD.getInstance().getDefaultIcon());
            }
        }
        if (MaintenanceMOTD.getInstance().isHigherVersion()) {
            if (version.getProtocol() < 341) {
                if (MaintenanceMOTD.getInstance().isRandomNormalMotd()) {
                    int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalMotds().size());
                    MOTD motd = MaintenanceMOTD.getInstance().getNormalMotds().get(index);
                    response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))))));
                } else {
                    response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getNormalMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getNormalMotd().get(1)), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil())))))));
                }
            } else {
                if (MaintenanceMOTD.getInstance().isRandomNormalMotd()) {
                    int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalMotds().size());
                    MOTD motd = MaintenanceMOTD.getInstance().getNormalMotds().get(index);
                    response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(motd.getLine1().concat("\n").concat(motd.getLine2()), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))))));
                } else {
                    response.setDescriptionComponent(new TextComponent(TextComponent.fromLegacyText(legacyComponentSerializer.serialize(MaintenanceMOTD.getInstance().getMinimessage().deserialize(MaintenanceMOTD.getInstance().getMessages().replacePlaceholders(MaintenanceMOTD.getInstance().getNormalMotd().get(0).concat("\n").concat(MaintenanceMOTD.getInstance().getNormalMotd().get(1)), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()))))));
                }
            }
        }
        if (MaintenanceMOTD.getInstance().isNormalPlayerCountEnabled()) {
            if (MaintenanceMOTD.getInstance().isRandomNormalPlayerCount()) {
                int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalPlayerCounts().size());
                version.setProtocol(-1);
                version.setName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getNormalPlayerCounts().get(index), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
            } else {
                version.setProtocol(-1);
                version.setName(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(MaintenanceMOTD.getInstance().getNormalPlayerCount(), players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"));
            }
        }
        if (MaintenanceMOTD.getInstance().isNormalPlayerCustomHover()) {
            ArrayList<ServerPing.PlayerInfo> playersList = new ArrayList<>();
            if (MaintenanceMOTD.getInstance().isRandomNormalPlayerCountHover()) {
                int index = (int) (Math.random() * MaintenanceMOTD.getInstance().getNormalPlayerCountHovers().size());
                for (String line : MaintenanceMOTD.getInstance().getNormalPlayerCountHovers().get(index)) {
                    playersList.add(new ServerPing.PlayerInfo(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"), new UUID(0, 0)));
                }
            } else {
                for (String line : MaintenanceMOTD.getInstance().getNormalPlayerCountHover()) {
                    playersList.add(new ServerPing.PlayerInfo(MaintenanceMOTD.getInstance().getMessages().replacePlaceholdersDownscale(line, players.getOnline(), players.getMax(), MaintenanceMOTD.getInstance().getReason(), MaintenanceMOTD.getInstance().getUntil()).replace("&", "§"), new UUID(0, 0)));
                }
            }
            players.setSample(playersList.toArray(new ServerPing.PlayerInfo[0]));
            if (MaintenanceMOTD.getInstance().isNormalPlayerCountEnabled()) {
                players.setOnline(0);
            }
        } else {
            if (MaintenanceMOTD.getInstance().isFakePlayersEnabled()) {
                if (MaintenanceMOTD.getInstance().isFakePlayersAddInList()) {
                    ArrayList<ServerPing.PlayerInfo> playersList;
                    if (players.getSample() != null) {
                        playersList = new ArrayList<>(Arrays.asList(players.getSample()));
                    } else {
                        playersList = new ArrayList<>();
                    }
                    int i = playersList.size();
                    if (MaintenanceMOTD.getInstance().isFakePlayersRandomOrder()) {
                        MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers().sort((o1, o2) -> {
                            if (Math.random() > 0.5) {
                                return 1;
                            } else {
                                return -1;
                            }
                        });
                        for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getFakePlayers()) {
                            if (i >= 10 || i >= players.getOnline()) {
                                break;
                            }
                            playersList.add(new ServerPing.PlayerInfo(player_name, new UUID(0, 0)));
                            i++;
                        }
                    } else {
                        for (String player_name : MaintenanceMOTD.getInstance().getFakePlayersInstance().getSortedFakePlayers()) {
                            if (i >= 10 || i >= players.getOnline()) {
                                break;
                            }
                            playersList.add(new ServerPing.PlayerInfo(player_name, new UUID(0, 0)));
                            i++;
                        }
                    }
                    players.setSample(playersList.toArray(new ServerPing.PlayerInfo[0]));
                }
            }
        }
        event.setResponse(response);
    }
}
