package com.youcreator.minecraftfusion.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TrollStick extends Item {

    public TrollStick(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!world.isClientSide) {
            // Determine which troll action to perform
            int trollAction = world.random.nextInt(5);
            
            switch (trollAction) {
                case 0:
                    // Launch nearby entities into the air
                    launchNearbyEntities(world, player);
                    break;
                case 1:
                    // Swap positions with a nearby entity
                    swapWithRandomEntity(world, player);
                    break;
                case 2:
                    // Turn blocks into various prank blocks
                    transformBlocks(world, player);
                    break;
                case 3:
                    // Create fake explosion particles without damage
                    createFakeExplosion(world, player);
                    break;
                case 4:
                    // Random teleport the player
                    randomTeleport(world, player);
                    break;
            }
            
            // Add cooldown to prevent spam
            player.getCooldowns().addCooldown(this, 200);
            
            // Damage the item when used
            if (stack.hurt(1, world.random, null)) {
                stack.shrink(1);
                player.broadcastBreakEvent(hand);
            }
        }
        
        // Play a sound effect when the item is used
        world.playSound(player, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.WITCH_CELEBRATE, SoundCategory.PLAYERS, 0.5F, 
                0.4F / (random.nextFloat() * 0.4F + 0.8F));
        
        return ActionResult.sidedSuccess(stack, world.isClientSide);
    }
    
    private void launchNearbyEntities(World world, PlayerEntity player) {
        double radius = 5.0D;
        List<Entity> entities = world.getEntities(player, player.getBoundingBox().inflate(radius));
        
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity && entity != player) {
                double strength = 1.5D;
                entity.setDeltaMovement(entity.getDeltaMovement().x, strength, entity.getDeltaMovement().z);
                
                // Let the client know about the motion change
                entity.hurtMarked = true;
            }
        }
    }
    
    private void swapWithRandomEntity(World world, PlayerEntity player) {
        double radius = 10.0D;
        List<Entity> entities = world.getEntities(player, player.getBoundingBox().inflate(radius));
        
        // Filter for living entities that aren't the player
        List<Entity> validEntities = entities.stream()
                .filter(e -> e instanceof LivingEntity && e != player)
                .collect(java.util.stream.Collectors.toList());
        
        if (!validEntities.isEmpty()) {
            // Pick a random entity to swap with
            Entity target = validEntities.get(world.random.nextInt(validEntities.size()));
            
            // Store positions
            Vector3d playerPos = player.position();
            Vector3d targetPos = target.position();
            
            // Swap positions
            player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
            target.teleportTo(playerPos.x, playerPos.y, playerPos.z);
            
            // Update motion to zero to prevent falling
            player.setDeltaMovement(Vector3d.ZERO);
            target.setDeltaMovement(Vector3d.ZERO);
        }
    }
    
    private void transformBlocks(World world, PlayerEntity player) {
        // Get the block the player is looking at
        BlockRayTraceResult rayTrace = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
        
        if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
            // Choose a random block to transform into
            Random rand = world.random;
            int blockChoice = rand.nextInt(4);
            
            // The block to replace with
            net.minecraft.block.BlockState newState;
            
            switch (blockChoice) {
                case 0:
                    // Replace with TNT
                    newState = net.minecraft.block.Blocks.TNT.defaultBlockState();
                    break;
                case 1:
                    // Replace with slime
                    newState = net.minecraft.block.Blocks.SLIME_BLOCK.defaultBlockState();
                    break;
                case 2:
                    // Replace with ice
                    newState = net.minecraft.block.Blocks.ICE.defaultBlockState();
                    break;
                case 3:
                    // Replace with cake
                    newState = net.minecraft.block.Blocks.CAKE.defaultBlockState();
                    break;
                default:
                    newState = net.minecraft.block.Blocks.AIR.defaultBlockState();
            }
            
            // Set the new block
            world.setBlock(rayTrace.getBlockPos(), newState, 3);
        }
    }
    
    private void createFakeExplosion(World world, PlayerEntity player) {
        // Create particle explosion without actual damage
        Vector3d pos = player.getEyePosition(1.0F)
                .add(player.getViewVector(1.0F).scale(3.0D));
        
        // Send an explosion packet to clients
        world.explode(player, pos.x, pos.y, pos.z, 2.0F, false, 
                net.minecraft.world.Explosion.Mode.NONE);
    }
    
    private void randomTeleport(World world, PlayerEntity player) {
        // Generate random offset
        double distance = 10.0D + world.random.nextDouble() * 10.0D;
        double angle = world.random.nextDouble() * Math.PI * 2.0D;
        
        double x = player.getX() + Math.cos(angle) * distance;
        double z = player.getZ() + Math.sin(angle) * distance;
        
        // Find a safe Y position (from current Y down to ground)
        double y = player.getY();
        boolean foundSafeSpot = false;
        
        for (int i = 0; i < 20 && y > 0; i++) {
            if (world.getBlockState(new net.minecraft.util.math.BlockPos(x, y - 1, z)).isCollisionShapeFullBlock(world, new net.minecraft.util.math.BlockPos(x, y - 1, z)) &&
                world.getBlockState(new net.minecraft.util.math.BlockPos(x, y, z)).isAir() &&
                world.getBlockState(new net.minecraft.util.math.BlockPos(x, y + 1, z)).isAir()) {
                foundSafeSpot = true;
                break;
            }
            y--;
        }
        
        // Teleport if safe spot was found
        if (foundSafeSpot) {
            player.teleportTo(x, y, z);
            player.fallDistance = 0.0F;
        }
    }
    
    // Helper method to get what the player is looking at
    protected static BlockRayTraceResult getPlayerPOVHitResult(World world, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        float pitch = player.xRot;
        float yaw = player.yRot;
        Vector3d eyePos = player.getEyePosition(1.0F);
        float cosYaw = (float) Math.cos(-yaw * 0.017453292F - (float) Math.PI);
        float sinYaw = (float) Math.sin(-yaw * 0.017453292F - (float) Math.PI);
        float cosPitch = (float) -Math.cos(-pitch * 0.017453292F);
        float sinPitch = (float) Math.sin(-pitch * 0.017453292F);
        float xDirection = sinYaw * cosPitch;
        float zDirection = cosYaw * cosPitch;
        Vector3d endPos = eyePos.add((double) xDirection * 5.0D, (double) sinPitch * 5.0D, (double) zDirection * 5.0D);
        return world.clip(new RayTraceContext(eyePos, endPos, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }
}