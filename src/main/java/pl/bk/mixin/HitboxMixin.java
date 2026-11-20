package pl.bk.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.bk.BKConfig;

@Mixin(Entity.class)
public abstract class HitboxMixin {
    @Inject(method="getBoundingBox", at=@At("RETURN"), cancellable=true)
    private void bk$expandHitbox(CallbackInfoReturnable<Box> cir) {
        if(!BKConfig.data.hitboxes) return;
        Entity entity=(Entity)(Object)this;
        if(!(entity instanceof LivingEntity living) || entity instanceof ClientPlayerEntity || !living.isAlive()) return;
        if(entity.isInvisible() && !BKConfig.data.hitboxInvisibles) return;
        MinecraftClient mc=MinecraftClient.getInstance();
        if(entity instanceof PlayerEntity player) {
            boolean friend=mc.player!=null && mc.player.isTeammate(player);
            if(friend && !BKConfig.data.hitboxFriends) return;
            boolean naked=!hasArmor(player);
            if(!BKConfig.data.hitboxPlayers && !BKConfig.data.hitboxNakedPlayers) return;
            if(BKConfig.data.hitboxPlayers && !BKConfig.data.hitboxNakedPlayers && naked) return;
            if(!BKConfig.data.hitboxPlayers && BKConfig.data.hitboxNakedPlayers && !naked) return;
        } else if(living instanceof AnimalEntity) {
            if(!BKConfig.data.hitboxAnimals) return;
        } else if(living instanceof MobEntity) {
            if(!BKConfig.data.hitboxMobs) return;
        } else return;
        float size=BKConfig.data.hitboxScale;
        if(size>0.0f) {
            Box original=cir.getReturnValue();
            cir.setReturnValue(original.expand(size,size,size));
        }
    }
    private static boolean hasArmor(PlayerEntity player) {
        for(var stack:player.getArmorItems()) if(!stack.isEmpty()) return true;
        return false;
    }
}
