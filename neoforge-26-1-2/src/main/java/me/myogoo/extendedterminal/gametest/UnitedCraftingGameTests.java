package me.myogoo.extendedterminal.gametest;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.crafting.RecipeAccess;
import appeng.helpers.InventoryAction;
import appeng.me.storage.NullInventory;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.adapter.recipe.table.IShapedTableRecipeAdapter;
import me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableRecipe;
import me.myogoo.extendedterminal.api.host.IUnitedTerminalHost;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedcrafting.slot.UnitedCraftingTerminalSlot;
import me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType;
import me.myogoo.extendedterminal.menu.extendedterminal.UnitedTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.menu.recipe.ETRecipeTransferPlanner;
import me.myogoo.extendedterminal.network.serverbound.FillTableCraftingGridFromRecipePacket;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ExtendedTerminal.MODID)
public final class UnitedCraftingGameTests {
    private static final List<TestCase> TESTS = List.of(
            new TestCase("united_honey_remainders", UnitedCraftingGameTests::honeyRemainders),
            new TestCase("united_custom_shaped_transfer", UnitedCraftingGameTests::customShapedTransfer),
            new TestCase("custom_shaped_transfer_across_et_panels", UnitedCraftingGameTests::transferAcrossPanels),
            new TestCase("table_transfer_rejects_invalid_packet", UnitedCraftingGameTests::rejectInvalidPacket),
            new TestCase("united_compact_and_distributed_inputs", UnitedCraftingGameTests::compactAndDistributedInputs));

    @SubscribeEvent
    public static void registerFunctions(RegisterEvent event) {
        if (event.getRegistryKey() == Registries.TEST_FUNCTION) {
            for (var test : TESTS) {
                event.register(Registries.TEST_FUNCTION, ExtendedTerminal.makeId(test.name()), test::action);
            }
        }
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        var environment = event.registerEnvironment(ExtendedTerminal.makeId("united_crafting"));
        for (var test : TESTS) {
            var id = ExtendedTerminal.makeId(test.name());
            event.registerTest(id, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id),
                    new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        }
    }

    private static void honeyRemainders(GameTestHelper helper) {
        for (int top = 0; top <= 7; top++) {
            for (int left = 0; left <= 7; left++) {
                var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "united-test"));
                var host = new TestHost(player);
                for (int y = top; y < top + 2; y++) {
                    for (int x = left; x < left + 2; x++) {
                        host.grid.setItemDirect(y * 9 + x, new ItemStack(Items.HONEY_BOTTLE));
                    }
                }
                var menu = new UnitedTerminalMenu(UnitedTerminalMenu.TYPE, 0, player.getInventory(), host);
                player.containerMenu = menu;
                var output = menu.slots.stream().filter(UnitedCraftingTerminalSlot.class::isInstance)
                        .map(UnitedCraftingTerminalSlot.class::cast).findFirst().orElseThrow();
                output.doClick(InventoryAction.CRAFT_ITEM, player);
                helper.assertTrue(menu.getCarried().is(Items.HONEY_BLOCK), "2x2 honey recipe must craft");
                for (int i = 0; i < 81; i++) {
                    boolean used = i % 9 >= left && i % 9 < left + 2 && i / 9 >= top && i / 9 < top + 2;
                    var stack = host.grid.getStackInSlot(i);
                    helper.assertTrue(used ? stack.is(Items.GLASS_BOTTLE) && stack.getCount() == 1 : stack.isEmpty(),
                            "Bottle remainder position/count at offset " + left + "," + top + ", slot " + i);
                }
                helper.assertTrue(player.getInventory().countItem(Items.GLASS_BOTTLE) == 0,
                        "Four empty ingredient slots must receive exactly four bottles, without inventory duplicates");
            }
        }
        helper.succeed();
    }

    private static void customShapedTransfer(GameTestHelper helper) {
        var key = ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace("honey_block"));
        var original = RecipeAccess.byKey(helper.getLevel(), RecipeType.CRAFTING, key);
        helper.assertTrue(original != null, "Vanilla honey recipe must exist");
        var recipe = new WrappedRecipe(original.value());
        var adapter = MyoTableRecipe.of(recipe, key);
        helper.assertTrue(adapter instanceof IShapedTableRecipeAdapter shaped && shaped.width() == 2 && shaped.height() == 2,
                "A non-ShapedRecipe wrapper must retain its declared 2x2 display dimensions");
        helper.assertTrue(!MyoTableRecipe.hasCraftingLayout(new WrappedRecipe(original.value(), List.of())),
                "A custom recipe without shape metadata must not be guessed shapeless");
        var display = recipe.display().getFirst();
        helper.assertTrue(!MyoTableRecipe.hasCraftingLayout(new WrappedRecipe(original.value(), List.of(display, display))),
                "Multiple alternative displays must not be guessed into one layout");
        for (int side : new int[]{3, 9}) {
            var ingredients = ETRecipeTransferPlanner.desiredIngredients(recipe,
                    NonNullList.withSize(side * side, ItemStack.EMPTY), 2, 2);
            int offset = (side - 2) / 2;
            for (int i = 0; i < ingredients.size(); i++) {
                boolean used = i % side >= offset && i % side < offset + 2 && i / side >= offset && i / side < offset + 2;
                helper.assertTrue(ingredients.get(i).isPresent() == used, "Custom shaped transfer slot " + i + " in " + side);
            }
        }
        helper.succeed();
    }

    private static void transferAcrossPanels(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "united-test"));
        var host = new TestHost(player, 9);
        host.setMode(ETTerminalMode.ANVIL);
        host.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).setItemDirect(0, new ItemStack(Items.HONEY_BOTTLE, 4));
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
        player.containerMenu = menu;
        var key = ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace("honey_block"));
        new FillTableCraftingGridFromRecipePacket(key, NonNullList.withSize(9, ItemStack.EMPTY), false, 2, 2)
                .handleOnServer(player);
        helper.assertTrue(menu.getMode() == ETTerminalMode.CRAFTING, "Custom shaped transfer must select the crafting panel");
        for (int i = 0; i < 9; i++) {
            helper.assertTrue(i % 3 < 2 && i / 3 < 2 ? host.grid.getStackInSlot(i).is(Items.HONEY_BOTTLE)
                    : host.grid.getStackInSlot(i).isEmpty(), "Other-panel ingredients must retain 2x2 shape, slot " + i);
        }
        helper.assertTrue(host.getSubInventory(ETTerminalMode.ANVIL.getInventoryId()).getStackInSlot(0).isEmpty(),
                "All four bottles must be moved, not duplicated");
        helper.succeed();
    }

    private static void compactAndDistributedInputs(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "united-test"));
        var host = new TestHost(player);
        var menu = new UnitedTerminalMenu(UnitedTerminalMenu.TYPE, 0, player.getInventory(), host);
        for (int y = 6; y < 9; y++) {
            for (int x = 6; x < 9; x++) {
                host.grid.setItemDirect(y * 9 + x, new ItemStack(x == 7 && y == 7 ? Items.LINGERING_POTION : Items.ARROW));
            }
        }
        menu.slotsChanged(host.grid.toContainer());
        var output = menu.slots.stream().filter(UnitedCraftingTerminalSlot.class::isInstance).findFirst().orElseThrow();
        helper.assertTrue(output.getItem().is(Items.TIPPED_ARROW), "Special recipe must see compact 3x3 input at the bottom-right edge");
        host.grid.clear();
        for (int slot : new int[]{0, 20, 40}) {
            host.grid.setItemDirect(slot, new ItemStack(Items.PAPER));
        }
        host.grid.setItemDirect(80, new ItemStack(Items.LEATHER));
        menu.slotsChanged(host.grid.toContainer());
        helper.assertTrue(output.getItem().is(Items.BOOK), "Shapeless ingredients can span all nine rows");
        host.grid.setItemDirect(79, new ItemStack(Items.STONE));
        menu.slotsChanged(host.grid.toContainer());
        helper.assertTrue(output.getItem().isEmpty(), "No unrelated occupied slot may be ignored");
        helper.succeed();
    }

    private static void rejectInvalidPacket(GameTestHelper helper) {
        for (int count : new int[]{-1, 0, 8, 82, Integer.MAX_VALUE}) {
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
            try {
                buffer.writeBoolean(false);
                buffer.writeInt(count);
                boolean rejected = false;
                try {
                    FillTableCraftingGridFromRecipePacket.decode(buffer);
                } catch (IllegalArgumentException expected) {
                    rejected = true;
                }
                helper.assertTrue(rejected, "Invalid template count must be rejected before allocation: " + count);
            } finally {
                buffer.release();
            }
        }
        for (int[] dimensions : new int[][]{{10, 2}, {4, 2}, {0, 2}, {-1, 2}, {2, -1}}) {
            boolean rejected = false;
            try {
                new FillTableCraftingGridFromRecipePacket(null, NonNullList.withSize(9, ItemStack.EMPTY), false,
                        dimensions[0], dimensions[1]);
            } catch (IllegalArgumentException expected) {
                rejected = true;
            }
            helper.assertTrue(rejected, "Invalid dimensions must be rejected before planning");
        }
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "united-test"));
        var host = new TestHost(player, 9);
        host.setMode(ETTerminalMode.ANVIL);
        host.grid.setItemDirect(0, new ItemStack(Items.DIAMOND));
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
        player.containerMenu = menu;
        new FillTableCraftingGridFromRecipePacket(null, NonNullList.withSize(81, ItemStack.EMPTY), false, 9, 9)
                .handleOnServer(player);
        var key = ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace("honey_block"));
        new FillTableCraftingGridFromRecipePacket(key, NonNullList.withSize(9, ItemStack.EMPTY), false, 3, 3)
                .handleOnServer(player);
        helper.assertTrue(menu.getMode() == ETTerminalMode.ANVIL && host.grid.getStackInSlot(0).is(Items.DIAMOND),
                "Mismatched menu size or recipe slots must leave the mode and inventory unchanged");
        helper.succeed();
    }

    private record TestCase(String name, Consumer<GameTestHelper> action) {
    }

    private record WrappedRecipe(CraftingRecipe delegate, List<RecipeDisplay> display) implements CraftingRecipe {
        private WrappedRecipe(CraftingRecipe delegate) { this(delegate, delegate.display()); }
        @Override public boolean matches(CraftingInput input, Level level) { return delegate.matches(input, level); }
        @Override public ItemStack assemble(CraftingInput input) { return delegate.assemble(input); }
        @Override public boolean showNotification() { return delegate.showNotification(); }
        @Override public String group() { return delegate.group(); }
        @Override public RecipeSerializer<? extends CraftingRecipe> getSerializer() { return delegate.getSerializer(); }
        @Override public CraftingBookCategory category() { return delegate.category(); }
        @Override public PlacementInfo placementInfo() { return delegate.placementInfo(); }
    }

    private static final class TestHost extends ItemMenuHost<Item> implements IUnitedTerminalHost, IETTerminalHost, ISegmentedInventory {
        private final AppEngInternalInventory grid;
        private final Map<Identifier, InternalInventory> inventories = new HashMap<>();
        private final IConfigManager config = IConfigManager.builder(() -> {}).build();
        private ETTerminalMode mode = ETTerminalMode.CRAFTING;

        private TestHost(Player player) {
            this(player, 81);
        }

        private TestHost(Player player, int size) {
            super(Items.STICK, player, MenuLocators.forStack(new ItemStack(Items.STICK)));
            grid = new AppEngInternalInventory(size);
            inventories.put(ETMenuType.UNITED_TERMINAL.getCraftingInventory(), grid);
            inventories.put(ETMenuType.ET_TERMINAL.getCraftingInventory(), grid);
            for (var panel : ETTerminalMode.values()) {
                inventories.putIfAbsent(panel.getInventoryId(), new AppEngInternalInventory(panel.getInputSlotSemantics().size()));
            }
        }

        @Override public InternalInventory getSubInventory(Identifier id) { return inventories.get(id); }
        @Override public MEStorage getInventory() { return NullInventory.of(); }
        @Override public ILinkStatus getLinkStatus() { return ILinkStatus.ofDisconnected(); }
        @Override public IConfigManager getConfigManager() { return config; }
        @Override public void returnToMainMenu(Player player, ISubMenu subMenu) { }
        @Override public ItemStack getMainMenuIcon() { return ItemStack.EMPTY; }
        @Override public boolean shouldRememberRecipeType() { return false; }
        @Override public void setRememberRecipeType(boolean remember) { }
        @Override public MyoRecipeType getLastRecipeType() { return MyoRecipeType.VANILLA; }
        @Override public void setLastRecipeType(MyoRecipeType recipeType) { }
        @Override public ETTerminalMode getMode() { return mode; }
        @Override public void setMode(ETTerminalMode mode) { this.mode = mode; }
        @Override public Identifier getStoneCutterRecipeId() { return null; }
        @Override public void setStoneCutterRecipeId(Identifier id) { }
        @Override public ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() { return null; }
        @Override public void setRememberedAnvilExperienceSource(ExperienceMath.ExperienceSource source) { }
    }
}
