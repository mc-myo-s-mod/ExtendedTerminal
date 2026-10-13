package me.myogoo.extendedterminal.gametest;

import appeng.api.inventories.InternalInventory;
import appeng.menu.locator.MenuLocators;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Unit;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.wut.WTDefinitions;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETDataComponent;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.menu.recipe.ETRecipeTransferSupport;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
@PrefixGameTestTemplate(false)
public final class WirelessTerminalGameTests {
    @GameTestGenerator
    public static List<TestFunction> optionalTests() {
        return MyotusAPI.integrations().isLoaded(AE2WTLib.class) ? Tests.tests() : List.of();
    }

    // Keep optional WTLib references behind the integration gate.
    private static final class Tests {
        private static List<TestFunction> tests() {
            return List.of(
                    new TestFunction("extendedterminal", "wireless_magnet_terminal_lookup", "extendedterminal:empty",
                            100, 0, true, Tests::magnetTerminalLookup),
                    new TestFunction("extendedterminal", "wireless_magnet_priority_and_cache", "extendedterminal:empty",
                            100, 0, true, Tests::magnetPriorityAndCache),
                    new TestFunction("extendedterminal", "wireless_shared_grid_preserves_tail", "extendedterminal:empty",
                            100, 0, true, Tests::sharedGridPreservesTail),
                    new TestFunction("extendedterminal", "wireless_legacy_sparse_grid_roundtrip", "extendedterminal:empty",
                            100, 0, true, Tests::legacySparseGridRoundtrip),
                    new TestFunction("extendedterminal", "wireless_packet_panel_roundtrip", "extendedterminal:empty",
                            100, 0, true, Tests::packetPanelRoundtrip),
                    new TestFunction("extendedterminal", "wireless_cross_panel_roundtrip", "extendedterminal:empty",
                            100, 0, true, Tests::crossPanelRoundtrip),
                    new TestFunction("extendedterminal", "wireless_blocked_destination_preserves_source", "extendedterminal:empty",
                            100, 0, true, Tests::blockedDestination),
                    new TestFunction("extendedterminal", "wireless_stone_selection_roundtrip", "extendedterminal:empty",
                            100, 0, true, Tests::stoneSelectionRoundtrip),
                    new TestFunction("extendedterminal", "wireless_anvil_experience_cost", "extendedterminal:empty",
                            100, 0, true, Tests::anvilExperienceCost));
        }
        private static void sharedGridPreservesTail(GameTestHelper helper) {
            for (var selected : new WTDefinition[]{definition(false), definition(true), WTDefinitions.CRAFTING}) {
                var player = player(helper);
                var stack = universal(selected, definition(false), definition(true), WTDefinitions.CRAFTING);
                var contents = NonNullList.withSize(81, ItemStack.EMPTY);
                contents.set(0, new ItemStack(Items.DIAMOND, 2));
                contents.set(8, new ItemStack(Items.EMERALD, 3));
                contents.set(80, new ItemStack(Items.GOLD_INGOT, 4));
                contents.get(0).set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("front"));
                contents.get(80).set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("tail"));
                stack.set(ETDataComponent.CRAFTING_INV, ItemContainerContents.fromItems(contents));
                helper.assertTrue(grid(player, stack, true).size() == 81, "United exposes 81 slots");

                var etGrid = grid(player, stack, false);
                helper.assertTrue(etGrid.size() == 9, "ET exposes only nine slots");
                var extracted = etGrid.extractItem(0, 1, false);
                helper.assertTrue(extracted.is(Items.DIAMOND) && extracted.getCount() == 1,
                        "ET extracts once from the shared first nine slots");
                etGrid.setItemDirect(8, new ItemStack(Items.EMERALD, 2));

                stack = reload(helper, stack);
                var unitedGrid = grid(player, stack, true);
                helper.assertTrue(unitedGrid.getStackInSlot(0).getCount() == 1
                                && unitedGrid.getStackInSlot(8).getCount() == 2
                                && ItemStack.isSameItemSameComponents(unitedGrid.getStackInSlot(0), contents.get(0)),
                        "ET edits remain shared after serialized reload, without duplicating inputs");
                helper.assertTrue(unitedGrid.getStackInSlot(80).is(Items.GOLD_INGOT)
                                && ItemStack.matches(unitedGrid.getStackInSlot(80), contents.get(80)),
                        "ET saves preserve United's final slot regardless of selected terminal");
                grid(player, stack, false).clear();
                unitedGrid = grid(player, reload(helper, stack), true);
                helper.assertTrue(unitedGrid.getStackInSlot(0).isEmpty() && unitedGrid.getStackInSlot(8).isEmpty()
                                && ItemStack.matches(unitedGrid.getStackInSlot(80), contents.get(80)),
                        "Clearing the ET view clears only the first nine slots");
            }
            helper.succeed();
        }

        private static void legacySparseGridRoundtrip(GameTestHelper helper) {
            for (boolean united : new boolean[]{false, true}) {
                for (boolean universal : new boolean[]{false, true}) {
                    var player = player(helper);
                    var definition = definition(united);
                    var stack = universal ? universal(definition, definition) : new ItemStack(definition.item());
                    // Both an old 9-slot grid and an old 81-slot grid trim to this same one-entry component.
                    stack.set(ETDataComponent.CRAFTING_INV,
                            ItemContainerContents.fromItems(List.of(new ItemStack(Items.DIAMOND, 7))));
                    var inventory = grid(player, stack, united);
                    helper.assertTrue(inventory.size() == (united ? 81 : 9)
                                    && inventory.getStackInSlot(0).getCount() == 7,
                            "Sparse legacy contents load for standalone and universal terminals");
                    inventory.setItemDirect(8, new ItemStack(Items.EMERALD, 2));
                    var reopened = grid(player, reload(helper, stack), united);
                    helper.assertTrue(reopened.getStackInSlot(0).is(Items.DIAMOND)
                                    && reopened.getStackInSlot(0).getCount() == 7
                                    && reopened.getStackInSlot(8).getCount() == 2,
                            "Sparse legacy items survive editing and serialized reload exactly once");
                }
            }
            helper.succeed();
        }

        private static void magnetTerminalLookup(GameTestHelper helper) {
            for (boolean united : new boolean[]{false, true}) {
                for (boolean universal : new boolean[]{false, true}) {
                    var player = player(helper);
                    var definition = definition(united);
                    var stack = universal ? universal(definition, definition) : new ItemStack(definition.item());
                    player.getInventory().setItem(0, stack);
                    var handler = new de.mari_023.ae2wtlib.wct.CraftingTerminalHandler(player);
                    helper.assertTrue(handler.getCraftingTerminal() == stack,
                            "Magnet lookup finds ET or United, including single-terminal WUT");
                    var locator = handler.getLocator();
                    var magnet = handler.getMagnetHost();
                    helper.assertTrue(magnet != null && handler.getMagnetHost() == magnet
                                    && handler.getLocator() == locator,
                            "Valid terminal locator and magnet host remain cached");
                    player.getInventory().setItem(0, ItemStack.EMPTY);
                    helper.assertTrue(handler.getCraftingTerminal().isEmpty() && handler.getMagnetHost() == null,
                            "Removing the terminal invalidates the lookup and magnet cache");
                }
            }
            helper.succeed();
        }

        private static void magnetPriorityAndCache(GameTestHelper helper) {
            var player = player(helper);
            var et = new ItemStack(definition(false).item());
            var united = new ItemStack(definition(true).item());
            var wct = new ItemStack(AE2wtlibItems.WIRELESS_CRAFTING_TERMINAL);
            player.getInventory().setItem(0, et);
            player.getInventory().setItem(1, united);
            player.getInventory().setItem(2, wct);
            var handler = new de.mari_023.ae2wtlib.wct.CraftingTerminalHandler(player);
            helper.assertTrue(handler.getCraftingTerminal() == wct, "Initial lookup prioritizes vanilla WCT");
            player.getInventory().setItem(2, ItemStack.EMPTY);
            helper.assertTrue(handler.getCraftingTerminal() == et, "ET retains fallback priority over United");
            var etMagnet = handler.getMagnetHost();
            player.getInventory().setItem(2, wct);
            helper.assertTrue(handler.getCraftingTerminal() == et && handler.getMagnetHost() == etMagnet,
                    "A valid cached ET locator retains the existing caching behavior");
            player.getInventory().setItem(0, ItemStack.EMPTY);
            helper.assertTrue(handler.getCraftingTerminal() == wct, "Invalid cache searches vanilla WCT first");
            player.getInventory().setItem(2, ItemStack.EMPTY);
            helper.assertTrue(handler.getCraftingTerminal() == united && handler.getMagnetHost() != etMagnet,
                    "United fallback replaces the invalidated ET magnet host");
            var unitedMagnet = handler.getMagnetHost();
            player.getInventory().setItem(1, ItemStack.EMPTY);
            player.getInventory().setItem(3, united);
            helper.assertTrue(handler.getCraftingTerminal() == united && handler.getMagnetHost() != unitedMagnet,
                    "Moving United to another slot invalidates and rebuilds its cached magnet host");
            helper.succeed();
        }

        private static void packetPanelRoundtrip(GameTestHelper helper) {
            for (boolean universal : new boolean[]{false, true}) {
                for (int occupied : new int[]{0, 63, 64}) {
                    var player = player(helper);
                    var stack = universal ? universal(definition(false), definition(false), definition(true))
                            : new ItemStack(definition(false).item());
                    var host = host(player, stack);
                    host.setMode(ETTerminalMode.ANVIL);
                    var source = new ItemStack(Items.STONE, 3);
                    source.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("source"));
                    host.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).setItemDirect(0, source);
                    if (occupied > 0) {
                        host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).setItemDirect(0, new ItemStack(Items.DIAMOND, occupied));
                    }
                    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                        player.getInventory().setItem(i, new ItemStack(Items.DIRT, 64));
                    }
                    // Leave space for only one of the blocking diamonds.
                    player.getInventory().setItem(0, new ItemStack(Items.DIAMOND, 63));
                    var recipe = helper.getLevel().getRecipeManager().getRecipesFor(RecipeType.STONECUTTING,
                            new SingleRecipeInput(new ItemStack(Items.STONE)), helper.getLevel()).getFirst().id();
                    var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
                    player.containerMenu = menu;
                    new me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket(recipe,
                            new ItemStack(Items.STONE), false).handleOnServer(player);
                    menu.removed(player);
                    var reopened = host(player, reload(helper, stack));
                    var savedSource = reopened.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0);
                    var savedTarget = reopened.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).getStackInSlot(0);
                    helper.assertTrue(occupied == 0
                                    ? savedSource.getCount() == 2 && savedTarget.getCount() == 1
                                        && ItemStack.isSameItemSameComponents(savedSource, source)
                                        && ItemStack.isSameItemSameComponents(savedTarget, source)
                                    : ItemStack.matches(savedSource, source) && savedTarget.is(Items.DIAMOND)
                                        && savedTarget.getCount() == occupied - 1 && player.getInventory().getItem(0).getCount() == 64,
                            "Packet close/codec/reopen conserves input and partial destination evacuation: " + occupied);
                }
            }
            helper.succeed();
        }
        private static ETWTHost host(FakePlayer player, ItemStack stack) {
            return new ETWTHost((ItemWT) stack.getItem(), player, MenuLocators.forStack(stack), (p, menu) -> {});
        }

        private static void crossPanelRoundtrip(GameTestHelper helper) {
            for (boolean universal : new boolean[]{false, true}) {
                for (var source : ETTerminalMode.values()) {
                    for (var target : ETTerminalMode.values()) {
                        if (source == target) continue;
                        var player = player(helper);
                        var stack = universal ? universal(definition(false), definition(false), definition(true))
                                : new ItemStack(definition(false).item());
                        var host = host(player, stack);
                        host.setMode(target);
                        var original = new ItemStack(Items.DIAMOND, 3);
                        original.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("panel input"));
                        host.getSubInventory(source.getInventoryId()).setItemDirect(0, original);
                        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
                        var moved = ETRecipeTransferSupport.takeIngredientFromOtherGrid(menu, Ingredient.of(Items.DIAMOND));
                        helper.assertTrue(moved.is(Items.DIAMOND) && moved.getCount() == 1, "Extract exactly one input");
                        host.getSubInventory(target.getInventoryId()).setItemDirect(0, moved);
                        var reopened = host(player, reload(helper, stack));
                        helper.assertTrue(reopened.getSubInventory(source.getInventoryId()).getStackInSlot(0).getCount() == 2
                                        && reopened.getSubInventory(target.getInventoryId()).getStackInSlot(0).getCount() == 1
                                        && ItemStack.isSameItemSameComponents(reopened.getSubInventory(source.getInventoryId()).getStackInSlot(0), original)
                                        && ItemStack.isSameItemSameComponents(reopened.getSubInventory(target.getInventoryId()).getStackInSlot(0), original),
                                "Serialized source and target conserve three items: " + source + " -> " + target);
                    }
                }
            }
            helper.succeed();
        }

        private static void blockedDestination(GameTestHelper helper) {
            var player = player(helper);
            var stack = new ItemStack(definition(false).item());
            var host = host(player, stack);
            host.setMode(ETTerminalMode.STONECUTTING);
            host.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).setItemDirect(0, new ItemStack(Items.STONE, 3));
            host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).setItemDirect(0, new ItemStack(Items.DIAMOND, 64));
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                player.getInventory().setItem(i, new ItemStack(Items.DIRT, 64));
            }
            var recipe = helper.getLevel().getRecipeManager().getRecipesFor(RecipeType.STONECUTTING,
                    new SingleRecipeInput(new ItemStack(Items.STONE)), helper.getLevel()).getFirst().id();
            var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
            player.containerMenu = menu;
            new me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket(recipe,
                    new ItemStack(Items.STONE), false).handleOnServer(player);
            var reopened = host(player, reload(helper, stack));
            helper.assertTrue(reopened.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0).getCount() == 3
                            && reopened.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).getStackInSlot(0).getCount() == 64,
                    "Full destination and player inventory preserve source and destination after serialized reopen");
            helper.succeed();
        }
        private static void stoneSelectionRoundtrip(GameTestHelper helper) {
            var player = player(helper);
            var stack = new ItemStack(definition(false).item());
            var host = host(player, stack);
            host.setMode(ETTerminalMode.STONECUTTING);
            host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId()).setItemDirect(0, new ItemStack(Items.STONE));
            var recipe = helper.getLevel().getRecipeManager().getRecipesFor(RecipeType.STONECUTTING,
                    new SingleRecipeInput(new ItemStack(Items.STONE)), helper.getLevel()).getFirst().id();
            var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
            menu.setStoneCutterRecipeId(recipe);
            stack = reload(helper, stack);
            host = host(player, stack);
            menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
            helper.assertTrue(recipe.equals(menu.getStoneCutterRecipeId()), "Selection survives component roundtrip and reopen");
            menu.setStoneCutterRecipeId(null);
            helper.assertTrue(host(player, reload(helper, stack)).getStoneCutterRecipeId() == null,
                    "Clearing the selection removes its old saved component value");
            helper.succeed();
        }

        private static void anvilExperienceCost(GameTestHelper helper) {
            var player = player(helper);
            var host = host(player, new ItemStack(definition(false).item()));
            host.setMode(ETTerminalMode.ANVIL);
            var input = new ItemStack(Items.DIAMOND);
            input.set(DataComponents.REPAIR_COST, 4);
            var inventory = host.getSubInventory(ETTerminalMode.ANVIL.getInventoryId());
            inventory.setItemDirect(0, input);
            var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
            menu.setAnvilItemName("XP regression");
            helper.assertTrue(menu.getAnvilCost() == 5, "Rename costs five levels");
            helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player), "Level zero cannot pay");
            player.giveExperiencePoints(7);
            helper.assertTrue(!menu.canPayAnvilCost(player) && !menu.consumeAnvilExperience(player)
                            && player.experienceLevel == 1 && ItemStack.matches(input, inventory.getStackInSlot(0)),
                    "Insufficient XP preserves both XP and input");
            player.giveExperiencePoints(48);
            helper.assertTrue(menu.consumeAnvilExperience(player) && player.experienceLevel == 0, "Exact XP pays once");
            player.giveExperiencePoints(173);
            boolean apothic = net.neoforged.fml.ModList.get().isLoaded("apotheosis")
                    || net.neoforged.fml.ModList.get().isLoaded("apothic_enchanting");
            helper.assertTrue(menu.consumeAnvilExperience(player) && player.totalExperience == (apothic ? 118 : 63),
                    "Vanilla progress fraction and special absolute XP cost remain distinct");
            player.getAbilities().instabuild = true;
            int before = player.totalExperience;
            helper.assertTrue(menu.canPayAnvilCost(player) && menu.consumeAnvilExperience(player)
                            && player.totalExperience == before, "Creative bypass does not debit XP");
            helper.succeed();
        }

        private static FakePlayer player(GameTestHelper helper) {
            return new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "wireless-test"));
        }

        private static WTDefinition definition(boolean united) {
            return WTDefinition.of((united ? ETMenuType.UNITED_TERMINAL : ETMenuType.ET_TERMINAL).getWTIdAsString());
        }

        private static ItemStack universal(WTDefinition selected, WTDefinition... installed) {
            var stack = new ItemStack(AE2wtlibItems.UNIVERSAL_TERMINAL);
            for (var definition : installed) {
                stack.set(definition.componentType(), Unit.INSTANCE);
            }
            stack.set(AE2wtlibComponents.CURRENT_TERMINAL, selected);
            return stack;
        }

        private static InternalInventory grid(FakePlayer player, ItemStack stack, boolean united) {
            var item = (ItemWT) stack.getItem();
            var locator = MenuLocators.forStack(stack);
            return united
                    ? new UnitedWTHost(item, player, locator, (p, menu) -> {}).getSubInventory(
                            ETMenuType.UNITED_TERMINAL.getCraftingInventory())
                    : new ETWTHost(item, player, locator, (p, menu) -> {}).getSubInventory(
                            ETMenuType.ET_TERMINAL.getCraftingInventory());
        }

        private static ItemStack reload(GameTestHelper helper, ItemStack stack) {
            var ops = helper.getLevel().registryAccess().createSerializationContext(NbtOps.INSTANCE);
            var saved = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow();
            return ItemStack.CODEC.parse(ops, saved).getOrThrow();
        }

    }
}
