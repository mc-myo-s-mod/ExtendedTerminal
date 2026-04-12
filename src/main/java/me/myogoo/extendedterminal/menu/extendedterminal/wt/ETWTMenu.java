package me.myogoo.extendedterminal.menu.extendedterminal.wt;

import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import appeng.menu.implementations.MenuTypeBuilder;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ETWTMenu extends ETTerminalMenu {
    public static final MenuType<ETWTMenu> TYPE = MenuTypeBuilder
            .create(ETWTMenu::new, ETWTHost.class)
            .build(ETMenuType.ET_TERMINAL.getWTIdAsString());

    public ETWTMenu(int id, Inventory ip, ETWTHost host) {
        super(TYPE, id, ip, host);
        addSlot(new RestrictedInputSlot(
                RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                host.getSubInventory(WTMenuHost.INV_SINGULARITY),
                0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public ETWTHost getHost() {
        return (ETWTHost) super.getHost();
    }

    public boolean isWUT() {
        return ((WTMenuHost) getHost()).getItemStack().getItem() instanceof ItemWUT;
    }
}
