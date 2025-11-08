package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Adjuster {
    private static Servo adjuster;


    public Adjuster(HardwareMap hardwareMap){
        adjuster = hardwareMap.get(Servo.class, "adjuster");
    }

    public static void setServoPos1(double angle) {
        adjuster.setPosition(angle);
    }
    public void setServoPos(double angle) {
    adjuster.setPosition(angle);
}
}
