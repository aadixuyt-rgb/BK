package pl.bk;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

public final class BKClickGui extends Screen {
    private static final int W=360,H=220;
    private String waitingForBind=null;
    private String page=null;

    public BKClickGui() { super(Text.literal("BK Client")); }

    @Override protected void init() {
        clearChildren();
        int x=width/2-W/2, y=height/2-H/2;
        addDrawableChild(button(x+W-94,y+12,80,20,"Zamknij",b->close()));
        if(page==null) {
            addModule(x,y+48,"NoPush");
            addModule(x,y+82,"Nametags");
            addModule(x,y+116,"Tracers");
            addDrawableChild(button(x+12,y+H-34,160,20,"Ustawienia NoPush",b->{page="NoPush";clearAndInit();}));
            addDrawableChild(button(x+180,y+H-34,168,20,"Ustawienia Tracers",b->{page="Tracers";clearAndInit();}));
        } else {
            addDrawableChild(button(x+12,y+38,100,20,"← Wróć",b->{page=null;clearAndInit();}));
            if(page.equals("NoPush")) {
                addToggle(x,y+70,"Push od graczy",()->BKConfig.data.noPushEntities,v->BKConfig.data.noPushEntities=v);
                addToggle(x,y+102,"Push od płynów",()->BKConfig.data.noPushFluids,v->BKConfig.data.noPushFluids=v);
                addToggle(x,y+134,"Wypychanie z bloków",()->BKConfig.data.noPushBlocks,v->BKConfig.data.noPushBlocks=v);
            } else {
                addToggle(x,y+70,"Gracze",()->BKConfig.data.tracerPlayers,v->BKConfig.data.tracerPlayers=v);
                addToggle(x,y+102,"Zwierzęta",()->BKConfig.data.tracerAnimals,v->BKConfig.data.tracerAnimals=v);
                addToggle(x,y+134,"Wrogie moby",()->BKConfig.data.tracerMobs,v->BKConfig.data.tracerMobs=v);
                addToggle(x,y+166,"Niewidzialni",()->BKConfig.data.tracerInvisibles,v->BKConfig.data.tracerInvisibles=v);
            }
        }
    }
    private void addModule(int x,int y,String name) {
        addDrawableChild(button(x+12,y,214,26,name+"  ["+(BKMod.isEnabled(name)?"WŁ.":"WYŁ.")+"]",b->{BKMod.toggle(name);clearAndInit();}));
        KeyBinding key=BKModuleKeys.byModule(name);
        String keyText=waitingForBind!=null&&waitingForBind.equals(name)?"Naciśnij klawisz":key.getBoundKeyLocalizedText().getString();
        addDrawableChild(button(x+234,y,114,26,"Bind: "+keyText,b->{waitingForBind=name;clearAndInit();}));
    }
    private void addToggle(int x,int y,String label,java.util.function.BooleanSupplier get,java.util.function.Consumer<Boolean> set) {
        addDrawableChild(button(x+12,y,336,24,label+": "+(get.getAsBoolean()?"WŁ.":"WYŁ."),b->{set.accept(!get.getAsBoolean());BKConfig.save();clearAndInit();}));
    }
    private ButtonWidget button(int x,int y,int w,int h,String label,ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(label),action).dimensions(x,y,w,h).build();
    }
    @Override public boolean keyPressed(int keyCode,int scanCode,int modifiers) {
        if(waitingForBind!=null) {
            BKModuleKeys.setBind(waitingForBind,keyCode==256?0:keyCode);
            waitingForBind=null;
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
        ctx.drawTextWithShadow(textRenderer,Text.literal(page==null?"Tylko trzy moduły":"Ustawienia: "+page),x+12,y+29,0xFFADB7C8);
        super.render(ctx,mouseX,mouseY,delta);
    }
    @Override public boolean shouldPause() { return false; }
}
