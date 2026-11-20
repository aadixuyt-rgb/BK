package pl.bk.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pl.bk.BKConfig;

/** NoPush: stops the local player being pushed out of blocks. */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method="pushOutOfBlocks", at=@At("HEAD"), cancellable=true)
    private void bk$disableBlockPush(double x,double z,CallbackInfo ci) {
        if(BKConfig.data.noPush && BKConfig.data.noPushBlocks) ci.cancel();
    }
}
