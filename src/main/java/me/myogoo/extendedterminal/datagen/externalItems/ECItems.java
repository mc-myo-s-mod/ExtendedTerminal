package me.myogoo.extendedterminal.datagen.externalItems;

import me.myogoo.extendedterminal.datagen.RawIngredientValue;
import me.myogoo.extendedterminal.util.mod.ModLoadHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.stream.Stream;

public final class ECItems {
    public static final Ingredient BASIC_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.ECCrafting_ID, "basic_table"))));
    public static final Ingredient ADVANCED_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.ECCrafting_ID, "advanced_table"))));
    public static final Ingredient ELITE_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.ECCrafting_ID, "elite_table"))));
    public static final Ingredient ULTIMATE_TABLE = Ingredient.fromValues(Stream.of(new RawIngredientValue(new ResourceLocation(ModLoadHelper.ECCrafting_ID, "ultimate_table"))));
}
