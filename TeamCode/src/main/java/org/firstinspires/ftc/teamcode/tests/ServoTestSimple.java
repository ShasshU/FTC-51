package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Servo Test SIMPLE", group = "Test")
public class ServoTestSimple extends OpMode {

    private Servo kicker;

    @Override
    public void init() {
        kicker = hardwareMap.get(Servo.class, "kicker");
        telemetry.addLine("Servo initialized!");
        telemetry.addLine("Hold A for 1.0, Hold B for 0.0");
        telemetry.update();
    }

    @Override
    public void loop() {
        // A = go to 1.0
        if (gamepad1.a) {
            kicker.setPosition(1.0);
        }

        // B = go to 0.0
        if (gamepad1.b) {
            kicker.setPosition(0.0);
        }

        // X = go to 0.5
        if (gamepad1.x) {
            kicker.setPosition(0.5);
        }

        telemetry.addLine("HOLD A = 1.0");
        telemetry.addLine("HOLD B = 0.0");
        telemetry.addLine("HOLD X = 0.5");
        telemetry.addData("A Pressed", gamepad1.a);
        telemetry.addData("B Pressed", gamepad1.b);
        telemetry.addData("X Pressed", gamepad1.x);
        telemetry.update();
    }
}