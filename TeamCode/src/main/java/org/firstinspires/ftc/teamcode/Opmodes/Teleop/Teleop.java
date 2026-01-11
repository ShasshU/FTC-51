package org.firstinspires.ftc.teamcode.Opmodes.Teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.Subsystems.Gate;
import org.firstinspires.ftc.teamcode.Subsystems.ShootingAction;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import static org.firstinspires.ftc.teamcode.Opmodes.Autonomous.BlueClose9Piece.autoEndPose;


@Configurable
@TeleOp(name = "TeleOp", group = "TeleOp")
public class Teleop extends OpMode {

    // Pedro Pathing
    private static Follower follower;
    public static Pose startingPose;

    public static Pose parkPose = new Pose(105, 38, Math.toRadians(90));
    public static Pose resetPose = new Pose(72, 72, Math.toRadians(90));

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Gate gate;
    private ShootingAction ShootingAction;

    // Slow mode
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private double turningMultiplier = 0.6;  // Reduce turning speed to 40%
    private boolean isAutoPark = false;


    @Override
    public void init() {
        // Initialize Pedro Pathing
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(autoEndPose == null ? new Pose() : autoEndPose);
        follower.update();

        // Initialize subsystems
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        gate = new Gate(hardwareMap);
        ShootingAction = new ShootingAction(intake, gate, shooter);

        telemetry.addLine("TeleOp Initialized!");
        telemetry.addData("Starting Heading", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();

        // FIX: Re-zero field orientation to current robot heading
        // This makes "wherever robot is facing now" = "forward on field"
        // Prevents reversed controls when transitioning from auto
        Pose currentPose = follower.getPose();
        follower.setPose(new Pose(currentPose.getX(), currentPose.getY(), 0));
        gate.close();
    }

    @Override
    public void loop() {
        // ========== UPDATE ALL SYSTEMS ==========
        follower.update();
        ShootingAction.update();
        gate.update();

        if (gamepad1.backWasPressed()) {
            isAutoPark = true;
            follower.holdPoint(parkPose);
        }


        if (isAutoPark) {
            boolean driverInput = Math.abs(gamepad1.left_stick_y) > 0.1 ||
                    Math.abs(gamepad1.left_stick_x) > 0.1 ||
                    Math.abs(gamepad1.right_stick_x) > 0.1;

            if (driverInput) {
                isAutoPark = false;
                follower.startTeleopDrive(); // Resume manual control
            }
        }

        // ========== DRIVETRAIN CONTROL ==========
        // Only allow manual drive if NOT in auto park
        if (!isAutoPark) {
            double speedMultiplier = slowMode ? slowModeMultiplier : 1.0;

            follower.setTeleOpDrive(
                    -gamepad1.left_stick_y * speedMultiplier,
                    -gamepad1.left_stick_x * speedMultiplier,
                    -gamepad1.right_stick_x * speedMultiplier * turningMultiplier, // Reduced turning speed
                    true  // false = field-oriented ON
            );
        }


        // ========== SLOW MODE CONTROL ==========
        // Right stick button = Toggle slow mode on/off
        if (gamepad1.rightStickButtonWasPressed()) {
            slowMode = !slowMode;
        }


        // ========== SHOOTER PRESET CONTROL ==========
        // Right bumper = Near shot (press once to toggle on/off)
        if (gamepad1.rightBumperWasPressed() && !ShootingAction.isScoring()) {
            if (shooter.getCurrentShotMode() == Shooter.ShotMode.NEAR) {
                shooter.turnOff();
            } else {
                ShootingAction.startScoringNear();
            }
        }

        // Left bumper = Far shot (press once to toggle on/off)
        if (gamepad1.leftBumperWasPressed() && !ShootingAction.isScoring()) {
            if (shooter.getCurrentShotMode() == Shooter.ShotMode.FAR) {
                shooter.turnOff();
            } else {
                ShootingAction.startScoringFar();
            }
        }
        if (!ShootingAction.isScoring()) {
            if (gamepad1.xWasPressed()) {
                intake.toggleIntake();
            }
        }

        if (!ShootingAction.isScoring()) {
            if (gamepad1.aWasPressed()) {
                gate.open();
            }
        }
        if (!ShootingAction.isScoring()) {
            if (gamepad1.bWasPressed()) {
                gate.close();
            }
        }

        // Y button = Manual stop shooter
        if (gamepad1.yWasPressed()) {
            shooter.turnOff();
        }

    }
}