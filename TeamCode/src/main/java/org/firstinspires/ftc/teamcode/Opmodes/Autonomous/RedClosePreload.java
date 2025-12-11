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
@Autonomous(name = "RedClosePreload", group = "Autonomous")
public class RedClosePreload extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState;

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Poses - RED ALLIANCE (mirrored from blue)
    private static final Pose startPose = new Pose(107.645, 135.673, Math.toRadians(90));
    private static final Pose scorePose = new Pose(84.085, 83.882, Math.toRadians(45));
    private static final Pose leavePose = new Pose(121, 72, Math.toRadians(-90));

    // Paths
    private PathChain scorePreload;
    private PathChain leave;

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
        buildPaths();

        panelsTelemetry.debug("Status", "Initialized - Preload Only");
        panelsTelemetry.update(telemetry);
    }

    public void buildPaths() {
        scorePreload = follower
                .pathBuilder()
                .addPath(new BezierLine(startPose, scorePose))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .build();

        leave = follower
                .pathBuilder()
                .addPath(new BezierLine(scorePose, leavePose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), leavePose.getHeading())
                .build();
    }

    @Override
    public void start() {
        pathState = 0;
        follower.followPath(scorePreload, true);
    }

    @Override
    public void loop() {
        follower.update();
        scoringAction.update(); // Update scoring state machine every loop
        pathState = autonomousPathUpdate();

        // Telemetry
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("Scoring State", scoringAction.getCurrentState());
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", Math.toDegrees(follower.getPose().getHeading()));
        panelsTelemetry.update(telemetry);
    }

    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0: // Drive to basket
                if (!follower.isBusy()) {
                    // Start scoring 3 preloaded balls
                    scoringAction.startScoring();
                    pathState = 1;
                }
                break;

            case 1: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    // Scoring done, leave
                    follower.followPath(leave, true);
                    pathState = 2;
                }
                break;

            case 2: // Drive to leave position
                if (!follower.isBusy()) {
                    autoEndPose = follower.getPose();
                    pathState = 3;
                }
                break;

            case 3: // Complete
                break;
        }

        return pathState;
    }

    @Override
    public void stop() {
        scoringAction.stopScoring();
        autoEndPose = follower.getPose();
    }
}