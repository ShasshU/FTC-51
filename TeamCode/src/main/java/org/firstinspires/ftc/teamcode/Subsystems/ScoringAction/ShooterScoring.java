package org.firstinspires.ftc.teamcode.Subsystems.ScoringAction;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

public class ShooterScoring {

    private Intake intake;
    private Shooter shooter;
    private ElapsedTime timer;

    public enum ScoringState {
        IDLE,                    // Not scoring
        REVERSING_SHOOTER,       // Reverse shooter FIRST to pull ball down
        REVERSING_INTAKE,        // Brief intake reverse to help position ball
        SPINNING_UP,             // Spin up shooter to target velocity
        FEEDING,                 // Feed balls into shooter
        COMPLETE                 // Scoring complete
    }

    private ScoringState currentState = ScoringState.IDLE;

    private static final double SHOOTER_REVERSE_DURATION = 0.5;  // Reverse shooter first
    private static final double INTAKE_REVERSE_DURATION = 0.2;   // Short intake reverse
    private static final double SPINUP_DURATION = 0.8;
    private static final double FEEDING_DURATION = 3.0;

    // Power/velocity constants
    private static final double INTAKE_REVERSE_POWER = -0.05;
    private static final double SHOOTER_REVERSE_VELOCITY = -500;  // Moderate reverse speed
    private static final double FEEDING_POWER = 1.0;

    public ShooterScoring(Intake intake, Shooter shooter) {
        this.intake = intake;
        this.shooter = shooter;
        this.timer = new ElapsedTime();
    }

    public void startScoring() {
        if (currentState == ScoringState.IDLE) {
            currentState = ScoringState.REVERSING_SHOOTER;
            timer.reset();
        }
    }

    public void update() {
        switch (currentState) {
            case IDLE:
                break;

            case REVERSING_SHOOTER:
                // Reverse ONLY the shooter first to pull initial ball down
                intake.stop();  // Keep intake stopped
                shooter.setVelocity(SHOOTER_REVERSE_VELOCITY);

                if (timer.seconds() >= SHOOTER_REVERSE_DURATION) {
                    // Move to brief intake reverse
                    currentState = ScoringState.REVERSING_INTAKE;
                    timer.reset();
                }
                break;

            case REVERSING_INTAKE:
                // Brief intake reverse while shooter continues reversing
                intake.setPower(INTAKE_REVERSE_POWER);
                shooter.setVelocity(SHOOTER_REVERSE_VELOCITY);  // Keep shooter reversing

                if (timer.seconds() >= INTAKE_REVERSE_DURATION) {
                    // Move to spinning up phase
                    currentState = ScoringState.SPINNING_UP;
                    timer.reset();
                }
                break;

            case SPINNING_UP:
                // Stop intake, set shooter to NEAR shot and let it spin up
                intake.stop();
                shooter.setNearShot();  // Set NEAR shot velocity

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