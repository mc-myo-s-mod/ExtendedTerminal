package me.myogoo.extendedterminal.menu.extendedterminal.wt;

import appeng.api.networking.security.IActionHost;
import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import de.mari_023.ae2wtlib.api.gui.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.api.terminal.ItemWUT;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ETWTMenu extends ETTerminalMenu {
    public static final MenuType<ETWTMenu> TYPE = MenuTypeBuilder
            .create(ETWTMenu::new, ETWTHost.class)
            .buildUnregistered(ExtendedTerminal.makeId("extended_terminal_wt"));

    public ETWTMenu(int id, Inventory ip, ETWTHost host) {
        super(TYPE, id, ip, host);

        var singularitySlot = new RestrictedInputSlot(
                RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                host.getSubInventory(WTMenuHost.INV_SINGULARITY),
                0);
        addSlot(singularitySlot, AE2wtlibSlotSemantics.SINGULARITY);
    }

    public boolean isWUT() {
        return ((WTMenuHost) getHost()).getItemStack().getItem() instanceof ItemWUT;
    }
}