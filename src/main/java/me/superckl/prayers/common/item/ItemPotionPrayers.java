package me.superckl.prayers.common.item;

import java.util.List;

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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemPotionPrayers extends ItemPrayers{

	private final PotionEffect[] effects;

	public ItemPotionPrayers() {
		this.setMaxStackSize(1).setCreativeTab(ModTabs.TAB_PRAYER_ITEMS).setHasSubtypes(true).setUnlocalizedName("potion");
		this.effects = new PotionEffect[] {new PotionEffect(ModPotions.prayerRestoreInstant.id, 0), new PotionEffect(ModPotions.prayerRestoreInstant.id, 0, 1),
				new PotionEffect(ModPotions.prayerRestore.id, 6000), new PotionEffect(ModPotions.prayerRestore.id, 12000, 1), new PotionEffect(ModPotions.prayerBoost.id, 2400),
				new PotionEffect(ModPotions.prayerBoost.id, 6000, 1)};
	}

	@Override
	protected boolean isNameDamageReliant() {
		return true;
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

		if (!world.isRemote)
		{
			final PotionEffect effect = this.effects[stack.getItemDamage()];
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
	public IIcon getIconFromDamage(final int meta) {
		return this.icons[meta];
	}

	@Override
	public int getMetadata(final int meta) {
		return meta;
	}

	@Override
	public void getSubItems(final Item item, final CreativeTabs tab, final List list) {
		for(int i = 0; i < this.effects.length; i++)
			list.add(new ItemStack(item, 1, i));
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	@Override
	public void registerIcons(final IIconRegister register) {
		this.icons = new IIcon[this.effects.length];
		for(int i = 0; i < this.effects.length; i++)
			this.icons[i] = register.registerIcon(ModData.MOD_ID+":potion"+i);
	}

}
