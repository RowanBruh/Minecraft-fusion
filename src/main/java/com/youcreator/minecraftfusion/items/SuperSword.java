package com.youcreator.minecraftfusion.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SuperSword extends SwordItem {

    public SuperSword(Item.Properties properties) {
        super(new SuperSwordTier(), 8, -2.4F, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Apply additional effects when hitting enemies
        if (!target.level.isClientSide) {
            // 20% chance to set target on fire
            if (target.level.random.nextFloat() < 0.2F) {
                target.setSecondsOnFire(5);
            }
            
            // 10% chance to apply weakness
            if (target.level.random.nextFloat() < 0.1F) {
                target.addEffect(new EffectInstance(Effects.WEAKNESS, 100, 1));
            }
        }
        
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Apply speed boost when used (right-click)
        if (!world.isClientSide) {
            player.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 200, 1));
            player.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 100, 0));
            
            // Add cooldown to prevent spam
            player.getCooldowns().addCooldown(this, 400);
            
            // Play sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.PLAYER_LEVELUP, SoundCategory.PLAYERS, 0.5F, 
                    0.4F / (random.nextFloat() * 0.4F + 0.8F));
            
            // Damage the item when used
            if (stack.hurt(1, world.random, null)) {
                stack.shrink(1);
            }
        }
        
        return ActionResult.sidedSuccess(stack, world.isClientSide);
    }
    
    // Custom tier for our super sword
    private static class SuperSwordTier implements IItemTier {
        @Override
        public int getUses() {
            return 1500;
        }

        @Override
        public float getSpeed() {
            return 10.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 8.0F;
        }

        @Override
        public int getLevel() {
            return 4;
        }

        @Override
        public int getEnchantmentValue() {
            return 22;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.of(net.minecraft.item.Items.DIAMOND);
        }
    }
}