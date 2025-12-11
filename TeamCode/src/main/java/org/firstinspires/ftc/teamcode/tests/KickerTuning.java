package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;

/**
 * Simple OpMode to test and tune the Kicker servo positions
 *
 * Controls:
 * - DPAD UP: Extend kicker
 * - DPAD DOWN: Retract kicker
 * - DPAD LEFT: Decrease position by 0.05
 * - DPAD RIGHT: Increase position by 0.05
 * - A: Set to current tuned position
 * - X: Print current position for tuning
 */
@TeleOp(name = "Kicker Tuning", group = "Tuning")
public class KickerTuning extends OpMode {

    private Kicker kicker;
    private double tunedPosition = 0.0;

    // Debouncing
    private boolean lastDpadLeft = false;
    private boolean lastDpadRight = false;
    private boolean lastDpadUp = false;
    private boolean lastDpadDown = false;
    private boolean lastA = false;
    private boolean lastX = false;

    @Override
    public void init() {
        kicker = new Kicker(hardwareMap);

        telemetry.addLine("Kicker Tuning Mode");
        telemetry.addLine("DPAD UP: Extend");
        telemetry.addLine("DPAD DOWN: Retract");
        telemetry.addLine("DPAD LEFT/RIGHT: Fine tune");
        telemetry.addLine("A: Set custom position");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Preset positions
        if (gamepad1.dpad_up && !lastDpadUp) {
            kicker.extend();
            telemetry.addLine("Extended!");
        }

        if (gamepad1.dpad_down && !lastDpadDown) {
            kicker.retract();
            telemetry.addLine("Retracted!");
        }

        // Fine tuning
        if (gamepad1.dpad_left && !lastDpadLeft) {
            tunedPosition = Math.max(0.0, kicker.getPosition() - 0.05);
            kicker.setPosition(tunedPosition);
        }

        if (gamepad1.dpad_right && !lastDpadRight) {
            tunedPosition = Math.min(1.0, kicker.getPosition() + 0.05);
            kicker.setPosition(tunedPosition);
        }

        // Set custom position
        if (gamepad1.a && !lastA) {
            kicker.setPosition(tunedPosition);
            telemetry.addLine("Set to: " + String.format("%.2f", tunedPosition));
        }

        // Print for tuning
        if (gamepad1.x && !lastX) {
            telemetry.addLine("=== COPY THIS ===");
            telemetry.addLine("Position: " + String.format("%.3f", kicker.getPosition()));
            telemetry.addLine("================");
        }

        // Update button states
        lastDpadUp = gamepad1.dpad_up;
        lastDpadDown = gamepad1.dpad_down;
        lastDpadLeft = gamepad1.dpad_left;
        lastDpadRight = gamepad1.dpad_right;
        lastA = gamepad1.a;
        lastX = gamepad1.x;

        // Display telemetry
        telemetry.addLine("=== Kicker Status ===");
        telemetry.addData("Current Position", String.format("%.3f", kicker.getPosition()));
        telemetry.addData("Is Extended", kicker.isExtended());
        telemetry.addData("Is Retracted", kicker.isRetracted());
        telemetry.addLine();
        telemetry.addLine("DPAD UP/DOWN: Presets");
        telemetry.addLine("DPAD LEFT/RIGHT: Fine tune");
        telemetry.addLine("X: Print position to copy");
        telemetry.update();
    }
}