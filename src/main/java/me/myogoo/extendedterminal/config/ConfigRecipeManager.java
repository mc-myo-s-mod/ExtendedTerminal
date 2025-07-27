package me.myogoo.extendedterminal.config;

import appeng.core.definitions.AEParts;
import com.blakebr0.cucumber.event.RecipeManagerLoadingEvent;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETItems;
import me.myogoo.extendedterminal.init.ETParts;
import me.myogoo.extendedterminal.api.ETModLoad;
import me.myogoo.extendedterminal.util.extendedcrafting.ShapedTableRecipeBuilder;
import net.neoforged.bus.api.SubscribeEvent;

public final class ConfigRecipeManager {
    private static final ConfigRecipeManager Instance = new ConfigRecipeManager();

    @SubscribeEvent
    static void onRegisterRecipes(RecipeManagerLoadingEvent event) {

    }

//    private static RecipeHolder<ITableRecipe> makeTableTerminalRecipe(ItemDefinition<?> terminal) {
//        return makeTableTerminalRecipe(terminal, 0);
//    }

//    private static RecipeHolder<ITableRecipe> makeTableTerminalRecipe(ItemDefinition<?> terminal, int tier) {
//        var id = ExtendedTerminal.makeId(terminal.id + "_table_recipe");
//    }



    @ETModLoad.ExtendedCrafting
    private void loadExCraftingRecipe() {
        ShapedTableRecipeBuilder.shaped(ETParts.BASIC_TERMINAL_PART, 1)
                .pattern("AB")
                .pattern("C ")
                .define('A', AEParts.CRAFTING_TERMINAL)
                .define('B', ModBlocks.BASIC_TABLE.get())
                .define('C', ETItems.COMPAT_PROCESSOR)
                .build(ExtendedTerminal.makeId("extended_crafting/basic_terminal"))
    }
}
