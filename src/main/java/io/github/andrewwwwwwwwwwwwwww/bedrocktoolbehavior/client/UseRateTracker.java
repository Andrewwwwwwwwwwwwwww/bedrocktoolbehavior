package io.github.andrewwwwwwwwwwwwwww.bedrocktoolbehavior.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Decides how long the client waits before firing the next held right-click.
 *
 * <p>Vanilla waits a flat four ticks, set before it knows what you are pointing at or whether the
 * use will do anything, so holding right-click is capped at five uses a second and a sweep across
 * a field skips blocks. Bedrock feels different because the interaction tracks the crosshair, not
 * a clock.
 *
 * <p>So rather than simply running the clock faster, this keys off movement: a block the crosshair
 * has just arrived at is used immediately, a block it is still sitting on keeps vanilla timing.
 * Each distinct block therefore gets exactly one interaction, never a repeat, which is both what
 * makes the sweep feel right and what keeps the packet rate looking like a player rather than an
 * autoclicker.
 *
 * <p>The remembered-positions map is what makes the second half work in multiplayer. The client
 * predicts the use locally but the block does not change until the server's update lands, so
 * without it a position would keep looking untouched — and therefore re-usable — for a whole round
 * trip, stuttering the tool sound and sending duplicate packets.
 *
 * <p>Eligibility is deliberately just "is this tool enabled", with no attempt to predict whether
 * the use will succeed. Vanilla's own checks live behind private methods that play sounds as a
 * side effect (the axe's strip / scrape / unwax chain in particular), so they cannot be dry-run,
 * and reimplementing them client-side would silently rot the next time they change.
 */
public final class UseRateTracker {

    /** Vanilla's own cooldown, and the slowest value this ever hands back. */
    public static final int VANILLA_DELAY = 4;

    /** Relative tick clock. Minecraft's own counter is private and only block ages matter here. */
    private static long tick;

    /** Positions used recently, mapped to the tick they were last used on. */
    private static final Map<BlockPos, Long> recent = new HashMap<>();

    private UseRateTracker() {
    }

    /** Advances the clock and forgets positions past their TTL. Called once per client tick. */
    public static void onClientTick(Minecraft client) {
        tick++;

        if (client.level == null) {
            // Left the world: positions from the old one must not match in the next one.
            recent.clear();
            return;
        }
        if (recent.isEmpty()) return;

        long ttl = Math.max(1, ToolBehaviorConfig.get().recentBlockTicks);
        Iterator<Map.Entry<BlockPos, Long>> it = recent.entrySet().iterator();
        while (it.hasNext()) {
            if (tick - it.next().getValue() > ttl) it.remove();
        }
    }

    /**
     * Stands in for the hard-coded 4 in {@code Minecraft.startUseItem}.
     *
     * @param vanillaDelay the constant being replaced, returned whenever this should not interfere
     * @return the number of ticks to wait before the next held right-click fires
     */
    public static int delayFor(Minecraft client, int vanillaDelay) {
        ToolBehaviorConfig cfg = ToolBehaviorConfig.get();
        if (!cfg.enabled) return vanillaDelay;

        LocalPlayer player = client.player;
        if (player == null) return vanillaDelay;

        // Blocks only. Entity interactions and empty air keep vanilla timing. The type check is
        // not redundant: a miss is also reported as a BlockHitResult.
        if (!(client.hitResult instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return vanillaDelay;
        }
        if (!holdingEnabledTool(player, cfg)) return vanillaDelay;

        BlockPos pos = hit.getBlockPos().immutable();

        // Refresh on every look, so a block being stared at never ages out and starts over.
        Long previous = recent.put(pos, tick);
        return clamp(previous == null ? cfg.newBlockDelay : cfg.sameBlockDelay);
    }

    /** Either hand counts: startUseItem tries main hand then offhand. */
    private static boolean holdingEnabledTool(LocalPlayer player, ToolBehaviorConfig cfg) {
        return isEnabledTool(player.getMainHandItem(), cfg)
                || isEnabledTool(player.getOffhandItem(), cfg);
    }

    private static boolean isEnabledTool(ItemStack stack, ToolBehaviorConfig cfg) {
        if (stack.isEmpty()) return false;
        return (cfg.hoe && stack.is(ItemTags.HOES))
                || (cfg.shovel && stack.is(ItemTags.SHOVELS))
                || (cfg.axe && stack.is(ItemTags.AXES));
    }

    /** Never slower than vanilla, never negative, whatever the config file says. */
    private static int clamp(int delay) {
        if (delay < 0) return 0;
        return Math.min(delay, VANILLA_DELAY);
    }
}
