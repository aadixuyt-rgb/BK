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
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/** HUD renderers for BK tracers, player ESP and nametags. */
public final class BKVisuals {
    private BKVisuals() {}

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.player==null || mc.world==null) return;
        float tickDelta=mc.getRenderTickCounter().getTickDelta(false);
        Camera camera=mc.gameRenderer.getCamera();
        int screenW=mc.getWindow().getScaledWidth();
        int screenH=mc.getWindow().getScaledHeight();

        for(PlayerEntity player:mc.world.getPlayers()) {
            if(player==mc.player || !player.isAlive()) continue;
            double distance=player.distanceTo(mc.player);
            boolean friend=mc.player.isTeammate(player);
            boolean hasArmor=hasArmor(player);
            if(distance>128.0 && !(BKConfig.data.playerEsp || BKConfig.data.nametagsEnabled)) continue;
            Vec3d point=player.getLerpedPos(tickDelta).add(0,player.getHeight()*0.72,0);
            ScreenPoint p=project(point,camera,screenW,screenH,mc);
            if(p==null) continue;

            boolean tracerTarget=BKConfig.data.tracerPlayers &&
                    (!friend || BKConfig.data.tracerFriends) &&
                    !(friend && BKConfig.data.tracerIgnoreFriends) &&
                    (hasArmor || BKConfig.data.tracerNakedPlayers);
            if(BKConfig.data.tracers && tracerTarget) {
                int tracerColor=friend?BKConfig.data.tracerFriendColor:BKConfig.data.tracerColor;
                drawLine(ctx,screenW/2,screenH-2,p.x,p.y,tracerColor,BKConfig.data.tracerWidth);
            }

            if(BKConfig.data.playerEsp && (!friend || BKConfig.data.espFriends) && distance<=BKConfig.data.espDistance) {
                int color=friend?BKConfig.data.espFriendColor:BKConfig.data.espColor;
                double projectedHeight=Math.max(8.0,Math.min(150.0,screenH*0.55/Math.max(1.0,p.depth)));
                double halfWidth=projectedHeight*0.28;
                if(BKConfig.data.espBox) drawOutline(ctx,(int)(p.x-halfWidth),(int)(p.y-projectedHeight*0.65),
                        (int)(p.x+halfWidth),(int)(p.y+projectedHeight*0.35),color);
                if(BKConfig.data.espName) drawCentered(ctx,mc,player.getGameProfile().getName(),
                        (int)p.x,(int)(p.y-projectedHeight*0.82),color);
                if(BKConfig.data.espHealth) drawCentered(ctx,mc,Math.round(player.getHealth())+" HP",
                        (int)p.x,(int)(p.y-projectedHeight*0.82+10),0xFF70E090);
            }

            if(BKConfig.data.nametagsEnabled && BKConfig.data.nametags && distance<=BKConfig.data.espDistance) {
                String label=player.getGameProfile().getName()+"  "+Math.round(player.getHealth())+" HP";
                int nameY=(int)(p.y-12*BKConfig.data.nametagScale);
                drawCentered(ctx,mc,label,(int)p.x,nameY,friend?BKConfig.data.espFriendColor:BKConfig.data.espColor);
                if(BKConfig.data.nametagArmor && hasArmor) drawArmor(ctx,player,(int)p.x,nameY+12);
            }
        }

        if(BKConfig.data.tracers && (BKConfig.data.tracerAnimals || BKConfig.data.tracerMobs)) {
            for(Entity entity:mc.world.getEntities()) {
                if(!(entity instanceof LivingEntity living) || entity==mc.player || !living.isAlive() ||
                        entity instanceof PlayerEntity || !isTracerTarget(living)) continue;
                if(entity.squaredDistanceTo(mc.player)>128.0*128.0) continue;
                Vec3d point=entity.getLerpedPos(tickDelta).add(0,entity.getHeight()*0.5,0);
                ScreenPoint p=project(point,camera,screenW,screenH,mc);
                if(p!=null) drawLine(ctx,screenW/2,screenH-2,p.x,p.y,BKConfig.data.tracerColor,BKConfig.data.tracerWidth);
            }
        }
    }

    private static boolean isTracerTarget(LivingEntity entity) {
        if(entity.isInvisible() && !BKConfig.data.tracerInvisibles) return false;
        return (entity instanceof PassiveEntity && BKConfig.data.tracerAnimals) ||
                (entity instanceof Monster && BKConfig.data.tracerMobs);
    }
    private static boolean hasArmor(PlayerEntity player) {
        for(ItemStack stack:player.getArmorItems()) if(!stack.isEmpty()) return true;
        return false;
    }
    private static void drawArmor(DrawContext ctx,PlayerEntity player,int centerX,int y) {
        int index=0;
        for(ItemStack stack:player.getArmorItems()) {
            if(!stack.isEmpty()) {
                ctx.drawItem(stack,centerX-32+index*16,y);
                index++;
            }
        }
    }

    private static ScreenPoint project(Vec3d world,Camera camera,int width,int height,MinecraftClient mc) {
        Vec3d rel=world.subtract(camera.getPos());
        double yaw=Math.toRadians(camera.getYaw());
        double pitch=Math.toRadians(camera.getPitch());
        double cosYaw=Math.cos(yaw),sinYaw=Math.sin(yaw);
        double cameraX=rel.x*cosYaw+rel.z*sinYaw;
        double forward=-rel.x*sinYaw+rel.z*cosYaw;
        double cameraY=rel.y*Math.cos(pitch)+forward*Math.sin(pitch);
        double depth=forward*Math.cos(pitch)-rel.y*Math.sin(pitch);
        if(depth<=0.05) return null;
        double fov=mc.options.getFov().getValue();
        double focal=(height*0.5)/Math.tan(Math.toRadians(fov*0.5));
        double x=width*0.5+cameraX*focal/depth;
        double y=height*0.5-cameraY*focal/depth;
        if(!Double.isFinite(x)||!Double.isFinite(y)||x< -1000||x>width+1000||y< -1000||y>height+1000)return null;
        return new ScreenPoint(x,y,depth);
    }

    private static void drawLine(DrawContext ctx,int x1,int y1,double x2,double y2,int color,float width) {
        double dx=x2-x1,dy=y2-y1;
        int steps=Math.max(1,(int)(Math.max(Math.abs(dx),Math.abs(dy))/3.0));
        int size=Math.max(1,Math.round(width));
        for(int i=0;i<=steps;i++){
            double t=i/(double)steps;
            int x=(int)Math.round(x1+dx*t),y=(int)Math.round(y1+dy*t);
            ctx.fill(x-size/2,y-size/2,x+(size+1)/2,y+(size+1)/2,color);
        }
    }
    private static void drawOutline(DrawContext ctx,int x1,int y1,int x2,int y2,int color) {
        if(x2<=x1||y2<=y1)return;
        ctx.fill(x1,y1,x2,y1+1,color); ctx.fill(x1,y2-1,x2,y2,color);
        ctx.fill(x1,y1,x1+1,y2,color); ctx.fill(x2-1,y1,x2,y2,color);
    }
    private static void drawCentered(DrawContext ctx,MinecraftClient mc,String value,int x,int y,int color) {
        int w=mc.textRenderer.getWidth(value);
        if(BKConfig.data.nametagBackground) ctx.fill(x-w/2-3,y-2,x+w/2+3,y+mc.textRenderer.fontHeight+1,0x900B0D13);
        ctx.drawTextWithShadow(mc.textRenderer,Text.literal(value),x-w/2,y,color);
    }
    private record ScreenPoint(double x,double y,double depth) {}
}
