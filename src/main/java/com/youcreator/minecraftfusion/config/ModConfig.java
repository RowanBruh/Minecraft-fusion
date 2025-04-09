package com.youcreator.minecraftfusion.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

import java.nio.file.Path;

public class ModConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final CommonConfig COMMON_CONFIG;
    
    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON_CONFIG = new CommonConfig(builder);
        COMMON_SPEC = builder.build();
    }
    
    public static class CommonConfig {
        // World Generation settings
        public final IntValue luckyBlockRarity;
        public final IntValue challengeTowerDistance;
        public final BooleanValue generateTrollForest;
        
        // Entity Settings
        public final DoubleValue trollHealthMultiplier;
        public final IntValue trollSpawnWeight;
        
        // Item Settings
        public final DoubleValue superSwordDamageMultiplier;
        public final IntValue trollStickDurability;
        
        // Misc Settings
        public final BooleanValue enablePrankEffects;
        
        CommonConfig(ForgeConfigSpec.Builder builder) {
            builder.comment("Minecraft Fusion Mod Configuration");
            
            builder.push("World Generation");
            luckyBlockRarity = builder
                    .comment("Rarity of lucky blocks in the world (higher = more rare)")
                    .defineInRange("luckyBlockRarity", 10, 1, 100);
                    
            challengeTowerDistance = builder
                    .comment("Average distance between challenge towers in chunks")
                    .defineInRange("challengeTowerDistance", 32, 16, 64);
                    
            generateTrollForest = builder
                    .comment("Whether troll forests should be generated")
                    .define("generateTrollForest", true);
            builder.pop();
            
            builder.push("Entity Settings");
            trollHealthMultiplier = builder
                    .comment("Multiplier for troll entity health")
                    .defineInRange("trollHealthMultiplier", 1.0, 0.5, 3.0);
                    
            trollSpawnWeight = builder
                    .comment("Spawn weight for troll entities (higher = more common)")
                    .defineInRange("trollSpawnWeight", 80, 0, 200);
            builder.pop();
            
            builder.push("Item Settings");
            superSwordDamageMultiplier = builder
                    .comment("Damage multiplier for super sword")
                    .defineInRange("superSwordDamageMultiplier", 1.0, 0.5, 5.0);
                    
            trollStickDurability = builder
                    .comment("Durability of the troll stick")
                    .defineInRange("trollStickDurability", 100, 50, 500);
            builder.pop();
            
            builder.push("Misc Settings");
            enablePrankEffects = builder
                    .comment("Whether to enable prank effects from troll blocks and items")
                    .define("enablePrankEffects", true);
            builder.pop();
        }
    }
    
    public static void loadConfig(CommentedFileConfig config, Path path) {
        config.load();
        // For 1.16.5, we just save the config
        config.save();
    }
}