package blue.beaming.soar.injected.mixin;

import blue.beaming.soar.injected.interfaces.SoaRPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
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

    @SuppressWarnings("ConstantConditions") @Override public float soar$ridingSittingDifference() {
        if (!((Entity) (Object) this).hasVehicle()) return 0;
        return 9 / 16f; // :(
        // the version without entity attachments has to use magic numbers, and this won't be consistent. Sorry!
        // this one simply raises the legs. Should another mod change the player model, this WILL break.
    }
}
