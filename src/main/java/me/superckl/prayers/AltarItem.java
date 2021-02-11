package me.superckl.prayers;

import java.util.function.Predicate;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;

@Builder
@Getter
public class AltarItem extends ForgeRegistryEntry<AltarItem>{

	public static DeferredRegister<AltarItem> REGISTER = DeferredRegister.create(AltarItem.class, Prayers.MOD_ID);

	@ObjectHolder("minecraft:bone")
	public static Item ITEM_BONE = null;
	public static RegistryObject<AltarItem> BONE = AltarItem.REGISTER.register("bone", AltarItem.builder().sacrificeXP(1).sacrificeTicks(20)
			.offerPoints(1).offerTicks(20).matcher(stack -> ItemStack.areItemsEqual(stack, new ItemStack(AltarItem.ITEM_BONE)))::build);

	@Default
	private final float offerPoints = 0;
	@Default
	private final int offerTicks = 0;
	@Default
	private final float sacrificeXP = 0;
	@Default
	private final int sacrificeTicks = 0;
	private final Predicate<ItemStack> matcher;

	public boolean canOffer() {
		return this.offerPoints > 0;
	}

	public boolean canSacrifice() {
		return this.sacrificeXP > 0;
	}

	public static AltarItem find(final ItemStack stack) {
		for (final RegistryObject<AltarItem> altarItem : AltarItem.REGISTER.getEntries())
			if(altarItem.get().matcher.test(stack))
				return altarItem.get();
		return null;
	}

}
