# CurseForge listing text

## Summary

Hold right-click and sweep: hoes, shovels and axes keep up with your crosshair instead of ticking along at five uses a second. Client-side, works on vanilla servers, one toggle key.

## Description

On Bedrock you hold right-click, drag across the ground, and every block you pass gets tilled. On Java the same sweep skips most of them. **BedrockToolBehavior** makes held right-click track your crosshair instead of a fixed clock, so hoes, shovels and axes keep up with how fast you actually move.

Works in **singleplayer and on any server, vanilla included** — nothing to install server-side.

### Why Java feels different

The rate is hard-coded. The game sets a four-tick cooldown the instant you use an item, before it has looked at what is under your crosshair or whether the use did anything at all, and it will not try again until that counter runs out. That is five uses per second however fast you sweep — move quicker and the blocks in between are never touched.

### What it changes

That cooldown is what decides how often the game *looks* at your crosshair, and while it runs the game is blind to you moving onto new ground. So while a tool is in your hand it checks every tick, and the rate limiting moves to where it belongs: **per block**.

A block you have just moved onto is used immediately. A block you are still standing on keeps vanilla's timing exactly. Every block gets one interaction and never a repeat — which is what makes a sweep feel continuous, and what keeps your interaction rate looking like a player rather than an autoclicker.

Everything else is untouched: interacting with entities, using other items, and any tool you switch off all behave exactly as they always have.

### Tools

- **Hoe** — tills grass, dirt, coarse dirt, dirt paths and rooted dirt
- **Shovel** — flattens ground into grass paths
- **Axe** — strips logs, scrapes and unwaxes copper

Eligibility uses the vanilla `hoes`, `shovels` and `axes` item tags, so modded tools are covered automatically, as are modded blocks those tools already know how to work.

### Controls

One rebindable key under **Options → Controls → BedrockToolBehavior** (default `'`) toggles it on and off, with the new state flashed above your hotbar.

### Configuration

`config/bedrocktoolbehavior.json` — per-tool switches for hoe, shovel and axe, plus `pollDelay` (how often the crosshair is checked) and `sameBlockCooldown` (how long before the same block may be used again). Defaults suit singleplayer and normal servers; raise `sameBlockCooldown` if you play at high ping.

### A note on durability

Tilling, pathing and stripping each cost a point of durability, here exactly as in vanilla. Being able to actually sweep means spending that durability about four times faster, so a wooden hoe goes quickly. That is the feature working, not a side effect.

## CurseForge project settings

- **Name:** BedrockToolBehavior
- **Categories:** Utility & QoL (Fabric edition also: Fabric; NeoForge edition: NeoForge)
- **Game version:** Minecraft 26.2
- **Mod loader:** Fabric (this repo) / NeoForge (the `-neoforge` repo)
- **Environment:** Client
- **Required dependency:** Fabric API (Fabric edition only)
- **License:** All Rights Reserved
- **Icon:** `branding/icon-curseforge-animated.gif`, or `branding/icon-curseforge-512.png` for a still
- **Source:** https://github.com/Andrewwwwwwwwwwwwwww/bedrocktoolbehavior

## File upload

- **Fabric jar:** `curseforge-upload/bedrocktoolbehavior-1.0.0+mc26.2.jar`
- **NeoForge jar:** in the `-neoforge` repo, `curseforge-upload/bedrocktoolbehavior-1.0.0+mc26.2-neoforge.jar`
- **Release type:** Release
- **Changelog:** the 1.0.0 entry from `CHANGELOG.md`
