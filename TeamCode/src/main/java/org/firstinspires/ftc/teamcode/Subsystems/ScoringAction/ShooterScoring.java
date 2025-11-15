package org.firstinspires.ftc.teamcode.Subsystems.ScoringAction;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Shooter;

public class ShooterScoring {

    private Intake intake;
    private Shooter shooter;
    private ElapsedTime timer;

    public enum ScoringState {
        IDLE,
        REVERSING_SHOOTER,
        REVERSING_INTAKE,
        SPINNING_UP,
        FEEDING_SLOW,
        FEEDING_FAST,
        REPOSITIONING_BALL_3_REVERSE,
        REPOSITIONING_BALL_3_INTAKE,
        FEEDING_BALL_3,
        COMPLETE
    }

    private ScoringState currentState = ScoringState.IDLE;

    private static final double SHOOTER_REVERSE_DURATION = 0.5;
    private static final double INTAKE_REVERSE_DURATION = 0.2;
    private static final double SPINUP_DURATION = 4.0;
    private static final double FEEDING_SLOW_DURATION = 3.7;
    private static final double FEEDING_FAST_DURATION = 1.5;
    private static final double BALL_3_REVERSE_DURATION = 0.2;  // Same as initial reverse
    private static final double BALL_3_INTAKE_DURATION = 1.0;   // Time to feed ball 3

    private static final double SHOOTER_REVERSE_VELOCITY = -100;
    private static final double INTAKE_REVERSE_POWER = -0.3;
    private static final double FEEDING_SLOW_POWER = 0.5;
    private static final double FEEDING_FAST_POWER = 1.0;

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
                intake.stop();
                shooter.setVelocity(SHOOTER_REVERSE_VELOCITY);

                if (timer.seconds() >= SHOOTER_REVERSE_DURATION) {
                    currentState = ScoringState.REVERSING_INTAKE;
                    timer.reset();
                }
                break;

            case REVERSING_INTAKE:
                intake.setPower(INTAKE_REVERSE_POWER);
                shooter.setVelocity(SHOOTER_REVERSE_VELOCITY);

                if (timer.seconds() >= INTAKE_REVERSE_DURATION) {
                    currentState = ScoringState.SPINNING_UP;
                    timer.reset();
                }
                break;

            case SPINNING_UP:
                intake.stop();
                shooter.setNearShot();

                if (timer.seconds() >= SPINUP_DURATION) {
                    currentState = ScoringState.FEEDING_SLOW;
                    timer.reset();
                }
                break;

            case FEEDING_SLOW:
                intake.setPower(FEEDING_SLOW_POWER);
                // Shooter stays at near shot velocity

                if (timer.seconds() >= FEEDING_SLOW_DURATION) {
                    currentState = ScoringState.FEEDING_FAST;
                    timer.reset();
                }
                break;

            case FEEDING_FAST:
                intake.setPower(FEEDING_FAST_POWER);
                // Shooter stays at near shot velocity

                if (timer.seconds() >= FEEDING_FAST_DURATION) {
                    currentState = ScoringState.REPOSITIONING_BALL_3_REVERSE;
                    timer.reset();
                }
                break;

            case REPOSITIONING_BALL_3_REVERSE:
                intake.setPower(INTAKE_REVERSE_POWER);
                // Shooter STAYS at near shot velocity (key optimization!)

                if (timer.seconds() >= BALL_3_REVERSE_DURATION) {
                    currentState = ScoringState.REPOSITIONING_BALL_3_INTAKE;
                    timer.reset();
                }
                break;

            case REPOSITIONING_BALL_3_INTAKE:
                intake.setPower(FEEDING_FAST_POWER);
                // Shooter STAYS at near shot velocity

                if (timer.seconds() >= BALL_3_INTAKE_DURATION) {
                    currentState = ScoringState.COMPLETE;
                    stopScoring();
                }
                break;

            case COMPLETE:
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