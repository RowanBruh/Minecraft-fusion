package com.youcreator.minecraftfusion.handlers;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.entities.TrollEntity;
import com.youcreator.minecraftfusion.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = MinecraftFusion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonEventHandler {

    /**
     * Register attributes for our custom entities
     */
    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.TROLL_ENTITY.get(), TrollEntity.createAttributes().build());
        
        MinecraftFusion.LOGGER.info("Registered entity attributes");
    }
    
    /**
     * Common setup event handler
     */
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // Register anything that needs to happen on both client and server
        MinecraftFusion.LOGGER.info("CommonSetup: Initializing additional components");
    }
}