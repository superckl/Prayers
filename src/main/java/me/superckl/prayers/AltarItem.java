package me.superckl.prayers;

import java.util.function.Predicate;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.RelicItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Builder
@Getter
public class AltarItem extends ForgeRegistryEntry<AltarItem>{

	public static DeferredRegister<AltarItem> REGISTER = DeferredRegister.create(AltarItem.class, Prayers.MOD_ID);

	public static RegistryObject<AltarItem> BONE = AltarItem.REGISTER.register("bone", AltarItem.builder().sacrificeXP(0.5F).sacrificeTicks(30)
			.offerPoints(1).offerTicks(40).matcher(stack -> ItemStack.isSame(stack, new ItemStack(Items.BONE)))::build);

	public static RegistryObject<AltarItem> GILDED_BONE = AltarItem.REGISTER.register("gilded_bone", AltarItem.builder().sacrificeXP(10).sacrificeTicks(50)
			.offerPoints(70).offerTicks(100).matcher(stack -> ItemStack.isSame(stack, new ItemStack(ModItems.GILDED_BONE::get)))::build);

	public static RegistryObject<AltarItem> RELIC = AltarItem.REGISTER.register("relic", AltarItem.builder().sacrificeXP(300).sacrificeTicks(200)
			.offerPoints(0).offerTicks(0).matcher(stack -> {
				for(final RegistryObject<Item> items:ModItems.RELICS.values())
					if(items.get() == stack.getItem() && !RelicItem.isCharged(stack))
						return true;
				return false;
			})::build);

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
