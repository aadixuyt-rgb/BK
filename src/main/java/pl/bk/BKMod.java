package pl.bk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public final class BKMod implements ClientModInitializer {
    @Override public void onInitializeClient() {
        BKConfig.load();
        BKModuleKeys.register();
        ClientTickEvents.END_CLIENT_TICK.register(BKMod::tick);
        HudRenderCallback.EVENT.register((context,tickCounter)->BKVisuals.render(context,tickCounter));
    }
    private static void tick(MinecraftClient client) {
        if(BKModuleKeys.openGui.wasPressed() && client.currentScreen==null) client.setScreen(new BKClickGui());
        while(BKModuleKeys.noPush.wasPressed()) toggle("NoPush");
        while(BKModuleKeys.tracers.wasPressed()) toggle("Tracers");
        while(BKModuleKeys.hitboxes.wasPressed()) toggle("Hitboxes");
    }
    public static boolean isEnabled(String module) {
        return switch(module) {
            case "NoPush" -> BKConfig.data.noPush;
            case "Tracers" -> BKConfig.data.tracers;
            case "Hitboxes" -> BKConfig.data.hitboxes;
            default -> false;
        };
    }
    public static void toggle(String module) {
        switch(module) {
            case "NoPush" -> BKConfig.data.noPush=!BKConfig.data.noPush;
            case "Tracers" -> BKConfig.data.tracers=!BKConfig.data.tracers;
            case "Hitboxes" -> BKConfig.data.hitboxes=!BKConfig.data.hitboxes;
            default -> { return; }
        }
        BKConfig.save();
    }
}
