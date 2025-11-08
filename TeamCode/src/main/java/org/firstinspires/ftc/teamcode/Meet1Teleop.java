package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Adjuster;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction.CloseShot;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction.FarShot;

@TeleOp(name = "Meet1TeleOp", group = "TeleOp")
public class Meet1Teleop extends LinearOpMode {

    // Drive motors
    private DcMotor leftFront, rightFront, leftBack, rightBack;

    // Subsystems
    private Flywheel flywheel; //out take
    private Intake intake;
//    private Kicker kicker1, kicker2;

    private Adjuster adjuster;
    // State variables
    private boolean lastA = false;

    private IMU imu;
    double forward, strafe, rotate;

    private CloseShot scoringSequenceClose;

    private FarShot scoringSequenceFar;

    private boolean lastLeftBumper = false;

    private boolean lastRightBumper = false;

    @Override
    public void runOpMode() {
        // Hardware mapping
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        flywheel = new Flywheel(hardwareMap);
        intake = new Intake(hardwareMap);
//        kicker1 = new Kicker(hardwareMap);
//        kicker2 = new Kicker(hardwareMap);
        adjuster = new Adjuster(hardwareMap);

        // Motor directions (match your working code)
        rightBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.FORWARD);
        leftBack.setDirection(DcMotor.Direction.FORWARD);
        rightFront.setDirection(DcMotor.Direction.REVERSE);
//
//        scoringSequenceClose = new CloseShot(flywheel, kicker1, kicker2);
//        scoringSequenceFar = new FarShot(flywheel, kicker1, kicker2);

        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        // Initialize servos
//        kicker1.setServoPos1(0.3);
//        kicker2.setServoPos2(0.5);
        adjuster.setServoPos(0.3);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // ====== DRIVE CONTROL ======
            forward = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            rotate = gamepad1.right_stick_x;

            driveFieldRelative(forward, 0.5*strafe, rotate);

            // ====== INTAKE TOGGLE ======
            if (gamepad1.right_trigger > 0.1) {
                intake.toggleIntake();
            }
            else{
                intake.setPower(0);
            }

            // ====== FLYWHEEL CONTROL ======
            flywheel.setVelocity(gamepad1.right_bumper ? 250 : 0);

            // ===== FLYWHEEL CONTROL =====
            // Only use manual bumper control if scoring sequence is NOT running
            if (!scoringSequenceClose.isRunning()) {
                double targetVelocity = flywheel.findFlyWheelVelocity(gamepad1);
                flywheel.setVelocity(targetVelocity);
            }

            // ===== SCORING SEQUENCE =====
            if (gamepad1.left_bumper && !lastLeftBumper) {
                scoringSequenceClose.start();
            }
            lastLeftBumper = gamepad1.left_bumper;

            if (scoringSequenceClose.isRunning()) {
                scoringSequenceClose.update();
            }

            if (!scoringSequenceFar.isRunning()) {
                double targetVelocity = flywheel.findFlyWheelVelocity(gamepad1);
                flywheel.setVelocity(targetVelocity);
            }

            if (gamepad1.right_bumper && !lastRightBumper) {
                scoringSequenceFar.start();
            }
            lastRightBumper = gamepad1.right_bumper;

            if (scoringSequenceFar.isRunning()) {
                scoringSequenceFar.update();
            }

            if (!scoringSequenceFar.isRunning()) {
                double targetVelocity = flywheel.findFlyWheelVelocity(gamepad1);
                flywheel.setVelocity(targetVelocity);
            }



            // ===== SCORING SEQUENCE =====
            if (gamepad1.left_trigger > 0.1) {
                intake.toggleOuttake();
            }
            else {
                intake.setPower(0);
            }


            // ===== MANUAL KICKER CONTROL =====
//            if (gamepad1.dpad_up) {
//                kicker1.setServoPos1(0.85);
//                sleep(1000);
//                kicker2.setServoPos2(0.9);
//                sleep(1000);
//                kicker1.setServoPos1(0.3);
//                kicker2.setServoPos2(0.5);
//            }
//
//            if (gamepad1.dpad_right) {
//                kicker2.setServoPos2(0.9);
//                sleep(200);
//                kicker2.setServoPos2(0.5);
//            }
//
//            if (gamepad1.dpad_left) {
//                kicker1.setServoPos1(1);
//                sleep(200);
//                kicker1.setServoPos1(0.3);
//            }

            if (gamepad1.back) {
                imu.resetYaw();
            }

            if (gamepad1.b) {
                flywheel.setVelocity(0);
            }

            telemetry.addData("Yaw (deg)", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
            telemetry.update();
        }
    }

    // ----- DRIVE METHODS -----
    public void drive(double forward, double strafe, double rotate) {
        double frontLeftPower = forward + strafe + rotate;
        double backLeftPower = forward - strafe + rotate;
        double frontRightPower = forward - strafe - rotate;
        double backRightPower = forward + strafe - rotate;

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

        double theta = Math.atan2(forward, strafe);
        double r = Math.hypot(strafe, forward);
        theta = AngleUnit.normalizeRadians(theta - heading);

        double newForward = r * Math.sin(theta);
        double newStrafe = r * Math.cos(theta) * 1.1;  // Strafing correction

        drive(newForward, newStrafe, rotate);
    }
}