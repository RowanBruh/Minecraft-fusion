package com.youcreator.minecraftfusion.world.gen.biome;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.init.ModWorldGen;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

public class TrollForestBiome {

    // Registry key for our biome
    public static final RegistryKey<Biome> TROLL_FOREST_KEY = RegistryKey.create(
            Registry.BIOME_REGISTRY,
            new ResourceLocation(MinecraftFusion.MOD_ID, "troll_forest")
    );

    public static void registerBiome() {
        // Store our biome key for later use
        ModWorldGen.BIOME_KEYS.put(new ResourceLocation(MinecraftFusion.MOD_ID, "troll_forest"), TROLL_FOREST_KEY);
        
        // Add the biome to the biome dictionary
        BiomeDictionary.addTypes(TROLL_FOREST_KEY,
                BiomeDictionary.Type.FOREST,
                BiomeDictionary.Type.MAGICAL,
                BiomeDictionary.Type.OVERWORLD);
                
        // Add the biome to the world generation
        BiomeManager.addBiome(BiomeManager.BiomeType.COOL,
                new BiomeManager.BiomeEntry(TROLL_FOREST_KEY, 10)); // Weight determines how common it is
        
        MinecraftFusion.LOGGER.info("Registered Troll Forest biome");
    }
    
    // For reference, actual biome configuration is in JSON
    /*
    public static Biome createTrollForestBiome() {
        // Configure the biome builder
        MobSpawnInfo.Builder mobSpawnBuilder = new MobSpawnInfo.Builder();
        
        // Add mob spawns
        mobSpawnBuilder.addSpawn(EntityClassification.MONSTER, 
                new MobSpawnInfo.Spawners(EntityType.ZOMBIE, 100, 4, 4));
        mobSpawnBuilder.addSpawn(EntityClassification.MONSTER, 
                new MobSpawnInfo.Spawners(ModEntities.TROLL_ENTITY.get(), 80, 1, 3));
        
        // Add standard passive mobs
        BiomeDefaultFeatures.farmAnimals(mobSpawnBuilder);
        BiomeDefaultFeatures.commonSpawns(mobSpawnBuilder);
        
        // Configure biome generation settings
        BiomeGenerationSettings.Builder biomeGenBuilder = new BiomeGenerationSettings.Builder()
                .surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
        
        // Add standard biome features
        BiomeDefaultFeatures.addDefaultOverworldLandStructures(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultCarvers(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultLakes(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultMonsterRoom(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultUndergroundVariety(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultOres(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultSoftDisks(biomeGenBuilder);
        
        // Add forest-specific features
        BiomeDefaultFeatures.addDefaultFlowers(biomeGenBuilder);
        BiomeDefaultFeatures.addForestGrass(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultMushrooms(biomeGenBuilder);
        BiomeDefaultFeatures.addDefaultExtraVegetation(biomeGenBuilder);
        BiomeDefaultFeatures.addSurfaceFreezing(biomeGenBuilder);
        
        // Build and return the biome
        return new Biome.Builder()
                .precipitation(Biome.RainType.RAIN)
                .biomeCategory(Biome.Category.FOREST)
                .depth(0.15F)
                .scale(0.3F)
                .temperature(0.6F)
                .downfall(0.7F)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(4012758)
                        .waterFogColor(329523)
                        .fogColor(10473397)
                        .skyColor(7829503)
                        .grassColorOverride(5681212)
                        .foliageColorOverride(6014293)
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(mobSpawnBuilder.build())
                .generationSettings(biomeGenBuilder.build())
                .build();
    }
    */
}