package pl.bk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

/** HUD rendering for Tracers only. */
public final class BKVisuals {
    private BKVisuals() {}
    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(!BKConfig.data.tracers || mc.player==null || mc.world==null) return;
        float tickDelta=mc.getRenderTickCounter().getTickDelta(false);
        Camera camera=mc.gameRenderer.getCamera();
        int sw=mc.getWindow().getScaledWidth(), sh=mc.getWindow().getScaledHeight();
        for(Entity entity:mc.world.getEntities()) {
            if(entity==mc.player || !(entity instanceof LivingEntity living) || !living.isAlive()) continue;
            if(entity.squaredDistanceTo(mc.player)>128.0*128.0) continue;
            boolean player=entity instanceof PlayerEntity;
            boolean friend=player && mc.player.isTeammate(entity);
            if(player) {
                if(!BKConfig.data.tracerPlayers || (friend && BKConfig.data.tracerIgnoreFriends)
                        || (friend && !BKConfig.data.tracerFriends)
                        || (!BKConfig.data.tracerNakedPlayers && !hasArmor((PlayerEntity)entity))) continue;
            } else if(!((living instanceof PassiveEntity && BKConfig.data.tracerAnimals)
                    || (living instanceof Monster && BKConfig.data.tracerMobs))) continue;
            if(living.isInvisible() && !BKConfig.data.tracerInvisibles) continue;
            Vec3d point=entity.getLerpedPos(tickDelta).add(0,entity.getHeight()*0.5,0);
            ScreenPoint p=project(point,camera,sw,sh,mc);
            if(p!=null) drawLine(ctx,sw/2,sh-2,p.x,p.y,
                    friend?BKConfig.data.tracerFriendColor:BKConfig.data.tracerColor,BKConfig.data.tracerWidth);
        }
    }
    private static boolean hasArmor(PlayerEntity player) {
        for(var stack:player.getArmorItems()) if(!stack.isEmpty()) return true;
        return false;
    }
    private static ScreenPoint project(Vec3d world,Camera camera,int width,int height,MinecraftClient mc) {
        Vec3d rel=world.subtract(camera.getPos());
        double yaw=Math.toRadians(camera.getYaw()),pitch=Math.toRadians(camera.getPitch());
        double cy=Math.cos(yaw),sy=Math.sin(yaw);
        double cameraX=rel.x*cy+rel.z*sy;
        double forward=-rel.x*sy+rel.z*cy;
        double cameraY=rel.y*Math.cos(pitch)+forward*Math.sin(pitch);
        double depth=forward*Math.cos(pitch)-rel.y*Math.sin(pitch);
        if(depth<=0.05) return null;
        double focal=(height*0.5)/Math.tan(Math.toRadians(mc.options.getFov().getValue()*0.5));
        double x=width*0.5+cameraX*focal/depth, y=height*0.5-cameraY*focal/depth;
        if(!Double.isFinite(x)||!Double.isFinite(y)||x< -1000||x>width+1000||y< -1000||y>height+1000) return null;
        return new ScreenPoint(x,y);
    }
    private static void drawLine(DrawContext ctx,int x1,int y1,double x2,double y2,int color,float width) {
        double dx=x2-x1,dy=y2-y1;
        int steps=Math.max(1,(int)(Math.max(Math.abs(dx),Math.abs(dy))/3.0));
        int size=Math.max(1,Math.round(width));
        for(int i=0;i<=steps;i++) {
            double t=i/(double)steps;
            int x=(int)Math.round(x1+dx*t),y=(int)Math.round(y1+dy*t);
            ctx.fill(x-size/2,y-size/2,x+(size+1)/2,y+(size+1)/2,color);
        }
    }
    private record ScreenPoint(double x,double y) {}
}
