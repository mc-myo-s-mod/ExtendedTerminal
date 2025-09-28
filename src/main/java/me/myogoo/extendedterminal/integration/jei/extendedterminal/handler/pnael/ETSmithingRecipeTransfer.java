package me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.pnael;

import appeng.api.stacks.AEItemKey;
import appeng.core.localization.ItemModText;
import appeng.core.network.ServerboundPacket;
import appeng.integration.modules.itemlists.EncodingHelper;
import me.myogoo.extendedterminal.api.adapter.recipe.smithing.ISmithingRecipeAdapter;
import me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper;
import me.myogoo.extendedterminal.integration.jei.extendedterminal.handler.ETTerminalBaseRecipeTransfer;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.network.serverbound.FillSmithingGridFromRecipePacket;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;


public class ETSmithingRecipeTransfer extends ETTerminalBaseRecipeTransfer<RecipeHolder<SmithingRecipe>> {
    private static final int SMITHING_SLOT_COUNT = 3;

    public ETSmithingRecipeTransfer(IRecipeTransferHandlerHelper helper) {
        super(helper);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, RecipeHolder<SmithingRecipe> recipeHolder) {
        var recipe = recipeHolder.value();
        var smithing = ISmithingRecipeAdapter.of(recipe);
        var ingredients = smithing.getIngredients();

        var result = new HashMap<Integer, Ingredient>(ingredients.size());

        for(int i = 0; i < ingredients.size(); i++) {
            result.put(i, ingredients.get(i));
        }

        return result;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(ETTerminalMenu menu, RecipeHolder<SmithingRecipe> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        var recipe = recipeHolder.value();

        boolean craftingMissing = AbstractContainerScreen.hasControlDown();
        var inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);

        var slotToIngredientMap = getGuiSlotToIngredientMap(menu, recipeHolder);
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            var missingSlotViews = missingSlots.missingSlots().stream()
                    .map(idx -> idx < inputSlots.size() ? inputSlots.get(idx) : null)
                    .filter(Objects::nonNull)
                    .toList();

            return helper.createUserErrorForMissingSlots(ItemModText.NO_ITEMS.text(), missingSlotViews);
        }

        if (!doTransfer) {
            if (missingSlots.totalSize() != 0) {
                int color = missingSlots.anyMissing() ? ORANGE_PLUS_BUTTON_COLOR : BLUE_PLUS_BUTTON_COLOR;
                return new Result.PartiallyCraftable(missingSlots, color, craftingMissing);
            }
        } else {
            menu.setMode(ETTerminalMode.SMITHING);
            performTransfer(menu, recipeHolder, craftingMissing);
        }

        return Result.createSuccessful();
    }

    @Override
    protected void performTransfer(ETTerminalMenu menu, RecipeHolder<SmithingRecipe> recipeHolder, boolean craftingMissing) {
        var recipeId = recipeHolder.id();
        var templateItems = findGoodTemplateItems(menu, ISmithingRecipeAdapter.of(recipeHolder.value()));

        if(menu.getPlayer().level().getRecipeManager().byKey(recipeId).isEmpty()) {
            return;
        }

        ServerboundPacket message = new FillSmithingGridFromRecipePacket(recipeId, templateItems, craftingMissing);
        PacketDistributor.sendToServer(message);
    }

    @Override
    public RecipeType<RecipeHolder<SmithingRecipe>> getRecipeType() {
        return RecipeTypes.SMITHING;
    }

    private NonNullList<ItemStack> findGoodTemplateItems(ETTerminalMenu menu, ISmithingRecipeAdapter recipe) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ItemListTermCraftingHelper.ENTRY_COMPARATOR); //보류

        var templateItems = NonNullList.withSize(SMITHING_SLOT_COUNT, ItemStack.EMPTY);
        var ingredients = recipe.getIngredients();

        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                var stack = ingredientPriorities.entrySet()
                        .stream()
                        .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                        .max(Comparator.comparingInt(Map.Entry::getValue))
                        .map(e -> ((AEItemKey) e.getKey()).toStack())
                        .orElse(ingredient.getItems()[0]);
                templateItems.set(i, stack);
            }
        }
        return templateItems;
    }
}
