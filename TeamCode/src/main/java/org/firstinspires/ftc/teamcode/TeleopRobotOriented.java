package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction.Shoot3;

@TeleOp(name="TeleopRobotOriented", group="TeleOp")
public class TeleopRobotOriented extends LinearOpMode {

    // Drive motors
    private DcMotor leftFront, rightFront, leftBack, rightBack;

    // Subsystems
    private Flywheel flywheel;
    private Intake intake;
    private Kicker kicker1, kicker2;

    // State variables
    private boolean lastA = false;

    double forward, strafe, rotate;
    private Shoot3 ScoringSequence;
    private boolean lastDpadDown = false; //used for scoring action, change later

    @Override
    public void runOpMode() {
        // ===== HARDWARE MAP =====
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        flywheel = new Flywheel(hardwareMap);
        intake = new Intake(hardwareMap);
        kicker1 = new Kicker(hardwareMap);
        kicker2 = new Kicker(hardwareMap);

        // Keep your working motor directions — no changes here
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.REVERSE);

        ScoringSequence = new Shoot3(flywheel, kicker1, kicker2);

        // Initialize servos
        kicker1.setServoPos1(0.3);
        kicker2.setServoPos2(0.5);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // ===== DRIVE CONTROL =====
            forward = -gamepad1.left_stick_y;
            strafe = gamepad1.left_stick_x;
            rotate = gamepad1.right_stick_x;

            drive(forward, strafe, rotate);   // ✅ this line makes it move now

            // ===== INTAKE TOGGLE =====
            if (gamepad1.a && !lastA) {
                intake.toggleIntake();
            }
            lastA = gamepad1.a;

            if (ScoringSequence.isRunning()) {
                ScoringSequence.update();
            }

            double targetVelocity = flywheel.findFlyWheelVelocity(gamepad1);

            // ===== FLYWHEEL CONTROL =====
            //flywheel.setVelocity(gamepad1.right_bumper ? 250 : 0);
            flywheel.setVelocity(targetVelocity);

            if (gamepad1.dpad_down && !lastDpadDown) {
                ScoringSequence.start(); // start the full 3→2→1 sequence
            }
            lastDpadDown = gamepad1.dpad_down;

            // ===== KICKER LOGIC =====
            if (gamepad1.dpad_up) {  // both
                kicker1.setServoPos1(0.85);
                sleep(1000);
                kicker2.setServoPos2(0.9);
                sleep(1000);
                kicker1.setServoPos1(0.3);
                kicker2.setServoPos2(0.5);
            }

            if (gamepad1.dpad_right) {  // kicker2 only
                kicker2.setServoPos2(0.9);
                sleep(200);
                kicker2.setServoPos2(0.5);
            }

            if (gamepad1.dpad_left) {  // kicker1 only
                kicker1.setServoPos1(1);
                sleep(1000);
                kicker1.setServoPos1(0.3);
            }
        }
    }



    // ----- DRIVE METHOD -----
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
}