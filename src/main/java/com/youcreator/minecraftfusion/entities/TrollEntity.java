package com.youcreator.minecraftfusion.entities;

import com.youcreator.minecraftfusion.init.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class TrollEntity extends MonsterEntity {

    private static final DataParameter<Boolean> IS_TROLLING = 
            EntityDataManager.defineId(TrollEntity.class, DataSerializers.BOOLEAN);

    public TrollEntity(EntityType<? extends MonsterEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new SwimGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 0.8D));
        
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return MonsterEntity.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_TROLLING, false);
    }

    @Override
    public void tick() {
        super.tick();
        
        // There's a small chance the troll will enter "trolling mode"
        if (!level.isClientSide && this.random.nextFloat() < 0.005F) {
            this.setTrolling(true);
        }
        
        // But it doesn't last long
        if (this.isTrolling() && this.random.nextFloat() < 0.1F) {
            this.setTrolling(false);
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.entity.Entity target) {
        boolean success = super.doHurtTarget(target);
        
        if (success && target instanceof LivingEntity) {
            // 20% chance to apply a trolling effect when attacking
            if (this.random.nextFloat() < 0.2F) {
                applyTrollingEffect((LivingEntity) target);
            }
        }
        
        return success;
    }

    private void applyTrollingEffect(LivingEntity target) {
        // Choose a random trolling effect
        int effect = this.random.nextInt(3);
        
        switch (effect) {
            case 0:
                // Jump boost (sudden launch)
                target.setDeltaMovement(target.getDeltaMovement().x, 1.0D, target.getDeltaMovement().z);
                break;
            case 1:
                // Spinning (random movement impulse)
                target.setDeltaMovement(
                        this.random.nextFloat() * 0.8F - 0.4F,
                        target.getDeltaMovement().y,
                        this.random.nextFloat() * 0.8F - 0.4F);
                break;
            case 2:
                // Confusion (let client handle via rendering)
                // Would need client-side rendering logic for full effect
                break;
        }
    }

    public boolean isTrolling() {
        return this.entityData.get(IS_TROLLING);
    }

    public void setTrolling(boolean trolling) {
        this.entityData.set(IS_TROLLING, trolling);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITCH_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WITCH_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITCH_DEATH;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        
        // Drop essence with increased chance based on looting
        if (this.random.nextFloat() < 0.3F + (looting * 0.1F)) {
            this.spawnAtLocation(new ItemStack(ModItems.TROLL_ESSENCE.get()));
        }
        
        // Very small chance to drop a troll stick
        if (this.random.nextFloat() < 0.05F + (looting * 0.01F)) {
            this.spawnAtLocation(new ItemStack(ModItems.TROLL_STICK.get()));
        }
    }
}