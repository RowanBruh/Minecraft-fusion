package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.entities.TrollEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// Register all our entities here
public class ModEntities {
    // Create a deferred register for our entities
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(ForgeRegistries.ENTITIES, MinecraftFusion.MOD_ID);
    
    // Register entities
    public static final RegistryObject<EntityType<TrollEntity>> TROLL_ENTITY = 
            ENTITY_TYPES.register("troll_entity", 
                    () -> EntityType.Builder.<TrollEntity>of(
                            TrollEntity::new, 
                            EntityClassification.MONSTER)
                            .sized(0.9F, 1.8F)
                            .clientTrackingRange(8)
                            .build(new ResourceLocation(MinecraftFusion.MOD_ID, "troll_entity").toString())
            );
}