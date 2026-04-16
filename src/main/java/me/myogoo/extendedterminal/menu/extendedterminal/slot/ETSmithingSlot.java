package me.myogoo.extendedterminal.menu.extendedterminal.slot;

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
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.slot.ETCraftingBaseSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ETSmithingSlot extends ETCraftingBaseSlot<SmithingRecipe, SimpleContainer> {
    private static final int SMITHING_SLOT_SIZE = 3;

    public ETSmithingSlot(Player player, IActionSource mySrc, IEnergySource energySrc, MEStorage storage,
            InternalInventory cMatrix, InternalInventory secondMatrix, IMenuCraftingPacket ccp) {
        super(player, mySrc, energySrc, storage, cMatrix, secondMatrix, ccp, ETMenuType.ET_TERMINAL);
    }

    @Override
    protected SmithingRecipe findRecipe(SimpleContainer ic, Level level) {
        if (this.menu instanceof ETTerminalMenu terminalMenu) {
            var recipe = terminalMenu.getSmithingRecipe();
            if (recipe != null && recipe.matches(ic, level)) {
                return recipe;
            }
        }
        return level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, ic, level).orElse(null);
    }

    protected NonNullList<ItemStack> getETRemainingItems(SimpleContainer ic, Level level) {
        var recipe = findRecipe(ic, level);
        if (recipe != null && recipe.matches(ic, level)) {
            return recipe.getRemainingItems(ic);
        }
        return NonNullList.create();
    }

    @Override
    protected void makeItem(Player player, ItemStack stack) {
        this.checkTakeAchievements(stack);

        var input = new SimpleContainer(
                this.craftInv.getStackInSlot(0),
                this.craftInv.getStackInSlot(1),
                this.craftInv.getStackInSlot(2));

        ForgeHooks.setCraftingPlayer(player);
        var remainingItems = this.getETRemainingItems(input, player.level());
        ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < SMITHING_SLOT_SIZE; i++) {
            this.craftInv.extractItem(i, 1, false);
            if (i < remainingItems.size()) {
                var remainingInSlot = remainingItems.get(i);
                if (!remainingInSlot.isEmpty()) {
                    if (this.craftInv.getStackInSlot(i).isEmpty()) {
                        this.craftInv.setItemDirect(i, remainingInSlot);
                    } else if (!player.getInventory().add(remainingInSlot)) {
                        player.drop(remainingInSlot, false);
                    }
                }
            }
        }
    }

    @Override
    protected ItemStack craftItem(Player player, MEStorage inv, KeyCounter all) {
        var result = this.getItem().copy();
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }

        final var extractedInputs = new ItemStack[this.getPattern().size()];
        Arrays.fill(extractedInputs, ItemStack.EMPTY);

        var level = player.level();
        if (!level.isClientSide()) {
            var input = new SimpleContainer(
                    this.getPattern().getStackInSlot(0).copy(),
                    this.getPattern().getStackInSlot(1).copy(),
                    this.getPattern().getStackInSlot(2).copy());
            var recipe = this.findRecipe(input, level);
            if (recipe == null) {
                return ItemStack.EMPTY;
            }

            result = recipe.assemble(input, level.registryAccess());
            if (inv != null) {
                var filter = ViewCellItem.createItemFilter(this.menu.getViewCells());
                for (var slot = 0; slot < this.getPattern().size(); slot++) {
                    if (!this.getPattern().getStackInSlot(slot).isEmpty()) {
                        extractedInputs[slot] = extractItemsByRecipe(this.energySrc, this.mySrc, inv, level, recipe,
                                result, List.of(
                                        this.getPattern().getStackInSlot(0).copy(),
                                        this.getPattern().getStackInSlot(1).copy(),
                                        this.getPattern().getStackInSlot(2).copy()),
                                this.getPattern().getStackInSlot(slot), slot, all, filter);
                    }
                }
            }
        }

        this.makeItem(player, result);
        this.postCraft(player, inv, extractedInputs, result);
        player.containerMenu.slotsChanged(this.craftInv.toContainer());
        return result;
    }

    private static ItemStack extractItemsByRecipe(IEnergySource energySrc, IActionSource mySrc, MEStorage src,
            Level level, SmithingRecipe recipe, ItemStack output, List<ItemStack> craftingItems,
            ItemStack providedTemplate, int slot, KeyCounter items, IPartitionList filter) {
        if (energySrc.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) <= 0.9) {
            return ItemStack.EMPTY;
        }
        if (providedTemplate == null) {
            return ItemStack.EMPTY;
        }

        var requested = AEItemKey.of(providedTemplate);
        if (filter == null || filter.isListed(requested)) {
            var extracted = src.extract(requested, 1, Actionable.MODULATE, mySrc);
            if (extracted > 0) {
                energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                return requested.toStack();
            }
        }

        var checkFuzzy = providedTemplate.getTags().findAny().isEmpty() || providedTemplate.isDamageableItem();
        if (items != null && checkFuzzy) {
            var craftingInputItems = new ArrayList<>(craftingItems);
            for (var entry : items) {
                if (entry.getKey() instanceof AEItemKey itemKey) {
                    if (providedTemplate.getItem() == itemKey.getItem() && !itemKey.matches(output)) {
                        craftingInputItems.set(slot, itemKey.toStack());
                        var adjustedCraftingInput = new SimpleContainer(
                                craftingInputItems.get(0),
                                craftingInputItems.get(1),
                                craftingInputItems.get(2));
                        if (recipe.matches(adjustedCraftingInput, level)
                                && ItemStack.matches(recipe.assemble(adjustedCraftingInput, level.registryAccess()), output)) {
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
