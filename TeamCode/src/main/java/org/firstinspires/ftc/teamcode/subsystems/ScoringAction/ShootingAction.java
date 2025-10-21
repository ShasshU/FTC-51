package org.firstinspires.ftc.teamcode.subsystems.ScoringAction;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;


import org.firstinspires.ftc.teamcode.subsystems.Flywheel;

public class ShootingAction {

    private boolean outtakeOn = false;
    private static DcMotorEx flywheel1;
    private static DcMotorEx flywheel2;
    double NEAR_VELOCITY = 200;
    double FAR_VELOCITY  = 225;
    double OFF_VELOCITY  = 0;
    public enum ShotMode { OFF, NEAR, FAR }
    Flywheel.ShotMode currentShotMode = Flywheel.ShotMode.OFF;
    boolean lastLeftBumper = false;
    boolean lastRightBumper = false;
    boolean lastBButton = false;

    private static DcMotor intake;
    private boolean intakeOn = false;
    private static Servo kicker1;
    private static Servo kicker2;

    public ShootingAction(HardwareMap hardwareMap){
        this.flywheel1 = hardwareMap.get(DcMotorEx.class, "flywheel1");
        this.flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");

        this.flywheel2.setDirection(DcMotorEx.Direction.REVERSE);

        this.flywheel1.setVelocityPIDFCoefficients(100,0.01,0, 0.1);
        this.flywheel1.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        this.flywheel2.setVelocityPIDFCoefficients(100,0.01,0, 0.1);
        this.flywheel2.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        intake = hardwareMap.get(DcMotor.class, "intake");

        kicker1 = hardwareMap.get(Servo.class, "kicker1");
        kicker2 = hardwareMap.get(Servo.class, "kicker2");
    }


    public class Shoot3Balls implements Action{


        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            if(!initialized) {
                flywheel1.ShotMode(NEAR_VELOCITY);
            }
            return false;
        }
    }


}
