package pl.bk;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

public final class BKMod implements ClientModInitializer {
    private static boolean jumpWasAirborne = false;
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
        if (client.player == null || client.world == null) {
            jumpWasAirborne = false;
        } else {
            boolean airborne = !client.player.isOnGround() && !client.player.isClimbing()
                    && !client.player.isSubmergedInWater();
            if (BKExtraModules.isEnabled("Jump Circles") && airborne && !jumpWasAirborne) {
                var origin = client.player.getPos();
                var settings = BKExtraModules.state("Jump Circles").settings;
                double radius = parseSetting(settings.get("radius"), 0.9, 0.5, 1.8);
                int points = (int) parseSetting(settings.get("points"), 24, 12, 48);
                for (int i = 0; i < points; i++) {
                    double angle = Math.PI * 2.0 * i / points;
                    double dx = Math.cos(angle), dz = Math.sin(angle);
                    client.world.addParticle(ParticleTypes.END_ROD,
                            origin.x + dx * radius, origin.y + 0.04, origin.z + dz * radius,
                            dx * 0.018, 0.012, dz * 0.018);
                }
            }
            jumpWasAirborne = airborne;
        }
        if (BKExtraModules.isEnabled("Blizzard") && client.player != null && client.world != null
                && client.player.age % 4 == 0) {
            var p = client.player.getPos();
            for (int i = 0; i < 10; i++) {
                double angle = Math.random() * Math.PI * 2.0;
                double radius = 2.0 + Math.random() * 4.0;
                double x = p.x + Math.cos(angle) * radius;
                double y = p.y + 1.0 + Math.random() * 3.0;
                double z = p.z + Math.sin(angle) * radius;
                client.world.addParticle(ParticleTypes.SNOWFLAKE, x, y, z,
                        -Math.cos(angle) * 0.025, -0.035, -Math.sin(angle) * 0.025);
            }
        }
        if (BKExtraModules.isEnabled("Orbit Particles") && client.player != null && client.world != null
                && client.player.age % 2 == 0) {
            double phase = client.player.age * 0.16;
            for (int i = 0; i < 3; i++) {
                double angle = phase + i * (Math.PI * 2.0 / 3.0);
                double x = client.player.getX() + Math.cos(angle) * 1.15;
                double y = client.player.getY() + 0.8 + Math.sin(phase * 0.7 + i) * 0.25;
                double z = client.player.getZ() + Math.sin(angle) * 1.15;
                client.world.addParticle(ParticleTypes.END_ROD, x, y, z, 0.0, 0.015, 0.0);
            }
        }
        if (client.world != null) {
            boolean chams = BKExtraModules.isEnabled("Chams");
            for (var entity : client.world.getPlayers()) {
                if (entity != client.player) {
                    // Client-side glowing outline; restore the flag when Chams is disabled.
                    entity.setGlowing(chams);
                }
            }
        }
        BKKnockbackDisplacement.tick(client);
    }
    private static double parseSetting(String value, double fallback, double min, double max) {
        try { return Math.max(min, Math.min(max, Double.parseDouble(value))); }
        catch (RuntimeException ignored) { return fallback; }
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
            default->{ if (BKModuleCatalog.EXTRA.stream().anyMatch(e -> e.name().equals(module))) { BKExtraModules.toggle(module); return; } return; }
        } BKConfig.save();
    }
}
