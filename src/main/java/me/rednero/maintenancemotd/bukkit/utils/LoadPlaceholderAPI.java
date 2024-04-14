package me.rednero.maintenancemotd.bukkit.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public interface LoadPlaceholderAPI {
    static LoadPlaceholderAPI loadPlaceholderAPI() {
        return PlaceholderAPIImpl.INSTANCE;
    }

    String replacePlaceholders(String string, Player player);

    final class PlaceholderAPIImpl implements LoadPlaceholderAPI {
        private static final PlaceholderAPIImpl INSTANCE = new PlaceholderAPIImpl();

        private PlaceholderAPIImpl() {
        }

        @Override
        public String replacePlaceholders(final String string, Player player) {
            return PlaceholderAPI.setPlaceholders(player, string);
        }
    }
}
