package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

// Limelight imports (adjust if your class names differ)
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class Turret {

    public static DcMotor turretMotor;
    public static Limelight3A limelight;

    // ===== TUNING CONSTANTS =====
    private static final double kP = 0.04;
    private static final double kD = 0.03;
    private static double lastTX = 0;
    private static final double DEAD_ZONE = 1; // degrees
    private static final double MAX_POWER = 0.5;

    // Encoder limits (adjust for your robot)
    private static final int MIN_POS = -1200;
    private static final int MAX_POS = 1200;

    // ===== CONSTRUCTOR =====
    public Turret(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotor.class, "turretMotor");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(1);

        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turretMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public static void aimAtTarget() {
        LLResult r = limelight.getLatestResult();

        if (r == null || !r.isValid()) {
            turretMotor.setPower(0);
            return;
        }

        double tx = r.getTx();
        double dTx = tx - lastTX; // how much tx changed since last loop
        lastTX = tx;

        double power = - (tx * kP + dTx * kD); // P + D term
        double MIN_POWER = 0.05;
        double distanceFactor = Math.min(1.0, Math.abs(tx)/30.0);
        power = Math.signum(power) * Math.max(MIN_POWER, Math.abs(power) * distanceFactor);
        if (power > 0 && power < MIN_POWER) power = MIN_POWER;
        if (power < 0 && power > -MIN_POWER) power = -MIN_POWER;

        power = Math.max(-MAX_POWER, Math.min(MAX_POWER, power));

        if (Math.abs(tx) < DEAD_ZONE) power = 0;

        turretMotor.setPower(power);


    }



    /** For telemetry */
    public int getPosition() {
        return turretMotor.getCurrentPosition();
    }
}
