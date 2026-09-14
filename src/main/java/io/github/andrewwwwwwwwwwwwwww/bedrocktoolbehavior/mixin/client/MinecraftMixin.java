package io.github.andrewwwwwwwwwwwwwww.bedrocktoolbehavior.mixin.client;

import io.github.andrewwwwwwwwwwwwwww.bedrocktoolbehavior.client.UseRateTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Replaces the hard-coded right-click cooldown in {@code Minecraft.startUseItem}.
 *
 * <p>The whole hold-to-use rate lives in three places. {@code tick} counts {@code rightClickDelay}
 * down by one each tick; {@code handleKeybinds} only calls {@code startUseItem} again once it
 * reaches zero; and {@code startUseItem} sets it back to 4 as its very first statement, before it
 * has checked whether the hands are busy or looked at what is under the crosshair. So the cooldown
 * is paid whether the use did anything or not, and holding the button gives five uses a second
 * flat.
 *
 * <p>That 4 is the only integer constant in the method, so the injection point is unambiguous.
 * Everything about when the faster rate actually applies is decided in {@link UseRateTracker};
 * this class is only the seam.
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @ModifyConstant(method = "startUseItem", constant = @Constant(intValue = 4))
    private int bedrocktoolbehavior$useDelay(int vanillaDelay) {
        return UseRateTracker.delayFor((Minecraft) (Object) this, vanillaDelay);
    }
}
