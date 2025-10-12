package me.myogoo.extendedterminal.network.serverbound;

import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.core.network.ServerboundPacket;
import appeng.helpers.ICraftingGridMenu;
import appeng.items.storage.ViewCellItem;
import appeng.me.storage.NullInventory;
import com.google.common.primitives.Ints;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

// or select?
public class FillStonecutterGridFromRecipePacket extends FillRecipeBasePacket implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, FillStonecutterGridFromRecipePacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    FillStonecutterGridFromRecipePacket::write,
                    FillStonecutterGridFromRecipePacket::decode
            );

    public static final CustomPacketPayload.Type<FillStonecutterGridFromRecipePacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("fill_stonecutter_grid_from_recipe"));


    private final ResourceLocation recipeId;
    private final boolean craftMissing;
    private final ItemStack ingredientTemplate;

    private void write(RegistryFriendlyByteBuf stream) {
        if (recipeId != null) {
            stream.writeBoolean(true);
            stream.writeResourceLocation(recipeId);
        } else {
            stream.writeBoolean(false);
        }

        ItemStack.OPTIONAL_STREAM_CODEC.encode(stream, ingredientTemplate);
        stream.writeBoolean(craftMissing);
    }

    private static FillStonecutterGridFromRecipePacket decode(RegistryFriendlyByteBuf stream) {
        ResourceLocation recipeId = null;
        if (stream.readBoolean()) {
            recipeId = stream.readResourceLocation();
        }
        var ingredientTemplate = ItemStack.OPTIONAL_STREAM_CODEC.decode(stream);
        var craftMissing = stream.readBoolean();
        return new FillStonecutterGridFromRecipePacket(recipeId, ingredientTemplate, craftMissing);
    }

    public FillStonecutterGridFromRecipePacket(ResourceLocation recipeId, ItemStack ingredientTemplate,boolean craftMissing) {
        this.recipeId = recipeId;
        this.craftMissing = craftMissing;
        this.ingredientTemplate = ingredientTemplate;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
// Setup and verification
        var menu = player.containerMenu;
        if (!(menu instanceof ETTerminalMenu cct)) {
            // Server might have closed the menu before the client-packet is processed. This is not an error.
            return;
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

        var craftMatrix = cct.getStoneCutterInventory();

        // We'll try to use the best possible ingredients based on what's available in the network

        var filter = ViewCellItem.createItemFilter(cct.getViewCells());
        var ingredients = getDesiredIngredients(player);

        // Prepare to autocraft some stuff
        var toAutoCraft = new LinkedHashMap<AEItemKey, IntList>();
        boolean touchedGridStorage = false;

        // Handle each slot
        for (var x = 0; x < craftMatrix.size(); x++) {
            var currentItem = craftMatrix.getStackInSlot(0);
            var ingredient = ingredients.getFirst();

            // Move out items blocking the grid
            if (!currentItem.isEmpty()) {
                // Put away old item, if not correct
                if (ingredient.test(currentItem)) {
                    // 이미 아이템이 있을 경우 같더라도 목표 recipeId가 다르면 다르게 셋팅
                    // recipeId를 선택을 안했거나
                    // 이미 선택했을 경우 처리
                    if(cct.getStoneCutterRecipeId() == null || (cct.getStoneCutterRecipeId() != null && !cct.getStoneCutterRecipeId().equals(recipeId))) {
                        cct.setStoneCutterRecipeId(recipeId);
                    }
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

            // If still nothing, try taking it form other grid inventory
            if (currentItem.isEmpty() && cct.getStoneCutterInventory() != null) {
                currentItem = takeIngredientFromOtherGrid(cct, ingredient);
            }

            craftMatrix.setItemDirect(x, currentItem);

            //마지막 한번더 확인 기존 아이템이 없을 경우를 대비하여
            if(!currentItem.isEmpty()) {
                cct.setStoneCutterRecipeId(recipeId);
            }

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
        cct.setMode(ETTerminalMode.STONECUTTING);
    }

    @Override
    protected NonNullList<Ingredient> getDesiredIngredients(Player player) {
        if(recipeId != null) {
            var optionalRecipe = player.level().getRecipeManager().byKey(this.recipeId);
            if(optionalRecipe.isPresent() && optionalRecipe.get().value() instanceof SingleItemRecipe recipe) {
                return recipe.getIngredients();
            }
        }
        return NonNullList.create();
    }


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
