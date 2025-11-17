package org.firstinspires.ftc.teamcode.Subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Configurable
@TeleOp(name = "Flywheel PID Tuning", group = "Tuning")
public class FlywheelPIDTuning extends LinearOpMode {

    public static double TARGET_VELOCITY = 185;
    public static double Kp = 0;   
    public static double Ki = 0;
    public static double Kd = 0;
    public static double Kf = 0;

    private DcMotorEx shooter;

    @Override
    public void runOpMode() {
        // Initialize hardware - matches your Shooter.java
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");

        // Match your configuration
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);


        waitForStart();

        while (opModeIsActive()) {
            // Update PID coefficients from PANELS
            shooter.setVelocityPIDFCoefficients(Kp, Ki, Kd, Kf);

            // Set velocity
            shooter.setVelocity(TARGET_VELOCITY);

            // Get current velocity
            double currentVelocity = shooter.getVelocity();
            double error = TARGET_VELOCITY - currentVelocity;
            double percentOfTarget = (currentVelocity / TARGET_VELOCITY) * 100;

            // Display telemetry
            telemetry.addData("Target Velocity", "%.0f ticks/sec", TARGET_VELOCITY);
            telemetry.addData("Current Velocity", "%.0f ticks/sec", currentVelocity);
            telemetry.addData("Error", "%.0f ticks/sec", error);
            telemetry.addData("Percent of Target", "%.1f%%", percentOfTarget);
            telemetry.addLine();
            telemetry.addData("Kp", Kp);
            telemetry.addData("Ki", Ki);
            telemetry.addData("Kd", Kd);
            telemetry.addData("Kf", Kf);
            telemetry.addLine();
            telemetry.addData("Motor Power", "%.2f", shooter.getPower());
            telemetry.update();
        }

        shooter.setPower(0);
    }
}