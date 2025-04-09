package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.world.gen.structure.ChallengeTowerStructure;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModStructures {
    // Create a deferred register for our structures
    public static final DeferredRegister<Structure<?>> STRUCTURES = 
            DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, MinecraftFusion.MOD_ID);
    
    // Register our structures
    public static final RegistryObject<ChallengeTowerStructure> CHALLENGE_TOWER = 
            STRUCTURES.register("challenge_tower", 
                    () -> new ChallengeTowerStructure(NoFeatureConfig.CODEC));
    
    // Store structure separation settings
    public static final Map<Structure<?>, StructureSeparationSettings> STRUCTURE_SETTINGS = new HashMap<>();
    
    // Setup our structures with the appropriate settings
    public static void setupStructures() {
        setupStructure(
            CHALLENGE_TOWER.get(),      // The structure itself
            new StructureSeparationSettings(
                32,                     // Average distance between structures in chunks
                16,                     // Minimum distance between structures in chunks
                339863782               // Seed modifier for structure placement
            ),
            true                        // Whether the structure can spawn in oceans
        );
        
        MinecraftFusion.LOGGER.info("Minecraft Fusion structures initialized with settings");
    }
    
    // Helper method to setup a structure with separation settings
    private static <T extends Structure<?>> void setupStructure(T structure, 
            StructureSeparationSettings settings, boolean transformSurroundingLand) {
        
        // Add the structure to the appropriate registry
        Structure.STRUCTURES_REGISTRY.put(
            new ResourceLocation(MinecraftFusion.MOD_ID, "challenge_tower").toString(),
            structure
        );
        
        // Add settings to our map for later use
        STRUCTURE_SETTINGS.put(structure, settings);
        
        // If this structure should transform surrounding land, we would add it to the list
        // In 1.16.5 the NOISE_AFFECTING_FEATURES is a final field, so we can't modify it directly
        if (transformSurroundingLand) {
            // Just log that we would do this
            MinecraftFusion.LOGGER.info("Would add structure to noise affecting features: " + 
                    structure.getRegistryName());
        }
    }
}