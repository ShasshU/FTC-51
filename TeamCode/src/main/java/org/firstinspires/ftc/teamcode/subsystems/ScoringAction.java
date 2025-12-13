package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.util.ElapsedTime;

public class ScoringAction {

    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ElapsedTime timer;

    public enum ScoringState {
        IDLE,
        FEEDING_1,             // Start intake, wait for ball 1 to feed
        PAUSE_1,               // Pause intake briefly
        KICK_1,                // Kick first ball
        FEEDING_2,             // Resume intake, wait for ball 2 to feed
        PAUSE_2,               // Pause intake briefly
        KICK_2,                // Kick second ball
        FEEDING_3,             // Resume intake, wait for ball 3 to feed
        PAUSE_3,               // Pause intake briefly
        KICK_3,                // Kick third ball
        WAITING_FINAL_KICK,    // Wait for final kick pulse to complete
        COMPLETE
    }

    private ScoringState currentState = ScoringState.IDLE;

    // BASELINE TIMINGS - Tune these values after testing
    private static final double FEEDING_DURATION = 1;       // Time for ball to feed
    private static final double PAUSE_DURATION = 0.3;         // Brief pause before kick (tune this!)

    // Power settings
    private static final double INTAKE_FEEDING_POWER = 0.7;   // Intake power during feeding

    public ScoringAction(Intake intake, Shooter shooter, Kicker kicker) {
        this.intake = intake;
        this.shooter = shooter;
        this.kicker = kicker;
        this.timer = new ElapsedTime();
    }

    /**
     * Start the 3-ball scoring sequence
     * Make sure flywheel is already spinning before calling this!
     */
    public void startScoring() {
        if (currentState == ScoringState.IDLE) {
            currentState = ScoringState.FEEDING_1;
            timer.reset();
        }
    }

    /**
     * Update the state machine - call this in your loop
     */
    public void update() {
        // CRITICAL: Also update kicker for pulse timing
        kicker.update();

        switch (currentState) {
            case IDLE:
                // Do nothing, waiting for startScoring()
                break;

            case FEEDING_1:
                // Start intake and wait for first ball to feed
                intake.setPower(INTAKE_FEEDING_POWER);

                if (timer.seconds() >= FEEDING_DURATION) {
                    currentState = ScoringState.PAUSE_1;
                    timer.reset();
                }
                break;

            case PAUSE_1:
                // Pause intake briefly to prevent jamming
                intake.stop();

                if (timer.seconds() >= PAUSE_DURATION) {
                    currentState = ScoringState.KICK_1;
                    timer.reset();
                }
                break;

            case KICK_1:
                // Kick first ball (automatic pulse)
                kicker.pulse();
                currentState = ScoringState.FEEDING_2;
                timer.reset();
                break;

            case FEEDING_2:
                // Resume intake, wait for ball 2 to feed
                intake.setPower(INTAKE_FEEDING_POWER);

                if (timer.seconds() >= FEEDING_DURATION) {
                    currentState = ScoringState.PAUSE_2;
                    timer.reset();
                }
                break;

            case PAUSE_2:
                // Pause intake briefly
                intake.stop();

                if (timer.seconds() >= PAUSE_DURATION) {
                    currentState = ScoringState.KICK_2;
                    timer.reset();
                }
                break;

            case KICK_2:
                // Kick second ball (automatic pulse)
                kicker.pulse();
                currentState = ScoringState.FEEDING_3;
                timer.reset();
                break;

            case FEEDING_3:
                // Resume intake, wait for ball 3 to feed
                intake.setPower(INTAKE_FEEDING_POWER);

                if (timer.seconds() >= FEEDING_DURATION) {
                    currentState = ScoringState.PAUSE_3;
                    timer.reset();
                }
                break;

            case PAUSE_3:
                // Pause intake briefly
                intake.stop();

                if (timer.seconds() >= PAUSE_DURATION) {
                    currentState = ScoringState.KICK_3;
                    timer.reset();
                }
                break;

            case KICK_3:
                // Kick third ball (automatic pulse)
                kicker.pulse();
                currentState = ScoringState.WAITING_FINAL_KICK;
                timer.reset();
                break;

            case WAITING_FINAL_KICK:
                // Wait for final kick pulse to complete
                if (timer.seconds() >= (Kicker.PULSE_DURATION_MS / 1000.0)) {
                    stopScoring();
                    currentState = ScoringState.COMPLETE;
                }
                break;

            case COMPLETE:
                // Sequence complete, return to IDLE
                currentState = ScoringState.IDLE;
                break;
        }
    }

    /**
     * Stop all subsystems (except shooter - driver controls that)
     */
    public void stopScoring() {
        intake.stop();
        kicker.retract();
        currentState = ScoringState.IDLE;
    }

    /**
     * Check if currently scoring
     */
    public boolean isScoring() {
        return currentState != ScoringState.IDLE && currentState != ScoringState.COMPLETE;
    }

    /**
     * Get current state
     */
    public ScoringState getCurrentState() {
        return currentState;
    }

    /**
     * Get elapsed time in current state
     */
    public double getStateTime() {
        return timer.seconds();
    }

    /**
     * Get total estimated sequence time
     */
    public static double getTotalSequenceTime() {
        return (FEEDING_DURATION * 3) + (PAUSE_DURATION * 3) + (Kicker.PULSE_DURATION_MS / 1000.0 * 3);
    }
}