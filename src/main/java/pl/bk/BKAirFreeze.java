package pl.bk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public final class BKAirFreeze {
    private BKAirFreeze() {}

    public static void setEnabled(MinecraftClient client, boolean enabled) {
        BKConfig.data.airFreeze = enabled;
        BKConfig.save();
        if (client.player != null) {
            client.player.sendMessage(Text.literal(enabled
                    ? "§aAir Freeze: ON"
                    : "§cAir Freeze: OFF"), true);
        }
    }

    public static void tick(MinecraftClient client) {
        if (!BKConfig.data.airFreeze || client.player == null || client.world == null) return;
        client.player.setVelocity(Vec3d.ZERO);
        client.player.setOnGround(true);
        client.player.fallDistance = 0.0f;
    }
}
