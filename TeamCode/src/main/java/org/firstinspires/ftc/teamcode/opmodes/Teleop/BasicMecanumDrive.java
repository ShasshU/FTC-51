package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Basic Mecanum Drive", group="TeleOp") //ROBOT ORIENTATED
public class BasicMecanumDrive extends OpMode {

    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    private DcMotor flywheel1;

    private DcMotor flywheel2;

    private DcMotor intake;

    private boolean lastA = false;

    boolean intakeOn = false;


    @Override
    public void init() {
        leftFront  = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack   = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack  = hardwareMap.get(DcMotor.class, "rightBack");
        intake = hardwareMap.get(DcMotor.class, "intake");
        flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        flywheel2.setDirection(DcMotor.Direction.REVERSE);

        // rightBack.setDirection(DcMotor.Direction.FORWARD);

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

        if (gamepad1.a && !lastA) {
            intakeOn = !intakeOn;
        }
        lastA = gamepad1.a;

        if (intakeOn) {
            intake.setPower(0.7);
        } else {
            intake.setPower(0.0);
        }
        if (gamepad1.right_bumper) {
            flywheel1.setPower(0.6);
            flywheel2.setPower(0.6);
        } else {
            flywheel1.setPower(0);
            flywheel2.setPower(0);
        }
    }
}