package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.MEStorage;
import appeng.me.helpers.PlayerSource;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.network.IMyotusPacket;
import me.myogoo.myotus.api.network.MyoPacketContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundSetCarriedItemPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class ETPickBlockPacket implements IMyotusPacket {
    private final ItemStack itemStack;

    public ETPickBlockPacket(FriendlyByteBuf stream) {
        this.itemStack = stream.readItem();
    }

    public ETPickBlockPacket(ItemStack itemStack) {
        this.itemStack = itemStack.copy();
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        stream.writeItem(this.itemStack);
    }

    public static void handle(ETPickBlockPacket packet, MyoPacketContext context) {
        var player = context.sender();
        if (player == null) {
            return;
        }

        ServerHandler.pickBlock(player, packet.itemStack);
    }

    private static final class ServerHandler {
        private ServerHandler() {
        }

        private static void pickBlock(ServerPlayer player, ItemStack requestedStack) {
            if (player.isCreative() || player.isSpectator() || requestedStack.isEmpty()) {
                return;
            }

            var terminalHandler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
            if (!terminalHandler.inRange()) {
                return;
            }

            var terminal = terminalHandler.getCraftingTerminal();
            if (!ItemWT.getBoolean(terminal, ETWTMenu.PICK_BLOCK)) {
                return;
            }

            var grid = terminalHandler.getTargetGrid();
            if (grid == null || grid.getStorageService() == null) {
                return;
            }

            var what = AEItemKey.of(requestedStack);
            if (what == null) {
                return;
            }

            var networkInventory = grid.getStorageService().getInventory();
            var playerSource = new PlayerSource(player, null);
            var inventory = player.getInventory();
            var targetSlot = inventory.getSuitableHotbarSlot();
            var toReplace = inventory.getItem(targetSlot);

            if (!canStoreReplacement(networkInventory, toReplace, playerSource)) {
                return;
            }

            var extracted = networkInventory.extract(what, requestedStack.getMaxStackSize(), Actionable.SIMULATE,
                    playerSource);
            if (extracted == 0) {
                return;
            }

            if (!storeReplacement(networkInventory, inventory, targetSlot, toReplace, playerSource)) {
                return;
            }

            extracted = networkInventory.extract(what, requestedStack.getMaxStackSize(), Actionable.MODULATE,
                    playerSource);
            if (extracted == 0) {
                inventory.setItem(targetSlot, ItemStack.EMPTY);
                return;
            }

            var pickedStack = requestedStack.copy();
            pickedStack.setCount((int) extracted);
            inventory.setItem(targetSlot, pickedStack);
            inventory.selected = targetSlot;
            player.connection.send(new ClientboundSetCarriedItemPacket(inventory.selected));
        }

        private static boolean canStoreReplacement(MEStorage networkInventory, ItemStack stack, PlayerSource source) {
            if (stack.isEmpty()) {
                return true;
            }

            var key = AEItemKey.of(stack);
            if (key == null) {
                return false;
            }

            var inserted = networkInventory.insert(key, stack.getCount(), Actionable.SIMULATE, source);
            return inserted == stack.getCount();
        }

        private static boolean storeReplacement(MEStorage networkInventory, Inventory inventory, int slot,
                ItemStack stack, PlayerSource source) {
            if (stack.isEmpty()) {
                return true;
            }

            var key = AEItemKey.of(stack);
            if (key == null) {
                return false;
            }

            var inserted = networkInventory.insert(key, stack.getCount(), Actionable.MODULATE, source);
            if (inserted == stack.getCount()) {
                return true;
            }

            var remaining = stack.copy();
            remaining.shrink((int) inserted);
            inventory.setItem(slot, remaining);
            return false;
        }
    }
}
