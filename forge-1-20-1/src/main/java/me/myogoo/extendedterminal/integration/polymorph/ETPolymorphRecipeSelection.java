package me.myogoo.extendedterminal.integration.polymorph;

import com.illusivesoulworks.polymorph.common.crafting.RecipeSelection;
import com.illusivesoulworks.polymorph.api.PolymorphApi;
import com.illusivesoulworks.polymorph.api.common.base.IRecipePair;
import com.illusivesoulworks.polymorph.common.capability.AbstractRecipeData;
import com.illusivesoulworks.polymorph.common.impl.RecipePair;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.List;
import java.util.TreeSet;
import javax.annotation.Nullable;

public final class ETPolymorphRecipeSelection {
    private ETPolymorphRecipeSelection() {
    }

    public static Optional<CraftingRecipe> getPlayerCraftingRecipe(AbstractContainerMenu menu,
                                                                   CraftingContainer input,
                                                                   Level level,
                                                                   Player player) {
        return RecipeSelection.getPlayerRecipe(menu, RecipeType.CRAFTING, input, level, player);
    }

    @Nullable
    public static UnitedTerminalMenu.UnitedRecipe getUnitedCraftingRecipe(UnitedTerminalMenu menu,
            List<UnitedTerminalMenu.UnitedRecipe> matches) {
        var player = menu.getPlayer();
        var level = player.level();
        var data = PolymorphApi.common().getRecipeData(player).orElse(null);
        if (data == null) return matches.isEmpty() ? null : matches.get(0);

        // Polymorph 0.49 caches recipe objects across same-tick selections and recipe reloads.
        var selectedId = data.getSelectedRecipe().map(recipe -> recipe.getId());
        var loadedId = data instanceof AbstractRecipeData<?> saved ? saved.getLoadedRecipe() : Optional.<ResourceLocation>empty();
        if (selectedId.isEmpty()) selectedId = loadedId;
        var preferredId = selectedId.orElse(null);
        UnitedTerminalMenu.UnitedRecipe selected = null;
        var pairs = new TreeSet<IRecipePair>();
        for (var match : matches) {
            var output = match.assemble(level);
            if (!output.isEmpty()) {
                pairs.add(new RecipePair(match.recipe().getId(), output));
                if (match.recipe().getId().equals(preferredId)) selected = match;
            }
        }
        if (selected == null && !pairs.isEmpty()) {
            var firstId = pairs.first().getResourceLocation();
            selected = matches.stream().filter(match -> match.recipe().getId().equals(firstId)).findFirst().orElseThrow();
        }
        if (selected != null && loadedId.isPresent()) {
            // Consume the persisted selection before other crafting menus can restore it again.
            data.getRecipe(RecipeType.CRAFTING, selected.input(), level, List.of((CraftingRecipe) selected.recipe()));
        }
        data.setRecipesList(pairs);
        if (selected != null) data.selectRecipe(selected.recipe());
        if (player instanceof ServerPlayer serverPlayer) {
            PolymorphApi.common().getPacketDistributor().sendRecipesListS2C(serverPlayer, pairs,
                    selected == null ? null : selected.recipe().getId());
        }
        return selected;
    }
}
