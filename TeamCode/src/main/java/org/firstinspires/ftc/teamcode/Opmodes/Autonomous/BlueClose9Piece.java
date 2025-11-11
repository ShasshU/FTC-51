//package org.firstinspires.ftc.teamcode.Opmodes.Autonomous;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.Pose;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.pedropathing.geometry.BezierLine;
//import com.pedropathing.paths.PathChain;
//import com.pedropathing.util.Timer;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//
//
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//
//@Autonomous(name = "BlueClose9Piece", group = "Autonomous")
//public class BlueClose9Piece extends OpMode {
//
//    public static Follower follower;
//    public static Pose autoEndPose;
//
//    private Timer pathTimer, opmodeTimer;
//    private int pathState;
//
//    private final Pose startPose = new Pose(36.355, 135.673, Math.toRadians(90));
//    private final Pose scorePose = new Pose(59.915, 83.882, Math.toRadians(135));
//    private final Pose pickup1IntermediatePose = new Pose(41.027, 83.678, Math.toRadians(180));
//    private final Pose pickup1Pose = new Pose(13.811, 83.882, Math.toRadians(180));
//    private final Pose pickup2IntermediatePose = new Pose(60.000, 60.500, Math.toRadians(180));
//    private final Pose pickup2Pose = new Pose(14.827, 60.118, Math.toRadians(180));
//    private final Pose leavePose = new Pose(50.000, 73.500, Math.toRadians(225));
//
//    private PathChain ScorePreload;
//    private PathChain Pickup1Part1;
//    private PathChain Pickup1Part2;
//    private PathChain ScorePickup1;
//    private PathChain Pickup2Part1;
//    private PathChain Pickup2Part2;
//    private PathChain ScorePickup2;
//    private PathChain Leave;
//
//    public void buildPaths() {
//        ScorePreload = follower
//                .pathBuilder()
//                .addPath(new BezierLine(startPose, scorePose))
//                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
//                .build();
//
//        Pickup1Part1 = follower
//                .pathBuilder()
//                .addPath(new BezierLine(scorePose, pickup1IntermediatePose))
//                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1IntermediatePose.getHeading())
//                .build();
//
//        Pickup1Part2 = follower
//                .pathBuilder()
//                .addPath(new BezierLine(pickup1IntermediatePose, pickup1Pose))
//                .setLinearHeadingInterpolation(pickup1IntermediatePose.getHeading(), pickup1Pose.getHeading())
//                .build();
//
//        ScorePickup1 = follower
//                .pathBuilder()
//                .addPath(new BezierLine(pickup1Pose, scorePose))
//                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
//                .build();
//
//        Pickup2Part1 = follower
//                .pathBuilder()
//                .addPath(new BezierLine(scorePose, pickup2IntermediatePose))
//                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2IntermediatePose.getHeading())
//                .build();
//
//        Pickup2Part2 = follower
//                .pathBuilder()
//                .addPath(new BezierLine(pickup2IntermediatePose, pickup2Pose))
//                .setLinearHeadingInterpolation(pickup2IntermediatePose.getHeading(), pickup2Pose.getHeading())
//                .build();
//
//        ScorePickup2 = follower
//                .pathBuilder()
//                .addPath(new BezierLine(pickup2Pose, scorePose))
//                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
//                .build();
//
//        Leave = follower
//                .pathBuilder()
//                .addPath(new BezierLine(scorePose, leavePose))
//                .setLinearHeadingInterpolation(scorePose.getHeading(), leavePose.getHeading())
//                .build();
//    }
//
//    public void autonomousPathUpdate() {
//        switch (pathState) {
//            case 0:
//                follower.followPath(ScorePreload); //scoring preload
//                setPathState(1);
//                break;
//
//            case 1:
//                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.5) {
//                    follower.followPath(Pickup1Part1, true); //goes to pickup part 1
//                    setPathState(2);
//                }
//                break;
//
//            case 2:
//                if (!follower.isBusy()) {
//                    follower.followPath(Pickup1Part2, true); //picks up the 3 artifacts
//                    setPathState(3);
//                }
//                break;
//
//            case 3:
//                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.3) {
//                    follower.followPath(ScorePickup1, true); //scores the 3 artifiacts
//                    setPathState(4);
//                }
//                break;
//
//            case 4:
//                // Wait at basket to score sample 1
//                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.5) {
//                    follower.followPath(Pickup2Part1, true); //goes to pick up another 3 artifacts
//                    setPathState(5);
//                }
//                break;
//
//            case 5:
//                if (!follower.isBusy()) {
//                    follower.followPath(Pickup2Part2, true); //intakes 3 artifacts
//                    setPathState(6);
//                }
//                break;
//
//            case 6:
//                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.3) {
//                    follower.followPath(ScorePickup2, true); //goes to score 3 artifacts
//                    setPathState(7);
//                }
//                break;
//
//            case 7:
//                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.5) {
//                    follower.followPath(Leave, true); //leaves
//                    setPathState(8);
//                }
//                break;
//
//            case 8:
//                if (!follower.isBusy()) {
//                    setPathState(-1);
//                }
//                break;
//
//            case -1:
//            default:
//                break;
//        }
//    }
//
//    public void setPathState(int pState) {
//        pathState = pState;
//        pathTimer.resetTimer();
//    }
//
//    public void init() {
//        pathTimer = new Timer();
//        opmodeTimer = new Timer();
//
//        follower = Constants.createFollower(hardwareMap);
//        buildPaths();
//        follower.setStartingPose(startPose);
//
//        telemetry.addLine("Auto Initialized!");
//        telemetry.update();
//    }
//
//    @Override
//    public void start() {
//        pathTimer.resetTimer();
//        opmodeTimer.resetTimer();
//        setPathState(0);
//    }
//
//    public void loop() {
//        follower.update();
//        autonomousPathUpdate();
//
//        telemetry.addData("Path State", pathState);
//        telemetry.addData("Time", "%.1f sec", opmodeTimer.getElapsedTimeSeconds());
//        telemetry.addData("X", "%.1f", follower.getPose().getX());
//        telemetry.addData("Y", "%.1f", follower.getPose().getY());
//        telemetry.addData("Heading", "%.1f°", Math.toDegrees(follower.getPose().getHeading()));
//        telemetry.addData("Busy", follower.isBusy());
//        telemetry.update();
//    }
//
//    public void stop() {
//        autoEndPose = follower.getPose();
//    }
//}package org.firstinspires.ftc.teamcode.Opmodes.Autonomous;
/// /
/// /import com.pedropathing.follower.Follower;
/// /import com.pedropathing.geometry.Pose;
/// /import com.qualcomm.robotcore.eventloop.opmode.OpMode;
/// /import com.pedropathing.geometry.BezierLine;
/// /import com.pedropathing.paths.PathChain;
/// /import com.pedropathing.util.Timer;
/// /import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
/// /
/// /
/// /import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
/// /
/// /@Autonomous(name = "BlueClose9Piece", group = "Autonomous")
/// /public class BlueClose9Piece extends OpMode {
/// /
/// /    public static Follower follower;
/// /    public static Pose autoEndPose;
/// /
/// /    private Timer pathTimer, opmodeTimer;
/// /    private int pathState;
/// /
/// /    private final Pose startPose = new Pose(36.355, 135.673, Math.toRadians(90));
/// /    private final Pose scorePose = new Pose(59.915, 83.882, Math.toRadians(135));
/// /    private final Pose pickup1IntermediatePose = new Pose(41.027, 83.678, Math.toRadians(180));
/// /    private final Pose pickup1Pose = new Pose(13.811, 83.882, Math.toRadians(180));
/// /    private final Pose pickup2IntermediatePose = new Pose(60.000, 60.500, Math.toRadians(180));
/// /    private final Pose pickup2Pose = new Pose(14.827, 60.118, Math.toRadians(180));
/// /    private final Pose leavePose = new Pose(50.000, 73.500, Math.toRadians(225));
/// /
/// /    private PathChain ScorePreload;
/// /    private PathChain Pickup1Part1;
/// /    private PathChain Pickup1Part2;
/// /    private PathChain ScorePickup1;
/// /    private PathChain Pickup2Part1;
/// /    private PathChain Pickup2Part2;
/// /    private PathChain ScorePickup2;
/// /    private PathChain Leave;
/// /
/// /    public void buildPaths() {
/// /        ScorePreload = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(startPose, scorePose))
/// /                .setLinearHeadingInterpolation(startPose.getHeading(), scorePose.getHeading())
/// /                .build();
/// /
/// /        Pickup1Part1 = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(scorePose, pickup1IntermediatePose))
/// /                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup1IntermediatePose.getHeading())
/// /                .build();
/// /
/// /        Pickup1Part2 = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(pickup1IntermediatePose, pickup1Pose))
/// /                .setLinearHeadingInterpolation(pickup1IntermediatePose.getHeading(), pickup1Pose.getHeading())
/// /                .build();
/// /
/// /        ScorePickup1 = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(pickup1Pose, scorePose))
/// /                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), scorePose.getHeading())
/// /                .build();
/// /
/// /        Pickup2Part1 = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(scorePose, pickup2IntermediatePose))
/// /                .setLinearHeadingInterpolation(scorePose.getHeading(), pickup2IntermediatePose.getHeading())
/// /                .build();
/// /
/// /        Pickup2Part2 = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(pickup2IntermediatePose, pickup2Pose))
/// /                .setLinearHeadingInterpolation(pickup2IntermediatePose.getHeading(), pickup2Pose.getHeading())
/// /                .build();
/// /
/// /        ScorePickup2 = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(pickup2Pose, scorePose))
/// /                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), scorePose.getHeading())
/// /                .build();
/// /
/// /        Leave = follower
/// /                .pathBuilder()
/// /                .addPath(new BezierLine(scorePose, leavePose))
/// /                .setLinearHeadingInterpolation(scorePose.getHeading(), leavePose.getHeading())
/// /                .build();
/// /    }
/// /
/// /    public void autonomousPathUpdate() {
/// /        switch (pathState) {
/// /            case 0:
/// /                follower.followPath(ScorePreload); //scoring preload
/// /                setPathState(1);
/// /                break;
/// /
/// /            case 1:
/// /                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.5) {
/// /                    follower.followPath(Pickup1Part1, true); //goes to pickup part 1
/// /                    setPathState(2);
/// /                }
/// /                break;
/// /
/// /            case 2:
/// /                if (!follower.isBusy()) {
/// /                    follower.followPath(Pickup1Part2, true); //picks up the 3 artifacts
/// /                    setPathState(3);
/// /                }
/// /                break;
/// /
/// /            case 3:
/// /                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.3) {
/// /                    follower.followPath(ScorePickup1, true); //scores the 3 artifiacts
/// /                    setPathState(4);
/// /                }
/// /                break;
/// /
/// /            case 4:
/// /                // Wait at basket to score sample 1
/// /                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.5) {
/// /                    follower.followPath(Pickup2Part1, true); //goes to pick up another 3 artifacts
/// /                    setPathState(5);
/// /                }
/// /                break;
/// /
/// /            case 5:
/// /                if (!follower.isBusy()) {
/// /                    follower.followPath(Pickup2Part2, true); //intakes 3 artifacts
/// /                    setPathState(6);
/// /                }
/// /                break;
/// /
/// /            case 6:
/// /                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.3) {
/// /                    follower.followPath(ScorePickup2, true); //goes to score 3 artifacts
/// /                    setPathState(7);
/// /                }
/// /                break;
/// /
/// /            case 7:
/// /                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 0.5) {
/// /                    follower.followPath(Leave, true); //leaves
/// /                    setPathState(8);
/// /                }
/// /                break;
/// /
/// /            case 8:
/// /                if (!follower.isBusy()) {
/// /                    setPathState(-1);
/// /                }
/// /                break;
/// /
/// /            case -1:
/// /            default:
/// /                break;
/// /        }
/// /    }
/// /
/// /    public void setPathState(int pState) {
/// /        pathState = pState;
/// /        pathTimer.resetTimer();
/// /    }
/// /
/// /    public void init() {
/// /        pathTimer = new Timer();
/// /        opmodeTimer = new Timer();
/// /
/// /        follower = Constants.createFollower(hardwareMap);
/// /        buildPaths();
/// /        follower.setStartingPose(startPose);
/// /
/// /        telemetry.addLine("Auto Initialized!");
/// /        telemetry.update();
/// /    }
/// /
/// /    @Override
/// /    public void start() {
/// /        pathTimer.resetTimer();
/// /        opmodeTimer.resetTimer();
/// /        setPathState(0);
/// /    }
/// /
/// /    public void loop() {
/// /        follower.update();
/// /        autonomousPathUpdate();
/// /
/// /        telemetry.addData("Path State", pathState);
/// /        telemetry.addData("Time", "%.1f sec", opmodeTimer.getElapsedTimeSeconds());
/// /        telemetry.addData("X", "%.1f", follower.getPose().getX());
/// /        telemetry.addData("Y", "%.1f", follower.getPose().getY());
/// /        telemetry.addData("Heading", "%.1f°", Math.toDegrees(follower.getPose().getHeading()));
/// /        telemetry.addData("Busy", follower.isBusy());
/// /        telemetry.update();
/// /    }
/// /
/// /    public void stop() {
/// /        autoEndPose = follower.getPose();
/// /    }
/// /}