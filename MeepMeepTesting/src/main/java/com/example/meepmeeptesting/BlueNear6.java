package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class BlueNear6 {

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();
        Pose2d startPos = new Pose2d(-55, -55, Math.toRadians(225));
        myBot.runAction(myBot.getDrive().actionBuilder(startPos)

//                .splineTo(new Vector2d(-15,-15), Math.toRadians(225))
                .lineToX(-10)
                .splineToLinearHeading(new Pose2d(-12.5,-34,Math.toRadians(270)), Math.toRadians(90))
                                .lineToY(-62)
                                .lineToY(-30)
                .splineToLinearHeading(new Pose2d(-10,-10,Math.toRadians(225)), Math.toRadians(225))

//                .splineToLinearHeading(new Pose2d(-15,-15,Math.toRadians(225)), Math.toRadians(270))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
