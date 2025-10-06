package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name="Teleop", group="TeleOp") // FIELD ORIENTATED
public class Teleop extends OpMode {


    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    private DcMotor flywheel1;

    private DcMotor flywheel2;

    private DcMotor intake;

    private boolean lastA = false;


    boolean intakeOn = false;

    private IMU imu;
    double forward, strafe, rotate;

    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        intake = hardwareMap.get(DcMotor.class, "intake");
        flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");

        flywheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        flywheel2.setDirection(DcMotor.Direction.REVERSE);


        imu = hardwareMap.get(IMU.class,"imu");

        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP); //change depending on robot

        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));



        telemetry.addData("Status", "Initialized");
    }

    public void drive(double forward, double strafe, double rotate) {
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower  = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower  = forward + strafe - rotate;

        double maxPower = 1.0;
        double maxSpeed = 1.0;

        maxPower = Math.max(maxPower, Math.abs(frontLeftPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        maxPower = Math.max(maxPower, Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backRightPower));

        leftFront.setPower( maxSpeed * (frontLeftPower / maxPower));
        leftBack.setPower( maxSpeed * (backLeftPower / maxPower));
        rightFront.setPower( maxSpeed * (frontRightPower / maxPower));
        rightBack.setPower( maxSpeed * (backRightPower / maxPower));

        telemetry.addData("FL", frontLeftPower);
        telemetry.addData("FR", frontRightPower);
        telemetry.addData("BL", backLeftPower);
        telemetry.addData("BR", backRightPower);
        telemetry.update();
    }

    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);

        theta = AngleUnit.normalizeRadians(theta -
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS));
        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta);

        newStrafe = newStrafe * 1.1; // <-- scales strafing to counter imperfect mecanum movement

        this.drive(newForward, newStrafe, rotate);
    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        this.driveFieldRelative(forward, strafe, rotate);

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