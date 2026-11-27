package pl.bk.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Allows one Fire Charge use per 1.5 seconds; repeated right-clicks are ignored. */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class FireballCooldownMixin {
    private static long bk$lastFireChargeUse = 0L;
    private static final long BK_FIRE_CHARGE_INTERVAL_MS = 1500L;

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    private void bk$limitFireChargeUse(PlayerEntity player, Hand hand,
                                       CallbackInfoReturnable<ActionResult> cir) {
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(Items.FIRE_CHARGE)) return;

        long now = System.currentTimeMillis();
        if (now - bk$lastFireChargeUse < BK_FIRE_CHARGE_INTERVAL_MS) {
            cir.setReturnValue(ActionResult.FAIL);
            return;
        }
        bk$lastFireChargeUse = now;
    }
}
