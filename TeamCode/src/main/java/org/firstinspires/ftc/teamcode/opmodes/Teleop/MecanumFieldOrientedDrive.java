package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@TeleOp(name="MecanumFieldOrientedDrive", group="TeleOp") // FIELD ORIENTATED
public class MecanumFieldOrientedDrive extends OpMode {
    MecanumDrive drive;
    private IMU imu;
    double forward, strafe, rotate;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, new Pose2d(0,0,0));

        imu = hardwareMap.get(IMU.class,"imu");

        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP); //change depending on robot

        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));

    }

    @Override
    public void loop() {
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        drive.driveFieldRelative(forward, strafe, rotate, imu);

    }
}
