package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name="Field Orientated Drive", group="TeleOp") // FIELD ORIENTATED

public class MecanumFieldOrientedDrive extends OpMode {

    public DcMotor intake;
    public DcMotorEx flywheel1;
    public DcMotor flywheel2;
    private boolean lastA = false; // remembers previous A button state
    boolean intakeOn = false;
    public static double targetVelocity = 3000;

    FieldCentricDrive fieldCentricDrive = new FieldCentricDrive();

    double forward, strafe, rotate;

    @Override
    public void init(){
        fieldCentricDrive.init(hardwareMap);
        intake = hardwareMap.get(DcMotor.class, "intake");
        flywheel1 = hardwareMap.get(DcMotorEx.class, "flywheel1");
        flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");

        flywheel1.setDirection(DcMotor.Direction.FORWARD);
        flywheel2.setDirection(DcMotor.Direction.REVERSE);

        flywheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    @Override
    public void loop(){
        forward = -gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x;
        rotate = gamepad1.right_stick_x;

        fieldCentricDrive.driveFieldRelative(forward, strafe, rotate);

        if(gamepad1.a && !lastA) {
            intakeOn = !intakeOn;
        }
        lastA = gamepad1.a;

        if(intakeOn) {
            intake.setPower(1.0);
        }
        else {
            intake.setPower(0.0);
        }
        if (gamepad1.b) {
            // Run the master motor at the target velocity
            flywheel1.setVelocity(targetVelocity);
            // Have the slave motor mirror the master's power
            flywheel2.setPower(flywheel1.getPower());
        } else {
            // Stop both motors
            flywheel1.setVelocity(0);
            flywheel2.setPower(0);
    }
}
}
