package com.youcreator.minecraftfusion.world.gen.feature;

import com.mojang.serialization.Codec;
import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class BouncyMushroomFeature extends Feature<NoFeatureConfig> {

    public BouncyMushroomFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, 
                        BlockPos pos, NoFeatureConfig config) {
        // Get the world surface position
        BlockPos surfacePos = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, pos);
        
        // Check if we can place the mushroom here
        if (!canPlaceMushroomAt(world, surfacePos)) {
            return false;
        }
        
        // Generate a cluster of bouncy mushrooms
        int mushroomCount = rand.nextInt(6) + 3; // 3 to 8 mushrooms
        boolean placedAny = false;
        
        for (int i = 0; i < mushroomCount; i++) {
            // Place mushrooms in a small area around the center position
            int xOffset = rand.nextInt(7) - 3;
            int zOffset = rand.nextInt(7) - 3;
            
            BlockPos mushroomPos = surfacePos.offset(xOffset, 0, zOffset);
            
            // Make sure we can place at this position
            if (canPlaceMushroomAt(world, mushroomPos)) {
                // Create mushroom of random height (1-3 blocks)
                int height = rand.nextInt(3) + 1;
                BlockState stemBlock = Blocks.MUSHROOM_STEM.defaultBlockState();
                BlockState capBlock = ModBlocks.BOUNCY_MUSHROOM_CAP.get().defaultBlockState();
                
                // Place the mushroom stem
                for (int y = 0; y < height; y++) {
                    world.setBlock(mushroomPos.above(y), stemBlock, 2);
                }
                
                // Place the mushroom cap on top
                world.setBlock(mushroomPos.above(height), capBlock, 2);
                
                // Place caps on the sides for larger mushrooms
                if (height > 1 && rand.nextBoolean()) {
                    // Add side caps for a more mushroom-like appearance
                    for (int side = 0; side < 4; side++) {
                        // 50% chance to skip a side
                        if (rand.nextBoolean()) {
                            continue;
                        }
                        
                        BlockPos sidePos = mushroomPos.above(height - 1);
                        
                        switch (side) {
                            case 0:
                                sidePos = sidePos.north();
                                break;
                            case 1:
                                sidePos = sidePos.east();
                                break;
                            case 2:
                                sidePos = sidePos.south();
                                break;
                            case 3:
                                sidePos = sidePos.west();
                                break;
                        }
                        
                        // Only place if the position is air
                        if (world.isEmptyBlock(sidePos)) {
                            world.setBlock(sidePos, capBlock, 2);
                        }
                    }
                }
                
                placedAny = true;
            }
        }
        
        return placedAny;
    }

    private boolean canPlaceMushroomAt(ISeedReader world, BlockPos pos) {
        // Check if the position is valid for a mushroom
        BlockState ground = world.getBlockState(pos.below());
        BlockState current = world.getBlockState(pos);
        
        // Mushrooms can only be placed on solid, natural blocks
        return current.isAir() && 
               (ground.is(Blocks.GRASS_BLOCK) || 
                ground.is(Blocks.DIRT) || 
                ground.is(Blocks.COARSE_DIRT) ||
                ground.is(Blocks.PODZOL) ||
                ground.is(Blocks.MYCELIUM));
    }
}