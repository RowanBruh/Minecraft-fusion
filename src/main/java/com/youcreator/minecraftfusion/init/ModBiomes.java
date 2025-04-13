package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers all biomes added by the mod
 */
public class ModBiomes {
    // Create a deferred register for biomes
    public static final DeferredRegister<Biome> BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, MinecraftFusion.MOD_ID);

    // Create resource keys for biomes
    public static final ResourceKey<Biome> TROLL_FOREST_KEY = ResourceKey.create(
            ForgeRegistries.Keys.BIOMES, 
            new ResourceLocation(MinecraftFusion.MOD_ID, "troll_forest"));
    
    public static final ResourceKey<Biome> CHALLENGE_PLAINS_KEY = ResourceKey.create(
            ForgeRegistries.Keys.BIOMES, 
            new ResourceLocation(MinecraftFusion.MOD_ID, "challenge_plains"));
    
    // Biome registry objects
    // These will be populated during datagen, so we don't need to register them here
    // We just use the keys above to reference them in world generation
}