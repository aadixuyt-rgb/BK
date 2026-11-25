package pl.bk;

import net.minecraft.block.BlockState;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class BKDripstone {
    private static long lastPlacementNanos=0L;
    private static int restoreSlot=-1;
    private BKDripstone() {}

    public static void tick(MinecraftClient client) {
        if(client.player==null || client.world==null || client.interactionManager==null || client.getNetworkHandler()==null) {
            restoreSlot=-1;
            return;
        }
        if(restoreSlot>=0) {
            int slot=restoreSlot;
            restoreSlot=-1;
            client.player.getInventory().selectedSlot=slot;
            client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
        if(client.currentScreen!=null) return;
        long now=System.nanoTime();
        long delay=Math.max(50,Math.min(2000,BKConfig.data.dripstoneDelayMs))*1_000_000L;
        if(now-lastPlacementNanos<delay) return;
        if(!(client.crosshairTarget instanceof BlockHitResult hit)) return;

        BlockPos dripstonePos=hit.getBlockPos();
        BlockState dripstoneState=client.world.getBlockState(dripstonePos);
        if(!dripstoneState.isOf(Blocks.POINTED_DRIPSTONE)) return;
        if(!dripstoneState.contains(PointedDripstoneBlock.VERTICAL_DIRECTION)) return;
        Direction vertical=dripstoneState.get(PointedDripstoneBlock.VERTICAL_DIRECTION);
        BlockPos placePos=dripstonePos.down();
        if(!client.world.getBlockState(placePos).isAir()) return;
        BlockPos supportPos=placePos.offset(vertical.getOpposite());
        if(client.world.getBlockState(supportPos).isAir()) return;

        int dripstoneSlot=findDripstone(client);
        if(dripstoneSlot<0) return;
        int oldSlot=client.player.getInventory().selectedSlot;
        if(oldSlot!=dripstoneSlot) {
            client.player.getInventory().selectedSlot=dripstoneSlot;
            client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(dripstoneSlot));
            restoreSlot=oldSlot;
        }

        Vec3d hitPos=Vec3d.ofCenter(placePos).add(
            vertical.getOffsetX()*0.5,
            vertical.getOffsetY()*0.5,
            vertical.getOffsetZ()*0.5
        );
        BlockHitResult placementHit=new BlockHitResult(hitPos,vertical,placePos,false);
        client.interactionManager.interactBlock(client.player,Hand.MAIN_HAND,placementHit);
        lastPlacementNanos=now;
    }

    private static int findDripstone(MinecraftClient client) {
        for(int i=0;i<9;i++) {
            ItemStack stack=client.player.getInventory().getStack(i);
            if(stack.isOf(Items.POINTED_DRIPSTONE)) return i;
        }
        return -1;
    }
}