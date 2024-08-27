package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntity.class) public class MixinPlayerEntity implements SoaRPlayer {
    @Unique private boolean standing = false;

    @Override public boolean soar$isStanding() {
        return this.standing;
    }

    @Override public void soar$setStanding(boolean standing) {
        this.standing = standing;
    }

    @SuppressWarnings("UnreachableCode") @Override public boolean soar$onMountAndStanding() {
        return ((Entity) (Object) this).hasVehicle() && this.standing;
    }

    @SuppressWarnings({"ConstantConditions", "UnreachableCode"}) @Override public float soar$ridingSittingDifference() {
        if (!((Entity) (Object) this).hasVehicle()) return 0;

        Entity vehicle = ((Entity) (Object) this).getVehicle();

        Vec3d vehicleAttachment = ((Entity) (Object) this).getVehicleAttachmentPos(vehicle);

        return (float) (vehicleAttachment.y);
    }
}
