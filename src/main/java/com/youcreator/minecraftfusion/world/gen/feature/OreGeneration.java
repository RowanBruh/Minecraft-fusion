package com.youcreator.minecraftfusion.world.gen.feature;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.init.ModBlocks;
import com.youcreator.minecraftfusion.init.ModWorldGen;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;

public class OreGeneration {

    // Register all ores for world generation
    public static void registerOres() {
        // Register lucky block ore generation
        registerLuckyBlockOre();
        
        // Register trap block ore generation
        registerTrapBlockOre();
        
        MinecraftFusion.LOGGER.info("Registered ore generation");
    }
    
    // Method to register the lucky block ore
    private static void registerLuckyBlockOre() {
        // Configure the lucky block ore feature
        ModWorldGen.registerConfiguredFeature("lucky_ore",
            Feature.ORE.configured(
                new OreFeatureConfig(
                    OreFeatureConfig.FillerBlockType.NATURAL_STONE,  // Target stone, granite, etc.
                    ModBlocks.LUCKY_BLOCK.get().defaultBlockState(), // The block to place
                    4                                                // Vein size (number of blocks)
                )
            ).decorated(
                Placement.RANGE.configured(
                    new TopSolidRangeConfig(
                        20,   // Bottom offset from world bottom
                        40,   // Minimum y level
                        80    // Maximum y level
                    )
                )
            ).squared()     // Spread horizontally
            .count(6)       // Attempts per chunk
        );
    }
    
    // Method to register the trap block ore
    private static void registerTrapBlockOre() {
        // Configure the trap block ore feature
        ModWorldGen.registerConfiguredFeature("trap_ore",
            Feature.ORE.configured(
                new OreFeatureConfig(
                    OreFeatureConfig.FillerBlockType.NATURAL_STONE,  // Target stone, granite, etc.
                    ModBlocks.TRAP_BLOCK.get().defaultBlockState(),  // The block to place
                    5                                                // Vein size (number of blocks)
                )
            ).decorated(
                Placement.RANGE.configured(
                    new TopSolidRangeConfig(
                        5,    // Bottom offset from world bottom
                        10,   // Minimum y level
                        60    // Maximum y level
                    )
                )
            ).squared()     // Spread horizontally
            .count(4)       // Attempts per chunk
        );
    }
    
    // Method to add ores to biomes
    public static void addOresToBiomes(BiomeLoadingEvent event) {
        String biomeName = event.getName().toString().toLowerCase();
        
        // Add lucky ore to all biomes except ocean, nether, and end
        if (!biomeName.contains("nether") && !biomeName.contains("end")) {
            
            // Add lucky ores to standard biomes
            event.getGeneration().addFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                ModWorldGen.getConfiguredFeature(new net.minecraft.util.ResourceLocation(MinecraftFusion.MOD_ID, "lucky_ore"))
            );
        }
        
        // Add trap ores to only certain biomes
        if (biomeName.contains("hills") || 
            biomeName.contains("mountain") || 
            biomeName.contains("forest") ||
            biomeName.contains(MinecraftFusion.MOD_ID + ":troll_forest")) {
            
            // Add trap ores to dangerous biomes
            event.getGeneration().addFeature(
                GenerationStage.Decoration.UNDERGROUND_ORES,
                ModWorldGen.getConfiguredFeature(new net.minecraft.util.ResourceLocation(MinecraftFusion.MOD_ID, "trap_ore"))
            );
        }
    }
}