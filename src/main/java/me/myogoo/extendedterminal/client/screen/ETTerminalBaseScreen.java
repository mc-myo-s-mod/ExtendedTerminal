package me.myogoo.extendedterminal.client.screen;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.core.AEConfig;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantic;
import com.mojang.blaze3d.platform.InputConstants;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.ExCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ETTerminalBaseScreen<R extends Recipe<?>,T extends ETTerminalBaseMenu<R>> extends MEStorageScreen<T> {
    public ETTerminalBaseScreen(T menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        ActionButton clearBtn = new ActionButton(ActionItems.STASH, btn -> menu.clearCraftingGrid());
        clearBtn.setHalfSize(true);
        widgets.add("clearCraftingGrid", clearBtn);

        ActionButton clearToPlayerInvBtn = new ActionButton(ActionItems.STASH_TO_PLAYER_INV,
                btn -> menu.clearToPlayerInventory());
        clearToPlayerInvBtn.setHalfSize(true);
        widgets.add("clearToPlayerInv",clearToPlayerInvBtn);
    }

    @Override
    public void onClose() {
        if(AEConfig.instance().isClearGridOnClose()) {
            this.getMenu().clearCraftingGrid();
        }
        super.onClose();
    }

    @Override
    protected void slotClicked(Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        if (slot instanceof ETCraftingBaseSlot) {
            InventoryAction action;
            if (hasShiftDown()) {
                action = InventoryAction.CRAFT_SHIFT;
            } else if (InputConstants.isKeyDown(getMinecraft().getWindow().getWindow(), GLFW.GLFW_KEY_SPACE)) {
                action = InventoryAction.CRAFT_ALL;
            } else {
                action = mouseButton == 1 ? InventoryAction.CRAFT_STACK : InventoryAction.CRAFT_ITEM;
            }

            final InventoryActionPacket p = new InventoryActionPacket(action, slotIdx, 0);
            NetworkHandler.instance().sendToServer(p);
            return;
        }
        super.slotClicked(slot, slotIdx, mouseButton, clickType);
    }

    @Override
    public void init() {
        super.init();
        ETMenuType menuType = this.getMenu().getETMenuType();
        drawCraftingSlot(menuType.getSlotSemanticGrid(),menuType.getGridSideLength());
    }

    protected void drawCraftingSlot(SlotSemantic slotSemantics, int sideLength) {
        List<Slot> craftingSlots = this.getMenu().getSlots(slotSemantics);
        Slot firstSlot = craftingSlots.get(0);

        int craftGridStartX = firstSlot.x;
        int craftGridStartY = firstSlot.y;

        for(int row = 0; row < sideLength; row++) {
            for(int col = 0; col < sideLength; col++) {
                int index = row * sideLength + col;
                Slot slot = craftingSlots.get(index);
                int x = craftGridStartX + col * 18;
                int y = craftGridStartY + row * 18;
                slot.x = x;
                slot.y = y;
            }
        }
    }
}
