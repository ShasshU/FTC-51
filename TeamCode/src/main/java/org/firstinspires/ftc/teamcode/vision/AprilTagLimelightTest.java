package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;

public abstract class AprilTagLimelightTest extends OpMode {

    private Limelight3A limelight;

    private IMU imu;

    @Override
    public void init(){
        limelight = hardwareMap.get(Limelight3A.class, "limelight"); //change name later dependig on driver hub
        limelight.pipelineSwitch(0);
        imu = hardwareMap.get(IMU.class, "imu");
    }


    @Override
    public void start(){

    }
}
