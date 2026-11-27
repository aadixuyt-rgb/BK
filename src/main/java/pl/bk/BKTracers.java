package pl.bk;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

/** Lekkie tracery 2D: jedna projekcja i jeden batch GPU na klatke HUD. */
public final class BKTracers {
    private static final float NEAR=0.05f;
    private static final Matrix4f VP=new Matrix4f();
    private static Vec3d camera=Vec3d.ZERO;
    private static boolean valid;

    private static final int[] COLORS={
            0xE6FFFFFF, 0xE6FF4545, 0xE650F08A, 0xE65BA8FF, 0xE6C477FF
    };

    private BKTracers() {}

    public static void capture(WorldRenderContext context) {
        Camera c=context.camera();
        camera=c.getPos();
        Matrix4f view=new Matrix4f().rotation(c.getRotation().conjugate(new Quaternionf()));
        new Matrix4f(context.projectionMatrix()).mul(view,VP);
        valid=true;
    }

    public static void render(DrawContext draw, RenderTickCounter tickCounter) {
        MinecraftClient mc=MinecraftClient.getInstance();
        if(!BKConfig.data.tracersEnabled || !valid || mc.player==null || mc.world==null
                || mc.options.hudHidden) return;

        float partialTick=tickCounter.getTickDelta(false);
        int sw=mc.getWindow().getScaledWidth(), sh=mc.getWindow().getScaledHeight();
        int color=COLORS[Math.max(0,Math.min(COLORS.length-1,BKConfig.data.tracerColor))];
        float width=Math.max(1.0f,Math.min(5.0f,BKConfig.data.tracerThickness));

        VertexConsumerProvider.Immediate provider=mc.getBufferBuilders().getEntityVertexConsumers();
        VertexConsumer vertices=provider.getBuffer(RenderLayer.getGui());
        Matrix4f gui=draw.getMatrices().peek().getPositionMatrix();
        boolean drew=false;

        for(AbstractClientPlayerEntity player:mc.world.getPlayers()) {
            if(player==mc.player || player.isSpectator() || !player.isAlive()) continue;

            Vec3d pos=player.getLerpedPos(partialTick);
            double targetY=pos.y + player.getHeight()*0.55;
            Vector4f point=new Vector4f(
                    (float)(pos.x-camera.x),
                    (float)(targetY-camera.y),
                    (float)(pos.z-camera.z),1.0f);
            VP.transform(point);
            // Nie odrzucaj graczy za kamera: ich projekcja wskazuje krawedz ekranu.
            float w=point.w;
            if(Math.abs(w)<NEAR) w=(w<0.0f ? -NEAR : NEAR);
            float x2=(point.x/w*0.5f+0.5f)*sw;
            float y2=(1.0f-(point.y/w*0.5f+0.5f))*sh;
            if(!Float.isFinite(x2) || !Float.isFinite(y2)) continue;

            // Początek linii jest w celowniku, a koniec ograniczony do obszaru ekranu.
            float x1=sw*0.5f, y1=sh*0.5f;
            float dx=x2-x1, dy=y2-y1;
            float len=(float)Math.sqrt(dx*dx+dy*dy);
            if(len<0.5f) {
                // Gracz dokladnie za kamera: pokaz wskaznik na dolnej krawedzi.
                dx=0.0f; dy=1.0f; len=1.0f;
            }
            float maxX=Math.max(1.0f,sw*0.5f-8.0f);
            float maxY=Math.max(1.0f,sh*0.5f-8.0f);
            float scale=Math.min(maxX/Math.max(0.0001f,Math.abs(dx)),maxY/Math.max(0.0001f,Math.abs(dy)));
            if(scale<1.0f) { dx*=scale; dy*=scale; }
            x2=x1+dx; y2=y1+dy;
            float nx=-dy/len*width*0.5f, ny=dx/len*width*0.5f;
            float[] xs={x1+nx,x1-nx,x2-nx,x2+nx};
            float[] ys={y1+ny,y1-ny,y2-ny,y2+ny};
            float area=0.0f;
            for(int i=0;i<4;i++){int j=(i+1)&3;area+=xs[i]*ys[j]-xs[j]*ys[i];}
            if(area>0) {
                for(int i=3;i>=0;i--) vertices.vertex(gui,xs[i],ys[i],0.0f).color(color);
            } else {
                for(int i=0;i<4;i++) vertices.vertex(gui,xs[i],ys[i],0.0f).color(color);
            }
            drew=true;
        }
        if(drew) provider.draw();
    }
}
