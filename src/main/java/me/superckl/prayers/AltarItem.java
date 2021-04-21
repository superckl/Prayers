package me.superckl.prayers;

import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import me.superckl.prayers.init.ModItems;
import me.superckl.prayers.item.BonesItem;
import me.superckl.prayers.item.RelicItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Builder
@Getter
public class AltarItem extends ForgeRegistryEntry<AltarItem>{

	public static DeferredRegister<AltarItem> REGISTER = DeferredRegister.create(AltarItem.class, Prayers.MOD_ID);
	public static final Supplier<IForgeRegistry<AltarItem>> REGISTRY = AltarItem.REGISTER.makeRegistry("altar_items", RegistryBuilder::new);

	public static RegistryObject<AltarItem> SMALL_BONE = AltarItem.REGISTER.register("small_bones", AltarItem.builder().sacrificeXP(1).sacrificeTicks(20)
			.offerPoints(1.5F).offerTicks(10).matcher(stack -> ItemStack.isSame(stack, new ItemStack(Items.BONE)) ||
					ItemStack.isSame(stack, new ItemStack(ModItems.BONES.get(BonesItem.Type.SMALL)::get)))::build);

	public static RegistryObject<AltarItem> LARGE_BONE = AltarItem.REGISTER.register("large_bones", AltarItem.builder().sacrificeXP(20).sacrificeTicks(40)
			.offerPoints(30F).offerTicks(20).matcher(stack -> ItemStack.isSame(stack, new ItemStack(ModItems.BONES.get(BonesItem.Type.LARGE)::get)))::build);

	public static RegistryObject<AltarItem> ANCIENT_BONE = AltarItem.REGISTER.register("ancient_bones", AltarItem.builder().sacrificeXP(60).sacrificeTicks(60)
			.offerPoints(100F).offerTicks(40).matcher(stack -> ItemStack.isSame(stack, new ItemStack(ModItems.BONES.get(BonesItem.Type.ANCIENT)::get)))::build);

	public static RegistryObject<AltarItem> GILDED_SMALL_BONE = AltarItem.REGISTER.register("gilded_small_bone", AltarItem.builder().sacrificeXP(10).sacrificeTicks(40)
			.offerPoints(80).offerTicks(200).matcher(stack -> ItemStack.isSame(stack, new ItemStack(ModItems.GILDED_BONES.get(BonesItem.Type.SMALL)::get)))::build);

	public static RegistryObject<AltarItem> GILDED_LARGE_BONE = AltarItem.REGISTER.register("gilded_large_bone", AltarItem.builder().sacrificeXP(200).sacrificeTicks(60)
			.offerPoints(110).offerTicks(200).matcher(stack -> ItemStack.isSame(stack, new ItemStack(ModItems.GILDED_BONES.get(BonesItem.Type.LARGE)::get)))::build);

	public static RegistryObject<AltarItem> GILDED_ANCIENT_BONE = AltarItem.REGISTER.register("gilded_ancient_bone", AltarItem.builder().sacrificeXP(600).sacrificeTicks(80)
			.offerPoints(180).offerTicks(200).matcher(stack -> ItemStack.isSame(stack, new ItemStack(ModItems.GILDED_BONES.get(BonesItem.Type.ANCIENT)::get)))::build);

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
		for (final AltarItem altarItem : AltarItem.REGISTRY.get().getValues())
			if(altarItem.matcher.test(stack))
				return altarItem;
		return null;
	}

}
