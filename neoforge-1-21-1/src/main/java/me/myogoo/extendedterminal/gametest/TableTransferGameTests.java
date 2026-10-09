package me.myogoo.extendedterminal.gametest;

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
