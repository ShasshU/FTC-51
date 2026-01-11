package org.firstinspires.ftc.teamcode.Subsystems;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ShootingAction {
    private Intake intake;
    private ElapsedTime timer;
    private Shooter shooter;
    private Gate gate;
    private static final double FLYWHEEL_TIME = 2; // Run flywheel for 0.8 seconds
    private ScoringState currentState = ScoringState.IDLE;
    public enum ScoringState {
        IDLE,
        GATEOPEN,      // Running intake
        INTAKING,
        GATECLOSED,
        COMPLETE
    }
    public ShootingAction(Intake intake, Gate gate, Shooter shooter) {
        this.intake = intake;
        this.gate = gate;
        this.shooter = shooter;
        this.timer = new ElapsedTime();
    }

    // Start the scoring sequence
    public void startScoringNear() {
        if (currentState == ScoringState.IDLE) {
            shooter.setNearShot();
            currentState = ScoringState.GATEOPEN;
            timer.reset();
        }
    }
    public void startScoringFar() {
        if (currentState == ScoringState.IDLE) {
            shooter.setFarShot();
            currentState = ScoringState.GATEOPEN;
            timer.reset();
        }
    }

    // Stop scoring sequence (emergency stop)
    public void stopScoring() {
        intake.stop();
        gate.close();
        currentState = ScoringState.IDLE;
    }

    // Call this every loop to update the state machine
    public void update() {

        switch (currentState) {
            case IDLE:
                // Do nothing, waiting for startScoring()
                break;

            case GATEOPEN:
                // Wait for intake time, then kick
                if (timer.seconds() >= FLYWHEEL_TIME) {
                    gate.open(); // Use pulse instead of extend!
                    currentState = ScoringState.INTAKING;
                    timer.reset();
                }
                break;

            case INTAKING:
                // Wait for pulse to complete (kicker will auto-retract via update())
                if (timer.seconds() >= 0.4) {
                    intake.toggleIntakeFast();
                    currentState = ScoringState.GATECLOSED;
                }
                break;

            case GATECLOSED:

                if (timer.seconds() >= 2) {
                    gate.close();
                    intake.stop();
                    shooter.turnOff();
                    currentState = ScoringState.COMPLETE;
                }
                break;

            case COMPLETE:
                currentState = ScoringState.IDLE;
                break;
        }
    }

    // Status checks
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
