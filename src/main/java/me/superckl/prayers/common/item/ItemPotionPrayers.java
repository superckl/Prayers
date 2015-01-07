package me.superckl.prayers.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.superckl.prayers.common.reference.ModData;
import me.superckl.prayers.common.reference.ModPotions;
import me.superckl.prayers.common.reference.ModTabs;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPotionPrayers extends ItemPrayers{

	private final PotionEffect[] effects;

	public ItemPotionPrayers() {
		this.setMaxStackSize(1).setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setHasSubtypes(true).setUnlocalizedName("potion");
		this.effects = new PotionEffect[] {new PotionEffect(ModPotions.prayerRestoreInstant.id, 0), new PotionEffect(ModPotions.prayerRestoreInstant.id, 0, 1),
				new PotionEffect(ModPotions.prayerRestore.id, 6000), new PotionEffect(ModPotions.prayerRestore.id, 12000, 1), new PotionEffect(ModPotions.prayerBoost.id, 2400),
				new PotionEffect(ModPotions.prayerBoost.id, 6000, 1), new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0), new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0, 1)};
	}

	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean bool) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("effect")){
			//emulate vanilla potion tooltips... sort of
			final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(stack.getTagCompound().getCompoundTag("effect"));
			String s1 = StatCollector.translateToLocal(effect.getEffectName()).trim();
			final Potion potion = Potion.potionTypes[effect.getPotionID()];

			if (effect.getAmplifier() > 0)
				s1 = s1 + " " + StatCollector.translateToLocal("potion.potency." + effect.getAmplifier()).trim();

			if (effect.getDuration() > 20)
				s1 = s1 + " (" + Potion.getDurationString(effect) + ")";

			if (potion.isBadEffect())
				list.add(EnumChatFormatting.RED + s1);
			else
				list.add(EnumChatFormatting.GRAY + s1);
		}
	}

	@Override
	public String getItemStackDisplayName(final ItemStack stack) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("effect")){
			final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(stack.getTagCompound().getCompoundTag("effect"));
			final String name = StatCollector.translateToLocal(effect.getEffectName()).trim()+" "+super.getItemStackDisplayName(stack).trim();
			return name;
		}else
			return super.getItemStackDisplayName(stack);
	}



	@Override
	public int getMaxItemUseDuration(final ItemStack p_77626_1_)
	{
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(final ItemStack p_77661_1_)
	{
		return EnumAction.drink;
	}

	@Override
	public ItemStack onItemRightClick(final ItemStack stack, final World world, final EntityPlayer player) {
		player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
		return stack;
	}

	@Override
	public ItemStack onEaten(final ItemStack stack, final World world, final EntityPlayer player) {
		if (!player.capabilities.isCreativeMode)
			--stack.stackSize;

		if (!world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("effect"))
		{
			final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(stack.getTagCompound().getCompoundTag("effect"));
			if(Potion.potionTypes[effect.getPotionID()].isInstant())
				Potion.potionTypes[effect.getPotionID()].affectEntity(null, player, effect.getAmplifier(), 0);
			else
				player.addPotionEffect(new PotionEffect(effect));
		}

		if (!player.capabilities.isCreativeMode)
		{
			if (stack.stackSize <= 0)
				return new ItemStack(Items.glass_bottle);

			player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		}

		return stack;
	}

	@Override
	public IIcon getIcon(final ItemStack stack, final int pass) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("effect")){
			final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(stack.getTagCompound().getCompoundTag("effect"));
			final String name = effect.getEffectName().split(".")[1];
			if(this.icons.containsKey(name))
				return this.icons.get(name);
		}
		return Items.potionitem.getIcon(stack, pass);
	}

	@Override
	public int getMetadata(final int meta) {
		return meta;
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		final ItemStack base = new ItemStack(item);
		ItemStack temp;
		for(final PotionEffect effect:this.effects){
			temp = base.copy();
			final NBTTagCompound comp = new NBTTagCompound();
			final NBTTagCompound sub = new NBTTagCompound();
			effect.writeCustomPotionEffectToNBT(sub);
			comp.setTag("effect", sub);
			temp.setTagCompound(comp);
			list.add(temp);
		}
	}

	@SideOnly(Side.CLIENT)
	private final Map<String, IIcon> icons = new HashMap<String, IIcon>();

	@Override
	public void registerIcons(final IIconRegister register) {
		this.icons.put("prayerboost", register.registerIcon(ModData.MOD_ID+":prayerboost"));
		this.icons.put("prayerrestore", register.registerIcon(ModData.MOD_ID+":prayerestore"));
		this.icons.put("prayerrestoreinstant", register.registerIcon(ModData.MOD_ID+":prayerestoreinstant"));
		this.icons.put("maxpointsraise", register.registerIcon(ModData.MOD_ID+":maxpointsraise"));
	}

}
