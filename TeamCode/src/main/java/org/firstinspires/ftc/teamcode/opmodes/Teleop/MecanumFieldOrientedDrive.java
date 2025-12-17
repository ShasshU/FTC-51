package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import static java.lang.Thread.sleep;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
@TeleOp(name = "MecanumFieldOrientedDrive", group = "TeleOp")
public class MecanumFieldOrientedDrive extends LinearOpMode {

    // Drive motors
    private DcMotor leftFront, rightFront, leftBack, rightBack;

    private Turret turret;

    // State variables
    private boolean lastA = false;

    private IMU imu;
    double forward, strafe, rotate;

    private final boolean lastLeftBumper = false;

    private final boolean lastRightBumper = false;

    @Override
    public void runOpMode() {
        // Hardware mapping
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        turret = new Turret(hardwareMap);

        // Motor directions (match your working code)
        rightBack.setDirection(DcMotor.Direction.REVERSE);


        // Initialize IMU
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
        );
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        // Initialize servos


        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // ====== DRIVE CONTROL ======
            forward = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            rotate = gamepad1.right_stick_x;

            driveFieldRelative(0.7 * forward, 0.5*strafe, 0.4 * rotate);

            Turret.aimAtTarget();

            if (gamepad1.back) {
                imu.resetYaw();
            }

            telemetry.addData("Turret Pos", turret.getPosition());
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