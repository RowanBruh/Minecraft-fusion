package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.world.gen.feature.BouncyMushroomFeature;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.ChanceConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MinecraftFusion.MOD_ID)
public class ModFeatures {

    // Registry for our features
    public static final DeferredRegister<Feature<?>> FEATURES = 
            DeferredRegister.create(ForgeRegistries.FEATURES, MinecraftFusion.MOD_ID);
    
    // Register our features
    public static final RegistryObject<BouncyMushroomFeature> BOUNCY_MUSHROOM = 
            FEATURES.register("bouncy_mushroom", () -> new BouncyMushroomFeature(NoFeatureConfig.CODEC));
    
    // Configure our features for world generation
    public static void registerConfiguredFeatures() {
        // Register the bouncy mushroom patch feature
        ModWorldGen.registerConfiguredFeature("bouncy_mushroom_patch",
            BOUNCY_MUSHROOM.get().configured(NoFeatureConfig.INSTANCE)
                .decorated(Placement.CHANCE.configured(new ChanceConfig(8))) // 1 in 8 chunks have this feature
        );
        
        MinecraftFusion.LOGGER.info("Registered configured features");
    }
    
    // Add our features to biomes
    public static void addFeaturesToBiomes(net.minecraftforge.event.world.BiomeLoadingEvent event) {
        // Check if it's a forest or magical biome
        if (event.getName() != null) {
            String biomeName = event.getName().toString().toLowerCase();
            
            if (biomeName.contains("forest") || 
                biomeName.contains(MinecraftFusion.MOD_ID.toLowerCase() + ":troll_forest")) {
                
                // Add bouncy mushroom patches to forests
                event.getGeneration().addFeature(
                    GenerationStage.Decoration.VEGETAL_DECORATION,
                    ModWorldGen.getConfiguredFeature(new net.minecraft.util.ResourceLocation(MinecraftFusion.MOD_ID, "bouncy_mushroom_patch"))
                );
            }
        }
    }
}