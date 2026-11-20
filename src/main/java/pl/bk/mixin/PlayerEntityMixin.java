package pl.bk.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.bk.BKConfig;

/** NoPush: optional fluid push handling for the local player. */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Inject(method="isPushedByFluids", at=@At("HEAD"), cancellable=true)
    private void bk$disableFluidPush(CallbackInfoReturnable<Boolean> cir) {
        if((Object)this==MinecraftClient.getInstance().player &&
                BKConfig.data.noPush && BKConfig.data.noPushFluids) {
            cir.setReturnValue(false);
        }
    }
}
