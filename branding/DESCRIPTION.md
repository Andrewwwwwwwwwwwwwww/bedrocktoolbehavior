# CurseForge listing text

## Summary

Hold right-click and sweep: hoes, shovels and axes keep up with your crosshair instead of ticking along at five uses a second. Client-side, works on vanilla servers, one toggle key.

## Description

On Bedrock you hold right-click, drag across the ground, and every block you pass gets tilled. On Java the same sweep skips most of them, because the game waits a fixed four ticks between uses however fast you move.

**BedrockToolBehavior** makes held right-click follow your crosshair instead of that clock. Move onto new ground and it works immediately; stay on one block and it behaves exactly like vanilla. Every block gets one interaction and never a repeat.

**Hoes** till, **shovels** make paths, **axes** strip logs and scrape copper. Eligibility comes from the vanilla item tags, so modded tools and blocks are covered too.

Client-side only — it works in singleplayer and on any server, vanilla included, with nothing to install server-side.

Toggle it with one rebindable key (**Options → Controls**, default `'`). Per-tool switches and timing live in `config/bedrocktoolbehavior.json`.

One caveat: tilling and stripping cost durability here exactly as in vanilla, so actually being able to sweep spends it about four times faster.

## CurseForge project settings

- **Name:** BedrockToolBehavior
- **Slug:** bedrocktoolbehavior
- **Categories:** Utility & QoL
- **Game version:** Minecraft 26.2
- **Mod loader:** Fabric (this repo) / NeoForge (the `-neoforge` repo)
- **Environment:** Client
- **Required dependency:** Fabric API (Fabric edition only)
- **License:** All Rights Reserved
- **Icon:** `branding/icon-curseforge-animated.gif`, or `branding/icon-curseforge-512.png` for a still
- **Source:** https://github.com/Andrewwwwwwwwwwwwwww/bedrocktoolbehavior
- **Wiki:** https://Andrewwwwwwwwwwwwwww.github.io/modhub/mods/bedrocktoolbehavior/

## File upload

- **Fabric jar:** `curseforge-upload/bedrocktoolbehavior-1.0.0+mc26.2.jar`
- **NeoForge jar:** in the `-neoforge` repo, `curseforge-upload/bedrocktoolbehavior-1.0.0+mc26.2-neoforge.jar`
- **Release type:** Release
- **Changelog:** the 1.0.0 entry from `CHANGELOG.md`
