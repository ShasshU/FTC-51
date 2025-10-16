package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class RedFarAutoLeave {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();
        Pose2d startPos = new Pose2d(60, -22, 0);
        myBot.runAction(myBot.getDrive().actionBuilder(startPos)
                .lineToX(50)
                .splineTo(new Vector2d(-15,15), Math.toRadians(135))
//                .splineTo(new Vector2d(35,50), Math.toRadians(90))
//                .splineToLinearHeading(new Pose2d(14,15,Math.toRadians(90)), Math.toRadians(270))
//                .splineToLinearHeading(new Pose2d(12,55,Math.toRadians(90)), Math.toRadians(270))
//                .splineToLinearHeading(new Pose2d(-15,-15,Math.toRadians(225)), Math.toRadians(270))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}