package org.firstinspires.ftc.teamcode.tests;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Configurable
@TeleOp(name = "Flywheel PID Tuning", group = "Tuning")
public class FlywheelPIDTuning extends OpMode {

    public static double TARGET_VELOCITY = 185;
    public static double Kp = 0;
    public static double Ki = 0;
    public static double Kd = 0;
    public static double Kf = 12;

    private DcMotorEx shooter;
    private boolean motorRunning = false;

    private TelemetryManager telemetryM;

    @Override
    public void init() {
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        telemetryM.debug("=== Flywheel PID Tuning ===");
        telemetryM.debug("Press RIGHT BUMPER to start motor");
        telemetryM.debug("Press LEFT BUMPER to stop motor");
        telemetryM.debug("Adjust values in PANELS dashboard");
        telemetryM.update(telemetry);
    }

    @Override
    public void loop() {
        if (gamepad1.right_bumper && !motorRunning) {
            motorRunning = true;
        } else if (gamepad1.left_bumper && motorRunning) {
            motorRunning = false;
            shooter.setPower(0);
        }

        if (motorRunning) {
            shooter.setVelocityPIDFCoefficients(Kp, Ki, Kd, Kf);
            shooter.setVelocity(TARGET_VELOCITY);

            double currentVelocity = shooter.getVelocity();
            double error = TARGET_VELOCITY - currentVelocity;
            double percentOfTarget = (TARGET_VELOCITY != 0) ?
                    (currentVelocity / TARGET_VELOCITY) * 100 : 0;

            telemetryM.debug("=== MOTOR RUNNING ===");
            telemetryM.debug("Target Velocity: " + String.format("%.0f", TARGET_VELOCITY) + " ticks/sec");
            telemetryM.debug("Current Velocity: " + String.format("%.0f", currentVelocity) + " ticks/sec");
            telemetryM.debug("Error: " + String.format("%.0f", error) + " ticks/sec");
            telemetryM.debug("% of Target: " + String.format("%.1f%%", percentOfTarget));
            telemetryM.debug("");
            telemetryM.debug("Kp: " + String.format("%.2f", Kp));
            telemetryM.debug("Ki: " + String.format("%.2f", Ki));
            telemetryM.debug("Kd: " + String.format("%.2f", Kd));
            telemetryM.debug("Kf: " + String.format("%.2f", Kf));
            telemetryM.debug("");
            telemetryM.debug("Motor Power: " + String.format("%.3f", shooter.getPower()));
        } else {
            telemetryM.debug("=== MOTOR STOPPED ===");
            telemetryM.debug("Press RIGHT BUMPER to start");
            telemetryM.debug("Current Velocity: " + String.format("%.0f", shooter.getVelocity()));
        }

        telemetryM.update(telemetry);
    }

    @Override
    public void stop() {
        shooter.setPower(0);
    }
}