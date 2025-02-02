package dev.demon.base.check.impl.movement.fly;

import dev.demon.base.check.api.Check;
import dev.demon.base.check.api.CheckType;
import dev.demon.base.check.api.Data;
import dev.demon.base.event.PacketEvent;
import org.bukkit.Bukkit;

@Data(name = "Fly",
        subName = "C",
        checkType = CheckType.MOVEMENT,
        experimental = true,
        description = "Checks for impossible movements in the air (ig).")

public class FlyC extends Check {

    private double threshold;

    @Override
    public void onPacket(PacketEvent event) {
        if (event.isFlying()) {

            if (getUser().getProcessorManager().getCollisionProcessor().getLiquidTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getIceTicks() > 0
                    || getUser().getProcessorManager().getActionProcessor().getLastVelocityTimer().hasNotPassed(9)
                    || getUser().getProcessorManager().getCollisionProcessor().getSoulSandTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getSlimeTicks() > 0
                    || getUser().getProcessorManager().getCollisionProcessor().getClimbableTicks() > 0
                    || getUser().generalCancel()
                    || getUser().getProcessorManager().getActionProcessor()
                    .getServerTeleportTimer().hasNotPassed(3)) return;


            boolean ground = getUser().getProcessorManager().getMovementProcessor().getTo().isOnGround();
            boolean serverGround = getUser().getProcessorManager().getCollisionProcessor().isServerGround();

            int airTicks = getUser().getProcessorManager().getCollisionProcessor().getServerAirTicks();

            double deltaY = getUser().getProcessorManager().getMovementProcessor().getDeltaY();

            if (!ground && !serverGround && airTicks > 7) {

                if (deltaY >= 0.0) {

                    this.threshold++;

                    if (++this.threshold > 5) {
                        this.fail("Invalid motion when in the air",
                                "deltaY=" + deltaY,
                                "airTick=" + airTicks,
                                "threshold=" + this.threshold);
                    }

                } else {
                    this.threshold -= Math.min(this.threshold, .0125);
                }
            } else {
                this.threshold -= Math.min(this.threshold, .025);
            }
        }
    }
}
