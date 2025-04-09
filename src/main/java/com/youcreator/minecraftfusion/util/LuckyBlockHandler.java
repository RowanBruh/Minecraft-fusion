package com.youcreator.minecraftfusion.util;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.config.ModConfig;
import com.youcreator.minecraftfusion.init.ModEntities;
import com.youcreator.minecraftfusion.init.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Handles Lucky Block outcomes
 */
public class LuckyBlockHandler {
    private static final List<BiConsumer<World, BlockPos>> GOOD_OUTCOMES = new ArrayList<>();
    private static final List<BiConsumer<World, BlockPos>> BAD_OUTCOMES = new ArrayList<>();
    private static final List<BiConsumer<World, BlockPos>> NEUTRAL_OUTCOMES = new ArrayList<>();
    
    private static boolean initialized = false;
    
    /**
     * Initialize all possible outcomes
     */
    public static void initOutcomes() {
        if (initialized) return;
        
        // Good outcomes
        GOOD_OUTCOMES.add((world, pos) -> {
            // Diamond reward
            spawnItemStack(world, pos, new ItemStack(Items.DIAMOND, 3 + world.rand.nextInt(5)));
            spawnParticlesAndSound(world, pos, true);
        });
        
        GOOD_OUTCOMES.add((world, pos) -> {
            // Super Sword reward
            spawnItemStack(world, pos, new ItemStack(ModItems.SUPER_SWORD.get()));
            spawnParticlesAndSound(world, pos, true);
        });
        
        GOOD_OUTCOMES.add((world, pos) -> {
            // Lightning Bow reward
            spawnItemStack(world, pos, new ItemStack(ModItems.LIGHTNING_BOW.get()));
            spawnItemStack(world, pos, new ItemStack(Items.ARROW, 16));
            spawnParticlesAndSound(world, pos, true);
        });
        
        GOOD_OUTCOMES.add((world, pos) -> {
            // Invisibility Clock reward
            spawnItemStack(world, pos, new ItemStack(ModItems.INVISIBILITY_CLOCK.get()));
            spawnParticlesAndSound(world, pos, true);
        });
        
        GOOD_OUTCOMES.add((world, pos) -> {
            // Gold and emerald reward
            spawnItemStack(world, pos, new ItemStack(Items.GOLD_INGOT, 5 + world.rand.nextInt(6)));
            spawnItemStack(world, pos, new ItemStack(Items.EMERALD, 2 + world.rand.nextInt(4)));
            spawnParticlesAndSound(world, pos, true);
        });
        
        GOOD_OUTCOMES.add((world, pos) -> {
            // Experience reward
            for (int i = 0; i < 5; i++) {
                double xOffset = world.rand.nextGaussian() * 0.5;
                double yOffset = world.rand.nextGaussian() * 0.5;
                double zOffset = world.rand.nextGaussian() * 0.5;
                BlockPos spawnPos = pos.add(xOffset, yOffset, zOffset);
                net.minecraft.entity.item.ExperienceOrbEntity orb = new net.minecraft.entity.item.ExperienceOrbEntity(
                        world, spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5, 10 + world.rand.nextInt(20));
                world.addEntity(orb);
            }
            spawnParticlesAndSound(world, pos, true);
        });
        
        GOOD_OUTCOMES.add((world, pos) -> {
            // Potion effects
            PlayerEntity nearestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, true);
            if (nearestPlayer != null) {
                nearestPlayer.addPotionEffect(new EffectInstance(Effects.REGENERATION, 600, 1));
                nearestPlayer.addPotionEffect(new EffectInstance(Effects.STRENGTH, 600, 1));
                nearestPlayer.addPotionEffect(new EffectInstance(Effects.SPEED, 600, 1));
                nearestPlayer.sendMessage(
                        new StringTextComponent(TextFormatting.GREEN + "You feel empowered!"),
                        nearestPlayer.getUniqueID());
            }
            spawnParticlesAndSound(world, pos, true);
        });
        
        // Bad outcomes
        BAD_OUTCOMES.add((world, pos) -> {
            // TNT trap
            for (int i = 0; i < 3; i++) {
                double xOffset = world.rand.nextGaussian() * 0.5;
                double yOffset = world.rand.nextGaussian() * 0.5 + 0.5; // Spawn slightly above
                double zOffset = world.rand.nextGaussian() * 0.5;
                BlockPos spawnPos = pos.add(xOffset, yOffset, zOffset);
                TNTEntity tnt = new TNTEntity(world, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), null);
                tnt.setFuse(20 + world.rand.nextInt(20)); // 1-2 second fuse
                world.addEntity(tnt);
            }
            spawnParticlesAndSound(world, pos, false);
        });
        
        BAD_OUTCOMES.add((world, pos) -> {
            // Zombie trap
            for (int i = 0; i < 4; i++) {
                double xOffset = world.rand.nextGaussian() * 1.0;
                double yOffset = 0.5;
                double zOffset = world.rand.nextGaussian() * 1.0;
                BlockPos spawnPos = pos.add(xOffset, yOffset, zOffset);
                ZombieEntity zombie = new ZombieEntity(world);
                zombie.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                world.addEntity(zombie);
            }
            spawnParticlesAndSound(world, pos, false);
        });
        
        BAD_OUTCOMES.add((world, pos) -> {
            // Mutant Zombie trap
            double xOffset = world.rand.nextGaussian() * 0.5;
            double yOffset = 0.5;
            double zOffset = world.rand.nextGaussian() * 0.5;
            BlockPos spawnPos = pos.add(xOffset, yOffset, zOffset);
            Entity mutantZombie = ModEntities.MUTANT_ZOMBIE.get().create(world);
            mutantZombie.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
            world.addEntity(mutantZombie);
            spawnParticlesAndSound(world, pos, false);
        });
        
        BAD_OUTCOMES.add((world, pos) -> {
            // Trap pit
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos blockPos = pos.add(x, -1, z);
                    world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                    
                    // Make the pit deeper
                    for (int y = 2; y <= 5; y++) {
                        blockPos = pos.add(x, -y, z);
                        world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
                    }
                    
                    // Add lava at the bottom
                    if (world.rand.nextBoolean()) {
                        blockPos = pos.add(x, -6, z);
                        world.setBlockState(blockPos, Blocks.LAVA.getDefaultState());
                    }
                }
            }
            spawnParticlesAndSound(world, pos, false);
        });
        
        BAD_OUTCOMES.add((world, pos) -> {
            // Lightning strike
            LightningBoltEntity lightning = new LightningBoltEntity(EntityType.LIGHTNING_BOLT, world);
            lightning.setPosition(pos.getX(), pos.getY(), pos.getZ());
            world.addEntity(lightning);
            spawnParticlesAndSound(world, pos, false);
        });
        
        BAD_OUTCOMES.add((world, pos) -> {
            // Potion effects
            PlayerEntity nearestPlayer = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 10, true);
            if (nearestPlayer != null) {
                nearestPlayer.addPotionEffect(new EffectInstance(Effects.POISON, 200, 0));
                nearestPlayer.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 200, 1));
                nearestPlayer.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 200, 0));
                nearestPlayer.sendMessage(
                        new StringTextComponent(TextFormatting.RED + "You feel weakened!"),
                        nearestPlayer.getUniqueID());
            }
            spawnParticlesAndSound(world, pos, false);
        });
        
        // Neutral outcomes
        NEUTRAL_OUTCOMES.add((world, pos) -> {
            // Chicken explosion
            for (int i = 0; i < 10; i++) {
                double xOffset = world.rand.nextGaussian() * 2.0;
                double yOffset = 0.5 + world.rand.nextGaussian() * 0.5;
                double zOffset = world.rand.nextGaussian() * 2.0;
                BlockPos spawnPos = pos.add(xOffset, yOffset, zOffset);
                ChickenEntity chicken = new ChickenEntity(world);
                chicken.setPosition(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                world.addEntity(chicken);
            }
            
            world.playSound(null, pos, SoundEvents.ENTITY_CHICKEN_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        });
        
        NEUTRAL_OUTCOMES.add((world, pos) -> {
            // Random blocks
            for (int x = -2; x <= 2; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -2; z <= 2; z++) {
                        // Only replace air blocks to avoid destroying terrain completely
                        BlockPos blockPos = pos.add(x, y, z);
                        if (world.isAirBlock(blockPos) && world.rand.nextInt(4) == 0) {
                            world.setBlockState(blockPos, getRandomBlock(world.rand));
                        }
                    }
                }
            }
            
            world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        });
        
        NEUTRAL_OUTCOMES.add((world, pos) -> {
            // Random ender crystal
            EnderCrystalEntity crystal = new EnderCrystalEntity(world, 
                    pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5);
            world.addEntity(crystal);
            
            world.playSound(null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        });
        
        NEUTRAL_OUTCOMES.add((world, pos) -> {
            // Creeper that doesn't explode
            CreeperEntity creeper = new CreeperEntity(world);
            creeper.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            
            // Set custom name to indicate it's friendly
            creeper.setCustomName(new StringTextComponent(TextFormatting.GREEN + "Friendly Creeper"));
            creeper.setCustomNameVisible(true);
            
            world.addEntity(creeper);
            world.playSound(null, pos, SoundEvents.ENTITY_CREEPER_HURT, SoundCategory.HOSTILE, 1.0F, 1.0F);
        });
        
        initialized = true;
        MinecraftFusion.LOGGER.info("Initialized Lucky Block outcomes");
    }
    
    /**
     * Process a lucky block outcome for a player
     */
    public static void processLuckyBlockOutcome(World world, BlockPos pos, PlayerEntity player) {
        // Get the number of possible outcomes from config
        int maxOutcomes = ModConfig.LUCKY_BLOCK_OUTCOMES.get();
        
        // Determine the outcome category (good, bad, or neutral)
        Random random = world.rand;
        int outcomeType = random.nextInt(10);
        
        if (outcomeType < 4) {
            // 40% chance of good outcome
            int index = random.nextInt(GOOD_OUTCOMES.size());
            GOOD_OUTCOMES.get(index).accept(world, pos);
            
            player.sendMessage(
                    new StringTextComponent(TextFormatting.GREEN + "Lucky!"),
                    player.getUniqueID());
            
        } else if (outcomeType < 8) {
            // 40% chance of bad outcome
            int index = random.nextInt(BAD_OUTCOMES.size());
            BAD_OUTCOMES.get(index).accept(world, pos);
            
            player.sendMessage(
                    new StringTextComponent(TextFormatting.RED + "Unlucky!"),
                    player.getUniqueID());
            
        } else {
            // 20% chance of neutral outcome
            int index = random.nextInt(NEUTRAL_OUTCOMES.size());
            NEUTRAL_OUTCOMES.get(index).accept(world, pos);
            
            player.sendMessage(
                    new StringTextComponent(TextFormatting.YELLOW + "Interesting..."),
                    player.getUniqueID());
        }
    }
    
    /**
     * Helper method to spawn an item stack in the world
     */
    private static void spawnItemStack(World world, BlockPos pos, ItemStack stack) {
        double xOffset = world.rand.nextFloat() * 0.5 + 0.25;
        double yOffset = world.rand.nextFloat() * 0.5 + 0.25;
        double zOffset = world.rand.nextFloat() * 0.5 + 0.25;
        
        ItemEntity itemEntity = new ItemEntity(world, 
                pos.getX() + xOffset, 
                pos.getY() + yOffset, 
                pos.getZ() + zOffset, 
                stack);
        
        itemEntity.setDefaultPickupDelay();
        world.addEntity(itemEntity);
    }
    
    /**
     * Helper method to spawn particles and play sound
     */
    private static void spawnParticlesAndSound(World world, BlockPos pos, boolean isGood) {
        // TODO: Add particle effects when particles are implemented
        
        // Play sound
        if (isGood) {
            world.playSound(null, pos, SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.BLOCKS, 1.0F, 1.0F);
        } else {
            world.playSound(null, pos, SoundEvents.ENTITY_WITCH_CELEBRATE, SoundCategory.BLOCKS, 1.0F, 0.5F);
        }
    }
    
    /**
     * Get a random block for the random blocks outcome
     */
    private static net.minecraft.block.BlockState getRandomBlock(Random random) {
        int block = random.nextInt(10);
        
        switch (block) {
            case 0:
                return Blocks.STONE.getDefaultState();
            case 1:
                return Blocks.DIRT.getDefaultState();
            case 2:
                return Blocks.COBBLESTONE.getDefaultState();
            case 3:
                return Blocks.SAND.getDefaultState();
            case 4:
                return Blocks.GRAVEL.getDefaultState();
            case 5:
                return Blocks.GOLD_BLOCK.getDefaultState();
            case 6:
                return Blocks.IRON_BLOCK.getDefaultState();
            case 7:
                return Blocks.COAL_BLOCK.getDefaultState();
            case 8:
                return Blocks.GLASS.getDefaultState();
            case 9:
                return Blocks.OAK_PLANKS.getDefaultState();
            default:
                return Blocks.STONE.getDefaultState();
        }
    }
}
