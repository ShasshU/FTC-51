//package org.firstinspires.ftc.teamcode.Opmodes.Teleop;
//
//import com.bylazar.configurables.annotations.Configurable;
//import com.pedropathing.follower.Follower;
//import com.pedropathing.geometry.Pose;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//
//import org.firstinspires.ftc.teamcode.Subsystems.Intake;
//import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
//import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
//import static org.firspackage org.firstinspires.ftc.teamcode.Opmodes.Teleop;
////
////import com.bylazar.configurables.annotations.Configurable;
////import com.pedropathing.follower.Follower;
////import com.pedropathing.geometry.Pose;
////import com.qualcomm.robotcore.eventloop.opmode.OpMode;
////import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
////
////import org.firstinspires.ftc.teamcode.Subsystems.Intake;
////import org.firstinspires.ftc.teamcode.Subsystems.Shooter;
////import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
////import static org.firstinspires.ftc.teamcode.Opmodes.Autonomous.BlueClose9Piece.autoEndPose;
////
////
////@Configurable
////@TeleOp(name = "Main TeleOp")
////public class TestTeleop extends OpMode {
////
////    // Pedro Pathing
////    private static Follower follower;
////    public static Pose startingPose;
////
////    // Subsystems
////    private Intake intake;
////    private Shooter shooter;
////
////    // Slow mode
////    private boolean slowMode = false;
////    private double slowModeMultiplier = 0.5;
////
////    @Override
////    public void init() {
////        // Initialize Pedro Pathing
////        follower = Constants.createFollower(hardwareMap);
////        follower.setStartingPose(autoEndPose == null ? new Pose() : autoEndPose);
////        follower.update();
////
////        // Initialize subsystems
////        intake = new Intake(hardwareMap);
////        shooter = new Shooter(hardwareMap);
////
////        telemetry.addLine("TeleOp Initialized!");
////        telemetry.update();
////    }
////
////    @Override
////    public void start() {
////        follower.startTeleopDrive();
////    }
////
////    @Override
////    public void loop() {
////        // ========== UPDATE ALL SYSTEMS ==========
////        follower.update();
////
////        // ========== DRIVETRAIN CONTROL ==========
////        double speedMultiplier = slowMode ? slowModeMultiplier : 1.0;
////
////        follower.setTeleOpDrive(
////                -gamepad1.left_stick_y * speedMultiplier,
////                -gamepad1.left_stick_x * speedMultiplier,
////                -gamepad1.right_stick_x * speedMultiplier,
////                false
////        );
////
////        // ========== SLOW MODE CONTROL ==========
////        // Right stick button = Toggle slow mode on/off
////        if (gamepad1.rightStickButtonWasPressed()) {
////            slowMode = !slowMode;
////        }
////
////        // ========== INTAKE CONTROL ==========
////        // Right trigger = Toggle intake on/off
////        if (gamepad1.right_trigger > 0.5) {
////            intake.startIntake();
////        }
////        // Left trigger = Outtake (reverse)
////        else if (gamepad1.left_trigger > 0.5) {
////            intake.startOuttake();
////        }
////        // No triggers = Stop intake
////        else {
////            intake.stop();
////        }
////
////        // ========== SHOOTER CONTROL ==========
////        // Right bumper = Near shot
////        if (gamepad1.right_bumper) {
////            if (shooter.getCurrentShotMode() == Shooter.ShotMode.NEAR) {
////                shooter.turnOff();
////            } else {
////                shooter.setNearShot();
////            }
////        }
////
////        // Left bumper = Far shot
////        if (gamepad1.left_bumper) {
////            if (shooter.getCurrentShotMode() == Shooter.ShotMode.FAR) {
////                shooter.turnOff();
////            } else {
////                shooter.setFarShot();
////            }
////        }
////
////        // A button = Turn off shooter
////        if (gamepad1.aWasPressed()) {
////            shooter.turnOff();
////        }
////
////    }
////
////    @Override
////    public void stop() {
////
////        // Turn off all subsystems
////        intake.stop();
////        shooter.turnOff();
////    }
////}tinspires.ftc.teamcode.Opmodes.Autonomous.BlueClose9Piece.autoEndPose;
//
//
//@Configurable
//@TeleOp(name = "Main TeleOp")
//public class TestTeleop extends OpMode {
//
//    // Pedro Pathing
//    private static Follower follower;
//    public static Pose startingPose;
//
//    // Subsystems
//    private Intake intake;
//    private Shooter shooter;
//
//    // Slow mode
//    private boolean slowMode = false;
//    private double slowModeMultiplier = 0.5;
//
//    @Override
//    public void init() {
//        // Initialize Pedro Pathing
//        follower = Constants.createFollower(hardwareMap);
//        follower.setStartingPose(autoEndPose == null ? new Pose() : autoEndPose);
//        follower.update();
//
//        // Initialize subsystems
//        intake = new Intake(hardwareMap);
//        shooter = new Shooter(hardwareMap);
//
//        telemetry.addLine("TeleOp Initialized!");
//        telemetry.update();
//    }
//
//    @Override
//    public void start() {
//        follower.startTeleopDrive();
//    }
//
//    @Override
//    public void loop() {
//        // ========== UPDATE ALL SYSTEMS ==========
//        follower.update();
//
//        // ========== DRIVETRAIN CONTROL ==========
//        double speedMultiplier = slowMode ? slowModeMultiplier : 1.0;
//
//        follower.setTeleOpDrive(
//                -gamepad1.left_stick_y * speedMultiplier,
//                -gamepad1.left_stick_x * speedMultiplier,
//                -gamepad1.right_stick_x * speedMultiplier,
//                false
//        );
//
//        // ========== SLOW MODE CONTROL ==========
//        // Right stick button = Toggle slow mode on/off
//        if (gamepad1.rightStickButtonWasPressed()) {
//            slowMode = !slowMode;
//        }
//
//        // ========== INTAKE CONTROL ==========
//        // Right trigger = Toggle intake on/off
//        if (gamepad1.right_trigger > 0.5) {
//            intake.startIntake();
//        }
//        // Left trigger = Outtake (reverse)
//        else if (gamepad1.left_trigger > 0.5) {
//            intake.startOuttake();
//        }
//        // No triggers = Stop intake
//        else {
//            intake.stop();
//        }
//
//        // ========== SHOOTER CONTROL ==========
//        // Right bumper = Near shot
//        if (gamepad1.right_bumper) {
//            if (shooter.getCurrentShotMode() == Shooter.ShotMode.NEAR) {
//                shooter.turnOff();
//            } else {
//                shooter.setNearShot();
//            }
//        }
//
//        // Left bumper = Far shot
//        if (gamepad1.left_bumper) {
//            if (shooter.getCurrentShotMode() == Shooter.ShotMode.FAR) {
//                shooter.turnOff();
//            } else {
//                shooter.setFarShot();
//            }
//        }
//
//        // A button = Turn off shooter
//        if (gamepad1.aWasPressed()) {
//            shooter.turnOff();
//        }
//
//    }
//
//    @Override
//    public void stop() {
//
//        // Turn off all subsystems
//        intake.stop();
//        shooter.turnOff();
//    }
//}