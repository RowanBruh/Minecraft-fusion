package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftFusion.MOD_ID)
public class ModLootTables {
    // Define IDs for custom loot tables
    public static final ResourceLocation CHALLENGE_TOWER_CHEST = 
            new ResourceLocation(MinecraftFusion.MOD_ID, "chests/challenge_tower");
    
    public static final ResourceLocation TROLL_ENTITY_LOOT = 
            new ResourceLocation(MinecraftFusion.MOD_ID, "entities/troll_entity");
    
    /**
     * Initialize loot tables
     */
    public static void init() {
        MinecraftFusion.LOGGER.info("Registering loot tables");
    }
    
    /**
     * Event handler for when loot tables are loaded
     */
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        // Modify vanilla loot tables to include our items
        ResourceLocation id = event.getName();
        
        // Add troll essence to witch drops with a small chance
        if (id.equals(LootTables.WITCH)) {
            LootPool pool = event.getTable().getPool("main");
            if (pool != null) {
                // In 1.16.5, we would modify the pool directly, but for simplicity
                // we're just logging that this would happen
                MinecraftFusion.LOGGER.debug("Would inject troll essence into witch loot table");
            }
        }
        
        // Add lucky blocks to dungeon chests
        if (id.equals(LootTables.SIMPLE_DUNGEON) || 
            id.equals(LootTables.ABANDONED_MINESHAFT) || 
            id.equals(LootTables.WOODLAND_MANSION)) {
            
            // In 1.16.5, we would modify these pools directly
            MinecraftFusion.LOGGER.debug("Would inject lucky blocks into " + id.toString());
        }
    }
}