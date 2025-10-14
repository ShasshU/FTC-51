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

@TeleOp(name="Teleop-RobotOriented", group="TeleOp") // FIELD ORIENTATED
public class TeleopRobotOriented extends LinearOpMode {


    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    private Flywheel flywheel;
    private Intake intake;


    private Kicker kicker1;
    private Kicker kicker2;


    private boolean lastA = false;
    private boolean lastRB = false;

    public int buttonY = 0;

    public int buttonB = 0;

    public int buttonX = 0;
    private IMU imu;
    double forward, strafe, rotate;

    @Override
    public void runOpMode()
    {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        flywheel = new Flywheel(hardwareMap);
        intake = new Intake(hardwareMap);
        kicker1 = new Kicker(hardwareMap);
        kicker2 = new Kicker(hardwareMap);


        rightBack.setDirection(DcMotor.Direction.FORWARD);
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);


        imu = hardwareMap.get(IMU.class, "imu");

        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP); //change depending on robot

        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        kicker2.setServoPos2(0.5);
        kicker1.setServoPos1(0.4);


        telemetry.addData("Status", "Initialized");
        waitForStart();

        while (opModeIsActive())
        {
            forward = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            rotate = gamepad1.right_stick_x;

            this.driveFieldRelative(forward, strafe, rotate);

            if (gamepad1.a && !lastA) {
                intake.toggleIntake();
            }
            lastA = gamepad1.a;

            if (gamepad1.right_bumper && !lastRB) {
                flywheel.toggleOuttake();
            }
            lastRB = gamepad1.right_bumper;

            flywheel.setVelocity(gamepad1.right_bumper ? 220 : 0);

            if (gamepad1.b) {
//                kicker2.setServoPos2(0.9);
            } else {
//                kicker2.setServoPos2(0.5);
            }

            if (gamepad1.y) {
                buttonY = 1;
//                kicker1.setServoPos1(0.9);
//                sleep(1000);
//                kicker2.setServoPos2(0.9);
            } else {
//                kicker1.setServoPos1(0.5);
//                kicker2.setServoPos2(0.5);
            }

            if (gamepad1.x) {
                buttonX = 1;
//                kicker1.setServoPos1(1);
            } else {
//                kicker1.setServoPos1(0.5);
            }

            if (buttonY == 1) {
                kicker1.setServoPos1(0.9);
                sleep(1000);
                kicker2.setServoPos2(0.9);
                sleep(1000);
                buttonY=0;

                if (buttonB ==1) {
                    kicker2.setServoPos2(0.9);
                }

                if (buttonX ==1) {
                    kicker1.setServoPos1(1);
                }

                if (buttonY == 0 & buttonB ==0 & buttonX==0
                ) {
                    kicker1.setServoPos1(0.4);
                    kicker2.setServoPos2(0.5);
                    buttonY = 0;
                }
            }


        }
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




    }}