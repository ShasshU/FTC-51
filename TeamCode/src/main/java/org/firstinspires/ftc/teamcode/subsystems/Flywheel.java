package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {
    private DcMotor flywheel1, flywheel2;

    public Flywheel(HardwareMap hardwareMap){
        flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");

        flywheel2.setDirection(DcMotor.Direction.REVERSE);

        flywheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPower(double power){
        flywheel1.setPower(power);
        flywheel2.setPower(power);
    }

    public void stop(){
        setPower(0);
    }
}
