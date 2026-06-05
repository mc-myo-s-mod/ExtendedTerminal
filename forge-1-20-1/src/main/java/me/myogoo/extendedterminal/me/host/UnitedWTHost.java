package me.myogoo.extendedterminal.me.host;

import appeng.api.inventories.InternalInventory;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class UnitedWTHost extends ETWTHost {
    private static final String UNITED_CRAFTING_GRID_TAG = "unitedCraftingGrid";

    private AppEngInternalInventory unitedCraftingGrid;

    public UnitedWTHost(Player player, @Nullable Integer inventorySlot, ItemStack itemStack,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, inventorySlot, itemStack, returnToMainMenu);
        ensureUnitedInventory();
        readFromNbt();
    }

    private void ensureUnitedInventory() {
        if (this.unitedCraftingGrid == null) {
            this.unitedCraftingGrid = new AppEngInternalInventory(this, ETMenuType.UNITED_TERMINAL.getGridSize());
        }
    }

    @Override
    protected void readFromNbt() {
        super.readFromNbt();
        ensureUnitedInventory();
        CompoundTag tag = getItemStack().getOrCreateTag();
        this.unitedCraftingGrid.readFromNBT(tag, UNITED_CRAFTING_GRID_TAG);
    }

    @Override
    public void saveChanges() {
        ensureUnitedInventory();
        super.saveChanges();
        CompoundTag tag = getItemStack().getOrCreateTag();
        this.unitedCraftingGrid.writeToNBT(tag, UNITED_CRAFTING_GRID_TAG);
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        ensureUnitedInventory();
        if (id.equals(ETMenuType.UNITED_TERMINAL.getCraftingInventory())) {
            return this.unitedCraftingGrid;
        }
        return super.getSubInventory(id);
    }
}
