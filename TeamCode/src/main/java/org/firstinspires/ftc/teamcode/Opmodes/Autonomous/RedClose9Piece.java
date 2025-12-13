package org.firstinspires.ftc.teamcode.Opmodes.Autonomous;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Configurable
@Autonomous(name = "RedClose9Piece", group = "Autonomous")
public class RedClose9Piece extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState;
    private Paths paths;
    private ElapsedTime shooterTimer;

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Starting pose
    private static final Pose startPose = new Pose(122, 121, Math.toRadians(45));
    // Shooter spinup time
    private static final double SHOOTER_SPINUP_TIME = 3.0; // seconds to reach speed

    // Store end pose for teleop continuity
    public static Pose autoEndPose = null;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Initialize Pedro Pathing
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        // Initialize timer
        shooterTimer = new ElapsedTime();

        // Initialize subsystems
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        kicker = new Kicker(hardwareMap);
        scoringAction = new ScoringAction(intake, shooter, kicker);

        // Build paths
        paths = new Paths(follower);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void start() {
        // Start shooter spinning immediately
        shooter.setNearShot();
        shooterTimer.reset();

        pathState = 0;
        setPathState(pathState);
    }

    @Override
    public void loop() {
        follower.update();
        scoringAction.update(); // Update scoring state machine every loop
        kicker.update(); // Update kicker for pulse timing
        pathState = autonomousPathUpdate();

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("Scoring State", scoringAction.getCurrentState());
        panelsTelemetry.debug("Shooter Velocity", shooter.getCurrentVelocity());
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", Math.toDegrees(follower.getPose().getHeading()));
        panelsTelemetry.update(telemetry);
    }

    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Wait for shooter to spin up, then drive to basket
                if (shooterTimer.seconds() >= SHOOTER_SPINUP_TIME) {
                    follower.followPath(paths.ScorePreload, true);
                    setPathState(1);
                }
                break;

            case 1: // Drive to score preload
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    setPathState(2);
                }
                break;

            case 2: // Wait for scoring sequence to complete
                if (!scoringAction.isScoring()) {
                    // Scoring complete, go pickup ball 1
                    intake.startIntake();
                    follower.followPath(paths.Pickup1Part1, true);
                    setPathState(3);
                }
                break;

            case 3: // Continue to pickup 1
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1Part2, true);
                    setPathState(4);
                }
                break;

            case 4: // Drive back to score pickup 1
                if (!follower.isBusy()) {
                    intake.stop();
                    follower.followPath(paths.ScorePickup1, true);
                    setPathState(5);
                }
                break;

            case 5: // Score pickup 1
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    setPathState(6);
                }
                break;

            case 6: // Wait for scoring sequence to complete
                if (!scoringAction.isScoring()) {
                    // Scoring complete, go pickup ball 2
                    intake.startIntake();
                    follower.followPath(paths.Pickup2Part1, true);
                    setPathState(7);
                }
                break;

            case 7: // Continue to pickup 2
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup2Part2, true);
                    setPathState(8);
                }
                break;

            case 8: // Drive back to score pickup 2
                if (!follower.isBusy()) {
                    intake.stop();
                    follower.followPath(paths.ScorePickup2, true);
                    setPathState(9);
                }
                break;

            case 9: // Score pickup 2
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    setPathState(10);
                }
                break;

            case 10: // Wait for scoring sequence to complete
                if (!scoringAction.isScoring()) {
                    // Scoring complete, turn off shooter and leave
                    shooter.turnOff();
                    follower.followPath(paths.Leave, true);
                    setPathState(11);
                }
                break;

            case 11: // Drive to leave position
                if (!follower.isBusy()) {
                    autoEndPose = follower.getPose();
                    setPathState(12);
                }
                break;

            case 12: // Complete
                break;
        }

        return pathState;
    }

    public void setPathState(int state) {
        pathState = state;
    }

    @Override
    public void stop() {
        scoringAction.stopScoring();
        shooter.turnOff();
        autoEndPose = follower.getPose();
    }

    public static class Paths {
        public PathChain ScorePreload;
        public PathChain Pickup1Part1;
        public PathChain Pickup1Part2;
        public PathChain ScorePickup1;
        public PathChain Pickup2Part1;
        public PathChain Pickup2Part2;
        public PathChain ScorePickup2;
        public PathChain Leave;

        public Paths(Follower follower) {
            ScorePreload = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(107.645, 135.673), new Pose(84.085, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(50))
                    .build();

            Pickup1Part1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(84.085, 83.882), new Pose(102.973, 83.678))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                    .build();

            Pickup1Part2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(102.973, 83.678), new Pose(130.189, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            ScorePickup1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(130.189, 83.882), new Pose(84.100, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                    .build();

            Pickup2Part1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(84.100, 83.882), new Pose(84.000, 60.500))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(0))
                    .build();

            Pickup2Part2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(84.000, 60.500), new Pose(133, 60.118))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            ScorePickup2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(129.173, 60.118), new Pose(84.085, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(84.085, 83.882), new Pose(94.000, 73.500))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(-45))
                    .build();
        }
    }
}