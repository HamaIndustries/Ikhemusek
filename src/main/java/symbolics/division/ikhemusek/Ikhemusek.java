package symbolics.division.ikhemusek;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.UseCooldown;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ikhemusek extends Item implements ModInitializer, ProjectileDeflection, ServerTickEvents.StartLevelTick, ServerLivingEntityEvents.AllowDamage {
	public static final String MOD_ID = "ikhemusek";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ResourceKey<Item> THIS = ResourceKey.create(Registries.ITEM, id("it"));
	public static final AttachmentType<Integer> PARRY_FRAMES = AttachmentRegistry.createDefaulted(id("parry_frames"), () -> 0);
	public static Ikhemusek IT;

	public static boolean PERFECT(AttachmentTarget entity) {
		return entity.getAttachedOrCreate(PARRY_FRAMES) > 0;
	}

	public static final Identifier id(String name) {
		return Identifier.fromNamespaceAndPath(MOD_ID, name);
	}

	public Ikhemusek() {
		super(new Item.Properties().setId(THIS));
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Oh? You're approaching me?");
		// watch this
		IT = this;
		Registry.register(BuiltInRegistries.ITEM, THIS, this);
		ServerTickEvents.START_LEVEL_TICK.register(this);
		ServerLivingEntityEvents.ALLOW_DAMAGE.register(this);
	}

	@Override
	public boolean releaseUsing(ItemStack itemStack, Level lev, LivingEntity entity, int remainingTime) {
		if (lev instanceof ServerLevel level) {
			entity.setAttached(PARRY_FRAMES, 8);
			itemStack.set(DataComponents.USE_COOLDOWN, new UseCooldown(2));
			var hit = entity.getAttackRangeWith(itemStack).getClosesetHit(entity, 0, e -> e instanceof LivingEntity living && entity.canAttack(living));
			if (hit instanceof BlockHitResult bhr && bhr.getType() != HitResult.Type.MISS) {
				Vec3 eyePos = entity.getEyePosition();
				Vec3 hitPos = bhr.getLocation();
				Vec3 motion = eyePos.subtract(hitPos).normalize().scale(5);
				if (entity instanceof ServerPlayer player) {
					player.connection.send(new ClientboundSetEntityMotionPacket(player.getId(), motion));
				}
			}
		}
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

	@Override
	public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksRemaining) {
		super.onUseTick(level, livingEntity, itemStack, ticksRemaining);
	}

	@Override
	public void deflect(Projectile projectile, @Nullable Entity entity, RandomSource random) {
		if (entity == null) return;
		ProjectileDeflection.AIM_DEFLECT.deflect(projectile, entity, random);
		projectile.setDeltaMovement(projectile.getDeltaMovement().scale(3));
		bingbong(entity.level(), entity);
	}

	@Override
	public void onStartTick(ServerLevel level) {
		// update frames
		for (var player : level.players()) {
			player.setAttached(PARRY_FRAMES, Math.max(player.getAttachedOrCreate(PARRY_FRAMES) - 1, 0));
		}
	}

	// sek? I hardly ikhemu
	@Override
	public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
		if (entity instanceof ServerPlayer player && PERFECT(player)) {
			var item = player.getMainHandItem();
			if (item.is(this)
					&& source.getDirectEntity() instanceof LivingEntity attacker
					&& entity.getAttackRangeWith(item).getClosesetHit(entity, 0, e -> e == attacker)
					instanceof EntityHitResult ehr
			) {
				Vec3 d = player.getEyePosition().subtract(attacker.getEyePosition());
				attacker.knockback(5, d.x, d.z);
				player.attack(entity);
				bingbong(entity.level(), player);
			}
			return false;
		}
		return true;
	}

	public void bingbong(Level level, Entity source) {
		level.playSound(null, source, SoundEvents.BELL_BLOCK, SoundSource.PLAYERS, 2, 1.2f + level.getRandom().nextFloat() * 0.2f);
	}
}
