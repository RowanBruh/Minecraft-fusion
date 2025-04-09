package com.youcreator.minecraftfusion.init;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.blocks.BouncyBlock;
import com.youcreator.minecraftfusion.blocks.LuckyBlock;
import com.youcreator.minecraftfusion.blocks.TrapBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// Register all our blocks here
public class ModBlocks {
    // Create a deferred register for our blocks
    public static final DeferredRegister<Block> BLOCKS = 
            DeferredRegister.create(ForgeRegistries.BLOCKS, MinecraftFusion.MOD_ID);
    
    // Register blocks
    public static final RegistryObject<Block> LUCKY_BLOCK = BLOCKS.register("lucky_block", 
            () -> new LuckyBlock(Block.Properties.of(Material.STONE)
                    .strength(1.5f, 6.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
            ));
    
    public static final RegistryObject<Block> TRAP_BLOCK = BLOCKS.register("trap_block", 
            () -> new TrapBlock(Block.Properties.of(Material.METAL)
                    .strength(2.0f, 6.0f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
            ));
    
    public static final RegistryObject<Block> BOUNCY_MUSHROOM_CAP = BLOCKS.register("bouncy_mushroom_cap", 
            () -> new BouncyBlock(Block.Properties.of(Material.CLAY)
                    .strength(0.5f)
                    .sound(SoundType.SLIME_BLOCK)
                    .noOcclusion()  // Makes the block not render adjacently
                    .jumpFactor(2.0f)  // Makes the block bouncy
            ));
    
    // Register BlockItems
    public static void registerBlockItems() {
        // Register a BlockItem for each of our blocks
        ModItems.ITEMS.register("lucky_block", 
                () -> new BlockItem(LUCKY_BLOCK.get(), 
                new Item.Properties().tab(MinecraftFusion.ITEM_GROUP)));
                
        ModItems.ITEMS.register("trap_block", 
                () -> new BlockItem(TRAP_BLOCK.get(), 
                new Item.Properties().tab(MinecraftFusion.ITEM_GROUP)));
                
        ModItems.ITEMS.register("bouncy_mushroom_cap", 
                () -> new BlockItem(BOUNCY_MUSHROOM_CAP.get(), 
                new Item.Properties().tab(MinecraftFusion.ITEM_GROUP)));
    }
}