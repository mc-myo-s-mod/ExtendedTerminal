package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.config.FuzzyMode;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.helpers.IMenuCraftingPacket;
import appeng.items.storage.ViewCellItem;
import appeng.util.prioritylist.IPartitionList;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.myotus.api.network.IMyotusPacket;
import me.myogoo.myotus.api.network.MyoPacketContext;
import me.myogoo.extendedterminal.util.extendedcrafting.TableCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.*;



public class ETFillCraftingGridFromRecipePacket extends FillRecipeBasePacket implements IMyotusPacket {
    public static final int NOT_SET_RECIPE_SIZE = -1;

    private final @Nullable ResourceLocation recipeId;
    private final List<ItemStack> ingredientTemplates;
    private final boolean craftMissing;
    private final int recipeWidth;
    private final int recipeHeight;
    private final @Nullable UnitedTerminalMenu.UnitedRecipeKind unitedRecipeKind;


    public ETFillCraftingGridFromRecipePacket(FriendlyByteBuf stream) {
        ResourceLocation decodedRecipeId = null;
        if (stream.readBoolean()) {
            decodedRecipeId = stream.readResourceLocation();
        }
        this.recipeId = decodedRecipeId;
        ingredientTemplates = NonNullList.withSize(stream.readInt(), ItemStack.EMPTY);
        for (int i = 0; i < ingredientTemplates.size(); i++) {
            ingredientTemplates.set(i, stream.readItem());
        }
        craftMissing = stream.readBoolean();
        recipeWidth = stream.readInt();
        recipeHeight = stream.readInt();
        int kindOrdinal = stream.readInt();
        UnitedTerminalMenu.UnitedRecipeKind decodedKind = null;
        var kinds = UnitedTerminalMenu.UnitedRecipeKind.values();
        if (kindOrdinal >= 0 && kindOrdinal < kinds.length) {
            decodedKind = kinds[kindOrdinal];
        }
        unitedRecipeKind = decodedKind;
    }


    public ETFillCraftingGridFromRecipePacket(
            @Nullable ResourceLocation recipeId,
            List<ItemStack> ingredientTemplates,
            boolean craftMissing,
            int recipeWidth,
            int recipeHeight
    ) {
        this(recipeId, ingredientTemplates, craftMissing, recipeWidth, recipeHeight, null);
    }

    public ETFillCraftingGridFromRecipePacket(
            @Nullable ResourceLocation recipeId,
            List<ItemStack> ingredientTemplates,
            boolean craftMissing,
            int recipeWidth,
            int recipeHeight,
            @Nullable UnitedTerminalMenu.UnitedRecipeKind unitedRecipeKind
    ) {
        this.recipeId = recipeId;
        this.ingredientTemplates = List.copyOf(ingredientTemplates);
        this.craftMissing = craftMissing;
        this.recipeWidth = recipeWidth;
        this.recipeHeight = recipeHeight;
        this.unitedRecipeKind = unitedRecipeKind;
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
        stream.writeInt(this.recipeWidth);
        stream.writeInt(this.recipeHeight);
        stream.writeInt(this.unitedRecipeKind == null ? -1 : this.unitedRecipeKind.ordinal());
    }

    public static void handle(ETFillCraftingGridFromRecipePacket packet, MyoPacketContext context) {
        var player = context.sender();
        if (player == null) {
            return;
        }

        packet.handleOnServer(player);
    }

    private void handleOnServer(ServerPlayer player) {
        // Setup and verification
        var menu = player.containerMenu;
        if (!(menu instanceof IMenuCraftingPacket cct)) {
            // Server might have closed the menu before the client-packet is processed. This is not an error.
            return;
        }
        if (unitedRecipeKind != null && menu instanceof UnitedTerminalMenu unitedMenu) {
            unitedMenu.setSelectedRecipeKind(unitedRecipeKind);
        }

        @Nullable
        ICraftingService craftingService;
        @Nullable
        IStorageService storageService;
        MEStorage networkStorage;
        KeyCounter cachedStorage;

        @Nullable
        var node = cct.getNetworkNode();
        if (node == null) {
            return;
        }

        craftingService = node.getGrid().getCraftingService();
        storageService = node.getGrid().getStorageService();
        networkStorage = storageService.getInventory();
        cachedStorage = storageService.getCachedInventory();

        var energy = node.getGrid().getEnergyService();
        var craftMatrix = cct.getCraftingMatrix();
        // We'll try to use the best possible ingredients based on what's available in the network
        var filter = ViewCellItem.createItemFilter(cct.getViewCells());
        var ingredients = getDesiredIngredients(player);

        // Prepare to autocraft some stuff
        var toAutoCraft = new LinkedHashMap<AEItemKey, IntList>();
        boolean touchedGridStorage = false;

        // Handle each slot
        for (var x = 0; x < craftMatrix.size(); x++) {
            var currentItem = craftMatrix.getStackInSlot(x);
            var ingredient = ingredients.get(x);

            // Move out items blocking the grid
            if (!currentItem.isEmpty()) {
                // Put away old item, if not correct
                if (ingredient.test(currentItem)) {
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

            if (ingredient.isEmpty()) {
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
                    .map(e -> new IMenuCraftingPacket.AutoCraftEntry(e.getKey(), e.getValue())).toList();
            cct.startAutoCrafting(stacks);
        }
    }

    @Override
    protected NonNullList<Ingredient> getDesiredIngredients(Player player) {
        if (recipeId != null) {
            var recipe = player.level().getRecipeManager().byKey(this.recipeId);
            if (recipe.isPresent()) {
                return ItemListTermCraftingHelper.ensureNxNTableCraftingGrid(recipe.get(), ingredientTemplates.size(), recipeWidth, recipeHeight);
            }
        }

        var ingredients = NonNullList.withSize(this.ingredientTemplates.size(), Ingredient.EMPTY);
        Preconditions.checkArgument(ingredients.size() == this.ingredientTemplates.size(),
                "Got %d ingredient templates from client, expected %d",
                ingredientTemplates.size(), ingredients.size());

        //shapeless recipes
        if (recipeWidth == NOT_SET_RECIPE_SIZE || recipeHeight == NOT_SET_RECIPE_SIZE) {
            for (int i = 0; i < ingredients.size(); i++) {
                var template = ingredientTemplates.get(i);
                if (!template.isEmpty()) {
                    ingredients.set(i, Ingredient.of(template));
                }
            }
        } else {
            int cursor = 0;
            var coordinator = TableCraftingHelper.indexToCoordinate(ingredientTemplates.size(), recipeWidth, recipeHeight);

            for (int i = 0; i < ingredients.size(); i++) {
                if (coordinator.test(i)) {
                    ingredients.set(i, Ingredient.of(ingredientTemplates.get(cursor++)));
                }
            }
        }
        return ingredients;
    }
}
