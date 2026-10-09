package me.myogoo.extendedterminal.gametest;

import appeng.api.inventories.InternalInventory;
import appeng.menu.locator.MenuLocators;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Unit;
import de.mari_023.ae2wtlib.AE2wtlibItems;
import de.mari_023.ae2wtlib.api.AE2wtlibComponents;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.ItemWT;
import de.mari_023.ae2wtlib.wct.CraftingTerminalHandler;
import de.mari_023.ae2wtlib.wut.WTDefinitions;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.init.ETDataComponent;
import me.myogoo.extendedterminal.init.wt.WTItems;
import me.myogoo.extendedterminal.me.host.ETWTHost;
import me.myogoo.extendedterminal.me.host.UnitedWTHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.myotus.api.MyotusAPI;
import me.myogoo.myotus.api.annotation.mods.AE2WTLib;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ExtendedTerminal.MODID)
public final class WirelessTerminalGameTests {
    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.TEST_FUNCTION
                && MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            Tests.registerFunctions(event);
        }
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        if (MyotusAPI.integrations().isLoaded(AE2WTLib.class)) {
            Tests.registerTests(event);
        }
    }

    // Keep WTLib references out of the always-loaded subscriber's fields and method signatures.
    private static final class Tests {
        private static final List<TestCase> TESTS = List.of(
                new TestCase("wireless_shared_grid_preserves_tail", Tests::sharedGridPreservesTail),
                new TestCase("wireless_legacy_sparse_grid_roundtrip", Tests::legacySparseGridRoundtrip),
                new TestCase("wireless_magnet_terminal_lookup", Tests::magnetTerminalLookup),
                new TestCase("wireless_magnet_priority_and_cache", Tests::magnetPriorityAndCache));

        private static void registerFunctions(RegisterEvent event) {
            for (var test : TESTS) {
                event.register(Registries.TEST_FUNCTION, ExtendedTerminal.makeId(test.name()), test::action);
            }
        }

        private static void registerTests(RegisterGameTestsEvent event) {
            var environment = event.registerEnvironment(ExtendedTerminal.makeId("wireless_terminal"));
            for (var test : TESTS) {
                var id = ExtendedTerminal.makeId(test.name());
                event.registerTest(id, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id),
                        new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
            }
        }

        private static void sharedGridPreservesTail(GameTestHelper helper) {
            for (var selected : new WTDefinition[]{definition(false), definition(true), WTDefinitions.CRAFTING}) {
                var player = player(helper);
                var stack = universal(selected, definition(false), definition(true), WTDefinitions.CRAFTING);
                var contents = NonNullList.withSize(81, ItemStack.EMPTY);
                contents.set(0, new ItemStack(Items.DIAMOND, 2));
                contents.set(8, new ItemStack(Items.EMERALD, 3));
                contents.set(80, new ItemStack(Items.GOLD_INGOT, 4));
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
                                && unitedGrid.getStackInSlot(8).getCount() == 2,
                        "ET edits remain shared after serialized reload, without duplicating inputs");
                helper.assertTrue(unitedGrid.getStackInSlot(80).is(Items.GOLD_INGOT)
                                && unitedGrid.getStackInSlot(80).getCount() == 4,
                        "ET saves preserve United's final slot regardless of selected terminal");
                grid(player, stack, false).clear();
                unitedGrid = grid(player, reload(helper, stack), true);
                helper.assertTrue(unitedGrid.getStackInSlot(0).isEmpty() && unitedGrid.getStackInSlot(8).isEmpty()
                                && unitedGrid.getStackInSlot(80).getCount() == 4,
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
                    var handler = new CraftingTerminalHandler(player);
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
            var et = WTItems.WIRELESS_ET_TERMINAL.stack();
            var united = WTItems.WIRELESS_UNITED_TERMINAL.stack();
            var wct = AE2wtlibItems.WIRELESS_CRAFTING_TERMINAL.stack();
            player.getInventory().setItem(0, et);
            player.getInventory().setItem(1, united);
            player.getInventory().setItem(2, wct);
            var handler = new CraftingTerminalHandler(player);
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

        private static FakePlayer player(GameTestHelper helper) {
            return new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "wireless-test"));
        }

        private static WTDefinition definition(boolean united) {
            return WTDefinition.of((united ? ETMenuType.UNITED_TERMINAL : ETMenuType.ET_TERMINAL).getWTIdAsString());
        }

        private static ItemStack universal(WTDefinition selected, WTDefinition... installed) {
            var stack = AE2wtlibItems.UNIVERSAL_TERMINAL.stack();
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

        private record TestCase(String name, Consumer<GameTestHelper> action) {
        }
    }
}
