package pl.bk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public final class BKMod implements ClientModInitializer {
    @Override public void onInitializeClient() {
        BKConfig.load();
        BKModuleKeys.register();
        ClientTickEvents.END_CLIENT_TICK.register(BKMod::tick);
    }
    private static void tick(MinecraftClient client) {
        if(BKModuleKeys.openGui.wasPressed() && client.currentScreen==null) client.setScreen(new BKClickGui());
        while(BKModuleKeys.noPush.wasPressed()) toggleNoPush();
    }
    public static void toggleNoPush() {
        BKConfig.data.noPush=!BKConfig.data.noPush;
        BKConfig.save();
    }
}