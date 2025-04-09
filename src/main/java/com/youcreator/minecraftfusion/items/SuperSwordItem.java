package com.youcreator.minecraftfusion.items;

import com.youcreator.minecraftfusion.MinecraftFusion;
import com.youcreator.minecraftfusion.config.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.StringTextComponent;

/**
 * Super Sword - A powerful sword inspired by Bionic Skiddzie's OP weapons videos
 * Features: High damage, sets enemies on fire, gives wielder strength when active
 */
public class SuperSwordItem extends SwordItem {
    
    public SuperSwordItem(Properties properties) {
        super(new SuperSwordTier(), 3, -2.4F, properties);
    }
    
    @Override
    public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Set target on fire for 5 seconds
        target.setFire(5);
        
        // Chance to apply weakness effect to target
        if (Math.random() < 0.3) {
            target.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 100, 1));
        }
        
        // Give attacker strength effect when hitting targets
        if (attacker instanceof PlayerEntity) {
            attacker.addPotionEffect(new EffectInstance(Effects.STRENGTH, 60, 1));
        }
        
        return super.hitEntity(stack, target, attacker);
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent(TextFormatting.RED + "Sets enemies on fire"));
        tooltip.add(new StringTextComponent(TextFormatting.GOLD + "Gives you strength when attacking"));
        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "30% chance to weaken enemies"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
    
    /**
     * Custom tier for the Super Sword
     */
    private static class SuperSwordTier implements IItemTier {
        @Override
        public int getMaxUses() {
            return 2000;
        }

        @Override
        public float getEfficiency() {
            return 8.0F;
        }

        @Override
        public float getAttackDamage() {
            return (float) ModConfig.SUPER_SWORD_DAMAGE.get();
        }

        @Override
        public int getHarvestLevel() {
            return 3;
        }

        @Override
        public int getEnchantability() {
            return 22;
        }

        @Override
        public Ingredient getRepairMaterial() {
            return Ingredient.fromItems(net.minecraft.item.Items.DIAMOND);
        }
    }
}
