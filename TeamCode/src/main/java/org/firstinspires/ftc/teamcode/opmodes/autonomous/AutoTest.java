package org.firstinspires.ftc.teamcode.opmodes.autonomous;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;


@Autonomous(name = "BlueFarLeave")
public class AutoTest extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException{
        Pose2d startPose = new Pose2d(60, 22, Math.toRadians(0));
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        Kicker kicker;
        Flywheel flywheel;
        Intake intake;

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(60,22,0))
                        .lineToX(30)
                        .build());


    }
}
