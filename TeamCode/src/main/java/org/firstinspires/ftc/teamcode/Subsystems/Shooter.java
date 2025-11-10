//package org.firstinspires.ftc.teamcode.Subsystems;
//
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.Gamepad;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//
//public class Shooter {
//    private boolean outtakeOn = false;
//    private static DcMotorEx shooter;
//    double NEAR_VELOCITY = 200;
//    double FAR_VELOCITY  = 225;
//    double OFF_VELOCITY  = 0;
//    public enum ShotMode { OFF, NEAR, FAR }
//    ShotMode currentShotMode = ShotMode.OFF;
//    boolean lastLeftBumper = false;
//    boolean lastRightBumper = false;
//    boolean lastBButton = false;
//
//    public Shooter(HardwareMap hardwareMap){
//        this.shooter = hardwareMap.get(DcMotorEx.class, "shooter");
//
//
//        this.shooter.setVelocityPIDFCoefficients(100,0.01,0, 0.1);
//        this.shooter.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//
//    }
//    public void toggleOuttake(){
//        outtakeOn = !outtakeOn;
//        shooter.setPower(outtakeOn ? 220 : 0);
//    }
//    public static void setVelocity(double vel){
//        shooter.setVelocity(vel);
//    }
//
//
//    public double findFlyWheelVelocity(Gamepad gamepad1)
//    {
//        if (gamepad1.left_bumper && !lastLeftBumper) {
//            currentShotMode = ShotMode.NEAR;
//        }
//        if (gamepad1.right_bumper && !lastRightBumper) {
//            currentShotMode = ShotMode.FAR;
//        }
//        // Optional OFF toggle (via button B for example)
//        if (gamepad1.b && !lastBButton) {
//            currentShotMode = ShotMode.OFF;
//        }
//
//        // Update last states
//        lastLeftBumper = gamepad1.left_bumper;
//        lastRightBumper = gamepad1.right_bumper;
//        lastBButton = gamepad1.b;
//
//        // Determine velocity
//        return getShotVelocity(currentShotMode);
//    }
//
//    public double getShotVelocity(ShotMode shotMode) {
//        switch (shotMode) {
//            case NEAR:
//                return NEAR_VELOCITY;
//            case FAR:
//                return FAR_VELOCITY;
//            case OFF:
//            default:
//                return OFF_VELOCITY;
//        }
//    }
//}