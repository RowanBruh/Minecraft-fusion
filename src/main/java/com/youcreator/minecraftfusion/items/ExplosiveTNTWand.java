package com.youcreator.minecraftfusion.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ExplosiveTNTWand extends Item {

    public ExplosiveTNTWand(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (!world.isClientSide) {
            // Create a TNT entity
            TNTEntity tnt = new TNTEntity(
                    world, 
                    player.getX(), 
                    player.getY() + 1.0D, 
                    player.getZ(), 
                    player);
            
            // Set the velocity based on where the player is looking
            Vector3d lookVec = player.getViewVector(1.0F);
            tnt.setDeltaMovement(
                    lookVec.x * 1.5D, 
                    lookVec.y * 1.5D, 
                    lookVec.z * 1.5D);
            
            // Set a short fuse time
            tnt.setFuse(20); // 1 second fuse
            
            // Add the TNT to the world
            world.addFreshEntity(tnt);
            
            // Play sound effect
            world.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            
            // Add cooldown to prevent spam
            player.getCooldowns().addCooldown(this, 40);
            
            // Damage the item
            if (stack.hurt(1, world.random, null)) {
                stack.shrink(1);
                player.broadcastBreakEvent(hand);
            }
        }
        
        return ActionResult.sidedSuccess(stack, world.isClientSide);
    }
}