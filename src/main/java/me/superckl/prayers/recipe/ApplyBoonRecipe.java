package me.superckl.prayers.recipe;

import com.google.gson.JsonObject;

import lombok.Getter;
import me.superckl.prayers.LogHelper;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.init.ModRecipes;
import me.superckl.prayers.item.RelicItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class ApplyBoonRecipe extends SpecialRecipe{

	@Getter
	private final ItemBoon boon;

	public ApplyBoonRecipe(final ResourceLocation id, final ItemBoon boon) {
		super(id);
		this.boon = boon;
	}

	@Override
	public boolean canCraftInDimensions(final int width, final int height) {
		return width*height >= 2;
	}

	@Override
	public IRecipeSerializer<?> getSerializer() {
		return ModRecipes.BOON_SERIALIZER.get();
	}

	@Override
	public boolean matches(final CraftingInventory inv, final World level) {
		ItemStack applyTo = ItemStack.EMPTY;
		ItemBoon toApply = null;
		for(int i = 0; i < inv.getContainerSize(); i++) {
			final ItemStack stack = inv.getItem(i);
			LogHelper.info(stack);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof RelicItem && RelicItem.isCharged(stack) && toApply == null) {
					toApply = ((RelicItem)stack.getItem()).getType();
					continue;
				}
				if(!applyTo.isEmpty())
					return false;
				applyTo = stack;
			}
		}
		return toApply == this.boon && toApply.canBeAppliedTo(applyTo) && !ItemBoon.getBoon(applyTo).filter(this.boon::equals).isPresent();
	}

	@Override
	public ItemStack assemble(final CraftingInventory inv) {
		ItemStack applyTo = ItemStack.EMPTY;
		ItemBoon toApply = null;
		for(int i = 0; i < inv.getContainerSize(); i++) {
			final ItemStack stack = inv.getItem(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() instanceof RelicItem && RelicItem.isCharged(stack) && toApply == null) {
					toApply = ((RelicItem)stack.getItem()).getType();
					continue;
				}
				applyTo = stack;
			}
			if(!applyTo.isEmpty() && toApply != null)
				break;
		}
		final ItemStack output = applyTo.copy();
		toApply.setBoon(output);
		return output;
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ApplyBoonRecipe>{

		public static final String BOON_KEY = "boon";

		@Override
		public ApplyBoonRecipe fromJson(final ResourceLocation rLoc, final JsonObject json) {
			final ItemBoon boon = ItemBoon.valueOf(JSONUtils.getAsString(json, Serializer.BOON_KEY).toUpperCase());
			return new ApplyBoonRecipe(rLoc, boon);
		}

		@Override
		public ApplyBoonRecipe fromNetwork(final ResourceLocation rLoc, final PacketBuffer buffer) {
			return new ApplyBoonRecipe(rLoc, buffer.readEnum(ItemBoon.class));
		}

		@Override
		public void toNetwork(final PacketBuffer buffer, final ApplyBoonRecipe instance) {
			buffer.writeEnum(instance.boon);
		}

	}

}
