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
@Autonomous(name = "Blue Close 15 Piece", group = "Autonomous")
public class BlueClose15Piece extends OpMode {

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

    // Starting pose - Mirrored from Red (124, 123, 42.5°) -> (20, 123, 137.5°)
    private static final Pose startPose = new Pose(20.000, 123.000, Math.toRadians(137.5));

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

            case 4: // Continue to pickup 1 part 2
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup1Part2, true);
                    pathState = 5;
                }
                break;

            case 5: // Drive back to score pickup 1
                if (!follower.isBusy()) {
                    // Keep intake running during return
                    follower.followPath(paths.ScorePickup1, true);
                    pathState = 6;
                }
                break;

            case 6: // Score pickup 1
                if (!follower.isBusy()) {
                    intake.stop();  // Stop NOW, right before scoring
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
                    intake.startIntake();  // Start intake for gate opening
                    follower.followPath(paths.OpenGatePart1, true);
                    pathState = 9;
                }
                break;

            case 9: // Continue opening gate and intaking
                if (!follower.isBusy()) {
                    // Keep intake running to collect gate balls
                    follower.followPath(paths.OpenGateandIntake, true);
                    pathState = 10;
                }
                break;

            case 10: // Drive back to score gate balls
                if (!follower.isBusy()) {
                    // Keep intake running during return
                    follower.followPath(paths.ScoreGate, true);
                    pathState = 11;
                }
                break;

            case 11: // Score gate balls
                if (!follower.isBusy()) {
                    intake.stop();  // Stop NOW, right before scoring
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

            case 13: // Wait after scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    intake.startIntake();
                    follower.followPath(paths.Pickup2Part1, true);
                    pathState = 14;
                }
                break;

            case 14: // Continue to pickup 2
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup2Part2, true);
                    pathState = 15;
                }
                break;

            case 15: // Drive back to score pickup 2
                if (!follower.isBusy()) {
                    // Keep intake running during return
                    follower.followPath(paths.ScorePickup2, true);
                    pathState = 16;
                }
                break;

            case 16: // Score pickup 2
                if (!follower.isBusy()) {
                    intake.stop();  // Stop NOW, right before scoring
                    scoringAction.startScoring();
                    pathState = 17;
                }
                break;

            case 17: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    waitTimer.reset();
                    pathState = 18;
                }
                break;

            case 18: // Wait after scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    intake.startIntake();
                    follower.followPath(paths.Pickup3Part1, true);
                    pathState = 19;
                }
                break;

            case 19: // Continue to pickup 3
                if (!follower.isBusy()) {
                    follower.followPath(paths.Pickup3Part2, true);
                    pathState = 20;
                }
                break;

            case 20: // Drive back to score pickup 3
                if (!follower.isBusy()) {
                    // Keep intake running during return
                    follower.followPath(paths.ScorePickup3, true);
                    pathState = 21;
                }
                break;

            case 21: // Score pickup 3
                if (!follower.isBusy()) {
                    intake.stop();  // Stop NOW, right before scoring
                    scoringAction.startScoring();
                    pathState = 22;
                }
                break;

            case 22: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    waitTimer.reset();
                    pathState = 23;
                }
                break;

            case 23: // Wait after final scoring
                if (waitTimer.seconds() >= POST_SCORE_WAIT) {
                    shooter.turnOff();
                    follower.followPath(paths.Leave, true);
                    pathState = 24;
                }
                break;

            case 24: // Drive to leave
                if (!follower.isBusy()) {
                    autoEndPose = follower.getPose();
                    pathState = 25;
                }
                break;

            case 25: // Complete
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
        public PathChain OpenGatePart1;
        public PathChain OpenGateandIntake;
        public PathChain ScoreGate;
        public PathChain Pickup2Part1;
        public PathChain Pickup2Part2;
        public PathChain ScorePickup2;
        public PathChain Pickup3Part1;
        public PathChain Pickup3Part2;
        public PathChain ScorePickup3;
        public PathChain Leave;

        public Paths(Follower follower) {
            // Red: (124, 123, 42.5°) -> (84.085, 83.882, 42.5°)
            // Blue: (20, 123, 137.5°) -> (59.915, 83.882, 137.5°)
            ScorePreload = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(20.000, 123.000), new Pose(59.915, 83.882)))
                    .setLinearHeadingInterpolation(Math.toRadians(137.5), Math.toRadians(137.5))
                    .build();

            // Red: (84.085, 83.882, 42.5°) -> (84.085, 58.693, 0°)
            // Blue: (59.915, 83.882, 137.5°) -> (59.915, 58.693, 180°)
            Pickup1Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.915, 83.882), new Pose(59.915, 58.693)))
                    .setLinearHeadingInterpolation(Math.toRadians(137.5), Math.toRadians(180))
                    .build();

            // Red: (84.085, 58.693, 0°) -> (136.273, 58.863, 0°)
            // Blue: (59.915, 58.693, 180°) -> (7.727, 58.863, 180°)
            Pickup1Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.915, 58.693), new Pose(7.727, 58.863)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            // Red: (136.273, 58.863, 0°) -> (84.410, 84.078, 42.5°)
            // Blue: (7.727, 58.863, 180°) -> (59.590, 84.078, 137.5°)
            ScorePickup1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(7.727, 58.863), new Pose(59.590, 84.078)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(137.5))
                    .build();

            // Red: (84.410, 84.078, 42.5°) -> (121.522, 58.561, 0°)
            // Blue: (59.590, 84.078, 137.5°) -> (22.478, 58.561, 180°)
            OpenGatePart1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.590, 84.078), new Pose(22.478, 58.561)))
                    .setLinearHeadingInterpolation(Math.toRadians(137.5), Math.toRadians(180))
                    .build();

            // Red: (121.522, 58.561, 0°) -> (133.585, 60.868, 35°)
            // Blue: (22.478, 58.561, 180°) -> (10.415, 60.868, 145°)
            OpenGateandIntake = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(22.478, 58.561), new Pose(10.415, 60.868)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(145))
                    .build();

            // Red: (133.585, 60.868, 35°) -> (84.293, 83.941, 42.5°)
            // Blue: (10.415, 60.868, 145°) -> (59.707, 83.941, 137.5°)
            ScoreGate = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(10.415, 60.868), new Pose(59.707, 83.941)))
                    .setLinearHeadingInterpolation(Math.toRadians(145), Math.toRadians(137.5))
                    .build();

            // Red: (84.293, 83.941, 42.5°) -> (95.878, 83.946, 0°)
            // Blue: (59.707, 83.941, 137.5°) -> (48.122, 83.946, 180°)
            Pickup2Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.707, 83.941), new Pose(48.122, 83.946)))
                    .setLinearHeadingInterpolation(Math.toRadians(137.5), Math.toRadians(180))
                    .build();

            // Red: (95.878, 83.946, 0°) -> (134.766, 83.254, 0°)
            // Blue: (48.122, 83.946, 180°) -> (9.234, 83.254, 180°)
            Pickup2Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(48.122, 83.946), new Pose(9.234, 83.254)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            // Red: (134.766, 83.254, 0°) -> (84.302, 84.205, 42.5°)
            // Blue: (9.234, 83.254, 180°) -> (59.698, 84.205, 137.5°)
            ScorePickup2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(9.234, 83.254), new Pose(59.698, 84.205)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(137.5))
                    .build();

            // Red: (84.302, 84.205, 42.5°) -> (93.541, 34.468, 0°)
            // Blue: (59.698, 84.205, 137.5°) -> (50.459, 34.468, 180°)
            Pickup3Part1 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.698, 84.205), new Pose(50.459, 34.468)))
                    .setLinearHeadingInterpolation(Math.toRadians(137.5), Math.toRadians(180))
                    .build();

            // Red: (93.541, 34.468, 0°) -> (137.605, 34.576, 0°)
            // Blue: (50.459, 34.468, 180°) -> (6.395, 34.576, 180°)
            Pickup3Part2 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(50.459, 34.468), new Pose(6.395, 34.576)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            // Red: (137.605, 34.576, 0°) -> (84.293, 83.941, 42.5°)
            // Blue: (6.395, 34.576, 180°) -> (59.707, 83.941, 137.5°)
            ScorePickup3 = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(6.395, 34.576), new Pose(59.707, 83.941)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(137.5))
                    .build();

            // Red: (84.293, 83.941, 42.5°) -> (95.029, 73.722, 42.5°)
            // Blue: (59.707, 83.941, 137.5°) -> (48.971, 73.722, 137.5°)
            Leave = follower
                    .pathBuilder()
                    .addPath(new BezierLine(new Pose(59.707, 83.941), new Pose(48.971, 73.722)))
                    .setLinearHeadingInterpolation(Math.toRadians(137.5), Math.toRadians(137.5))
                    .build();
        }
    }
}