package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.crafting.RecipeAccess;
import appeng.core.network.ServerboundPacket;
import appeng.helpers.ICraftingGridMenu;
import appeng.items.storage.ViewCellItem;
import appeng.me.storage.NullInventory;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.primitives.Ints;
import me.myogoo.extendedterminal.api.annotation.ExtendedCrafting;
import me.myogoo.extendedterminal.api.annotation.ReAvaritia;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.menu.recipe.ETRecipeTransferPlanner;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.myotus.api.MyotusAPI;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FillTableCraftingGridFromRecipePacket extends FillRecipeBasePacket implements ServerboundPacket {
    public static final int NOT_SET_RECIPE_SIZE = -1;

    public static final StreamCodec<RegistryFriendlyByteBuf, FillTableCraftingGridFromRecipePacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    FillTableCraftingGridFromRecipePacket::write,
                    FillTableCraftingGridFromRecipePacket::decode);

    public static final CustomPacketPayload.Type<FillTableCraftingGridFromRecipePacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("fill_table_crafting_grid_from_recipe"));

    protected final List<ItemStack> ingredientTemplates;
    protected final boolean craftMissing;
    protected final int recipeWidth;
    protected final int recipeHeight;
    protected final @Nullable ResourceKey<Recipe<?>> recipeId;


    @Override
    public CustomPacketPayload.@NotNull Type<? extends FillTableCraftingGridFromRecipePacket> type() {
        return TYPE;
    }

    public FillTableCraftingGridFromRecipePacket(
            @Nullable ResourceKey<Recipe<?>> recipeId,
            List<ItemStack> ingredientTemplates,
            boolean craftMissing,
            int recipeWidth,
            int recipeHeight
    ) {
        int side = (int) Math.sqrt(validateGridSize(ingredientTemplates.size()));
        if (!(recipeWidth == NOT_SET_RECIPE_SIZE && recipeHeight == NOT_SET_RECIPE_SIZE)
                && (recipeWidth < 1 || recipeWidth > side || recipeHeight < 1 || recipeHeight > side)) {
            throw new IllegalArgumentException("Recipe dimensions must both be unset or fit the crafting grid");
        }
        this.recipeId = recipeId;
        this.ingredientTemplates = NonNullList.copyOf(ingredientTemplates.stream().map(ItemStack::copy).toList());
        this.craftMissing = craftMissing;
        this.recipeWidth = recipeWidth;
        this.recipeHeight = recipeHeight;
    }

    public void write(RegistryFriendlyByteBuf stream) {
        if (recipeId != null) {
            stream.writeBoolean(true);
            stream.writeResourceKey(recipeId);
        } else {
            stream.writeBoolean(false);
        }

        stream.writeInt(ingredientTemplates.size());
        for (var ingredientTemplate : ingredientTemplates) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(stream, ingredientTemplate);
        }
        stream.writeBoolean(craftMissing);
        stream.writeInt(recipeWidth);
        stream.writeInt(recipeHeight);
    }

    public static FillTableCraftingGridFromRecipePacket decode(RegistryFriendlyByteBuf stream) {
        ResourceKey<Recipe<?>> recipeId = null;
        if (stream.readBoolean()) {
            recipeId = stream.readResourceKey(Registries.RECIPE);
        }
        var ingredientTemplates = NonNullList.withSize(validateGridSize(stream.readInt()), ItemStack.EMPTY);
        ingredientTemplates.replaceAll(ignored -> ItemStack.OPTIONAL_STREAM_CODEC.decode(stream));
        var craftMissing = stream.readBoolean();
        int recipeWidth = stream.readInt();
        int recipeHeight = stream.readInt();

        return new FillTableCraftingGridFromRecipePacket(recipeId, ingredientTemplates, craftMissing, recipeWidth, recipeHeight);
    }

    private static int validateGridSize(int count) {
        int side = (int) Math.sqrt(count);
        if (count < 1 || count > 81 || side * side != count) {
            throw new IllegalArgumentException("Recipe transfer requires a square grid of at most 81 slots");
        }
        return count;
    }

    @Override
    @Nullable
    protected NonNullList<Optional<Ingredient>> getDesiredIngredients(Player player) {
        Recipe<?> recipe = null;
        if (recipeId != null) {
            RecipeHolder<? extends Recipe<?>> recipeHolder = RecipeAccess.byKey(player.level(), RecipeType.CRAFTING, this.recipeId);
            if (recipeHolder == null && MyotusAPI.integrations().isLoaded(ExtendedCrafting.class)) {
                recipeHolder = RecipeAccess.byKey(player.level(), ModRecipeTypes.TABLE.get(), this.recipeId);
            }
            if (recipeHolder == null && MyotusAPI.integrations().isLoaded(ReAvaritia.class)) {
                recipeHolder = RecipeAccess.byKey(player.level(), committee.nova.mods.avaritia.init.registry.ModRecipeTypes.CRAFTING_TABLE_RECIPE.get(), this.recipeId);
            }
            recipe = recipeHolder != null ? recipeHolder.value() : null;
        }
        if (recipe != null) {
            int slots = MyoTableRecipe.positionedIngredients(recipe).size();
            if (slots > ingredientTemplates.size()
                    || (recipeWidth != NOT_SET_RECIPE_SIZE && slots != recipeWidth * recipeHeight)) {
                return null;
            }
        }
        return ETRecipeTransferPlanner.desiredIngredients(recipe, ingredientTemplates, recipeWidth, recipeHeight);
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        // Setup and verification
        var menu = player.containerMenu;
        if (!(menu instanceof ICraftingGridMenu cct)) {
            // Server might have closed the menu before the client-packet is processed. This is not an error.
            return;
        }
        var craftMatrix = cct.getCraftingMatrix();
        if (ingredientTemplates.size() != craftMatrix.size()) {
            return;
        }
        var ingredients = getDesiredIngredients(player);
        if (ingredients == null) {
            return;
        }
        if (menu instanceof ETTerminalMenu terminalMenu) {
            terminalMenu.setMode(ETTerminalMode.CRAFTING);
        }

        var energy = cct.getEnergySource();
        @Nullable
        ICraftingService craftingService;
        @Nullable
        IStorageService storageService;
        MEStorage networkStorage;
        KeyCounter cachedStorage;

        @Nullable
        var node = cct.getGridNode();
        if (node != null && cct.getLinkStatus().connected()) {
            craftingService = node.getGrid().getCraftingService();
            storageService = node.getGrid().getStorageService();
            networkStorage = storageService.getInventory();
            cachedStorage = storageService.getCachedInventory();
        } else {
            craftingService = null;
            storageService = null;
            networkStorage = NullInventory.of();
            cachedStorage = new KeyCounter();
        }

        // We'll try to use the best possible ingredients based on what's available in the network
        var filter = ViewCellItem.createItemFilter(cct.getViewCells());

        // Prepare to autocraft some stuff
        var toAutoCraft = new LinkedHashMap<AEItemKey, IntList>();
        boolean touchedGridStorage = false;

        // Handle each slot
        for (var x = 0; x < craftMatrix.size(); x++) {
            var currentItem = craftMatrix.getStackInSlot(x);
            var ingredient = ingredients.get(x).orElse(null);

            // Move out items blocking the grid
            if (!currentItem.isEmpty()) {
                // Put away old item, if not correct
                if (ingredient != null && ingredient.test(currentItem)) {
                    // Grid already has an item that matches the ingredient
                    continue;
                } else {
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

                    // If more is remaining, try moving it to the player inventory
                    player.getInventory().add(currentItem);

                    craftMatrix.setItemDirect(x, currentItem.isEmpty() ? ItemStack.EMPTY : currentItem);
                }
            }

            if (ingredient == null) {
                continue;
            }

            // Try to find the best item for this slot. Sort by the amount available in the last tick,
            // then try to extract from most to least available item until 1 can be extracted.
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

            // If still nothing, try taking it from the player inventory
            if (currentItem.isEmpty()) {
                currentItem = takeIngredientFromPlayer(cct, player, ingredient);
                if (currentItem.isEmpty()) {
                    currentItem = takeIngredientFromOtherGrid(cct, ingredient);
                }
            }
            craftMatrix.setItemDirect(x, currentItem);

            // If we couldn't find the item, schedule its autocrafting
            if (currentItem.isEmpty() && craftMissing && craftingService != null) {
                int slot = x;
                findCraftableKey(ingredient, craftingService).ifPresent(key -> {
                    toAutoCraft.computeIfAbsent(key, k -> new IntArrayList()).add(slot);
                });
            }
        }

        menu.slotsChanged(craftMatrix.toContainer());

        if (!toAutoCraft.isEmpty()) {
            // Invalidate the grid storage cache if we modified it. The crafting plan will use
            // the outdated cached inventory otherwise.
            if (touchedGridStorage) {
                storageService.invalidateCache();
            }

            // This must be the last call since it changes the menu!
            var stacks = toAutoCraft.entrySet().stream()
                    .map(e -> new ICraftingGridMenu.AutoCraftEntry(e.getKey(), e.getValue())).toList();
            cct.startAutoCrafting(stacks);
        }
    }
}
