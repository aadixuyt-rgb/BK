package pl.bk;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class BKModuleKeys {
    public static KeyBinding openGui,airFreeze;
    private BKModuleKeys() {}
    public static void register() {
        openGui=register("key.bk.open_gui",BKConfig.data.guiKey);
        airFreeze=register("key.bk.air_freeze",BKConfig.data.airFreezeKey);
    }
    private static KeyBinding register(String id,int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(id,InputUtil.Type.KEYSYM,key,"category.bk.client"));
    }
    public static void setAirFreezeBind(int code) {
        BKConfig.data.airFreezeKey=code;
        airFreeze.setBoundKey(InputUtil.fromKeyCode(code,0));
        KeyBinding.updateKeysByCode();
        BKConfig.save();
    }
}