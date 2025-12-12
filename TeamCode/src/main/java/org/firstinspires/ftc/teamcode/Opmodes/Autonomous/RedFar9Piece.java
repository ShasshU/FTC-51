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

import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Configurable
@Autonomous(name = "RedFar9Piece", group = "Autonomous")
public class RedFar9Piece extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState;
    private Paths paths;

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Starting pose - RED FAR SIDE (mirrored from blue)
    private static final Pose startPose = new Pose(88.000, 8.000, Math.toRadians(90));

    // Store end pose for teleop continuity
    public static Pose autoEndPose = null;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // Initialize Pedro Pathing
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startPose);

        // Initialize subsystems
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);
        kicker = new Kicker(hardwareMap);
        scoringAction = new ScoringAction(intake, shooter, kicker);

        // Build paths
        paths = new Paths(follower);

        panelsTelemetry.debug("Status", "Initialized - Far Side");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void start() {
        pathState = 0;
        setPathState(pathState);
    }

    @Override
    public void loop() {
        follower.update();
        scoringAction.update(); // Update scoring state machine every loop
        pathState = autonomousPathUpdate();

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("Scoring State", scoringAction.getCurrentState());
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", Math.toDegrees(follower.getPose().getHeading()));
        panelsTelemetry.update(telemetry);
    }

    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Drive to score preload
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    setPathState(1);
                }
                break;

            case 1: // Wait for scoring sequence to complete
                if (!scoringAction.isScoring()) {
                    // Scoring complete, go pickup ball 1
                    intake.startIntake();
                    follower.followPath(paths.Pickup1Part1, true);
                    setPathState(2);
                }
                break;

            case 2: // Continue to pickup 1
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1Part2, true);
                    setPathState(3);
                }
                break;

            case 3: // Drive back to score pickup 1
                if (!follower.isBusy()) {
                    intake.stop();
                    follower.followPath(paths.ScorePickup1, true);
                    setPathState(4);
                }
                break;

            case 4: // Score pickup 1
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    setPathState(5);
                }
                break;

            case 5: // Wait for scoring sequence to complete
                if (!scoringAction.isScoring()) {
                    // Scoring complete, go pickup ball 2
                    intake.startIntake();
                    follower.followPath(paths.Pickup2Part1, true);
                    setPathState(6);
                }
                break;

            case 6: // Continue to pickup 2
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup2Part2, true);
                    setPathState(7);
                }
                break;

            case 7: // Drive back to score pickup 2
                if (!follower.isBusy()) {
                    intake.stop();
                    follower.followPath(paths.ScorePickup2, true);
                    setPathState(8);
                }
                break;

            case 8: // Score pickup 2
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    setPathState(9);
                }
                break;

            case 9: // Wait for scoring sequence to complete
                if (!scoringAction.isScoring()) {
                    // Scoring complete, leave
                    follower.followPath(paths.Leave, true);
                    setPathState(10);
                }
                break;

            case 10: // Drive to leave position
                if (!follower.isBusy()) {
                    autoEndPose = follower.getPose();
                    setPathState(11);
                }
                break;

            case 11: // Complete
                break;
        }

        return pathState;
    }

    public void setPathState(int state) {
        pathState = state;

        // Execute path on state entry
        switch (pathState) {
            case 0:
                follower.followPath(paths.ScorePreload, true);
                break;
        }
    }

    @Override
    public void stop() {
        scoringAction.stopScoring();
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
            // RED FAR SIDE - Mirrored coordinates (144 - x)
            ScorePreload = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(88.000, 8.000), new Pose(77.585, 18.685))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(64))
                    .build();

            Pickup1Part1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(77.585, 18.685), new Pose(118.612, 9.140))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(65), Math.toRadians(0))
                    .build();

            Pickup1Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(118.612, 9.140), new Pose(138.313, 8.124)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            ScorePickup1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(138.313, 8.124), new Pose(77.992, 18.685)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(63))
                    .build();

            Pickup2Part1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(77.992, 18.685), new Pose(118.815, 9.140))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(65), Math.toRadians(0))
                    .build();

            Pickup2Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(118.815, 9.140), new Pose(137.907, 7.921)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                    .build();

            ScorePickup2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(137.907, 7.921), new Pose(77.585, 18.685)))
                    .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(63))
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(77.585, 18.685), new Pose(117.800, 46.307))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(63), Math.toRadians(90))
                    .build();
        }
    }
}