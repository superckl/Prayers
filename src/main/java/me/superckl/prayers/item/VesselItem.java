package me.superckl.prayers.item;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import me.superckl.prayers.Prayers;
import me.superckl.prayers.client.ClientHelper;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.util.LangUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

public class VesselItem extends Item {

	public static final String KILL_LIST_NBT = "kills";
	public static final Set<ResourceLocation> REQ_MOBS = ImmutableSet.of(EntityType.ZOMBIE.getRegistryName(), EntityType.SKELETON.getRegistryName(),
			EntityType.SPIDER.getRegistryName(), EntityType.ENDERMAN.getRegistryName(), EntityType.BLAZE.getRegistryName(),
			EntityType.CAVE_SPIDER.getRegistryName(), EntityType.CREEPER.getRegistryName(), EntityType.GHAST.getRegistryName(),
			EntityType.HUSK.getRegistryName(), EntityType.RAVAGER.getRegistryName(), EntityType.PILLAGER.getRegistryName(),
			EntityType.EVOKER.getRegistryName(), EntityType.WITCH.getRegistryName(), EntityType.WITHER_SKELETON.getRegistryName(),
			EntityType.STRAY.getRegistryName(), EntityType.SILVERFISH.getRegistryName());

	private static Map<ResourceLocation, IFormattableTextComponent> REQ_NAMES;

	public VesselItem() {
		super(new Item.Properties().stacksTo(1).tab(ModItems.PRAYERS_GROUP));
	}

	@Override
	public void appendHoverText(final ItemStack stack, final World level, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
		final Set<ResourceLocation> kills = VesselItem.getStoredKills(stack);
		tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("soul_orb.souls"), kills.size(), VesselItem.REQ_MOBS.size()));
		if(level == null || InputMappings.isKeyDown(ClientHelper.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)) {
			if(VesselItem.REQ_NAMES == null) {
				VesselItem.REQ_NAMES = Maps.newHashMap();
				VesselItem.REQ_MOBS.forEach(rLoc -> VesselItem.REQ_NAMES.put(rLoc, ForgeRegistries.ENTITIES.getValue(rLoc).getDescription().copy()));
			}
			final Set<ResourceLocation> missing = Sets.difference(VesselItem.REQ_MOBS, kills);
			final Iterator<Entry<ResourceLocation, IFormattableTextComponent>> it = VesselItem.REQ_NAMES.entrySet().iterator();
			while(it.hasNext()) {
				final Entry<ResourceLocation, IFormattableTextComponent> entry = it.next();
				final IFormattableTextComponent name1 = entry.getValue();
				final TextFormatting color1 = missing.contains(entry.getKey()) ? TextFormatting.RED:TextFormatting.GREEN;
				final IFormattableTextComponent text = name1.withStyle(color1);
				tooltip.add(text);
			}
		} else
			tooltip.add(new TranslationTextComponent(LangUtil.buildTextLoc("hold_key"), new StringTextComponent("LSHIFT").withStyle(TextFormatting.AQUA)).withStyle(TextFormatting.GRAY));
	}

	@Override
	public void fillItemCategory(final ItemGroup group, final NonNullList<ItemStack> stacks) {
		if(this.allowdedIn(group)) {
			stacks.add(new ItemStack(this));
			stacks.add(VesselItem.makeFullVessel());
		}
	}

	public static ItemStack makeFullVessel() {
		final ItemStack allKills = new ItemStack(ModItems.VESSEL::get);
		VesselItem.storeKills(VesselItem.REQ_MOBS, allKills);
		return allKills;
	}

	@Override
	public String getDescriptionId(final ItemStack stack) {
		String description = super.getDescriptionId();
		if(VesselItem.hasAllKills(stack))
			description = description.concat("_full");
		return description;
	}

	@Override
	public boolean isFoil(final ItemStack stack) {
		return VesselItem.hasAllKills(stack);
	}

	@Override
	public Rarity getRarity(final ItemStack stack) {
		if(VesselItem.hasAllKills(stack))
			return Rarity.EPIC;
		return super.getRarity(stack);
	}

	public static  boolean hasAllKills(final ItemStack stack) {
		return VesselItem.getStoredKills(stack).size() == VesselItem.REQ_MOBS.size();
	}

	public boolean onKill(final EntityType<?> type, final ItemStack stack) {
		if(!VesselItem.REQ_MOBS.contains(type.getRegistryName()))
			return false;
		final Set<ResourceLocation> kills = VesselItem.getStoredKills(stack);
		if(kills.add(type.getRegistryName())) {
			VesselItem.storeKills(kills, stack);
			return true;
		}
		return false;
	}

	public static Set<ResourceLocation> getStoredKills(final ItemStack stack){
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		final ListNBT kills = nbt.getList(VesselItem.KILL_LIST_NBT, Constants.NBT.TAG_STRING);
		final Set<ResourceLocation> rLocs = Sets.newHashSet();
		kills.forEach(inbt -> rLocs.add(new ResourceLocation(inbt.getAsString())));
		return rLocs;
	}

	private static void storeKills(final Set<ResourceLocation> kills, final ItemStack stack) {
		final ListNBT list = new ListNBT();
		kills.forEach(rLoc -> list.add(StringNBT.valueOf(rLoc.toString())));
		final CompoundNBT nbt = stack.getOrCreateTagElement(Prayers.MOD_ID);
		nbt.put(VesselItem.KILL_LIST_NBT, list);
	}

}
