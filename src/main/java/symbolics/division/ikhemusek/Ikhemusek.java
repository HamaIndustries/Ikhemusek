package symbolics.division.ikhemusek;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ikhemusek extends Item implements ModInitializer {
	public static final String MOD_ID = "ikhemusek";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceKey<Item> THIS = ResourceKey.create(Registries.ITEM, id("it"));
	public static final AttachmentType<Integer> PARRY_FRAMES = AttachmentRegistry.createDefaulted(id("parry_frames"), () -> 0);

	public boolean PERFECT(Player player) {
		return player.getAttachedOrCreate(PARRY_FRAMES) > 0;
	}

	public static final Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(MOD_ID, name);
	}

	public Ikhemusek() {
		super(new Item.Properties()
				.setId(THIS)
		);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Oh? You're approaching me?");
		// watch this
		Registry.register(BuiltInRegistries.ITEM, THIS, this);
		ServerTickEvents.START_LEVEL_TICK.register(this::updateFrames);
		ServerLivingEntityEvents.ALLOW_DAMAGE.register(this::ikhemu);
	}

	@Override
	public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
		entity.setAttached(PARRY_FRAMES, 10);
		return true;
	}

	@Override
	public int getUseDuration(ItemStack itemStack, LivingEntity user) {
		return 72000;
	}

	@Override
	public ItemUseAnimation getUseAnimation(ItemStack itemStack) {
		return ItemUseAnimation.BOW;
	}

	@Override
	public InteractionResult use(final Level level, final Player player, final InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResult.CONSUME;
	}

	public void updateFrames(ServerLevel level) {
		for (var player : level.players()) {
			player.setAttached(PARRY_FRAMES, Math.max(player.getAttachedOrCreate(PARRY_FRAMES) - 1, 0));
		}
	}

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksRemaining) {
		super.onUseTick(level, livingEntity, itemStack, ticksRemaining);
	}

	// sek? I hardly know 'er
	public boolean ikhemu(LivingEntity entity, DamageSource source, float amount) {
		System.out.println("awa");
		if (entity instanceof ServerPlayer player && PERFECT(player)) {
			return false;
		}
		return true;
	}
}