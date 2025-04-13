package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.items.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// Register all our items here
public class ModItems {
    // Create a deferred register for our items
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, MinecraftFusion.MOD_ID);
    
    // Register items
    public static final RegistryObject<Item> SUPER_SWORD = ITEMS.register("super_sword", 
            () -> new SuperSword(new Item.Properties()
                    .tab(MinecraftFusion.ITEM_GROUP)
                    .stacksTo(1)
                    .defaultDurability(1500)
            ));
    
    public static final RegistryObject<Item> LIGHTNING_BOW = ITEMS.register("lightning_bow", 
            () -> new LightningBow(new Item.Properties()
                    .tab(MinecraftFusion.ITEM_GROUP)
                    .stacksTo(1)
                    .defaultDurability(384)
            ));
    
    public static final RegistryObject<Item> TELEPORT_ROD = ITEMS.register("teleport_rod", 
            () -> new TeleportRod(new Item.Properties()
                    .tab(MinecraftFusion.ITEM_GROUP)
                    .stacksTo(1)
                    .defaultDurability(64)
            ));
    
    public static final RegistryObject<Item> EXPLOSIVE_TNT_WAND = ITEMS.register("explosive_tnt_wand", 
            () -> new ExplosiveTNTWand(new Item.Properties()
                    .tab(MinecraftFusion.ITEM_GROUP)
                    .stacksTo(1)
                    .defaultDurability(50)
            ));
    
    public static final RegistryObject<Item> INVISIBILITY_CLOCK = ITEMS.register("invisibility_clock", 
            () -> new InvisibilityClock(new Item.Properties()
                    .tab(MinecraftFusion.ITEM_GROUP)
                    .stacksTo(1)
                    .defaultDurability(20)
            ));
    
    public static final RegistryObject<Item> TROLL_STICK = ITEMS.register("troll_stick", 
            () -> new TrollStick(new Item.Properties()
                    .tab(MinecraftFusion.ITEM_GROUP)
                    .stacksTo(1)
                    .defaultDurability(100)
            ));
    
    public static final RegistryObject<Item> TROLL_ESSENCE = ITEMS.register("troll_essence", 
            () -> new Item(new Item.Properties()
                    .tab(MinecraftFusion.ITEM_GROUP)
                    .stacksTo(64)
            ));
}