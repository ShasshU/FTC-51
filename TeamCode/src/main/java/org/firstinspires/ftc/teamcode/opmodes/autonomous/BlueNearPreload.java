package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.TeleopRobotOriented;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;

@Autonomous()
public class BlueNearPreload extends LinearOpMode {

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
    private Flywheel flywheel;
    private Intake intake;
    private Kicker kicker1, kicker2;

    public class intakeOn implements InstantFunction {
        @Override
        public void run() {
            Intake.setPower(0.7);
        }

    }
    public class intakeOff implements InstantFunction {
        @Override
        public void run() {
            Intake.setPower(0.0);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(180));
        drive = new MecanumDrive(hardwareMap, startPose);
        flywheel = new Flywheel(hardwareMap);
        intake = new Intake(hardwareMap);
        kicker1 = new Kicker(hardwareMap);
        kicker2 = new Kicker(hardwareMap);


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
        kicker1.setServoPos1(0.3);
        kicker2.setServoPos2(0.5);

        Pose2d startPos = new Pose2d(-28, 22, Math.toRadians(180));
        Action sequence1 = drive.actionBuilder(startPos)
                .lineToX(10)
                .waitSeconds(4)
//                .splineTo(new Vector2d(-15,-15), Math.toRadians(225))
//                .splineTo(new Vector2d(35,38), Math.toRadians(90))
//                .splineTo(new Vector2d(35,50), Math.toRadians(90))
//                .splineToLinearHeading(new Pose2d(14,15,Math.toRadians(90)), Math.toRadians(270))
//                .stopAndAdd(new intakeOn())
//                .splineToLinearHeading(new Pose2d(12,55,Math.toRadians(90)), Math.toRadians(270))
//                .stopAndAdd(new intakeOff())
//                .splineToLinearHeading(new Pose2d(-15,-15,Math.toRadians(225)), Math.toRadians(270))
                .build();

//
//            .lineToX(20)
//                .turn(Math.toRadians(90))
//                .lineToY(20)
//                .turn(Math.toRadians(90))
//                .lineToX(0)
//                .turn(Math.toRadians(90))
//                .lineToY(0)
//                .turn(Math.toRadians(90))
//                .build();


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
        //Actions.runBlocking(sequence1);

        double targetVelocity = flywheel.getShotVelocity(Flywheel.ShotMode.NEAR);

        telemetry.addData("Status", targetVelocity);
        telemetry.update();
        // ===== FLYWHEEL CONTROL =====
        flywheel.setVelocity(targetVelocity);
        sleep(3000);
        //shoot 3 artifact
        kicker1.setServoPos1(0.85);
        sleep(1000);
        kicker2.setServoPos2(0.9);
        sleep(1000);
        kicker1.setServoPos1(0.3);
        kicker2.setServoPos2(0.5);

        sleep(1000);
        kicker2.setServoPos2(0.9);
        sleep(200);
        kicker2.setServoPos2(0.5);

        sleep(1000);
        kicker1.setServoPos1(1);
        sleep(1000);
        kicker1.setServoPos1(0.3);

// Execute the second trajectory sequence after the first one completes
        //Actions.runBlocking(sequence2);

    }

}
