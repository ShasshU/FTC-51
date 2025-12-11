package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Kicker {

    private Servo kicker;

    public static final double RETRACTED_POSITION = 0.0;  // Servo pulled back
    public static final double EXTENDED_POSITION = 1.0;   // Servo pushed out to kick

    public static final double MID_POSITION = 0.5;

    private double currentPosition = RETRACTED_POSITION;

    public Kicker(HardwareMap hardwareMap) {
        kicker = hardwareMap.get(Servo.class, "kicker");
        retract();
    }

    public void extend() {
        kicker.setPosition(EXTENDED_POSITION);
        currentPosition = EXTENDED_POSITION;
    }

    public void retract() {
        kicker.setPosition(RETRACTED_POSITION);
        currentPosition = RETRACTED_POSITION;
    }

    public void mid() {
        kicker.setPosition(MID_POSITION);
        currentPosition = MID_POSITION;
    }

    public void setPosition(double position) {
        kicker.setPosition(position);
        currentPosition = position;
    }

    public double getPosition() {
        return currentPosition;
    }

    public boolean isExtended() {
        return Math.abs(currentPosition - EXTENDED_POSITION) < 0.01;
    }

    public boolean isRetracted() {
        return Math.abs(currentPosition - RETRACTED_POSITION) < 0.01;
    }
}