# Extended Terminal for Minecraft 26.1.2

Ported from the 1.21.1 module. Uses NeoForge and Java 25, with its own Gradle 9.2.1 wrapper; the older modules retain the root Gradle 8 build.

From this directory on Windows:

```powershell
cmd.exe /c "gradlew.bat compileJava test build --console=plain --no-configuration-cache"
cmd.exe /c "gradlew.bat runGameTestServer --console=plain --no-configuration-cache"
cmd.exe /c "gradlew.bat runData --console=plain --no-configuration-cache"
cmd.exe /c "gradlew.bat runClient --console=plain --no-configuration-cache"
```

Supported integrations: Extended Crafting 8.0.1, Re:Avaritia 1.4.2, JEI 29.33.0.87, and AE2WTLib 26.1.1-beta. AE2 26.1.10-beta and Myotus 26.0.0 are required. Existing 1.21.1 assets are reused with the new item and AE2 part model definitions.

AvaritiaNeo, EMI, Polymorph, and Inventory Tweaks are not included in this port. Older modules are unchanged by these exclusions.

Optional runtime integrations can be disabled with `-Penable_excrafting=false -Penable_avaritia=none -Penable_wtlib=false -PitemListMod=none`.

Recipe sources are in `src/main/resources/data/extendedterminal/recipe`. After `runData`, sync the generated recipes from `src/generated/resources/data/extendedterminal/recipe` back to that directory; generated output is ignored by Git.

Material Converter and legacy material conversion recipes are removed in 26.1.2.

Regression GameTests cover United crafting, Extended panel input/mode/power checks, recipe-transfer validation, and wireless inventory persistence and magnet terminal lookup. Wireless tests run only when AE2WTLib is enabled. Power-transition tests use simulated activation; magnet tests check terminal lookup, not dropped-item attraction.

Verification also covers compilation, unit tests, recipe generation, dedicated-server startup with and without optional integrations, client startup, and a client connection with JEI registration. Actual GUI transfer clicks, panel switching, real ME-network power transitions, dropped-item attraction, and reconnect behavior still need in-game validation.

On a Wireless Universal Terminal, the Extended and United panels retain their existing shared first nine crafting slots. Editing the Extended panel now preserves the remaining 72 United slots; items already lost before this fix cannot be recovered.

GitHub releases accept `26.1.2-26.0.0` and `26.1.2-26.0.0-hotfixN` tags (optionally prefixed with `v`). The workflow uses this module's Gradle wrapper and Java 25, and uploads the JAR to GitHub, Modrinth, and CurseForge. The mod version must match `gradle.properties`.

The tested Re:Avaritia/Jade dependencies log their own recipe/loot-table errors during server startup. No workaround for those upstream issues is included.
