package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumFieldOrientatedDrive extends OpMode {

    FieldCentricDrive fieldCentricDrive = new FieldCentricDrive();

    double forward, strafe, rotate;

    @Override
    public void init(){
        fieldCentricDrive.init(hardwareMap);

    }

    @Override
    public void loop(){
        forward = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.left_stick_x;

        fieldCentricDrive.driveFieldRelative(forward, strafe, rotate);

    }
}
