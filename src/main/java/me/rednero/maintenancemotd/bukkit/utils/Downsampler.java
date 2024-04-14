package me.rednero.maintenancemotd.bukkit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public interface Downsampler {
    static Downsampler downsampler() {
        return ComponentColorDownsamplerImpl.INSTANCE;
    }

    Component downsample(Component component);

    final class ComponentColorDownsamplerImpl implements Downsampler {
        private static final Downsampler INSTANCE = new ComponentColorDownsamplerImpl();

        private ComponentColorDownsamplerImpl() {
        }

        @Override
        public Component downsample(final Component component) {
            return GsonComponentSerializer.gson().deserializeFromTree(
                    GsonComponentSerializer.colorDownsamplingGson().serializeToTree(component)
            );
        }
    }
}