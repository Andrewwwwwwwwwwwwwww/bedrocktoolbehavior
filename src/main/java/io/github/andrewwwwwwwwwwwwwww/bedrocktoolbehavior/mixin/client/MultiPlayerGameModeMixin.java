package io.github.andrewwwwwwwwwwwwwww.bedrocktoolbehavior.mixin.client;

import io.github.andrewwwwwwwwwwwwwww.bedrocktoolbehavior.client.UseRateTracker;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Rate limits repeat interactions per block, which is what lets the client poll every tick without
 * spamming.
 *
 * <p>Everything {@code useItemOn} does — running the interaction locally as a prediction, and
 * sending the packet — happens inside its {@code startPrediction} call, so cancelling at HEAD
 * suppresses the packet, the predicted sound and the action together, at no cost.
 *
 * <p>Returning {@code FAIL} rather than {@code PASS} matters: {@code Minecraft.startUseItem}
 * returns immediately on a fail, whereas a pass would fall through to trying the item on empty air.
 */
@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void bedrocktoolbehavior$limitRepeats(LocalPlayer player, InteractionHand hand,
                                                  BlockHitResult hit,
                                                  CallbackInfoReturnable<InteractionResult> cir) {
        if (UseRateTracker.shouldSuppress(player, hit)) {
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}
