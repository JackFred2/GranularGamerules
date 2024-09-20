# 🌍 Granular Gamerules

Grants you more control over the vanilla game rule system by:

- Breaking up some of Minecraft's most overloaded game rules, such as `mobGriefing`, `doMobSpawning` or `universalAnger`, into deferrable sub-rules.
- Adding smaller utility rules such as turning off thunder or removing underground players from the sleep vote.
- On dedicated servers, allowing some common properties in `server.properties` to be changed via fake game rules, such as `pvp` or `enableCommandBlock`.

These are all done in a way that doesn't break datapacks, mods or server plugins that change game rules, while letting you 'pin' features on or off.

## 👩‍👧‍👦 Sub-Rules

When you install the mod, nothing will change right away. That's because most rules are _deferred_ to their parent.

For example, the new rule `mobGriefing/creepersDestroyBlocks` by default will just check `mobGriefing`, but can be overridden to turn on or off Creeper destruction regardless.

There are 33 new sub-rules; for a full overview see [the Wiki](https://docs.jackf.red/granular-gamerules).

## 🛌 New Miscellaneous Rules

**Granular Gamerules** adds a couple new rules you might find yourself wanting:

- Removing underground players from the sleep vote - no longer prevented from sleeping due to miners.
- Disabling thunder from the weather cycle.

These are disabled by default, and are available on the wiki for details.

## 🖥️ Dedicated Server Rules

You may have setup a dedicated server and realised you forgot to change some settings in `server.properties`. **Granular Gamerules** lets you change several of these in-game via fake game rules:

- `pvp`
- `allowFlight`
- `enableCommandBlock`
- `maxPlayers`
- `spawnProtection`
- `viewDistance`
- `simulationDistance`
- `entityBroadcastRangePercentage`

These are backed by the actual `server.properties` file, and will update the file on change.

# 🛠️ Install & Requirements

Can work server-side only, in the case of dedicated servers.

**Granular Gamerules** requires [Fabric API](https://modrinth.com/mod/fabric-api) and [YACL](https://modrinth.com/mod/yacl), and should be good to add or remove to an existing world.

<details>

<summary>Dev Stuff</summary>

## Command Implementation

'Overlays' defer nodes and fake gamerules by re-registering the 'gamerule' node. Should be fine, seems to just add.

## Mob Griefing

Not all of these will be done, just most likely

- Weaving Effect Places Webs 
- Wither places wither rose down
- Mobs pick up items ✅
  - Allays pick up items ✅
  - Piglins pick up gold ✅
- Villagers work farmland ✅
- Zombies break doors ✅
- Sheep eat grass ✅
- Zombies break turtle eggs ✅
- Foxes harvest berry bushes ✅
- Rabbit harvest carrots ✅
- Snow golems place snow ✅
- Ender dragon breaks non-end blocks ✅
- Wither breaks blocks on damage ✅
- Endermen place/break blocks ✅
- Evokers turn blue sheep red ✅
- Ravagers destroy ✅
  - ..leaves ✅
  - ..crops ✅
- Silverfish enter stone ✅
- Silverfish wake others up from infested stone ✅
- Ghast fireballs ignite blocks ✅
- Mob launched projectiles (blaze fireballs, skeleton arrows)
  - Ignite campfires
  - Destroy dripstone, chorus flowers or pots
  - Ignite TNT
- Destroy powder snow on extinguish
- Lower cauldron level on extinguish
- Blaze fireballs set fires
- Breeze projectiles trigger blocks
- Mob sourced explosions destroy blocks
  - Creepers ✅
  - Ghast Fireballs ✅
  - Wither initial explosion ✅
  - Wither projectiles ✅
- Falling mobs destroy farmland ✅

## (Dedicated Server) Fake game rules

- Pvp ✅
- Allow Flight ✅
- Enable Command Block ✅
- Entity View Distance Percentage ✅
- Spawn Protection Radius ✅
- View Distance ✅
- Simulation Distance ✅
- Max Players ✅

Added to the /gamerule command, updates the backing server.properties

</details>
