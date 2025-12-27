package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Kicker Tuning", group = "Tuning")
public class KickerTuning extends OpMode {

    private Servo kicker;
    private double currentPosition = 0.5;

    // Debouncing
    private boolean lastDpadLeft = false;
    private boolean lastDpadRight = false;
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;
    private boolean lastX = false;

    @Override
    public void init() {
        kicker = hardwareMap.get(Servo.class, "kicker");
        kicker.setPosition(currentPosition);

        telemetry.addLine("Kicker Tuning Mode");
        telemetry.addLine("DPAD UP: Go to 1.0");
        telemetry.addLine("DPAD DOWN: Go to 0.0");
        telemetry.addLine("DPAD LEFT/RIGHT: Fine tune");
        telemetry.addLine("X: Print position");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Preset positions
        if (gamepad1.dpad_up && !lastDpadUp) {
            currentPosition = 1.0;
            kicker.setPosition(currentPosition);
            telemetry.addLine("Set to 1.0 (EXTENDED)");
        }

        if (gamepad1.dpad_down && !lastDpadDown) {
            currentPosition = 0.0;
            kicker.setPosition(currentPosition);
            telemetry.addLine("Set to 0.0 (RETRACTED)");
        }

        // Fine tuning
        if (gamepad1.dpad_left && !lastDpadLeft) {
            currentPosition = Math.max(0.0, currentPosition - 0.05);
            kicker.setPosition(currentPosition);
        }

        if (gamepad1.dpad_right && !lastDpadRight) {
            currentPosition = Math.min(1.0, currentPosition + 0.05);
            kicker.setPosition(currentPosition);
        }

        // Print for tuning
        if (gamepad1.x && !lastX) {
            telemetry.addLine("=========================");
            telemetry.addLine("COPY THIS VALUE:");
            telemetry.addLine(String.format("%.3f", currentPosition));
            telemetry.addLine("=========================");
        }

        // Update button states
        lastDpadUp = gamepad1.dpad_up;
        lastDpadDown = gamepad1.dpad_down;
        lastDpadLeft = gamepad1.dpad_left;
        lastDpadRight = gamepad1.dpad_right;
        lastX = gamepad1.x;

        // Display telemetry
        telemetry.addLine("=== Kicker Tuning ===");
        telemetry.addData("Current Position", String.format("%.3f", currentPosition));
        telemetry.addLine();
        telemetry.addLine("Controls:");
        telemetry.addLine("DPAD UP: Jump to 1.0");
        telemetry.addLine("DPAD DOWN: Jump to 0.0");
        telemetry.addLine("DPAD LEFT: -0.05");
        telemetry.addLine("DPAD RIGHT: +0.05");
        telemetry.addLine("X: Print value to copy");
        telemetry.addLine();
        telemetry.addLine("=== Instructions ===");
        telemetry.addLine("1. Press DPAD UP for extended");
        telemetry.addLine("2. Use LEFT/RIGHT to fine-tune");
        telemetry.addLine("3. Press X to copy the value");
        telemetry.addLine("4. Press DPAD DOWN, then tune retracted");
        telemetry.addLine("5. Press X again to copy");
        telemetry.update();
    }
}