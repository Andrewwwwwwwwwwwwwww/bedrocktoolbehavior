package io.github.andrewwwwwwwwwwwwwww.bedrocktoolbehavior.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Settings, stored in {@code config/bedrocktoolbehavior.json} so they survive restarts.
 *
 * <p>The config directory is handed in by the entrypoint rather than looked up here, which is what
 * keeps this class byte-identical between the Fabric and NeoForge builds.
 *
 * <p>The toggle key covers {@link #enabled}; everything else is deliberately file-only. Per-tool
 * switches and the two delays are set-once tuning, not things worth spending keybinds on.
 */
public class ToolBehaviorConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path path;
    private static ToolBehaviorConfig instance = new ToolBehaviorConfig();

    /** Master switch, bound to the toggle key. Off means vanilla timing everywhere. */
    public boolean enabled = true;

    /** Which tools get the faster repeat. Anything else keeps vanilla timing. */
    public boolean hoe = true;
    public boolean shovel = true;
    public boolean axe = true;

    /**
     * Ticks to wait when the crosshair has moved onto a block we did not just use. 0-4.
     * This is the setting that makes a sweep till every block instead of every fourth one.
     */
    public int newBlockDelay = 1;

    /**
     * Ticks to wait when the crosshair is still on a block we just used. 0-4, and 4 is vanilla.
     * Leaving this at vanilla is what stops the mod re-firing at a block whose server-side change
     * has not come back yet, which would otherwise stutter the tool sound.
     */
    public int sameBlockDelay = 4;

    /**
     * How long a used position stays remembered, in ticks. Needs to comfortably outlast the round
     * trip to the server, or sweeping back onto a block re-uses it before its update arrives.
     */
    public int recentBlockTicks = 10;

    public static ToolBehaviorConfig get() {
        return instance;
    }

    public static void load(Path configDir) {
        path = configDir.resolve("bedrocktoolbehavior.json");
        try {
            if (Files.exists(path)) {
                ToolBehaviorConfig parsed = GSON.fromJson(Files.readString(path), ToolBehaviorConfig.class);
                if (parsed != null) instance = parsed;
            }
        } catch (Exception ignored) {
        }
    }

    public void save() {
        if (path == null) return;
        try {
            Files.writeString(path, GSON.toJson(this));
        } catch (Exception ignored) {
        }
    }
}
