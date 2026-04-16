package me.myogoo.extendedterminal.me.host;

import appeng.api.implementations.blockentities.IViewCellStorage;
import appeng.api.inventories.InternalInventory;
import appeng.menu.ISubMenu;
import appeng.util.inv.AppEngInternalInventory;
import de.mari_023.ae2wtlib.terminal.WTMenuHost;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.ANVIL_INVENTORY;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.SMITHING_INVENTORY;
import static me.myogoo.extendedterminal.part.extendedterminal.ETTerminalPart.STONECUTTER_INVENTORY;

public class ETWTHost extends WTMenuHost implements IViewCellStorage, IETTerminalHost {
    private static final String MODE_TAG = "mode";
    private static final String RECIPE_TAG = "stonecuttingRecipeId";
    private static final String CRAFTING_GRID_TAG = "craftingGrid";
    private static final String SMITHING_GRID_TAG = "smithingGrid";
    private static final String STONECUTTER_GRID_TAG = "stoneCutterGrid";
    private static final String ANVIL_GRID_TAG = "anvilInv";

    private final AppEngInternalInventory craftingGrid;
    private final AppEngInternalInventory smithingGrid;
    private final AppEngInternalInventory stoneCutterGrid;
    private final AppEngInternalInventory anvilInv;

    private ETTerminalMode mode = ETTerminalMode.loadableValues().get(0);
    @Nullable
    private ResourceLocation stonecuttingRecipeId;

    public ETWTHost(Player player, @Nullable Integer inventorySlot, ItemStack itemStack,
            BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, inventorySlot, itemStack, returnToMainMenu);
        this.craftingGrid = new AppEngInternalInventory(this, ETMenuType.ET_TERMINAL.getGridSize());
        this.smithingGrid = new AppEngInternalInventory(this, 3);
        this.stoneCutterGrid = new AppEngInternalInventory(this, 1);
        this.anvilInv = new AppEngInternalInventory(this, 2);
        readFromNbt();
    }

    @Override
    protected void readFromNbt() {
        super.readFromNbt();

        CompoundTag tag = getItemStack().getOrCreateTag();
        try {
            if (tag.contains(MODE_TAG, Tag.TAG_STRING)) {
                this.mode = ETTerminalMode.valueOf(tag.getString(MODE_TAG));
            }
        } catch (IllegalArgumentException ignored) {
            this.mode = ETTerminalMode.loadableValues().get(0);
        }

        this.stonecuttingRecipeId = tag.contains(RECIPE_TAG, Tag.TAG_STRING)
                ? ResourceLocation.tryParse(tag.getString(RECIPE_TAG))
                : null;

        this.craftingGrid.readFromNBT(tag, CRAFTING_GRID_TAG);
        this.smithingGrid.readFromNBT(tag, SMITHING_GRID_TAG);
        this.stoneCutterGrid.readFromNBT(tag, STONECUTTER_GRID_TAG);
        this.anvilInv.readFromNBT(tag, ANVIL_GRID_TAG);
    }

    @Override
    public void saveChanges() {
        super.saveChanges();

        CompoundTag tag = getItemStack().getOrCreateTag();
        tag.putString(MODE_TAG, this.mode.name());
        if (this.stonecuttingRecipeId != null) {
            tag.putString(RECIPE_TAG, this.stonecuttingRecipeId.toString());
        } else {
            tag.remove(RECIPE_TAG);
        }

        this.craftingGrid.writeToNBT(tag, CRAFTING_GRID_TAG);
        this.smithingGrid.writeToNBT(tag, SMITHING_GRID_TAG);
        this.stoneCutterGrid.writeToNBT(tag, STONECUTTER_GRID_TAG);
        this.anvilInv.writeToNBT(tag, ANVIL_GRID_TAG);
    }

    @Override
    public @Nullable InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(ETMenuType.ET_TERMINAL.getCraftingInventory())) {
            return this.craftingGrid;
        } else if (id.equals(SMITHING_INVENTORY)) {
            return this.smithingGrid;
        } else if (id.equals(STONECUTTER_INVENTORY)) {
            return this.stoneCutterGrid;
        } else if (id.equals(ANVIL_INVENTORY)) {
            return this.anvilInv;
        }
        return super.getSubInventory(id);
    }

    @Override
    public ETTerminalMode getMode() {
        return this.mode;
    }

    @Override
    public void setMode(ETTerminalMode mode) {
        this.mode = mode;
        saveChanges();
    }

    @Override
    public @Nullable ResourceLocation getStoneCutterRecipeId() {
        return this.stonecuttingRecipeId;
    }

    @Override
    public void setStoneCutterRecipeId(@Nullable ResourceLocation stonecuttingRecipeId) {
        this.stonecuttingRecipeId = stonecuttingRecipeId;
        saveChanges();
    }
}
