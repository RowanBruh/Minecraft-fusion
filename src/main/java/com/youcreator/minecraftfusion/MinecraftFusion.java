package com.youcreator.minecraftfusion;

import com.youcreator.minecraftfusion.config.ModConfig;
import com.youcreator.minecraftfusion.handlers.CommonEventHandler;
import com.youcreator.minecraftfusion.init.*;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MinecraftFusion.MOD_ID)
public class MinecraftFusion {
    public static final String MOD_ID = "minecraftfusion";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final ItemGroup ITEM_GROUP = new ItemGroup("minecraftfusionTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.SUPER_SWORD.get());
        }
    };

    public MinecraftFusion() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        
        // Register configs
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);
        ModConfig.loadConfig(ModConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
        
        // Register mod components
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntities.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModStructures.STRUCTURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModFeatures.FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Minecraft Fusion Mod: Initializing");
        
        // Initialize world generation after registration
        event.enqueueWork(() -> {
            // Initialize world generation components
            ModWorldGen.init();
            
            // Initialize structure settings
            ModStructures.setupStructures();
        });
        
        // Initialize loot tables
        ModLootTables.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Minecraft Fusion Mod: Client Setup");
    }
}
