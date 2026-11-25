package pl.bk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

/**
 * Client-side Air Freeze based on the supplied prototype.
 * The server remains authoritative and may still correct player movement.
 */
public final class BKAirFreeze {
    private BKAirFreeze() {}

    public static void tick(MinecraftClient client) {
        if (!BKConfig.data.airFreeze || client.player == null || client.world == null) return;

        // Do not pin the player to saved coordinates: accepted teleports stay valid.
        client.player.setVelocity(Vec3d.ZERO);
        client.player.setOnGround(true);
        client.player.fallDistance = 0.0f;
    }
}
