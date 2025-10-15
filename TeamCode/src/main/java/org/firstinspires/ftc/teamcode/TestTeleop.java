package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;

@TeleOp(name="TestTeleop", group="TeleOp")
public class TestTeleop extends LinearOpMode {

    // Drive motors
    private DcMotor leftFront, rightFront, leftBack, rightBack;

    // Subsystems
    private Flywheel flywheel;
    private Intake intake;
    private Kicker kicker1, kicker2;

    // State variables
    private boolean lastA = false;
    private boolean lastRB = false;

    private IMU imu;
    double forward, strafe, rotate;

    @Override
    public void runOpMode() {
        // Hardware mapping
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        flywheel = new Flywheel(hardwareMap);
        intake = new Intake(hardwareMap);
        kicker1 = new Kicker(hardwareMap);
        kicker2 = new Kicker(hardwareMap);

        // Motor directions (match your working code)
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        leftFront.setDirection(DcMotor.Direction.REVERSE);

        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        // Initialize servos
        kicker1.setServoPos1(0.3);
        kicker2.setServoPos2(0.5);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // ====== DRIVE CONTROL ======
            forward = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            rotate = gamepad1.right_stick_x;

            driveFieldRelative(forward, strafe, rotate);

            // ====== INTAKE TOGGLE ======
            if (gamepad1.a && !lastA) {
                intake.toggleIntake();
            }
            lastA = gamepad1.a;

            // ====== FLYWHEEL CONTROL ======
            flywheel.setVelocity(gamepad1.right_bumper ? 250 : 0);

            // ====== KICKER LOGIC ======
            // D-pad UP sequence (shoot both)
            //2 balls
            if (gamepad1.dpad_up) {
                kicker1.setServoPos1(0.85);
                sleep(1000);
                kicker2.setServoPos2(0.9);
                sleep(1000);
                kicker1.setServoPos1(0.3);
                kicker2.setServoPos2(0.5);
            }

            // D-pad RIGHT (only kicker2)
            // 3 ball
            if (gamepad1.dpad_right) {
                kicker2.setServoPos2(0.9);
                sleep(200);
                kicker2.setServoPos2(0.5);
            }

            // D-pad LEFT (only kicker1)
            // 1 balls
            if (gamepad1.dpad_left) {
                kicker1.setServoPos1(1);
                sleep (200);
                kicker1.setServoPos1(0.3);
            }

            // Reset heading if needed
            if (gamepad1.left_bumper) {
                imu.resetYaw();
            }

            telemetry.addData("Yaw (deg)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.update();
        }
    }

    // ----- DRIVE METHODS -----

    public void drive(double forward, double strafe, double rotate) {
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower  = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower  = forward + strafe - rotate;

        double maxPower = Math.max(1.0,
                Math.max(Math.abs(frontLeftPower),
                        Math.max(Math.abs(backLeftPower),
                                Math.max(Math.abs(frontRightPower), Math.abs(backRightPower)))));

        leftFront.setPower(frontLeftPower / maxPower);
        leftBack.setPower(backLeftPower / maxPower);
        rightFront.setPower(frontRightPower / maxPower);
        rightBack.setPower(backRightPower / maxPower);
    }

    public void driveFieldRelative(double forward, double strafe, double rotate) {
        double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

        // Correct atan2 order and coordinate math
        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(forward, strafe);
        theta = AngleUnit.normalizeRadians(theta - heading);

        double newForward = r * Math.cos(theta);
        double newStrafe  = r * Math.sin(theta) * 1.1;  // Strafing correction

        drive(newForward, newStrafe, rotate);
    }
}
