package com.youcreator.minecraftfusion.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class TeleportRod extends Item {

    public TeleportRod(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Raycast to find where the player is looking
        BlockRayTraceResult rayTraceResult = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
        
        if (rayTraceResult.getType() == RayTraceResult.Type.BLOCK) {
            if (!world.isClientSide) {
                // Calculate teleport position (on top of the block they're looking at)
                Vector3d hitPos = rayTraceResult.getLocation();
                
                // Adjust position to be on top of the block and centered
                double tpX = Math.floor(hitPos.x) + 0.5;
                double tpY = Math.floor(hitPos.y) + 1.0; // On top of the block
                double tpZ = Math.floor(hitPos.z) + 0.5;
                
                // Check if there's enough room to teleport (2 blocks high)
                if (world.getBlockState(new net.minecraft.util.math.BlockPos(tpX, tpY, tpZ)).isAir() && 
                    world.getBlockState(new net.minecraft.util.math.BlockPos(tpX, tpY + 1, tpZ)).isAir()) {
                    
                    // Teleport the player
                    player.teleportTo(tpX, tpY, tpZ);
                    player.fallDistance = 0.0F;
                    
                    // Play sound and spawn particles
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), 
                            SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    
                    // Add cooldown to prevent spam
                    player.getCooldowns().addCooldown(this, 100);
                    
                    // Damage the item
                    if (stack.hurt(1, world.random, null)) {
                        stack.shrink(1);
                        player.broadcastBreakEvent(hand);
                    }
                    
                    return ActionResult.success(stack);
                }
            }
        }
        
        return ActionResult.fail(stack);
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
        Vector3d endPos = eyePos.add((double) xDirection * 30.0D, (double) sinPitch * 30.0D, (double) zDirection * 30.0D);
        return world.clip(new RayTraceContext(eyePos, endPos, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
    }
}