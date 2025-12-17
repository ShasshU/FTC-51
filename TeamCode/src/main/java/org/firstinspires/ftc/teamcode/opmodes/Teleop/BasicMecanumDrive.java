package org.firstinspires.ftc.teamcode.opmodes.Teleop;

import static org.firstinspires.ftc.teamcode.subsystems.Turret.limelight;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.subsystems.Turret;
import com.qualcomm.hardware.limelightvision.LLResult;

@TeleOp(name="Basic Mecanum Drive", group="TeleOp") //ROBOT ORIENTATED
public class BasicMecanumDrive extends OpMode {

    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotor leftBack;
    private DcMotor rightBack;
    private Turret turret;

    private boolean slowMode = false; // start in normal mode
    private boolean lastX = false;    // remembers previous X button state


    @Override
    public void init() {
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        turret = new Turret(hardwareMap);
        limelight.start();
        limelight.pipelineSwitch(0);


        rightBack.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
            Turret.aimAtTarget();

        if (gamepad1.x && !lastX) {
                slowMode = !slowMode;
            }
            lastX = gamepad1.x;

            double y = -gamepad1.left_stick_y; // Forward/backward
            double x = gamepad1.left_stick_x;  // Strafe left/right
            double rx = gamepad1.right_stick_x; // Rotation

            double frontLeftPower = y + x + rx;
            double backLeftPower = y - x + rx;
            double frontRightPower = y - x - rx;
            double backRightPower = y + x - rx;

            double max = Math.max(1.0, Math.abs(frontLeftPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backRightPower));

            leftFront.setPower(frontLeftPower / max);
            leftBack.setPower(backLeftPower / max);
            rightFront.setPower(frontRightPower / max);
            rightBack.setPower(backRightPower / max);

            double scale = slowMode ? 0.1 : 1.0; // 10% power when in slow mode

            leftFront.setPower((frontLeftPower / max) * scale);
            leftBack.setPower((backLeftPower / max) * scale);
            rightFront.setPower((frontRightPower / max) * scale);
            rightBack.setPower((backRightPower / max) * scale);

            telemetry.addData("Slow Mode", slowMode ? "ON" : "OFF");
            telemetry.addData("FL", frontLeftPower);
            telemetry.addData("FR", frontRightPower);
            telemetry.addData("BL", backLeftPower);
            telemetry.addData("BR", backRightPower);
            telemetry.addData("Turret Pos", turret.getPosition());
            telemetry.addData("Connected", limelight.isConnected());
            telemetry.update();
            telemetry.update();
        }
    }