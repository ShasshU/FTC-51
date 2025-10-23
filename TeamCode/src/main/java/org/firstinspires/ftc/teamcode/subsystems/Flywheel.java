package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.TeleopRobotOriented;

public class Flywheel {
    private boolean outtakeOn = false;
    private static DcMotorEx flywheel1;
    private static DcMotorEx flywheel2;
    double NEAR_VELOCITY = 200;
    double FAR_VELOCITY  = 225;
    double OFF_VELOCITY  = 0;
    public enum ShotMode { OFF, NEAR, FAR }
    ShotMode currentShotMode = ShotMode.OFF;
    boolean lastLeftBumper = false;
    boolean lastRightBumper = false;
    boolean lastBButton = false;

    public Flywheel(HardwareMap hardwareMap){
        this.flywheel1 = hardwareMap.get(DcMotorEx.class, "flywheel1");
        this.flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");

        this.flywheel2.setDirection(DcMotorEx.Direction.REVERSE);

        this.flywheel1.setVelocityPIDFCoefficients(100,0.01,0, 0.1);
        this.flywheel1.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        this.flywheel2.setVelocityPIDFCoefficients(100,0.01,0, 0.1);
        this.flywheel2.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
    }
    public void toggleOuttake(){
        outtakeOn = !outtakeOn;
        flywheel1.setPower(outtakeOn ? 220 : 0);
        flywheel2.setPower(outtakeOn ? 220 : 0);
    }
    public static void setVelocity(double vel){
        flywheel1.setVelocity(vel);
        flywheel2.setVelocity(vel);
    }


    public double findFlyWheelVelocity(Gamepad gamepad1)
    {
        if (gamepad1.left_bumper && !lastLeftBumper) {
            currentShotMode = ShotMode.NEAR;
        }
        if (gamepad1.right_bumper && !lastRightBumper) {
            currentShotMode = ShotMode.FAR;
        }
        // Optional OFF toggle (via button B for example)
        if (gamepad1.b && !lastBButton) {
            currentShotMode = ShotMode.OFF;
        }

        // Update last states
        lastLeftBumper = gamepad1.left_bumper;
        lastRightBumper = gamepad1.right_bumper;
        lastBButton = gamepad1.b;

        // Determine velocity
        return getShotVelocity(currentShotMode);
    }

    public double getShotVelocity(ShotMode shotMode) {
        switch (shotMode) {
            case NEAR:
                return NEAR_VELOCITY;
            case FAR:
                return FAR_VELOCITY;
            case OFF:
            default:
                return OFF_VELOCITY;
        }
    }
}
