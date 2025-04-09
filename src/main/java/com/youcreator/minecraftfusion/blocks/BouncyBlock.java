package com.youcreator.minecraftfusion.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BouncyBlock extends Block {

    public BouncyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(World world, BlockPos pos, Entity entity, float fallDistance) {
        // Reduce fall damage on bouncy blocks
        if (entity.isSuppressingBounce()) {
            super.fallOn(world, pos, entity, fallDistance);
        } else {
            entity.causeFallDamage(fallDistance, 0.0F);
        }
    }

    @Override
    public void updateEntityAfterFallOn(IBlockReader reader, Entity entity) {
        // Bounce the entity up when it lands on this block
        if (entity.isSuppressingBounce()) {
            super.updateEntityAfterFallOn(reader, entity);
        } else {
            this.bounceEntity(entity);
        }
    }

    private void bounceEntity(Entity entity) {
        // Calculate the bounce velocity
        Vector3d vector3d = entity.getDeltaMovement();
        
        // Apply a higher bounce for living entities
        if (entity instanceof LivingEntity) {
            // Give a super high bounce for players who are crouching
            if (entity instanceof PlayerEntity && entity.isShiftKeyDown()) {
                entity.setDeltaMovement(vector3d.x * 0.9D, 1.8D, vector3d.z * 0.9D);
            } else {
                entity.setDeltaMovement(vector3d.x * 0.9D, 0.9D, vector3d.z * 0.9D);
            }
        } else {
            // Lower bounce for non-living entities
            entity.setDeltaMovement(vector3d.x * 0.8D, 0.5D, vector3d.z * 0.8D);
        }
    }
    
    @Override
    public void stepOn(World world, BlockPos pos, Entity entity) {
        // Apply a small upward boost when an entity walks on the block
        double upwardBoost = 0.3D;
        Vector3d motion = entity.getDeltaMovement();
        
        // Only apply boost if entity is not rising already
        if (motion.y <= 0.0D) {
            entity.setDeltaMovement(motion.x, upwardBoost, motion.z);
        }
        
        super.stepOn(world, pos, entity);
    }
}