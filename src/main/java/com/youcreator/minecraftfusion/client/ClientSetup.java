package com.youcreator.minecraftfusion.client;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.init.ModEntities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Handles all client-side setup and registration
 * This includes entity renderers and particle factories
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MinecraftFusion.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    
    /**
     * Register entity renderers
     */
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftFusion.LOGGER.info("MinecraftFusion: Registering entity renderers");
        
        // Register explosive arrow renderer (uses standard arrow renderer)
        RenderingRegistry.registerEntityRenderingHandler(
                ModEntities.EXPLOSIVE_ARROW.get(),
                renderManager -> new ArrowRenderer<>(renderManager));
                
        // Add more renderers here when we implement other entities
    }
    
    /**
     * Register item models
     */
    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        MinecraftFusion.LOGGER.info("MinecraftFusion: Registering models");
        
        // Register any special item models here
    }
}