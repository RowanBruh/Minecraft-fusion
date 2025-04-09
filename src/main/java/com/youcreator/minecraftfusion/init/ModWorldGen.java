package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.world.gen.feature.OreGeneration;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = MinecraftFusion.MOD_ID)
public class ModWorldGen {

    // Registry keys for our biomes
    public static final Map<ResourceLocation, RegistryKey<Biome>> BIOME_KEYS = new HashMap<>();
    
    // Registry for our configured structures
    public static final Map<ResourceLocation, StructureFeature<?, ?>> CONFIGURED_STRUCTURES = new HashMap<>();
    
    // Registry for our configured features
    private static final Map<ResourceLocation, ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = new HashMap<>();
    
    // Method for registering configured features (ores, vegetation, etc.)
    public static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> registerConfiguredFeature(String name, 
            ConfiguredFeature<FC, ?> configuredFeature) {
        ResourceLocation id = new ResourceLocation(MinecraftFusion.MOD_ID, name);
        
        if (WorldGenRegistries.CONFIGURED_FEATURE.keySet().contains(id)) {
            throw new IllegalStateException("Configured Feature ID: \"" + id.toString() + "\" already exists in the registry!");
        }
        
        // Store in our local map for easier access
        CONFIGURED_FEATURES.put(id, configuredFeature);
        
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, id, configuredFeature);
    }
    
    // Method for getting configured features by ID
    @SuppressWarnings("unchecked")
    public static <FC extends IFeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> 
            getConfiguredFeature(ResourceLocation id) {
        return (ConfiguredFeature<FC, F>) CONFIGURED_FEATURES.get(id);
    }
    
    // Method for registering configured structures
    public static <FC extends IFeatureConfig> StructureFeature<FC, ?> registerConfiguredStructure(String name, 
            StructureFeature<FC, ?> configuredStructure) {
        ResourceLocation id = new ResourceLocation(MinecraftFusion.MOD_ID, name);
        CONFIGURED_STRUCTURES.put(id, configuredStructure);
        
        return Registry.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, id, configuredStructure);
    }
    
    // This event is fired when biomes are loaded
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        // Add ores to appropriate biomes
        OreGeneration.addOresToBiomes(event);
        
        // Add features to the appropriate biomes
        ModFeatures.addFeaturesToBiomes(event);
        
        // Add structures to the appropriate biomes
        addStructuresToBiomes(event);
    }
    
    // Add structures to biomes
    private static void addStructuresToBiomes(BiomeLoadingEvent event) {
        // Example: Add Challenge Tower to most biomes that aren't ocean or nether
        if (event.getCategory() != Biome.Category.OCEAN && 
            event.getCategory() != Biome.Category.NETHER &&
            event.getCategory() != Biome.Category.THEEND) {
            
            // Add our challenge tower structure if we're in a suitable biome
            event.getGeneration().getStructures().add(() -> 
                getConfiguredStructure(new ResourceLocation(MinecraftFusion.MOD_ID, "challenge_tower")));
        }
    }
    
    // This event is fired when a world is loaded
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) event.getWorld();
            // Add structure spacing settings
            addStructureSpacingSettings(serverWorld);
        }
    }
    
    // Helper method to add structure spacing settings
    private static void addStructureSpacingSettings(ServerWorld serverWorld) {
        try {
            // Get the chunk generator
            ChunkGenerator chunkGenerator = serverWorld.getChunkSource().getGenerator();
            
            // Skip flat chunk generators
            if (chunkGenerator instanceof FlatChunkGenerator) {
                return;
            }
            
            // Use reflection to get the settings
            Method method = ObfuscationReflectionHelper.findMethod(ChunkGenerator.class, 
                    "func_235957_b_"); // getSettings method
            
            DimensionStructuresSettings structuresSettings = 
                    (DimensionStructuresSettings) method.invoke(chunkGenerator);
            
            // Use reflection to access the structure map
            Method getStructuresMap = ObfuscationReflectionHelper.findMethod(DimensionStructuresSettings.class,
                    "func_236195_a_"); // getStructuresMap method
            
            @SuppressWarnings("unchecked")
            Map<StructureFeature<?, ?>, StructureSeparationSettings> tempMap = 
                    (Map<StructureFeature<?, ?>, StructureSeparationSettings>) getStructuresMap.invoke(structuresSettings);
            
            // Make a copy of the map to avoid concurrency issues
            Map<StructureFeature<?, ?>, StructureSeparationSettings> structureMap = new HashMap<>(tempMap);
            
            // Add our structures to the map
            structureMap.putAll(ModStructures.STRUCTURE_SETTINGS);
            
            // Reflection to set the structures map
            ObfuscationReflectionHelper.setPrivateValue(DimensionStructuresSettings.class,
                    structuresSettings, structureMap, "field_236193_a_"); // structures field
                    
        } catch (Exception e) {
            MinecraftFusion.LOGGER.error("Error adding structure spacing settings", e);
        }
    }
    
    // Helper method to get configured structures
    @SuppressWarnings("unchecked")
    private static <FC extends IFeatureConfig> StructureFeature<FC, ?> 
        getConfiguredStructure(ResourceLocation id) {
        return (StructureFeature<FC, ?>) CONFIGURED_STRUCTURES.get(id);
    }
    
    // Initialize all world generation
    public static void init() {
        MinecraftFusion.LOGGER.info("Registering world generation for Minecraft Fusion Mod");
        
        // Register ore generation
        OreGeneration.registerOres();
        
        // Register features
        ModFeatures.registerConfiguredFeatures();
        
        // Register and set up structures
        // ModStructures class handles this via the event system
        
        // Register biomes
        com.youcreator.minecraftfusion.world.gen.biome.TrollForestBiome.registerBiome();
    }
}