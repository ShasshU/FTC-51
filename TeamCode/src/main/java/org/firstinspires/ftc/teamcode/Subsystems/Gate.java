package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Gate {

    private Servo gate;

    // Servo positions - TUNE THESE VALUES
    public static final double OPEN_POSITION = -0.7;  // Servo pulled back
    public static final double CLOSED_POSITION = 0.15;   // Servo pushed out to kick

    // Optional intermediate position if needed
    public static final double MID_POSITION = 0.5;

    private double currentPosition = OPEN_POSITION;

    // Pulse timing
    private boolean pulseActive = false;
    private long pulseStartTime = 0;
    public static final long PULSE_DURATION_MS = 200; // How long to hold extended (tune this!)

    public Gate(HardwareMap hardwareMap) {
        gate = hardwareMap.get(Servo.class, "gate");

        // Initialize to retracted position
        close();
    }

    /**
     * Call this in your loop() to handle automatic pulse retracting
     */
    public void update() {
        if (pulseActive && (System.currentTimeMillis() - pulseStartTime >= PULSE_DURATION_MS)) {
            open();
            pulseActive = false;
        }
    }

    /**
     * Automatic kick pulse: extend → wait → retract
     * Call update() in your loop for this to work!
     */
    public void pulse() {
        close();
        pulseActive = true;
        pulseStartTime = System.currentTimeMillis();
    }

    /**
     * Extend the kicker to push/kick
     */
    public void close() {
        gate.setPosition(CLOSED_POSITION);
        currentPosition = CLOSED_POSITION;
    }

    /**
     * Retract the kicker back to starting position
     */
    public void open() {
        gate.setPosition(OPEN_POSITION);
        currentPosition = OPEN_POSITION;
        pulseActive = false; // Cancel any active pulse
    }

    /**
     * Move to intermediate position
     */
    public void mid() {
        gate.setPosition(MID_POSITION);
        currentPosition = MID_POSITION;
    }

    /**
     * Set servo to custom position (0.0 to 1.0)
     */
    public void setPosition(double position) {
        gate.setPosition(position);
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
        return Math.abs(currentPosition - CLOSED_POSITION) < 0.01;
    }

    /**
     * Check if kicker is retracted
     */
    public boolean isRetracted() {
        return Math.abs(currentPosition - OPEN_POSITION) < 0.01;
    }
}
