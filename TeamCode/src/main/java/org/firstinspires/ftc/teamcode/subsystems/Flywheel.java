package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {
    private DcMotorEx flywheel1, flywheel2;

    public Flywheel(HardwareMap hardwareMap){
        this.flywheel1 = hardwareMap.get(DcMotorEx.class, "flywheel1");
        this.flywheel2 = hardwareMap.get(DcMotorEx.class, "flywheel2");

        this.flywheel2.setDirection(DcMotorEx.Direction.REVERSE);

        this.flywheel1.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        this.flywheel1.setVelocityPIDFCoefficients(100,0.01,0, 0.1);
        this.flywheel1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.flywheel2.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        this.flywheel2.setVelocityPIDFCoefficients(100,0.01,0, 0.1);
        this.flywheel2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setVelocity(double vel){
        flywheel1.setVelocity(vel);
        flywheel2.setVelocity(vel);
    }

    public void stop(){
        setVelocity(0);
    }
}
