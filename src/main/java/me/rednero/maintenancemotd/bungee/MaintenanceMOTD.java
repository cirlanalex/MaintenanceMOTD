package me.rednero.maintenancemotd.bungee;

import me.rednero.maintenancemotd.bungee.commands.maintenanceCommand;
import me.rednero.maintenancemotd.bungee.listeners.onPlayerJoinListener;
import me.rednero.maintenancemotd.bungee.listeners.pingListener;
import me.rednero.maintenancemotd.bungee.utils.FakePlayers;
import me.rednero.maintenancemotd.bungee.utils.MOTD;
import me.rednero.maintenancemotd.bungee.utils.Messages;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public final class MaintenanceMOTD extends Plugin {

    private static MaintenanceMOTD instance;
    private final MiniMessage minimessage = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.decorations())
                    .resolver(StandardTags.rainbow())
                    .resolver(StandardTags.gradient())
                    .resolver(StandardTags.newline())
                    .build()
            )
            .build();
    private Messages messages;
    private boolean maxPlayersEnabled;
    private int maxPlayers;
    private boolean fakePlayersEnabled;
    private int fakePlayers;
    private boolean fakePlayersAddInList;
    private boolean fakePlayersRandomOrder;
    private FakePlayers fakePlayersInstance;
    private String normalMotdRawLine1;
    private String normalMotdRawLine2;
    private String maintenanceMotdRawLine1;
    private String maintenanceMotdRawLine2;
    private List<String> normalMotd;
    private List<String> maintenanceMotd;
    private boolean maintenanceMode = false;
    private boolean normalPlayerCountEnabled = false;
    private boolean maintenancePlayerCountEnabled = false;
    private String normalPlayerCount;
    private String maintenancePlayerCount;
    private boolean normalPlayerCustomHover;
    private boolean maintenancePlayerCustomHover;
    private List<String> normalPlayerCountHover;
    private List<String> maintenancePlayerCountHover;
    private String reason;
    private long until;
    private boolean randomNormalMotd;
    private boolean randomNormalPlayerCount;
    private boolean randomNormalPlayerCountHover;
    private boolean randomMaintenanceMotd;
    private boolean randomMaintenancePlayerCount;
    private boolean randomMaintenancePlayerCountHover;
    private List<MOTD> normalMotds;
    private List<MOTD> maintenanceMotds;
    private List<String> normalPlayerCounts;
    private List<String> maintenancePlayerCounts;
    private List<List<String>> normalPlayerCountHovers;
    private List<List<String>> maintenancePlayerCountHovers;
    private Logger loggerInstance;
    private boolean customIconEnabled;
    private boolean customIconRandomOrder;
    private String defaultIconName;
    private HashMap<String, Favicon> customIcons;
    private boolean customMaintenanceIconEnabled;
    private boolean customMaintenanceIconRandomOrder;
    private String defaultMaintenanceIconName;
    private HashMap<String, Favicon> customMaintenanceIcons;
    private LegacyComponentSerializer legacyComponentSerializer;
    private boolean higherVersion = false;

    public static MaintenanceMOTD getInstance() {
        return instance;
    }

    private Configuration configuration;

    public Configuration getConfig() {
        return configuration;
    }

    public void loadConfiguration() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            loggerInstance.warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to load the config file");
            // e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.getConfig(), new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            loggerInstance.warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to save the config file");
            // e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        if (!(this.getDataFolder().exists())) {
            this.getDataFolder().mkdir();
        }

        File file = new File(this.getDataFolder(), "config.yml");
        if (!(file.exists())) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                loggerInstance.warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to save the default config file");
                // e.printStackTrace();
            }
        }
    }

    public void saveResource(String resource, boolean replace) {
        File file = new File(this.getDataFolder(), resource);
        if (replace || !file.exists()) {
            try (InputStream in = getResourceAsStream(resource)) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                loggerInstance.warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to save the resource file");
                // e.printStackTrace();
            }
        }
    }

    public void loadConfig() {
        saveDefaultConfig();
        // load config
        loadConfiguration();
        reason = configuration.getString("reason");
        until = configuration.getLong("until");
        customIconEnabled = configuration.getBoolean("custom-icon.normal.enabled");
        customIconRandomOrder = configuration.getBoolean("custom-icon.normal.random");
        defaultIconName = configuration.getString("custom-icon.normal.default");
        customMaintenanceIconEnabled = configuration.getBoolean("custom-icon.maintenance.enabled");
        customMaintenanceIconRandomOrder = configuration.getBoolean("custom-icon.maintenance.random");
        defaultMaintenanceIconName = configuration.getString("custom-icon.maintenance.default");

        customIcons = new HashMap<>();
        // for every element in icons folder add it to customIcons list
        File iconsFolder = new File(getDataFolder(), "icons");
        if (!iconsFolder.exists()) {
            iconsFolder.mkdirs();
            saveResource("icons/1.png", false);
        }
        File[] icons = iconsFolder.listFiles();
        if (icons != null) {
            for (File icon : icons) {
                try {
                    BufferedImage image = ImageIO.read(icon);
                    customIcons.put(icon.getName(), Favicon.create(image));
                } catch (Exception e) {
                    MaintenanceMOTD.getInstance().getLogger().warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to load an icon from the icons folder");
                    // e.printStackTrace();
                }
            }
        }

        customMaintenanceIcons = new HashMap<>();
        // for every element in icons folder add it to customIcons list
        File maintenanceIconsFolder = new File(getDataFolder(), "maintenance-icons");
        if (!maintenanceIconsFolder.exists()) {
            maintenanceIconsFolder.mkdirs();
            saveResource("maintenance-icons/1.png", false);
        }
        File[] maintenanceIcons = maintenanceIconsFolder.listFiles();
        if (maintenanceIcons != null) {
            for (File icon : maintenanceIcons) {
                try {
                    BufferedImage image = ImageIO.read(icon);
                    customMaintenanceIcons.put(icon.getName(), Favicon.create(image));
                } catch (Exception e) {
                    MaintenanceMOTD.getInstance().getLogger().warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to load an icon from the maintenance-icons folder");
                    // e.printStackTrace();
                }
            }
        }

        maintenanceMode = configuration.getBoolean("maintenance-mode");
        maxPlayersEnabled = configuration.getBoolean("max-players.custom");
        maxPlayers = configuration.getInt("max-players.value");
        fakePlayersEnabled = configuration.getBoolean("fake-players.custom");
        fakePlayers = configuration.getInt("fake-players.value-to-add");
        fakePlayersAddInList = configuration.getBoolean("fake-players.add-in-list");
        fakePlayersRandomOrder = configuration.getBoolean("fake-players.random-order");
        normalMotdRawLine1 = configuration.getString("normal-motd.line1");
        normalMotdRawLine2 = configuration.getString("normal-motd.line2");
        normalMotd = new ArrayList<>();
        normalMotd.add(normalMotdRawLine1);
        normalMotd.add(normalMotdRawLine2);
        maintenanceMotdRawLine1 = configuration.getString("maintenance-motd.line1");
        maintenanceMotdRawLine2 = configuration.getString("maintenance-motd.line2");
        maintenanceMotd = new ArrayList<>();
        maintenanceMotd.add(maintenanceMotdRawLine1);
        maintenanceMotd.add(maintenanceMotdRawLine2);
        normalPlayerCountEnabled = configuration.getBoolean("normal-motd.custom-player-count");
        maintenancePlayerCountEnabled = configuration.getBoolean("maintenance-motd.custom-player-count");
        normalPlayerCount = configuration.getString("normal-motd.player-count");
        maintenancePlayerCount = configuration.getString("maintenance-motd.player-count");
        normalPlayerCustomHover = configuration.getBoolean("normal-motd.custom-hover");
        maintenancePlayerCustomHover = configuration.getBoolean("maintenance-motd.custom-hover");
        normalPlayerCountHover = (List<String>) configuration.getList("normal-motd.player-count-hover");
        maintenancePlayerCountHover = (List<String>) configuration.getList("maintenance-motd.player-count-hover");

        randomNormalMotd = configuration.getBoolean("normal-motd.random-motd");
        randomNormalPlayerCount = configuration.getBoolean("normal-motd.random-player-count");
        randomNormalPlayerCountHover = configuration.getBoolean("normal-motd.random-hover");
        randomMaintenanceMotd = configuration.getBoolean("maintenance-motd.random-motd");
        randomMaintenancePlayerCount = configuration.getBoolean("maintenance-motd.random-player-count");
        randomMaintenancePlayerCountHover = configuration.getBoolean("maintenance-motd.random-hover");

        Configuration normalMotdsSection = configuration.getSection("normal-motd.motds");
        normalMotds = new ArrayList<>();
        normalMotds.add(new MOTD(normalMotdRawLine1, normalMotdRawLine2));
        if (normalMotdsSection != null) {
            for (String key : normalMotdsSection.getKeys()) {
                String line1 = normalMotdsSection.getString(key + ".line1");
                String line2 = normalMotdsSection.getString(key + ".line2");
                normalMotds.add(new MOTD(line1, line2));
            }
        }

        Configuration maintenanceMotdsSection = configuration.getSection("maintenance-motd.motds");
        maintenanceMotds = new ArrayList<>();
        maintenanceMotds.add(new MOTD(maintenanceMotdRawLine1, maintenanceMotdRawLine2));
        if (maintenanceMotdsSection != null) {
            for (String key : maintenanceMotdsSection.getKeys()) {
                String line1 = maintenanceMotdsSection.getString(key + ".line1");
                String line2 = maintenanceMotdsSection.getString(key + ".line2");
                maintenanceMotds.add(new MOTD(line1, line2));
            }
        }

        List<String> readNormalPlayerCounts = (List<String>) configuration.getList("normal-motd.player-counts");
        normalPlayerCounts = new ArrayList<>(readNormalPlayerCounts);
        normalPlayerCounts.add(0, normalPlayerCount);
        List<String> readMaintenancePlayerCounts = (List<String>) configuration.getList("maintenance-motd.player-counts");
        maintenancePlayerCounts = new ArrayList<>(readMaintenancePlayerCounts);
        maintenancePlayerCounts.add(0, maintenancePlayerCount);

        Configuration normalPlayerCountHoversSection = configuration.getSection("normal-motd.player-count-hovers");
        normalPlayerCountHovers = new ArrayList<>();
        normalPlayerCountHovers.add(normalPlayerCountHover);
        if (normalPlayerCountHoversSection != null) {
            for (String key : normalPlayerCountHoversSection.getKeys()) {
                List<String> hover = (List<String>) normalPlayerCountHoversSection.getList(key);
                normalPlayerCountHovers.add(hover);
            }
        }

        Configuration maintenancePlayerCountHoversSection = configuration.getSection("maintenance-motd.player-count-hovers");
        maintenancePlayerCountHovers = new ArrayList<>();
        maintenancePlayerCountHovers.add(maintenancePlayerCountHover);
        if (maintenancePlayerCountHoversSection != null) {
            for (String key : maintenancePlayerCountHoversSection.getKeys()) {
                List<String> hover = (List<String>) maintenancePlayerCountHoversSection.getList(key);
                maintenancePlayerCountHovers.add(hover);
            }
        }

        if (until != 0 && System.currentTimeMillis() >= until) {
            MaintenanceMOTD.getInstance().setMaintenanceMode(false);
            MaintenanceMOTD.getInstance().setUntil(0);
            MaintenanceMOTD.getInstance().setReason("");
            MaintenanceMOTD.getInstance().saveChanges();
        }
    }

    public void reloadSettings() {
        this.loadConfig();

        // reload messages
        messages = new Messages();

        // reload fake players
        fakePlayersInstance = new FakePlayers();
    }

    public void saveChanges() {
        configuration.set("reason", reason);
        configuration.set("until", until);
        configuration.set("custom-icon.normal.enabled", customIconEnabled);
        configuration.set("custom-icon.normal.random", customIconRandomOrder);
        configuration.set("custom-icon.normal.default", defaultIconName);
        configuration.set("custom-icon.maintenance.enabled", customMaintenanceIconEnabled);
        configuration.set("custom-icon.maintenance.random", customMaintenanceIconRandomOrder);
        configuration.set("custom-icon.maintenance.default", defaultMaintenanceIconName);
        configuration.set("maintenance-mode", maintenanceMode);
        configuration.set("max-players.custom", maxPlayersEnabled);
        configuration.set("max-players.value", maxPlayers);
        configuration.set("fake-players.custom", fakePlayersEnabled);
        configuration.set("fake-players.value-to-add", fakePlayers);
        configuration.set("fake-players.add-in-list", fakePlayersAddInList);
        configuration.set("fake-players.random-order", fakePlayersRandomOrder);
        configuration.set("normal-motd.line1", normalMotdRawLine1);
        configuration.set("normal-motd.line2", normalMotdRawLine2);
        configuration.set("maintenance-motd.line1", maintenanceMotdRawLine1);
        configuration.set("maintenance-motd.line2", maintenanceMotdRawLine2);
        configuration.set("normal-motd.custom-player-count", normalPlayerCountEnabled);
        configuration.set("maintenance-motd.custom-player-count", maintenancePlayerCountEnabled);
        configuration.set("normal-motd.player-count", normalPlayerCount);
        configuration.set("maintenance-motd.player-count", maintenancePlayerCount);
        configuration.set("normal-motd.custom-hover", normalPlayerCustomHover);
        configuration.set("maintenance-motd.custom-hover", maintenancePlayerCustomHover);
        configuration.set("normal-motd.player-count-hover", normalPlayerCountHover);
        configuration.set("maintenance-motd.player-count-hover", maintenancePlayerCountHover);
        configuration.set("normal-motd.random-motd", randomNormalMotd);
        configuration.set("normal-motd.random-player-count", randomNormalPlayerCount);
        configuration.set("normal-motd.random-hover", randomNormalPlayerCountHover);
        configuration.set("maintenance-motd.random-motd", randomMaintenanceMotd);
        configuration.set("maintenance-motd.random-player-count", randomMaintenancePlayerCount);
        configuration.set("maintenance-motd.random-hover", randomMaintenancePlayerCountHover);


        saveConfig();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;

        loggerInstance = this.getLogger();

//        String version = getProxy().getClass().getPackage().getName().split("\\.")[3];
//        String subVersion = version.split("_")[1];
//        int subVersionInt = Integer.parseInt(subVersion);
//        if (subVersionInt > 12) {
//            higherVersion = true;
//            legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
//        } else {
//            legacyComponentSerializer = LegacyComponentSerializer.legacySection();
//        }
        higherVersion = true;
        legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors().useUnusualXRepeatedCharacterHexFormat().build();

        // load messages
        messages = new Messages();

        // load fake players
        fakePlayersInstance = new FakePlayers();

        // load config settings
        loadConfig();
        // register event listener
        getProxy().getPluginManager().registerListener(this, new pingListener());
        getProxy().getPluginManager().registerListener(this, new onPlayerJoinListener());

        // register commands
        // new maintenanceCommand();
        getProxy().getPluginManager().registerCommand(this, new maintenanceCommand());

        loggerInstance.info("Maintenance MOTD by RedNero was enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;

        loggerInstance.info("Maintenance MOTD by RedNero was disabled!");
    }

    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public boolean isMaxPlayersEnabled() {
        return maxPlayersEnabled;
    }

    public void setMaxPlayersEnabled(boolean maxPlayersEnabled) {
        this.maxPlayersEnabled = maxPlayersEnabled;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isFakePlayersEnabled() {
        return fakePlayersEnabled;
    }

    public void setFakePlayersEnabled(boolean fakePlayersEnabled) {
        this.fakePlayersEnabled = fakePlayersEnabled;
    }

    public int getFakePlayers() {
        return fakePlayers;
    }

    public void setFakePlayers(int fakePlayers) {
        this.fakePlayers = fakePlayers;
    }

    public List<String> getNormalMotd() {
        return normalMotd;
    }

    public List<String> getMaintenanceMotd() {
        return maintenanceMotd;
    }

    public void setNormalMotd(int line, String text) {
        if (line == 0) {
            this.normalMotdRawLine1 = text;
            this.normalMotds.get(0).setLine1(text);
        } else if (line == 1) {
            this.normalMotdRawLine2 = text;
            this.normalMotds.get(0).setLine2(text);
        } else {
            return;
        }
        this.normalMotd.set(line, text);
    }

    public void setMaintenanceMotd(int line, String text) {
        if (line == 0) {
            this.maintenanceMotdRawLine1 = text;
            this.maintenanceMotds.get(0).setLine1(text);
        } else if (line == 1) {
            this.maintenanceMotdRawLine2 = text;
            this.maintenanceMotds.get(0).setLine2(text);
        } else {
            return;
        }
        this.maintenanceMotd.set(line, text);
    }

    public MiniMessage getMinimessage() {
        return minimessage;
    }

    public Messages getMessages() {
        return messages;
    }

    public boolean isNormalPlayerCountEnabled() {
        return normalPlayerCountEnabled;
    }

    public void setNormalPlayerCountEnabled(boolean normalPlayerCountEnabled) {
        this.normalPlayerCountEnabled = normalPlayerCountEnabled;
    }

    public boolean isMaintenancePlayerCountEnabled() {
        return maintenancePlayerCountEnabled;
    }

    public void setMaintenancePlayerCountEnabled(boolean maintenancePlayerCountEnabled) {
        this.maintenancePlayerCountEnabled = maintenancePlayerCountEnabled;
    }

    public String getNormalPlayerCount() {
        return normalPlayerCount;
    }

    public void setNormalPlayerCount(String normalPlayerCount) {
        this.normalPlayerCount = normalPlayerCount;
        this.normalPlayerCounts.set(0, normalPlayerCount);
    }

    public String getMaintenancePlayerCount() {
        return maintenancePlayerCount;
    }

    public void setMaintenancePlayerCount(String maintenancePlayerCount) {
        this.maintenancePlayerCount = maintenancePlayerCount;
        this.maintenancePlayerCounts.set(0, maintenancePlayerCount);
    }

    public List<String> getNormalPlayerCountHover() {
        return normalPlayerCountHover;
    }

    public List<String> getMaintenancePlayerCountHover() {
        return maintenancePlayerCountHover;
    }

    public void setNormalPlayerCountHover(int line, String text) {
        int size = this.normalPlayerCountHover.size();
        if (line >= size) {
            for (int i = size; i <= line; i++) {
                this.normalPlayerCountHover.add("");
            }
        }
        this.normalPlayerCountHover.set(line, text);
    }

    public void setMaintenancePlayerCountHover(int line, String text) {
        int size = this.maintenancePlayerCountHover.size();
        if (line >= size) {
            for (int i = size; i <= line; i++) {
                this.maintenancePlayerCountHover.add("");
            }
        }
        this.maintenancePlayerCountHover.set(line, text);
    }

    public boolean removeNormalPlayerCountHover(CommandSender sender, int line) {
        if (line >= this.normalPlayerCountHover.size()) {
            sender.sendMessage(messages.getThisLineDoesNotExistMessage());
            return false;
        }
        this.normalPlayerCountHover.remove(line);
        return true;
    }

    public boolean removeMaintenancePlayerCountHover(CommandSender sender, int line) {
        if (line >= this.maintenancePlayerCountHover.size()) {
            sender.sendMessage(messages.getThisLineDoesNotExistMessage());
            return false;
        }
        this.maintenancePlayerCountHover.remove(line);
        return true;
    }

    public FakePlayers getFakePlayersInstance() {
        return fakePlayersInstance;
    }

    public boolean isFakePlayersAddInList() {
        return fakePlayersAddInList;
    }

    public void setFakePlayersAddInList(boolean fakePlayersAddInList) {
        this.fakePlayersAddInList = fakePlayersAddInList;
    }

    public boolean isFakePlayersRandomOrder() {
        return fakePlayersRandomOrder;
    }

    public void setFakePlayersRandomOrder(boolean fakePlayersRandomOrder) {
        this.fakePlayersRandomOrder = fakePlayersRandomOrder;
    }

    public boolean isNormalPlayerCustomHover() {
        return normalPlayerCustomHover;
    }

    public void setNormalPlayerCustomHover(boolean normalPlayerCustomHover) {
        this.normalPlayerCustomHover = normalPlayerCustomHover;
    }

    public boolean isMaintenancePlayerCustomHover() {
        return maintenancePlayerCustomHover;
    }

    public void setMaintenancePlayerCustomHover(boolean maintenancePlayerCustomHover) {
        this.maintenancePlayerCustomHover = maintenancePlayerCustomHover;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getUntil() {
        return until;
    }

    public void setUntil(long until) {
        this.until = until;
    }

    public boolean isRandomNormalMotd() {
        return randomNormalMotd;
    }

    public void setRandomNormalMotd(boolean randomNormalMotd) {
        this.randomNormalMotd = randomNormalMotd;
    }

    public boolean isRandomNormalPlayerCount() {
        return randomNormalPlayerCount;
    }

    public void setRandomNormalPlayerCount(boolean randomNormalPlayerCount) {
        this.randomNormalPlayerCount = randomNormalPlayerCount;
    }

    public boolean isRandomNormalPlayerCountHover() {
        return randomNormalPlayerCountHover;
    }

    public void setRandomNormalPlayerCountHover(boolean randomNormalPlayerCountHover) {
        this.randomNormalPlayerCountHover = randomNormalPlayerCountHover;
    }

    public boolean isRandomMaintenanceMotd() {
        return randomMaintenanceMotd;
    }

    public void setRandomMaintenanceMotd(boolean randomMaintenanceMotd) {
        this.randomMaintenanceMotd = randomMaintenanceMotd;
    }

    public boolean isRandomMaintenancePlayerCount() {
        return randomMaintenancePlayerCount;
    }

    public void setRandomMaintenancePlayerCount(boolean randomMaintenancePlayerCount) {
        this.randomMaintenancePlayerCount = randomMaintenancePlayerCount;
    }

    public boolean isRandomMaintenancePlayerCountHover() {
        return randomMaintenancePlayerCountHover;
    }

    public void setRandomMaintenancePlayerCountHover(boolean randomMaintenancePlayerCountHover) {
        this.randomMaintenancePlayerCountHover = randomMaintenancePlayerCountHover;
    }

    public List<MOTD> getNormalMotds() {
        return normalMotds;
    }

    public List<MOTD> getMaintenanceMotds() {
        return maintenanceMotds;
    }

    public List<String> getNormalPlayerCounts() {
        return normalPlayerCounts;
    }

    public List<String> getMaintenancePlayerCounts() {
        return maintenancePlayerCounts;
    }

    public List<List<String>> getNormalPlayerCountHovers() {
        return normalPlayerCountHovers;
    }

    public List<List<String>> getMaintenancePlayerCountHovers() {
        return maintenancePlayerCountHovers;
    }

    public boolean isCustomIconEnabled() {
        return customIconEnabled;
    }

    public void setCustomIconEnabled(boolean customIconEnabled) {
        this.customIconEnabled = customIconEnabled;
    }

    public boolean isCustomIconRandomOrder() {
        return customIconRandomOrder;
    }

    public void setCustomIconRandomOrder(boolean customIconRandomOrder) {
        this.customIconRandomOrder = customIconRandomOrder;
    }

    public Favicon getDefaultIcon() {
        return customIcons.get(defaultIconName);
    }

    public HashMap<String, Favicon> getCustomIcons() {
        return customIcons;
    }

    public boolean isCustomMaintenanceIconEnabled() {
        return customMaintenanceIconEnabled;
    }

    public void setCustomMaintenanceIconEnabled(boolean customMaintenanceIconEnabled) {
        this.customMaintenanceIconEnabled = customMaintenanceIconEnabled;
    }

    public boolean isCustomMaintenanceIconRandomOrder() {
        return customMaintenanceIconRandomOrder;
    }

    public void setCustomMaintenanceIconRandomOrder(boolean customMaintenanceIconRandomOrder) {
        this.customMaintenanceIconRandomOrder = customMaintenanceIconRandomOrder;
    }

    public Favicon getDefaultMaintenanceIcon() {
        return customMaintenanceIcons.get(defaultMaintenanceIconName);
    }

    public HashMap<String, Favicon> getCustomMaintenanceIcons() {
        return customMaintenanceIcons;
    }

    public LegacyComponentSerializer getLegacyComponentSerializer() {
        return legacyComponentSerializer;
    }

    public boolean isHigherVersion() {
        return higherVersion;
    }
}
