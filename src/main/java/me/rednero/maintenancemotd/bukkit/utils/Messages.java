package me.rednero.maintenancemotd.bukkit.utils;

import me.rednero.maintenancemotd.bukkit.MaintenanceMOTD;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Messages {
    private final List<String> helpMessage = new ArrayList<>();
    private File messagesFile;
    private YamlConfiguration messagesConfig;
    private Component prefix;
    private String noPermissionMessage;
    private String alreadyEnabledMessage;
    private String alreadyDisabledMessage;
    private String enabledMessage;
    private String enabledMessageReason;
    private String disabledMessage;
    private String maxPlayersUsageMessage;
    private String normalMotdUsageMessage;
    private String maintenanceMotdUsageMessage;
    private String mustBePositiveMessage;
    private String maxPlayersSetMessage;
    private String maxPlayersUsageSetMessage;
    private String maxPlayersEnabledMessage;
    private String maxPlayersDisabledMessage;
    private String fakePlayersUsageMessage;
    private String fakePlayersUsageSetMessage;
    private String fakePlayersEnabledMessage;
    private String fakePlayersDisabledMessage;
    private String fakePlayersSetMessage;
    private String fakePlayersAddInListEnabledMessage;
    private String fakePlayersAddInListDisabledMessage;
    private String fakePlayersRandomOrderEnabledMessage;
    private String fakePlayersRandomOrderDisabledMessage;
    private String fakePlayersUsageSetAddInListMessage;
    private String fakePlayersUsageSetRandomOrderMessage;
    private String normalMotdSetMessage;
    private String maintenanceMotdSetMessage;
    private String invalidLineMessage;
    private String normalPlayerCountUsageMessage;
    private String maintenancePlayerCountUsageMessage;
    private String normalPlayerCountEnabledMessage;
    private String maintenancePlayerCountEnabledMessage;
    private String normalPlayerCountDisabledMessage;
    private String maintenancePlayerCountDisabledMessage;
    private String normalPlayerCountSetMessage;
    private String maintenancePlayerCountSetMessage;
    private String normalPlayerCountHoverSetMessage;
    private String maintenancePlayerCountHoverSetMessage;
    private String atLeastOneMessage;
    private String normalPlayerCountUsageSetMessage;
    private String maintenancePlayerCountUsageSetMessage;
    private String normalPlayerCountUsageSetHoverMessage;
    private String normalPlayerCountUsageHoverMessage;
    private String maintenancePlayerCountUsageHoverMessage;
    private String normalPlayerCountHoverEnabledMessage;
    private String maintenancePlayerCountHoverEnabledMessage;
    private String normalPlayerCountHoverDisabledMessage;
    private String maintenancePlayerCountHoverDisabledMessage;
    private String maintenancePlayerCountUsageSetHoverMessage;
    private String normalPlayerCountUsageRemoveHoverMessage;
    private String maintenancePlayerCountUsageRemoveHoverMessage;
    private String normalPlayerCountHoverRemoveMessage;
    private String maintenancePlayerCountHoverRemoveMessage;
    private String normalMotdSetCleanMessage;
    private String maintenanceMotdSetCleanMessage;
    private String normalPlayerCountRemoveMessage;
    private String maintenancePlayerCountRemoveMessage;
    private String thisLineDoesNotExistMessage;
    private String reloadedMessage;
    private String kickMessage;
    private String kickMessageReason;
    private String kickMessageUntil;
    private String enabledMessageUntil;
    private String enabledMessageReasonUntil;
    private String kickMessageReasonUntil;
    private String untilUsageMessage;
    private String days;
    private String hours;
    private String minutes;
    private String seconds;
    private String daysShort;
    private String hoursShort;
    private String minutesShort;
    private String secondsShort;
    private MiniMessage minimessage;
    private String normalRandomUsageMessage;
    private String maintenanceRandomUsageMessage;
    private String normalRandomMotdUsageMessage;
    private String maintenanceRandomMotdUsageMessage;
    private String normalRandomMotdEnabledMessage;
    private String maintenanceRandomMotdEnabledMessage;
    private String normalRandomMotdDisabledMessage;
    private String maintenanceRandomMotdDisabledMessage;
    private String normalRandomPlayerCountUsageMessage;
    private String maintenanceRandomPlayerCountUsageMessage;
    private String normalRandomPlayerCountEnabledMessage;
    private String maintenanceRandomPlayerCountEnabledMessage;
    private String normalRandomPlayerCountDisabledMessage;
    private String maintenanceRandomPlayerCountDisabledMessage;
    private String normalRandomMotdUsageHoverMessage;
    private String maintenanceRandomMotdUsageHoverMessage;
    private String normalRandomMotdHoverEnabledMessage;
    private String maintenanceRandomMotdHoverEnabledMessage;
    private String normalRandomMotdHoverDisabledMessage;
    private String maintenanceRandomMotdHoverDisabledMessage;
    private String normalIconUsageMessage;
    private String maintenanceIconUsageMessage;
    private String normalIconEnabledMessage;
    private String maintenanceIconEnabledMessage;
    private String normalIconDisabledMessage;
    private String maintenanceIconDisabledMessage;
    private String normalIconUsageRandomMessage;
    private String maintenanceIconUsageRandomMessage;
    private String normalIconRandomEnabledMessage;
    private String maintenanceIconRandomEnabledMessage;
    private String normalIconRandomDisabledMessage;
    private String maintenanceIconRandomDisabledMessage;
    private String defaultNoReasonMessage;
    private String defaultNoUntilMessage;
    private String defaultNoRemainMessage;
    private String dateFormat;
    private String timeFormat;

    private final LegacyComponentSerializer legacyComponentSerializer;

    public Messages() {
        legacyComponentSerializer = MaintenanceMOTD.getInstance().getLegacyComponentSerializer();
        loadMessages();
    }

    private void loadMessages() {
        // load messages from messages.yml
        messagesFile = new File(MaintenanceMOTD.getInstance().getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            MaintenanceMOTD.getInstance().saveResource("messages.yml", false);
        }
        messagesConfig = new YamlConfiguration();
        try {
            messagesConfig.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            MaintenanceMOTD.getInstance().getLogger().warning("§c[§4MaintenanceMOTD§c] §cAn error occurred while trying to load messages.yml");
            // e.printStackTrace();
        }
        minimessage = MaintenanceMOTD.getInstance().getMinimessage();

        List<String> readHelpMessage = messagesConfig.getStringList("help");
        for (String line : readHelpMessage) {
            helpMessage.add(legacyComponentSerializer.serialize(minimessage.deserialize(line)));
        }
        String readPrefix = messagesConfig.getString("prefix");
        prefix = minimessage.deserialize(readPrefix);
        noPermissionMessage = generateMessage(messagesConfig.getString("no-permission"));
        alreadyEnabledMessage = generateMessage(messagesConfig.getString("already-enabled"));
        alreadyDisabledMessage = generateMessage(messagesConfig.getString("already-disabled"));
        enabledMessage = generateMessage(messagesConfig.getString("enabled"));
        enabledMessageReason = readPrefix + messagesConfig.getString("enabled-with-reason");
        disabledMessage = generateMessage(messagesConfig.getString("disabled"));
        maxPlayersUsageMessage = generateMessage(messagesConfig.getString("max-players.usage.general"));
        maxPlayersUsageSetMessage = generateMessage(messagesConfig.getString("max-players.usage.set"));
        maxPlayersEnabledMessage = generateMessage(messagesConfig.getString("max-players.enabled"));
        maxPlayersDisabledMessage = generateMessage(messagesConfig.getString("max-players.disabled"));
        fakePlayersUsageMessage = generateMessage(messagesConfig.getString("fake-players.usage.general"));
        fakePlayersUsageSetMessage = generateMessage(messagesConfig.getString("fake-players.usage.set"));
        fakePlayersEnabledMessage = generateMessage(messagesConfig.getString("fake-players.enabled"));
        fakePlayersDisabledMessage = generateMessage(messagesConfig.getString("fake-players.disabled"));
        fakePlayersAddInListEnabledMessage = generateMessage(messagesConfig.getString("fake-players.add-in-list.enabled"));
        fakePlayersAddInListDisabledMessage = generateMessage(messagesConfig.getString("fake-players.add-in-list.disabled"));
        fakePlayersRandomOrderEnabledMessage = generateMessage(messagesConfig.getString("fake-players.random-order.enabled"));
        fakePlayersRandomOrderDisabledMessage = generateMessage(messagesConfig.getString("fake-players.random-order.disabled"));
        fakePlayersUsageSetAddInListMessage = generateMessage(messagesConfig.getString("fake-players.usage.add-in-list"));
        fakePlayersUsageSetRandomOrderMessage = generateMessage(messagesConfig.getString("fake-players.usage.random-order"));
        normalMotdUsageMessage = generateMessage(messagesConfig.getString("normal-motd.usage"));
        maintenanceMotdUsageMessage = generateMessage(messagesConfig.getString("maintenance-motd.usage"));
        mustBePositiveMessage = generateMessage(messagesConfig.getString("must-be-positive"));
        maxPlayersSetMessage = readPrefix + messagesConfig.getString("max-players.set");
        fakePlayersSetMessage = readPrefix + messagesConfig.getString("fake-players.set");
        normalMotdSetMessage = readPrefix + messagesConfig.getString("normal-motd.set");
        maintenanceMotdSetMessage = readPrefix + messagesConfig.getString("maintenance-motd.set");
        invalidLineMessage = generateMessage(messagesConfig.getString("invalid-line"));
        normalPlayerCountUsageMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.usage.general"));
        maintenancePlayerCountUsageMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.usage.general"));
        normalPlayerCountEnabledMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.enabled"));
        maintenancePlayerCountEnabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.enabled"));
        normalPlayerCountDisabledMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.disabled"));
        maintenancePlayerCountDisabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.disabled"));
        normalPlayerCountSetMessage = readPrefix + messagesConfig.getString("normal-motd.player-count.set");
        maintenancePlayerCountSetMessage = readPrefix + messagesConfig.getString("maintenance-motd.player-count.set");
        normalPlayerCountHoverSetMessage = readPrefix + messagesConfig.getString("normal-motd.player-count.hover.set");
        maintenancePlayerCountHoverSetMessage = readPrefix + messagesConfig.getString("maintenance-motd.player-count.hover.set");
        normalPlayerCountHoverEnabledMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.hover.enabled"));
        maintenancePlayerCountHoverEnabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.hover.enabled"));
        normalPlayerCountHoverDisabledMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.hover.disabled"));
        maintenancePlayerCountHoverDisabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.hover.disabled"));
        atLeastOneMessage = generateMessage(messagesConfig.getString("at-least-one"));
        normalPlayerCountUsageSetMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.usage.set"));
        maintenancePlayerCountUsageSetMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.usage.set"));
        normalPlayerCountUsageSetHoverMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.usage.set-hover"));
        maintenancePlayerCountUsageSetHoverMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.usage.set-hover"));
        normalPlayerCountUsageHoverMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.usage.hover"));
        maintenancePlayerCountUsageHoverMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.usage.hover"));
        normalPlayerCountUsageRemoveHoverMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.usage.remove-hover"));
        maintenancePlayerCountUsageRemoveHoverMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.usage.remove-hover"));
        normalPlayerCountHoverRemoveMessage = readPrefix + messagesConfig.getString("normal-motd.player-count.hover.remove");
        maintenancePlayerCountHoverRemoveMessage = readPrefix + messagesConfig.getString("maintenance-motd.player-count.hover.remove");
        normalMotdSetCleanMessage = readPrefix + messagesConfig.getString("normal-motd.set-clean");
        maintenanceMotdSetCleanMessage = readPrefix + messagesConfig.getString("maintenance-motd.set-clean");
        normalPlayerCountRemoveMessage = generateMessage(messagesConfig.getString("normal-motd.player-count.remove"));
        maintenancePlayerCountRemoveMessage = generateMessage(messagesConfig.getString("maintenance-motd.player-count.remove"));
        thisLineDoesNotExistMessage = generateMessage(messagesConfig.getString("this-line-does-not-exist"));

        reloadedMessage = generateMessage(messagesConfig.getString("reloaded"));
        kickMessage = legacyComponentSerializer.serialize(minimessage.deserialize(messagesConfig.getString("kick.message")));
        kickMessageReason = messagesConfig.getString("kick.message-with-reason");
        kickMessageUntil = messagesConfig.getString("kick.message-with-until");
        enabledMessageUntil = readPrefix + messagesConfig.getString("until");
        enabledMessageReasonUntil = readPrefix + messagesConfig.getString("until-with-reason");
        kickMessageReasonUntil = messagesConfig.getString("kick.message-with-until-and-reason");
        untilUsageMessage = generateMessage(messagesConfig.getString("until-usage"));

        days = messagesConfig.getString("days");
        hours = messagesConfig.getString("hours");
        minutes = messagesConfig.getString("minutes");
        seconds = messagesConfig.getString("seconds");
        daysShort = messagesConfig.getString("days-short");
        hoursShort = messagesConfig.getString("hours-short");
        minutesShort = messagesConfig.getString("minutes-short");
        secondsShort = messagesConfig.getString("seconds-short");

        normalRandomUsageMessage = generateMessage(messagesConfig.getString("normal-motd.random.usage.general"));
        maintenanceRandomUsageMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.usage.general"));
        normalRandomMotdUsageMessage = generateMessage(messagesConfig.getString("normal-motd.random.usage.motd"));
        maintenanceRandomMotdUsageMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.usage.motd"));
        normalRandomMotdEnabledMessage = generateMessage(messagesConfig.getString("normal-motd.random.motd.enabled"));
        maintenanceRandomMotdEnabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.motd.enabled"));
        normalRandomMotdDisabledMessage = generateMessage(messagesConfig.getString("normal-motd.random.motd.disabled"));
        maintenanceRandomMotdDisabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.motd.disabled"));
        normalRandomPlayerCountUsageMessage = generateMessage(messagesConfig.getString("normal-motd.random.usage.player-count"));
        maintenanceRandomPlayerCountUsageMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.usage.player-count"));
        normalRandomPlayerCountEnabledMessage = generateMessage(messagesConfig.getString("normal-motd.random.player-count.enabled"));
        maintenanceRandomPlayerCountEnabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.player-count.enabled"));
        normalRandomPlayerCountDisabledMessage = generateMessage(messagesConfig.getString("normal-motd.random.player-count.disabled"));
        maintenanceRandomPlayerCountDisabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.player-count.disabled"));
        normalRandomMotdUsageHoverMessage = generateMessage(messagesConfig.getString("normal-motd.random.usage.hover"));
        maintenanceRandomMotdUsageHoverMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.usage.hover"));
        normalRandomMotdHoverEnabledMessage = generateMessage(messagesConfig.getString("normal-motd.random.hover.enabled"));
        maintenanceRandomMotdHoverEnabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.hover.enabled"));
        normalRandomMotdHoverDisabledMessage = generateMessage(messagesConfig.getString("normal-motd.random.hover.disabled"));
        maintenanceRandomMotdHoverDisabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.random.hover.disabled"));

        normalIconUsageMessage = generateMessage(messagesConfig.getString("normal-motd.icon.usage.general"));
        maintenanceIconUsageMessage = generateMessage(messagesConfig.getString("maintenance-motd.icon.usage.general"));
        normalIconEnabledMessage = generateMessage(messagesConfig.getString("normal-motd.icon.enabled"));
        maintenanceIconEnabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.icon.enabled"));
        normalIconDisabledMessage = generateMessage(messagesConfig.getString("normal-motd.icon.disabled"));
        maintenanceIconDisabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.icon.disabled"));
        normalIconUsageRandomMessage = generateMessage(messagesConfig.getString("normal-motd.icon.usage.random"));
        maintenanceIconUsageRandomMessage = generateMessage(messagesConfig.getString("maintenance-motd.icon.usage.random"));
        normalIconRandomEnabledMessage = generateMessage(messagesConfig.getString("normal-motd.icon.random.enabled"));
        maintenanceIconRandomEnabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.icon.random.enabled"));
        normalIconRandomDisabledMessage = generateMessage(messagesConfig.getString("normal-motd.icon.random.disabled"));
        maintenanceIconRandomDisabledMessage = generateMessage(messagesConfig.getString("maintenance-motd.icon.random.disabled"));

        defaultNoReasonMessage = messagesConfig.getString("default-no-reason");
        defaultNoUntilMessage = messagesConfig.getString("default-no-until");
        defaultNoRemainMessage = messagesConfig.getString("default-no-remain");

        dateFormat = messagesConfig.getString("format.date");
        timeFormat = messagesConfig.getString("format.time");
    }

    public List<String> getHelpMessage() {
        return helpMessage;
    }

    public String getNoPermissionMessage() {
        return noPermissionMessage;
    }

    public String getAlreadyEnabledMessage() {
        return alreadyEnabledMessage;
    }

    public String getAlreadyDisabledMessage() {
        return alreadyDisabledMessage;
    }

    public String getEnabledMessage() {
        return enabledMessage;
    }

    public String getDisabledMessage() {
        return disabledMessage;
    }

    public String getMaxPlayersUsageMessage() {
        return maxPlayersUsageMessage;
    }

    public String getMaxPlayersUsageSetMessage() {
        return maxPlayersUsageSetMessage;
    }

    public String getMaxPlayersEnabledMessage() {
        return maxPlayersEnabledMessage;
    }

    public String getMaxPlayersDisabledMessage() {
        return maxPlayersDisabledMessage;
    }

    public String getFakePlayersUsageMessage() {
        return fakePlayersUsageMessage;
    }

    public String getFakePlayersUsageSetMessage() {
        return fakePlayersUsageSetMessage;
    }

    public String getFakePlayersEnabledMessage() {
        return fakePlayersEnabledMessage;
    }

    public String getFakePlayersDisabledMessage() {
        return fakePlayersDisabledMessage;
    }

    public String getFakePlayersSetMessage() {
        return fakePlayersSetMessage;
    }

    public String getFakePlayersAddInListEnabledMessage() {
        return fakePlayersAddInListEnabledMessage;
    }

    public String getFakePlayersAddInListDisabledMessage() {
        return fakePlayersAddInListDisabledMessage;
    }

    public String getFakePlayersRandomOrderEnabledMessage() {
        return fakePlayersRandomOrderEnabledMessage;
    }

    public String getFakePlayersRandomOrderDisabledMessage() {
        return fakePlayersRandomOrderDisabledMessage;
    }

    public String getFakePlayersUsageSetAddInListMessage() {
        return fakePlayersUsageSetAddInListMessage;
    }

    public String getFakePlayersUsageSetRandomOrderMessage() {
        return fakePlayersUsageSetRandomOrderMessage;
    }

    public String getNormalMotdUsageMessage() {
        return normalMotdUsageMessage;
    }

    public String getMaintenanceMotdUsageMessage() {
        return maintenanceMotdUsageMessage;
    }

    public String getMustBePositiveMessage() {
        return mustBePositiveMessage;
    }

    public String getMaxPlayersSetMessage() {
        return maxPlayersSetMessage;
    }

    public String getNormalMotdSetMessage() {
        return normalMotdSetMessage;
    }

    public String getMaintenanceMotdSetMessage() {
        return maintenanceMotdSetMessage;
    }

    public String getInvalidLineMessage() {
        return invalidLineMessage;
    }

    public String getReloadedMessage() {
        return reloadedMessage;
    }

    public String getNormalPlayerCountUsageMessage() {
        return normalPlayerCountUsageMessage;
    }

    public String getMaintenancePlayerCountUsageMessage() {
        return maintenancePlayerCountUsageMessage;
    }

    public String getNormalPlayerCountEnabledMessage() {
        return normalPlayerCountEnabledMessage;
    }

    public String getMaintenancePlayerCountEnabledMessage() {
        return maintenancePlayerCountEnabledMessage;
    }

    public String getNormalPlayerCountDisabledMessage() {
        return normalPlayerCountDisabledMessage;
    }

    public String getMaintenancePlayerCountDisabledMessage() {
        return maintenancePlayerCountDisabledMessage;
    }

    public String getNormalPlayerCountSetMessage() {
        return normalPlayerCountSetMessage;
    }

    public String getMaintenancePlayerCountSetMessage() {
        return maintenancePlayerCountSetMessage;
    }

    public String getNormalPlayerCountHoverSetMessage() {
        return normalPlayerCountHoverSetMessage;
    }

    public String getMaintenancePlayerCountHoverSetMessage() {
        return maintenancePlayerCountHoverSetMessage;
    }

    public String getAtLeastOneMessage() {
        return atLeastOneMessage;
    }

    public String getNormalPlayerCountUsageSetMessage() {
        return normalPlayerCountUsageSetMessage;
    }

    public String getMaintenancePlayerCountUsageSetMessage() {
        return maintenancePlayerCountUsageSetMessage;
    }

    public String getNormalPlayerCountUsageSetHoverMessage() {
        return normalPlayerCountUsageSetHoverMessage;
    }

    public String getMaintenancePlayerCountUsageSetHoverMessage() {
        return maintenancePlayerCountUsageSetHoverMessage;
    }

    public String getNormalPlayerCountUsageRemoveHoverMessage() {
        return normalPlayerCountUsageRemoveHoverMessage;
    }

    public String getMaintenancePlayerCountUsageRemoveHoverMessage() {
        return maintenancePlayerCountUsageRemoveHoverMessage;
    }

    public String getNormalPlayerCountHoverRemoveMessage() {
        return normalPlayerCountHoverRemoveMessage;
    }

    public String getMaintenancePlayerCountHoverRemoveMessage() {
        return maintenancePlayerCountHoverRemoveMessage;
    }

    public String getNormalMotdSetCleanMessage() {
        return normalMotdSetCleanMessage;
    }

    public String getMaintenanceMotdSetCleanMessage() {
        return maintenanceMotdSetCleanMessage;
    }

    public String getNormalPlayerCountRemoveMessage() {
        return normalPlayerCountRemoveMessage;
    }

    public String getMaintenancePlayerCountRemoveMessage() {
        return maintenancePlayerCountRemoveMessage;
    }

    public String getThisLineDoesNotExistMessage() {
        return thisLineDoesNotExistMessage;
    }

    public String getNormalPlayerCountUsageHoverMessage() {
        return normalPlayerCountUsageHoverMessage;
    }

    public String getMaintenancePlayerCountUsageHoverMessage() {
        return maintenancePlayerCountUsageHoverMessage;
    }

    public String getNormalPlayerCountHoverEnabledMessage() {
        return normalPlayerCountHoverEnabledMessage;
    }

    public String getMaintenancePlayerCountHoverEnabledMessage() {
        return maintenancePlayerCountHoverEnabledMessage;
    }

    public String getNormalPlayerCountHoverDisabledMessage() {
        return normalPlayerCountHoverDisabledMessage;
    }

    public String getMaintenancePlayerCountHoverDisabledMessage() {
        return maintenancePlayerCountHoverDisabledMessage;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public String getKickMessageReason() {
        return kickMessageReason;
    }

    public String getEnabledMessageReason() {
        return enabledMessageReason;
    }

    public String getKickMessageUntil() {
        return kickMessageUntil;
    }

    public String getEnabledMessageUntil() {
        return enabledMessageUntil;
    }

    public String getEnabledMessageReasonUntil() {
        return enabledMessageReasonUntil;
    }

    public String getKickMessageReasonUntil() {
        return kickMessageReasonUntil;
    }

    public String getUntilUsageMessage() {
        return untilUsageMessage;
    }

    public String getDays() {
        return days;
    }

    public String getHours() {
        return hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public String getSeconds() {
        return seconds;
    }

    public Component getPrefix() {
        return prefix;
    }

    public String generateMessage(String message) {
        return legacyComponentSerializer.serialize(prefix.append(minimessage.deserialize(message)));
    }

    public Component generateMessageComponent(String message) {
        return prefix.append(minimessage.deserialize(message));
    }

    public String getNormalRandomUsageMessage() {
        return normalRandomUsageMessage;
    }

    public String getMaintenanceRandomUsageMessage() {
        return maintenanceRandomUsageMessage;
    }

    public String getNormalRandomMotdUsageMessage() {
        return normalRandomMotdUsageMessage;
    }

    public String getMaintenanceRandomMotdUsageMessage() {
        return maintenanceRandomMotdUsageMessage;
    }

    public String getNormalRandomMotdEnabledMessage() {
        return normalRandomMotdEnabledMessage;
    }

    public String getMaintenanceRandomMotdEnabledMessage() {
        return maintenanceRandomMotdEnabledMessage;
    }

    public String getNormalRandomMotdDisabledMessage() {
        return normalRandomMotdDisabledMessage;
    }

    public String getMaintenanceRandomMotdDisabledMessage() {
        return maintenanceRandomMotdDisabledMessage;
    }

    public String getNormalRandomPlayerCountUsageMessage() {
        return normalRandomPlayerCountUsageMessage;
    }

    public String getMaintenanceRandomPlayerCountUsageMessage() {
        return maintenanceRandomPlayerCountUsageMessage;
    }

    public String getNormalRandomPlayerCountEnabledMessage() {
        return normalRandomPlayerCountEnabledMessage;
    }

    public String getMaintenanceRandomPlayerCountEnabledMessage() {
        return maintenanceRandomPlayerCountEnabledMessage;
    }

    public String getNormalRandomPlayerCountDisabledMessage() {
        return normalRandomPlayerCountDisabledMessage;
    }

    public String getMaintenanceRandomPlayerCountDisabledMessage() {
        return maintenanceRandomPlayerCountDisabledMessage;
    }

    public String getNormalRandomMotdUsageHoverMessage() {
        return normalRandomMotdUsageHoverMessage;
    }

    public String getMaintenanceRandomMotdUsageHoverMessage() {
        return maintenanceRandomMotdUsageHoverMessage;
    }

    public String getNormalRandomMotdHoverEnabledMessage() {
        return normalRandomMotdHoverEnabledMessage;
    }

    public String getMaintenanceRandomMotdHoverEnabledMessage() {
        return maintenanceRandomMotdHoverEnabledMessage;
    }

    public String getNormalRandomMotdHoverDisabledMessage() {
        return normalRandomMotdHoverDisabledMessage;
    }

    public String getMaintenanceRandomMotdHoverDisabledMessage() {
        return maintenanceRandomMotdHoverDisabledMessage;
    }

    public String getNormalIconUsageMessage() {
        return normalIconUsageMessage;
    }

    public String getMaintenanceIconUsageMessage() {
        return maintenanceIconUsageMessage;
    }

    public String getNormalIconEnabledMessage() {
        return normalIconEnabledMessage;
    }

    public String getMaintenanceIconEnabledMessage() {
        return maintenanceIconEnabledMessage;
    }

    public String getNormalIconDisabledMessage() {
        return normalIconDisabledMessage;
    }

    public String getMaintenanceIconDisabledMessage() {
        return maintenanceIconDisabledMessage;
    }

    public String getNormalIconUsageRandomMessage() {
        return normalIconUsageRandomMessage;
    }

    public String getMaintenanceIconUsageRandomMessage() {
        return maintenanceIconUsageRandomMessage;
    }

    public String getNormalIconRandomEnabledMessage() {
        return normalIconRandomEnabledMessage;
    }

    public String getMaintenanceIconRandomEnabledMessage() {
        return maintenanceIconRandomEnabledMessage;
    }

    public String getNormalIconRandomDisabledMessage() {
        return normalIconRandomDisabledMessage;
    }

    public String getMaintenanceIconRandomDisabledMessage() {
        return maintenanceIconRandomDisabledMessage;
    }

    public String replaceTime(String original, long timeMillis) {
        String remain = "";
        long copy = timeMillis;
        if (copy > 1000 * 60 * 60 * 24) {
            remain = copy / (1000 * 60 * 60 * 24) + " " + days;
            copy %= 1000 * 60 * 60 * 24;
        }
        if (copy > 1000 * 60 * 60) {
            if (timeMillis > 1000 * 60 * 60 * 24) {
                remain += ", ";
            }
            remain += copy / (1000 * 60 * 60) + " " + hours;
            copy %= 1000 * 60 * 60;
        }
        if (copy > 1000 * 60) {
            if (timeMillis % (1000 * 60 * 60 * 24) > 1000 * 60 * 60) {
                remain += ", ";
            }
            remain += copy / (1000 * 60) + " " + minutes;
            copy %= 1000 * 60;
        }
        if (copy > 1000) {
            if (timeMillis % (1000 * 60 * 60) > 1000 * 60) {
                remain += ", ";
            }
            remain += copy / (1000) + " " + seconds;
        }
        return original.replace("%remain%", remain);
    }

    public String replaceTimeShort(String original, long timeMillis) {
        String remain = "";
        long copy = timeMillis;
        if (copy > 1000 * 60 * 60 * 24) {
            remain = copy / (1000 * 60 * 60 * 24) + daysShort;
            copy %= 1000 * 60 * 60 * 24;
        }
        if (copy > 1000 * 60 * 60) {
            if (timeMillis > 1000 * 60 * 60 * 24) {
                remain += ", ";
            }
            remain += copy / (1000 * 60 * 60) + hoursShort;
            copy %= 1000 * 60 * 60;
        }
        if (copy > 1000 * 60) {
            if (timeMillis % (1000 * 60 * 60 * 24) > 1000 * 60 * 60) {
                remain += ", ";
            }
            remain += copy / (1000 * 60) + minutesShort;
            copy %= 1000 * 60;
        }
        if (copy > 1000) {
            if (timeMillis % (1000 * 60 * 60) > 1000 * 60) {
                remain += ", ";
            }
            remain += copy / (1000) + secondsShort;
        }
        return original.replace("%remain%", remain);
    }

    public String replaceDateFormat(String original, long timeMillis) {
        Date date = new Date(timeMillis);

        // format date
        String day = String.valueOf(date.getDate());
        String month = String.valueOf(date.getMonth() + 1);
        String year = String.valueOf(date.getYear() + 1900);

        // format based on format.date (dd/mm/yyyy, mm/dd/yyyy, yyyy/mm/dd, dd/mm/yy, d/m/yy)
        String dateFormat = this.dateFormat;
        dateFormat = dateFormat.replace("dd", day.length() == 1 ? "0" + day : day);
        dateFormat = dateFormat.replace("mm", month.length() == 1 ? "0" + month : month);
        dateFormat = dateFormat.replace("yyyy", year);
        dateFormat = dateFormat.replace("yy", year.substring(2));
        dateFormat = dateFormat.replace("d", day);
        dateFormat = dateFormat.replace("m", month);

        return original.replace("%date%", dateFormat);
    }

    public String replaceTimeFormat(String original, long timeMillis) {

        // format time
        timeMillis = timeMillis / 1000;
        long hours = timeMillis / 3600;
        long minutes = (timeMillis % 3600) / 60;
        long seconds = timeMillis % 60;

        String hour = String.valueOf(hours);
        String minute = String.valueOf(minutes);
        String second = String.valueOf(seconds);


        // format based on format.time (hh:mm:ss, hh:mm, h:m, h:mm:ss, h:mm, hh:m, hh:m:ss)
        String timeFormat = this.timeFormat;
        timeFormat = timeFormat.replace("hh", hour.length() == 1 ? "0" + hour : hour);
        timeFormat = timeFormat.replace("mm", minute.length() == 1 ? "0" + minute : minute);
        timeFormat = timeFormat.replace("ss", second.length() == 1 ? "0" + second : second);
        timeFormat = timeFormat.replace("h", hour);
        timeFormat = timeFormat.replace("m", minute);
        timeFormat = timeFormat.replace("s", second);

        return original.replace("%time%", timeFormat);
    }

    public String replacePlaceholders(String original, int onlineplayers, int maxplayers, String reason, long until) {
        if (reason.isEmpty()) {
            reason = defaultNoReasonMessage;
        }
        if (until == 0) {
            if (MaintenanceMOTD.getInstance().isPlaceholderAPIEnabled()) {
                String replaced = original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", defaultNoUntilMessage).replace("%remain%", defaultNoRemainMessage).replace("%date%", defaultNoUntilMessage).replace("%time%", defaultNoRemainMessage);
                return LoadPlaceholderAPI.loadPlaceholderAPI().replacePlaceholders(replaced, null);
            }
            return original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", defaultNoUntilMessage).replace("%remain%", defaultNoRemainMessage).replace("%date%", defaultNoUntilMessage).replace("%time%", defaultNoRemainMessage);
        }
        if (MaintenanceMOTD.getInstance().isPlaceholderAPIEnabled()) {
            String replaced = replaceDateFormat(replaceTimeFormat(original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", new Date(until).toString()).replace("%remain%", replaceTimeShort("%remain%", until - System.currentTimeMillis())), until - System.currentTimeMillis()), until);
            return LoadPlaceholderAPI.loadPlaceholderAPI().replacePlaceholders(replaced, null);
        }
        return replaceDateFormat(replaceTimeFormat(original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", new Date(until).toString()).replace("%remain%", replaceTimeShort("%remain%", until - System.currentTimeMillis())), until - System.currentTimeMillis()), until);
    }

    public String replacePlaceholdersDownscale(String original, int onlineplayers, int maxplayers, String reason, long until) {
        if (reason.isEmpty()) {
            reason = defaultNoReasonMessage;
        }
        if (until == 0) {
            if (MaintenanceMOTD.getInstance().isPlaceholderAPIEnabled()) {
                String replaced = original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", defaultNoUntilMessage).replace("%remain%", defaultNoRemainMessage).replace("%date%", defaultNoUntilMessage).replace("%time%", defaultNoRemainMessage);
                return legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(LoadPlaceholderAPI.loadPlaceholderAPI().replacePlaceholders(replaced, null))));
            }
            return legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", defaultNoUntilMessage).replace("%remain%", defaultNoRemainMessage).replace("%date%", defaultNoUntilMessage).replace("%time%", defaultNoRemainMessage))));
        }
        if (MaintenanceMOTD.getInstance().isPlaceholderAPIEnabled()) {
            String replaced = replaceDateFormat(replaceTimeFormat(original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", new Date(until).toString()).replace("%remain%", replaceTimeShort("%remain%", until - System.currentTimeMillis())), until - System.currentTimeMillis()), until);
            return legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(LoadPlaceholderAPI.loadPlaceholderAPI().replacePlaceholders(replaced, null))));
        }
        return legacyComponentSerializer.serialize(Downsampler.downsampler().downsample(MaintenanceMOTD.getInstance().getMinimessage().deserialize(replaceDateFormat(replaceTimeFormat(original.replace("%max%", String.valueOf(maxplayers)).replace("%online%", String.valueOf(onlineplayers)).replace("%reason%", reason).replace("%until%", new Date(until).toString()).replace("%remain%", replaceTimeShort("%remain%", until - System.currentTimeMillis())), until - System.currentTimeMillis()), until))));
    }
}
