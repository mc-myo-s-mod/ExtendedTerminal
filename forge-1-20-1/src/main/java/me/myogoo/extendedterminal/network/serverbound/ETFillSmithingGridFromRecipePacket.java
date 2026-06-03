package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.helpers.IMenuCraftingPacket;
import appeng.items.storage.ViewCellItem;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.myogoo.extendedterminal.api.adapter.recipe.smithing.ISmithingRecipeAdapter;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.myotus.api.network.IMyotusPacket;
import me.myogoo.myotus.api.network.MyoPacketContext;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

public class ETFillSmithingGridFromRecipePacket extends FillRecipeBasePacket implements IMyotusPacket {
    private final @Nullable ResourceLocation recipeId;
    private final NonNullList<ItemStack> ingredientTemplates;
    private final boolean craftMissing;

    public ETFillSmithingGridFromRecipePacket(FriendlyByteBuf stream) {
        ResourceLocation decodedRecipeId = null;
        if (stream.readBoolean()) {
            decodedRecipeId = stream.readResourceLocation();
        }
        this.recipeId = decodedRecipeId;
        this.ingredientTemplates = NonNullList.withSize(stream.readInt(), ItemStack.EMPTY);
        for (int i = 0; i < this.ingredientTemplates.size(); i++) {
            this.ingredientTemplates.set(i, stream.readItem());
        }
        this.craftMissing = stream.readBoolean();
    }

    public ETFillSmithingGridFromRecipePacket(@Nullable ResourceLocation recipeId,
            NonNullList<ItemStack> ingredientTemplates, boolean craftMissing) {
        this.recipeId = recipeId;
        this.ingredientTemplates = NonNullList.withSize(ingredientTemplates.size(), ItemStack.EMPTY);
        for (int i = 0; i < ingredientTemplates.size(); i++) {
            this.ingredientTemplates.set(i, ingredientTemplates.get(i));
        }
        this.craftMissing = craftMissing;
    }

    @Override
    public void write(FriendlyByteBuf stream) {
        if (this.recipeId != null) {
            stream.writeBoolean(true);
            stream.writeResourceLocation(this.recipeId);
        } else {
            stream.writeBoolean(false);
        }

        stream.writeInt(this.ingredientTemplates.size());
        for (var ingredientTemplate : this.ingredientTemplates) {
            stream.writeItem(ingredientTemplate);
        }
        stream.writeBoolean(this.craftMissing);
    }

    public static void handle(ETFillSmithingGridFromRecipePacket packet, MyoPacketContext context) {
        var player = context.sender();
        if (player == null) {
            return;
        }

        packet.handleOnServer(player);
    }

    private void handleOnServer(ServerPlayer player) {
        var menu = player.containerMenu;
        if (!(menu instanceof ETTerminalMenu cct)) {
            return;
        }

        @Nullable
        var node = cct.getNetworkNode();
        if (node == null) {
            return;
        }

        var energy = node.getGrid().getEnergyService();
        @Nullable ICraftingService craftingService = node.getGrid().getCraftingService();
        @Nullable IStorageService storageService = node.getGrid().getStorageService();
        if (storageService == null) {
            return;
        }

        MEStorage networkStorage = storageService.getInventory();
        KeyCounter cachedStorage = storageService.getCachedInventory();
        var craftMatrix = cct.getSmithingInventory();
        var filter = ViewCellItem.createItemFilter(cct.getViewCells());
        var ingredients = getDesiredIngredients(player);

        var toAutoCraft = new LinkedHashMap<AEItemKey, IntList>();
        boolean touchedGridStorage = false;

        for (int x = 0; x < craftMatrix.size(); x++) {
            var currentItem = craftMatrix.getStackInSlot(x);
            var ingredient = x < ingredients.size() ? ingredients.get(x) : Ingredient.EMPTY;

            if (!currentItem.isEmpty()) {
                if (ingredient.test(currentItem)) {
                    continue;
                }

                var in = AEItemKey.of(currentItem);
                var inserted = StorageHelper.poweredInsert(energy, networkStorage, in, currentItem.getCount(),
                        cct.getActionSource());
                if (inserted > 0) {
                    touchedGridStorage = true;
                }
                if (inserted < currentItem.getCount()) {
                    currentItem = currentItem.copy();
                    currentItem.shrink((int) inserted);
                } else {
                    currentItem = ItemStack.EMPTY;
                }

                player.getInventory().add(currentItem);
                craftMatrix.setItemDirect(x, currentItem.isEmpty() ? ItemStack.EMPTY : currentItem);
            }

            if (ingredient.isEmpty()) {
                continue;
            }

            if (currentItem.isEmpty()) {
                var request = findBestMatchingItemStack(ingredient, filter, cachedStorage);
                for (var what : request) {
                    var extracted = StorageHelper.poweredExtraction(energy, networkStorage, what, 1,
                            cct.getActionSource());
                    if (extracted > 0) {
                        touchedGridStorage = true;
                        currentItem = what.toStack(Ints.saturatedCast(extracted));
                        break;
                    }
                }
            }

            if (currentItem.isEmpty()) {
                currentItem = takeIngredientFromPlayer(cct, player, ingredient);
            }

            if (currentItem.isEmpty()) {
                currentItem = takeIngredientFromOtherGrid(cct, ingredient);
            }

            craftMatrix.setItemDirect(x, currentItem);

            if (currentItem.isEmpty() && this.craftMissing && craftingService != null) {
                int slot = x;
                findCraftableKey(ingredient, craftingService).ifPresent(key ->
                        toAutoCraft.computeIfAbsent(key, ignored -> new IntArrayList()).add(slot));
            }
        }

        menu.slotsChanged(craftMatrix.toContainer());

        if (!toAutoCraft.isEmpty()) {
            if (touchedGridStorage) {
                storageService.invalidateCache();
            }

            var stacks = toAutoCraft.entrySet().stream()
                    .map(entry -> new IMenuCraftingPacket.AutoCraftEntry(entry.getKey(), entry.getValue()))
                    .toList();
            cct.startAutoCrafting(stacks);
        }

        cct.setMode(ETTerminalMode.SMITHING);
    }

    @Override
    protected NonNullList<Ingredient> getDesiredIngredients(Player player) {
        if (this.recipeId != null) {
            var optionalRecipe = player.level().getRecipeManager().byKey(this.recipeId);
            if (optionalRecipe.isPresent() && optionalRecipe.get() instanceof SmithingRecipe recipe) {
                return ISmithingRecipeAdapter.of(recipe).getIngredients();
            }
        }

        var ingredients = NonNullList.withSize(this.ingredientTemplates.size(), Ingredient.EMPTY);
        for (int i = 0; i < this.ingredientTemplates.size(); i++) {
            var template = this.ingredientTemplates.get(i);
            if (!template.isEmpty()) {
                ingredients.set(i, Ingredient.of(template));
            }
        }
        return ingredients;
    }
}
