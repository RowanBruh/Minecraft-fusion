package com.youcreator.minecraftfusion.items;

import com.youcreator.minecraftfusion.entities.ExplosiveArrowEntity;
import com.youcreator.minecraftfusion.init.ModEntities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class LightningBow extends BowItem {
    
    public LightningBow(Item.Properties properties) {
        super(properties);
    }
    
    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            boolean hasInfiniteArrows = player.abilities.instabuild || 
                    EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack arrowStack = player.getProjectile(stack);
            
            int useDuration = this.getUseDuration(stack) - timeLeft;
            if (useDuration < 0) return;
            
            if (!arrowStack.isEmpty() || hasInfiniteArrows) {
                float power = getPowerForTime(useDuration);
                
                if (power >= 0.1F) {
                    boolean hasArrow = player.abilities.instabuild || 
                            (arrowStack.getItem() instanceof ArrowItem && !arrowStack.isEmpty());
                    
                    if (!hasArrow) {
                        return;
                    }
                    
                    if (!world.isClientSide) {
                        // Create arrow entity
                        ArrowEntity arrowEntity = createArrow(world, arrowStack, player);
                        arrowEntity.shootFromRotation(player, player.xRot, player.yRot, 
                                0.0F, power * 3.0F, 1.0F);
                        
                        // Apply power and punch
                        if (power == 1.0F) {
                            arrowEntity.setCritArrow(true);
                        }
                        
                        int powerEnchant = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (powerEnchant > 0) {
                            arrowEntity.setBaseDamage(arrowEntity.getBaseDamage() + 
                                    (double) powerEnchant * 0.5D + 0.5D);
                        }
                        
                        int punchEnchant = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (punchEnchant > 0) {
                            arrowEntity.setKnockback(punchEnchant);
                        }
                        
                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            arrowEntity.setSecondsOnFire(100);
                        }
                        
                        // Damage the bow
                        stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(player.getUsedItemHand()));
                        
                        // Trace where the arrow will hit
                        Vector3d startPos = player.getEyePosition(1.0F);
                        Vector3d lookVec = player.getViewVector(1.0F);
                        Vector3d endPos = startPos.add(lookVec.x * 128, lookVec.y * 128, lookVec.z * 128);
                        
                        RayTraceResult rayTrace = world.clip(new RayTraceContext(
                                startPos, endPos,
                                RayTraceContext.BlockMode.COLLIDER,
                                RayTraceContext.FluidMode.NONE,
                                arrowEntity));
                        
                        if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {
                            // Spawn lightning at the hit location
                            BlockPos hitPos = ((BlockRayTraceResult)rayTrace).getBlockPos();
                            LightningBoltEntity lightningBolt = new LightningBoltEntity(
                                    world.getServer().getLevel(world.dimension()),
                                    hitPos.getX() + 0.5D, hitPos.getY(), hitPos.getZ() + 0.5D, 
                                    false);
                            world.addFreshEntity(lightningBolt);
                        }
                        
                        // Fire the arrow
                        world.addFreshEntity(arrowEntity);
                    }
                    
                    // Play sound and stats
                    world.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS,
                            1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + power * 0.5F);
                    
                    // Consume arrow if not in creative
                    if (!player.abilities.instabuild && !hasInfiniteArrows) {
                        arrowStack.shrink(1);
                        if (arrowStack.isEmpty()) {
                            player.inventory.removeItem(arrowStack);
                        }
                    }
                    
                    // Add stats
                    player.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }
    
    protected ArrowEntity createArrow(World world, ItemStack arrowStack, PlayerEntity player) {
        // Create our explosive arrow instead of a regular arrow
        ExplosiveArrowEntity explosiveArrow = new ExplosiveArrowEntity(world, player);
        explosiveArrow.setCausesLightning(true);
        explosiveArrow.setExplosionPower(1.0F);
        return explosiveArrow;
    }
}