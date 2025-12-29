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

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;

@Configurable
@Autonomous(name = "RedClose12Piece", group = "Autonomous")
public class RedClose12Piece extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState;
    private Paths paths;
    private ElapsedTime shooterTimer;
    private ElapsedTime waitTimer;

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Starting pose
    private static final Pose startPose = new Pose(124, 123, Math.toRadians(45));

    // Timing constants
    private static final double SHOOTER_SPINUP_TIME = 0.25;
    private static final double POST_SCORE_WAIT = 0.3;

    // Store end pose for teleop continuity
    public static Pose autoEndPose = null;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Initialize Pedro Pathing
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        // Initialize timers
        shooterTimer = new ElapsedTime();
        waitTimer = new ElapsedTime();

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
        shooter.setNearShot();
        shooterTimer.reset();
        pathState = 0;
    }

    @Override
    public void loop() {
        follower.update();
        scoringAction.update();
        kicker.update();
        pathState = autonomousPathUpdate();

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
            case 0: // Wait for shooter to spin up
                if (shooterTimer.seconds() >= SHOOTER_SPINUP_TIME) {
                    follower.followPath(paths.ScorePreload, true);
                    pathState = 1;
                }
                break;

            case 1: // Drive to score preload
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    pathState = 2;
                }
                break;

            case 2: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    waitTimer.reset();
                    pathState = 3;
                }
                break;
            case 3:
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    follower.followPath(paths.removeBalls, true);
                    pathState = 4;
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    waitTimer.reset();
                    pathState = 5;
                }
                break;

            case 5: // Wait after scoring
                if (waitTimer.seconds() >= 1.5) {
                    intake.startIntake();
                    follower.followPath(paths.Pickup1Part1, true);
                    pathState = 6;
                }
                break;

            case 6: // Continue to pickup 1
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1Part2, true);
                    pathState = 7;
                }
                break;

            case 7: // Drive back to score pickup 1
                if (!follower.isBusy()) {
                    // Keep intake running during return (EXTENDED INTAKE TIME)
                    follower.followPath(paths.ScorePickup1, true);
                    pathState = 8;
                }
                break;

            case 8: // Score pickup 1
                if (!follower.isBusy()) {
                    intake.stop();  // Stop NOW, right before scoring
                    scoringAction.startScoring();
                    pathState = 9;
                }
                break;

            case 9: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    waitTimer.reset();
                    pathState = 10;
                }
                break;

            case 10: // Wait after scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    intake.startIntake();
                    follower.followPath(paths.Pickup2Part1, true);
                    pathState = 11;
                }
                break;

            case 11: // Continue to pickup 2
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup2Part2, true);
                    pathState = 12;
                }
                break;

            case 12: // Drive back to score pickup 2
                if (!follower.isBusy()) {
                    // Keep intake running during return (EXTENDED INTAKE TIME)
                    follower.followPath(paths.ScorePickup2, true);
                    pathState = 13;
                }
                break;

            case 13: // Score pickup 2
                if (!follower.isBusy()) {
                    intake.stop();  // Stop NOW, right before scoring
                    scoringAction.startScoring();
                    pathState = 14;
                }
                break;

            case 14: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    waitTimer.reset();
                    pathState = 15;
                }
                break;

            case 15: // Wait after final scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    shooter.turnOff();
                    follower.followPath(paths.Leave, true);
                    pathState = 16;
                }
                break;

            case 16: // Drive to leave
                if (!follower.isBusy()) {
                    autoEndPose = follower.getPose();
                    pathState = 17;
                }
                break;

            case 17: // Complete
                break;
        }

        return pathState;
    }

    @Override
    public void stop() {
        scoringAction.stopScoring();
        shooter.turnOff();
        autoEndPose = follower.getPose();
    }

    public static class Paths {
        public PathChain ScorePreload;
        public PathChain removeBalls;
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
                    .addPath(new BezierLine(new Pose(124, 123), new Pose(84.085, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(42.5))
                    .build();
            removeBalls = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(84.085, 83.882), new Pose(128, 70)))
                    .setLinearHeadingInterpolation(Math.toRadians(42.5), Math.toRadians(180))
                    .build();


            Pickup1Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(84.085, 83.882), new Pose(95, 82.678)))
                    .setLinearHeadingInterpolation(Math.toRadians(42.5), Math.toRadians(0))
                    .build();

            Pickup1Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(95, 82.678), new Pose(131.189, 82.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            ScorePickup1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(131.189, 82.882), new Pose(84.100, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(42.5))
                    .build();

            Pickup2Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(84.100, 83.882), new Pose(84.000, 57)))
                    .setLinearHeadingInterpolation(Math.toRadians(42.5), Math.toRadians(0))
                    .build();

            Pickup2Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(84.000, 57), new Pose(132, 57)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            ScorePickup2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(132, 57), new Pose(84.085, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(42.5))
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(84.085, 83.882), new Pose(94.000, 73.500)))
                    .setLinearHeadingInterpolation(Math.toRadians(42.5), Math.toRadians(-45))
                    .build();
        }
    }
}