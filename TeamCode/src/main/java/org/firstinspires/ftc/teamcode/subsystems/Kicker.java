package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Kicker {
    private Servo kicker;


    public Kicker(HardwareMap hardwareMap){
        kicker = hardwareMap.get(Servo.class, "kicker");
    }

    public void setServoPos(double angle) {
        kicker.setPosition(angle);
    }
}
