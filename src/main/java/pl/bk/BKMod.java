package pl.bk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

public final class BKMod implements ClientModInitializer {
    @Override public void onInitializeClient(){
        BKConfig.load(); BKModuleKeys.register();
        ClientTickEvents.END_CLIENT_TICK.register(BKMod::tick);
        HudRenderCallback.EVENT.register((context,tickCounter)->BKVisuals.render(context,tickCounter));
    }
    private static void tick(MinecraftClient client){
        if(BKModuleKeys.openGui.wasPressed()&&client.currentScreen==null)client.setScreen(new BKClickGui());
        while(BKModuleKeys.noPush.wasPressed())toggle("NoPush");
        while(BKModuleKeys.tracers.wasPressed())toggle("Tracers");
        while(BKModuleKeys.hitboxes.wasPressed())toggle("Hitboxes");
        while(BKModuleKeys.errorKiller.wasPressed())toggle("Error Killer");
        while(BKModuleKeys.knockback.wasPressed())toggle("Knockback Displacement");
        while(BKModuleKeys.playerEsp.wasPressed())toggle("Player ESP");
        while(BKModuleKeys.nametags.wasPressed())toggle("Nametags");
        for (BKModuleCatalog.Entry entry : BKModuleCatalog.EXTRA) {
            net.minecraft.client.option.KeyBinding binding = BKModuleKeys.byModule(entry.name());
            if (binding != null) while (binding.wasPressed()) toggle(entry.name());
        }
        BKErrorKiller.tick(client);
        BKKnockbackDisplacement.tick(client);
    }
    public static boolean isEnabled(String module){
        return switch(module){
            case "NoPush"->BKConfig.data.noPush; case "Tracers"->BKConfig.data.tracers;
            case "Hitboxes"->BKConfig.data.hitboxes; case "Error Killer"->BKConfig.data.errorKiller;
            case "Knockback Displacement"->BKConfig.data.knockbackDisplacement;
            case "Player ESP"->BKConfig.data.playerEsp; case "Nametags"->BKConfig.data.nametagsEnabled;
            default->BKModuleCatalog.EXTRA.stream().anyMatch(e -> e.name().equals(module) && BKExtraModules.isEnabled(module));
        };
    }
    public static void toggle(String module){
        switch(module){
            case "NoPush"->BKConfig.data.noPush=!BKConfig.data.noPush;
            case "Tracers"->BKConfig.data.tracers=!BKConfig.data.tracers;
            case "Hitboxes"->BKConfig.data.hitboxes=!BKConfig.data.hitboxes;
            case "Error Killer"->BKConfig.data.errorKiller=!BKConfig.data.errorKiller;
            case "Knockback Displacement"->BKConfig.data.knockbackDisplacement=!BKConfig.data.knockbackDisplacement;
            case "Player ESP"->BKConfig.data.playerEsp=!BKConfig.data.playerEsp;
            case "Nametags"->BKConfig.data.nametagsEnabled=!BKConfig.data.nametagsEnabled;
            default->{return;}
        } BKConfig.save();
    }
}
