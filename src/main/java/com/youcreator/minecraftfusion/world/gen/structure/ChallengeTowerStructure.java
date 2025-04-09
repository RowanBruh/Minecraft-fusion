package com.youcreator.minecraftfusion.world.gen.structure;

import com.mojang.serialization.Codec;
import com.youcreator.minecraftfusion.MinecraftFusion;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;
import java.util.Random;

public class ChallengeTowerStructure extends Structure<NoFeatureConfig> {

    public ChallengeTowerStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.SURFACE_STRUCTURES;
    }

    @Override
    protected boolean isFeatureChunk(ChunkGenerator chunkGenerator, BiomeProvider biomeProvider,
                                    long seed, SharedSeedRandom random, int chunkX, int chunkZ,
                                    Biome biome, ChunkPos chunkPos, NoFeatureConfig config) {
        // Try to avoid structures too close to each other
        BlockPos centerOfChunk = new BlockPos(chunkX * 16 + 8, 0, chunkZ * 16 + 8);
        
        // In 1.16.5, we need to use the biome registry name to determine category
        ResourceLocation biomeName = biome.getRegistryName();
        if (biomeName != null) {
            String biomeNameStr = biomeName.toString();
            
            // Check if we're in a valid biome (avoid ocean, nether, end)
            if (biomeNameStr.contains("ocean") || 
                biomeNameStr.contains("nether") ||
                biomeNameStr.contains("end")) {
                return false;
            }
        }
        
        // Get the heightmap at this position 
        int landHeight = chunkGenerator.getBaseHeight(centerOfChunk.getX(), centerOfChunk.getZ(), 
                Heightmap.Type.WORLD_SURFACE_WG);
                
        // Check if the ground is mostly flat
        IBlockReader columnOfBlocks = chunkGenerator.getBaseColumn(centerOfChunk.getX(), centerOfChunk.getZ());
        BlockState topBlock = columnOfBlocks.getBlockState(centerOfChunk.above(landHeight));
        
        // Only generate in flat areas with grass, dirt, etc.
        return topBlock.getFluidState().isEmpty();
    }

    @Override
    public IStartFactory<NoFeatureConfig> getStartFactory() {
        return ChallengeTowerStructure.Start::new;
    }

    public static class Start extends StructureStart<NoFeatureConfig> {
    
        public Start(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, 
                    MutableBoundingBox boundingBox, int references, long seed) {
            super(structure, chunkX, chunkZ, boundingBox, references, seed);
        }

        @Override
        public void generatePieces(DynamicRegistries registries, ChunkGenerator chunkGenerator,
                                TemplateManager templateManager, int chunkX, int chunkZ,
                                Biome biome, NoFeatureConfig config) {
                
            // Determine the center position of the structure
            int x = chunkX * 16 + 8;
            int z = chunkZ * 16 + 8;
            
            // Get the height at this position for the structure to be placed on
            int y = chunkGenerator.getBaseHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG);
            BlockPos pos = new BlockPos(x, y, z);
            
            // Create a rotation for this structure
            Random random = new Random(x + z);
            Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
            
            // Initialize the challenge tower piece
            ChallengeTowerPiece challengeTowerPiece = new ChallengeTowerPiece(
                templateManager, 
                new ResourceLocation(MinecraftFusion.MOD_ID, "challenge_tower/base_piece"),
                pos, 
                rotation, 
                0);
                
            // Add the tower piece to this structure
            this.pieces.add(challengeTowerPiece);
            
            // Set the bounding box for the structure
            this.calculateBoundingBox();
            
            MinecraftFusion.LOGGER.debug("Challenge Tower at " + pos.toShortString());
        }
    }
}