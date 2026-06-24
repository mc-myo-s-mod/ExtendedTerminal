package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import com.google.common.primitives.Ints;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.myotus.api.network.IMyotusPacket;
import me.myogoo.myotus.api.network.MyoPacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ETFillPendingCraftingSlotPacket implements IMyotusPacket {
    private final int slotIndex;
    private final AEKey what;

    public ETFillPendingCraftingSlotPacket(FriendlyByteBuf stream) {
        this.slotIndex = stream.readVarInt();
        this.what = AEKey.readKey(stream);
    }

    public ETFillPendingCraftingSlotPacket(int slotIndex, AEKey what) {
        this.slotIndex = slotIndex;
        this.what = what;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeVarInt(slotIndex);
        AEKey.writeKey(stream, what);
    }

    public static void handle(ETFillPendingCraftingSlotPacket packet, MyoPacketContext context) {
        var player = context.sender();
        if (player != null) {
            packet.handleOnServer(player);
        }
    }

    private void handleOnServer(ServerPlayer player) {
        if (!(player.containerMenu instanceof ETTerminalBaseMenu<?> menu) || !(what instanceof AEItemKey itemKey)) {
            return;
        }

        var slots = menu.getSlots(menu.getCraftingGridSlotSemantic());
        if (slotIndex < 0 || slotIndex >= slots.size() || slots.get(slotIndex).hasItem()) {
            return;
        }

        var node = menu.getNetworkNode();
        if (node == null) {
            return;
        }

        var extracted = StorageHelper.poweredExtraction(
                node.getGrid().getEnergyService(),
                node.getGrid().getStorageService().getInventory(),
                itemKey,
                1,
                menu.getActionSource());
        if (extracted <= 0) {
            return;
        }

        menu.getCraftingMatrix().setItemDirect(slotIndex, itemKey.toStack(Ints.saturatedCast(extracted)));
        menu.slotsChanged(menu.getCraftingMatrix().toContainer());
        menu.broadcastChanges();
    }
}
