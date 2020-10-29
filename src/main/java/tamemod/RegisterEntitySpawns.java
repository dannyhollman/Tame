package tamemod;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.registries.ForgeRegistries;
import tamemod.init.ExampleEntityInit;

public class RegisterEntitySpawns {

	public static void spawnMobs() {
		// Add spawns for tame mobs in each biome
		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("field")
				|| biome.getRegistryName().toString().contains("taiga")
				|| biome.getRegistryName().toString().contains("plains")
				|| biome.getRegistryName().toString().contains("forest"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_CHICKEN_ENTITY.get(), 5, 3, 6));
		});
		
		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("field")
				|| biome.getRegistryName().toString().contains("taiga")
				|| biome.getRegistryName().toString().contains("plains")
				|| biome.getRegistryName().toString().contains("forest"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_COW_ENTITY.get(), 5, 3, 6));
		});

		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("field")
				|| biome.getRegistryName().toString().contains("plains")
				|| biome.getRegistryName().toString().contains("forest"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_BEE_ENTITY.get(), 1, 3, 3));
		});
		
		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("field")
				|| biome.getRegistryName().toString().contains("taiga")
				|| biome.getRegistryName().toString().contains("plains")
				|| biome.getRegistryName().toString().contains("forest"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_PIG_ENTITY.get(), 5, 3, 6));
		});
		
		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("field")
				|| biome.getRegistryName().toString().contains("taiga")
				|| biome.getRegistryName().toString().contains("plains")
				|| biome.getRegistryName().toString().contains("forest"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_FOX_ENTITY.get(), 1, 1, 3));
		});
		
		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("jungle"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_PANDA_ENTITY.get(), 1, 1, 3));
		});
		
		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("field")
				|| biome.getRegistryName().toString().contains("taiga")
				|| biome.getRegistryName().toString().contains("plains")
				|| biome.getRegistryName().toString().contains("forest"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_RABBIT_ENTITY.get(), 3, 1, 3));
		});
		
		ForgeRegistries.BIOMES.getValues().stream()
		.filter(biome -> biome.getRegistryName().toString().contains("ocean")
				|| biome.getRegistryName().toString().contains("beach"))
		.forEach(biome -> {
			biome.getSpawns(EntityClassification.CREATURE)
			.add(new SpawnListEntry(ExampleEntityInit.TAME_TURTLE_ENTITY.get(), 2, 2, 3));
		});
		
	}

}
