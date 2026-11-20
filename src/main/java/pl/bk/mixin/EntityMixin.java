package pl.bk.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pl.bk.BKConfig;

/** Expands selected client-side entity bounds while the Hitboxes module is enabled. */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method="getBoundingBox", at=@At("RETURN"), cancellable=true)
    private void bk$expandTargetHitbox(CallbackInfoReturnable<Box> cir) {
        Entity entity=(Entity)(Object)this;
        MinecraftClient mc=MinecraftClient.getInstance();
        if(!BKConfig.data.hitboxes || mc.player==null || entity==mc.player ||
                !(entity instanceof LivingEntity living) || !living.isAlive()) return;
        boolean selected =
                (living instanceof PlayerEntity && BKConfig.data.hitboxPlayers) ||
                (living instanceof PassiveEntity && BKConfig.data.hitboxAnimals) ||
                (living instanceof Monster && BKConfig.data.hitboxMobs);
        if(!selected || (!BKConfig.data.hitboxInvisibles && living.isInvisible())) return;
        if(living instanceof PlayerEntity target) {
            if(mc.player.isTeammate(target) && !BKConfig.data.hitboxFriends) return;
            if(!BKConfig.data.hitboxNakedPlayers) {
                for(net.minecraft.item.ItemStack stack:target.getArmorItems()) if(!stack.isEmpty()) { selected=false; break; }
                if(!selected) return;
            }
        }
        double extra=BKConfig.data.hitboxExpansion;
        if(extra>0) cir.setReturnValue(cir.getReturnValue().expand(extra,0.0,extra));
    }
}
