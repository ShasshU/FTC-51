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
@Autonomous(name = "RedClosePreload", group = "Autonomous")
public class RedClosePreload extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;
    private int pathState;
    private ElapsedTime shooterTimer;

    // Subsystems
    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ScoringAction scoringAction;

    // Poses
    private static final Pose startPose = new Pose(122, 121, Math.toRadians(45));
    private static final Pose scorePose = new Pose(84.085, 83.882, Math.toRadians(50));
    private static final Pose leavePose = new Pose(121, 72, Math.toRadians(-90));

    // Shooter spinup time
    private static final double SHOOTER_SPINUP_TIME = 3.0;

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

        // Initialize timer
        shooterTimer = new ElapsedTime();

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
        // Start shooter spinning immediately
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
            case 0: // Wait for shooter to spin up
                if (shooterTimer.seconds() >= SHOOTER_SPINUP_TIME) {
                    follower.followPath(scorePreload, true);
                    pathState = 1;
                }
                break;

            case 1: // Drive to basket
                if (!follower.isBusy()) {
                    scoringAction.startScoring();
                    pathState = 2;
                }
                break;

            case 2: // Wait for scoring to complete
                if (!scoringAction.isScoring()) {
                    shooter.turnOff();
                    follower.followPath(leave, true);
                    pathState = 3;
                }
                break;

            case 3: // Drive to leave position
                if (!follower.isBusy()) {
                    autoEndPose = follower.getPose();
                    pathState = 4;
                }
                break;

            case 4: // Complete
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
}