package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import static java.lang.Thread.sleep;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;

@TeleOp(name="Teleop", group="TeleOp") // FIELD ORIENTATED
public class Teleop extends OpMode {


    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;

    private Flywheel flywheel;
    private Intake intake;

    private Kicker kicker1;
    private Kicker kicker2;


    private boolean lastA = false;



    private IMU imu;
    double forward, strafe, rotate;

    @Override
    public void init() {
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


        imu = hardwareMap.get(IMU.class,"imu");

        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP); //change depending on robot

        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        kicker2.setServoPos(0.4);
        kicker1.setServoPos(0.6);



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
            intake.toggleIntake();
        }
        lastA = gamepad1.a;

        flywheel.setVelocity(gamepad1.right_bumper ? 250 : 0);

        if (gamepad1.b) {
            kicker1.setServoPos(0.2);
            try {
                sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            kicker2.setServoPos(0.8);

        }

        if (gamepad1.y) {
            kicker1.setServoPos(0.6);
            kicker2.setServoPos(0.4);
        }
    }

}