package me.myogoo.extendedterminal.gametest;

import appeng.helpers.InventoryAction;
import com.mojang.authlib.GameProfile;
import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wut.WUTHandler;
import io.netty.buffer.Unpooled;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.network.serverbound.ETFillSmithingGridFromRecipePacket;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.gametest.GameTestHolder;

import java.util.List;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
@net.minecraftforge.gametest.PrefixGameTestTemplate(false)
public final class PanelRegressionGameTests {
    @net.minecraft.gametest.framework.GameTest(template = "empty")
    @SuppressWarnings("deprecation")
    public static void legacyRecipeBuilder(GameTestHelper helper) {
        var id = new ResourceLocation(ExtendedTerminal.MODID, "legacy_builder_test");
        var recipe = me.myogoo.extendedterminal.util.recipe.ETShapedRecipeBuilder
                .shaped(Items.DIAMOND, 3).pattern("AA").pattern(" A").define('A', Items.STONE).build(id);
        helper.assertTrue(recipe.getResultItem(helper.getLevel().registryAccess()).getCount() == 3,
                "Legacy builder must preserve output count");
        var ingredients = recipe.getIngredients();
        helper.assertTrue(ingredients.size() == 4 && ingredients.get(2).isEmpty()
                        && ingredients.get(0).test(new ItemStack(Items.STONE))
                        && ingredients.get(1).test(new ItemStack(Items.STONE))
                        && ingredients.get(3).test(new ItemStack(Items.STONE)),
                "Legacy builder must preserve repeated symbols and spaces in pattern order");
        for (String pattern : List.of("AB", "")) {
            try {
                var invalid = me.myogoo.extendedterminal.util.recipe.ETShapedRecipeBuilder.shaped(Items.DIAMOND, 1);
                if (!pattern.isEmpty()) invalid.pattern(pattern).define('A', Items.STONE);
                invalid.build(id);
                helper.fail("Undefined symbols and missing patterns must be rejected");
            } catch (IllegalStateException expected) {
                // Legacy build must reject invalid patterns before creating a recipe.
            }
        }
        helper.succeed();
    }

    @GameTestGenerator
    public static List<TestFunction> tests() {
        return ModList.get().isLoaded("ae2wtlib") ? Tests.tests() : List.of();
    }

    private static final class Tests {
        private static List<TestFunction> tests() {
            return List.of(
                    new TestFunction("extendedterminal", "panel_invalid_smithing_counts", "extendedterminal:empty", 100, 0, true, Tests::invalidCounts),
                    new TestFunction("extendedterminal", "panel_selection_and_stale_output", "extendedterminal:empty", 100, 0, true, Tests::selectionAndStaleOutput),
                    new TestFunction("extendedterminal", "wireless_united_magnet_lookup", "extendedterminal:empty", 100, 0, true, Tests::magnetLookup));
        }

        private static void invalidCounts(GameTestHelper helper) {
            for (int count : new int[]{-1, 0, 2, 4, Integer.MAX_VALUE}) {
                var buffer = new FriendlyByteBuf(Unpooled.buffer());
                try {
                    buffer.writeBoolean(false);
                    buffer.writeInt(count);
                    boolean rejected = false;
                    try {
                        new ETFillSmithingGridFromRecipePacket(buffer);
                    } catch (IllegalArgumentException expected) {
                        rejected = true;
                    }
                    helper.assertTrue(rejected && buffer.readerIndex() == 5, "Reject count before allocating/reading templates");
                } finally {
                    buffer.release();
                }
            }
            helper.succeed();
        }

        private static void selectionAndStaleOutput(GameTestHelper helper) {
            var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "panel-test"));
            var stack = new ItemStack(WTItems.WIRELESS_ET_TERMINAL);
            var host = host(player, stack);
            host.setMode(ETTerminalMode.STONECUTTING);
            host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).setItemDirect(0, new ItemStack(Items.STONE, 2));
            var recipe = helper.getLevel().getRecipeManager().getRecipesFor(RecipeType.STONECUTTING,
                    new SimpleContainer(new ItemStack(Items.STONE)), helper.getLevel()).stream()
                    .filter(r -> r.getResultItem(helper.getLevel().registryAccess()).is(Items.STONE_SLAB)).findFirst().orElseThrow();
            var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
            player.containerMenu = menu;
            menu.setStoneCutterRecipeId(recipe.getId());
            menu.removed(player);
            host = host(player, ItemStack.of(stack.save(new CompoundTag())));
            menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
            player.containerMenu = menu;
            var output = menu.getSlots(me.myogoo.extendedterminal.menu.ETSlotSemantics.STONECUTTING_RESULT).get(0);
            helper.assertTrue(recipe.getId().equals(menu.getStoneCutterRecipeId()) && output.getItem().is(Items.STONE_SLAB),
                    "Selection and output survive real wireless NBT reopen");
            var input = host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId());
            var preview = output.getItem().copy();
            input.setItemDirect(0, new ItemStack(Items.DIRT, 2));
            output.set(preview);
            menu.doAction(player, InventoryAction.CRAFT_ITEM, output.index, 0);
            helper.assertTrue(menu.getCarried().isEmpty() && input.getStackInSlot(0).getCount() == 2,
                    "Stale stonecutting output cannot consume mismatching input");
            input.setItemDirect(0, new ItemStack(Items.STONE, 2));
            menu.setStoneCutterRecipeId(recipe.getId());
            menu.setMode(ETTerminalMode.CRAFTING);
            output.set(preview);
            menu.doAction(player, InventoryAction.CRAFT_ITEM, output.index, 0);
            helper.assertTrue(menu.getCarried().isEmpty() && input.getStackInSlot(0).getCount() == 2,
                    "Hidden stonecutting panel cannot craft");
            for (var id : new ResourceLocation[]{null, new ResourceLocation("minecraft", "missing_recipe"), new ResourceLocation("minecraft", "honey_block")}) {
                try {
                    var handle = ETFillSmithingGridFromRecipePacket.class.getDeclaredMethod("handleOnServer", net.minecraft.server.level.ServerPlayer.class);
                    handle.setAccessible(true);
                    handle.invoke(new ETFillSmithingGridFromRecipePacket(id, NonNullList.withSize(3, ItemStack.EMPTY), false), player);
                } catch (ReflectiveOperationException exception) {
                    throw new AssertionError(exception);
                }
                helper.assertTrue(menu.getMode() == ETTerminalMode.CRAFTING && input.getStackInSlot(0).getCount() == 2,
                        "Invalid smithing recipes preserve mode and inventory");
            }
            menu.setMode(ETTerminalMode.STONECUTTING);
            menu.setStoneCutterRecipeId(recipe.getId());
            menu.doAction(player, InventoryAction.CRAFT_ITEM, output.index, 0);
            helper.assertTrue(menu.getCarried().is(Items.STONE_SLAB) && menu.getCarried().getCount() == 2
                            && input.getStackInSlot(0).getCount() == 1,
                    "Unlinked wireless host still crafts valid local input when offline crafting is enabled");
            helper.succeed();
        }

        private static ETWTHost host(FakePlayer player, ItemStack stack) {
            return new ETWTHost(player, null, stack, (p, menu) -> {});
        }

        private static void magnetLookup(GameTestHelper helper) {
            for (var type : new ETMenuType[]{ETMenuType.ET_TERMINAL, ETMenuType.UNITED_TERMINAL}) {
                for (boolean universal : new boolean[]{false, true}) {
                    var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "magnet-test"));
                    var definition = WUTHandler.wirelessTerminals.get(type.getWTIdAsString());
                    var stack = universal ? new ItemStack((net.minecraft.world.item.Item) AE2wtlib.UNIVERSAL_TERMINAL)
                            : new ItemStack((net.minecraft.world.item.Item) definition.item());
                    if (universal) {
                        stack.getOrCreateTag().putBoolean(type.getWTIdAsString(), true);
                        stack.getOrCreateTag().putString("currentTerminal", type.getWTIdAsString());
                    }
                    player.getInventory().setItem(0, stack);
                    var handler = CraftingTerminalHandler.getCraftingTerminalHandler(player);
                    helper.assertTrue(handler.getCraftingTerminal() == stack && handler.getMagnetHost() != null,
                            "Magnet finds standalone and single-terminal WUT for ET/United");
                    player.getInventory().setItem(0, ItemStack.EMPTY);
                    helper.assertTrue(handler.getCraftingTerminal().isEmpty() && handler.getMagnetHost() == null,
                            "Removing terminal invalidates magnet lookup");
                    CraftingTerminalHandler.removePlayer(player);
                }
            }
            helper.succeed();
        }
    }
}
