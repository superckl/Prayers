package me.superckl.prayers.common.item;

import java.util.ArrayList;
import java.util.List;

import me.superckl.prayers.common.reference.ModPotions;
import me.superckl.prayers.common.reference.ModTabs;
import me.superckl.prayers.common.utility.LogHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPotionPrayers extends ItemPrayers{

	private final PotionEffect[] effects;

	public ItemPotionPrayers() {
		this.setMaxStackSize(1).setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setHasSubtypes(true).setUnlocalizedName("potion");
		this.effects = new PotionEffect[] {new PotionEffect(ModPotions.prayerRestoreInstant.id, 0), new PotionEffect(ModPotions.prayerRestoreInstant.id, 0, 1),
				new PotionEffect(ModPotions.prayerRestore.id, 6000), new PotionEffect(ModPotions.prayerRestore.id, 12000, 1), new PotionEffect(ModPotions.prayerBoost.id, 2400),
				new PotionEffect(ModPotions.prayerBoost.id, 6000, 1), new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0), new PotionEffect(ModPotions.prayerMaxPointsRaise.id, 0, 1),
				new PotionEffect(ModPotions.prayerDrain.id, 6000), new PotionEffect(ModPotions.prayerDrain.id, 12000, 1)};
	}

	@Override
	public void addInformation(final ItemStack stack, final EntityPlayer player, final List list, final boolean bool) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("effects")){
			//emulate vanilla potion tooltips... sort of
			final NBTTagList nbtList = stack.getTagCompound().getTagList("effects", NBT.TAG_COMPOUND);
			if(nbtList.tagCount() <= 0)
				return;
			for(int i = 0; i < nbtList.tagCount(); i++){
				final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(nbtList.getCompoundTagAt(i));
				if(effect == null){
					LogHelper.error("Something went wrong when parsing a potion effect!");
					continue;
				}
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
	}

	@Override
	public String getItemStackDisplayName(final ItemStack stack) {
		if(stack.hasTagCompound() && stack.getTagCompound().hasKey("effects")){
			final NBTTagList list = stack.getTagCompound().getTagList("effects", NBT.TAG_COMPOUND);
			if(list.tagCount() <= 0)
				return super.getItemStackDisplayName(stack);
			final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(list.getCompoundTagAt(0));
			if(effect == null){
				LogHelper.error("Something went wrong when parsing a potion effect!");
				return "Potion";
			}
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

		if (!world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey("effects"))
		{
			final NBTTagList list = stack.getTagCompound().getTagList("effects", NBT.TAG_COMPOUND);
			if(list.tagCount() <= 0)
				return stack;
			for(int i = 0; i < list.tagCount(); i++){
				final PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(list.getCompoundTagAt(i));
				if(Potion.potionTypes[effect.getPotionID()].isInstant())
					Potion.potionTypes[effect.getPotionID()].affectEntity(null, player, effect.getAmplifier(), 0);
				else
					player.addPotionEffect(new PotionEffect(effect));
			}
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
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(final ItemStack stack, final int pass)
	{
		if((pass > 0) || !(stack.hasTagCompound() && stack.getTagCompound().hasKey("effects")))
			return 16777215;
		final NBTTagList list = stack.getTagCompound().getTagList("effects", NBT.TAG_COMPOUND);
		if(list.tagCount() <= 0)
			return 16777215;
		final List<PotionEffect> effects = new ArrayList<PotionEffect>();
		for(int i = 0; i < list.tagCount(); i++)
			effects.add(PotionEffect.readCustomPotionEffectFromNBT(list.getCompoundTagAt(i)));
		return PotionHelper.calcPotionLiquidColor(effects);
	}

	@Override
	public IIcon getIcon(final ItemStack stack, final int pass) {
		return pass == 0 ? this.blankPotion:this.emptyBottle;
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		final ItemStack base = new ItemStack(item);
		ItemStack temp;
		for(final PotionEffect effect:this.effects){
			temp = base.copy();
			ItemPotionPrayers.withEffects(temp, effect);
			list.add(temp);
		}
	}

	/*@SideOnly(Side.CLIENT)
	private final Map<String, IIcon> icons = new HashMap<String, IIcon>();

	@Override
	public void registerIcons(final IIconRegister register) {
		this.icons.put("prayerboost", register.registerIcon(ModData.MOD_ID+":prayerboost"));
		this.icons.put("prayerrestore", register.registerIcon(ModData.MOD_ID+":prayerrestore"));
		this.icons.put("prayerrestoreinstant", register.registerIcon(ModData.MOD_ID+":prayerrestoreinstant"));
		this.icons.put("attunement", register.registerIcon(ModData.MOD_ID+":attunement"));
	}*/

	private IIcon blankPotion;
	private IIcon emptyBottle;

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(final IIconRegister p_94581_1_)
	{
		this.emptyBottle = p_94581_1_.registerIcon("potion" + "_" + "bottle_drinkable");
		this.blankPotion = p_94581_1_.registerIcon("potion" + "_" + "overlay");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@Override
	public boolean hasEffect(final ItemStack i){
		return true;
	}

	public static ItemStack withEffects(final ItemStack stack, final PotionEffect ... effects){
		if(!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());
		if(!stack.getTagCompound().hasKey("effects"))
			stack.getTagCompound().setTag("effects", new NBTTagList());
		final NBTTagList list = stack.getTagCompound().getTagList("effects", NBT.TAG_COMPOUND);
		for(final PotionEffect effect:effects){
			final NBTTagCompound sub = new NBTTagCompound();
			effect.writeCustomPotionEffectToNBT(sub);
			list.appendTag(sub);
		}
		return stack;
	}

}
