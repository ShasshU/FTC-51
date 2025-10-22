package org.firstinspires.ftc.teamcode.opmodes.autonomous;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;
import org.firstinspires.ftc.teamcode.subsystems.ScoringAction.Shoot3;

@Autonomous()
public class BlueNearPreload extends LinearOpMode {

    private MecanumDrive drive;
    private Flywheel flywheel;
    private Kicker kicker1, kicker2;
    private Shoot3 scoringSequence;
    private IMU imu;

    @Override
    public void runOpMode() throws InterruptedException {

        // ===== INIT SUBSYSTEMS =====
        flywheel = new Flywheel(hardwareMap);
        kicker1 = new Kicker(hardwareMap);
        kicker2 = new Kicker(hardwareMap);

        kicker1.setServoPos1(0.3);
        kicker2.setServoPos2(0.5);

        scoringSequence = new Shoot3(flywheel, kicker1, kicker2);

        // ===== INIT DRIVE =====
        Pose2d startPose = new Pose2d(-55, -55, Math.toRadians(225));
        drive = new MecanumDrive(hardwareMap, startPose);

        imu = hardwareMap.get(IMU.class,"imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

        waitForStart();
        if (isStopRequested()) return;

        // ===== TRAJECTORY =====
        Action sequence1 = drive.actionBuilder(startPose)
                .lineToX(-10)
                .waitSeconds(4)
                .build();

        Actions.runBlocking(sequence1);

        // ===== SCORING SEQUENCE =====
        scoringSequence.start();

        while (scoringSequence.isRunning() && opModeIsActive()) {
            scoringSequence.update();
            idle();
        }
    }
}