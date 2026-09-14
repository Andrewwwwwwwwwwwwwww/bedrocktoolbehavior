# BedrockToolBehavior

Hold-to-use for hoes, shovels and axes that keeps up with the crosshair, the way it does on
Bedrock. Sweep across a field with right-click held and every block you pass gets tilled, pathed or
stripped — instead of every fourth one.

Client-side only. Works in singleplayer and on **vanilla servers**, with nothing installed
server-side.

## Why Java feels wrong

The rate is hard-coded. `Minecraft.startUseItem` sets a four-tick cooldown as its very first
statement — before it has checked whether your hands are busy, before it has looked at what is
under the crosshair, and regardless of whether the use does anything at all. `handleKeybinds` only
calls it again once that counter reaches zero, so holding right-click gives exactly five uses per
second no matter how fast you sweep. Move the crosshair quicker than that and you skip ground.

## What this changes

Not the clock — the trigger. A block the crosshair has just arrived at is used immediately; a block
it is still sitting on keeps vanilla's timing. Each distinct block gets exactly one interaction and
never a repeat, which is both what makes a sweep feel continuous and what keeps the interaction
rate looking like a player rather than an autoclicker.

Positions stay remembered for a short while after use. In multiplayer the client predicts the use
locally but the block does not change until the server's update arrives, so without that memory a
block would keep looking untouched — and re-usable — for a whole round trip, stuttering the tool
sound and sending duplicate packets.

Everything else is untouched: entity interactions, empty hands, other items, and any tool you have
switched off all keep vanilla timing exactly.

## Keys

Rebindable under **Options → Controls → BedrockToolBehavior**:

| Key | Action |
|-----|--------|
| `'` | Toggle Fast Tool Use |

The new state flashes above the hotbar (green ON / red OFF) and is saved.

## Config

`config/bedrocktoolbehavior.json`, written on first run:

| Setting | Default | Meaning |
|---------|---------|---------|
| `enabled` | `true` | Master switch, same as the toggle key |
| `hoe` / `shovel` / `axe` | `true` | Which tools get the faster repeat |
| `newBlockDelay` | `1` | Ticks to wait on a block you have just moved onto (0–4) |
| `sameBlockDelay` | `4` | Ticks to wait on a block you are still on (4 is vanilla) |
| `recentBlockTicks` | `10` | How long a used position stays remembered |

Raise `newBlockDelay` to tone the effect down; `4` everywhere is identical to vanilla.

## Install

1. Install [Fabric Loader](https://fabricmc.net/) and [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api) for Minecraft 26.2.
2. Drop the jar into your `mods` folder (client only — servers don't need it).
3. Hold right-click with a hoe and sweep.

## Notes

- Tilling, pathing and stripping each cost a point of durability, in vanilla and here alike. Being
  able to actually sweep means spending that durability about four times faster, so a wooden hoe
  goes quickly.
- Tool eligibility is just the `hoes` / `shovels` / `axes` item tags, so modded tools are covered
  automatically, as are modded blocks those tools know how to work.
- A NeoForge build of the same mod lives in its own repository.
