package com.youcreator.minecraftfusion.items;

import com.youcreator.minecraftfusion.config.ModConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;

/**
 * Teleport Rod - Inspired by both YouTubers' challenge items
 * Features: Allows players to teleport to where they're looking
 */
public class TeleportRodItem extends Item {
    
    private static final int COOLDOWN_TICKS = 20; // 1 second cooldown
    
    public TeleportRodItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        
        // Don't allow teleporting if the player is on cooldown
        if (player.getCooldownTracker().hasCooldown(this)) {
            return ActionResult.resultFail(stack);
        }
        
        // Get the teleport range from config
        int range = ModConfig.TELEPORT_ROD_RANGE.get();
        
        // Perform a ray trace to find where the player is looking
        Vector3d eyePosition = player.getEyePosition(1.0F);
        Vector3d lookVector = player.getLook(1.0F);
        Vector3d targetVector = eyePosition.add(lookVector.x * range, lookVector.y * range, lookVector.z * range);
        
        // Create the ray trace
        RayTraceContext rayTraceContext = new RayTraceContext(
                eyePosition,
                targetVector,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                player
        );
        
        BlockRayTraceResult rayTraceResult = world.rayTraceBlocks(rayTraceContext);
        
        // If we hit something, teleport to just before the hit position
        if (rayTraceResult.getType() != RayTraceResult.Type.MISS) {
            BlockPos hitPos = rayTraceResult.getPos();
            Vector3d hitVec = rayTraceResult.getHitVec();
            
            // Find a safe position to teleport to
            Vector3d teleportPos = findSafePosition(world, hitVec, rayTraceResult);
            
            // If we found a safe position, teleport the player
            if (teleportPos != null) {
                // Spawn particles at old position
                for (int i = 0; i < 10; i++) {
                    world.addParticle(
                            ParticleTypes.PORTAL,
                            player.getPosX() + (world.rand.nextDouble() - 0.5) * 1.0,
                            player.getPosY() + world.rand.nextDouble() * 2.0,
                            player.getPosZ() + (world.rand.nextDouble() - 0.5) * 1.0,
                            0, 0, 0
                    );
                }
                
                // Teleport the player
                player.setPositionAndUpdate(teleportPos.x, teleportPos.y, teleportPos.z);
                
                // Spawn particles at new position
                for (int i = 0; i < 10; i++) {
                    world.addParticle(
                            ParticleTypes.PORTAL,
                            teleportPos.x + (world.rand.nextDouble() - 0.5) * 1.0,
                            teleportPos.y + world.rand.nextDouble() * 2.0,
                            teleportPos.z + (world.rand.nextDouble() - 0.5) * 1.0,
                            0, 0, 0
                    );
                }
                
                // Play teleport sound
                world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(),
                        SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS,
                        1.0F, 1.0F);
                
                // Set cooldown
                player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
                
                // Damage the item
                stack.damageItem(1, player, (p) -> p.sendBreakAnimation(hand));
                
                return ActionResult.resultConsume(stack);
            }
        }
        
        return ActionResult.resultPass(stack);
    }
    
    /**
     * Find a safe position to teleport to near the hit position
     */
    private Vector3d findSafePosition(World world, Vector3d hitVec, BlockRayTraceResult rayTraceResult) {
        // Move slightly back from the hit position based on the face we hit
        Vector3d adjustedPos = hitVec.subtract(
                rayTraceResult.getFace().getDirectionVec().getX() * 0.5,
                0,  // Don't adjust Y as we want to land on the block below
                rayTraceResult.getFace().getDirectionVec().getZ() * 0.5
        );
        
        // Make sure we're centered on the block
        adjustedPos = new Vector3d(
                Math.floor(adjustedPos.x) + 0.5,
                adjustedPos.y,
                Math.floor(adjustedPos.z) + 0.5
        );
        
        // Check if the position is safe (has 2 blocks of air above it)
        BlockPos pos = new BlockPos(adjustedPos);
        if (world.isAirBlock(pos) && world.isAirBlock(pos.up())) {
            // Return a position standing on the block below
            return new Vector3d(adjustedPos.x, Math.floor(adjustedPos.y), adjustedPos.z);
        } else if (world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2))) {
            // Or standing on this block if it's not air but has space above
            return new Vector3d(adjustedPos.x, Math.floor(adjustedPos.y) + 1, adjustedPos.z);
        }
        
        // Not a safe position
        return null;
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(TextFormatting.AQUA + "Right-click to teleport where you're looking"));
        tooltip.add(new StringTextComponent(TextFormatting.GRAY + "Range: " + ModConfig.TELEPORT_ROD_RANGE.get() + " blocks"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
