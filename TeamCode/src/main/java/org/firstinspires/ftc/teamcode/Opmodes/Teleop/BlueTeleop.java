package org.firstinspires.ftc.teamcode.Opmodes.Teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import static org.firstinspires.ftc.teamcode.Opmodes.Autonomous.BlueClose9Piece.autoEndPose;


@Configurable
@TeleOp(name = "Blue TeleOp", group = "TeleOp")
public class BlueTeleop extends OpMode {

    // Pedro Pathing
    private static Follower follower;
    public static Pose startingPose;

    public static Pose parkPose = new Pose(105, 38, Math.toRadians(90));
    public static Pose resetPose = new Pose(72, 72, Math.toRadians(90));

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Slow mode
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private double turningMultiplier = 0.6;  // Reduce turning speed to 40%

    // Auto park state
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
        kicker = new Kicker(hardwareMap);
        scoringAction = new ScoringAction(intake, shooter, kicker);

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
    }

    @Override
    public void loop() {
        // ========== UPDATE ALL SYSTEMS ==========
        follower.update();
        scoringAction.update(); // CRITICAL: Update scoring state machine every loop
        kicker.update(); // CRITICAL: Update kicker for automatic pulse retracting

        // ========== AUTO PARK HANDLING ==========
        // Back button = Auto drive to park
        if (gamepad1.backWasPressed()) {
            isAutoPark = true;
            follower.holdPoint(parkPose);
        }

        // Cancel auto park if driver touches any stick
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
                    false  // false = field-oriented ON
            );
        }

        // ========== SLOW MODE CONTROL ==========
        // Right stick button = Toggle slow mode on/off
        if (gamepad1.rightStickButtonWasPressed()) {
            slowMode = !slowMode;
        }

        // ========== RESET POSE ==========
        // Left stick button = Reset pose to known position
        if (gamepad1.leftStickButtonWasPressed()) {
            follower.setPose(resetPose);
            isAutoPark = false; // Cancel auto park if resetting
        }

        // ========== INTAKE CONTROL ==========
        // Right trigger = Intake
        if (gamepad1.right_trigger > 0.5) {
            intake.startIntake();
        }
        // Left trigger = Outtake (reverse)
        else if (gamepad1.left_trigger > 0.5) {
            intake.startOuttake();
        }
        // No triggers = Stop intake
        else {
            intake.stop();
        }

        // ========== SHOOTER PRESET CONTROL ==========
        // Right bumper = Near shot (press once to toggle on/off)
        if (gamepad1.right_bumper) {
            if (shooter.getCurrentShotMode() == Shooter.ShotMode.NEAR) {
                shooter.turnOff();
            } else {
                shooter.setNearShot();
            }
        }

        // Left bumper = Far shot (press once to toggle on/off)
        if (gamepad1.left_bumper) {
            if (shooter.getCurrentShotMode() == Shooter.ShotMode.FAR) {
                shooter.turnOff();
            } else {
                shooter.setFarShot();
            }
        }

        // Y button = Manual stop shooter
        if (gamepad1.yWasPressed()) {
            shooter.turnOff();
        }

        // ========== KICKER CONTROL ==========
        // A button = Automatic kick pulse (extend → wait → retract)
        if (gamepad1.aWasPressed()) {
            kicker.pulse();
        }

        // B button = Manual retract (emergency/override)
        if (gamepad1.bWasPressed()) {
            kicker.retract();
        }

        // ========== AUTO SCORING SEQUENCE ==========
        // X button = Start automatic 3-ball scoring sequence
        if (gamepad1.xWasPressed() && !scoringAction.isScoring()) {
            scoringAction.startScoring();
        }

        // Start button = Emergency stop scoring sequence
        if (gamepad1.startWasPressed() && scoringAction.isScoring()) {
            scoringAction.stopScoring();
        }

    }

    @Override
    public void stop() {
        scoringAction.stopScoring(); // Stops intake, shooter, and kicker
    }
}