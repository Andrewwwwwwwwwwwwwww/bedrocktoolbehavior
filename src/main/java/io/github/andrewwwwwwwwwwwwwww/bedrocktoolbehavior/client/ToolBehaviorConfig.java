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
     * Ticks between checks while an enabled tool is held, 0-4. At 1 the client looks every tick,
     * which is what lets a sweep catch every block it crosses; raising it reintroduces the blind
     * gaps that make a sweep skip ground, and 4 is identical to vanilla.
     */
    public int pollDelay = 1;

    /**
     * Ticks before the same block may be used again. 4 matches vanilla's spacing, so holding the
     * button on one spot behaves exactly as it always has.
     *
     * <p>This also has to outlast the round trip to the server. The client predicts the use but the
     * block does not change until the server's update arrives, so a value below the ping lets the
     * same block be used twice before it has visibly changed.
     */
    public int sameBlockCooldown = 4;

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
