package symbolics.division.ikhemusek;

import net.fabricmc.api.ModInitializer;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ikhemusek extends Item implements ModInitializer {
	public static final String MOD_ID = "ikhemusek";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceKey<Item> THIS = ResourceKey.create(Registries.ITEM, id("it"));

	public Ikhemusek() {
		super(new Item.Properties()
				.setId(THIS)
		);
	}

	public static final Identifier id(String name ) {
		return Identifier.fromNamespaceAndPath(MOD_ID, name);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Oh? You're approaching me?");
		// watch this
		Registry.register(BuiltInRegistries.ITEM, THIS, this);
	}
}