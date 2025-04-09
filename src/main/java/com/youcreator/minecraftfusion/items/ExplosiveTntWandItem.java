package com.youcreator.minecraftfusion.items;

import com.youcreator.minecraftfusion.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;

/**
 * Explosive TNT Wand - Inspired by Doni Bobes' explosive pranks
 * Features: Creates explosions at a distance
 */
public class ExplosiveTntWandItem extends Item {
    
    private static final int COOLDOWN_TICKS = 40; // 2 second cooldown
    private static final int MAX_RANGE = 50; // Maximum range in blocks
    
    public ExplosiveTntWandItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Don't allow explosions if the player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultFail(stack);
        }
        
        // Perform a ray trace to find where the player is looking
        Vector3d eyePosition = player.getEyePosition(1.0F);
        Vector3d lookVector = player.getLook(1.0F);
        Vector3d targetVector = eyePosition.add(lookVector.x * MAX_RANGE, lookVector.y * MAX_RANGE, lookVector.z * MAX_RANGE);
        
        // Create the ray trace
        RayTraceContext rayTraceContext = new RayTraceContext(
                eyePosition,
                targetVector,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                player
        );
        
        BlockRayTraceResult rayTraceResult = world.rayTraceBlocks(rayTraceContext);
        
        // If we hit something, create an explosion at that position
        if (rayTraceResult.getType() != RayTraceResult.Type.MISS) {
            BlockPos hitPos = rayTraceResult.getPos();
            Vector3d hitVec = rayTraceResult.getHitVec();
            
            if (!world.isRemote) {
                // Get explosion power from config
                float explosionPower = ModConfig.EXPLOSIVE_TNT_POWER.get();
                
                // Create the explosion
                world.createExplosion(null, hitVec.x, hitVec.y, hitVec.z, 
                        explosionPower, Explosion.Mode.BREAK);
            }
            
            // Play explosion sound
            world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                    SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,
                    1.0F, 1.0F);
            
            // Set cooldown
            player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
            
            // Damage the item
            stack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
            
            return ActionResult.resultConsume(stack);
        }
        
        return ActionResult.resultPass(stack);
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(TextFormatting.RED + "Right-click to create an explosion where you're looking"));
        tooltip.add(new StringTextComponent(TextFormatting.GOLD + "Explosion Power: " + ModConfig.EXPLOSIVE_TNT_POWER.get()));
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + "Maximum Range: " + MAX_RANGE + " blocks"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
