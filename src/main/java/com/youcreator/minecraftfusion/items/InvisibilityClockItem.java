package com.youcreator.minecraftfusion.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;

/**
 * Invisibility Clock - Inspired by Bionic Skiddzie's hiding challenges
 * Features: Makes the player invisible for a short period
 */
public class InvisibilityClockItem extends Item {
    
    private static final int INVISIBILITY_DURATION = 200; // 10 seconds
    private static final int COOLDOWN_TICKS = 400; // 20 second cooldown
    
    public InvisibilityClockItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Don't allow activation if the player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultFail(stack);
        }
        
        // Apply invisibility effect
        player.addPotionEffect(new EffectInstance(Effects.INVISIBILITY, INVISIBILITY_DURATION, 0, false, false));
        
        // Also add speed for a short time
        player.addPotionEffect(new EffectInstance(Effects.SPEED, INVISIBILITY_DURATION / 2, 0));
        
        // Play activation sound
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS,
                0.5F, 1.0F);
        
        // Set cooldown
        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        
        // Damage the item
        stack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
        
        return ActionResult.resultConsume(stack);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int itemSlot, boolean isSelected) {
        if (entity instanceof PlayerEntity && isSelected && !world.isRemote) {
            PlayerEntity player = (PlayerEntity) entity;
            
            // Show particles around the player when holding the clock
            if (world.getGameTime() % 10 == 0) {
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS,
                        0.1F, 1.0F + (float)world.rand.nextGaussian() * 0.05F);
            }
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Right-click to become invisible for " + (INVISIBILITY_DURATION / 20) + " seconds"));
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + "Cooldown: " + (COOLDOWN_TICKS / 20) + " seconds"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
