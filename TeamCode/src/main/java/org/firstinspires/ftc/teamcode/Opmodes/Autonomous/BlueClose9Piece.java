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
@Autonomous(name = "BlueClose9Piece", group = "Autonomous")
public class BlueClose9Piece extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState;
    private Paths paths;

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction ScoringAction;

    // Starting pose
    private static final Pose startPose = new Pose(36.355, 135.673, Math.toRadians(90));

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
        ScoringAction = new ScoringAction(intake, shooter, kicker);

        // Build paths
        paths = new Paths(follower);

        panelsTelemetry.debug("Status", "Initialized");
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
        ScoringAction.update(); // Update scoring state machine every loop
        pathState = autonomousPathUpdate();

    }

    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Drive to score preload
                if (!follower.isBusy()) {
                    ScoringAction.startScoring();
                    setPathState(1);
                }
                break;

            case 1: // Wait for scoring sequence to complete
                if (!ScoringAction.isScoring()) {
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
                    ScoringAction.startScoring();
                    setPathState(5);
                }
                break;

            case 5: // Wait for scoring sequence to complete
                if (!ScoringAction.isScoring()) {
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
                    ScoringAction.startScoring();
                    setPathState(9);
                }
                break;

            case 9: // Wait for scoring sequence to complete
                if (!ScoringAction.isScoring()) {
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
        ScoringAction.stopScoring();
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
                            new BezierLine(new Pose(36.355, 135.673), new Pose(59.915, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(135))
                    .build();

            Pickup1Part1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(59.915, 83.882), new Pose(41.027, 83.678))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
                    .build();

            Pickup1Part2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(41.027, 83.678), new Pose(13.811, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            ScorePickup1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(13.811, 83.882), new Pose(59.900, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
                    .build();

            Pickup2Part1 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(59.900, 83.882), new Pose(60.000, 60.500))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(180))
                    .build();

            Pickup2Part2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(60.000, 60.500), new Pose(14.827, 60.118))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            ScorePickup2 = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(14.827, 60.118), new Pose(59.915, 83.882))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(135))
                    .build();

            Leave = follower
                    .pathBuilder()
                    .addPath(
                            new BezierLine(new Pose(59.915, 83.882), new Pose(50.000, 73.500))
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(225))
                    .build();
        }
    }
}