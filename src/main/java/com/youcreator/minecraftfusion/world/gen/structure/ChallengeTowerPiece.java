package com.youcreator.minecraftfusion.world.gen.structure;

import com.youcreator.minecraftfusion.MinecraftFusion;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.Random;

public class ChallengeTowerPiece extends TemplateStructurePiece {

    private final ResourceLocation templateResource;
    private final Rotation rotation;

    public ChallengeTowerPiece(TemplateManager templateManager, ResourceLocation locationIn, 
                              BlockPos position, Rotation rotationIn, int yOffset) {
        super(ModStructurePieces.CHALLENGE_TOWER, 0);
        
        this.templateResource = locationIn;
        this.templatePosition = position.offset(0, yOffset, 0);
        this.rotation = rotationIn;
        this.setupPiece(templateManager);
    }

    public ChallengeTowerPiece(TemplateManager templateManager, CompoundNBT nbt) {
        super(ModStructurePieces.CHALLENGE_TOWER, nbt);
        this.templateResource = new ResourceLocation(nbt.getString("Template"));
        this.rotation = Rotation.valueOf(nbt.getString("Rotation"));
        this.setupPiece(templateManager);
    }

    private void setupPiece(TemplateManager templateManager) {
        Template template = templateManager.getOrCreate(this.templateResource);
        PlacementSettings placementsettings = (new PlacementSettings())
                .setRotation(this.rotation)
                .setMirror(Mirror.NONE)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        this.setup(template, this.templatePosition, placementsettings);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, IServerWorld world, Random rand, 
                                   MutableBoundingBox boundingBox) {
        // Handle data markers from structure blocks, such as chest positions
        if (function.startsWith("chest")) {
            // Replace structure block with chest and populate it
            world.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);
            TileEntity tileentity = world.getBlockEntity(pos);
            
            if (tileentity instanceof ChestTileEntity) {
                // Add some loot to the chest based on the position in the tower
                ((ChestTileEntity)tileentity).setLootTable(
                    new ResourceLocation(MinecraftFusion.MOD_ID, "chests/challenge_tower"), 
                    rand.nextLong());
            }
        } else if (function.startsWith("trap")) {
            // Create a trap in the tower (e.g., pressure plate connected to TNT)
            world.setBlock(pos, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 2);
            
            // Place TNT underneath
            world.setBlock(pos.below(), Blocks.TNT.defaultBlockState(), 2);
        } else if (function.startsWith("spawner")) {
            // Place a spawner block
            world.setBlock(pos, Blocks.SPAWNER.defaultBlockState(), 2);
            
            // Configure the spawner
            TileEntity tileentity = world.getBlockEntity(pos);
            
            if (tileentity instanceof net.minecraft.tileentity.MobSpawnerTileEntity) {
                // Set the spawner to create zombies or similar
                ((net.minecraft.tileentity.MobSpawnerTileEntity)tileentity)
                    .getSpawner()
                    .setEntityId(net.minecraft.entity.EntityType.ZOMBIE);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putString("Template", this.templateResource.toString());
        nbt.putString("Rotation", this.rotation.name());
    }

    @Override
    public boolean postProcess(ISeedReader world, ChunkGenerator generator, Random randomIn, 
                              MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos) {
        // Prepare blocks for structure placement
        PlacementSettings placementsettings = (new PlacementSettings())
                .setRotation(this.rotation)
                .setMirror(Mirror.NONE)
                .addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        
        // Adjust the position to be on the surface of the world
        BlockPos adjustedPosition = this.templatePosition;
        
        // Create a solid foundation for the structure if it's floating
        if (adjustedPosition.getY() < 60) {
            adjustedPosition = new BlockPos(adjustedPosition.getX(), 60, adjustedPosition.getZ());
        }
        
        // Get the template
        Template template = this.template;
        
        // If the template doesn't exist, something went wrong
        if (template == null) {
            MinecraftFusion.LOGGER.error("Template not found: " + this.templateResource);
            return false;
        }
        
        // Get the blocks to be placed
        BlockState blockstate = world.getBlockState(adjustedPosition.below());
        
        // Make sure there's solid ground to build on
        if (!blockstate.getMaterial().isSolid()) {
            for (int x = 0; x < template.getSize().getX(); x++) {
                for (int z = 0; z < template.getSize().getZ(); z++) {
                    BlockPos foundationPos = adjustedPosition.offset(x, -1, z);
                    world.setBlock(foundationPos, Blocks.DIRT.defaultBlockState(), 2);
                }
            }
        }
        
        // Adjust the template position to the new adjusted position 
        this.templatePosition = adjustedPosition;
        
        // Place the structure
        return super.postProcess(world, generator, randomIn, structureBoundingBoxIn, chunkPos);
    }
}