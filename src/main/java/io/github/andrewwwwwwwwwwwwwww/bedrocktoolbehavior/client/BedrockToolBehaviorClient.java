package io.github.andrewwwwwwwwwwwwwww.bedrocktoolbehavior.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client entrypoint. Registers the toggle key (Options - Controls - "BedrockToolBehavior") and
 * drives {@link UseRateTracker}'s tick clock. Default key is {@code '}, which the vanilla
 * keybinds and the other mods here leave free.
 */
public class BedrockToolBehaviorClient implements ClientModInitializer {

    public static final String MOD_ID = "bedrocktoolbehavior";
    public static final Logger LOGGER = LoggerFactory.getLogger("BedrockToolBehavior");

    private KeyMapping toggle;

    @Override
    public void onInitializeClient() {
        ToolBehaviorConfig.load(FabricLoader.getInstance().getConfigDir());

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "keys"));
        toggle = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.bedrocktoolbehavior.toggle", GLFW.GLFW_KEY_APOSTROPHE, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ToolBehaviorConfig cfg = ToolBehaviorConfig.get();
            while (toggle.consumeClick()) {
                cfg.enabled = !cfg.enabled;
                cfg.save();
                announce(client, cfg.enabled);
            }
            UseRateTracker.onClientTick(client);
        });

        ToolBehaviorConfig cfg = ToolBehaviorConfig.get();
        LOGGER.info("BedrockToolBehavior ready - enabled={}, hoe={}, shovel={}, axe={}, pollDelay={}, sameBlockCooldown={}",
                cfg.enabled, cfg.hoe, cfg.shovel, cfg.axe, cfg.pollDelay, cfg.sameBlockCooldown);
    }

    /** Action-bar toggle feedback: "BedrockToolBehavior: ON". */
    private static void announce(Minecraft client, boolean enabled) {
        if (client.player == null) return;
        client.player.sendOverlayMessage(
                Component.translatable("bedrocktoolbehavior.msg.toggle").withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                        .append(Component.translatable(enabled
                                        ? "bedrocktoolbehavior.msg.on"
                                        : "bedrocktoolbehavior.msg.off")
                                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)));
    }
}
