package symbolics.division.ikhemusek.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import org.spongepowered.asm.mixin.Mixin;
import symbolics.division.ikhemusek.Ikhemusek;

@Mixin(Entity.class)
public class EntityMixin {
	@WrapMethod(
			method = "deflection"
	)
	public ProjectileDeflection deflection(final Projectile projectile, Operation<ProjectileDeflection> original) {
		Entity e = (Entity) (Object) this;
		return !e.level().isClientSide() && Ikhemusek.PERFECT(e) ? Ikhemusek.IT : original.call(projectile);
	}
}
