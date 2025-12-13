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
@Autonomous(name = "BlueClose9Piece", group = "Autonomous")
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

    // Starting pose
    private static final Pose startPose = new Pose(20, 123, Math.toRadians(135));

    // Timing constants
    private static final double SHOOTER_SPINUP_TIME = 1.5;
    private static final double POST_SCORE_WAIT = 0.5;

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

            case 3: // Wait after scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    intake.startIntake();
                    follower.followPath(paths.Pickup1Part1, true);
                    pathState = 4;
                }
                break;

            case 4: // Continue to pickup 1
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1Part2, true);
                    pathState = 5;
                }
                break;

            case 5: // Drive back to score pickup 1
                if (!follower.isBusy()) {
                    intake.stop();
                    follower.followPath(paths.ScorePickup1, true);
                    pathState = 6;
                }
                break;

            case 6: // Score pickup 1
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    pathState = 7;
                }
                break;

            case 7: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    waitTimer.reset();
                    pathState = 8;
                }
                break;

            case 8: // Wait after scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    intake.startIntake();
                    follower.followPath(paths.Pickup2Part1, true);
                    pathState = 9;
                }
                break;

            case 9: // Continue to pickup 2
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup2Part2, true);
                    pathState = 10;
                }
                break;

            case 10: // Drive back to score pickup 2
                if (!follower.isBusy()) {
                    intake.stop();
                    follower.followPath(paths.ScorePickup2, true);
                    pathState = 11;
                }
                break;

            case 11: // Score pickup 2
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    pathState = 12;
                }
                break;

            case 12: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    waitTimer.reset();
                    pathState = 13;
                }
                break;

            case 13: // Wait after final scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    shooter.turnOff();
                    follower.followPath(paths.Leave, true);
                    pathState = 14;
                }
                break;

            case 14: // Drive to leave
                if (!follower.isBusy()) {
                    autoEndPose = follower.getPose();
                    pathState = 15;
                }
                break;

            case 15: // Complete
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
                    .addPath(new BezierLine(new Pose(20, 123), new Pose(59.915, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(50))
                    .build();

            Pickup1Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.915, 83.882), new Pose(102.973, 83.678)))
                    .setLinearHeadingInterpolation(Math.toRadians(50), Math.toRadians(180))
                    .build();

            Pickup1Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(102.973, 83.678), new Pose(13.811, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            ScorePickup1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(13.811, 83.882), new Pose(59.900, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(50))
                    .build();

            Pickup2Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.900, 83.882), new Pose(60.000, 60.500)))
                    .setLinearHeadingInterpolation(Math.toRadians(50), Math.toRadians(180))
                    .build();

            Pickup2Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(60.000, 60.500), new Pose(12, 60.118)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            ScorePickup2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(14.827, 60.118), new Pose(59.915, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(50))
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.915, 83.882), new Pose(50.000, 73.500)))
                    .setLinearHeadingInterpolation(Math.toRadians(50), Math.toRadians(225))
                    .build();
        }
    }
}