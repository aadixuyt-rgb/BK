package pl.bk.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.bk.BKConfig;

/** NoPush: disables entity-to-player pushing only for the local player. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method="isPushable", at=@At("HEAD"), cancellable=true)
    private void bk$disableEntityPush(CallbackInfoReturnable<Boolean> cir) {
        if((Object)this==MinecraftClient.getInstance().player &&
                BKConfig.data.noPush && BKConfig.data.noPushEntities) {
            cir.setReturnValue(false);
        }
    }
}
