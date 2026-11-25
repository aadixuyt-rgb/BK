package pl.bk;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class BKModuleKeys {
    public static KeyBinding openGui,noPush;
    private BKModuleKeys() {}
    public static void register() {
        openGui=register("key.bk.open_gui",BKConfig.data.guiKey);
        noPush=register("key.bk.air_freeze",BKConfig.data.noPushKey);
    }
    private static KeyBinding register(String id,int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id,InputUtil.Type.KEYSYM,key,"category.bk.client"));
    }
    public static void setNoPushBind(int code) {
        BKConfig.data.noPushKey=code;
        noPush.setBoundKey(InputUtil.fromKeyCode(code,0));
        KeyBinding.updateKeysByCode();
        BKConfig.save();
    }
}