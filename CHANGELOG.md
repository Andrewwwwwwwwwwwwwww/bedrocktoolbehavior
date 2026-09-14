# Changelog

## v1.0.0 (2026-09-13)

First release. Client-side hold-to-use pacing for hoes, shovels and axes on Fabric / Minecraft 26.2
— works in singleplayer and on vanilla servers with nothing installed server-side.

- Held right-click now keeps up with the crosshair instead of vanilla's flat four-tick cooldown.
  Sweeping a hoe across ground tills every block passed rather than every fourth.
- A block you are still standing on keeps vanilla timing, so holding the button on one spot behaves
  exactly as it always has. Every block gets one interaction and never a repeat.
- Covers hoes, shovels and axes by item tag, so modded tools and modded blocks are included.
  Entity interactions, other items and disabled tools keep vanilla timing untouched.
- Rebindable toggle key under Options → Controls → BedrockToolBehavior (default `'`), with colored
  action-bar feedback.
- Settings persist in `config/bedrocktoolbehavior.json`: per-tool switches for hoe, shovel and axe,
  plus `pollDelay` and `sameBlockCooldown`.
- Mod icon built from the vanilla netherite shovel texture with the vanilla enchantment glint
  scrolled over it; an animated version is in `branding/` for the project listing.
