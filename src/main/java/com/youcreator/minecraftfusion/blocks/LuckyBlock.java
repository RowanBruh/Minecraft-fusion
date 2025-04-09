package com.youcreator.minecraftfusion.blocks;

import com.youcreator.minecraftfusion.MinecraftFusion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class LuckyBlock extends Block {

    public LuckyBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, 
                              PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        
        // Remove the block when interacted with
        world.removeBlock(pos, false);
        
        // Generate a random effect
        Random rand = world.getRandom();
        int effect = rand.nextInt(10); // 0-9 possible effects
        
        switch (effect) {
            case 0:
                // Spawn some diamonds
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.DIAMOND, 1 + rand.nextInt(3)));
                break;
            case 1:
                // Spawn a mob
                summonRandomMob(world, pos);
                break;
            case 2:
                // Lightning strike
                if (world instanceof ServerWorld) {
                    LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                    lightning.moveTo(Vector3d.atBottomCenterOf(pos));
                    world.addFreshEntity(lightning);
                }
                break;
            case 3:
                // Spawn enchanted items
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), 
                        new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1));
                break;
            case 4:
                // Spawn emeralds
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.EMERALD, 1 + rand.nextInt(5)));
                break;
            case 5:
                // Spawn iron ingots
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, 1 + rand.nextInt(8)));
                break;
            case 6:
                // Spawn gold ingots
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT, 1 + rand.nextInt(4)));
                break;
            case 7:
                // Spawn netherite scrap
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.NETHERITE_SCRAP, 1));
                break;
            case 8:
                // Spawn TNT and ignite it
                world.setBlock(pos, net.minecraft.block.Blocks.TNT.defaultBlockState(), 3);
                world.getBlockState(pos).catchFire(world, pos, null, null);
                world.removeBlock(pos, false);
                break;
            case 9:
                // Spawn a totem of undying
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.TOTEM_OF_UNDYING, 1));
                break;
            default:
                // Default to coal as a fallback
                spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.COAL, 1 + rand.nextInt(5)));
                break;
        }
        
        return ActionResultType.CONSUME;
    }
    
    // Helper method to spawn item stacks in the world
    private static void spawnItemStack(World world, double x, double y, double z, ItemStack stack) {
        double offsetX = world.random.nextFloat() * 0.5F + 0.25D;
        double offsetY = world.random.nextFloat() * 0.5F + 0.25D;
        double offsetZ = world.random.nextFloat() * 0.5F + 0.25D;
        
        ItemEntity itemEntity = new ItemEntity(world, x + offsetX, y + offsetY, z + offsetZ, stack);
        itemEntity.setDefaultPickUpDelay();
        world.addFreshEntity(itemEntity);
    }
    
    // Helper method to summon a random mob
    private void summonRandomMob(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld)) {
            return;
        }
        
        Random rand = world.getRandom();
        int mobChoice = rand.nextInt(5);
        
        // Create a suitable entity depending on the roll
        switch (mobChoice) {
            case 0:
                // Friendly villager
                EntityType.VILLAGER.spawn((ServerWorld) world, null, null, pos.above(), 
                        net.minecraft.world.spawner.AbstractSpawner.NO_ANIMATION, false, false);
                break;
            case 1:
                // Zombie
                EntityType.ZOMBIE.spawn((ServerWorld) world, null, null, pos.above(), 
                        net.minecraft.world.spawner.AbstractSpawner.NO_ANIMATION, false, false);
                break;
            case 2:
                // Skeleton
                EntityType.SKELETON.spawn((ServerWorld) world, null, null, pos.above(), 
                        net.minecraft.world.spawner.AbstractSpawner.NO_ANIMATION, false, false);
                break;
            case 3:
                // Creeper
                EntityType.CREEPER.spawn((ServerWorld) world, null, null, pos.above(), 
                        net.minecraft.world.spawner.AbstractSpawner.NO_ANIMATION, false, false);
                break;
            case 4:
                // Sheep
                EntityType.SHEEP.spawn((ServerWorld) world, null, null, pos.above(), 
                        net.minecraft.world.spawner.AbstractSpawner.NO_ANIMATION, false, false);
                break;
        }
    }
}