package pl.bk;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public final class BKAirFreeze {
    private static String notice = "";
    private static long noticeUntil = 0L;

    private BKAirFreeze() {}

    public static void setEnabled(boolean enabled) {
        BKConfig.data.airFreeze = enabled;
        BKConfig.save();
        notice = enabled ? "Air Freeze: WLACZONY" : "Air Freeze: WYLACZONY";
        noticeUntil = System.currentTimeMillis() + 2000L;
    }

    public static void renderNotice(net.minecraft.client.gui.DrawContext context, MinecraftClient client) {
        if (client.player == null || System.currentTimeMillis() > noticeUntil || notice.isEmpty()) return;
        int color = BKConfig.data.airFreeze ? 0xFF55FF77 : 0xFFFF7777;
        context.drawCenteredTextWithShadow(
            client.textRenderer,
            Text.literal(notice),
            context.getScaledWindowWidth() / 2,
            context.getScaledWindowHeight() / 2 + 30,
            color
        );
    }

    public static void tick(MinecraftClient client) {
        if (!BKConfig.data.airFreeze || client.player == null || client.world == null) return;
        // Cancel the player's motion each client tick to prevent normal falling.
        client.player.setVelocity(Vec3d.ZERO);
        client.player.fallDistance = 0.0f;
    }
}
