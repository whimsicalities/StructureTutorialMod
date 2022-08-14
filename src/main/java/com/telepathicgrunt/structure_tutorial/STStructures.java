package com.telepathicgrunt.structure_tutorial;

import com.telepathicgrunt.structure_tutorial.structures.SkyStructures;
import com.telepathicgrunt.structure_tutorial.structures.StrangeCactus;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class STStructures {

    public static StructureType<SkyStructures> SKY_STRUCTURES;
    public static StructureType<StrangeCactus> STRANGE_CACTUS;

    /**
     * Registers the structure itself and sets what its path is. In this case, the
     * structure will have the Identifier of structure_tutorial:sky_structures.
     *
     * It is always a good idea to register your Structures so that other mods and datapacks can
     * use them too directly from the registries. It's great for mod/datapacks compatibility.
     */
    public static void registerStructureFeatures() {
        SKY_STRUCTURES = Registry.register(Registry.STRUCTURE_TYPE, new Identifier(StructureTutorialMain.MODID, "sky_structures"), () -> SkyStructures.CODEC);
        STRANGE_CACTUS = Registry.register(Registry.STRUCTURE_TYPE, new Identifier(StructureTutorialMain.MODID, "strange_cactus"), () -> StrangeCactus.CODEC);
    }
}
