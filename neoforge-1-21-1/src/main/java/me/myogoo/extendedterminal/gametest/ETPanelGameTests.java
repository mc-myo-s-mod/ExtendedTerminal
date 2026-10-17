package me.myogoo.extendedterminal.gametest;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ILinkStatus;
import appeng.api.storage.MEStorage;
import appeng.api.util.IConfigManager;
import appeng.util.ConfigManager;
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
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@net.neoforged.neoforge.gametest.GameTestHolder(ExtendedTerminal.MODID)
@net.neoforged.neoforge.gametest.PrefixGameTestTemplate(false)
public final class ETPanelGameTests {
    @net.minecraft.gametest.framework.GameTest(template = "empty")
    @SuppressWarnings("deprecation")
    public static void legacyRecipeBuilders(GameTestHelper helper) {
        var builder = me.myogoo.extendedterminal.util.recipe.builder.ETShapedRecipeBuilder
                .shaped(Items.DIAMOND, 3).pattern("AA").pattern(" A").define('A', Items.STONE);
        var recipe = builder.build();
        helper.assertTrue(recipe.getResultItem(helper.getLevel().registryAccess()).getCount() == 3,
                "Legacy vanilla builder must preserve output count");
        helper.assertTrue(recipe.getIngredients().size() == 4 && recipe.getIngredients().get(2).isEmpty()
                        && recipe.getIngredients().get(3).test(new ItemStack(Items.STONE)),
                "Legacy vanilla builder must preserve repeated symbols and spaces");
        recipe.getResultItem(helper.getLevel().registryAccess()).setCount(1);
        helper.assertTrue(builder.build().getResultItem(helper.getLevel().registryAccess()).getCount() == 3,
                "Built recipes must not mutate the builder's output stack");
        var table = me.myogoo.extendedterminal.util.recipe.builder.ShapedTableRecipeBuilder
                .shaped(Items.DIAMOND, 3).pattern("AA").pattern(" A").define('A', Items.STONE);
        var id = ResourceLocation.fromNamespaceAndPath(ExtendedTerminal.MODID, "legacy_builder_test");
        if (me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType.BASIC.isActive()) {
            helper.assertTrue(table.buildEC(id).value().getResultItem(helper.getLevel().registryAccess()).getCount() == 3,
                    "Legacy EC builder must preserve output count");
        }
        if (me.myogoo.extendedterminal.menu.extendedterminal.MyoRecipeType.SCULK.isActive()) {
            helper.assertTrue(table.buildReAV(id).value().getResultItem(helper.getLevel().registryAccess()).getCount() == 3,
                    "Legacy Re Avaritia builder must preserve output count");
        }
        var input = me.myogoo.extendedterminal.api.adapter.recipe.table.MyoTableInput
                .create(2, 2, List.of(new ItemStack(Items.STONE), ItemStack.EMPTY,
                        ItemStack.EMPTY, new ItemStack(Items.STONE)), 3);
        helper.assertTrue(input.cast(net.minecraft.world.item.crafting.CraftingInput.class).equals(input.cast()),
                "Legacy vanilla cast must delegate to the current factory");
        var tiered = input.cast(LegacyTieredInput.class);
        helper.assertTrue(tiered.tier == 3 && tiered.width() == 2 && tiered.height() == 2
                        && ItemStack.listMatches(tiered.items(), input.items()),
                "Legacy custom cast must invoke the requested four-argument factory");
        for (var type : List.of(MissingLegacyFactory.class, ThrowingLegacyFactory.class, PrivateLegacyFactory.class)) {
            try {
                input.cast(type);
                helper.fail("Invalid legacy factories must throw");
            } catch (RuntimeException expected) {
                helper.assertTrue(type == MissingLegacyFactory.class ? expected.getCause() instanceof NoSuchMethodException
                                : type == PrivateLegacyFactory.class ? expected.getCause() instanceof IllegalAccessException
                                : expected.getCause() instanceof java.lang.reflect.InvocationTargetException
                                    && expected.getCause().getCause() instanceof IllegalStateException,
                        "Legacy cast must preserve its wrapped reflection failure: " + type.getSimpleName());
            }
        }
        helper.succeed();
    }

    public static class LegacyTieredInput extends net.minecraft.world.item.crafting.CraftingInput {
        private final int tier;
        private LegacyTieredInput(int width, int height, List<ItemStack> items, int tier) {
            super(width, height, items);
            this.tier = tier;
        }
        public static LegacyTieredInput of(int width, int height, List<ItemStack> items, int tier) {
            return new LegacyTieredInput(width, height, items, tier);
        }
    }

    public static class MissingLegacyFactory extends net.minecraft.world.item.crafting.CraftingInput {
        private MissingLegacyFactory() { super(0, 0, List.of()); }
    }

    public static class ThrowingLegacyFactory extends net.minecraft.world.item.crafting.CraftingInput {
        private ThrowingLegacyFactory() { super(0, 0, List.of()); }
        public static ThrowingLegacyFactory of(int width, int height, List<ItemStack> items, int tier) {
            throw new IllegalStateException("legacy factory regression");
        }
    }

    public static class PrivateLegacyFactory extends net.minecraft.world.item.crafting.CraftingInput {
        private PrivateLegacyFactory() { super(0, 0, List.of()); }
        private static PrivateLegacyFactory of(int width, int height, List<ItemStack> items, int tier) {
            return new PrivateLegacyFactory();
        }
    }

    @net.minecraft.gametest.framework.GameTest(template = "empty")
    public static void stonecuttingSelection(GameTestHelper helper) {
        var player = player(helper);
        var host = new TestHost(player, ETTerminalMode.STONECUTTING);
        var inventory = host.getSubInventory(ETTerminalMode.STONECUTTING.getInventoryId());
        var recipeInput = new SingleRecipeInput(new ItemStack(Items.STONE));
        var recipe = helper.getLevel().getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, recipeInput, helper.getLevel()).stream()
                .filter(r -> r.value().assemble(recipeInput, helper.getLevel().registryAccess()).is(Items.STONE_SLAB)).findFirst().orElseThrow();
        var recipeId = recipe.id();
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

    @net.minecraft.gametest.framework.GameTest(template = "empty")
    public static void clientRefreshKeepsSelection(GameTestHelper helper) {
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

    @net.minecraft.gametest.framework.GameTest(template = "empty")
    public static void hiddenPanels(GameTestHelper helper) {
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
                menu.setStoneCutterRecipeId(ResourceLocation.withDefaultNamespace("missing_recipe"));
                helper.assertTrue(host.stoneRecipeId != null, "Hidden-panel selection actions must be ignored");
            }
        }
        helper.succeed();
    }

    @net.minecraft.gametest.framework.GameTest(template = "empty")
    public static void powerTransitions(GameTestHelper helper) {
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

    @net.minecraft.gametest.framework.GameTest(template = "empty")
    public static void invalidPanelRequests(GameTestHelper helper) {
        var player = player(helper);
        var host = new TestHost(player, ETTerminalMode.ANVIL);
        for (var mode : ETTerminalMode.values()) {
            host.getSubInventory(mode.getInventoryId()).setItemDirect(0, new ItemStack(Items.DIAMOND, 3));
        }
        var menu = open(player, host);
        for (var id : new ResourceLocation[]{null, ResourceLocation.withDefaultNamespace("missing_recipe"), ResourceLocation.withDefaultNamespace("honey_block")}) {
            new me.myogoo.extendedterminal.network.serverbound.FillSmithingGridFromRecipePacket(id,
                    net.minecraft.core.NonNullList.withSize(3, ItemStack.EMPTY), false).handleOnServer(player);
            new me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket(id,
                    new ItemStack(Items.STONE), false).handleOnServer(player);
            helper.assertTrue(menu.getMode() == ETTerminalMode.ANVIL, "Invalid recipe must not change active panel");
            for (var mode : ETTerminalMode.values()) {
                var item = host.getSubInventory(mode.getInventoryId()).getStackInSlot(0);
                helper.assertTrue(item.is(Items.DIAMOND) && item.getCount() == 3, "Invalid recipe preserves all panel inputs");
            }
        }
        player.containerMenu = player.inventoryMenu;
        new me.myogoo.extendedterminal.network.serverbound.FillSmithingGridFromRecipePacket(null,
                net.minecraft.core.NonNullList.withSize(3, ItemStack.EMPTY), false).handleOnServer(player);
        new me.myogoo.extendedterminal.network.serverbound.FillStonecutterGridFromRecipePacket(null,
                ItemStack.EMPTY, false).handleOnServer(player);
        helper.assertTrue(player.getInventory().isEmpty(), "Late requests after closing menu are harmless");
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
                host.stoneRecipeId = helper.getLevel().getRecipeManager().getRecipesFor(RecipeType.STONECUTTING,
                        new SingleRecipeInput(input.getStackInSlot(0)), helper.getLevel()).getFirst().id();
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

    // Exercise client-side refresh decisions without loading client networking on the GameTest server.
    private static final class ClientRefreshTestMenu extends ETTerminalMenu {
        private boolean simulateClient;
        private int selectionRequests;
        private ClientRefreshTestMenu(FakePlayer player, TestHost host) {
            super(TYPE, 0, player.getInventory(), host);
        }
        @Override public boolean isClientSide() { return simulateClient || super.isClientSide(); }
        @Override public void setStoneCutterRecipeId(ResourceLocation id) {
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
        private final Map<ResourceLocation, InternalInventory> inventories = new HashMap<>();
        private final IConfigManager config = new ConfigManager((manager, setting) -> {});
        private ETTerminalMode mode;
        private ResourceLocation stoneRecipeId;

        private TestHost(Player player, ETTerminalMode mode) {
            super(Items.STICK, player, MenuLocators.forStack(new ItemStack(Items.STICK)));
            this.mode = mode;
            for (var panel : ETTerminalMode.values()) {
                inventories.put(panel.getInventoryId(), new AppEngInternalInventory(panel == ETTerminalMode.CRAFTING
                        ? 9 : panel.getInputSlotSemantics().size()));
            }
        }

        @Override public InternalInventory getSubInventory(ResourceLocation id) { return inventories.get(id); }
        @Override public MEStorage getInventory() { return NullInventory.of(); }
        @Override public ILinkStatus getLinkStatus() { return ILinkStatus.ofDisconnected(); }
        @Override public IConfigManager getConfigManager() { return config; }
        @Override public void returnToMainMenu(Player player, ISubMenu subMenu) { }
        @Override public ItemStack getMainMenuIcon() { return ItemStack.EMPTY; }
        @Override public ETTerminalMode getMode() { return mode; }
        @Override public void setMode(ETTerminalMode mode) { this.mode = mode; }
        @Override public ResourceLocation getStoneCutterRecipeId() { return stoneRecipeId; }
        @Override public void setStoneCutterRecipeId(ResourceLocation id) { stoneRecipeId = id; }
        @Override public ExperienceMath.ExperienceSource getRememberedAnvilExperienceSource() { return null; }
        @Override public void setRememberedAnvilExperienceSource(ExperienceMath.ExperienceSource source) { }
    }
}
