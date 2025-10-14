package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Intake {
    private static DcMotor intake;
    private boolean intakeOn = false;

    public Intake(HardwareMap hardwareMap){
        intake = hardwareMap.get(DcMotor.class, "intake");
    }

    // Toggle the intake on/off when called
    public void toggleIntake(){
        intakeOn = !intakeOn;
        intake.setPower(intakeOn ? 0.7 : 0);
    }

    // Directly set intake power if needed
    public static void setPower(double power){
        intake.setPower(power);
    }
}