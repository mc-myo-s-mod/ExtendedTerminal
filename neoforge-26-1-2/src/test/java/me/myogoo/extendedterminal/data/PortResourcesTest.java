package me.myogoo.extendedterminal.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PortResourcesTest {
    @Test
    void itemModelsAndRecipesUseThe26Format() throws Exception {
        var terminals = List.of("et_terminal", "united_terminal", "basic_terminal", "advanced_terminal",
                "elite_terminal", "ultimate_terminal", "sculk_terminal", "nether_terminal",
                "end_terminal", "extreme_terminal");
        for (var id : terminals) {
            var model = json("assets/extendedterminal/items/" + id + ".json").getAsJsonObject("model");
            assertEquals("minecraft:model", model.get("type").getAsString());
            assertEquals(5, model.getAsJsonArray("tints").size(), id);
            assertNotNull(json("assets/extendedterminal/ae2/parts/" + id + ".json"));
        }
        for (var id : List.of("wireless_et_terminal", "wireless_united_terminal",
                "charged_ender_pearl", "compat_press", "compat_processor", "printed_compat_processor")) {
            assertEquals("extendedterminal:item/" + id,
                    json("assets/extendedterminal/items/" + id + ".json")
                            .getAsJsonObject("model").get("model").getAsString());
        }

        var base = "data/extendedterminal/recipe/";
        var fallback = json(base + "extendedterminal/united_terminal.json");
        assertEquals(List.of("minecraft:crafting_table", "myotus:compat_processor", "ae2:dense_energy_cell"),
                fallback.getAsJsonArray("ingredients").asList().stream().map(v -> v.getAsString()).toList());
        assertEquals(2, fallback.getAsJsonArray("neoforge:conditions").size());
        for (var condition : fallback.getAsJsonArray("neoforge:conditions")) {
            assertEquals("neoforge:not", condition.getAsJsonObject().get("type").getAsString());
        }
        for (var key : json(base + "extendedterminal/et_terminal.json").getAsJsonObject("key").asMap().values()) {
            assertTrue(key.isJsonPrimitive(), "26.1.2 ingredient must be an item/tag string");
        }
        assertTrue(json(base + "ae2wtlib/united_etc.json").get("terminalA").isJsonPrimitive());
        assertTrue(json(base + "ae2wtlib/upgrade_wireless_united_terminal.json").get("terminal").isJsonPrimitive());
        assertNull(getClass().getClassLoader().getResource(base + "avaritianeo/united_terminal.json"));
    }

    @Test
    void materialConverterResourcesAreNotPackaged() throws Exception {
        for (var path : List.of(
                "assets/extendedterminal/blockstates/material_converter.json",
                "assets/extendedterminal/models/block/material_converter.json",
                "assets/extendedterminal/models/item/material_converter.json",
                "assets/extendedterminal/items/material_converter.json",
                "assets/extendedterminal/textures/block/material_converter.png",
                "assets/extendedterminal/ae2guide/material_converter.md",
                "data/extendedterminal/recipe/extendedterminal/material_converter.json",
                "data/extendedterminal/loot_table/blocks/material_converter.json")) {
            assertNull(getClass().getClassLoader().getResource(path), path);
        }
        try (var input = getClass().getClassLoader()
                .getResourceAsStream("assets/extendedterminal/ae2guide/index.md")) {
            assertNotNull(input);
            assertFalse(new String(input.readAllBytes(), StandardCharsets.UTF_8).contains("material_converter"));
        }
    }

    private JsonObject json(String path) throws Exception {
        try (var input = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(input, path);
            return JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
        }
    }
}
