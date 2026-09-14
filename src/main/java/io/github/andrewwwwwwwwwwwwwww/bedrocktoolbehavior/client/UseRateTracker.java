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
 * Paces held right-click so that sweeping a tool across ground works every block it passes.
 *
 * <p>Vanilla waits a flat four ticks between uses, set before it knows what is under the crosshair
 * or whether the use will do anything, so holding right-click is capped at five uses a second and
 * a sweep skips ground.
 *
 * <p>Two separate things decide the feel, and they must not share a number:
 *
 * <ul>
 *   <li><b>How often the client looks.</b> This is the cooldown vanilla hard-codes, and while it is
 *       running the client is blind — it cannot notice the crosshair moving onto new ground. So
 *       while an enabled tool is held this drops to {@code pollDelay}, checking every tick.
 *   <li><b>Whether the look turns into a use.</b> Rate limiting belongs here instead, per block:
 *       a position that was just used is refused until {@code sameBlockCooldown} ticks have passed,
 *       which is enforced by cancelling the interaction rather than by sleeping.
 * </ul>
 *
 * <p>Putting the rate limit in the cooldown instead — the obvious first cut — is what makes a sweep
 * still skip blocks. The tick after a successful use the crosshair is usually still on the same
 * block, so a same-block cooldown of four puts the client to sleep for four ticks, and whatever
 * ground it crosses while asleep is never touched.
 *
 * <p>The per-block cooldown also absorbs the multiplayer round trip. The client predicts the use
 * locally but the block does not change until the server's update lands, so without it a position
 * keeps looking untouched, and therefore reusable, long enough to fire several times over.
 *
 * <p>Nothing here tries to predict whether a use will succeed. Vanilla's own eligibility checks sit
 * behind private methods that play sounds as a side effect (the axe's strip / scrape / unwax chain
 * in particular), so they cannot be dry-run, and copying them client-side would rot.
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

    /** Advances the clock and forgets positions past their cooldown. Once per client tick. */
    public static void onClientTick(Minecraft client) {
        tick++;

        if (client.level == null) {
            // Left the world: positions from the old one must not match in the next one.
            recent.clear();
            return;
        }
        if (recent.isEmpty()) return;

        long cooldown = cooldownTicks();
        Iterator<Map.Entry<BlockPos, Long>> it = recent.entrySet().iterator();
        while (it.hasNext()) {
            if (tick - it.next().getValue() > cooldown) it.remove();
        }
    }

    /**
     * Stands in for the hard-coded 4 in {@code Minecraft.startUseItem}.
     *
     * <p>Deliberately unconditional on what the crosshair is pointing at beyond it being a block:
     * this only decides how often the client gets to look, and looking is what lets it notice new
     * ground. Whether a look becomes a use is {@link #shouldSuppress} 's job.
     *
     * @param vanillaDelay the constant being replaced, returned whenever this should not interfere
     */
    public static int delayFor(Minecraft client, int vanillaDelay) {
        ToolBehaviorConfig cfg = ToolBehaviorConfig.get();
        if (!cfg.enabled) return vanillaDelay;

        LocalPlayer player = client.player;
        if (player == null || !holdingEnabledTool(player, cfg)) return vanillaDelay;

        // Blocks only. Pointing at air falls through to the item-use path, which has nothing to
        // rate limit per block, so leave that at vanilla speed rather than polling it every tick.
        if (!(client.hitResult instanceof BlockHitResult hit)
                || hit.getType() != HitResult.Type.BLOCK) {
            return vanillaDelay;
        }
        return clamp(cfg.pollDelay);
    }

    /**
     * Whether this block interaction should be cancelled outright because the same position was
     * used moments ago. Only ever true while the faster polling is actually in effect, so vanilla
     * timing is never interfered with.
     *
     * <p>Keyed on position alone rather than on the item: once polling is fast, every interaction
     * has to be limited, otherwise an offhand tool would speed up whatever is in the main hand.
     */
    public static boolean shouldSuppress(LocalPlayer player, BlockHitResult hit) {
        ToolBehaviorConfig cfg = ToolBehaviorConfig.get();
        if (!cfg.enabled || !holdingEnabledTool(player, cfg)) return false;

        BlockPos pos = hit.getBlockPos().immutable();
        Long last = recent.get(pos);
        if (last != null && tick - last < cooldownTicks()) return true;

        recent.put(pos, tick);
        return false;
    }

    private static long cooldownTicks() {
        return Math.max(1, ToolBehaviorConfig.get().sameBlockCooldown);
    }

    /**
     * The main hand decides, so an offhand tool can never accelerate whatever is being held in the
     * main hand. An offhand tool still counts when the main hand is empty, which is the only case
     * where vanilla would reach for it anyway.
     */
    private static boolean holdingEnabledTool(LocalPlayer player, ToolBehaviorConfig cfg) {
        ItemStack main = player.getMainHandItem();
        if (isEnabledTool(main, cfg)) return true;
        return main.isEmpty() && isEnabledTool(player.getOffhandItem(), cfg);
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
