package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Kicker {

    private Servo kicker;

    // Servo positions - TUNE THESE VALUES
    public static final double RETRACTED_POSITION = 0.0;  // Servo pulled back
    public static final double EXTENDED_POSITION = 1.0;   // Servo pushed out to kick

    // Optional intermediate position if needed
    public static final double MID_POSITION = 0.5;

    private double currentPosition = RETRACTED_POSITION;

    // Pulse timing
    private boolean pulseActive = false;
    private long pulseStartTime = 0;
    public static final long PULSE_DURATION_MS = 300; // How long to hold extended (tune this!)

    public Kicker(HardwareMap hardwareMap) {
        kicker = hardwareMap.get(Servo.class, "kicker");

        // Initialize to retracted position
        retract();
    }

    /**
     * Call this in your loop() to handle automatic pulse retracting
     */
    public void update() {
        if (pulseActive && (System.currentTimeMillis() - pulseStartTime >= PULSE_DURATION_MS)) {
            retract();
            pulseActive = false;
        }
    }

    /**
     * Automatic kick pulse: extend → wait → retract
     * Call update() in your loop for this to work!
     */
    public void pulse() {
        extend();
        pulseActive = true;
        pulseStartTime = System.currentTimeMillis();
    }

    /**
     * Extend the kicker to push/kick
     */
    public void extend() {
        kicker.setPosition(EXTENDED_POSITION);
        currentPosition = EXTENDED_POSITION;
    }

    /**
     * Retract the kicker back to starting position
     */
    public void retract() {
        kicker.setPosition(RETRACTED_POSITION);
        currentPosition = RETRACTED_POSITION;
        pulseActive = false; // Cancel any active pulse
    }

    /**
     * Move to intermediate position
     */
    public void mid() {
        kicker.setPosition(MID_POSITION);
        currentPosition = MID_POSITION;
    }

    /**
     * Set servo to custom position (0.0 to 1.0)
     */
    public void setPosition(double position) {
        kicker.setPosition(position);
        currentPosition = position;
    }

    /**
     * Get current target position
     */
    public double getPosition() {
        return currentPosition;
    }

    /**
     * Check if kicker is extended
     */
    public boolean isExtended() {
        return Math.abs(currentPosition - EXTENDED_POSITION) < 0.01;
    }

    /**
     * Check if kicker is retracted
     */
    public boolean isRetracted() {
        return Math.abs(currentPosition - RETRACTED_POSITION) < 0.01;
    }
}