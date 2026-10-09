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
import me.myogoo.extendedterminal.ExtendedTerminal;
import me.myogoo.extendedterminal.api.host.IETTerminalHost;
import me.myogoo.extendedterminal.config.ExtendedTerminalConfig;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMenu;
import me.myogoo.extendedterminal.menu.extendedterminal.ETTerminalMode;
import me.myogoo.myotus.api.experience.ExperienceMath;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@EventBusSubscriber(modid = ExtendedTerminal.MODID)
public final class ETPanelGameTests {
    private static final List<TestCase> TESTS = List.of(
            new TestCase("et_stonecutting_validates_input_and_restores_selection", ETPanelGameTests::stonecuttingSelection),
            new TestCase("et_client_refresh_keeps_server_selection", ETPanelGameTests::clientRefreshKeepsSelection),
            new TestCase("et_hidden_panels_cannot_craft", ETPanelGameTests::hiddenPanels),
            new TestCase("et_craft_only_active_and_power_transitions", ETPanelGameTests::powerTransitions));

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
        var environment = event.registerEnvironment(ExtendedTerminal.makeId("et_panels"));
        for (var test : TESTS) {
            var id = ExtendedTerminal.makeId(test.name());
            event.registerTest(id, new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id),
                    new TestData<>(environment, Identifier.withDefaultNamespace("empty"), 100, 0, true)));
        }
    }

    private static void stonecuttingSelection(GameTestHelper helper) {
        var player = player(helper);
        var host = new TestHost(player, ETTerminalMode.STONECUTTING);
        var inventory = host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId());
        var recipeInput = new SingleRecipeInput(new ItemStack(Items.STONE));
        var recipe = RecipeAccess.getRecipesFor(helper.getLevel(), RecipeType.STONECUTTING, recipeInput)
                .filter(r -> r.value().assemble(recipeInput).is(Items.STONE_SLAB)).findFirst().orElseThrow();
        var recipeId = recipe.id().identifier();
        var menu = open(player, host);
        var output = output(menu, ETTerminalMode.STONECUTTING);

        for (var invalidInput : List.of(ItemStack.EMPTY, new ItemStack(Items.DIRT))) {
            inventory.setItemDirect(0, invalidInput);
            menu.setStoneCutterRecipeId(recipeId);
            helper.assertTrue(output.getItem().isEmpty() && menu.getStoneCutterRecipeId() == null,
                    "Selecting a valid recipe with empty/wrong input must not create output");
        }

        inventory.setItemDirect(0, new ItemStack(Items.STONE, 2));
        menu.setStoneCutterRecipeId(recipeId);
        helper.assertTrue(output.getItem().is(Items.STONE_SLAB), "Matching input must produce the selected output");
        var reopened = open(player, host);
        output = output(reopened, ETTerminalMode.STONECUTTING);
        helper.assertTrue(output.getItem().is(Items.STONE_SLAB), "Reopening must restore the remembered output");

        // Do not notify the menu: exercise the actual-take check against a stale preview/selection.
        for (var invalidInput : List.of(ItemStack.EMPTY, new ItemStack(Items.DIRT))) {
            inventory.setItemDirect(0, invalidInput);
            reopened.doAction(player, InventoryAction.CRAFT_ITEM, output.index, 0);
            helper.assertTrue(reopened.getCarried().isEmpty(), "Actual take must reject stale input");
            helper.assertTrue(ItemStack.matches(inventory.getStackInSlot(0), invalidInput),
                    "Rejected take must preserve the input");
        }
        inventory.setItemDirect(0, new ItemStack(Items.STONE, 2));
        reopened.doAction(player, InventoryAction.CRAFT_ITEM, output.index, 0);
        helper.assertTrue(reopened.getCarried().is(Items.STONE_SLAB)
                        && reopened.getCarried().getCount() == 2 && inventory.getStackInSlot(0).getCount() == 1,
                "Valid take must consume exactly one stone and produce two slabs");
        helper.succeed();
    }

    private static void clientRefreshKeepsSelection(GameTestHelper helper) {
        var player = player(helper);
        var host = new TestHost(player, ETTerminalMode.STONECUTTING);
        fillInputs(host, ETTerminalMode.STONECUTTING, helper);
        var menu = new ClientRefreshTestMenu(player, host);
        var selectedRecipe = menu.getStoneCutterRecipeId();
        helper.assertTrue(selectedRecipe != null, "Fixture must have a server-selected recipe");
        menu.simulateClient = true;
        var input = host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId());
        for (var staleInput : List.of(ItemStack.EMPTY, new ItemStack(Items.DIRT))) {
            input.setItemDirect(0, staleInput);
            menu.slotsChanged(input.toContainer());
            menu.updateStonecutterRecipes();
            helper.assertTrue(menu.selectionRequests == 0 && selectedRecipe.equals(menu.getStoneCutterRecipeId()),
                    "Automatic client refresh must not request a clear from stale input");
        }
        helper.succeed();
    }

    private static void hiddenPanels(GameTestHelper helper) {
        for (var mode : ETTerminalMode.values()) {
            var player = player(helper);
            var host = new TestHost(player, mode);
            fillInputs(host, mode, helper);
            var menu = open(player, host);
            var output = output(menu, mode);
            var preview = output.getItem().copy();
            helper.assertTrue(!preview.isEmpty(), "Fixture must produce output for " + mode);
            menu.setMode(mode == ETTerminalMode.CRAFTING ? ETTerminalMode.ANVIL : ETTerminalMode.CRAFTING);
            helper.assertTrue(output.getItem().isEmpty(), "Switching panels must clear the hidden preview: " + mode);
            output.set(preview);
            assertTakeRejected(helper, player, menu, host, mode);
            if (mode == ETTerminalMode.STONECUTTING) {
                menu.setStoneCutterRecipeId(Identifier.withDefaultNamespace("missing_recipe"));
                helper.assertTrue(host.stoneRecipeId != null, "Hidden-panel selection actions must be ignored");
            }
        }
        helper.succeed();
    }

    private static void powerTransitions(GameTestHelper helper) {
        ModConfigSpec.BooleanValue option = ExtendedTerminalConfig.COMMON.getValues()
                .get(List.of("ExtendedTerminal", "Extended Terminal", "Craftable when Active"));
        boolean original = option.get();
        try {
            option.set(true);
            ExtendedTerminalConfig.INSTANCE.bake();
            for (var mode : ETTerminalMode.values()) {
                var player = player(helper);
                var host = new TestHost(player, mode);
                fillInputs(host, mode, helper);
                var menu = new PowerTestMenu(player, host);
                player.containerMenu = menu;
                var output = output(menu, mode);
                helper.assertTrue(output.getItem().isEmpty(), "Disconnected menu must honor Craftable when Active: " + mode);
                menu.active = true;
                menu.broadcastChanges();
                helper.assertTrue(!output.getItem().isEmpty(), "Power restoration must restore output without input edits: " + mode);
                menu.active = false;
                assertTakeRejected(helper, player, menu, host, mode);
                menu.broadcastChanges();
                helper.assertTrue(output.getItem().isEmpty(), "Power loss must clear output without input edits: " + mode);
                menu.active = true;
                menu.broadcastChanges();
                helper.assertTrue(!output.getItem().isEmpty(), "Unchanged recipe must return after the second power transition: " + mode);
            }
            option.set(false);
            ExtendedTerminalConfig.INSTANCE.bake();
            var player = player(helper);
            var host = new TestHost(player, ETTerminalMode.CRAFTING);
            fillInputs(host, ETTerminalMode.CRAFTING, helper);
            var menu = open(player, host);
            menu.doAction(player, InventoryAction.CRAFT_ITEM, output(menu, ETTerminalMode.CRAFTING).index, 0);
            helper.assertTrue(menu.getCarried().is(Items.OAK_PLANKS), "Default configuration must still allow offline crafting");
        } finally {
            option.set(original);
            ExtendedTerminalConfig.INSTANCE.bake();
        }
        helper.succeed();
    }

    private static void assertTakeRejected(GameTestHelper helper, FakePlayer player, ETTerminalMenu menu,
                                           TestHost host, ETTerminalMode mode) {
        var input = host.getSubInventory(mode.getInventoryId());
        var before = java.util.stream.IntStream.range(0, input.size()).mapToObj(i -> input.getStackInSlot(i).copy()).toList();
        var output = output(menu, mode);
        if (mode == ETTerminalMode.ANVIL) {
            helper.assertTrue(!output.mayPickup(player), "Anvil normal take must be blocked");
            menu.quickMoveStack(player, output.index);
        } else {
            for (var action : List.of(InventoryAction.CRAFT_ITEM, InventoryAction.CRAFT_SHIFT,
                    InventoryAction.CRAFT_STACK, InventoryAction.CRAFT_ALL)) {
                menu.doAction(player, action, output.index, 0);
            }
        }
        helper.assertTrue(menu.getCarried().isEmpty() && player.getInventory().isEmpty(),
                "Blocked panel must not produce carried or inventory output: " + mode);
        for (int i = 0; i < input.size(); i++) {
            helper.assertTrue(ItemStack.matches(before.get(i), input.getStackInSlot(i)),
                    "Blocked panel must preserve input " + i + " in " + mode);
        }
    }

    private static void fillInputs(TestHost host, ETTerminalMode mode, GameTestHelper helper) {
        var input = host.getSubInventory(mode.getInventoryId());
        switch (mode) {
            case CRAFTING -> input.setItemDirect(0, new ItemStack(Items.OAK_LOG));
            case STONECUTTING -> {
                input.setItemDirect(0, new ItemStack(Items.STONE));
                host.stoneRecipeId = RecipeAccess.getRecipesFor(helper.getLevel(), RecipeType.STONECUTTING,
                        new SingleRecipeInput(input.getStackInSlot(0))).findFirst().orElseThrow().id().identifier();
            }
            case SMITHING -> {
                input.setItemDirect(0, new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
                input.setItemDirect(1, new ItemStack(Items.DIAMOND_SWORD));
                input.setItemDirect(2, new ItemStack(Items.NETHERITE_INGOT));
            }
            case ANVIL -> {
                var sword = new ItemStack(Items.IRON_SWORD);
                sword.setDamageValue(150);
                input.setItemDirect(0, sword);
                input.setItemDirect(1, new ItemStack(Items.IRON_INGOT));
            }
        }
    }

    private static FakePlayer player(GameTestHelper helper) {
        var player = new FakePlayer(helper.getLevel(), new GameProfile(UUID.randomUUID(), "et-panel-test"));
        player.getAbilities().instabuild = true;
        return player;
    }

    private static ETTerminalMenu open(FakePlayer player, TestHost host) {
        var menu = new ETTerminalMenu(ETTerminalMenu.TYPE, 0, player.getInventory(), host);
        player.containerMenu = menu;
        return menu;
    }

    private static Slot output(ETTerminalMenu menu, ETTerminalMode mode) {
        return menu.getSlots(mode.getOutputSlotSemantics()).getFirst();
    }

    private record TestCase(String name, Consumer<GameTestHelper> action) { }

    // Exercise client-side refresh decisions without loading client networking on the GameTest server.
    private static final class ClientRefreshTestMenu extends ETTerminalMenu {
        private boolean simulateClient;
        private int selectionRequests;
        private ClientRefreshTestMenu(FakePlayer player, TestHost host) {
            super(TYPE, 0, player.getInventory(), host);
        }
        @Override public boolean isClientSide() { return simulateClient || super.isClientSide(); }
        @Override public void setStoneCutterRecipeId(Identifier id) {
            if (simulateClient) {
                selectionRequests++;
            } else {
                super.setStoneCutterRecipeId(id);
            }
        }
    }

    // Simulate grid activation, while the inactive path still uses the real loaded configuration and null node.
    private static final class PowerTestMenu extends ETTerminalMenu {
        private boolean active;
        private PowerTestMenu(FakePlayer player, TestHost host) {
            super(TYPE, 0, player.getInventory(), host);
        }
        @Override protected boolean checkCraftingOnlyActive() { return !active && super.checkCraftingOnlyActive(); }
    }

    private static final class TestHost extends ItemMenuHost<Item> implements IETTerminalHost, ISegmentedInventory {
        private final Map<Identifier, InternalInventory> inventories = new HashMap<>();
        private final IConfigManager config = IConfigManager.builder(() -> {}).build();
        private ETTerminalMode mode;
        private Identifier stoneRecipeId;

        private TestHost(Player player, ETTerminalMode mode) {
            super(Items.STICK, player, MenuLocators.forStack(new ItemStack(Items.STICK)));
            this.mode = mode;
            for (var panel : ETTerminalMode.values()) {
                inventories.put(panel.getInventoryId(), new AppEngInternalInventory(panel == ETTerminalMode.CRAFTING
                        ? 9 : panel.getInputSlotSemantics().size()));
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
        @Override public Identifier getStoneCutterRecipeId() { return stoneRecipeId; }
        @Override public void setStoneCutterRecipeId(Identifier id) { stoneRecipeId = id; }
        @Override public ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() { return null; }
        @Override public void setRememberedAnvilExperienceSource(ExperienceMath.ExperienceSource source) { }
    }
}
