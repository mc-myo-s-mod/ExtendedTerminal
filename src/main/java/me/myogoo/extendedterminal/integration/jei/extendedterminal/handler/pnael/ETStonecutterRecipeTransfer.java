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
import me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_PLUS_BUTTON_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.ORANGE_PLUS_BUTTON_COLOR;

public class ETStonecutterRecipeTransfer<T extends ETTerminalMenu> extends ETTerminalBaseRecipeTransfer<T ,RecipeHolder<StonecutterRecipe>> {
    public ETStonecutterRecipeTransfer(MenuType<T> menuType, Class<T> classContainer, IRecipeTransferHandlerHelper helper) {
        super(menuType, classContainer, helper);
    }

    @Override
    protected Map<Integer, Ingredient> getGuiSlotToIngredientMap(ETTerminalMenu menu, RecipeHolder<StonecutterRecipe> recipeHolder) {
        var recipe = recipeHolder.value();
        var ingredients = recipe.getIngredients();

        var result = new HashMap<Integer, Ingredient>(ingredients.size());
        result.put(0, ingredients.getFirst());
        return result;
    }


    @Override
    public @Nullable IRecipeTransferError transferRecipe(T menu, RecipeHolder<StonecutterRecipe> recipeHolder, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
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
            performTransfer(menu, recipeHolder, craftingMissing);
        }

        return Result.createSuccessful();
    }

    @Override
    protected void performTransfer(ETTerminalMenu menu, RecipeHolder<StonecutterRecipe> recipeHolder, boolean craftingMissing) {
        var recipeId = recipeHolder.id();
        var templateItem = findGoodTemplateItem(menu, recipeHolder.value());

        if (menu.getPlayer().level().getRecipeManager().byKey(recipeId).isEmpty()) {
            return;
        }

        ServerboundPacket message = new FillStonecutterGridFromRecipePacket(recipeId, templateItem, craftingMissing);
        PacketDistributor.sendToServer(message);
    }

    @Override
    public RecipeType<RecipeHolder<StonecutterRecipe>> getRecipeType() {
        return RecipeTypes.STONECUTTING;
    }

    private ItemStack findGoodTemplateItem(ETTerminalMenu menu, SingleItemRecipe recipe) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ItemListTermCraftingHelper.ENTRY_COMPARATOR); //보류

        var templateItems = NonNullList.withSize(1, ItemStack.EMPTY);
        var ingredients = recipe.getIngredients();

        var ingredient = ingredients.getFirst();
        if (!ingredient.isEmpty()) {
            var stack = ingredientPriorities.entrySet()
                    .stream()
                    .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .map(e -> ((AEItemKey) e.getKey()).toStack())
                    .orElse(ingredient.getItems()[0]);
            templateItems.set(0, stack);
        }

        return templateItems.getFirst();
    }
}
