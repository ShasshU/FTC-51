package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Shooter;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;


public class ScoringAction{

    private Intake intake;
    private Shooter shooter;
    private Kicker kicker;
    private ElapsedTime timer;

    public enum ScoringState {
        IDLE,
        SPINNING_UP,           // Flywheel ramping up + slow intake
        KICK_1,                // Kick first ball
        RETRACT_1,             // Retract kicker
        FEEDING_2,             // Wait for ball 2 to feed
        KICK_2,                // Kick second ball
        RETRACT_2,             // Retract kicker
        FEEDING_3,             // Wait for ball 3 to feed
        KICK_3,                // Kick third ball
        RETRACT_3,             // Retract kicker
        COMPLETE
    }

    private ScoringState currentState = ScoringState.IDLE;

    // BASELINE TIMINGS - Tune these values after testing
    private static final double SPINUP_DURATION = 3.0;        // Time for flywheel to reach speed
    private static final double KICK_DURATION = 0.3;          // How long to hold kicker extended
    private static final double FEEDING_DURATION = 1.5;       // Time between kicks for ball to feed

    // Power settings
    private static final double INTAKE_FEEDING_POWER = 0.4;   // Slow intake power during feeding

    public ScoringAction(Intake intake, Shooter shooter, Kicker kicker) {
        this.intake = intake;
        this.shooter = shooter;
        this.kicker = kicker;
        this.timer = new ElapsedTime();
    }

    /**
     * Start the 3-ball scoring sequence
     */
    public void startScoring() {
        if (currentState == ScoringState.IDLE) {
            currentState = ScoringState.SPINNING_UP;
            timer.reset();
        }
    }

    /**
     * Update the state machine - call this in your loop
     */
    public void update() {
        switch (currentState) {
            case IDLE:
                // Do nothing, waiting for startScoring()
                break;

            case SPINNING_UP:
                // Spin up flywheel and start slow intake
                shooter.setNearShot();
                intake.setPower(INTAKE_FEEDING_POWER);

                if (timer.seconds() >= SPINUP_DURATION) {
                    currentState = ScoringState.KICK_1;
                    timer.reset();
                }
                break;

            case KICK_1:
                // Extend kicker for first shot
                kicker.extend();
                // Flywheel and intake stay running

                if (timer.seconds() >= KICK_DURATION) {
                    currentState = ScoringState.RETRACT_1;
                    timer.reset();
                }
                break;

            case RETRACT_1:
                // Retract kicker immediately
                kicker.retract();
                currentState = ScoringState.FEEDING_2;
                timer.reset();
                break;

            case FEEDING_2:
                // Wait for ball 2 to feed through intake
                // Flywheel and intake stay running

                if (timer.seconds() >= FEEDING_DURATION) {
                    currentState = ScoringState.KICK_2;
                    timer.reset();
                }
                break;

            case KICK_2:
                // Extend kicker for second shot
                kicker.extend();

                if (timer.seconds() >= KICK_DURATION) {
                    currentState = ScoringState.RETRACT_2;
                    timer.reset();
                }
                break;

            case RETRACT_2:
                // Retract kicker
                kicker.retract();
                currentState = ScoringState.FEEDING_3;
                timer.reset();
                break;

            case FEEDING_3:
                // Wait for ball 3 to feed through intake

                if (timer.seconds() >= FEEDING_DURATION) {
                    currentState = ScoringState.KICK_3;
                    timer.reset();
                }
                break;

            case KICK_3:
                // Extend kicker for third shot
                kicker.extend();

                if (timer.seconds() >= KICK_DURATION) {
                    currentState = ScoringState.RETRACT_3;
                    timer.reset();
                }
                break;

            case RETRACT_3:
                // Retract kicker and stop everything
                kicker.retract();
                stopScoring();
                currentState = ScoringState.COMPLETE;
                break;

            case COMPLETE:
                // Sequence complete, return to IDLE
                currentState = ScoringState.IDLE;
                break;
        }
    }

    /**
     * Stop all subsystems
     */
    public void stopScoring() {
        intake.stop();
        shooter.turnOff();
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
        return SPINUP_DURATION + (KICK_DURATION * 3) + (FEEDING_DURATION * 2);
    }
}