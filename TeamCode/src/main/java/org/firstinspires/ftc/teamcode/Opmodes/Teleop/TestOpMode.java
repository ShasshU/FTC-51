package org.firstinspires.ftc.teamcode.Opmodes.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

@TeleOp(name = "Pinpoint Raw Test", group = "Test")
public class TestOpMode extends OpMode {

    GoBildaPinpointDriver pinpoint;

    @Override
    public void init() {
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setOffsets(0, 0, DistanceUnit.INCH); // zero offsets for testing
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );

        pinpoint.resetPosAndIMU();

        telemetry.addLine("Initialized - Push robot or spin encoder wheels");
        telemetry.update();
    }

    @Override
    public void loop() {
        pinpoint.update();

        Pose2D pos = pinpoint.getPosition();

        telemetry.addData("X Position", pos.getX(DistanceUnit.INCH));
        telemetry.addData("Y Position", pos.getY(DistanceUnit.INCH));
        telemetry.addData("Heading", pos.getHeading(AngleUnit.DEGREES));
        telemetry.addLine();
        telemetry.addLine("Push FORWARD - X should change");
        telemetry.addLine("Push LEFT - Y should change");
        telemetry.addLine("Rotate - Heading should change");
        telemetry.update();
    }
}