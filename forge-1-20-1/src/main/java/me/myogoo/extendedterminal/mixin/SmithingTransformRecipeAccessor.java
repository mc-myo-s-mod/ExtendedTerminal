package me.myogoo.extendedterminal.mixin;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface SmithingTransformRecipeAccessor {
    @Accessor("template")
    Ingredient extendedterminal$getTemplate();

    @Accessor("base")
    Ingredient extendedterminal$getBase();

    @Accessor("addition")
    Ingredient extendedterminal$getAddition();
}
