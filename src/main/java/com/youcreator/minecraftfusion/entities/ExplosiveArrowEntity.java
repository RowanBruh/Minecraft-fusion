package com.youcreator.minecraftfusion.entities;

import com.youcreator.minecraftfusion.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

/**
 * Explosive Arrow - Used by the Lightning Bow
 * Features: Creates explosions on impact
 */
public class ExplosiveArrowEntity extends AbstractArrowEntity {
    
    private float explosionPower = 2.0F;
    private boolean causesLightning = false;
    
    public ExplosiveArrowEntity(EntityType<? extends ExplosiveArrowEntity> type, World world) {
        super(type, world);
    }
    
    public ExplosiveArrowEntity(World world, LivingEntity shooter) {
        super(ModEntities.EXPLOSIVE_ARROW.get(), shooter, world);
        this.setDamage(2.0); // Base damage
    }
    
    public void setExplosionPower(float power) {
        this.explosionPower = power;
    }
    
    public void setCausesLightning(boolean causesLightning) {
        this.causesLightning = causesLightning;
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Spawn trail particles
        if (this.world.isRemote) {
            if (this.inGround) {
                if (this.timeInGround % 5 == 0) {
                    this.spawnParticles(1);
                }
            } else {
                this.spawnParticles(2);
            }
        }
    }
    
    private void spawnParticles(int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            this.world.addParticle(
                    ParticleTypes.FLAME,
                    this.getPosX() + (this.rand.nextDouble() - 0.5D) * 0.2D,
                    this.getPosY() + (this.rand.nextDouble() - 0.5D) * 0.2D,
                    this.getPosZ() + (this.rand.nextDouble() - 0.5D) * 0.2D,
                    0, 0, 0);
        }
    }
    
    @Override
    protected void onEntityHit(EntityRayTraceResult entityRayTraceResult) {
        super.onEntityHit(entityRayTraceResult);
        
        // Create explosion on hit if we're not in ground yet
        if (!this.inGround) {
            this.explode();
        }
    }
    
    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        
        // Create explosion when hitting anything
        if (!this.world.isRemote && !this.inGround) {
            this.explode();
            this.remove();
        }
    }
    
    private void explode() {
        // Create explosion
        this.world.createExplosion(
                this, 
                this.getPosX(), 
                this.getPosY(), 
                this.getPosZ(), 
                this.explosionPower, 
                this.isBurning(), 
                Explosion.Mode.BREAK);
        
        // Summon lightning if enabled
        if (this.causesLightning) {
            BlockPos strikePos = new BlockPos(this.getPosX(), this.getPosY(), this.getPosZ());
            net.minecraft.entity.effect.LightningBoltEntity lightning = 
                    new net.minecraft.entity.effect.LightningBoltEntity(
                            net.minecraft.entity.EntityType.LIGHTNING_BOLT, 
                            world);
            lightning.setPosition(strikePos.getX(), strikePos.getY(), strikePos.getZ());
            world.addEntity(lightning);
        }
    }
    
    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.ARROW);
    }
}
