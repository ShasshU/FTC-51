package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Kicker {
   private Servo kicker1;
    private Servo kicker2;


    public Kicker(HardwareMap hardwareMap){
       kicker1 = hardwareMap.get(Servo.class, "kicker1");
        kicker2 = hardwareMap.get(Servo.class, "kicker2");
    }

    public void setServoPos1(double angle) {
       kicker1.setPosition(angle);
    }
    public void setServoPos2(double angle) {
        kicker2.setPosition(angle);
    }
}
