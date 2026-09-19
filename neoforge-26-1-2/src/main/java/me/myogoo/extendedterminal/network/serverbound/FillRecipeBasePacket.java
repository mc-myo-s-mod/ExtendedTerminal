package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.config.FuzzyMode;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.helpers.ICraftingGridMenu;
import appeng.util.prioritylist.IPartitionList;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class FillRecipeBasePacket implements IETFillRecipeBasePacket{
    protected abstract NonNullList<Optional<Ingredient>> getDesiredIngredients(Player player);

    protected ItemStack takeIngredientFromPlayer(ICraftingGridMenu cct, ServerPlayer player, Ingredient ingredient) {
        var playerInv = player.getInventory();
        for (int i = 0; i < playerInv.getNonEquipmentItems().size(); i++) {
            // Do not take ingredients out of locked slots
            if (cct.isPlayerInventorySlotLocked(i)) {
                continue;
            }

            var item = playerInv.getItem(i);
            if (ingredient.test(item)) {
                var result = item.split(1);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    protected List<AEItemKey> findBestMatchingItemStack(Ingredient ingredient, IPartitionList filter,
                                                        KeyCounter storage) {
        if (!ingredient.isCustom()) {
            return ingredient.getValues().stream()
                    .map(Holder::value)
                    .map(AEItemKey::of)
                    .filter(r -> r != null && (filter == null || filter.isListed(r)))
                    .flatMap(s -> storage.findFuzzy(s, FuzzyMode.IGNORE_ALL).stream())
                    // While FuzzyMode.IGNORE_ALL will retrieve all stacks of the same Item which matches
                    // standard Vanilla Ingredient matching, there are NBT-matching Ingredient subclasses on Forge,
                    // and Mods might actually have mixed into Ingredient
                    .filter(e -> ((AEItemKey) e.getKey()).matches(ingredient))
                    // Sort in descending order of availability
                    .sorted((a, b) -> Long.compare(b.getLongValue(), a.getLongValue()))
                    .map(e -> (AEItemKey) e.getKey())
                    .toList();
        }

        return storage.keySet().stream()
                .map(k -> k instanceof AEItemKey itemKey ? itemKey : null)
                .filter(r -> r != null && (filter == null || filter.isListed(r))
                        && ingredient.test(r.getReadOnlyStack()))
                .sorted((a, b) -> Long.compare(storage.get(b), storage.get(a)))
                .toList();
    }

    protected Optional<AEItemKey> findCraftableKey(Ingredient ingredient, ICraftingService craftingService) {
        return craftingService.getCraftables(AEItemKey.filter()).stream()
                .map(k -> k instanceof AEItemKey itemKey ? itemKey : null)
                .filter(Objects::nonNull)
                .filter(k -> ingredient.test(k.getReadOnlyStack()))
                .findAny();
    }


}
