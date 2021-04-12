package me.superckl.prayers;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

@Getter
public class ClientConfig {

	@Getter
	private static ClientConfig instance;

	//Widget position
	private final DoubleValue widgetX;
	private final DoubleValue widgetY;

	private ClientConfig(final ForgeConfigSpec.Builder builder) {
		builder.comment("Positions of the prayer bar widget. You can adjust this in game by opening the prayer menu (default key 'O') and dragging the bar.");
		this.widgetX = builder.defineInRange("Prayer Bar X", .041, 0, 1);
		this.widgetY = builder.defineInRange("Prayer Bar Y", .927, 0, 1);
	}

	public static ForgeConfigSpec setup() {
		if (ClientConfig.instance != null)
			throw new IllegalStateException("Config has already been setup!");
		final Pair<ClientConfig, ForgeConfigSpec> specPair =  new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		ClientConfig.instance = specPair.getKey();
		return specPair.getValue();
	}

}
