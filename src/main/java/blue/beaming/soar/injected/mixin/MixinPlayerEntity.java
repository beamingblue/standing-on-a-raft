package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class) public class MixinPlayerEntity implements SoaRPlayer {
    @Unique private boolean standing = false;

    @Override public boolean soar$standing() {
        return this.standing;
    }

    @Override public void soar$setStanding(boolean standing) {
        this.standing = standing;
    }

    @Override public boolean soar$standingOnRaft() {
        if (!this.standing) return false;
        return soar$onRaft();
    }

    @Override public boolean soar$onRaft() {
        Entity vehicle = ((Entity) (Object) this).getVehicle();
        return vehicle != null && (vehicle.getType() == EntityType.BOAT || vehicle.getType() == EntityType.CHEST_BOAT) && ((BoatEntity) vehicle).getVariant() == BoatEntity.Type.BAMBOO;
    }
}
