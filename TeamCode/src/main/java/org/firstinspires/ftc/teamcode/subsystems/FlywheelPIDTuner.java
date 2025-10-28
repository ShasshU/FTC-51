package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@Config
@TeleOp(name = "Flywheel Tuning", group = "Tuning")
public class FlywheelPIDTuner extends LinearOpMode {

    // Dashboard-tunable coefficients
    public static double kP = 0.0002;
    public static double kI = 0.0;
    public static double kD = 0.0;
    public static double kS = 0.0;
    public static double kV = 0.00025;
    public static double targetRPM = 200;

    // Internal variables
    private double lastError = 0;
    private double integralSum = 0;
    private long lastTime = 0;

    private DcMotorEx flywheel1, flywheel2;
    private FtcDashboard dashboard;

    @Override
    public void runOpMode() throws InterruptedException {
        flywheel1 = hardwareMap.get(DcMotorEx.class, "flywheel1");
        flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");

        flywheel1.setDirection(DcMotorEx.Direction.FORWARD);
        flywheel2.setDirection(DcMotorEx.Direction.REVERSE);

        dashboard = FtcDashboard.getInstance();

        waitForStart();
        lastTime = System.nanoTime();

        while (opModeIsActive()) {
            double velocity1 = flywheel1.getVelocity();
            double velocity2 = flywheel2.getVelocity();
            double currentRPM = (velocity1 + velocity2) / 2.0;

            // Calculate PID terms
            double error = targetRPM - currentRPM;
            double dt = (System.nanoTime() - lastTime) / 1e9;
            integralSum += error * dt;
            double derivative = (error - lastError) / dt;

            double pidOutput = (kP * error) + (kI * integralSum) + (kD * derivative);

            // Feedforward
            double ff = kS + (kV * targetRPM);

            double power = Range.clip(pidOutput + ff, 0, 1);

            flywheel1.setPower(power);
            flywheel2.setPower(power);

            // Send telemetry to Dashboard
            TelemetryPacket packet = new TelemetryPacket();
            packet.put("Target RPM", targetRPM);
            packet.put("Current RPM", currentRPM);
            packet.put("Power", power);
            packet.put("Error", error);
            packet.put("PID Output", pidOutput);
            packet.put("Feedforward", ff);
            dashboard.sendTelemetryPacket(packet);

            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Current RPM", currentRPM);
            telemetry.addData("Power", power);
            telemetry.addData("Error", error);
            telemetry.update();

            lastError = error;
            lastTime = System.nanoTime();
        }
    }
}