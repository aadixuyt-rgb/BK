package pl.bk;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class BKModuleKeys {
    public static KeyBinding openGui,noPush,tracers,hitboxes;
    private BKModuleKeys() {}
    public static void register() {
        openGui=register("key.bk.open_gui",BKConfig.data.guiKey);
        noPush=register("key.bk.no_push",BKConfig.data.noPushKey);
        tracers=register("key.bk.tracers",BKConfig.data.tracersKey);
        hitboxes=register("key.bk.hitboxes",BKConfig.data.hitboxesKey);
    }
    private static KeyBinding register(String id,int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id,InputUtil.Type.KEYSYM,key,"category.bk.client"));
    }
    public static KeyBinding byModule(String module) {
        return switch(module) {
            case "BK GUI" -> openGui;
            case "NoPush" -> noPush;
            case "Tracers" -> tracers;
            case "Hitboxes" -> hitboxes;
            default -> null;
        };
    }
    public static void setBind(String module,int code) {
        switch(module) {
            case "BK GUI" -> BKConfig.data.guiKey=code;
            case "NoPush" -> BKConfig.data.noPushKey=code;
            case "Tracers" -> BKConfig.data.tracersKey=code;
            case "Hitboxes" -> BKConfig.data.hitboxesKey=code;
            default -> { return; }
        }
        KeyBinding binding=byModule(module);
        if(binding!=null) {
            binding.setBoundKey(InputUtil.fromKeyCode(code,0));
            KeyBinding.updateKeysByCode();
        }
        BKConfig.save();
    }
}
