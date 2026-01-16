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


@Configurable
@TeleOp(name = "Red TeleOp - Heading Lock", group = "TeleOp")
public class RedTeleopHeadingLock extends OpMode {

    // Pedro Pathing
    private static Follower follower;
    public static Pose startingPose;

    public static Pose parkPose = new Pose(38, 33, Math.toRadians(90));
    public static Pose resetPose = new Pose(8.710509477450213, 8.119694519935345, Math.toRadians(180));

    // Goal position for heading lock (Red basket)
    private static final double GOAL_X = 144.0;
    private static final double GOAL_Y = 144.0;

    // Heading lock PID constants (tunable in code)
    private static final double HEADING_KP = 0.02;
    private static final double HEADING_KI = 0.0;
    private static final double HEADING_KD = 0.002;
    private static final double TURN_DEADZONE = 0.1;
    private static final double MAX_HEADING_CORRECTION = 0.6;

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Slow mode
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private double turningMultiplier = 0.4;

    // Heading lock state
    private boolean headingLockEnabled = false;
    private double targetHeading = 0.0;
    private double headingIntegral = 0.0;
    private double lastHeadingError = 0.0;
    private long lastHeadingUpdateTime = 0;

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
        telemetry.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
        lastHeadingUpdateTime = System.nanoTime();
        // Initialize target heading to current heading
        targetHeading = follower.getPose().getHeading();
    }

    @Override
    public void loop() {
        // ========== UPDATE ALL SYSTEMS ==========
        follower.update();
        scoringAction.update();
        kicker.update();

        // ========== AUTO PARK HANDLING ==========
        if (gamepad1.backWasPressed()) {
            isAutoPark = true;
            headingLockEnabled = false; // Disable heading lock during auto park
            follower.holdPoint(parkPose);
        }

        // Cancel auto park if driver touches any stick
        if (isAutoPark) {
            boolean driverInput = Math.abs(gamepad1.left_stick_y) > 0.1 ||
                    Math.abs(gamepad1.left_stick_x) > 0.1 ||
                    Math.abs(gamepad1.right_stick_x) > 0.1;

            if (driverInput) {
                isAutoPark = false;
                follower.startTeleopDrive();
            }
        }

        // ========== HEADING LOCK TOGGLE ==========
        // D-pad up = Toggle heading lock to goal
        if (gamepad1.dpadUpWasPressed()) {
            headingLockEnabled = !headingLockEnabled;

            if (headingLockEnabled) {
                // Calculate angle to goal when enabling
                Pose currentPose = follower.getPose();
                double robotX = currentPose.getX();
                double robotY = currentPose.getY();

                targetHeading = Math.atan2(GOAL_X - robotX, GOAL_Y - robotY);

                // Reset PID state
                headingIntegral = 0.0;
                lastHeadingError = 0.0;
            }
        }

        // ========== DRIVETRAIN CONTROL WITH HEADING LOCK ==========
        if (!isAutoPark) {
            double speedMultiplier = slowMode ? slowModeMultiplier : 1.0;

            double forwardPower = -gamepad1.left_stick_y * speedMultiplier;
            double strafePower = -gamepad1.left_stick_x * speedMultiplier;
            double turnPower = -gamepad1.right_stick_x;

            // Check if driver is actively rotating
            boolean driverRotating = Math.abs(turnPower) > TURN_DEADZONE;

            if (headingLockEnabled && !driverRotating) {
                // Apply heading lock - calculate correction using PID
                Pose currentPose = follower.getPose();
                double currentHeading = currentPose.getHeading();

                // Recalculate target heading to goal in real-time
                double robotX = currentPose.getX();
                double robotY = currentPose.getY();
                targetHeading = Math.atan2(GOAL_X - robotX, GOAL_Y - robotY);

                // Calculate heading error (shortest path)
                double headingError = angleWrap(targetHeading - currentHeading);

                // Calculate PID correction
                long currentTime = System.nanoTime();
                double dt = (currentTime - lastHeadingUpdateTime) / 1e9; // Convert to seconds
                lastHeadingUpdateTime = currentTime;

                // PID terms
                double proportional = headingError * HEADING_KP;
                headingIntegral += headingError * dt * HEADING_KI;
                double derivative = HEADING_KD * (headingError - lastHeadingError) / dt;

                lastHeadingError = headingError;

                // Calculate total correction
                double headingCorrection = proportional + headingIntegral + derivative;

                // Clamp correction
                headingCorrection = Math.max(-MAX_HEADING_CORRECTION,
                        Math.min(MAX_HEADING_CORRECTION, headingCorrection));

                // Apply correction as rotation
                follower.setTeleOpDrive(
                        forwardPower,
                        strafePower,
                        headingCorrection,
                        false  // Field-centric
                );
            } else {
                // Normal manual control or driver is rotating
                if (driverRotating) {
                    // Update target heading when driver rotates
                    targetHeading = follower.getPose().getHeading();
                    headingIntegral = 0.0;  // Reset integral when driver takes control
                }

                follower.setTeleOpDrive(
                        forwardPower,
                        strafePower,
                        turnPower * speedMultiplier * turningMultiplier,
                        false  // Field-centric
                );
            }
        }

        // ========== SLOW MODE CONTROL ==========
        if (gamepad1.rightStickButtonWasPressed()) {
            slowMode = !slowMode;
        }

        // ========== RESET POSE ==========
        if (gamepad1.leftStickButtonWasPressed()) {
            follower.setPose(resetPose);
            isAutoPark = false;
            headingLockEnabled = false;
        }

        // ========== INTAKE CONTROL ==========
        if (gamepad1.right_trigger > 0.5) {
            intake.startIntake();
        } else if (gamepad1.left_trigger > 0.5) {
            intake.startOuttake();
        } else {
            intake.stop();
        }

        // ========== SHOOTER PRESET CONTROL ==========
        if (gamepad1.rightBumperWasPressed()) {
            if (shooter.getCurrentShotMode() == Shooter.ShotMode.NEAR) {
                shooter.turnOff();
            } else {
                shooter.setNearShot();
            }
        }

        if (gamepad1.leftBumperWasPressed()) {
            if (shooter.getCurrentShotMode() == Shooter.ShotMode.FAR) {
                shooter.turnOff();
            } else {
                shooter.setFarShot();
            }
        }

        if (gamepad1.yWasPressed()) {
            shooter.turnOff();
        }

        // ========== KICKER CONTROL ==========
        if (gamepad1.aWasPressed()) {
            kicker.pulse();
        }

        if (gamepad1.bWasPressed()) {
            kicker.retract();
        }

        // ========== AUTO SCORING SEQUENCE ==========
        if (gamepad1.xWasPressed() && !scoringAction.isScoring()) {
            scoringAction.startScoring();
        }

        if (gamepad1.startWasPressed() && scoringAction.isScoring()) {
            scoringAction.stopScoring();
        }

        // ========== TELEMETRY ==========
        Pose currentPose = follower.getPose();
        telemetry.addData("Heading Lock", headingLockEnabled ? "ENABLED" : "Disabled");
        telemetry.addData("Current Heading", "%.1f°", Math.toDegrees(currentPose.getHeading()));
        telemetry.addData("Target Heading", "%.1f°", Math.toDegrees(targetHeading));
        telemetry.addData("Heading Error", "%.1f°", Math.toDegrees(angleWrap(targetHeading - currentPose.getHeading())));
        telemetry.addData("Slow Mode", slowMode ? "ON" : "OFF");
        telemetry.addData("Auto Park", isAutoPark ? "ACTIVE" : "Inactive");
        telemetry.update();
    }

    /**
     * Wraps angle to [-PI, PI] range for shortest path calculation
     */
    private double angleWrap(double angle) {
        while (angle > Math.PI) angle -= 2 * Math.PI;
        while (angle < -Math.PI) angle += 2 * Math.PI;
        return angle;
    }

    @Override
    public void stop() {
        scoringAction.stopScoring();
    }
}