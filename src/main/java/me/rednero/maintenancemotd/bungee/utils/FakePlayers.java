package me.rednero.maintenancemotd.bungee.utils;

import me.rednero.maintenancemotd.bungee.MaintenanceMOTD;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FakePlayers {
    private File fakePlayersFile;
    private Configuration fakePlayersConfig;
    private List<String> fakePlayers;
    private List<String> sortedFakePlayers;

    public FakePlayers() {
        loadFakePlayers();
    }

    public void loadConfiguration() {
        try {
            fakePlayersConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(MaintenanceMOTD.getInstance().getDataFolder(), "fake-players.yml"));
        } catch (IOException e) {
            MaintenanceMOTD.getInstance().getLogger().warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to load the fake-players.yml file");
            // e.printStackTrace();
        }
    }

    private void loadFakePlayers() {
        // load fake players from fake-players.yml
        fakePlayersFile = new File(MaintenanceMOTD.getInstance().getDataFolder(), "fake-players.yml");
        if (!fakePlayersFile.exists()) {
            fakePlayersFile.getParentFile().mkdirs();
            MaintenanceMOTD.getInstance().saveResource("fake-players.yml", false);
        }
        loadConfiguration();
        sortedFakePlayers = fakePlayersConfig.getStringList("fake-players");
        fakePlayers = new ArrayList<>(sortedFakePlayers);
    }

    public List<String> getFakePlayers() {
        return fakePlayers;
    }

    public List<String> getSortedFakePlayers() {
        return sortedFakePlayers;
    }
}
