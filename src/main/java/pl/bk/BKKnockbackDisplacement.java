package pl.bk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

/**
 * Defers a regular player attack by one tick and sends a temporary look packet first,
 * so directional knockback is based on the selected yaw. It never creates an attack.
 */
public final class BKKnockbackDisplacement {
    private static LivingEntity pending;
    private static ClientPlayerEntity attacker;
    private static ClientWorld pendingWorld;
    private static int pendingAge;
    private static float forcedYaw;
    private static boolean replaying;
    private BKKnockbackDisplacement(){}

    public static boolean deferAttack(ClientPlayerEntity player,Entity target){
        MinecraftClient mc=MinecraftClient.getInstance();
        if(!BKConfig.data.knockbackDisplacement||replaying||player==null||mc.world==null||
                !(target instanceof LivingEntity living)||!living.isAlive())return false;
        if(BKConfig.data.knockbackSprintOnly&&!player.isSprinting())return false;
        if(pending!=null)return true;
        attacker=player; pending=living; pendingWorld=mc.world; pendingAge=player.age;
        if(BKConfig.data.knockbackDynamic){
            double dx=player.getX()-target.getX();
            double dz=player.getZ()-target.getZ();
            forcedYaw=(float)Math.toDegrees(Math.atan2(-dx,dz));
        }else forcedYaw=player.getYaw()+180.0f;
        forcedYaw=wrap(forcedYaw+BKConfig.data.knockbackAngle);
        return true;
    }

    public static void tick(MinecraftClient mc){
        if(pending==null)return;
        if(mc.player==null||mc.player!=attacker||mc.world!=pendingWorld||mc.interactionManager==null||
                !pending.isAlive()||pending.isRemoved()||mc.player.age-pendingAge>4||
                mc.player.squaredDistanceTo(pending)>Math.pow(mc.player.getEntityInteractionRange(),2)){
            clear();return;
        }
        if(mc.player.age<=pendingAge)return;
        ClientPlayerEntity player=mc.player;
        float originalYaw=player.getYaw();
        float originalPitch=player.getPitch();
        boolean canRedirect=!BKConfig.data.knockbackSprintOnly||player.isSprinting();
        replaying=true;
        try{
            if(canRedirect) player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                    forcedYaw,originalPitch,player.isOnGround(),player.horizontalCollision));
            mc.interactionManager.attackEntity(player,pending);
            player.swingHand(Hand.MAIN_HAND);
            if(canRedirect) player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
                    originalYaw,originalPitch,player.isOnGround(),player.horizontalCollision));
        }finally{
            replaying=false;clear();
        }
    }
    public static boolean isReplaying(){return replaying;}
    private static float wrap(float yaw){
        yaw%=360.0f;
        if(yaw>=180.0f)yaw-=360.0f;
        if(yaw< -180.0f)yaw+=360.0f;
        return yaw;
    }
    private static void clear(){pending=null;attacker=null;pendingWorld=null;}
}
