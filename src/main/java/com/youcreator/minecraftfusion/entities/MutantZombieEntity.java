package com.youcreator.minecraftfusion.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

/**
 * Mutant Zombie - Inspired by Bionic Skiddzie's zombie challenges
 * Features: Stronger, faster, and has special attacks
 */
public class MutantZombieEntity extends ZombieEntity {

    private int attackCooldown = 0;
    
    public MutantZombieEntity(EntityType<? extends ZombieEntity> type, World worldIn) {
        super(type, worldIn);
        this.experienceValue = 20; // More XP
    }
    
    public static AttributeModifierMap.MutableAttribute getAttributeModifiers() {
        return ZombieEntity.func_234342_eQ_()
                .createMutableAttribute(Attributes.MAX_HEALTH, 50.0D) // More health
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.3D) // Faster
                .createMutableAttribute(Attributes.ATTACK_DAMAGE, 8.0D) // Stronger
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 35.0D) // Sees farther
                .createMutableAttribute(Attributes.ARMOR, 4.0D); // Some armor
    }
    
    @Override
    protected void registerGoals() {
        super.registerGoals();
        
        // Clear existing goals and add our own
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F)); // Jump attack
        this.goalSelector.addGoal(4, new MoveThroughVillageGoal(this, 1.0D, false, 4, () -> false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(7, new LookRandomlyGoal(this));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }
    
    @Override
    public void livingTick() {
        super.livingTick();
        
        // Decrease attack cooldown
        if (attackCooldown > 0) {
            attackCooldown--;
        }
        
        // Handle special attack
        LivingEntity target = this.getAttackTarget();
        if (target != null && !world.isRemote) {
            // If the target is nearby and cooldown is ready, perform special attack
            double distance = this.getDistanceSq(target);
            if (distance < 9.0 && attackCooldown <= 0) {
                performSpecialAttack(target);
                attackCooldown = 100; // 5 seconds cooldown
            }
        }
        
        // Spawn particles
        if (world.isRemote && world.rand.nextInt(10) == 0) {
            world.addParticle(
                    ParticleTypes.ENTITY_EFFECT,
                    this.getPosX() + (world.rand.nextDouble() - 0.5) * this.getWidth(),
                    this.getPosY() + world.rand.nextDouble() * this.getHeight(),
                    this.getPosZ() + (world.rand.nextDouble() - 0.5) * this.getWidth(),
                    0.2, 0.8, 0.2
            );
        }
    }
    
    /**
     * Perform a special attack on the target
     */
    private void performSpecialAttack(LivingEntity target) {
        // Decide which attack to use
        int attackType = world.rand.nextInt(3);
        
        switch (attackType) {
            case 0:
                // Poison attack
                target.addPotionEffect(new EffectInstance(Effects.POISON, 100, 0));
                break;
            case 1:
                // Hunger attack
                target.addPotionEffect(new EffectInstance(Effects.HUNGER, 200, 1));
                break;
            case 2:
                // Ground pound attack
                // Damage and knock back nearby entities
                for (LivingEntity entity : world.getEntitiesWithinAABB(
                        LivingEntity.class, 
                        this.getBoundingBox().grow(4.0D),
                        e -> e != this)) {
                    entity.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);
                    
                    // Calculate knockback direction
                    double dx = entity.getPosX() - this.getPosX();
                    double dz = entity.getPosZ() - this.getPosZ();
                    double strength = 0.5;
                    
                    entity.setMotion(entity.getMotion().add(
                            dx * strength, 
                            0.4, 
                            dz * strength
                    ));
                }
                
                // Play sound
                this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), 
                        SoundEvents.ENTITY_GENERIC_EXPLODE, 
                        this.getSoundCategory(), 
                        1.0F, 1.0F);
                break;
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }
    
    @Override
    protected float getSoundVolume() {
        return 1.5F; // Louder
    }
}
