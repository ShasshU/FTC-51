package org.firstinspires.ftc.teamcode.opmodes.autonomous;//package org.firstinspires.ftc.teamcode.opmodes.Autonomous;
//package com.example.meepmeeptesting;


import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Actions;
//import com.acmerobotics.roadrunner.MeepMeep;
//import com.acmerobotics.roadrunner.roadrunner.DefaultBotBuilder;
//import com.acmerobotics.roadrunner.roadrunner.entity.RoadRunnerBotEntity;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Blue leave auto")
public class AutoBlueLeave extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(60, 22, 0));

        waitForStart();

        Actions.runBlocking(
                drive.actionBuilder(new Pose2d(60, 22, 0))
                        .lineToX(30)
                        .build());


     


    }

  
}
