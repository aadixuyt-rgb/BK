package pl.bk;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Standalone ClickGUI with module toggles, per-module bind capture and editable settings. */
public final class BKClickGui extends Screen {
    private static final int PANEL_W=430, PANEL_H=340;
    private static final List<String> CATEGORIES=List.of("Visuals","Combat","Player","Misc","Existing");
    private String category="Visuals";
    private String selectedModule=null;
    private String waitingForBind=null;
    private int scroll;

    public BKClickGui(){super(Text.literal("BK Client"));}

    @Override protected void init(){
        clearChildren();
        int left=width/2-PANEL_W/2;
        int top=height/2-PANEL_H/2;
        addDrawableChild(button(left+14,top+35,142,20,"Kategoria: "+category+"  >",b->{int i=(CATEGORIES.indexOf(category)+1)%CATEGORIES.size();category=CATEGORIES.get(i);selectedModule=null;scroll=0;clearAndInit();}));
        if(selectedModule!=null) addDrawableChild(button(left+110,top+35,90,20,"← Wróć",b->{selectedModule=null;clearAndInit();}));
        addDrawableChild(button(left+PANEL_W-104,top+35,90,20,"Zamknij",b->close()));
        if(selectedModule==null) initModules(left,top); else initSettings(left,top);
    }

    private void initModules(int left,int top){
        int y=top+68-scroll;
        for(String module:modulesForCategory()){
            final String name=module;
            addDrawableChild(button(left+14,y,142,24,stateText(name),b->{
                BKMod.toggle(name); clearAndInit();
            }));
            KeyBinding key=BKModuleKeys.byModule(name);
            String keyName=waitingForBind!=null&&waitingForBind.equals(name)?"Naciśnij klawisz":
                    (key==null?"---":key.getBoundKeyLocalizedText().getString());
            addDrawableChild(button(left+164,y,138,24,"Bind: "+keyName,b->{waitingForBind=name;clearAndInit();}));
            addDrawableChild(button(left+308,y,108,24,"Ustawienia →",b->{selectedModule=name;waitingForBind=null;clearAndInit();}));
            y+=32;
        }
        addDrawableChild(button(left+14,top+PANEL_H-32,402,20,"Zapisz ustawienia",b->{
            BKConfig.save(); clearAndInit();}));
    }

    private void initSettings(int left,int top){
        int y=top+72;
        addDrawableChild(button(left+14,y,402,22,"Moduł: "+selectedModule,b->{})); y+=32;
        switch(selectedModule){
            case "NoPush" -> {
                y=toggle(left,y,"Usuwaj push od graczy",()->BKConfig.data.noPushEntities,v->BKConfig.data.noPushEntities=v);
                y=toggle(left,y,"Usuwaj push od płynów",()->BKConfig.data.noPushFluids,v->BKConfig.data.noPushFluids=v);
                y=toggle(left,y,"Usuwaj wypychanie z bloków",()->BKConfig.data.noPushBlocks,v->BKConfig.data.noPushBlocks=v);
            }
            case "Tracers" -> {
                y=toggle(left,y,"Gracze",()->BKConfig.data.tracerPlayers,v->BKConfig.data.tracerPlayers=v);
                y=toggle(left,y,"Zwierzęta",()->BKConfig.data.tracerAnimals,v->BKConfig.data.tracerAnimals=v);
                y=toggle(left,y,"Moby",()->BKConfig.data.tracerMobs,v->BKConfig.data.tracerMobs=v);
                y=toggle(left,y,"Niewidzialni",()->BKConfig.data.tracerInvisibles,v->BKConfig.data.tracerInvisibles=v);
                y=toggle(left,y,"Znajomi",()->BKConfig.data.tracerFriends,v->BKConfig.data.tracerFriends=v);
                y=toggle(left,y,"Ignoruj znajomych",()->BKConfig.data.tracerIgnoreFriends,v->BKConfig.data.tracerIgnoreFriends=v);
                y=toggle(left,y,"Nadzy gracze",()->BKConfig.data.tracerNakedPlayers,v->BKConfig.data.tracerNakedPlayers=v);
                y=cycle(left,y,"Grubość linii: "+BKConfig.data.tracerWidth,()->BKConfig.data.tracerWidth=BKConfig.data.tracerWidth>=4.5f?1.0f:BKConfig.data.tracerWidth+0.5f);
            }
            case "Hitboxes" -> {
                y=cycle(left,y,"Rozmiar: "+BKConfig.data.hitboxExpansion,()->BKConfig.data.hitboxExpansion=BKConfig.data.hitboxExpansion>=0.9f?0.1f:BKConfig.data.hitboxExpansion+0.1f);
                y=toggle(left,y,"Gracze",()->BKConfig.data.hitboxPlayers,v->BKConfig.data.hitboxPlayers=v);
                y=toggle(left,y,"Zwierzęta",()->BKConfig.data.hitboxAnimals,v->BKConfig.data.hitboxAnimals=v);
                y=toggle(left,y,"Moby",()->BKConfig.data.hitboxMobs,v->BKConfig.data.hitboxMobs=v);
                y=toggle(left,y,"Nadzy gracze",()->BKConfig.data.hitboxNakedPlayers,v->BKConfig.data.hitboxNakedPlayers=v);
                y=toggle(left,y,"Znajomi",()->BKConfig.data.hitboxFriends,v->BKConfig.data.hitboxFriends=v);
                y=toggle(left,y,"Niewidzialni",()->BKConfig.data.hitboxInvisibles,v->BKConfig.data.hitboxInvisibles=v);
            }
            case "Error Killer" -> {
                y=cycle(left,y,"Opóźnienie Cobweb: "+BKConfig.data.errorKillerCobwebDelay+" ms",()->BKConfig.data.errorKillerCobwebDelay=BKConfig.data.errorKillerCobwebDelay>=100?1:BKConfig.data.errorKillerCobwebDelay+1);
                y=cycle(left,y,"Opóźnienie Obsidian: "+BKConfig.data.errorKillerObsidianDelay+" ms",()->BKConfig.data.errorKillerObsidianDelay=BKConfig.data.errorKillerObsidianDelay>=100?1:BKConfig.data.errorKillerObsidianDelay+1);
            }
            case "Knockback Displacement" -> {
                y=toggle(left,y,"Tryb dynamiczny",()->BKConfig.data.knockbackDynamic,v->BKConfig.data.knockbackDynamic=v);
                y=toggle(left,y,"Tylko podczas sprintu",()->BKConfig.data.knockbackSprintOnly,v->BKConfig.data.knockbackSprintOnly=v);
                y=toggle(left,y,"Ukryj zmianę rotacji",()->BKConfig.data.knockbackHideRotation,v->BKConfig.data.knockbackHideRotation=v);
                y=cycle(left,y,"Kąt: "+BKConfig.data.knockbackAngle+"°",()->BKConfig.data.knockbackAngle=BKConfig.data.knockbackAngle>=180?-180:BKConfig.data.knockbackAngle+15);
                y=cycle(left,y,"Krok kąta: "+BKConfig.data.knockbackAngleStep+"°",()->BKConfig.data.knockbackAngleStep=BKConfig.data.knockbackAngleStep>=180?15:BKConfig.data.knockbackAngleStep+15);
            }
            case "Player ESP" -> {
                y=toggle(left,y,"Ramki",()->BKConfig.data.espBox,v->BKConfig.data.espBox=v);
                y=toggle(left,y,"Nazwa gracza",()->BKConfig.data.espName,v->BKConfig.data.espName=v);
                y=toggle(left,y,"Życie",()->BKConfig.data.espHealth,v->BKConfig.data.espHealth=v);
                y=cycle(left,y,"Zasięg: "+BKConfig.data.espDistance+" bloków",()->BKConfig.data.espDistance=BKConfig.data.espDistance>=192?8:BKConfig.data.espDistance+8);
            }
            case "Nametags" -> {
                y=toggle(left,y,"Tło etykiet",()->BKConfig.data.nametagBackground,v->BKConfig.data.nametagBackground=v);
                y=toggle(left,y,"Pokaż zbroję (ustawienie)",()->BKConfig.data.nametagArmor,v->BKConfig.data.nametagArmor=v);
                y=cycle(left,y,"Skala: "+BKConfig.data.nametagScale,()->BKConfig.data.nametagScale=BKConfig.data.nametagScale>=2?0.5f:BKConfig.data.nametagScale+0.1f);
            }
        }
        addDrawableChild(button(left+14,top+PANEL_H-32,402,20,"Zapisz ustawienia",b->{BKConfig.save();clearAndInit();}));
    }

    private int toggle(int left,int y,String label,java.util.function.BooleanSupplier get,java.util.function.Consumer<Boolean> set){
        addDrawableChild(button(left+14,y,402,22,label+": "+(get.getAsBoolean()?"WŁ.":"WYŁ."),b->{set.accept(!get.getAsBoolean());BKConfig.save();clearAndInit();}));
        return y+27;
    }
    private int cycle(int left,int y,String label,Runnable action){
        addDrawableChild(button(left+14,y,402,22,label,b->{action.run();BKConfig.save();clearAndInit();}));
        return y+27;
    }
    private ButtonWidget button(int x,int y,int w,int h,String label,ButtonWidget.PressAction action){
        return ButtonWidget.builder(Text.literal(label),action).dimensions(x,y,w,h).build();
    }
    private List<String> modulesForCategory(){
        if(category.equals("Existing")) return List.of("NoPush","Tracers","Hitboxes","Error Killer",
                "Knockback Displacement","Player ESP","Nametags");
        return BKModuleCatalog.EXTRA.stream().filter(e -> e.category().equals(category)).map(BKModuleCatalog.Entry::name).toList();
    }
    private String stateText(String module){
        return module+"  ["+(BKMod.isEnabled(module)?"WŁ.":"WYŁ.")+"]";
    }

    @Override public boolean keyPressed(int keyCode,int scanCode,int modifiers){
        if(waitingForBind!=null){
            if(keyCode==256) BKModuleKeys.setBind(waitingForBind,0);
            else BKModuleKeys.setBind(waitingForBind,keyCode);
            waitingForBind=null; clearAndInit(); return true;
        }
        if(keyCode==256){close();return true;}
        return super.keyPressed(keyCode,scanCode,modifiers);
    }

    @Override public void render(DrawContext ctx,int mouseX,int mouseY,float delta){
        renderBackground(ctx,mouseX,mouseY,delta);
        int left=width/2-PANEL_W/2,top=height/2-PANEL_H/2;
        ctx.fill(left,top,left+PANEL_W,top+PANEL_H,0xF0161B26);
        ctx.fill(left,top,left+PANEL_W,top+2,0xFF9A5DFF);
        ctx.drawTextWithShadow(textRenderer,Text.literal("BK CLIENT  •  CLICKGUI"),left+14,top+13,0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer,Text.literal(selectedModule==null?"Wybierz kategorię; każdy moduł ma osobny bind.":(waitingForBind==null?"Kliknij bind, aby go zmienić.":"Naciśnij nowy klawisz…")),left+14,top+22,0xFFADB7C8);
        super.render(ctx,mouseX,mouseY,delta);
    }
    @Override public boolean shouldPause(){return false;}
}
