package pl.bk.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.bk.BKKnockbackDisplacement;

/** Hook used by Knockback Displacement to defer an already-requested local attack. */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Inject(method="attackEntity",at=@At("HEAD"),cancellable=true)
    private void bk$deferKnockbackAttack(PlayerEntity player,Entity target,CallbackInfo ci){
        if(player==MinecraftClient.getInstance().player && player instanceof ClientPlayerEntity clientPlayer &&
                BKKnockbackDisplacement.deferAttack(clientPlayer,target)){
            ci.cancel();
        }
    }
}
