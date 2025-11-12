package org.firstinspires.ftc.teamcode.Opmodes.Teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.ScoringAction.ShooterScoring;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import static org.firstinspires.ftc.teamcode.Opmodes.Autonomous.BlueClose9Piece.autoEndPose;


@Configurable
@TeleOp(name = "Main TeleOp")
public class RedTeleop extends OpMode {

    // Pedro Pathing
    private static Follower follower;
    public static Pose startingPose;

    public static Pose blueParkPose = new Pose(112, 41, Math.toRadians(90));
    public static Pose redParkPose = blueParkPose.mirror();
    // Subsystems
    private Intake intake;
    private Shooter shooter;

    // Actions
    private ShooterScoring scoringAction;

    // Slow mode
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;

    @Override
    public void init() {
        // Initialize Pedro Pathing
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(autoEndPose == null ? new Pose() : autoEndPose);
        follower.update();

        // Initialize subsystems
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);

        // Initialize scoring action
        scoringAction = new ShooterScoring(intake, shooter);

        telemetry.addLine("TeleOp Initialized!");
        telemetry.update();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        // ========== UPDATE ALL SYSTEMS ==========
        follower.update();
        scoringAction.update();  // Always update scoring action state machine

        // ========== DRIVETRAIN CONTROL ==========
        double speedMultiplier = slowMode ? slowModeMultiplier : 1.0;

        follower.setTeleOpDrive(
                -gamepad1.left_stick_y * speedMultiplier,
                -gamepad1.left_stick_x * speedMultiplier,
                -gamepad1.right_stick_x * speedMultiplier,
                false
        );

        // ========== SLOW MODE CONTROL ==========
        // Right stick button = Toggle slow mode on/off
        if (gamepad1.rightStickButtonWasPressed()) {
            slowMode = !slowMode;
        }

        // ========== MANUAL CONTROL (only when not scoring) ==========
        if (!scoringAction.isScoring()) {
            // ========== INTAKE CONTROL ==========
            // Right trigger = Toggle intake on/off
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
            // Right bumper = Near shot (toggle)
            if (gamepad1.right_bumper) {
                if (shooter.getCurrentShotMode() == Shooter.ShotMode.NEAR) {
                    shooter.turnOff();
                } else {
                    shooter.setNearShot();
                }
            }

            // Left bumper = Far shot (toggle)
            if (gamepad1.left_bumper) {
                if (shooter.getCurrentShotMode() == Shooter.ShotMode.FAR) {
                    shooter.turnOff();
                } else {
                    shooter.setFarShot();
                }
            }

            if (gamepad1.backWasPressed()) {
                follower.holdPoint(redParkPose);
            }

            // ========== SCORING ACTION TRIGGER ==========
            // B button = Start scoring sequence (always uses NEAR shot)
            if (gamepad1.bWasPressed()) {
                scoringAction.startScoring();
            }
        } else {
            // ========== EMERGENCY STOP DURING SCORING ==========
            // A button = Emergency stop scoring
            if (gamepad1.aWasPressed()) {
                scoringAction.stopScoring();
            }
        }

        // ========== TELEMETRY ==========
        telemetry.addData("Slow Mode", slowMode);
        telemetry.addData("Intake State", intake.getState());
        telemetry.addData("Shooter Mode", shooter.getCurrentShotMode());
        telemetry.addData("Shooter Velocity", "%.0f ticks/sec", shooter.getCurrentVelocity());
        telemetry.addData("Scoring State", scoringAction.getCurrentState());
        telemetry.addData("State Time", "%.2f sec", scoringAction.getStateTime());
        telemetry.addLine();
        telemetry.addLine("=== CONTROLS ===");
        telemetry.addLine("Left Stick: Drive");
        telemetry.addLine("Right Stick X: Turn");
        telemetry.addLine("Right Stick Button: Toggle Slow Mode");
        telemetry.addLine("Right Trigger: Intake");
        telemetry.addLine("Left Trigger: Outtake");
        telemetry.addLine("Right Bumper: Near Shot");
        telemetry.addLine("Left Bumper: Far Shot");
        telemetry.addLine("B Button: START SCORING (NEAR)");
        telemetry.addLine("A Button: STOP SCORING");
        telemetry.update();
    }

    @Override
    public void stop() {
        intake.stop();
        shooter.turnOff();
        scoringAction.stopScoring();
    }
}