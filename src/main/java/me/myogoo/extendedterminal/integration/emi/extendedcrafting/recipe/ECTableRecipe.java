package me.myogoo.extendedterminal.integration.emi.extendedcrafting.recipe;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.ITableRecipeAdapter;
import me.myogoo.extendedterminal.integration.emi.extendedcrafting.ECRecipeCategory;
import me.myogoo.extendedterminal.util.TableCraftingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ECTableRecipe extends BasicEmiRecipe {
    private final static int baseInX = 62;
    private final ITableRecipe recipe;
    private final ClientLevel level;

    public ECTableRecipe(EmiRecipeCategory category, RecipeHolder<ITableRecipe> recipeHolder) {
        super(category, recipeHolder.id(), calcInvX(recipeHolder.value().getTier()), calcInvY(recipeHolder.value().getTier()));
        this.recipe = recipeHolder.value();
        this.level = Minecraft.getInstance().level;

        addIngredients(recipeHolder);
        this.outputs.add(EmiStack.of(this.recipe.getResultItem(level.registryAccess())));
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < this.getGridSize(); i++) {
            EmiIngredient ingredient;
            if (i < this.inputs.size()) {
                ingredient = this.inputs.get(i);
            } else {
                ingredient = EmiIngredient.of(Ingredient.EMPTY);
            }

            int nx = (i % this.getTierColNRow()) * 18;
            int ny = (i / this.getTierColNRow()) * 18;
            widgets.addTexture(EmiTexture.SLOT, nx, ny);
            widgets.addSlot(ingredient, nx, ny)
                    .drawBack(false);
        }

        int outputX = calcInvX(this.recipe.getTier());
        int outputY = ((int) (double) (this.getTierColNRow() / 2)) * 18;

        if (this.recipe instanceof ShapelessTableRecipe) {
            widgets.addTexture(EmiTexture.SHAPELESS, outputX - 25, outputY - 4 - 14);
        }

        widgets.addTexture(EmiTexture.EMPTY_ARROW, outputX - 58, outputY - 4);

        String tooltipText = String.format("Require Tier %d Crafting Table", this.recipe.getTier());
        var tooltip = ClientTooltipComponent.create(FormattedCharSequence.forward(tooltipText, Style.EMPTY));

        widgets.addTexture(EmiTexture.LARGE_SLOT, outputX - 30, outputY - 4)
                .tooltip(List.of(tooltip));
        widgets.addSlot(EmiStack.of(recipe.getResultItem(level.registryAccess())), outputX - 30, outputY - 4).large(true)
                .recipeContext(this)
                .drawBack(false);
    }

    private int getTierColNRow() {
        int tier = this.recipe.getTier();
        return tier * 2 + 1;
    }

    private int getGridSize() {
        return this.getTierColNRow() * this.getTierColNRow();
    }

    private void addIngredients(RecipeHolder<ITableRecipe> recipeHolder) {
        var recipe = recipeHolder.value();
        var adapter = ITableRecipeAdapter.of(recipe);

        if(adapter instanceof IShapedTableRecipeAdapter shapedAdapter) {
            int width = shapedAdapter.width();
            int height = shapedAdapter.height();
            int gridSize = 2 * recipe.getTier() + 1;
            int offsetX = (gridSize - width) / 2;
            int offsetY = (gridSize - height) / 2;
            this.inputs = new ArrayList<>(Collections.nCopies(TableCraftingHelper.getCraftingGridSize(adapter), EmiIngredient.of(Ingredient.EMPTY)));

            for(int y = 0; y < height; y++) {
                for(int x = 0; x < width; x++) {
                    int index = x + y * width;
                    Ingredient ingredient = recipe.getIngredients().get(index);
                    int guiSlot = (y + offsetY) * gridSize + (x + offsetX);
                    if(!ingredient.isEmpty()) {
                        this.inputs.set(guiSlot, EmiIngredient.of(ingredient));
                    }
                }
            }
        } else {
            for (var ingredient : recipe.getIngredients()) {
                this.inputs.add(EmiIngredient.of(ingredient));
            }
        }
    }
    private static int calcInvX(int tier) {
        return baseInX + 18 * (2 * tier + 1);
    }

    private static int calcInvY(int tier) {
        return 18 * (2 * tier + 1);
    }

    public static EmiRecipeCategory getCategory(int tier) {
        return switch (tier) {
            case 1 -> ECRecipeCategory.BASIC_TABLE_CRAFTING_CATEGORY;
            case 2 -> ECRecipeCategory.ADVANCED_TABLE_CRAFTING_CATEGORY;
            case 3 -> ECRecipeCategory.ELITE_TABLE_CRAFTING_CATEGORY;
            case 4 -> ECRecipeCategory.ULTIMATE_TABLE_CRAFTING_CATEGORY;
            default -> throw new IllegalArgumentException("Invalid tier: " + tier);
        };
    }
}