package me.myogoo.extendedterminal.integration.polymorph;

import com.illusivesoulworks.polymorph.common.crafting.RecipeSelection;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class ETPolymorphRecipeSelection {
    private ETPolymorphRecipeSelection() {
    }

    public static Optional<CraftingRecipe> getPlayerCraftingRecipe(AbstractContainerMenu menu,
                                                                   CraftingContainer input,
                                                                   Level level,
                                                                   Player player) {
        return RecipeSelection.getPlayerRecipe(menu, RecipeType.CRAFTING, input, level, player);
    }
}
