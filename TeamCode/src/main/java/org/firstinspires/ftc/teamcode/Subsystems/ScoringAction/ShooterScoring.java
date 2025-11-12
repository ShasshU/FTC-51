package org.firstinspires.ftc.teamcode.Subsystems.ScoringAction;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

public class ShooterScoring {

    private Intake intake;
    private Shooter shooter;
    private ElapsedTime timer;

    public enum ScoringState {
        IDLE,           // Not scoring
        REVERSING,      // Reverse both systems to clear
        SPINNING_UP,    // Spin up shooter to target velocity
        FEEDING,        // Feed balls into shooter
        COMPLETE        // Scoring complete
    }

    private ScoringState currentState = ScoringState.IDLE;

    // Timing constants (in seconds)
    private static final double REVERSE_DURATION = 0.5;
    private static final double SPINUP_DURATION = 0.8;
    private static final double FEEDING_DURATION = 3.0;


    private static final double REVERSE_POWER = -0.2;
    private static final double FEEDING_POWER = 1.0;

    public ShooterScoring(Intake intake, Shooter shooter) {
        this.intake = intake;
        this.shooter = shooter;
        this.timer = new ElapsedTime();
    }


    public void startScoring() {
        if (currentState == ScoringState.IDLE) {
            currentState = ScoringState.REVERSING;
            timer.reset();
        }
    }


    public void update() {
        switch (currentState) {
            case IDLE:
                // Do nothing, waiting for startScoring() to be called
                break;

            case REVERSING:
                // Reverse both shooter and intake
                intake.setPower(REVERSE_POWER);
                shooter.setVelocity(-500);  // Reverse shooter at moderate speed

                if (timer.seconds() >= REVERSE_DURATION) {
                    // Move to spinning up phase
                    currentState = ScoringState.SPINNING_UP;
                    timer.reset();
                }
                break;

            case SPINNING_UP:
                // Stop intake, set shooter to NEAR shot and let it spin up
                intake.stop();
                shooter.setNearShot();  // Set NEAR shot velocity here

                if (timer.seconds() >= SPINUP_DURATION) {
                    // Move to feeding phase
                    currentState = ScoringState.FEEDING;
                    timer.reset();
                }
                break;

            case FEEDING:
                // Feed balls into shooter at max speed
                intake.setPower(FEEDING_POWER);
                // Shooter continues running at target velocity

                if (timer.seconds() >= FEEDING_DURATION) {
                    // Scoring complete
                    currentState = ScoringState.COMPLETE;
                    stopScoring();
                }
                break;

            case COMPLETE:
                // Scoring finished, return to idle
                currentState = ScoringState.IDLE;
                break;
        }
    }


    public void stopScoring() {
        intake.stop();
        shooter.turnOff();
        currentState = ScoringState.IDLE;
    }


    public boolean isScoring() {
        return currentState != ScoringState.IDLE && currentState != ScoringState.COMPLETE;
    }


    public ScoringState getCurrentState() {
        return currentState;
    }


    public double getStateTime() {
        return timer.seconds();
    }
}