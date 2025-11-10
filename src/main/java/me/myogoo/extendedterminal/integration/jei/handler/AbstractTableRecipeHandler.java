package me.myogoo.extendedterminal.integration.jei.handler;

import appeng.core.network.ServerboundPacket;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.table.ITableRecipeAdapter;
import me.myogoo.extendedterminal.menu.ETTerminalBaseMenu;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static me.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.findGoodTemplateItems;
import static me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket.NOT_SET_RECIPE_SIZE;

public abstract class AbstractTableRecipeHandler<T extends ETTerminalBaseMenu<R>, R extends Recipe<?>>
        implements IRecipeTransferHandler<T, R>, IAbstractRecipeHandler {
    private final Class<T> containerClass;
    private final MenuType<T> menuType;
    private final RecipeType<R> recipeType;

    public AbstractTableRecipeHandler(Class<T> containerClass, MenuType<T> menuType, RecipeType<R> recipeType) {
        this.containerClass = containerClass;
        this.menuType = menuType;
        this.recipeType = recipeType;
    }

    @Override
    public @NotNull Class<? extends T> getContainerClass() {
        return containerClass;
    }

    @Override
    public @NotNull Optional<MenuType<T>> getMenuType() {
        return Optional.of(menuType);
    }

    @Override
    public @NotNull RecipeType<R> getRecipeType() {
        return recipeType;
    }

    protected abstract Map<Integer, Ingredient> getGuiSlotToIngredientMap(T menu, ITableRecipeAdapter recipe);

    protected void performTransfer(T menu, ITableRecipeAdapter recipe, boolean craftMissing, Supplier<RecipeHolder<R>> supplier) {
        var recipeHolder = supplier.get();
        performTransfer(menu, recipe, craftMissing, recipeHolder.id());
    }

    protected void performTransfer(T menu, ITableRecipeAdapter recipe, boolean craftMissing, ResourceLocation recipeId) {
        var templateItems = findGoodTemplateItems(recipe, menu);
        int recipeWidth = NOT_SET_RECIPE_SIZE;
        int recipeHeight = NOT_SET_RECIPE_SIZE;
        if (recipe instanceof IShapedTableRecipeAdapter shapedRecipe) {
            recipeWidth = shapedRecipe.width();
            recipeHeight = shapedRecipe.height();
        }

        ServerboundPacket message = new FillTableCraftingGridFromRecipePacket(recipeId, templateItems, craftMissing,
                recipeWidth, recipeHeight);
        PacketDistributor.sendToServer(message);
    }



}
