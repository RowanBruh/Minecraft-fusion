package com.youcreator.minecraftfusion;

import com.youcreator.minecraftfusion.config.ModConfig;
import com.youcreator.minecraftfusion.handlers.CommonEventHandler;
import com.youcreator.minecraftfusion.init.*;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MinecraftFusion.MOD_ID)
public class MinecraftFusion {
    public static final String MOD_ID = "minecraftfusion";
    public static final Logger LOGGER = LogManager.getLogger();
    
    // Create a deferred register for creative mode tabs
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    
    // Register our custom creative tab
    public static final RegistryObject<CreativeModeTab> ITEM_GROUP = CREATIVE_MODE_TABS.register("minecraftfusion_tab", 
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.minecraftfusion"))
                    .icon(() -> new ItemStack(ModItems.SUPER_SWORD.get()))
                    .displayItems((parameters, output) -> {
                        // Add all mod items to the tab
                        ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        // Add all mod blocks to the tab
                        ModBlocks.BLOCKS.getEntries().forEach(block -> output.accept(block.get()));
                    })
                    .build()
    );

    public MinecraftFusion() {
        // Get event bus
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register the setup methods for modloading
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        
        // Register configs
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.COMMON_SPEC);
        ModConfig.loadConfig(ModConfig.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MOD_ID + "-common.toml"));
        
        // Register mod components
        CREATIVE_MODE_TABS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBiomes.BIOMES.register(modEventBus);
        
        // Register ourselves for server and other game events
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CommonEventHandler());
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Minecraft Fusion Mod: Initializing");
        
        // Initialize world generation and structures
        event.enqueueWork(() -> {
            ModWorldGen.init();
        });
        
        // Initialize loot tables
        ModLootTables.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Minecraft Fusion Mod: Client Setup");
        
        // The ClientSetup class handles entity renderers through event subscriptions
        // Initialize other client-side only features here if needed
    }
}
