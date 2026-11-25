package pl.bk;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public final class BKClickGui extends Screen {
    private static final int W=410,H=310;
    private boolean waitingForBind=false;
    public BKClickGui() { super(Text.literal("BK Client")); }

    @Override protected void init() {
        clearChildren();
        int x=width/2-W/2, y=height/2-H/2;
        addDrawableChild(button(x+W-94,y+12,80,20,"Zamknij",b->close()));
        addDrawableChild(button(x+12,y+48,244,28,"Air Freeze  ["+(BKConfig.data.noPush?"WŁ.":"WYŁ.")+"]",b->{BKMod.toggleAirFreeze();clearAndInit();}));
        KeyBinding key=BKModuleKeys.noPush;
        String keyText=waitingForBind?"Naciśnij klawisz":key.getBoundKeyLocalizedText().getString();
        addDrawableChild(button(x+264,y+48,134,28,"Bind: "+keyText,b->{waitingForBind=true;clearAndInit();}));
        addToggle(x,y+84,"Blokuj push od graczy",()->BKConfig.data.noPushEntities,v->BKConfig.data.noPushEntities=v);
        addToggle(x,y+114,"Blokuj push od płynów",()->BKConfig.data.noPushFluids,v->BKConfig.data.noPushFluids=v);
        addToggle(x,y+144,"Blokuj wypychanie z bloków",()->BKConfig.data.noPushBlocks,v->BKConfig.data.noPushBlocks=v);

        addDrawableChild(button(x+12,y+190,W-24,22,"BK DRIPSTONE — ZAWSZE AKTYWNY",b->{}));
        addDrawableChild(new DelaySlider(x+12,y+220,W-24,20));
        addDrawableChild(button(x+12,y+252,W-24,24,"Opóźnienie zapisuje się automatycznie",b->{}));
    }
    private void addToggle(int x,int y,String label,java.util.function.BooleanSupplier get,java.util.function.Consumer<Boolean> set) {
        addDrawableChild(button(x+12,y,386,24,label+": "+(get.getAsBoolean()?"WŁ.":"WYŁ."),b->{set.accept(!get.getAsBoolean());BKConfig.save();clearAndInit();}));
    }
    private ButtonWidget button(int x,int y,int w,int h,String label,ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(label),action).dimensions(x,y,w,h).build();
    }
    private static final class DelaySlider extends SliderWidget {
        DelaySlider(int x,int y,int width,int height) {
            super(x,y,width,height,Text.empty(),(BKConfig.data.dripstoneDelayMs-50)/1950.0);
            updateMessage();
        }
        @Override protected void updateMessage() {
            setMessage(Text.literal("Opóźnienie: "+getDelay()+" ms"));
        }
        @Override protected void applyValue() {
            BKConfig.data.dripstoneDelayMs=getDelay();
            BKConfig.save();
        }
        private int getDelay() {
            return Math.max(50,Math.min(2000,50+(int)Math.round(value*1950)));
        }
    }
    @Override public boolean keyPressed(int keyCode,int scanCode,int modifiers) {
        if(waitingForBind) {
            BKModuleKeys.setNoPushBind(keyCode==256?0:keyCode);
            waitingForBind=false;
            clearAndInit();
            return true;
        }
        if(keyCode==256) { close(); return true; }
        return super.keyPressed(keyCode,scanCode,modifiers);
    }
    @Override public void render(DrawContext ctx,int mouseX,int mouseY,float delta) {
        renderBackground(ctx,mouseX,mouseY,delta);
        int x=width/2-W/2,y=height/2-H/2;
        ctx.fill(x,y,x+W,y+H,0xF0161B26);
        ctx.fill(x,y,x+W,y+2,0xFF9A5DFF);
        ctx.drawTextWithShadow(textRenderer,Text.literal("BK CLIENT"),x+12,y+14,0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer,Text.literal("Moduły i ustawienia"),x+12,y+31,0xFFADB7C8);
        super.render(ctx,mouseX,mouseY,delta);
    }
    @Override public boolean shouldPause() { return false; }
}