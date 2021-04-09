package me.superckl.prayers.recipe;

import java.util.stream.Stream;

import com.google.gson.JsonObject;

import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.VesselItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public class FullVesselIngredient extends Ingredient{

	public FullVesselIngredient() {
		super(Stream.of(new SingleItemList(VesselItem.makeFullVessel())));
	}

	@Override
	public boolean test(final ItemStack stack) {
		return stack.getItem() == ModItems.VESSEL.get() && VesselItem.hasAllKills(stack);
	}

	@Override
	public IIngredientSerializer<? extends Ingredient> getSerializer() {
		return Serializer.INSTANCE;
	}

	public static class Serializer implements IIngredientSerializer<FullVesselIngredient>{

		public static final Serializer INSTANCE = new Serializer();

		@Override
		public FullVesselIngredient parse(final PacketBuffer buffer) {
			return new FullVesselIngredient();
		}

		@Override
		public FullVesselIngredient parse(final JsonObject json) {
			return new FullVesselIngredient();
		}

		@Override
		public void write(final PacketBuffer buffer, final FullVesselIngredient ingredient) {}

	}

}
