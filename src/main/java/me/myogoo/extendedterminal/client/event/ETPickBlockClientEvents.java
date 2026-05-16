package me.myogoo.extendedterminal.client.event;

import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.network.serverbound.ETPickBlockPacket;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.wt.AE2WTLib;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExtendedTerminal.MODID, value = Dist.CLIENT)
public final class ETPickBlockClientEvents {
    private ETPickBlockClientEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void pickBlock(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isPickBlock() || event.isCanceled()) {
            return;
        }
        if (!MyotusAPI.modIntegrationManager().isLoaded(AE2WTLib.class)) {
            return;
        }

        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;
        var hitResult = minecraft.hitResult;
        if (player == null || level == null || hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }
        if (player.getAbilities().instabuild || player.isSpectator()) {
            return;
        }

        var blockPos = ((BlockHitResult) hitResult).getBlockPos();
        var blockState = level.getBlockState(blockPos);
        if (blockState.isAir()) {
            return;
        }

        ItemStack stack = blockState.getCloneItemStack(hitResult, level, blockPos, player);
        if (stack.isEmpty() || player.getInventory().findSlotMatchingItem(stack) != -1) {
            return;
        }

        MyotusAPI.network().sendToServer(new ETPickBlockPacket(stack));
    }
}
