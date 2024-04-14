package me.rednero.maintenancemotd.bukkit.utils;

import me.rednero.maintenancemotd.bukkit.MaintenanceMOTD;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FakePlayers {
    private File fakePlayersFile;
    private YamlConfiguration fakePlayersConfig;
    private List<String> fakePlayers;
    private List<String> sortedFakePlayers;

    public FakePlayers() {
        loadFakePlayers();
    }

    private void loadFakePlayers() {
        // load fake players from fake-players.yml
        fakePlayersFile = new File(MaintenanceMOTD.getInstance().getDataFolder(), "fake-players.yml");
        if (!fakePlayersFile.exists()) {
            fakePlayersFile.getParentFile().mkdirs();
            MaintenanceMOTD.getInstance().saveResource("fake-players.yml", false);
        }
        fakePlayersConfig = new YamlConfiguration();
        try {
            fakePlayersConfig.load(fakePlayersFile);
        } catch (IOException | InvalidConfigurationException e) {
            MaintenanceMOTD.getInstance().getLogger().warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to load fake-players.yml");
            // e.printStackTrace();
        }
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
