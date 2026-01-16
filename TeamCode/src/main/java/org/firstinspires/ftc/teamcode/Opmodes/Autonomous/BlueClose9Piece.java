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
@Autonomous(name = "Blue Close 9 Piece", group = "Autonomous")
public class BlueClose9Piece extends OpMode {

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

    // Timing constants
    private static final double SHOOTER_SPINUP_TIME = 0.25;
    private static final double POST_SCORE_WAIT = 0.4;

    // Store end pose for teleop continuity
    public static Pose autoEndPose = null;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Initialize Pedro Pathing
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(Paths.startPose);

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

            case 3: // Wait after scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    intake.startIntake();
                    follower.followPath(paths.Pickup1Part1, true);
                    pathState = 4;
                }
                break;

            case 4: // Continue to pickup 1 part 2
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1Part2, true);
                    pathState = 5;
                }
                break;

            case 5: // Drive to gate setup position
                if (!follower.isBusy()) {
                    // Stop intake before gate
                    intake.stop();
                    follower.followPath(paths.GateSetup, true);
                    pathState = 6;
                }
                break;

            case 6: // Open gate (no intake)
                if (!follower.isBusy()) {
                    follower.followPath(paths.GateEmpty, true);
                    pathState = 7;
                }
                break;

            case 7: // Drive back to score pickup 1
                if (!follower.isBusy()) {
                    // Start intake again for return
                    intake.startIntake();
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
                    // Keep intake running during return
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
        // Define all poses first for easy editing
        public static final Pose startPose = new Pose(20.000, 123.000, Math.toRadians(137.5));

        // Scoring poses
        public static final Pose scorePreloadPose = new Pose(58.276, 84.936, Math.toRadians(139.5));
        public static final Pose scorePickup1Pose = new Pose(58.144, 85.170, Math.toRadians(143.5));
        public static final Pose scorePickup2Pose = new Pose(58.042, 85.170, Math.toRadians(140.5));

        // Pickup 1 poses
        public static final Pose pickup1IntermediatePose = new Pose(49.000, 82.5, Math.toRadians(180));
        public static final Pose pickup1Pose = new Pose(12.694, 82.5, Math.toRadians(180));

        // Gate poses
        public static final Pose gateSetupPose = new Pose(29.766, 69.371, Math.toRadians(0));
        public static final Pose gateEmptyPose = new Pose(13.883, 69.371, Math.toRadians(0));

        // Pickup 2 poses
        public static final Pose pickup2IntermediatePose = new Pose(58.276, 59, Math.toRadians(180));
        public static final Pose pickup2Pose = new Pose(9.500, 59, Math.toRadians(180));

        // Leave pose
        public static final Pose leavePose = new Pose(44.615, 66.710, Math.toRadians(137.5));

        // PathChains
        public PathChain ScorePreload;
        public PathChain Pickup1Part1;
        public PathChain Pickup1Part2;
        public PathChain GateSetup;
        public PathChain GateEmpty;
        public PathChain ScorePickup1;
        public PathChain Pickup2Part1;
        public PathChain Pickup2Part2;
        public PathChain ScorePickup2;
        public PathChain Leave;

        public Paths(Follower follower) {
            ScorePreload = follower
                    .pathBuilder()
                    .addPath(new BezierLine(startPose, scorePreloadPose))
                    .setLinearHeadingInterpolation(startPose.getHeading(), scorePreloadPose.getHeading())
                    .build();

            Pickup1Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(scorePreloadPose, pickup1IntermediatePose))
                    .setLinearHeadingInterpolation(scorePreloadPose.getHeading(), pickup1IntermediatePose.getHeading())
                    .build();

            Pickup1Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(pickup1IntermediatePose, pickup1Pose))
                    .setLinearHeadingInterpolation(pickup1IntermediatePose.getHeading(), pickup1Pose.getHeading())
                    .build();

            GateSetup = follower
                    .pathBuilder()
                    .addPath(new BezierLine(pickup1Pose, gateSetupPose))
                    .setLinearHeadingInterpolation(pickup1Pose.getHeading(), gateSetupPose.getHeading())
                    .build();

            GateEmpty = follower
                    .pathBuilder()
                    .addPath(new BezierLine(gateSetupPose, gateEmptyPose))
                    .setLinearHeadingInterpolation(gateSetupPose.getHeading(), gateEmptyPose.getHeading())
                    .build();

            ScorePickup1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(gateEmptyPose, scorePickup1Pose))
                    .setLinearHeadingInterpolation(gateEmptyPose.getHeading(), scorePickup1Pose.getHeading())
                    .build();

            Pickup2Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(scorePickup1Pose, pickup2IntermediatePose))
                    .setLinearHeadingInterpolation(scorePickup1Pose.getHeading(), pickup2IntermediatePose.getHeading())
                    .build();

            Pickup2Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(pickup2IntermediatePose, pickup2Pose))
                    .setLinearHeadingInterpolation(pickup2IntermediatePose.getHeading(), pickup2Pose.getHeading())
                    .build();

            ScorePickup2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(pickup2Pose, scorePickup2Pose))
                    .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePickup2Pose.getHeading())
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(new BezierLine(scorePickup2Pose, leavePose))
                    .setLinearHeadingInterpolation(scorePickup2Pose.getHeading(), leavePose.getHeading())
                    .build();
        }
    }
}