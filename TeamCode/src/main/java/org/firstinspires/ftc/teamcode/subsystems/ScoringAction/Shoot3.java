package org.firstinspires.ftc.teamcode.subsystems.ScoringAction;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.subsystems.Kicker;

public class Shoot3 {
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

    private final double K1_INITIAL = 0.3;     // kicker1 initial in TeleOp
    private final double K2_INITIAL = 0.5;     // kicker2 initial in TeleOp

    private final double K2_FIRE_3 = 0.9;      // dpad_right (3-ball)
    private final double K1_FIRE_2 = 0.85;     // dpad_up (both) kicker1 pos
    private final double K2_FIRE_2 = 0.9;      // dpad_up (both) kicker2 pos
    private final double K1_FIRE_1 = 1.0;      // dpad_left (1-ball)

    // timing (tweakable)
    private final double SPIN_UP_SECONDS = 1.2;
    private final double FEED_WAIT_SECONDS = 0.4;

    public Shoot3(Flywheel flywheel, Kicker kicker1, Kicker kicker2) {
        this.flywheel = flywheel;
        this.kicker1 = kicker1;
        this.kicker2 = kicker2;

        // ensure servos are at the same init positions used in TeleOp
        Kicker.setServoPos1(K1_INITIAL);   // static setter in your Kicker class
        kicker2.setServoPos2(K2_INITIAL);
    }

    public void start() {
        if (state != State.IDLE && state != State.DONE) return; // already running
        state = State.SPIN_UP;
        timer.reset();
    }


    public boolean update() {
        switch (state) {
            case IDLE:
                return false;

            case SPIN_UP:
                Flywheel.setVelocity(flywheel.getShotVelocity(Flywheel.ShotMode.NEAR));
                timer.reset();
                state = State.SHOOT_3;
                return true;

            case SHOOT_3:
                kicker2.setServoPos2(K2_FIRE_3);
                timer.reset();
                state = State.WAIT_3;
                return true;

            case WAIT_3:
                if (timer.seconds() >= FEED_WAIT_SECONDS) {
                    kicker2.setServoPos2(K2_INITIAL);
                    timer.reset();
                    state = State.SHOOT_2;
                }
                return true;

            case SHOOT_2:
                Kicker.setServoPos1(K1_FIRE_2);
                kicker2.setServoPos2(K2_FIRE_2);
                timer.reset();
                state = State.WAIT_2;
                return true;

            case WAIT_2:
                if (timer.seconds() >= FEED_WAIT_SECONDS) {
                    Kicker.setServoPos1(K1_INITIAL);
                    kicker2.setServoPos2(K2_INITIAL);
                    timer.reset();
                    state = State.SHOOT_1;
                }
                return true;

            case SHOOT_1:
                Kicker.setServoPos1(K1_FIRE_1);
                timer.reset();
                state = State.WAIT_1;
                return true;

            case WAIT_1:
                if (timer.seconds() >= FEED_WAIT_SECONDS) {
                    Kicker.setServoPos1(K1_INITIAL);
                    timer.reset();
                    state = State.STOP_FLYWHEEL;
                }
                return true;

            case STOP_FLYWHEEL:
                Flywheel.setVelocity(flywheel.getShotVelocity(Flywheel.ShotMode.OFF));
                state = State.DONE;
                return true;

            case DONE:
                state = State.IDLE; // reset so it can be started again later
                return false;
        }

        return false;
    }

    public boolean isRunning() {
        return !(state == State.IDLE);
    }
}