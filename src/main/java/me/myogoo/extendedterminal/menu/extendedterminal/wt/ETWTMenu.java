package me.myogoo.extendedterminal.menu.extendedterminal.wt;

import appeng.api.networking.IGridNode;
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
import org.jetbrains.annotations.Nullable;

public class ETWTMenu extends ETTerminalMenu {
    public static final MenuType<ETWTMenu> TYPE = MenuTypeBuilder
            .create(ETWTMenu::new, ETWTHost.class)
            .build(ETMenuType.ET_TERMINAL.getWTIdAsString());
    private final ETWTHost host;
    public ETWTMenu(int id, Inventory ip, ETWTHost host) {
        super(TYPE, id, ip, host);
        this.host =host;
        addSlot(new RestrictedInputSlot(
                RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                host.getSubInventory(WTMenuHost.INV_SINGULARITY),
                0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public @Nullable IGridNode getNetworkNode() {
        return this.host.getActionableNode();
    }

    public boolean isWUT() {
        return this.host.getItemStack().getItem() instanceof ItemWUT;
    }
}
