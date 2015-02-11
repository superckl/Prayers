package me.superckl.prayers.integration.thaumcraft;

import me.superckl.prayers.common.reference.ModBlocks;
import me.superckl.prayers.common.reference.ModItems;
import me.superckl.prayers.integration.IIntegrationModule;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ThaumcraftIntegration implements IIntegrationModule{

	@Override
	public void preInit() {}

	@Override
	public void init() {}

	@Override
	public void postInit() {

		//Item aspects
		ThaumcraftApi.registerObjectTag(new ItemStack(ModBlocks.offeringTable, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 2).add(Aspect.AURA, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.basicBone, 1, 0), new AspectList(new ItemStack(Items.bone)));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.basicBone, 1, 1), new AspectList(new ItemStack(Items.bone)));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.basicBone, 1, 2), new AspectList(new ItemStack(Items.bone)).add(Aspect.EARTH, 1));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.basicBone, 1, 3), new AspectList(new ItemStack(Items.bone)).add(Aspect.ORDER, 5));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.bottle, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.WATER, 2).add(Aspect.ORDER, 3));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.potion, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.HEAL, 2));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.tome, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MIND, 5));
		ThaumcraftApi.registerObjectTag(new ItemStack(ModItems.aTome, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.MIND, 5));

		//Entity aspects
		ThaumcraftApi.registerEntityTag("prayersundeadpriest", new AspectList().add(Aspect.UNDEAD, 5).add(Aspect.SOUL, 5).add(Aspect.MAGIC, 5).add(Aspect.TAINT, 5));
		ThaumcraftApi.registerEntityTag("prayerswizardspell", new AspectList().add(Aspect.MAGIC, 5).add(Aspect.WEAPON, 2));
	}

	@Override
	public String getName() {
		return "Thaumcraft Integration";
	}

}
