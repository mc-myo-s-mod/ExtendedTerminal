package me.myogoo.extendedterminal.client.screen.extendedterminal.gui.config;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.WidgetContainer;
import appeng.client.gui.widgets.AECheckbox;
import de.mari_023.ae2wtlib.TextConstants;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import me.myogoo.extendedterminal.menu.extendedterminal.wt.ETWTMenu;
import me.myogoo.myotus.api.config.MyoConfigTabScreen;
import net.minecraft.world.item.ItemStack;

public class ETTerminalConfigScreen implements MyoConfigTabScreen {
    private ETWTMenu menu;
    private AECheckbox pickBlock;
    private AECheckbox craftIfMissing;
    private AECheckbox restock;
    private AECheckbox magnet;
    private AECheckbox pickupToME;

    @Override
    public void buildTab(WidgetContainer widgets, AEBaseScreen<?> screen) {
        if (!(screen.getMenu() instanceof ETWTMenu menu)) {
            return;
        }

        this.menu = menu;
        this.pickBlock = widgets.addCheckbox("pickBlock", TextConstants.PICK_BLOCK, this::save);
        this.craftIfMissing = widgets.addCheckbox("craftIfMissing", TextConstants.CRAFT_IF_MISSING, this::save);
        this.restock = widgets.addCheckbox("restock", TextConstants.RESTOCK, this::save);
        this.magnet = widgets.addCheckbox("magnet", TextConstants.MAGNET, this::save);
        this.pickupToME = widgets.addCheckbox("pickupToME", TextConstants.PICKUP_TO_ME, this::save);

        var stack = stack();
        pickBlock.setSelected(ItemWT.getBoolean(stack, ETWTMenu.PICK_BLOCK));
        craftIfMissing.setSelected(ItemWT.getBoolean(stack, ETWTMenu.CRAFT_IF_MISSING));
        craftIfMissing.active = pickBlock.isSelected();
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
        menu.setCraftIfMissing(craftIfMissing.isSelected());
        craftIfMissing.active = pickBlock.isSelected();
        menu.setRestock(restock.isSelected());
        if (!magnet.active) {
            return;
        }

        var mode = menu.getMagnetMode().set(magnet.isSelected(), pickupToME.isSelected());
        menu.setMagnetMode(mode);
    }
}
