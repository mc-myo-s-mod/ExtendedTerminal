package me.myogoo.extendedterminal.gametest;

import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.me.storage.NullInventory;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.util.ConfigManager;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.TableTerminalBaseMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.EndTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.ExtremeTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.NetherTerminalMenu;
import me.myogoo.extendedterminal.menu.avaritiaRe.SculkTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.FillUnitedGridPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GameTestHolder(ExtendedTerminal.MODID)
@PrefixGameTestTemplate(false)
public final class TableTransferGameTests {
    @GameTestGenerator
    public static List<TestFunction> optionalIntegrationTests() {
        var tests = new ArrayList<TestFunction>();
        if (ETMenuType.SCULK_TERMINAL.canLoad()) {
            tests.add(new TestFunction("extendedterminal", "tabletransfergametests.tiermenutransfers",
                    "extendedterminal:empty", 100, 0, true, TableTransferGameTests::tierMenuTransfers));
        }
        if (UnitedTerminalMenu.getActiveRecipeTypes().size() > 1) {
            tests.add(new TestFunction("extendedterminal", "tabletransfergametests.unitedvalidationbeforeselection",
                    "extendedterminal:empty", 100, 0, true, TableTransferGameTests::unitedValidationBeforeSelection));
        }
        return tests;
    }

    @GameTest(template = "empty")
    public static void smithingInvalidCounts(GameTestHelper helper) {
        for (int count : new int[]{-1, 0, 2, 4, Integer.MAX_VALUE}) {
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
            try {
                buffer.writeBoolean(false);
                buffer.writeInt(count);
                expectRejected(helper, () -> me.myogoo.extendedterminal.network.serverbound.FillSmithingGridFromRecipePacket.decode(buffer));
                helper.assertTrue(buffer.readerIndex() == 5, "Reject smithing size before reading or allocating templates");
            } finally {
                buffer.release();
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void gridIndexBounds(GameTestHelper helper) {
        for (int side : new int[]{3, 5, 7, 9}) {
            var grid = me.myogoo.extendedterminal.util.TableCraftingHelper.indexToCoordinate(side * side, 2, 2);
            helper.assertTrue(!grid.test(-1) && !grid.test(side * side), "Negative and end-exclusive grid indexes reject");
            int selected = 0;
            for (int i = 0; i < side * side; i++) if (grid.test(i)) selected++;
            helper.assertTrue(selected == 4, "Exactly four centered grid positions selected");
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void invalidCounts(GameTestHelper helper) {
        for (boolean united : new boolean[]{false, true}) {
            for (int count : new int[]{-1, 0, 8, 82, Integer.MAX_VALUE}) {
                var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
                try {
                    buffer.writeBoolean(false);
                    buffer.writeInt(count);
                    expectRejected(helper, () -> {
                        if (united) {
                            FillUnitedGridPacket.decode(buffer);
                        } else {
                            FillTableCraftingGridFromRecipePacket.decode(buffer);
                        }
                    });
                    helper.assertTrue(buffer.readerIndex() == 5, "Reject invalid count before reading templates");
                } finally {
                    buffer.release();
                }
                if (count >= 0 && count <= 82) {
                    expectRejected(helper, () -> new FillTableCraftingGridFromRecipePacket(null,
                            NonNullList.withSize(count, ItemStack.EMPTY), false, -1, -1));
                }
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void dimensionsAndRoundTrip(GameTestHelper helper) {
        for (int size : new int[]{9, 25, 49, 81}) {
            int side = (int) Math.sqrt(size);
            for (int[] dimensions : new int[][]{{-1, 2}, {2, -1}, {0, 2}, {side + 1, 2}}) {
                expectRejected(helper, () -> new FillTableCraftingGridFromRecipePacket(null,
                        NonNullList.withSize(size, ItemStack.EMPTY), false, dimensions[0], dimensions[1]));
                if (size == 81) {
                    expectRejected(helper, () -> new FillUnitedGridPacket(null,
                            NonNullList.withSize(size, ItemStack.EMPTY), false,
                            dimensions[0], dimensions[1], MyoRecipeType.VANILLA));
                }
            }
            for (int dimension : new int[]{-1, side}) {
                var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
                try {
                    var packet = new FillTableCraftingGridFromRecipePacket(null,
                            NonNullList.withSize(size, ItemStack.EMPTY), false, dimension, dimension);
                    packet.write(buffer);
                    FillTableCraftingGridFromRecipePacket.decode(buffer);
                    helper.assertTrue(!buffer.isReadable(), "Valid square-grid packet must round-trip");
                    if (size == 81) {
                        var united = new FillUnitedGridPacket(null, NonNullList.withSize(size, ItemStack.EMPTY),
                                false, dimension, dimension, MyoRecipeType.VANILLA);
                        united.write(buffer);
                        FillUnitedGridPacket.decode(buffer);
                        helper.assertTrue(!buffer.isReadable(), "Valid United packet must round-trip");
                    }
                } finally {
                    buffer.release();
                }
            }
        }
        helper.succeed();
    }

    public static void tierMenuTransfers(GameTestHelper helper) {
        for (var type : new ETMenuType[]{ETMenuType.SCULK_TERMINAL, ETMenuType.NETHER_TERMINAL,
                ETMenuType.END_TERMINAL, ETMenuType.EXTREME_TERMINAL}) {
            var player = player(helper);
            var host = new TestHost(player, type.getGridSize());
            TableTerminalBaseMenu<?> menu = switch (type) {
                case SCULK_TERMINAL -> new SculkTerminalMenu(SculkTerminalMenu.TYPE, 0, player.getInventory(), host);
                case NETHER_TERMINAL -> new NetherTerminalMenu(NetherTerminalMenu.TYPE, 0, player.getInventory(), host);
                case END_TERMINAL -> new EndTerminalMenu(EndTerminalMenu.TYPE, 0, player.getInventory(), host);
                case EXTREME_TERMINAL -> new ExtremeTerminalMenu(ExtremeTerminalMenu.TYPE, 0, player.getInventory(), host);
                default -> throw new AssertionError(type);
            };
            player.containerMenu = menu;
            for (int i = 0; i < host.grid.size(); i++) {
                host.grid.setItemDirect(i, new ItemStack(Items.DIAMOND, i % 64 + 1));
            }
            player.getInventory().setItem(9, new ItemStack(Items.STONE, 17));
            var before = snapshot(host, player);
            for (int size : new int[]{9, 25, 49, 81}) {
                if (size != type.getGridSize()) {
                    new FillTableCraftingGridFromRecipePacket(null, NonNullList.withSize(size, ItemStack.EMPTY),
                            false, -1, -1).handleOnServer(player);
                    assertUnchanged(helper, before, host, player);
                }
            }
            for (int dimension : new int[]{1, 3}) {
                new FillTableCraftingGridFromRecipePacket(ResourceLocation.withDefaultNamespace("honey_block"),
                        NonNullList.withSize(type.getGridSize(), ItemStack.EMPTY), false, dimension, dimension)
                        .handleOnServer(player);
                assertUnchanged(helper, before, host, player);
            }
            host.grid.clear();
            var templates = NonNullList.withSize(type.getGridSize(), ItemStack.EMPTY);
            templates.set(0, new ItemStack(Items.STONE));
            new FillTableCraftingGridFromRecipePacket(null, templates, false, -1, -1).handleOnServer(player);
            helper.assertTrue(host.grid.getStackInSlot(0).is(Items.STONE)
                    && host.grid.getStackInSlot(0).getCount() == 1
                    && player.getInventory().getItem(9).getCount() == 16,
                    "Valid transfer must move exactly one player item into " + type);
        }
        helper.succeed();
    }

    public static void unitedValidationBeforeSelection(GameTestHelper helper) {
        var player = player(helper);
        var host = new TestHost(player, 81);
        var menu = new UnitedTerminalMenu(UnitedTerminalMenu.TYPE, 0, player.getInventory(), host);
        player.containerMenu = menu;
        var alternative = UnitedTerminalMenu.getActiveRecipeTypes().stream()
                .filter(type -> type != MyoRecipeType.VANILLA).findFirst().orElseThrow();
        menu.setSelectedRecipeType(alternative);
        host.grid.setItemDirect(0, new ItemStack(Items.DIAMOND, 11));
        player.getInventory().setItem(9, new ItemStack(Items.HONEY_BOTTLE, 4));
        var before = snapshot(host, player);
        for (int dimension : new int[]{1, 3}) {
            new FillUnitedGridPacket(ResourceLocation.withDefaultNamespace("honey_block"),
                    NonNullList.withSize(81, ItemStack.EMPTY), false, dimension, dimension, MyoRecipeType.VANILLA)
                    .handleOnServer(player);
            assertUnchanged(helper, before, host, player);
            helper.assertTrue(menu.getSelectedRecipeType() == alternative && host.lastRecipeType == alternative,
                    "Rejected recipe must not change selected or remembered recipe type");
        }
        host.grid.clear();
        new FillUnitedGridPacket(ResourceLocation.withDefaultNamespace("honey_block"),
                NonNullList.withSize(81, ItemStack.EMPTY), false, 2, 2, MyoRecipeType.VANILLA).handleOnServer(player);
        helper.assertTrue(menu.getSelectedRecipeType() == MyoRecipeType.VANILLA,
                "Valid transfer must select the requested recipe type");
        int bottles = 0;
        for (int i = 0; i < host.grid.size(); i++) {
            if (host.grid.getStackInSlot(i).is(Items.HONEY_BOTTLE)) {
                bottles += host.grid.getStackInSlot(i).getCount();
            }
        }
        helper.assertTrue(bottles == 4 && player.getInventory().getItem(9).isEmpty(),
                "Valid United transfer must move all four recipe ingredients exactly once");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void honeyRemaindersAndStaleResult(GameTestHelper helper) {
        for (int top = 0; top <= 7; top++) {
            for (int left = 0; left <= 7; left++) {
                var player = player(helper);
                var host = new TestHost(player, 81);
                for (int y = top; y < top + 2; y++) {
                    for (int x = left; x < left + 2; x++) {
                        host.grid.setItemDirect(y * 9 + x, new ItemStack(Items.HONEY_BOTTLE));
                    }
                }
                var menu = new UnitedTerminalMenu(UnitedTerminalMenu.TYPE, 0, player.getInventory(), host);
                player.containerMenu = menu;
                var output = menu.slots.stream()
                        .filter(me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot.class::isInstance)
                        .map(me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot.class::cast)
                        .findFirst().orElseThrow();
                var preview = output.getItem().copy();
                output.doClick(appeng.helpers.InventoryAction.CRAFT_ITEM, player);
                helper.assertTrue(menu.getCarried().is(Items.HONEY_BLOCK), "Off-center 2x2 recipe crafts");
                for (int i = 0; i < 81; i++) {
                    boolean used = i % 9 >= left && i % 9 < left + 2 && i / 9 >= top && i / 9 < top + 2;
                    var stack = host.grid.getStackInSlot(i);
                    helper.assertTrue(used ? stack.is(Items.GLASS_BOTTLE) && stack.getCount() == 1 : stack.isEmpty(),
                            "Exactly four remainders at original input coordinates, slot " + i);
                }
                helper.assertTrue(player.getInventory().isEmpty(), "No duplicate remainders in player inventory");
                menu.setCarried(ItemStack.EMPTY);
                output.set(preview);
                output.doClick(appeng.helpers.InventoryAction.CRAFT_ITEM, player);
                helper.assertTrue(menu.getCarried().isEmpty(), "Stale recipe mismatch rejects without null dereference");
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void fuzzyCandidates(GameTestHelper helper) throws Exception {
        var template = new ItemStack(Items.DIAMOND);
        template.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("template"));
        var candidate = new ItemStack(Items.DIAMOND);
        candidate.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, net.minecraft.network.chat.Component.literal("candidate"));
        var key = appeng.api.stacks.AEItemKey.of(candidate);
        var input = NonNullList.withSize(81, ItemStack.EMPTY);
        input.set(0, template);
        var output = new ItemStack(Items.EMERALD);
        var method = java.util.Arrays.stream(me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("extractItemsByRecipe")).findFirst().orElseThrow();
        method.setAccessible(true);
        for (int policy = 0; policy < 3; policy++) {
            int rule = policy;
            var recipe = new me.myogoo.extendedterminal.adapter.recipe.table.ShapedTableRecipeAdapter(
                    helper.getLevel().getRecipeManager().byKey(ResourceLocation.withDefaultNamespace("honey_block")).orElseThrow().value(),
                    1, 1, 1, ResourceLocation.withDefaultNamespace("honey_block")) {
                @Override public boolean matches(MyoTableInput actual, net.minecraft.world.level.Level level, MyoRecipeType type) {
                    return rule != 0 || ItemStack.isSameItemSameComponents(actual.getItem(0), template);
                }
                @Override public ItemStack assemble(MyoTableInput actual, net.minecraft.world.level.Level level, MyoRecipeType type) {
                    return rule == 1 && !ItemStack.isSameItemSameComponents(actual.getItem(0), template)
                            ? new ItemStack(Items.COAL) : output.copy();
                }
            };
            int[] extracted = {0};
            var storage = new MEStorage() {
                @Override public long extract(appeng.api.stacks.AEKey requested, long amount,
                        appeng.api.config.Actionable action, appeng.api.networking.security.IActionSource source) {
                    if (!key.equals(requested)) return 0;
                    if (action == appeng.api.config.Actionable.MODULATE) extracted[0]++;
                    return 1;
                }
                @Override public net.minecraft.network.chat.Component getDescription() { return net.minecraft.network.chat.Component.literal("test"); }
            };
            var available = new appeng.api.stacks.KeyCounter();
            available.add(key, 1);
            appeng.api.networking.energy.IEnergySource energy = (amount, action, multiplier) -> amount;
            var result = (ItemStack) method.invoke(null, energy, appeng.api.networking.security.IActionSource.ofPlayer(player(helper)),
                    storage, helper.getLevel(), recipe, MyoRecipeType.VANILLA, output, input, template, 0, available, null);
            helper.assertTrue(policy == 2 ? ItemStack.isSameItemSameComponents(result, candidate) && extracted[0] == 1
                            : result.isEmpty() && extracted[0] == 0,
                    "Fuzzy substitution validates candidate matches and output before extraction, policy " + policy);
            helper.assertTrue(ItemStack.isSameItemSameComponents(input.get(0), template), "Validation preserves original template");
        }
        helper.succeed();
    }
    private static void expectRejected(GameTestHelper helper, Runnable action) {
        boolean rejected = false;
        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        helper.assertTrue(rejected, "Malformed recipe transfer must be rejected");
    }

    private static FakePlayer player(GameTestHelper helper) {
        return new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "table-transfer-test"));
    }

    private static List<ItemStack> snapshot(TestHost host, FakePlayer player) {
        var stacks = new ArrayList<ItemStack>();
        for (int i = 0; i < host.grid.size(); i++) {
            stacks.add(host.grid.getStackInSlot(i).copy());
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            stacks.add(player.getInventory().getItem(i).copy());
        }
        return stacks;
    }

    private static void assertUnchanged(GameTestHelper helper, List<ItemStack> before, TestHost host, FakePlayer player) {
        var after = snapshot(host, player);
        for (int i = 0; i < before.size(); i++) {
            helper.assertTrue(ItemStack.matches(before.get(i), after.get(i)),
                    "Rejected packet must not mutate grid or player inventory, slot " + i);
        }
    }

    private static final class TestHost extends ItemMenuHost<Item> implements IUnitedTerminalHost, ISegmentedInventory {
        private final AppEngInternalInventory grid;
        private final IConfigManager config = new ConfigManager((manager, setting) -> {});
        private MyoRecipeType lastRecipeType = MyoRecipeType.VANILLA;

        private TestHost(Player player, int size) {
            super(Items.STICK, player, MenuLocators.forStack(new ItemStack(Items.STICK)));
            grid = new AppEngInternalInventory(size);
        }

        @Override public InternalInventory getSubInventory(ResourceLocation id) { return grid; }
        @Override public MEStorage getInventory() { return NullInventory.of(); }
        @Override public ILinkStatus getLinkStatus() { return ILinkStatus.ofDisconnected(); }
        @Override public IConfigManager getConfigManager() { return config; }
        @Override public void returnToMainMenu(Player player, ISubMenu subMenu) { }
        @Override public ItemStack getMainMenuIcon() { return ItemStack.EMPTY; }
        @Override public boolean shouldRememberRecipeType() { return true; }
        @Override public void setRememberRecipeType(boolean remember) { }
        @Override public MyoRecipeType getLastRecipeType() { return lastRecipeType; }
        @Override public void setLastRecipeType(MyoRecipeType recipeType) { lastRecipeType = recipeType; }
    }
}
