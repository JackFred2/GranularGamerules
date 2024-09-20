package red.jackf.granulargamerules.impl.rules;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.minecraft.world.level.GameRules;

public class MobGriefingRules {
    private static final Utils.RuleGenerator<GameRules.BooleanValue> GENERATOR = Utils.createGenerator(GameRules.RULE_MOBGRIEFING, true, GameRuleFactory::createBooleanRule);

    public static final GameRules.Key<GameRules.BooleanValue> CREATURES_EAT_PLANTS = create("creaturesEatPlants");
    public static final GameRules.Key<GameRules.BooleanValue> CREEPERS_DESTROY_BLOCKS = create("creepersDestroyBlocks");
    public static final GameRules.Key<GameRules.BooleanValue> ENDER_DRAGON_DESTROYS_BLOCKS = create("enderDragonDestroysBlocks");
    public static final GameRules.Key<GameRules.BooleanValue> ENDERMEN_MOVE_BLOCKS = create("endermenMoveBlocks");
    public static final GameRules.Key<GameRules.BooleanValue> EVOKERS_WOLOLO = create("evokersWololo");
    public static final GameRules.Key<GameRules.BooleanValue> GHASTS_DESTROY_BLOCKS = create("ghastsDestroyBlocks");
    public static final GameRules.Key<GameRules.BooleanValue> ITEMS_TAKEN_BY_ALLAYS = create("itemsTakenByAllays");
    public static final GameRules.Key<GameRules.BooleanValue> ITEMS_TAKEN_BY_OTHERS = create("itemsTakenByOthers");
    public static final GameRules.Key<GameRules.BooleanValue> ITEMS_TAKEN_BY_PIGLINS = create("itemsTakenByPiglins");
    public static final GameRules.Key<GameRules.BooleanValue> ITEMS_TAKEN_BY_VILLAGERS = create("itemsTakenByVillagers");
    public static final GameRules.Key<GameRules.BooleanValue> MOBS_CRUSH_TURTLE_EGGS = create("mobsCrushTurtleEggs");
    public static final GameRules.Key<GameRules.BooleanValue> MOBS_TRAMPLE_FARMLAND = create("mobsTrampleFarmland");
    public static final GameRules.Key<GameRules.BooleanValue> RAVAGERS_DESTROY_PLANTS = create("ravagersDestroyPlants");
    public static final GameRules.Key<GameRules.BooleanValue> SILVERFISH_ENTER_STONE = create("silverfishEnterStone");
    public static final GameRules.Key<GameRules.BooleanValue> SILVERFISH_WAKE_FRIENDS = create("silverfishWakeFriends");
    public static final GameRules.Key<GameRules.BooleanValue> SNOW_GOLEMS_LEAVE_TRAILS = create("snowGolemsLeaveTrails");
    public static final GameRules.Key<GameRules.BooleanValue> VILLAGERS_WORK_FARMLAND = create("villagersWorkFarmland");
    public static final GameRules.Key<GameRules.BooleanValue> WITHERS_DESTROY_BLOCKS = create("withersDestroyBlocks");
    public static final GameRules.Key<GameRules.BooleanValue> ZOMBIES_BREAK_DOORS = create("zombiesBreakDoors");

    public static void setup() {
        // no-op
    }

    private static GameRules.Key<GameRules.BooleanValue> create(String name) {
        return GENERATOR.create(name);
    }
}
