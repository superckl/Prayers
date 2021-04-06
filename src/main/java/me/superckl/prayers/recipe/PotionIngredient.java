package me.superckl.prayers.recipe;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PotionIngredient extends Ingredient{

	private final Item item;
	private final Potion potion;

	public PotionIngredient(final Item item, final Potion potion) {
		super(Stream.of(new SingleItemList(PotionUtils.setPotion(new ItemStack(item), potion))));
		this.item = item;
		this.potion = potion;
	}

	@Override
	public boolean test(final ItemStack stack) {
		return super.test(stack) && PotionUtils.getPotion(stack) == this.potion;
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return new Serializer();
	}

	public static class Serializer implements IIngredientSerializer<PotionIngredient>{

		@Override
		public PotionIngredient parse(final PacketBuffer buffer) {
			return new PotionIngredient(buffer.readRegistryIdSafe(Item.class), buffer.readRegistryIdSafe(Potion.class));
		}

		@Override
		public PotionIngredient parse(final JsonObject json) {
			final Item item = GameRegistry.findRegistry(Item.class).getValue(new ResourceLocation(JSONUtils.getAsString(json, "item")));
			final Potion potion = GameRegistry.findRegistry(Potion.class).getValue(new ResourceLocation(JSONUtils.getAsString(json, "potion")));
			return new PotionIngredient(item, potion);
		}

		@Override
		public void write(final PacketBuffer buffer, final PotionIngredient ingredient) {
			buffer.writeRegistryId(ingredient.item);
			buffer.writeRegistryId(ingredient.potion);

		}

	}

}
