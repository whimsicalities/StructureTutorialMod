package com.telepathicgrunt.structuretutorial.world.features.structures;

import java.util.Random;
import java.util.function.Function;

import org.apache.logging.log4j.Level;

import com.mojang.datafixers.Dynamic;
import com.telepathicgrunt.structuretutorial.StructureTutorialMain;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class RunDownHouseStructure extends Structure<NoFeatureConfig> 
{
	public RunDownHouseStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> config) 
	{
		super(config);
	}

	/*
	 * Determines the valid chunks that this structure can spawn in.
	 * 
	 * This is using vanilla's default algorithm to determine chunks that this structure can spawn in.
	 */
	protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ) 
	{
		//this means Run Down Houses cannot be closer than 7 chunks or more than 12 chunks
		int maxDistance = 12;
		int minDistance = 7;
		
		int xTemp = x + maxDistance * spacingOffsetsX;
		int ztemp = z + maxDistance * spacingOffsetsZ;
		int xTemp2 = xTemp < 0 ? xTemp - maxDistance + 1 : xTemp;
		int zTemp2 = ztemp < 0 ? ztemp - maxDistance + 1 : ztemp;
		int validChunkX = xTemp2 / maxDistance;
		int validChunkZ = zTemp2 / maxDistance;
		
		
		((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), validChunkX, validChunkZ, this.getSeedModifier());
		validChunkX = validChunkX * maxDistance;
		validChunkZ = validChunkZ * maxDistance;
		validChunkX = validChunkX + random.nextInt(maxDistance - minDistance);
		validChunkZ = validChunkZ + random.nextInt(maxDistance - minDistance);
		
		return new ChunkPos(validChunkX, validChunkZ);
	}

	/*
	 * Seems to be unused but cannot be removed.
	 * Keep this as is.
	 */
	protected boolean isEnabledIn(IWorld worldIn) 
	{
		return worldIn.getWorldInfo().isMapFeaturesEnabled();
	}

	/*
	 * Structure name. What the player will see when doing /Locate command.
	 */
	public String getStructureName() 
	{
		return StructureTutorialMain.MODID + ":run_down_house";
	}

	/*
	 * This seems to be unused but cannot be removed.
	 * Just return 0 is all you need to do.
	 */
	public int getSize() 
	{
		return 0;
	}

	/*
	 * This is how the worldgen code will start the generation of our structure when it passes the checks.
	 */
	public Structure.IStartFactory getStartFactory() 
	{
		return RunDownHouseStructure.Start::new;
	}

	/*
	 * I believe this is used so that no two structure's spawning will be in perfect sync
	 * as long as they have different seed modifier.
	 */
	protected int getSeedModifier() 
	{
		return 123456789;
	}

	/*
	 * This is where all the checks will be done to determine if the structure can spawn here.
	 * 
	 * Notice how the biome is also passed in. While you could do manual checks on the biome to 
	 * see if you can spawn here, that is highly discouraged. Instead, you should do the biome check in 
	 * the FMLCommonSetupEvent event (setup method in StructureTutorialMain) and add your structure to the
	 * biome with .addFeature and .func_226711_a_ methods.
	 * 
	 * Instead, this method is best used for determining if the chunk position itself is valid, if
	 * certain other structures are too close or not, or some other restrictive condition. 
	 *
	 * For example, Pillager Outposts added a check to make sure it cannot spawn  
	 * within 10 chunk of a Village. (Bedrock Edition seems to not have the same check)
	 */
	public boolean func_225558_a_(BiomeManager p_225558_1_, ChunkGenerator<?> chunkGen, Random rand, int chunkPosX, int chunkPosZ, Biome biome) 
	{
		ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);
		
		//Checks to see if current chunk is valid to spawn in.
		if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z) 
		{
			//Checks if the biome can spawn this structure.
			if (chunkGen.hasStructure(biome, this)) 
			{
				return true;
			}
		}
		
		return false;
	}

	
	/*
	 * Handles calling up the structure's pieces class and height that structure will spawn at.
	 */
	public static class Start extends StructureStart 
	{
		public Start(Structure<?> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) 
		{
			super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
		}

		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) 
		{
			//Check out vanilla's WoodlandMansionStructure for how they offset the x and z
			//so that they get the y value of the land at the mansion's entrance, no matter
			//which direction the mansion is rotated.
			//
			//However, for most purposes, getting the y value of land with the default x and z is good enough.
			Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];

			//Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
			int x = (chunkX << 4) + 7;
			int z = (chunkZ << 4) + 7;
			
			//Finds the y value of the terrain at location.
			int surfaceY = generator.func_222531_c(x, z, Heightmap.Type.WORLD_SURFACE_WG);
			BlockPos blockpos = new BlockPos(x, surfaceY, z);
			
			//Now adds the structure pieces to this.components with all details such as where each part goes 
			//so that the structure can be added to the world by worldgen.
			RunDownHousePieces.start(templateManagerIn, blockpos, rotation, this.components, this.rand);
			
			//Sets the bounds of the structure. 
			//Nether Fortresses uses their bounds to keep spawning Wither Skeletons in the fortress.
			this.recalculateStructureSize();
			
			//I use to debug and quickly find out if the structure is spawning or not and where it is.
			StructureTutorialMain.LOGGER.log(Level.DEBUG, "Rundown House at "+(blockpos.getX())+" "+blockpos.getY()+" "+(blockpos.getZ()));
		}

	}
}