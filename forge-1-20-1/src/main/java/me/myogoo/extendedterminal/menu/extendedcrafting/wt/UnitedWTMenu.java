package me.myogoo.extendedterminal.menu.extendedcrafting.wt;

import appeng.api.networking.IGridNode;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.RestrictedInputSlot;
import de.mari_023.ae2wtlib.AE2wtlibSlotSemantics;
import de.mari_023.ae2wtlib.AE2wtlibTags;
import de.mari_023.ae2wtlib.terminal.ItemWT;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetHandler;
import de.mari_023.ae2wtlib.wct.magnet_card.MagnetMode;
import de.mari_023.ae2wtlib.wct.magnet_card.config.MagnetMenu;
import de.mari_023.ae2wtlib.wut.ItemWUT;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class UnitedWTMenu extends UnitedTerminalMenu {
    public static final MenuType<UnitedWTMenu> TYPE = MenuTypeBuilder
            .create(UnitedWTMenu::new, UnitedWTHost.class)
            .build(ETMenuType.UNITED_TERMINAL.getWTIdAsString());
    public static final String RESTOCK = AE2wtlibTags.RESTOCK;
    public static final String PICK_BLOCK = AE2wtlibTags.PICK_BLOCK;
    public static final String CRAFT_IF_MISSING = AE2wtlibTags.CRAFT_IF_MISSING;
    public static final String MAGNET_MODE = "magnetMode";
    public static final String MAGNET_MENU = "magnetMenu";
    private final UnitedWTHost host;

    public UnitedWTMenu(int id, Inventory ip, UnitedWTHost host) {
        super(TYPE, id, ip, host);
        this.host = host;
        addSlot(new RestrictedInputSlot(
                RestrictedInputSlot.PlacableItemType.QE_SINGULARITY,
                host.getSubInventory(WTMenuHost.INV_SINGULARITY),
                0), AE2wtlibSlotSemantics.SINGULARITY);

        registerClientAction(RESTOCK, Boolean.class, this::setRestock);
        registerClientAction(PICK_BLOCK, Boolean.class, this::setPickBlock);
        registerClientAction(CRAFT_IF_MISSING, Boolean.class, this::setCraftIfMissing);
        registerClientAction(MAGNET_MODE, MagnetMode.class, this::setMagnetMode);
        registerClientAction(MAGNET_MENU, this::openMagnetMenu);
    }

    @Override
    public @Nullable IGridNode getNetworkNode() {
        return this.host.getActionableNode();
    }

    public boolean isWUT() {
        return this.host.getItemStack().getItem() instanceof ItemWUT;
    }

    public UnitedWTHost getWTHost() {
        return this.host;
    }

    public boolean isRestock() {
        return ItemWT.getBoolean(this.host.getItemStack(), RESTOCK);
    }

    public boolean isPickBlock() {
        return ItemWT.getBoolean(this.host.getItemStack(), PICK_BLOCK);
    }

    public boolean isCraftIfMissing() {
        return ItemWT.getBoolean(this.host.getItemStack(), CRAFT_IF_MISSING);
    }

    public void setPickBlock(boolean enabled) {
        if (isClientSide()) {
            sendClientAction(PICK_BLOCK, enabled);
        }
        ItemWT.setBoolean(this.host.getItemStack(), enabled, PICK_BLOCK);
        this.host.saveChanges();
    }

    public void setCraftIfMissing(boolean enabled) {
        if (isClientSide()) {
            sendClientAction(CRAFT_IF_MISSING, enabled);
        }
        ItemWT.setBoolean(this.host.getItemStack(), enabled, CRAFT_IF_MISSING);
        this.host.saveChanges();
    }

    public void setRestock(boolean enabled) {
        if (isClientSide()) {
            sendClientAction(RESTOCK, enabled);
        }
        ItemWT.setBoolean(this.host.getItemStack(), enabled, RESTOCK);
        this.host.saveChanges();
    }

    public MagnetMode getMagnetMode() {
        return MagnetHandler.getMagnetMode(this.host.getItemStack());
    }

    public void setMagnetMode(MagnetMode mode) {
        if (isClientSide()) {
            sendClientAction(MAGNET_MODE, mode);
        }
        MagnetHandler.saveMagnetMode(this.host.getItemStack(), mode);
        this.host.saveChanges();
    }

    public void openMagnetMenu() {
        if (isClientSide()) {
            sendClientAction(MAGNET_MENU);
            return;
        }
        MenuOpener.open(MagnetMenu.TYPE, getPlayer(), getLocator());
    }
}
