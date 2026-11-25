package pl.bk;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public final class BKClickGui extends Screen {
    private static final int W=410,H=270;
    private boolean waitingForBind=false;
    public BKClickGui() { super(Text.literal("BK Client")); }

    @Override protected void init() {
        clearChildren();
        int x=width/2-W/2, y=height/2-H/2;
        addDrawableChild(button(x+W-94,y+12,80,20,"Zamknij",b->close()));
        addDrawableChild(button(x+12,y+48,244,28,"Air Freeze  ["+(BKConfig.data.airFreeze?"WŁ.":"WYŁ.")+"]",b->{BKMod.toggleAirFreeze();clearAndInit();}));
        KeyBinding key=BKModuleKeys.airFreeze;
        String keyText=waitingForBind?"Naciśnij klawisz":key.getBoundKeyLocalizedText().getString();
        addDrawableChild(button(x+264,y+48,134,28,"Bind: "+keyText,b->{waitingForBind=true;clearAndInit();}));
        addToggle(x,y+92,"NoPush",()->BKConfig.data.noPush,v->BKConfig.data.noPush=v);
        addToggle(x,y+126,"Blokuj push od graczy",()->BKConfig.data.noPushEntities,v->BKConfig.data.noPushEntities=v);
        addToggle(x,y+160,"Blokuj push od płynów",()->BKConfig.data.noPushFluids,v->BKConfig.data.noPushFluids=v);
        addToggle(x,y+194,"Blokuj wypychanie z bloków",()->BKConfig.data.noPushBlocks,v->BKConfig.data.noPushBlocks=v);
    }
    private void addToggle(int x,int y,String label,java.util.function.BooleanSupplier get,java.util.function.Consumer<Boolean> set) {
        addDrawableChild(button(x+12,y,386,26,label+": "+(get.getAsBoolean()?"WŁ.":"WYŁ."),b->{set.accept(!get.getAsBoolean());BKConfig.save();clearAndInit();}));
    }
    private ButtonWidget button(int x,int y,int w,int h,String label,ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(label),action).dimensions(x,y,w,h).build();
    }
    @Override public boolean keyPressed(int keyCode,int scanCode,int modifiers) {
        if(waitingForBind) {
            BKModuleKeys.setAirFreezeBind(keyCode==256?0:keyCode);
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