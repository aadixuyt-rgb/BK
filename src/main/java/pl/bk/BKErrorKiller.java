package pl.bk;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CobwebBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.MinecraftClient;

/** Adapted Error Killer interaction helper, with separately configurable delays. */
public final class BKErrorKiller {
    private enum Mode { COBWEB, OBSIDIAN }
    private static int step=-1, originalSlot=-1;
    private static long lastStepTime;
    private static boolean lastRightClickPressed, moveFixSprint, useOriginalSlotForPlace;
    private static BlockHitResult target;
    private static Mode mode=Mode.COBWEB;
    private BKErrorKiller(){}

    public static void tick(MinecraftClient mc){
        if(!BKConfig.data.errorKiller){lastRightClickPressed=false;reset(mc,true);return;}
        if(mc.player==null||mc.world==null||mc.interactionManager==null||mc.getNetworkHandler()==null){
            lastRightClickPressed=false;reset(mc,true);return;
        }
        if(mc.currentScreen!=null){lastRightClickPressed=false;return;}
        if(mc.world.getBlockState(mc.player.getBlockPos().up()).getBlock() instanceof CobwebBlock){
            lastRightClickPressed=false;reset(mc,true);return;
        }
        if(step==-1){detectTrigger(mc);return;}
        applyMoveFix(mc);runMacro(mc);
    }

    private static void detectTrigger(MinecraftClient mc){
        boolean right=GLFW.glfwGetMouseButton(mc.getWindow().getHandle(),GLFW.GLFW_MOUSE_BUTTON_RIGHT)==GLFW.GLFW_PRESS;
        ItemStack held=mc.player.getMainHandStack();
        HitResult hit=mc.crosshairTarget;
        if(right&&!lastRightClickPressed&&validItem(held,mc)&&hit instanceof BlockHitResult blockHit&&isTarget(mc,blockHit)){
            originalSlot=mc.player.getInventory().selectedSlot;
            target=normalize(mc,blockHit);
            mode=held.isOf(Items.COBWEB)?Mode.COBWEB:Mode.OBSIDIAN;
            useOriginalSlotForPlace=mode==Mode.OBSIDIAN;
            moveFixSprint=mc.player.isSprinting()||mc.options.sprintKey.isPressed();
            step=0;
        }
        lastRightClickPressed=right;
    }

    private static boolean validItem(ItemStack stack,MinecraftClient mc){
        if(stack.isEmpty())return false;
        if(stack.isOf(Items.COBWEB))return true;
        Item item=stack.getItem();
        if(!(item instanceof BlockItem blockItem))return false;
        BlockState state=blockItem.getBlock().getDefaultState();
        return state.isFullCube((BlockView)mc.world,BlockPos.ORIGIN);
    }
    private static boolean isTarget(MinecraftClient mc,BlockHitResult hit){
        BlockPos pos=hit.getBlockPos();
        return trigger(mc.world.getBlockState(pos).getBlock())||
                trigger(mc.world.getBlockState(pos.offset(hit.getSide())).getBlock());
    }
    private static boolean trigger(Block block){return block instanceof LeverBlock||block instanceof ButtonBlock;}
    private static BlockHitResult normalize(MinecraftClient mc,BlockHitResult hit){
        BlockPos pos=hit.getBlockPos();
        if(trigger(mc.world.getBlockState(pos).getBlock())){
            BlockPos support=pos.offset(hit.getSide().getOpposite());
            if(!mc.world.getBlockState(support).isAir())
                return new BlockHitResult(hit.getPos(),hit.getSide(),support,hit.isInsideBlock());
        }
        return hit;
    }

    private static void runMacro(MinecraftClient mc){
        PlayerInventory inv=mc.player.getInventory();
        int fluid=-1,empty=-1,place=-1;
        Item placeItem=mode==Mode.COBWEB?Items.COBWEB:Items.OBSIDIAN;
        for(int slot=0;slot<9;slot++){
            ItemStack stack=inv.getStack(slot);
            if(stack.isOf(Items.WATER_BUCKET)||stack.isOf(Items.LAVA_BUCKET))fluid=slot;
            if(stack.isOf(Items.BUCKET))empty=slot;
            if(stack.isOf(placeItem))place=slot;
        }
        if(useOriginalSlotForPlace&&originalSlot>=0&&originalSlot<=8)place=originalSlot;
        int delay=mode==Mode.OBSIDIAN?BKConfig.data.errorKillerObsidianDelay:BKConfig.data.errorKillerCobwebDelay;
        BlockHitResult placeTarget=target;
        if(placeTarget==null&&mc.crosshairTarget instanceof BlockHitResult current)placeTarget=normalize(mc,current);
        if(delay<=1){
            if(fluid!=-1&&place!=-1&&placeTarget!=null){
                use(mc,fluid);
                int pickup=fluid!=-1?fluid:empty;
                if(pickup!=-1)use(mc,pickup);
                place(mc,place,placeTarget);
            }
            reset(mc,true);return;
        }
        long now=System.currentTimeMillis();
        switch(step){
            case 0->{if(fluid==-1){reset(mc,true);return;}use(mc,fluid);lastStepTime=now;step=1;}
            case 1->{if(now-lastStepTime>=delay)step=2;}
            case 2->{int pickup=fluid!=-1?fluid:empty;if(pickup!=-1)use(mc,pickup);step=3;}
            case 3->{if(place!=-1&&placeTarget!=null)place(mc,place,placeTarget);reset(mc,true);}
            default->reset(mc,true);
        }
    }
    private static void use(MinecraftClient mc,int slot){select(mc,slot);mc.interactionManager.interactItem(mc.player,Hand.MAIN_HAND);applyMoveFix(mc);}
    private static void place(MinecraftClient mc,int slot,BlockHitResult hit){select(mc,slot);mc.interactionManager.interactBlock(mc.player,Hand.MAIN_HAND,hit);applyMoveFix(mc);}
    private static void select(MinecraftClient mc,int slot){
        if(slot<0||slot>8||mc.player==null||mc.getNetworkHandler()==null)return;
        mc.player.getInventory().setSelectedSlot(slot);
        mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
    }
    private static void applyMoveFix(MinecraftClient mc){
        if(moveFixSprint&&mc.player!=null&&!mc.options.sneakKey.isPressed()&&mc.options.forwardKey.isPressed())
            mc.player.setSprinting(true);
    }
    private static void reset(MinecraftClient mc,boolean restore){
        if(restore&&originalSlot!=-1&&mc.player!=null)select(mc,originalSlot);
        step=-1;target=null;originalSlot=-1;mode=Mode.COBWEB;useOriginalSlotForPlace=false;moveFixSprint=false;
    }
}
