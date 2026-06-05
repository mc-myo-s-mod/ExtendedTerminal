package me.myogoo.extendedterminal.menu.extendedcrafting.slot;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.IMenuCraftingPacket;
import appeng.items.storage.ViewCellItem;
import appeng.util.prioritylist.IPartitionList;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

public class UnitedCraftingTerminalSlot extends ETCraftingBaseSlot<Recipe<?>, CraftingContainer> {
    public UnitedCraftingTerminalSlot(Player player, IActionSource mySrc, IEnergySource energySrc,
                                      MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix,
                                      IMenuCraftingPacket ccp, ETMenuType menuType) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp, menuType);
    }

    @Override
    protected Recipe<?> findRecipe(CraftingContainer input, Level level) {
        if (this.menu instanceof UnitedTerminalMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentUnitedRecipe();
            var recipeInput = terminalMenu.getCurrentUnitedInput();
            if (recipe != null && recipeInput != null && terminalMenu.getCurrentUnitedRecipeRecord().matches(recipeInput, level)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    protected ItemStack craftItem(Player player, MEStorage inv, KeyCounter all) {
        var result = this.getItem().copy();
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final var set = new ItemStack[this.getPattern().size()];
        Arrays.fill(set, ItemStack.EMPTY);

        var level = player.level();
        if (!level.isClientSide()) {
            final var items = NonNullList.withSize(menuType.getGridSize(), ItemStack.EMPTY);
            for (var x = 0; x < menuType.getGridSize(); x++) {
                items.set(x, this.getPattern().getStackInSlot(x));
            }

            UnitedTerminalMenu.UnitedRecipe recipe = null;
            if (this.menu instanceof UnitedTerminalMenu terminalMenu) {
                recipe = terminalMenu.findUnitedRecipe(items);
            }
            if (recipe == null) {
                return ItemStack.EMPTY;
            }

            result = recipe.assemble(level);

            if (inv != null) {
                var filter = ViewCellItem.createItemFilter(this.menu.getViewCells());
                for (var x = 0; x < this.getPattern().size(); x++) {
                    if (!this.getPattern().getStackInSlot(x).isEmpty()) {
                        set[x] = extractItemsByRecipe(this.energySrc, this.mySrc, inv, level, recipe, result,
                                (AbstractContainerMenu) this.menu, items, this.getPattern().getStackInSlot(x), x, all, filter);
                        items.set(x, set[x]);
                    }
                }
            }
        }

        this.makeItem(player, result);
        this.postCraft(player, inv, set, result);
        player.containerMenu.slotsChanged(this.craftInv.toContainer());
        return result;
    }

    private static ItemStack extractItemsByRecipe(IEnergySource energySrc, IActionSource mySrc, MEStorage src,
                                                  Level level, UnitedTerminalMenu.UnitedRecipe recipe, ItemStack output,
                                                  AbstractContainerMenu menu, List<ItemStack> craftingItems,
                                                  ItemStack providedTemplate, int slot, KeyCounter items,
                                                  IPartitionList filter) {
        if (energySrc.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) <= 0.9 || providedTemplate == null) {
            return ItemStack.EMPTY;
        }

        var aeReq = AEItemKey.of(providedTemplate);
        if (filter == null || filter.isListed(aeReq)) {
            var extracted = src.extract(aeReq, 1, Actionable.MODULATE, mySrc);
            if (extracted > 0) {
                energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                return aeReq.toStack();
            }
        }

        var checkFuzzy = providedTemplate.getTags().findAny().isEmpty() || providedTemplate.isDamageableItem();
        if (items != null && checkFuzzy) {
            var craftingInputItems = NonNullList.withSize(craftingItems.size(), ItemStack.EMPTY);
            for (int i = 0; i < craftingItems.size(); i++) {
                craftingInputItems.set(i, craftingItems.get(i).copy());
            }

            for (var entry : items) {
                if (entry.getKey() instanceof AEItemKey itemKey) {
                    if (providedTemplate.getItem() == itemKey.getItem() && !itemKey.matches(output)) {
                        craftingInputItems.set(slot, itemKey.toStack());
                        var adjustedRecipe = recipe.menu().findUnitedRecipe(craftingInputItems, recipe.kind());
                        if (adjustedRecipe != null
                                && ItemStack.matches(adjustedRecipe.assemble(level), output)) {
                            if (filter == null || filter.isListed(itemKey)) {
                                var extracted = src.extract(itemKey, 1, Actionable.MODULATE, mySrc);
                                if (extracted > 0) {
                                    energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                                    return itemKey.toStack();
                                }
                            }
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
