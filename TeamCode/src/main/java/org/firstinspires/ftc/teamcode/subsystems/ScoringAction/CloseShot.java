package org.firstinspires.ftc.teamcode.subsystems.ScoringAction;

import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;

public class CloseShot {
    private final Flywheel flywheel;
    private final Kicker kicker1;
    private final Kicker kicker2;

    private final ElapsedTime timer = new ElapsedTime();

    private enum State {
        IDLE,
        SPIN_UP,
        SHOOT_3, WAIT_3,
        SHOOT_2, WAIT_2,
        SHOOT_1, WAIT_1,
        STOP_FLYWHEEL,
        DONE
    }

    private State state = State.IDLE;

    // Initial positions (match TeleOp)
    private final double K1_INITIAL = 0.3;
    private final double K2_INITIAL = 0.5;

    // Fire positions
    private final double K2_FIRE_3 = 0.9;
    private final double K1_FIRE_2 = 0.85;
    private final double K2_FIRE_2 = 0.9;
    private final double K1_FIRE_1 = 1.0;

    // Timings (increase FEED_WAIT_SECONDS to slow kickers)
    private final double SPIN_UP_SECONDS = 1.5;
    private final double FEED_WAIT_SECONDS = 1.5;

    public CloseShot(Flywheel flywheel, Kicker kicker1, Kicker kicker2) {
        this.flywheel = flywheel;
        this.kicker1 = kicker1;
        this.kicker2 = kicker2;

        // Ensure correct initial positions
        kicker1.setServoPos1(K1_INITIAL);
        kicker2.setServoPos2(K2_INITIAL);
    }

    public void start() {
        if (state != State.IDLE && state != State.DONE) return; // prevent restart mid-sequence
        timer.reset();
        state = State.SPIN_UP;
    }

    public boolean update() {
        // Keep flywheel spinning at NEAR speed during the whole sequence
                    flywheel.setVelocity(190); // NEAR_VELOCITY

        switch (state) {
            case IDLE:
                return false;

            case SPIN_UP:
                if (timer.seconds() >= SPIN_UP_SECONDS) {
                    state = State.SHOOT_3;
                    timer.reset();
                }
                return true;

            case SHOOT_3:
                kicker2.setServoPos2(K2_FIRE_3);
                if (timer.seconds() >= 0.5) {
                    kicker2.setServoPos2(K2_INITIAL);
                    state = State.WAIT_3;
                    timer.reset();
                }
                return true;

            case WAIT_3:
                if (timer.seconds() >= FEED_WAIT_SECONDS) {
                    kicker2.setServoPos2(K2_INITIAL);
                    state = State.SHOOT_2;
                    timer.reset();
                }
                return true;

            case SHOOT_2:
                kicker1.setServoPos1(K1_FIRE_2);
                if (timer.seconds() >= 0.5) {
                    kicker2.setServoPos2(K2_FIRE_2);
                }
                if (timer.seconds() >= 1.2) {
                    kicker1.setServoPos1(0.7);
                    kicker2.setServoPos2(K2_INITIAL);
                    state = State.WAIT_2;
                    timer.reset();
                }
                return true;

            case WAIT_2:
                if (timer.seconds() >= FEED_WAIT_SECONDS) {
                    kicker2.setServoPos2(K2_INITIAL);
                    kicker1.setServoPos1(K1_INITIAL);
                    state = State.SHOOT_1;
                    timer.reset();
                }
                return true;

            case SHOOT_1:
                kicker1.setServoPos1(1);
                if (timer.seconds() >= 1.2) {
                    kicker2.setServoPos1(0.9);
                }
                state = State.WAIT_1;
                timer.reset();
                return true;

            case WAIT_1:
                if (timer.seconds() >= FEED_WAIT_SECONDS) {
                    kicker1.setServoPos1(K1_INITIAL);
                    flywheel.setVelocity(0); // stop flywheel at the very end
                    state = State.STOP_FLYWHEEL;
                    timer.reset();
                }
                return true;

            case STOP_FLYWHEEL:
                    flywheel.setVelocity(0);
                    state = State.DONE;

                return true;

            case DONE:
                state = State.IDLE;
                return false;
        }

        return false;
    }

    public boolean isRunning() {
        return state != State.IDLE;
    }
}