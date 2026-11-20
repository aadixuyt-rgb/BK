package pl.bk;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import java.util.LinkedHashMap;
import java.util.Map;

/** Every module gets an independent configurable key binding. */
public final class BKModuleKeys {
    public static KeyBinding openGui,noPush,tracers,hitboxes,errorKiller,knockback,playerEsp,nametags;
    private static final Map<String,KeyBinding> EXTRA = new LinkedHashMap<>();
    private BKModuleKeys(){}
    public static void register(){
        openGui=register("key.bk.open_gui",BKConfig.data.guiKey);
        noPush=register("key.bk.no_push",BKConfig.data.noPushKey);
        tracers=register("key.bk.tracers",BKConfig.data.tracersKey);
        hitboxes=register("key.bk.hitboxes",BKConfig.data.hitboxesKey);
        errorKiller=register("key.bk.error_killer",BKConfig.data.errorKillerKey);
        knockback=register("key.bk.knockback_displacement",BKConfig.data.knockbackKey);
        playerEsp=register("key.bk.player_esp",BKConfig.data.playerEspKey);
        nametags=register("key.bk.nametags",BKConfig.data.nametagsKey);
        for (BKModuleCatalog.Entry entry : BKModuleCatalog.EXTRA) {
            String id = entry.name().toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
            EXTRA.put(entry.name(), register("key.bk." + id, BKExtraModules.state(entry.name()).key));
        }
    }
    private static KeyBinding register(String id,int key){
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id,InputUtil.Type.KEYSYM,key,"category.bk.client"));
    }
    public static KeyBinding byModule(String module){return switch(module){
        case "BK GUI"->openGui; case "NoPush"->noPush; case "Tracers"->tracers; case "Hitboxes"->hitboxes;
        case "Error Killer"->errorKiller; case "Knockback Displacement"->knockback; case "Player ESP"->playerEsp;
        case "Nametags"->nametags; default->EXTRA.get(module);};}
    public static void setBind(String module,int code){
        switch(module){
            case "BK GUI"->BKConfig.data.guiKey=code; case "NoPush"->BKConfig.data.noPushKey=code;
            case "Tracers"->BKConfig.data.tracersKey=code; case "Hitboxes"->BKConfig.data.hitboxesKey=code;
            case "Error Killer"->BKConfig.data.errorKillerKey=code; case "Knockback Displacement"->BKConfig.data.knockbackKey=code;
            case "Player ESP"->BKConfig.data.playerEspKey=code; case "Nametags"->BKConfig.data.nametagsKey=code;
            default->{ if (BKModuleCatalog.EXTRA.stream().anyMatch(e -> e.name().equals(module))) BKExtraModules.state(module).key=code; else return; }
        }
        KeyBinding b=byModule(module);
        if(b!=null){b.setBoundKey(InputUtil.fromKeyCode(code,0));KeyBinding.updateKeysByCode();}
        BKConfig.save();
    }
}
