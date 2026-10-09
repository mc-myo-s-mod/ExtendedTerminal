package me.myogoo.extendedterminal.gametest;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.crafting.RecipeAccess;
import appeng.me.storage.NullInventory;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.util.inv.AppEngInternalInventory;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.menu.ETMenuType;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.extendedterminal.menu.extendedterminal.slot.ETStoneCutterSlot;
import me.myogoo.extendedterminal.network.serverbound.FillSmithingGridFromRecipePacket;
import me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ExtendedTerminal.MODID)
public final class PanelTransferGameTests {
    private static final List<TestCase> TESTS = List.of(
            new TestCase("smithing_transfer_template_count", PanelTransferGameTests::templateCount),
            new TestCase("panel_transfer_rejects_invalid_recipe", PanelTransferGameTests::invalidRecipe),
            new TestCase("panel_transfer_rejects_disabled_target", PanelTransferGameTests::disabledTarget),
            new TestCase("smithing_transfer_across_panels", PanelTransferGameTests::smithingAcrossPanels),
            new TestCase("stonecutter_transfer_across_panels", PanelTransferGameTests::stonecutterAcrossPanels));

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
        var environment = event.registerEnvironment(ExtendedTerminal.makeId("panel_transfer"));
        for (var test : TESTS) {
            var id = ExtendedTerminal.makeId(test.name());
            event.registerTest(id, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id),
                    new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        }
    }

    private static void templateCount(GameTestHelper helper) {
        // Keep counts small so this regression is safe even before the allocation guard exists.
        for (int count : new int[]{-1, 0, 1, 2, 4, 64}) {
            var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
            try {
                buffer.writeBoolean(false);
                buffer.writeInt(count);
                boolean rejected = false;
                try {
                    FillSmithingGridFromRecipePacket.decode(buffer);
                } catch (IllegalArgumentException expected) {
                    rejected = true;
                }
                helper.assertTrue(rejected && buffer.readerIndex() == 5,
                        "Invalid smithing count must be rejected before reading templates: " + count);
            } finally {
                buffer.release();
            }
            if (count >= 0) {
                boolean rejected = false;
                try {
                    new FillSmithingGridFromRecipePacket(null, NonNullList.withSize(count, ItemStack.EMPTY), false);
                } catch (IllegalArgumentException expected) {
                    rejected = true;
                }
                helper.assertTrue(rejected, "Constructor must reject invalid smithing count: " + count);
            }
        }
        var buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            var packet = new FillSmithingGridFromRecipePacket(null, NonNullList.withSize(3, ItemStack.EMPTY), false);
            FillSmithingGridFromRecipePacket.STREAM_CODEC.encode(buffer, packet);
            FillSmithingGridFromRecipePacket.STREAM_CODEC.decode(buffer);
            helper.assertTrue(!buffer.isReadable(), "Three-template JEI packet shape must round-trip");
        } finally {
            buffer.release();
        }
        helper.succeed();
    }

    private static void invalidRecipe(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "panel-transfer-test"));
        var host = new TestHost(player, ETTerminalMode.ANVIL);
        for (var mode : ETTerminalMode.values()) {
            var inventory = host.getSubInventory(mode.getInventoryId());
            for (int slot = 0; slot < inventory.size(); slot++) {
                inventory.setItemDirect(slot, new ItemStack(Items.DIAMOND, slot + 1));
            }
        }
        player.getInventory().setItem(9, new ItemStack(Items.STONE, 17));
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
        player.containerMenu = menu;
        var before = inventorySnapshot(host, player);
        for (var id : new Identifier[]{null, ExtendedTerminal.makeId("missing_panel_transfer_recipe"),
                Identifier.withDefaultNamespace("honey_block")}) {
            var key = id == null ? null : ResourceKey.create(Registries.RECIPE, id);
            new FillSmithingGridFromRecipePacket(key, NonNullList.withSize(3, ItemStack.EMPTY), false)
                    .handleOnServer(player);
            assertUnchanged(helper, menu, host, player, before);
            new FillStonecutterGridFromRecipePacket(key, ItemStack.EMPTY, false).handleOnServer(player);
            assertUnchanged(helper, menu, host, player, before);
        }
        helper.succeed();
    }

    private static void disabledTarget(GameTestHelper helper) {
        var smithingInput = new SmithingRecipeInput(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_INGOT));
        var smithingRecipe = RecipeAccess.getRecipeFor(helper.getLevel(), RecipeType.SMITHING, smithingInput);
        var stonecuttingRecipe = RecipeAccess.getRecipeFor(helper.getLevel(), RecipeType.STONECUTTING,
                new SingleRecipeInput(new ItemStack(Items.STONE)));
        helper.assertTrue(smithingRecipe != null && stonecuttingRecipe != null, "Valid transfer recipes must exist");
        for (var target : new ETTerminalMode[]{ETTerminalMode.SMITHING, ETTerminalMode.STONECUTTING}) {
            var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "panel-transfer-test"));
            var host = new TestHost(player, ETTerminalMode.ANVIL);
            for (var mode : ETTerminalMode.values()) {
                var inventory = host.getSubInventory(mode.getInventoryId());
                for (int slot = 0; slot < inventory.size(); slot++) {
                    inventory.setItemDirect(slot, new ItemStack(Items.DIAMOND, slot + 1));
                }
            }
            for (int i = 0; i < 3; i++) {
                player.getInventory().setItem(9 + i, smithingInput.getItem(i).copy());
            }
            player.getInventory().setItem(12, new ItemStack(Items.STONE, 17));
            var menu = new DisabledPanelTestMenu(player, host, target);
            player.containerMenu = menu;
            var before = inventorySnapshot(host, player);
            if (target == ETTerminalMode.SMITHING) {
                new FillSmithingGridFromRecipePacket(smithingRecipe.id(), NonNullList.withSize(3, ItemStack.EMPTY), false)
                        .handleOnServer(player);
            } else {
                new FillStonecutterGridFromRecipePacket(stonecuttingRecipe.id(), new ItemStack(Items.STONE), false)
                        .handleOnServer(player);
            }
            assertUnchanged(helper, menu, host, player, before);
        }
        helper.succeed();
    }

    private static void assertUnchanged(GameTestHelper helper, ETTerminalMenu menu, TestHost host, FakePlayer player,
            List<ItemStack> before) {
        helper.assertTrue(menu.getMode() == ETTerminalMode.ANVIL && host.getMode() == ETTerminalMode.ANVIL,
                "Rejected recipe must not change the active panel");
        helper.assertTrue(menu.getStoneCutterRecipeId() == null && host.getStoneCutterRecipeId() == null,
                "Rejected recipe must not change the stonecutter selection");
        var after = inventorySnapshot(host, player);
        for (int i = 0; i < before.size(); i++) {
            helper.assertTrue(ItemStack.matches(before.get(i), after.get(i)),
                    "Rejected recipe must not mutate any panel or player inventory, slot " + i);
        }
    }

    private static List<ItemStack> inventorySnapshot(TestHost host, FakePlayer player) {
        var stacks = new ArrayList<ItemStack>();
        for (var mode : ETTerminalMode.values()) {
            var inventory = host.getSubInventory(mode.getInventoryId());
            for (int slot = 0; slot < inventory.size(); slot++) {
                stacks.add(inventory.getStackInSlot(slot).copy());
            }
        }
        for (var stack : player.getInventory().getNonEquipmentItems()) {
            stacks.add(stack.copy());
        }
        return stacks;
    }

    private static void smithingAcrossPanels(GameTestHelper helper) {
        var input = new SmithingRecipeInput(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                new ItemStack(Items.DIAMOND_SWORD), new ItemStack(Items.NETHERITE_INGOT));
        var recipe = RecipeAccess.getRecipeFor(helper.getLevel(), RecipeType.SMITHING, input);
        helper.assertTrue(recipe != null, "Vanilla netherite smithing recipe must exist");
        for (var source : new ETTerminalMode[]{ETTerminalMode.CRAFTING, ETTerminalMode.STONECUTTING, ETTerminalMode.ANVIL}) {
            var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "panel-transfer-test"));
            var host = new TestHost(player, source);
            var sourceInventory = host.getSubInventory(source.getInventoryId());
            for (int i = 0; i < 3; i++) {
                if (i < sourceInventory.size()) {
                    sourceInventory.setItemDirect(i, input.getItem(i).copy());
                } else {
                    host.getSubInventory(ETTerminalMode.CRAFTING.getInventoryId()).setItemDirect(i, input.getItem(i).copy());
                }
            }
            var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
            player.containerMenu = menu;
            new FillSmithingGridFromRecipePacket(recipe.id(), NonNullList.withSize(3, ItemStack.EMPTY), false)
                    .handleOnServer(player);
            helper.assertTrue(menu.getMode() == ETTerminalMode.SMITHING, "Transfer must select smithing from " + source);
            for (int i = 0; i < 3; i++) {
                helper.assertTrue(ItemStack.matches(menu.getSmithingInventory().getStackInSlot(i), input.getItem(i)),
                        "Smithing input must be transferred exactly once from " + source + ", slot " + i);
            }
            assertOtherPanelsEmpty(helper, host, ETTerminalMode.SMITHING);
        }
        helper.succeed();
    }

    private static void stonecutterAcrossPanels(GameTestHelper helper) {
        var input = new SingleRecipeInput(new ItemStack(Items.STONE));
        var recipe = RecipeAccess.getRecipeFor(helper.getLevel(), RecipeType.STONECUTTING, input);
        helper.assertTrue(recipe != null, "Vanilla stonecutting recipe must exist");
        for (var source : new ETTerminalMode[]{ETTerminalMode.CRAFTING, ETTerminalMode.SMITHING, ETTerminalMode.ANVIL}) {
            for (boolean alreadyInTarget : new boolean[]{false, true}) {
                var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "panel-transfer-test"));
                var host = new TestHost(player, source);
                host.getSubInventory((alreadyInTarget ? ETTerminalMode.STONECUTTING : source).getInventoryId())
                        .setItemDirect(0, new ItemStack(Items.STONE));
                var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
                player.containerMenu = menu;
                new FillStonecutterGridFromRecipePacket(recipe.id(), new ItemStack(Items.STONE), false)
                        .handleOnServer(player);
                helper.assertTrue(menu.getMode() == ETTerminalMode.STONECUTTING,
                        "Transfer must select stonecutting from " + source);
                helper.assertTrue(ItemStack.matches(menu.getStoneCutterInventory().getStackInSlot(0), input.getItem(0)),
                        "Stonecutting input must be transferred exactly once");
                helper.assertTrue(recipe.id().identifier().equals(menu.getStoneCutterRecipeId()),
                        "Recipe selection must happen after activating the stonecutting panel");
                helper.assertTrue(menu.slots.stream().anyMatch(slot -> slot instanceof ETStoneCutterSlot
                        && ItemStack.matches(slot.getItem(), recipe.value().assemble(input))),
                        "Valid transfer must populate the selected recipe output");
                assertOtherPanelsEmpty(helper, host, ETTerminalMode.STONECUTTING);
            }
        }
        helper.succeed();
    }

    private static void assertOtherPanelsEmpty(GameTestHelper helper, TestHost host, ETTerminalMode target) {
        for (var mode : ETTerminalMode.values()) {
            if (mode != target) {
                var inventory = host.getSubInventory(mode.getInventoryId());
                for (int i = 0; i < inventory.size(); i++) {
                    helper.assertTrue(inventory.getStackInSlot(i).isEmpty(), "Source ingredients must not be duplicated");
                }
            }
        }
    }

    private record TestCase(String name, Consumer<GameTestHelper> action) {
    }

    // Simulate setMode's disabled-panel rejection without mutating the enum's startup-only final flag.
    private static final class DisabledPanelTestMenu extends ETTerminalMenu {
        private final ETTerminalMode disabledMode;

        private DisabledPanelTestMenu(FakePlayer player, TestHost host, ETTerminalMode disabledMode) {
            super(TYPE, 0, player.getInventory(), host);
            this.disabledMode = disabledMode;
        }

        @Override
        public void setMode(ETTerminalMode mode) {
            if (mode != disabledMode) {
                super.setMode(mode);
            }
        }
    }

    private static final class TestHost extends ItemMenuHost<Item> implements IETTerminalHost, ISegmentedInventory {
        private final Map<Identifier, InternalInventory> inventories = new HashMap<>();
        private final IConfigManager config = IConfigManager.builder(() -> {}).build();
        private ETTerminalMode mode;
        private Identifier stonecutterRecipeId;

        private TestHost(Player player, ETTerminalMode mode) {
            super(Items.STICK, player, MenuLocators.forStack(new ItemStack(Items.STICK)));
            this.mode = mode;
            inventories.put(ETMenuType.ET_TERMINAL.getCraftingInventory(), new AppEngInternalInventory(9));
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
        @Override public ETTerminalMode getMode() { return mode; }
        @Override public void setMode(ETTerminalMode mode) { this.mode = mode; }
        @Override public Identifier getStoneCutterRecipeId() { return stonecutterRecipeId; }
        @Override public void setStoneCutterRecipeId(Identifier id) { stonecutterRecipeId = id; }
        @Override public ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() { return null; }
        @Override public void setRememberedAnvilExperienceSource(ExperienceMath.ExperienceSource source) { }
    }
}
