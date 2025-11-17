package org.firstinspires.ftc.teamcode.Opmodes.Autonomous;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "BlueClosePreload", group = "Autonomous")
public class BlueClosePreload extends OpMode {

    public static Follower follower;
    public static Pose autoEndPose;

    private Timer pathTimer, opmodeTimer;
    private int pathState;

    private Intake intake;
    private Shooter shooter;

    private final Pose startPose = new Pose(21.7, 125.3, Math.toRadians(135));
    private final Pose scorePose = new Pose(59.915, 83.882, Math.toRadians(130));
    private final Pose leavePose = new Pose(23, 72, Math.toRadians(-90));

    private PathChain ScorePreload;
    private PathChain Leave;

    public void buildPaths() {
        ScorePreload = follower
                .pathBuilder()
                .addPath(new BezierLine(startPose, scorePose))
                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
                .build();

        Leave = follower
                .pathBuilder()
                .addPath(new BezierLine(scorePose, leavePose))
                .setLinearHeadingInterpolation(scorePose.getHeading(), leavePose.getHeading())
                .build();
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                // Start shooter ramping up immediately
                shooter.setNearShot();
                follower.followPath(ScorePreload);
                setPathState(1);
                break;

            case 1:
                // Wait at score position for 3 seconds
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 3.0) {
                    // Start feeding balls
                    intake.startIntake();
                    setPathState(2);
                }
                break;

            case 2:
                // Run intake for enough time to shoot all 3 balls (adjust as needed)
                if (pathTimer.getElapsedTimeSeconds() > 4.0) {
                    // Stop everything and leave
                    intake.stop();
                    shooter.turnOff();
                    follower.followPath(Leave, true);
                    setPathState(3);
                }
                break;

            case 3:
                // Wait to reach leave position
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
                break;

            case -1:
            default:
                break;
        }
    }

    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();

        follower = Constants.createFollower(hardwareMap);
        intake = new Intake(hardwareMap);
        shooter = new Shooter(hardwareMap);

        buildPaths();
        follower.setStartingPose(startPose);

        telemetry.addLine("Auto Initialized!");
        telemetry.update();
    }

    @Override
    public void start() {
        pathTimer.resetTimer();
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    public void loop() {
        follower.update();
        autonomousPathUpdate();

        telemetry.addData("Path State", pathState);
        telemetry.addData("Shooter Velocity", shooter.getCurrentVelocity());
        telemetry.addData("Time", "%.1f sec", opmodeTimer.getElapsedTimeSeconds());
        telemetry.addData("X", "%.1f", follower.getPose().getX());
        telemetry.addData("Y", "%.1f", follower.getPose().getY());
        telemetry.addData("Heading", "%.1f°", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.addData("Busy", follower.isBusy());
        telemetry.update();
    }

    public void stop() {
        autoEndPose = follower.getPose();
        intake.stop();
        shooter.turnOff();
    }
}