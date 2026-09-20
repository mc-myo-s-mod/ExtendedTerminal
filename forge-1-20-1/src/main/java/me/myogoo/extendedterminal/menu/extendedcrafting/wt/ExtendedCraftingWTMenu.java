package me.myogoo.extendedterminal.menu.extendedcrafting.wt;

import appeng.api.networking.IGridNode;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import me.myogoo.extendedterminal.config.extendedcrafting.ExtendedCraftingConfig;
import me.myogoo.extendedterminal.me.host.ExtendedCraftingWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class ExtendedCraftingWTMenu extends ExtendedTerminalBaseMenu {
    public static final MenuType<ExtendedCraftingWTMenu> EPIC_TYPE = MenuTypeBuilder
            .create(ExtendedCraftingWTMenu::new, ExtendedCraftingWTHost.class)
            .build(ETMenuType.EPIC_TERMINAL.getWTIdAsString());
    public static final MenuType<ExtendedCraftingWTMenu> LEGENDARY_TYPE = MenuTypeBuilder
            .create(ExtendedCraftingWTMenu::new, ExtendedCraftingWTHost.class)
            .build(ETMenuType.LEGENDARY_TERMINAL.getWTIdAsString());

    public ExtendedCraftingWTMenu(MenuType<?> menuType, int id, Inventory inventory, ExtendedCraftingWTHost host) {
        super(menuType, id, inventory, host, host.getTerminalType(), switch (host.getTerminalType()) {
            case EPIC_TERMINAL -> ExtendedCraftingConfig.INSTANCE.getEpicConfig();
            case LEGENDARY_TERMINAL -> ExtendedCraftingConfig.INSTANCE.getLegendaryConfig();
            default -> throw new IllegalArgumentException("Unsupported wireless crafting terminal");
        });
        addSlot(new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                host.getSubInventory(WTMenuHost.INV_SINGULARITY), 0), AE2wtlibSlotSemantics.SINGULARITY);
    }

    @Override
    public @Nullable IGridNode getNetworkNode() {
        return getWTHost().getActionableNode();
    }

    public ExtendedCraftingWTHost getWTHost() {
        return (ExtendedCraftingWTHost) getHost();
    }

    public boolean isWUT() {
        return getWTHost().getItemStack().getItem() instanceof ItemWUT;
    }
}
