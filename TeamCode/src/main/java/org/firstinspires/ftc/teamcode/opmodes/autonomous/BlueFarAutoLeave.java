package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;

import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous()
public class BlueFarAutoLeave extends LinearOpMode {

    enum State
    {
        START,
        SECOND_STEP,
        FLY_WHEEL_SHOOT,
        DONE
    }

    MecanumDrive drive;
    private IMU imu;
    private State state;
    double lastTime;
    public static double DISTANCE = 20;
    Pose2d startPos;
    Pose2d lastStepPos;


    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d startPose = new Pose2d(0, 0, 0);
        drive = new MecanumDrive(hardwareMap, startPose);

        imu = hardwareMap.get(IMU.class,"imu");

        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP); //change depending on robot

        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

//        Action sequence1 = drive.actionBuilder(startPose)
//                .lineToX(DISTANCE)
//                .waitSeconds(2)
//                .turn(Math.toRadians(45))        // turn +45 degrees (counterclockwise)
//                .build();

        Pose2d startPos = new Pose2d(60, 22, 0);
        Action sequence1 = drive.actionBuilder(startPos)
                .lineToX(30)
                .build();

//        Action sequence2 = drive.actionBuilder(startPose)
//            .lineToX(20)
//                .turn(Math.toRadians(90))
//                .lineToY(20)
//                .turn(Math.toRadians(90))
//                .lineToX(0)
//                .turn(Math.toRadians(90))
//                .lineToY(0)
//                .turn(Math.toRadians(90))
//                .build();

//        // Define the second trajectory sequence
//        Action sequence2 = drive.actionBuilder(drive.getPoseEstimate())
//                .lineTo(Xnew Vector2d(60, 10))
//                .waitSeconds(2)
//                .turn(Math.toRadians(-45))
//                .strafeTo(new Vector2d(60, 0))
//                .build();

        // Wait for start
        waitForStart();
        if (isStopRequested()) {
            return;
        }

        // Execute the first trajectory sequence
        Actions.runBlocking(sequence1);

// Execute the second trajectory sequence after the first one completes
        //Actions.runBlocking(sequence2);

    }

}
