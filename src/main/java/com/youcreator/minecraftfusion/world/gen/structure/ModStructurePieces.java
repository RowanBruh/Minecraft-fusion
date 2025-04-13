package com.youcreator.minecraftfusion.world.gen.structure;

import com.youcreator.minecraftfusion.MinecraftFusion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MinecraftFusion.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModStructurePieces {
    // Register the structure piece types
    public static final IStructurePieceType CHALLENGE_TOWER = 
        registerPiece("challenge_tower", ChallengeTowerPiece::new);
    
    // Method to register a structure piece type
    private static IStructurePieceType registerPiece(String name, IStructurePieceType type) {
        return Registry.register(Registry.STRUCTURE_PIECE, new ResourceLocation(MinecraftFusion.MOD_ID, name), type);
    }
    
    // Event handler to register the structure piece types
    @SubscribeEvent
    public static void registerStructurePieces(RegistryEvent.Register<?> event) {
        MinecraftFusion.LOGGER.info("Registering structure pieces for Minecraft Fusion Mod");
        // No direct registration needed here, as piece types use built-in registry
    }
}