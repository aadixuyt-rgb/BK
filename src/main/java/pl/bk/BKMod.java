package pl.bk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;

public final class BKMod implements ClientModInitializer {
    @Override public void onInitializeClient() {
        BKConfig.load();
        BKModuleKeys.register();
        WorldRenderEvents.END.register(BKTracers::capture);
        HudRenderCallback.EVENT.register((context, tickCounter) -> BKTracers.render(context));
        ClientTickEvents.END_CLIENT_TICK.register(BKMod::tick);
    }
    private static void tick(MinecraftClient client) {
        if(BKModuleKeys.openGui.wasPressed() && client.currentScreen==null) client.setScreen(new BKClickGui());
        while(BKModuleKeys.airFreeze.wasPressed()) {
            BKAirFreeze.setEnabled(client, !BKConfig.data.airFreeze);
        }
        BKAirFreeze.tick(client);
    }
    public static void toggleAirFreeze() {
        MinecraftClient client = MinecraftClient.getInstance();
        BKAirFreeze.setEnabled(client, !BKConfig.data.airFreeze);
    }
    public static void toggleNoPush() {
        BKConfig.data.noPush=!BKConfig.data.noPush;
        BKConfig.save();
    }
}
