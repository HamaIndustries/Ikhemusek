package symbolics.division.ikhemusek.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import symbolics.division.ikhemusek.Ikhemusek;

@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentTarget {
	@Shadow
	public abstract Level level();
	
	@WrapMethod(
			method = "deflection"
	)
	public ProjectileDeflection deflection(final Projectile projectile, Operation<ProjectileDeflection> original) {
		return !this.level().isClientSide() && Ikhemusek.PERFECT(this) ? Ikhemusek.IT : original.call(projectile);
	}
}
