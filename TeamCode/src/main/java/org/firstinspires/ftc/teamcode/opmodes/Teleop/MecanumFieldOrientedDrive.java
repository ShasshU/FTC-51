package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Field Orientated Drive", group="TeleOp") // FIELD ORIENTATED

public class MecanumFieldOrientedDrive extends OpMode {

    private DcMotor intake;

    FieldCentricDrive fieldCentricDrive = new FieldCentricDrive();

    double forward, strafe, rotate;

    @Override
    public void init(){
        fieldCentricDrive.init(hardwareMap);
        intake = hardwareMap.get(DcMotor.class, "intake");

    }

    @Override
    public void loop(){
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        fieldCentricDrive.driveFieldRelative(forward, strafe, rotate);

        if(gamepad1.a) {
            intake.setPower(1.0);
        }

    }
}
