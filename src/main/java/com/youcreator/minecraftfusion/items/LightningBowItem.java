package com.youcreator.minecraftfusion.items;

import com.youcreator.minecraftfusion.config.ModConfig;
import com.youcreator.minecraftfusion.entities.ExplosiveArrowEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.client.util.ITooltipFlag;

/**
 * Lightning Bow - Inspired by Doni Bobes' special weapons
 * Features: Shoots explosive arrows and summons lightning strikes
 */
public class LightningBowItem extends BowItem {
    private static final Predicate<ItemStack> ARROWS = item -> item.getItem() instanceof ArrowItem;
    private int cooldown = 0;

    public LightningBowItem(Properties builder) {
        super(builder);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity playerentity = (PlayerEntity)entityLiving;
            boolean hasInfinity = playerentity.abilities.isCreativeMode || 
                    EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack arrowStack = playerentity.findAmmo(stack);

            int useTime = this.getUseDuration(stack) - timeLeft;
            if (useTime < 0) return;

            if (!arrowStack.isEmpty() || hasInfinity) {
                if (arrowStack.isEmpty()) {
                    arrowStack = new ItemStack(Items.ARROW);
                }

                float power = getPowerForTime(useTime);
                if (power >= 0.1F) {
                    boolean isArrowInfinite = playerentity.abilities.isCreativeMode || 
                            (arrowStack.getItem() instanceof ArrowItem && ((ArrowItem)arrowStack.getItem()).isInfinite(arrowStack, stack, playerentity));

                    if (!worldIn.isRemote) {
                        // Create explosive arrow instead of normal arrow
                        ExplosiveArrowEntity arrow = new ExplosiveArrowEntity(worldIn, playerentity);
                        arrow.setDamage(power * 2.5F);
                        arrow.setKnockbackStrength(1);

                        // Apply enchantments
                        int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
                        if (powerLevel > 0) {
                            arrow.setDamage(arrow.getDamage() + (double)powerLevel * 0.5D + 0.5D);
                        }

                        int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
                        if (punchLevel > 0) {
                            arrow.setKnockbackStrength(punchLevel);
                        }

                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
                            arrow.setFire(100);
                        }

                        // Damage the bow
                        stack.damageItem(1, playerentity, (p) -> p.sendBreakAnimation(playerentity.getActiveHand()));

                        // Set arrow as non-pickup
                        arrow.pickupStatus = AbstractArrowEntity.PickupStatus.DISALLOWED;

                        // Shoot with velocity
                        arrow.shoot(playerentity, playerentity.rotationPitch, playerentity.rotationYaw, 0.0F, power * 3.0F, 1.0F);
                        worldIn.addEntity(arrow);

                        // Strike lightning if cooldown is ready
                        if (cooldown <= 0 && power >= 0.9F) {
                            worldIn.playSound(null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), 
                                    SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 1.0F, 1.0F);
                            
                            // We'll strike lightning where the player is looking
                            cooldown = ModConfig.LIGHTNING_BOW_COOLDOWN.get();
                        }
                    }

                    worldIn.playSound(null, playerentity.getPosX(), playerentity.getPosY(), playerentity.getPosZ(), 
                            SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);

                    if (!isArrowInfinite && !playerentity.abilities.isCreativeMode) {
                        arrowStack.shrink(1);
                        if (arrowStack.isEmpty()) {
                            playerentity.inventory.deleteStack(arrowStack);
                        }
                    }

                    playerentity.addStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, net.minecraft.entity.Entity entityIn, int itemSlot, boolean isSelected) {
        if (cooldown > 0) {
            cooldown--;
        }
    }

    private static float getPowerForTime(int time) {
        float f = (float)time / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(TextFormatting.BLUE + "Shoots explosive arrows"));
        tooltip.add(new StringTextComponent(TextFormatting.YELLOW + "Fully charged shots will strike lightning"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public Predicate<ItemStack> getInventoryAmmoPredicate() {
        return ARROWS;
    }
}
