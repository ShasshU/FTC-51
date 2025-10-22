package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTest {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 18)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-55, 55, Math.toRadians(125)))
                .waitSeconds(2)
                .setTangent(Math.toRadians(-35))
                //.lineToX(-24)
                .splineToLinearHeading(new Pose2d(-10, 10,Math.toRadians(135)),Math.toRadians(-35))
                .waitSeconds(2.5)
                .setTangent(Math.toRadians(0))
                .splineToLinearHeading(new Pose2d(-12,24,Math.toRadians(90)),Math.toRadians(90))
                .lineToY(54)
                .setTangent(Math.toRadians(270))
                .splineToLinearHeading(new Pose2d(-10, 10,Math.toRadians(135)),Math.toRadians(90))
                .waitSeconds(2.5)
                .splineToLinearHeading(new Pose2d(-60,24,Math.toRadians(180)),Math.toRadians(180))
                //.lineToX(-24)
                .build());


        // The following are examples of what can be done with the path planning inside RoadRunner
        //
        // .splineToLinearHeading(new Pose2d( X , Y , Heading), Angle)
        //      for SplineToLinearHeading, you need to give it a "Pose2d", which is your final X, Y and heading for the robot
        //      The Angle is what angle do you want to "enter" the final position from. So, if you want to enter from the left, then you would use Math.toRadians(180)
        //
        // .splineToConstantHeading(new Pose2d( X, Y, Heading))
        //      for SplineToConstantHeading, you need to give it a "Pose2d", which is your final X, Y and heading for the robot
        //      similar to the Spline to Linear Heading, you need to give it a Pose2d but no Angle as it will just rotate smoothly as it traverses to that point.
        //
        // .setTangent(Angle)
        //      this is used to basically set the exit angle that you want to exit out of your current position at.
        //
        // .splineTo(new Vector2d( X, Y), Angle)
        //      splineTo takes in a Vector2d as well
        //
        // .strafeTo(new Vector2d( X, Y))
        //      Move the robot sideways to a given location
        //      Works similar to LineToConstantHeading but you don't have to tell it the angle you want to stay facing

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_LIGHT)
                .setDarkMode(false)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}