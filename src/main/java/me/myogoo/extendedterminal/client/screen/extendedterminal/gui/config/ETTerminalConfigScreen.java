package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.config;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ETTerminalConfigScreen implements MyoConfigTabScreen {
    private ETWTMenu menu;
    private AECheckbox pickBlock;
    private AECheckbox restock;
    private AECheckbox magnet;
    private AECheckbox pickupToME;

    @Override
    public void buildTab(WidgetContainer widgets, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof ETWTMenu menu)) {
            return;
        }

        this.menu = menu;
        this.pickBlock = widgets.addCheckbox("pickBlock",
                Component.translatable("gui.ae2wtlib.pick_block.text"), this::save);
        this.restock = widgets.addCheckbox("restock",
                Component.translatable("gui.ae2wtlib.restock"), this::save);
        this.magnet = widgets.addCheckbox("magnet",
                Component.translatable("gui.ae2wtlib.magnetcard"), this::save);
        this.pickupToME = widgets.addCheckbox("pickupToME",
                Component.translatable("gui.ae2wtlib.magnetcard.desc.me"), this::save);

        var stack = stack();
        pickBlock.setSelected(ItemWT.getBoolean(stack, ETWTMenu.PICK_BLOCK));
        restock.setSelected(ItemWT.getBoolean(stack, ETWTMenu.RESTOCK));

        var mode = menu.getMagnetMode();
        magnet.setSelected(mode.magnet());
        pickupToME.setSelected(mode.pickupToME());

        if (mode == MagnetMode.NO_CARD || mode == MagnetMode.INVALID) {
            magnet.active = false;
            pickupToME.active = false;
        }
    }

    private ItemStack stack() {
        if (menu == null) {
            return ItemStack.EMPTY;
        }
        return menu.getWTHost().getItemStack();
    }

    private void save() {
        if (menu == null) {
            return;
        }

        menu.setPickBlock(pickBlock.isSelected());
        menu.setRestock(restock.isSelected());
        if (!magnet.active) {
            return;
        }

        var mode = menu.getMagnetMode().set(magnet.isSelected(), pickupToME.isSelected());
        menu.setMagnetMode(mode);
    }
}
