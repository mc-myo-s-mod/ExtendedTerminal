# Extended Terminal for Minecraft 26.1.2

Ported from the 1.21.1 module. Uses NeoForge and Java 25, with its own Gradle 9.2.1 wrapper; the older modules retain the root Gradle 8 build.

From this directory on Windows:

```powershell
cmd.exe /c "gradlew.bat compileJava test build --console=plain --no-configuration-cache"
cmd.exe /c "gradlew.bat runData --console=plain --no-configuration-cache"
cmd.exe /c "gradlew.bat runClient --console=plain --no-configuration-cache"
```

Supported integrations: Extended Crafting 8.0.1, Re:Avaritia 1.4.2, JEI 29.33.0.87, and AE2WTLib 26.1.1-beta. AE2 26.1.10-beta and Myotus 26.0.0 are required. Existing 1.21.1 assets are reused with the new item and AE2 part model definitions.

AvaritiaNeo, EMI, Polymorph, and Inventory Tweaks are not included in this port. Older modules are unchanged by these exclusions.

Optional runtime integrations can be disabled with `-Penable_excrafting=false -Penable_avaritia=none -Penable_wtlib=false -PitemListMod=none`.

Recipe sources are in `src/main/resources/data/extendedterminal/recipe`. After `runData`, sync the generated recipes from `src/generated/resources/data/extendedterminal/recipe` back to that directory; generated output is ignored by Git.

Material Converter is removed in 26.1.2. Legacy materials can still be converted to Myotus materials through crafting recipes.

Verification covers compilation, regression tests, recipe generation, dedicated-server startup with and without optional integrations, client startup, and a client connection with JEI registration. Actual terminal crafting, transfer clicks, panel switching, and reconnect behavior still need in-game validation.

The tested Re:Avaritia/Jade dependencies log their own recipe/loot-table errors during server startup. No workaround for those upstream issues is included.
