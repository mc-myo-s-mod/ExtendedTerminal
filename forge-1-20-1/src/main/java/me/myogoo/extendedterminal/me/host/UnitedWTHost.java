package me.myogoo.extendedterminal.me.host;

import appeng.api.inventories.InternalInventory;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class UnitedWTHost extends ETWTHost implements IUnitedTerminalHost {
    private static final String UNITED_CRAFTING_GRID_TAG = "unitedCraftingGrid";
    private static final String REMEMBER_RECIPE_TYPE = "rememberUnitedRecipeType";
    private static final String SELECTED_RECIPE_TYPE = "selectedUnitedRecipeType";

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

    @Override
    public boolean shouldRememberRecipeType() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        return !tag.contains(REMEMBER_RECIPE_TYPE, Tag.TAG_BYTE) || tag.getBoolean(REMEMBER_RECIPE_TYPE);
    }

    @Override
    public void setRememberRecipeType(boolean remember) {
        CompoundTag tag = getItemStack().getOrCreateTag();
        tag.putBoolean(REMEMBER_RECIPE_TYPE, remember);
        if (!remember) {
            tag.remove(SELECTED_RECIPE_TYPE);
        }
        saveChanges();
    }

    @Override
    public @Nullable UnitedTerminalMenu.UnitedRecipeKind getLastRecipeKind() {
        CompoundTag tag = getItemStack().getOrCreateTag();
        if (!tag.contains(SELECTED_RECIPE_TYPE, Tag.TAG_STRING)) {
            return null;
        }
        try {
            return UnitedTerminalMenu.UnitedRecipeKind.valueOf(tag.getString(SELECTED_RECIPE_TYPE));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public void setLastRecipeKind(@Nullable UnitedTerminalMenu.UnitedRecipeKind recipeKind) {
        CompoundTag tag = getItemStack().getOrCreateTag();
        if (recipeKind == null) {
            tag.remove(SELECTED_RECIPE_TYPE);
        } else {
            tag.putString(SELECTED_RECIPE_TYPE, recipeKind.name());
        }
        saveChanges();
    }
}
