package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Intake {

    // Intake states
    public enum IntakeState {
        OFF,
        INTAKE,
        OUTTAKE
    }

    private DcMotorEx intake;
    private IntakeState currentState = IntakeState.OFF;

    private static final double INTAKE_POWER = 1.0;
    private static final double OUTTAKE_POWER = -1.0;
    private static final double OFF_POWER = 0.0;

    public Intake(HardwareMap hardwareMap) {
        intake = hardwareMap.get(DcMotorEx.class, "intake");
    }

    // Toggle between OFF and INTAKE
    public void toggleIntake() {
        if (currentState == IntakeState.INTAKE) {
            stop();
        } else {
            startIntake();
        }
    }

    // Toggle between OFF and OUTTAKE
    public void toggleOuttake() {
        if (currentState == IntakeState.OUTTAKE) {
            stop();
        } else {
            startOuttake();
        }
    }

    // Direct control methods
    public void startIntake() {
        currentState = IntakeState.INTAKE;
        intake.setPower(INTAKE_POWER);
    }

    public void startOuttake() {
        currentState = IntakeState.OUTTAKE;
        intake.setPower(OUTTAKE_POWER);
    }

    public void stop() {
        currentState = IntakeState.OFF;
        intake.setPower(OFF_POWER);
    }

    // Set custom power (for tuning)
    public void setPower(double power) {
        intake.setPower(power);
    }

    // Get current state
    public IntakeState getState() {
        return currentState;
    }
}