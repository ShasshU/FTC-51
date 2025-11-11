package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Shooter {

    private DcMotorEx shooter;

    public enum ShotMode {
        OFF,
        NEAR,    // Close shot
        FAR      // Far shot
    }

    private ShotMode currentShotMode = ShotMode.OFF;


    private static final double NEAR_VELOCITY = 200;    //1400
    private static final double FAR_VELOCITY = 300;     //unsure
    private static final double OFF_VELOCITY = 0.0;

    // =======================================

    // Button debouncing
    private boolean lastLeftBumper = false;
    private boolean lastRightBumper = false;
    private boolean lastBButton = false;

    // PIDF coefficients
    private static final double P = 100;
    private static final double I = 0.01;
    private static final double D = 0;
    private static final double F = 0.1;

    // Store last target
    private double lastTargetVelocity = 0;

    public Shooter(HardwareMap hardwareMap) {
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setVelocityPIDFCoefficients(P, I, D, F);
        shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        shooter.setDirection(DcMotorSimple.Direction.REVERSE);
    }

    // Call this in your OpMode loop to handle button presses
    public void updateShotMode(Gamepad gamepad) {
        // Left bumper = NEAR preset
        if (gamepad.left_bumper && !lastLeftBumper) {
            currentShotMode = ShotMode.NEAR;
        }

        // Right bumper = FAR preset
        if (gamepad.right_bumper && !lastRightBumper) {
            currentShotMode = ShotMode.FAR;
        }

        // B button = OFF
        if (gamepad.b && !lastBButton) {
            currentShotMode = ShotMode.OFF;
        }

        // Update button states
        lastLeftBumper = gamepad.left_bumper;
        lastRightBumper = gamepad.right_bumper;
        lastBButton = gamepad.b;

        // Apply the velocity
        updateVelocity();
    }

    // Update shooter velocity based on current mode
    public void updateVelocity() {
        double targetVelocity = getPresetVelocity();
        lastTargetVelocity = targetVelocity;
        setVelocity(targetVelocity);
    }

    // Get velocity for current mode
    private double getPresetVelocity() {
        switch (currentShotMode) {
            case NEAR:
                return NEAR_VELOCITY;
            case FAR:
                return FAR_VELOCITY;
            case OFF:
            default:
                return OFF_VELOCITY;
        }
    }

    // Direct control methods
    public void setNearShot() {
        currentShotMode = ShotMode.NEAR;
        updateVelocity();
    }

    public void setFarShot() {
        currentShotMode = ShotMode.FAR;
        updateVelocity();
    }

    public void turnOff() {
        currentShotMode = ShotMode.OFF;
        updateVelocity();
    }

    // Set shooter velocity (in ticks per second)
    public void setVelocity(double velocity) {
        shooter.setVelocity(velocity);
    }


    public double getCurrentVelocity() {
        return shooter.getVelocity();
    }
    public ShotMode getCurrentShotMode() {
        return currentShotMode;
    }

}