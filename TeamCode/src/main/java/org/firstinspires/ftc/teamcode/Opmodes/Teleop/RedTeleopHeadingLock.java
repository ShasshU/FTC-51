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
import static org.firstinspires.ftc.teamcode.Opmodes.Autonomous.RedClose9Piece.autoEndPose;

// Heading Lock Tuning Constants
class HeadingLockTuning {
    public static double Kp = 0.020;              // Start slightly aggressive (Pinpoint is accurate)
    public static double Ki = 0.0;                // Usually not needed
    public static double Kd = 0.002;              // Damping for smooth corrections
    public static double deadzone = 0.1;          // Right stick deadzone
    public static double maxCorrection = 0.5;     // Safety limit
    public static double minOdometryConfidence = 5.0;  // Inches from origin
}

@Configurable
@TeleOp(name = "Red Teleop Heading Lock", group = "TeleOp")
public class RedTeleopHeadingLock extends OpMode {

    // Pedro Pathing
    private static Follower follower;
    public static Pose startingPose;

    public static Pose parkPose = new Pose(38, 33, Math.toRadians(90));
    public static Pose resetPose = new Pose(72, 72, Math.toRadians(90));

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Slow mode
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private double turningMultiplier = 0.6;  // Reduce turning speed to 60%

    // Auto park state
    private boolean isAutoPark = false;

    // Heading lock state
    private boolean headingLockEnabled = true;
    private double targetHeading = 0.0;
    private double headingIntegral = 0.0;
    private double lastHeadingError = 0.0;

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
        telemetry.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();

        // Initialize target heading to current heading
        targetHeading = follower.getPose().getHeading();
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

        // ========== DRIVETRAIN CONTROL WITH HEADING LOCK ==========
        if (!isAutoPark) {
            double speedMultiplier = slowMode ? slowModeMultiplier : 1.0;

            double drive = -gamepad1.left_stick_y * speedMultiplier;
            double strafe = -gamepad1.left_stick_x * speedMultiplier;
            double turn = -gamepad1.right_stick_x;

            // Driver manually rotating?
            boolean manualRotation = Math.abs(turn) > HeadingLockTuning.deadzone;

            if (manualRotation) {
                // Manual control - update target heading
                targetHeading = follower.getPose().getHeading();
                headingIntegral = 0.0;
                turn *= turningMultiplier;
            }
            else if (headingLockEnabled && isOdometryReliable() && !follower.isBusy()) {
                // Heading lock active - maintain heading
                double currentHeading = follower.getPose().getHeading();
                double headingError = angleWrap(targetHeading - currentHeading);

                headingIntegral += headingError;
                headingIntegral = Math.max(-1.0, Math.min(1.0, headingIntegral));

                double headingDerivative = headingError - lastHeadingError;
                lastHeadingError = headingError;

                turn = (HeadingLockTuning.Kp * headingError) +
                        (HeadingLockTuning.Ki * headingIntegral) +
                        (HeadingLockTuning.Kd * headingDerivative);

                turn = Math.max(-HeadingLockTuning.maxCorrection,
                        Math.min(HeadingLockTuning.maxCorrection, turn));
            }

            follower.setTeleOpDrive(drive, strafe, turn, false);
        }

        // ========== HEADING LOCK CONTROLS ==========
        // D-pad Up = Toggle heading lock on/off
        if (gamepad1.dpadUpWasPressed()) {
            headingLockEnabled = !headingLockEnabled;
            if (headingLockEnabled) {
                targetHeading = follower.getPose().getHeading();
            }
        }

        // D-pad Right = Snap to Red scoring angle (50°)
        if (gamepad1.dpadRightWasPressed()) {
            targetHeading = Math.toRadians(50);
            headingIntegral = 0.0;
        }

        // D-pad Down = Face backward (180°)
        if (gamepad1.dpadDownWasPressed()) {
            targetHeading = Math.toRadians(180);
            headingIntegral = 0.0;
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

        // ========== TELEMETRY (Optional - helpful for tuning) ==========
        telemetry.addData("Heading Lock", headingLockEnabled ? "ON" : "OFF");
        telemetry.addData("Current Heading", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Target Heading", Math.toDegrees(targetHeading));
        telemetry.update();
    }

    // ========== HELPER METHODS ==========

    // Wraps angle to [-PI, PI] for shortest rotation
    private double angleWrap(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    // Check if odometry is reliable (robot moved >5" from origin)
    private boolean isOdometryReliable() {
        Pose currentPose = follower.getPose();
        double distanceFromOrigin = Math.sqrt(
                currentPose.getX() * currentPose.getX() +
                        currentPose.getY() * currentPose.getY()
        );
        return distanceFromOrigin > HeadingLockTuning.minOdometryConfidence;
    }

    @Override
    public void stop() {
        scoringAction.stopScoring(); // Stops intake, shooter, and kicker
    }
}