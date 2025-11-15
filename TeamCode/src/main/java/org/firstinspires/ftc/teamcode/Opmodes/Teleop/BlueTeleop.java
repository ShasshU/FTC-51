package org.firstinspires.ftc.teamcode.Opmodes.Teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
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

    // Slow mode
    private boolean slowMode = false;
    private double slowModeMultiplier = 0.5;
    private double turningMultiplier = 0.4;  // Reduce turning speed to 60%

    @Override
    public void init() {
        // Initialize Pedro Pathing
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(autoEndPose == null ? new Pose() : autoEndPose);
        follower.update();

        // Initialize subsystems
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);

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

        // ========== DRIVETRAIN CONTROL ==========
        double speedMultiplier = slowMode ? slowModeMultiplier : 1.0;

        follower.setTeleOpDrive(
                -gamepad1.left_stick_y * speedMultiplier,
                -gamepad1.left_stick_x * speedMultiplier,
                -gamepad1.right_stick_x * speedMultiplier * turningMultiplier, // Reduced turning speed
                true
        );

        // ========== SLOW MODE CONTROL ==========
        // Right stick button = Toggle slow mode on/off
        if (gamepad1.rightStickButtonWasPressed()) {
            slowMode = !slowMode;
        }

        // ========== RESET POSE ==========
        // Left stick button = Reset pose to known position
        if (gamepad1.leftStickButtonWasPressed()) {
            follower.setPose(resetPose);
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

        // Back button = Auto drive to park
        if (gamepad1.backWasPressed()) {
            follower.holdPoint(parkPose);
        }

        // ========== TELEMETRY ==========
        telemetry.addData("Slow Mode", slowMode);
        telemetry.addData("X", "%.1f", follower.getPose().getX());
        telemetry.addData("Y", "%.1f", follower.getPose().getY());
        telemetry.addData("Heading", "%.1f°", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Intake State", intake.getState());
        telemetry.addData("Shooter Mode", shooter.getCurrentShotMode());
        telemetry.addData("Shooter Velocity", "%.0f ticks/sec", shooter.getCurrentVelocity());
        telemetry.addLine();
        telemetry.addLine("=== CONTROLS ===");
        telemetry.addLine("Left Stick: Drive");
        telemetry.addLine("Right Stick X: Turn (60% speed)");
        telemetry.addLine("Right Stick Button: Toggle Slow Mode");
        telemetry.addLine("Left Stick Button: RESET POSE");
        telemetry.addLine("Right Trigger: Intake");
        telemetry.addLine("Left Trigger: Outtake");
        telemetry.addLine("Right Bumper: Toggle Near Shot");
        telemetry.addLine("Left Bumper: Toggle Far Shot");
        telemetry.addLine("Y Button: Stop Shooter");
        telemetry.addLine("X Button: Reverse Shooter (hold)");
        telemetry.addLine("Back Button: Auto Park");
        telemetry.update();
    }

    @Override
    public void stop() {
        intake.stop();
        shooter.turnOff();
    }
}