package com.youcreator.minecraftfusion.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Random;

public class TrapBlock extends Block {

    public TrapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(World world, BlockPos pos, Entity entity) {
        // Only apply trap effects to living entities
        if (entity instanceof LivingEntity) {
            // Only apply the trap on the server side
            if (!world.isClientSide) {
                // Generate a random trap effect
                applyRandomTrapEffect(world, pos, (LivingEntity) entity);
            }
        }
        
        super.stepOn(world, pos, entity);
    }
    
    private void applyRandomTrapEffect(World world, BlockPos pos, LivingEntity entity) {
        Random rand = world.getRandom();
        int effect = rand.nextInt(6); // 0-5 possible trap effects
        
        switch (effect) {
            case 0:
                // Explode
                world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                        2.0F, Explosion.Mode.DESTROY);
                break;
            case 1:
                // Blindness effect
                ((LivingEntity) entity).addEffect(
                        new EffectInstance(Effects.BLINDNESS, 200, 0));
                break;
            case 2:
                // Launch player upward
                Vector3d motion = entity.getDeltaMovement();
                entity.setDeltaMovement(motion.x, 1.5, motion.z);
                break;
            case 3:
                // Poison effect
                ((LivingEntity) entity).addEffect(
                        new EffectInstance(Effects.POISON, 100, 1));
                break;
            case 4:
                // Slowness effect
                ((LivingEntity) entity).addEffect(
                        new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 300, 2));
                break;
            case 5:
                // Replace block with lava
                world.removeBlock(pos, false);
                world.setBlock(pos, net.minecraft.block.Blocks.LAVA.defaultBlockState(), 3);
                break;
        }
        
        // Always remove the trap block after triggering (except for lava case)
        if (effect != 5) {
            world.removeBlock(pos, false);
        }
    }
}