package me.superckl.prayers;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import lombok.Getter;
import me.superckl.prayers.block.AltarBlock.AltarTypes;
import me.superckl.prayers.boon.ItemBoon;
import me.superckl.prayers.item.decree.DecreeItem;
import me.superckl.prayers.item.decree.DecreeItem.Type;
import me.superckl.prayers.prayer.Prayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.config.ModConfig.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Getter
public class Config {

	@Getter
	private static Config instance;
	//Prayers
	private final ConfigValue<List<String>> prayers;

	//Decrees
	private final Map<DecreeItem.Type, IntValue> decreeRanges = new EnumMap<>(DecreeItem.Type.class);
	private final DoubleValue fertilityCropChance;
	private final DoubleValue fertilityAnimalChance;

	//Boons
	private final Map<ItemBoon, DoubleValue> boonValues = new EnumMap<>(ItemBoon.class);
	private final DoubleValue digBoon;

	//Wither Cheese
	private final BooleanValue preventWitherCheese;

	//Talisman & Reliquary
	private final DoubleValue talimsanPoints;
	private final DoubleValue talismanLossFactor;
	private final DoubleValue reliquaryPoints;
	private final DoubleValue reliquaryLossFactor;

	//Altars
	private final Map<AltarTypes, DoubleValue> altarPoints = new EnumMap<>(AltarTypes.class);
	private final Map<AltarTypes, DoubleValue> altarRecharge = new EnumMap<>(AltarTypes.class);
	private final Map<AltarTypes, DoubleValue> altarTransfer = new EnumMap<>(AltarTypes.class);
	private final Map<AltarTypes, IntValue> altarConnected = new EnumMap<>(AltarTypes.class);
	private final Map<AltarTypes, IntValue> maxSacrificeStack = new EnumMap<>(AltarTypes.class);

	//Grenade
	private final IntValue grenadeTime;

	private Config(final ForgeConfigSpec.Builder builder) {
		//Prayers
		final List<String> prayerLocs = Prayer.defaultLocations().stream().map(ResourceLocation::toString).collect(Collectors.toList());
		final String[] comment = new String[prayerLocs.size()+1];
		comment[0] = "List of prayer resource locations to disable. Defaults prayers are ";
		for(int i = 0; i < prayerLocs.size(); i++)
			comment[i + 1] = prayerLocs.get(i);
		builder.comment(comment);
		this.prayers = builder.define("Prayers", Lists.newArrayList());

		//Decrees
		builder.comment("Radius, in blocks, of the decree of fertility. (default 9)");
		this.decreeRanges.put(Type.FERTILITY, builder.defineInRange("Decree.Decree of Fertility Range", 9, 0, Integer.MAX_VALUE));
		builder.comment("This controls how often the decree of fertility will apply bonemeal to crops within its radius. (default .0003125)",
				"A random value (0,1) is checked against this value every tick; .0003125 means on average every 160 seconds.",
				"This might sound like a lot, but a 9x9 farm has ~80 blocks, so one crop bonemeals every 2 seconds.");
		this.fertilityCropChance = builder.defineInRange("Decree.Decree of Fertility Crop Chance", .0003125, 0, 1);
		builder.comment("This controls how often the decree of fertility will apply food to animals. (default .0003125)",
				"A random value (0,1) is checked against this value every tick; .0003125 means on average every 160 seconds.",
				"This might sound like a lot, but with 10 animals one will get fed every 16 seconds.");
		this.fertilityAnimalChance = builder.defineInRange("Decree.Decree of Fertility Animal Chance", .0003125, 0, 1);

		builder.comment("Radius, in blocks, of the decree of infertility. (default 9)");
		this.decreeRanges.put(Type.INFERTILITY, builder.defineInRange("Decree.Decree of Infertility Range", 9, 0, Integer.MAX_VALUE));

		builder.comment("Radius, in blocks, of the decree of sanctuary. (default 30)");
		this.decreeRanges.put(Type.SANCTUARY, builder.defineInRange("Decree.Decree of Sanctuary Range", 30, 0, Integer.MAX_VALUE));

		builder.comment("Radius, in chunks, of the decree of persistence. (default 2)");
		this.decreeRanges.put(Type.PERSISTENCE, builder.defineInRange("Decree.Decree of Persistence Range", 2, 0, Integer.MAX_VALUE));

		//Boons
		builder.comment("Boon of Damage percentage bonus (default 0.5 (+50%))");
		this.boonValues.put(ItemBoon.ATTACK_DAMAGE, builder.defineInRange("Boon.Boon of Damage", 0.5, 0, Double.MAX_VALUE));

		builder.comment("Boon of Armor percentage bonus (default 0.5 (+50%))");
		this.boonValues.put(ItemBoon.ARMOR, builder.defineInRange("Boon.Boon of Armor", 0.5, 0, Double.MAX_VALUE));

		builder.comment("Boon of Speed percentage bonus (default 0.3 (+30%))");
		this.boonValues.put(ItemBoon.SPEED, builder.defineInRange("Boon.Boon of Speed", 0.3, 0, Double.MAX_VALUE));

		builder.comment("Boon of Rapidity attack speed percentage bonus (default 0.3 (+30%))");
		this.boonValues.put(ItemBoon.USE_SPEED, builder.defineInRange("Boon.Boon of Rapidity Attack", 0.3, 0, Double.MAX_VALUE));

		builder.comment("Boon of Rapidity block break speed percentage bonus (default 0.75 (+75%))");
		this.digBoon = builder.defineInRange("Boon.Boon of Rapidity Dig", 0.75, 0, Double.MAX_VALUE);

		//Wither Cheese
		builder.comment("If true, Prayers will attempt to prevent 'cheesing' of upgraded withers by cancelling any damage from fake players (e.g., grinders)");
		this.preventWitherCheese = builder.define("Prevent Wither Cheese", true);

		//Talisman and Reliquary
		builder.comment("Maximum points that can be stored in a talisman (default 300)");
		this.talimsanPoints = builder.defineInRange("Items.Talisman Points", 300, 0, Double.MAX_VALUE);

		builder.comment("Loss factor for recharging talisman. Applied inversely (default 5, so 1-1/5 -> 80% loss)");
		this.talismanLossFactor = builder.defineInRange("Items.Talisman Loss", 5, 1, Double.MAX_VALUE);

		builder.comment("Maximum points that can be stored in a reliquary (default 1000)");
		this.reliquaryPoints = builder.defineInRange("Items.Reliquary Points", 1000, 0, Double.MAX_VALUE);

		builder.comment("Loss factor for recharging reliquary. Applied inversely (default 2.5, so 1-1/2.5 -> 60% loss)");
		this.reliquaryLossFactor = builder.defineInRange("Items.Talisman Loss", 2.5, 1, Double.MAX_VALUE);

		//Altars
		builder.comment("Base points stored by this altar type. (default 100)");
		this.altarPoints.put(AltarTypes.SANDSTONE, builder.defineInRange("Altar.Sandstone Altar Points", 100, 0, Double.MAX_VALUE));

		builder.comment("Base points stored by this altar type. (default 500)");
		this.altarPoints.put(AltarTypes.GILDED_SANDSTONE, builder.defineInRange("Altar.Gilded Sandstone Altar Points", 500, 0, Double.MAX_VALUE));

		builder.comment("Base points stored by this altar type. (default 3000)");
		this.altarPoints.put(AltarTypes.MARBLE, builder.defineInRange("Altar.Marble Altar Points", 3000, 0, Double.MAX_VALUE));

		builder.comment("Recharge rate of this altar type. Applied per tick multiplied by the max points. (default 1/24000, one day)");
		this.altarRecharge.put(AltarTypes.SANDSTONE, builder.defineInRange("Altar.Sandstone Altar Recharge", 1D/24000D, 0, Double.MAX_VALUE));

		builder.comment("Recharge rate of this altar type. Applied per tick multiplied by the max points. (default 1/36000)");
		this.altarRecharge.put(AltarTypes.GILDED_SANDSTONE, builder.defineInRange("Altar.Gilded Sandstone Altar Recharge", 1D/36000D, 0, Double.MAX_VALUE));

		builder.comment("Recharge rate of this altar type. Applied per tick multiplied by the max points. (default 1/48000, two days)");
		this.altarRecharge.put(AltarTypes.MARBLE, builder.defineInRange("Altar.Marble Altar Recharge", 1D/48000D, 0, Double.MAX_VALUE));

		builder.comment("Rate of point transfer to crafting stands of this altar type. Applied per tick. (default 1/10)");
		this.altarTransfer.put(AltarTypes.SANDSTONE, builder.defineInRange("Altar.Sandstone Altar Transfer", 2D/20D, 0, Double.MAX_VALUE));

		builder.comment("Rate of point transfer to crafting stands of this altar type. Applied per tick. (default 3/10)");
		this.altarTransfer.put(AltarTypes.GILDED_SANDSTONE, builder.defineInRange("Altar.Gilded Sandstone Altar Transfer", 6D/20D, 0, Double.MAX_VALUE));

		builder.comment("Rate of point transfer to crafting stands of this altar type. Applied per tick. (default 1.5)");
		this.altarTransfer.put(AltarTypes.MARBLE, builder.defineInRange("Altar.Marble Altar Transfer", 30D/20D, 0, Double.MAX_VALUE));

		builder.comment("Maximum number of connected altars for this altar type. (default 2)");
		this.altarConnected.put(AltarTypes.SANDSTONE, builder.defineInRange("Altar.Sandstone Altar Connected", 2, 0, Integer.MAX_VALUE));

		builder.comment("Maximum number of connected altars for this altar type. (default 4)");
		this.altarConnected.put(AltarTypes.GILDED_SANDSTONE, builder.defineInRange("Altar.Gilded Sandstone Altar Connected", 4, 0, Integer.MAX_VALUE));

		builder.comment("Maximum number of connected altars for this altar type. (default 5)");
		this.altarConnected.put(AltarTypes.MARBLE, builder.defineInRange("Altar.Marble Altar Connected", 5, 0, Integer.MAX_VALUE));

		builder.comment("Maximum number of items that may be sacrified at a time on a sandstone altar. (default 0, no limit)");
		this.maxSacrificeStack.put(AltarTypes.SANDSTONE, builder.defineInRange("Altar.Sandstone Altar Max Stack", 0, 0, Integer.MAX_VALUE));

		builder.comment("Maximum number of items that may be sacrified at a time on a gilded sandstone altar. (default 0, no limit)");
		this.maxSacrificeStack.put(AltarTypes.GILDED_SANDSTONE, builder.defineInRange("Altar.Gilded Sandstone Altar Max Stack", 0, 0, Integer.MAX_VALUE));

		builder.comment("Maximum number of items that may be sacrified at a time on a marble altar. (default 0, no limit)");
		this.maxSacrificeStack.put(AltarTypes.MARBLE, builder.defineInRange("Altar.Marble Altar Max Stack", 0, 0, Integer.MAX_VALUE));

		//Grenade
		builder.comment("Time, in ticks, for grenade to explode.");
		this.grenadeTime = builder.defineInRange("Grenade.Time", 80, 1, Integer.MAX_VALUE);
	}

	public static ForgeConfigSpec setup() {
		if (Config.instance != null)
			throw new IllegalStateException("Config has already been setup!");
		final Pair<Config, ForgeConfigSpec> specPair =  new ForgeConfigSpec.Builder().configure(Config::new);
		Config.instance = specPair.getKey();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(Config.instance::onConfigLoad);
		return specPair.getValue();
	}

	private void onConfigLoad(final ModConfigEvent e) {
		this.applyConfig();
	}

	private void applyConfig() {
		final List<String> disabled = this.prayers.get();
		Prayer.REGISTRY.get().getValues().forEach(prayer ->
		prayer.setEnabled(!disabled.contains(prayer.getRegistryName().toString())));
	}

}
