package me.superckl.prayers;

import java.util.function.Predicate;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.RelicItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;

@Builder
@Getter
public class AltarItem extends ForgeRegistryEntry<AltarItem>{

	public static DeferredRegister<AltarItem> REGISTER = DeferredRegister.create(AltarItem.class, Prayers.MOD_ID);

	public static RegistryObject<AltarItem> BONE = AltarItem.REGISTER.register("bone", AltarItem.builder().sacrificeXP(1).sacrificeTicks(20)
			.offerPoints(1.5F).offerTicks(10).matcher(stack -> ItemStack.isSame(stack, new ItemStack(Items.BONE)))::build);

	public static RegistryObject<AltarItem> GILDED_BONE = AltarItem.REGISTER.register("gilded_bone", AltarItem.builder().sacrificeXP(10).sacrificeTicks(40)
			.offerPoints(80).offerTicks(200).matcher(stack -> ItemStack.isSame(stack, new ItemStack(ModItems.GILDED_BONE::get)))::build);

	public static RegistryObject<AltarItem> ROTTEN_FLESH = AltarItem.REGISTER.register("rotten_flesh", AltarItem.builder().sacrificeXP(0.5F).sacrificeTicks(10)
			.offerPoints(0.5F).offerTicks(5).matcher(stack -> ItemStack.isSame(stack, new ItemStack(Items.ROTTEN_FLESH)))::build);

	public static RegistryObject<AltarItem> GOLDEN_APPLE = AltarItem.REGISTER.register("golden_apple", AltarItem.builder().sacrificeXP(0).sacrificeTicks(0)
			.offerPoints(50).offerTicks(200).matcher(stack -> ItemStack.isSame(stack, new ItemStack(Items.GOLDEN_APPLE)))::build);

	public static RegistryObject<AltarItem> RELIC = AltarItem.REGISTER.register("relic", AltarItem.builder().sacrificeXP(300).sacrificeTicks(100)
			.offerPoints(0).offerTicks(0).matcher(stack -> {
				for(final RegistryObject<RelicItem> items:ModItems.RELICS.values())
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
