package me.myogoo.extendedterminal.gametest;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.menuobjects.IPortableTerminal;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.InventoryAction;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.authlib.GameProfile;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
public final class UnitedCraftingGameTests {
    private UnitedCraftingGameTests() {
    }

    @GameTestGenerator
    public static List<TestFunction> generateTests() {
        var tests = new ArrayList<TestFunction>();
        tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.offsetshaped",
                "extendedterminal:empty", 20, 0, true, UnitedCraftingGameTests::offsetShaped));
        tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.distributedshapeless",
                "extendedterminal:empty", 20, 0, true, UnitedCraftingGameTests::distributedShapeless));
        tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.tippedarrows",
                "extendedterminal:empty", 20, 0, true, UnitedCraftingGameTests::tippedArrows));
        if (ModList.get().isLoaded("extendedcrafting")) {
            tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.outsidetier",
                    "extendedterminal:empty", 20, 0, true, UnitedCraftingGameTests::outsideTier));
        }
        if (ModList.get().isLoaded("immersiveengineering")) {
            tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.immersiveengineeringremainder",
                    "extendedterminal:empty", 20, 0, true, UnitedCraftingGameTests::immersiveEngineeringRemainder));
            tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.reinforcedcratenbt",
                    "extendedterminal:empty", 20, 0, true, UnitedCraftingGameTests::reinforcedCrateNbt));
            tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.mirroredfluidremainder",
                    "extendedterminal:empty", 20, 0, true, UnitedCraftingGameTests::mirroredFluidRemainder));
        }
        if (Boolean.getBoolean("extendedterminal.benchmark")
                || Boolean.parseBoolean(System.getenv("EXTENDEDTERMINAL_BENCHMARK"))) {
            tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.benchmark",
                    "extendedterminal:empty", 200, 0, true, UnitedCraftingGameTests::benchmark));
        } else {
            // RecipeManager.replaceRecipes changes iteration order; keep it out of benchmark runs.
            tests.add(new TestFunction("extendedterminal", "unitedcraftinggametests.cacheinvalidation",
                    "extendedterminal:empty", 40, 0, true, UnitedCraftingGameTests::cacheInvalidation));
        }
        return tests;
    }

    private static void benchmark(GameTestHelper helper) {
        var level = helper.getLevel();
        var validItems = furnaceItems(3, 3);
        var noMatchItems = copyItems(validItems);
        noMatchItems.set(0, new ItemStack(Items.NETHER_STAR));
        var compactMissItems = copyItems(validItems);
        compactMissItems.set(3 * 9 + 3, new ItemStack(Items.NETHER_STAR));

        int warmup = 200;
        int batches = 7;
        int batchSize = 100;
        var coldLookup = new ArrayList<Long>(batches);
        var warmLookup = new ArrayList<Long>(batches);
        var noMatchLookup = new ArrayList<Long>(batches);
        var compactMissLookup = new ArrayList<Long>(batches);
        var coldMenu = createMenu(testPlayer(helper), emptyGrid()).menu;
        var warmMenu = createMenu(testPlayer(helper), validItems).menu;
        var missMenu = createMenu(testPlayer(helper), emptyGrid()).menu;
        for (int i = 0; i < warmup; i++) {
            helper.assertTrue(coldMenu.findUnitedRecipe(validItems) != null,
                    "benchmark cold lookup must find a recipe");
            helper.assertTrue(warmMenu.findUnitedRecipe(validItems) != null,
                    "benchmark warm lookup must find a recipe");
            helper.assertTrue(missMenu.findUnitedRecipe(noMatchItems) == null,
                    "benchmark no-match lookup must reject the extra item");
            helper.assertTrue(missMenu.findUnitedRecipe(compactMissItems) == null,
                    "benchmark compact no-match lookup must reject the extra item");
        }
        for (int batch = 0; batch < batches; batch++) {
            var start = System.nanoTime();
            boolean found = true;
            for (int i = 0; i < batchSize; i++) {
                found &= coldMenu.findUnitedRecipe(validItems) != null;
            }
            coldLookup.add((System.nanoTime() - start) / batchSize);
            helper.assertTrue(found, "benchmark cold lookup must find a recipe");

            start = System.nanoTime();
            found = true;
            for (int i = 0; i < batchSize; i++) {
                found &= warmMenu.findUnitedRecipe(validItems) != null;
            }
            warmLookup.add((System.nanoTime() - start) / batchSize);
            helper.assertTrue(found, "benchmark warm lookup must find a recipe");

            start = System.nanoTime();
            boolean rejected = true;
            for (int i = 0; i < batchSize; i++) {
                rejected &= missMenu.findUnitedRecipe(noMatchItems) == null;
            }
            noMatchLookup.add((System.nanoTime() - start) / batchSize);
            helper.assertTrue(rejected, "benchmark no-match lookup must reject the extra item");

            start = System.nanoTime();
            rejected = true;
            for (int i = 0; i < batchSize; i++) {
                rejected &= missMenu.findUnitedRecipe(compactMissItems) == null;
            }
            compactMissLookup.add((System.nanoTime() - start) / batchSize);
            helper.assertTrue(rejected, "benchmark compact no-match lookup must reject the extra item");
        }

        System.out.println("ET_BENCH lookup_cold_us=" + medianMicros(coldLookup)
                + " lookup_warm_us=" + medianMicros(warmLookup)
                + " lookup_no_match_us=" + medianMicros(noMatchLookup)
                + " lookup_compact_miss_us=" + medianMicros(compactMissLookup)
                + " vanilla_recipes=" + level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).size()
                + " extended_crafting_loaded=" + ModList.get().isLoaded("extendedcrafting"));

        if (ModList.get().isLoaded("extendedcrafting")) {
            var extendedRecipe = level.getRecipeManager().byKey(
                    new ResourceLocation("extendedcrafting", "crystaltine_ingot")).orElse(null);
            if (extendedRecipe != null) {
                var extendedItems = extendedCraftingItems(extendedRecipe.getIngredients());
                var extendedMenu = createMenu(testPlayer(helper), emptyGrid()).menu;
                extendedMenu.setSelectedRecipeKind(UnitedTerminalMenu.UnitedRecipeKind.EXTENDED_CRAFTING_ELITE);
                var ecLookup = new ArrayList<Long>(batches);
                for (int i = 0; i < warmup; i++) {
                    helper.assertTrue(extendedMenu.findUnitedRecipe(extendedItems,
                                    UnitedTerminalMenu.UnitedRecipeKind.EXTENDED_CRAFTING_ELITE) != null,
                            "benchmark Extended Crafting lookup must find the recipe");
                }
                for (int batch = 0; batch < batches; batch++) {
                    var start = System.nanoTime();
                    boolean found = true;
                    for (int i = 0; i < batchSize; i++) {
                        found &= extendedMenu.findUnitedRecipe(extendedItems,
                                UnitedTerminalMenu.UnitedRecipeKind.EXTENDED_CRAFTING_ELITE) != null;
                    }
                    ecLookup.add((System.nanoTime() - start) / batchSize);
                    helper.assertTrue(found, "benchmark Extended Crafting lookup must find the recipe");
                }
                System.out.println("ET_BENCH lookup_extended_crafting_elite_us=" + medianMicros(ecLookup)
                        + " extended_crafting_recipes="
                        + level.getRecipeManager().getRecipes().stream()
                                .filter(recipe -> recipe.getType() == extendedRecipe.getType()).count());
            }
        }

        int craftWarmup = 5;
        int craftRuns = 15;
        var craftTimes = new ArrayList<Long>(craftRuns);
        for (int i = 0; i < craftWarmup + craftRuns; i++) {
            var fixture = createMenu(testPlayer(helper), validItems);
            fixture.host.storage.add(new ItemStack(Items.COBBLESTONE), 8L * 64L);
            int outputIndex = outputSlotIndex(fixture.menu);
            fixture.player.containerMenu = fixture.menu;
            var start = System.nanoTime();
            fixture.menu.doAction(fixture.player, InventoryAction.CRAFT_SHIFT, outputIndex, 0);
            var elapsed = System.nanoTime() - start;
            helper.assertTrue(countPlayerItem(fixture.player, Items.FURNACE) == 64,
                    "benchmark CRAFT_SHIFT must produce 64 furnaces");
            helper.assertTrue(fixture.host.storage.amount(new ItemStack(Items.COBBLESTONE)) == 0,
                    "benchmark CRAFT_SHIFT must consume the 64 furnace inputs");
            if (i >= craftWarmup) {
                craftTimes.add(elapsed);
            }
        }
        System.out.println("ET_BENCH craft_shift_64_us=" + medianMicros(craftTimes));
        helper.succeed();
    }

    private static void cacheInvalidation(GameTestHelper helper) {
        var level = helper.getLevel();
        var manager = level.getRecipeManager();
        var originalRecipes = new ArrayList<>(manager.getRecipes());
        var recipeId = new ResourceLocation("extendedterminal", "gametest_cache_recipe");
        var recipeA = trackingRecipe(recipeId);
        var recipeB = shapedRecipe(recipeId, Items.GOLD_INGOT);
        var recipeList = new ArrayList<Recipe<?>>();
        try {
            recipeList.removeIf(recipe -> recipe.getId().equals(recipeId));
            recipeList.add(recipeA);
            manager.replaceRecipes(recipeList);

            var initialItems = singleItemItems(new ItemStack(Items.OAK_PLANKS));
            var fixture = createMenu(testPlayer(helper), initialItems);
            var first = fixture.menu.findUnitedRecipe(initialItems);
            helper.assertTrue(first != null && first.assemble(level).is(Items.DIAMOND)
                            && first.assemble(level).getCount() == 1,
                    "cache fixture must find the first recipe");

            first.input().setItem(0, new ItemStack(Items.NETHER_STAR));
            var freshAfterMutation = fixture.menu.findUnitedRecipe(initialItems);
            helper.assertTrue(freshAfterMutation != null && freshAfterMutation.assemble(level).is(Items.DIAMOND)
                            && freshAfterMutation.assemble(level).getCount() == 1,
                    "mutating a returned cached input must not poison a fresh lookup");

            var countChanged = singleItemItems(new ItemStack(Items.OAK_PLANKS, 2));
            var countResult = fixture.menu.findUnitedRecipe(countChanged);
            helper.assertTrue(countResult != null && countResult.assemble(level).is(Items.DIAMOND)
                            && countResult.assemble(level).getCount() == 2,
                    "changing the input count must invalidate cached lookup state");

            var named = new ItemStack(Items.OAK_PLANKS);
            named.setHoverName(Component.literal("cache-nbt"));
            var nbtChanged = singleItemItems(named);
            var nbtResult = fixture.menu.findUnitedRecipe(nbtChanged);
            helper.assertTrue(nbtResult != null && nbtResult.assemble(level).is(Items.DIAMOND)
                            && nbtResult.assemble(level).hasCustomHoverName(),
                    "changing input NBT must invalidate cached lookup state");

            recipeList.removeIf(recipe -> recipe.getId().equals(recipeId));
            recipeList.add(recipeB);
            manager.replaceRecipes(recipeList);
            fixture.menu.slotsChanged(fixture.menu.getCraftingMatrix().toContainer());
            var replaced = fixture.menu.findUnitedRecipe(initialItems);
            helper.assertTrue(replaced != null && replaced.assemble(level).is(Items.GOLD_INGOT),
                    "replacing a recipe under the same id must invalidate lookup state");
            helper.assertTrue(fixture.menu.getSlot(outputSlotIndex(fixture.menu)).getItem().is(Items.GOLD_INGOT),
                    "replacing a recipe under the same id must update the displayed output");

            recipeList.removeIf(recipe -> recipe.getId().equals(recipeId));
            manager.replaceRecipes(recipeList);
            fixture.menu.slotsChanged(fixture.menu.getCraftingMatrix().toContainer());
            helper.assertTrue(fixture.menu.findUnitedRecipe(initialItems) == null,
                    "removing a recipe must invalidate lookup state");
            helper.assertTrue(fixture.menu.getSlot(outputSlotIndex(fixture.menu)).getItem().isEmpty(),
                    "removing a recipe must clear the displayed output");

            var nineByNineId = new ResourceLocation("extendedterminal", "gametest_nine_by_nine");
            var nineByNine = new ShapedRecipe(nineByNineId, "", CraftingBookCategory.MISC, 1, 1,
                    NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.COBBLESTONE)), new ItemStack(Items.DIAMOND)) {
                @Override
                public boolean matches(CraftingContainer input, net.minecraft.world.level.Level ignored) {
                    return input.getWidth() == 9 && input.getHeight() == 9 && super.matches(input, ignored);
                }
            };
            recipeList.add(nineByNine);
            manager.replaceRecipes(recipeList);
            var nineItems = singleItemItems(new ItemStack(Items.COBBLESTONE));
            var nineResult = createMenu(testPlayer(helper), nineItems).menu.findUnitedRecipe(nineItems);
            helper.assertTrue(nineResult != null && nineResult.assemble(level).is(Items.DIAMOND),
                    "a shaped subclass requiring 9x9 input must keep the full-grid fallback");
            var paddedIngredients = NonNullList.withSize(25, Ingredient.EMPTY);
            paddedIngredients.set(12, Ingredient.of(Items.DIAMOND));
            recipeList.add(new ShapedRecipe(new ResourceLocation("extendedterminal", "gametest_padded_shape"),
                    "", CraftingBookCategory.MISC, 5, 5, paddedIngredients, new ItemStack(Items.EMERALD)));
            manager.replaceRecipes(recipeList);
            var paddedItems = singleItemItems(new ItemStack(Items.DIAMOND));
            var padded = createMenu(testPlayer(helper), paddedItems).menu.findUnitedRecipe(paddedItems);
            helper.assertTrue(padded != null && padded.assemble(level).is(Items.EMERALD),
                    "a large vanilla shape with an empty border still needs the full-grid fallback");
        } finally {
            manager.replaceRecipes(originalRecipes);
        }
        helper.succeed();
    }

    private static ShapedRecipe shapedRecipe(ResourceLocation id, net.minecraft.world.item.Item result) {
        return new ShapedRecipe(id, "", CraftingBookCategory.MISC, 1, 1,
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.OAK_PLANKS)), new ItemStack(result));
    }

    private static ShapedRecipe trackingRecipe(ResourceLocation id) {
        return new ShapedRecipe(id, "", CraftingBookCategory.MISC, 1, 1,
                NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.OAK_PLANKS)), new ItemStack(Items.DIAMOND)) {
            @Override
            public ItemStack assemble(CraftingContainer input, net.minecraft.core.RegistryAccess registryAccess) {
                var result = super.assemble(input, registryAccess);
                var source = input.getItem(0);
                result.setCount(source.is(Items.OAK_PLANKS) ? source.getCount() : 9);
                if (source.hasCustomHoverName()) {
                    result.setHoverName(source.getHoverName());
                }
                return result;
            }
        };
    }

    private static NonNullList<ItemStack> singleItemItems(ItemStack stack) {
        var items = emptyGrid();
        items.set(40, stack);
        return items;
    }

    private static NonNullList<ItemStack> furnaceItems(int left, int top) {
        var items = emptyGrid();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (x != 1 || y != 1) {
                    items.set((top + y) * 9 + left + x, new ItemStack(Items.COBBLESTONE));
                }
            }
        }
        return items;
    }

    private static NonNullList<ItemStack> extendedCraftingItems(List<net.minecraft.world.item.crafting.Ingredient> ingredients) {
        var items = emptyGrid();
        for (int i = 0; i < ingredients.size() && i < 49; i++) {
            var ingredientItems = ingredients.get(i).getItems();
            if (ingredientItems.length > 0) {
                items.set((1 + i / 7) * 9 + 1 + i % 7, ingredientItems[0].copy());
            }
        }
        return items;
    }

    private static int outputSlotIndex(UnitedTerminalMenu menu) {
        for (int i = 0; i < menu.slots.size(); i++) {
            if (menu.getSlot(i) instanceof UnitedCraftingTerminalSlot) {
                return i;
            }
        }
        throw new IllegalStateException("United crafting output slot is missing");
    }

    private static int countPlayerItem(FakePlayer player, net.minecraft.world.item.Item item) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static double medianMicros(List<Long> nanos) {
        var sorted = new ArrayList<>(nanos);
        Collections.sort(sorted);
        return sorted.get(sorted.size() / 2) / 1_000.0;
    }

    private static void offsetShaped(GameTestHelper helper) {
        var items = emptyGrid();
        // The first 2x2 slot starts at 49 (row 5, column 4), not at the centered 3x3 area.
        items.set(49, new ItemStack(Items.OAK_PLANKS));
        items.set(50, new ItemStack(Items.OAK_PLANKS));
        items.set(58, new ItemStack(Items.OAK_PLANKS));
        items.set(59, new ItemStack(Items.OAK_PLANKS));
        var menu = createMenu(testPlayer(helper), items).menu;
        helper.assertTrue(menu.getCurrentUnitedRecipeRecord() != null
                        && menu.getCurrentUnitedRecipeRecord().assemble(helper.getLevel()).is(Items.CRAFTING_TABLE),
                "vanilla shaped recipes must match at an arbitrary 9x9 offset");
        for (int offsetY = 0; offsetY <= 6; offsetY++) {
            for (int offsetX = 0; offsetX <= 6; offsetX++) {
                var furnace = emptyGrid();
                for (int y = 0; y < 3; y++) {
                    for (int x = 0; x < 3; x++) {
                        if (x != 1 || y != 1) {
                            furnace.set((offsetY + y) * 9 + offsetX + x, new ItemStack(Items.COBBLESTONE));
                        }
                    }
                }
                var matched = menu.findUnitedRecipe(furnace);
                helper.assertTrue(matched != null && matched.assemble(helper.getLevel()).is(Items.FURNACE),
                        "3x3 shaped recipe must match at offset " + offsetX + "," + offsetY);
            }
        }
        var mirroredAxe = emptyGrid();
        for (int index : new int[]{6, 7, 16}) {
            mirroredAxe.set(index, new ItemStack(Items.IRON_INGOT));
        }
        for (int index : new int[]{15, 24}) {
            mirroredAxe.set(index, new ItemStack(Items.STICK));
        }
        var mirrored = menu.findUnitedRecipe(mirroredAxe);
        helper.assertTrue(mirrored != null && mirrored.assemble(helper.getLevel()).is(Items.IRON_AXE),
                "mirrored shaped recipe must match away from the center");
        helper.succeed();
    }

    private static void distributedShapeless(GameTestHelper helper) {
        var level = helper.getLevel();
        var recipe = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(r -> r instanceof ShapelessRecipe)
                .filter(r -> r.getResultItem(level.registryAccess()).is(Items.BOOK))
                .findFirst().orElse(null);
        helper.assertTrue(recipe != null, "minecraft:book recipe must be loaded");
        if (recipe == null) {
            return;
        }

        var items = emptyGrid();
        items.set(0, new ItemStack(Items.PAPER));
        items.set(20, new ItemStack(Items.PAPER));
        items.set(40, new ItemStack(Items.PAPER));
        items.set(80, new ItemStack(Items.LEATHER));
        var menu = createMenu(testPlayer(helper), items).menu;
        helper.assertTrue(menu.getCurrentUnitedRecipeRecord() != null
                        && menu.getCurrentUnitedRecipeRecord().assemble(level).is(Items.BOOK),
                "shapeless ingredients must match when distributed across the full 9x9 grid");

        items.set(79, new ItemStack(Items.NETHER_STAR));
        helper.assertTrue(createMenu(testPlayer(helper), items).menu.getCurrentUnitedRecipeRecord() == null,
                "an extra item must reject the distributed shapeless recipe");
        helper.succeed();
    }

    private static void immersiveEngineeringRemainder(GameTestHelper helper) {
        var level = helper.getLevel();
        var target = level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING).stream()
                .filter(r -> r instanceof IShapedRecipe<?>)
                .filter(r -> {
                    var id = ForgeRegistries.ITEMS.getKey(r.getResultItem(level.registryAccess()).getItem());
                    return id != null && id.equals(new ResourceLocation("immersiveengineering", "treated_wood_horizontal"));
                })
                .filter(r -> ((IShapedRecipe<?>) r).getRecipeWidth() == 3
                        && ((IShapedRecipe<?>) r).getRecipeHeight() == 3)
                .findFirst().orElse(null);
        var creosote = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("immersiveengineering", "creosote"));
        var filledBucket = creosote == null ? ItemStack.EMPTY : FluidUtil.getFilledBucket(new FluidStack(creosote, 1000));
        helper.assertTrue(target != null && !filledBucket.isEmpty(),
                "IE treated wood and its creosote bucket must be loaded");
        if (target == null || filledBucket.isEmpty()) {
            return;
        }

        var shaped = (IShapedRecipe<?>) target;
        var adapter = me.myogoo.extendedterminal.adapter.recipe.TableRecipeAdapters.of(target);
        helper.assertTrue(adapter instanceof me.myogoo.extendedterminal.api.adapter.recipe.IShapedTableRecipeAdapter<?> s
                        && s.width() == 3 && s.height() == 3,
                "IE treated wood must retain its Forge shaped dimensions during recipe transfer");
        var items = emptyGrid();
        int offset = (9 - shaped.getRecipeWidth()) / 2;
        int bucketSlot = (offset + 1) * 9 + offset + 1;
        for (int y = 0; y < shaped.getRecipeHeight(); y++) {
            for (int x = 0; x < shaped.getRecipeWidth(); x++) {
                int slot = (offset + y) * 9 + offset + x;
                items.set(slot, slot == bucketSlot ? filledBucket.copy() : new ItemStack(Items.OAK_PLANKS));
            }
        }
        var fixture = createMenu(testPlayer(helper), items);
        helper.assertTrue(fixture.menu.getCurrentUnitedRecipeRecord() != null,
                "IE fluid-aware shaped recipes must match in the United Terminal");
        if (fixture.menu.getCurrentUnitedRecipeRecord() == null) {
            return;
        }

        fixture.host.storage.add(filledBucket, 1);
        for (int i = 0; i < 8; i++) {
            fixture.host.storage.add(new ItemStack(Items.OAK_PLANKS), 1);
        }
        fixture.player.containerMenu = fixture.menu;
        int outputIndex = -1;
        for (int i = 0; i < fixture.menu.slots.size(); i++) {
            if (fixture.menu.getSlot(i) instanceof UnitedCraftingTerminalSlot) {
                outputIndex = i;
                break;
            }
        }
        helper.assertTrue(outputIndex >= 0, "United Terminal must expose its crafting output slot");
        if (outputIndex < 0) {
            return;
        }
        fixture.menu.doAction(fixture.player, InventoryAction.CRAFT_ITEM, outputIndex, 0);
        helper.assertTrue(fixture.menu.getCarried().is(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("immersiveengineering", "treated_wood_horizontal")))
                        && fixture.menu.getCarried().getCount() == 8,
                "crafting IE treated wood must put the result in the carried stack");
        helper.assertTrue(fixture.host.grid.getStackInSlot(bucketSlot).is(Items.BUCKET),
                "crafting IE treated wood must return an empty bucket to the United Terminal grid");
        helper.succeed();
    }

    private static void tippedArrows(GameTestHelper helper) {
        for (int offset : new int[]{0, 6}) {
            var items = emptyGrid();
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    items.set((offset + y) * 9 + offset + x, x == 1 && y == 1
                            ? PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.POISON)
                            : new ItemStack(Items.ARROW));
                }
            }
            var fixture = createMenu(testPlayer(helper), items);
            craftOnce(fixture);
            var result = fixture.menu.getCarried();
            helper.assertTrue(result.is(Items.TIPPED_ARROW) && result.getCount() == 8
                            && PotionUtils.getPotion(result) == Potions.POISON,
                    "offset tipped arrows must craft and preserve the potion at " + offset);
            helper.assertTrue(fixture.host.grid.isEmpty(), "tipped arrows must consume exactly the grid inputs");
        }
        helper.succeed();
    }

    private static void outsideTier(GameTestHelper helper) {
        var recipe = helper.getLevel().getRecipeManager().byKey(
                new ResourceLocation("extendedcrafting", "crystaltine_ingot")).orElseThrow();
        var items = emptyGrid();
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            items.set((1 + i / 7) * 9 + 1 + i % 7, recipe.getIngredients().get(i).getItems()[0].copy());
        }
        var fixture = createMenu(testPlayer(helper), items);
        fixture.menu.setSelectedRecipeKind(UnitedTerminalMenu.UnitedRecipeKind.EXTENDED_CRAFTING_ELITE);
        helper.assertTrue(fixture.menu.getCurrentUnitedRecipeRecord() != null, "the tier recipe must match first");
        items.set(0, new ItemStack(Items.DIAMOND));
        var blocked = createMenu(testPlayer(helper), items);
        blocked.menu.setSelectedRecipeKind(UnitedTerminalMenu.UnitedRecipeKind.EXTENDED_CRAFTING_ELITE);
        helper.assertTrue(blocked.menu.getCurrentUnitedRecipeRecord() == null,
                "items outside the selected tier must reject crafting");
        craftOnce(blocked);
        for (int i = 0; i < items.size(); i++) {
            helper.assertTrue(ItemStack.matches(items.get(i), blocked.host.grid.getStackInSlot(i)),
                    "rejected tier craft must preserve slot " + i);
        }
        helper.succeed();
    }

    private static void reinforcedCrateNbt(GameTestHelper helper) {
        var recipe = (CraftingRecipe) helper.getLevel().getRecipeManager().byKey(
                new ResourceLocation("immersiveengineering", "crafting/reinforced_crate")).orElseThrow();
        var items = shapedItems(recipe, 2, 5, false);
        var crate = items.get(6 * 9 + 3);
        var contents = NonNullList.withSize(27, ItemStack.EMPTY);
        contents.set(5, new ItemStack(Items.DIAMOND, 17));
        ContainerHelper.saveAllItems(crate.getOrCreateTag(), contents);
        crate.setHoverName(Component.literal("United crate contents"));
        var expectedTag = crate.getTag().copy();
        var fixture = createMenu(testPlayer(helper), items);
        craftOnce(fixture);
        var result = fixture.menu.getCarried();
        helper.assertTrue(result.is(ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("immersiveengineering", "reinforced_crate")))
                        && expectedTag.equals(result.getTag()),
                "reinforcing an offset crate must preserve its stored items and name");
        helper.assertTrue(fixture.host.grid.isEmpty(), "crate crafting must consume exactly the grid inputs");
        helper.succeed();
    }

    private static void mirroredFluidRemainder(GameTestHelper helper) {
        var recipe = (CraftingRecipe) helper.getLevel().getRecipeManager().byKey(
                new ResourceLocation("immersiveengineering", "crafting/torch")).orElseThrow();
        var creosote = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("immersiveengineering", "creosote"));
        for (boolean mirrored : new boolean[]{false, true}) {
            var items = shapedItems(recipe, 6, 7, mirrored);
            int bucketSlot = 7 * 9 + 7;
            items.set(bucketSlot, FluidUtil.getFilledBucket(new FluidStack(creosote, 1000)));
            var fixture = createMenu(testPlayer(helper), items);
            craftOnce(fixture);
            helper.assertTrue(fixture.menu.getCarried().is(Items.TORCH)
                            && fixture.menu.getCarried().getCount() == 12,
                    "shifted fluid recipe must craft with mirrored=" + mirrored);
            for (int i = 0; i < items.size(); i++) {
                var remaining = fixture.host.grid.getStackInSlot(i);
                helper.assertTrue(i == bucketSlot ? remaining.is(Items.BUCKET) && remaining.getCount() == 1 : remaining.isEmpty(),
                        "fluid remainder must return only to its original slot " + i);
            }
        }
        helper.succeed();
    }

    private static NonNullList<ItemStack> shapedItems(CraftingRecipe recipe, int left, int top, boolean mirrored) {
        var shape = (IShapedRecipe<?>) recipe;
        var items = emptyGrid();
        for (int y = 0; y < shape.getRecipeHeight(); y++) {
            for (int x = 0; x < shape.getRecipeWidth(); x++) {
                var ingredient = recipe.getIngredients().get(y * shape.getRecipeWidth() + x);
                if (!ingredient.isEmpty()) {
                    int targetX = mirrored ? shape.getRecipeWidth() - x - 1 : x;
                    items.set((top + y) * 9 + left + targetX, ingredient.getItems()[0].copy());
                }
            }
        }
        return items;
    }

    private static void craftOnce(MenuFixture fixture) {
        fixture.player.containerMenu = fixture.menu;
        for (int i = 0; i < fixture.menu.slots.size(); i++) {
            if (fixture.menu.getSlot(i) instanceof UnitedCraftingTerminalSlot) {
                fixture.menu.doAction(fixture.player, InventoryAction.CRAFT_ITEM, i, 0);
                return;
            }
        }
        throw new IllegalStateException("United crafting output slot is missing");
    }

    private static NonNullList<ItemStack> emptyGrid() {
        return NonNullList.withSize(81, ItemStack.EMPTY);
    }

    private static NonNullList<ItemStack> copyItems(List<ItemStack> source) {
        var copy = emptyGrid();
        for (int i = 0; i < source.size(); i++) {
            copy.set(i, source.get(i).copy());
        }
        return copy;
    }

    private static FakePlayer testPlayer(GameTestHelper helper) {
        return new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-united-crafting-test"));
    }

    private static MenuFixture createMenu(FakePlayer player, List<ItemStack> items) {
        var host = new TestHost(player);
        for (int i = 0; i < items.size(); i++) {
            host.grid.setItemDirect(i, items.get(i).copy());
        }
        return new MenuFixture(player, host, new UnitedTerminalMenu(UnitedTerminalMenu.TYPE, 1, player.getInventory(), host));
    }

    private record MenuFixture(FakePlayer player, TestHost host, UnitedTerminalMenu menu) {
    }

    private static final class TestHost extends ItemMenuHost implements IPortableTerminal, ISegmentedInventory {
        private final TestStorage storage = new TestStorage();
        private final AppEngInternalInventory grid = new AppEngInternalInventory(null, 81);
        private final ConfigManager config = new ConfigManager(() -> { });

        private TestHost(Player player) {
            super(player, null, new ItemStack(Items.CRAFTING_TABLE));
        }

        @Override
        public MEStorage getInventory() {
            return storage;
        }

        @Override
        public InternalInventory getSubInventory(ResourceLocation id) {
            return ETMenuType.UNITED_TERMINAL.getCraftingInventory().equals(id) ? grid : InternalInventory.empty();
        }

        @Override
        public double extractAEPower(double amt, Actionable mode, PowerMultiplier usePowerMultiplier) {
            return amt;
        }

        @Override
        public ConfigManager getConfigManager() {
            return config;
        }

        @Override
        public void returnToMainMenu(Player player, appeng.menu.ISubMenu subMenu) {
        }

        @Override
        public ItemStack getMainMenuIcon() {
            return new ItemStack(Items.CRAFTING_TABLE);
        }
    }

    private static final class TestStorage implements MEStorage {
        private final Map<AEKey, Long> amounts = new HashMap<>();

        private void add(ItemStack stack, long amount) {
            amounts.merge(AEItemKey.of(stack), amount, Long::sum);
        }

        private long amount(ItemStack stack) {
            return amounts.getOrDefault(AEItemKey.of(stack), 0L);
        }

        @Override
        public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
            long available = amounts.getOrDefault(what, 0L);
            long extracted = Math.min(available, amount);
            if (mode == Actionable.MODULATE && extracted > 0) {
                amounts.put(what, available - extracted);
            }
            return extracted;
        }

        @Override
        public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
            if (mode == Actionable.MODULATE) {
                amounts.merge(what, amount, Long::sum);
            }
            return amount;
        }

        @Override
        public void getAvailableStacks(KeyCounter out) {
            amounts.forEach(out::add);
        }

        @Override
        public Component getDescription() {
            return Component.literal("test");
        }
    }
}
