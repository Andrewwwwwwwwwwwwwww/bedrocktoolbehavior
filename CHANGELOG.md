# Changelog

## v1.0.0 (2026-09-13)

First release. Client-side hold-to-use pacing for hoes, shovels and axes on Fabric / Minecraft 26.2
— works in singleplayer and on vanilla servers with nothing installed server-side.

- Held right-click now fires as soon as the crosshair moves onto a new block, instead of waiting
  out vanilla's flat four-tick cooldown. Sweeping a hoe across ground tills every block passed
  rather than every fourth.
- A block the crosshair is still resting on keeps vanilla timing, so standing still does not spam
  the tool or its sound.
- Used positions stay remembered for a short window, which stops a block being used twice while its
  server-side change is still in flight.
- Covers hoes, shovels and axes by item tag, so modded tools and modded blocks are included.
  Entity interactions, other items and disabled tools keep vanilla timing untouched.
- Rebindable toggle key under Options → Controls → BedrockToolBehavior (default `'`), with colored
  action-bar feedback.
- Settings persist in `config/bedrocktoolbehavior.json`: per-tool switches, both delays, and the
  remembered-position window.
