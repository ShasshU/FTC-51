package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="Basic Mecanum Drive", group="TeleOp") //ROBOT ORIENTATED
public class BasicMecanumDrive extends OpMode {

    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    @Override
    public void init() {
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack  = hardwareMap.get(DcMotor.class, "rightBack");

        rightBack.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        double y = -gamepad1.left_stick_y; // Forward/backward
        double x = gamepad1.left_stick_x;  // Strafe left/right
        double rx = gamepad1.right_stick_x; // Rotation

        double frontLeftPower = y + x + rx;
        double backLeftPower  = y - x + rx;
        double frontRightPower = y - x - rx;
        double backRightPower  = y + x - rx;

        double max = Math.max(1.0, Math.abs(frontLeftPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backRightPower));

        leftFront.setPower(frontLeftPower / max);
        leftBack.setPower(backLeftPower / max);
        rightFront.setPower(frontRightPower / max);
        rightBack.setPower(backRightPower / max);

        telemetry.addData("FL", frontLeftPower);
        telemetry.addData("FR", frontRightPower);
        telemetry.addData("BL", backLeftPower);
        telemetry.addData("BR", backRightPower);
        telemetry.update();
    }
}