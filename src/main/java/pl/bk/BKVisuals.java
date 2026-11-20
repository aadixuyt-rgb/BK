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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Rendering for the only two HUD modules: Tracers and Nametags. */
public final class BKVisuals {
    private BKVisuals() {}

    public static void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(mc.player==null || mc.world==null) return;

        float tickDelta=mc.getRenderTickCounter().getTickDelta(false);
        Camera camera=mc.gameRenderer.getCamera();
        int sw=mc.getWindow().getScaledWidth(), sh=mc.getWindow().getScaledHeight();
        List<Label> labels=new ArrayList<>();

        for(PlayerEntity player:mc.world.getPlayers()) {
            if(player==mc.player || !player.isAlive()) continue;
            double distance=player.distanceTo(mc.player);
            if(distance>128.0) continue;

            boolean friend=mc.player.isTeammate(player);
            boolean armored=hasArmor(player);
            Vec3d point=player.getLerpedPos(tickDelta).add(0,player.getHeight()*0.72,0);
            ScreenPoint p=project(point,camera,sw,sh,mc);
            if(p==null) continue;

            if(BKConfig.data.tracers && BKConfig.data.tracerPlayers
                    && (!friend || BKConfig.data.tracerFriends)
                    && !(friend && BKConfig.data.tracerIgnoreFriends)
                    && (armored || BKConfig.data.tracerNakedPlayers)) {
                int color=friend?BKConfig.data.tracerFriendColor:BKConfig.data.tracerColor;
                drawLine(ctx,sw/2,sh-2,p.x,p.y,color,BKConfig.data.tracerWidth);
            }

            if(BKConfig.data.nametagsEnabled) {
                String name=player.getGameProfile().getName();
                String hp=Math.round(player.getHealth())+" HP";
                String text=name+"  "+hp;
                int color=friend?0xFF20D86A:0xFFFFFFFF;
                labels.add(new Label(p.x,p.y-12,text,color,
                        BKConfig.data.nametagScale,
                        BKConfig.data.nametagArmor && armored?player:null,distance));
            }
        }

        // Keep nearby labels readable instead of drawing several on the same pixels.
        labels.sort(Comparator.comparingDouble(Label::distance));
        List<int[]> occupied=new ArrayList<>();
        int count=0;
        for(Label label:labels) {
            if(count>=32) break;
            int tw=(int)(mc.textRenderer.getWidth(label.text())*label.scale())+8;
            int th=(int)(mc.textRenderer.fontHeight*label.scale())+5;
            int cx=(int)Math.round(label.x()), base=(int)Math.round(label.y());
            boolean placed=false;
            for(int step=0;step<20;step++) {
                int offset=step==0?0:((step+1)/2)*Math.max(8,th)*(step%2==1?-1:1);
                int cy=base+offset,left=cx-tw/2,right=cx+tw/2,top=cy-2,bottom=cy+th;
                if(left<0||right>sw||top<0||bottom>sh) continue;
                boolean overlap=false;
                for(int[] r:occupied) if(left<r[2]&&right>r[0]&&top<r[3]&&bottom>r[1]) { overlap=true; break; }
                if(overlap) continue;
                occupied.add(new int[]{left,top,right,bottom});
                drawLabel(ctx,mc,label,cx,cy);
                placed=true;
                break;
            }
            if(placed) count++;
        }

        if(BKConfig.data.tracers && (BKConfig.data.tracerAnimals || BKConfig.data.tracerMobs)) {
            for(Entity entity:mc.world.getEntities()) {
                if(!(entity instanceof LivingEntity living) || entity==mc.player || !living.isAlive()
                        || entity instanceof PlayerEntity || !isTracerTarget(living)) continue;
                if(entity.squaredDistanceTo(mc.player)>128.0*128.0) continue;
                Vec3d point=entity.getLerpedPos(tickDelta).add(0,entity.getHeight()*0.5,0);
                ScreenPoint p=project(point,camera,sw,sh,mc);
                if(p!=null) drawLine(ctx,sw/2,sh-2,p.x,p.y,BKConfig.data.tracerColor,BKConfig.data.tracerWidth);
            }
        }
    }

    private static boolean isTracerTarget(LivingEntity entity) {
        if(entity.isInvisible() && !BKConfig.data.tracerInvisibles) return false;
        return (entity instanceof PassiveEntity && BKConfig.data.tracerAnimals)
                || (entity instanceof Monster && BKConfig.data.tracerMobs);
    }
    private static boolean hasArmor(PlayerEntity player) {
        for(ItemStack stack:player.getArmorItems()) if(!stack.isEmpty()) return true;
        return false;
    }
    private static void drawLabel(DrawContext ctx,MinecraftClient mc,Label label,int x,int y) {
        int width=mc.textRenderer.getWidth(label.text());
        var matrices=ctx.getMatrices();
        matrices.push();
        matrices.translate(x,y,0);
        matrices.scale(label.scale(),label.scale(),1.0f);
        if(BKConfig.data.nametagBackground)
            ctx.fill(-width/2-4,-2,width/2+4,mc.textRenderer.fontHeight+2,0x900B0D13);
        ctx.drawTextWithShadow(mc.textRenderer,Text.literal(label.text()),-width/2,0,label.color());
        matrices.pop();
        if(label.armorPlayer()!=null) drawArmor(ctx,label.armorPlayer(),x,
                y+(int)(mc.textRenderer.fontHeight+4));
    }
    private static void drawArmor(DrawContext ctx,PlayerEntity player,int centerX,int y) {
        int i=0;
        for(ItemStack stack:player.getArmorItems()) if(!stack.isEmpty()) {
            ctx.drawItem(stack,centerX-32+i*16,y);
            i++;
        }
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
        double fov=mc.options.getFov().getValue();
        double focal=(height*0.5)/Math.tan(Math.toRadians(fov*0.5));
        double x=width*0.5+cameraX*focal/depth;
        double y=height*0.5-cameraY*focal/depth;
        if(!Double.isFinite(x)||!Double.isFinite(y)||x< -1000||x>width+1000||y< -1000||y>height+1000) return null;
        return new ScreenPoint(x,y,depth);
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
    private record Label(double x,double y,String text,int color,float scale,PlayerEntity armorPlayer,double distance) {}
    private record ScreenPoint(double x,double y,double depth) {}
}
