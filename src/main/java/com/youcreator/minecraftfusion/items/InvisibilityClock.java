package com.youcreator.minecraftfusion.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class InvisibilityClock extends Item {
    private static final String TAG_ACTIVE = "Active";
    private static final String TAG_ACTIVATION_TIME = "ActivationTime";
    private static final int DURATION = 200; // 10 seconds (20 ticks per second)
    
    public InvisibilityClock(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundNBT tag = stack.getOrCreateTag();
        
        // Toggle invisibility
        boolean isActive = tag.getBoolean(TAG_ACTIVE);
        if (!isActive) {
            // Activate invisibility
            tag.putBoolean(TAG_ACTIVE, true);
            tag.putLong(TAG_ACTIVATION_TIME, world.getGameTime());
            
            // Add invisibility effect to player
            player.addEffect(new EffectInstance(Effects.INVISIBILITY, DURATION, 0, false, false));
            
            // Play activation sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 2.0F);
            
            // Add cooldown
            player.getCooldowns().addCooldown(this, DURATION + 100); // Duration + 5 second cooldown
            
            if (!world.isClientSide) {
                // Damage the item
                if (stack.hurt(1, world.random, null)) {
                    stack.shrink(1);
                    player.broadcastBreakEvent(hand);
                }
            }
        }
        
        return ActionResult.sidedSuccess(stack, world.isClientSide);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean isSelected) {
        if (world.isClientSide && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            CompoundNBT tag = stack.getOrCreateTag();
            
            if (tag.getBoolean(TAG_ACTIVE)) {
                long activationTime = tag.getLong(TAG_ACTIVATION_TIME);
                long currentTime = world.getGameTime();
                
                // Check if effect has expired
                if (currentTime > activationTime + DURATION) {
                    tag.putBoolean(TAG_ACTIVE, false);
                    
                    // Play deactivation sound
                    world.playSound(player, player.getX(), player.getY(), player.getZ(), 
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5F, 0.5F);
                } else {
                    // Spawn particles around player while invisible
                    if (world.random.nextFloat() < 0.2F) {
                        world.addParticle(
                                ParticleTypes.PORTAL, 
                                player.getX() + (world.random.nextDouble() - 0.5D) * 0.5D, 
                                player.getY() + world.random.nextDouble() * 2.0D - 0.25D, 
                                player.getZ() + (world.random.nextDouble() - 0.5D) * 0.5D, 
                                0, 0, 0);
                    }
                }
            }
        }
    }
}