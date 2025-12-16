package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

// Limelight imports (adjust if your class names differ)
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class Turret {

    private DcMotor turretMotor;
    private Limelight3A limelight;

    // ===== TUNING CONSTANTS =====
    private static final double kP = 0.02;
    private static final double DEAD_ZONE = 1.0; // degrees
    private static final double MAX_POWER = 0.4;

    // Encoder limits (adjust for your robot)
    private static final int MIN_POS = -1200;
    private static final int MAX_POS = 1200;

    // ===== CONSTRUCTOR =====
    public Turret(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotor.class, "turretMotor");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turretMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // ===== PUBLIC METHODS =====

    /** Call this every loop to auto-aim the turret */
    public void aimAtTarget() {
        LLResult results = limelight.getLatestResult();

        if (results == null || !results.isValid()) {
            turretMotor.setPower(0);
            return;
        }

        double tx = results.getTx();

        // Dead zone to prevent jitter
        if (Math.abs(tx) < DEAD_ZONE) {
            turretMotor.setPower(0);
            return;
        }

        double power = tx * kP;

        // Clamp power
        power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

        // Enforce encoder limits
        int pos = turretMotor.getCurrentPosition();
        if ((pos <= MIN_POS && power < 0) ||
                (pos >= MAX_POS && power > 0)) {
            power = 0;
        }

        turretMotor.setPower(power);
    }

    /** Manual control (for driver override) */
    public void setManualPower(double power) {
        turretMotor.setPower(power);
    }

    /** Stop turret */
    public void stop() {
        turretMotor.setPower(0);
    }

    /** For telemetry */
    public int getPosition() {
        return turretMotor.getCurrentPosition();
    }
}
