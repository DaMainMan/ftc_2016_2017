package org.ftc7244.robotcontroller.programs.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftc7244.robotcontroller.hardware.VelocityVortexWestcoast;
import org.ftc7244.robotcontroller.input.Button;
import org.ftc7244.robotcontroller.input.ButtonType;
import org.ftc7244.robotcontroller.input.PressButton;
import org.ftc7244.robotcontroller.sensor.SickUltrasonic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@TeleOp(name = "VelocityVortexWestcoast Drive")
public class VelocityVortexWestcoastTeleop extends OpMode {

    private static final double LIFT_DRIVE_COEFFICIENT = 0.4;

    private VelocityVortexWestcoast robot;
    private Button aButton, triggerL, triggerR, xButton, yButton, bButton;
    private Button driverBButton, driverYButton;
    private AtomicBoolean runningLauncher, flicker;
    private ExecutorService service;

    @Override
    public void init() {
        robot = new VelocityVortexWestcoast(this);
        aButton = new Button(gamepad2, ButtonType.A);
        triggerL = new Button(gamepad2, ButtonType.LEFT_TRIGGER);
        triggerR = new Button(gamepad2, ButtonType.RIGHT_TRIGGER);
        xButton = new Button(gamepad2, ButtonType.X);
        yButton = new Button(gamepad2, ButtonType.Y);
        bButton = new Button(gamepad2, ButtonType.B);

        driverBButton = new PressButton(gamepad1, ButtonType.B);
        driverYButton = new PressButton(gamepad1, ButtonType.Y);

        runningLauncher = new AtomicBoolean(false);
        service = Executors.newCachedThreadPool();

        robot.init();
    }

    @Override
    public void loop() {
        //DRIVE
        //Allow for full power of the robot
        if (!driverYButton.isPressed()) {
            robot.drive(-gamepad1.right_stick_y, -gamepad1.left_stick_y);
            //Invert power and ramp down control
        } else {
            robot.drive(-gamepad1.right_stick_y*LIFT_DRIVE_COEFFICIENT, -gamepad1.left_stick_y*LIFT_DRIVE_COEFFICIENT);
        }

        robot.getBeaconPusher().setPosition(bButton.isPressed() ? 0 : 1);

        //If we are not in running launcher mode then allow for manual
        if (!runningLauncher.get()) {
            robot.getLauncher().setPower(xButton.isPressed() ? 1 : 0);
            robot.setDoorState(yButton.isPressed() ? VelocityVortexWestcoast.DoorState.OPEN : VelocityVortexWestcoast.DoorState.CLOSED);
        }

        //Run the automatic shoot system
        if (aButton.isPressed() && !runningLauncher.get()) {
            runningLauncher.set(true);
            service.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        robot.shoot(0);
                        VelocityVortexWestcoast.sleep(200);
                    } catch (InterruptedException e) {
                        RobotLog.e("Shooting was stopped early");
                    }
                    runningLauncher.set(false);
                }
            });
        }

        robot.setCarriageState(driverBButton.isPressed() ? VelocityVortexWestcoast.CarriageState.OPEN : VelocityVortexWestcoast.CarriageState.CLOSED);

        //Spool with a minimum power
        float spoolerPower = gamepad1.right_trigger - gamepad1.left_trigger;
        robot.setSpoolerPower(Math.abs(spoolerPower) > 0.5 ? spoolerPower : 0);

        //INTAKE
        //If no triggers are pressed stop the lift
        int intakeSpeed = 0;
        //If the right trigger is pressed startImageReading the lift
        if (triggerR.isPressed()) intakeSpeed = 1;
        //If the left trigger is pressed reverse the lift
        if (triggerL.isPressed()) intakeSpeed = -1;
        robot.getIntake().setPower(intakeSpeed);

        //Wall Notifier
        double trailing = robot.getTrailingUltrasonic().getUltrasonicLevel(), leading = robot.getLeadingUltrasonic().getUltrasonicLevel();
        boolean hitWall = ((trailing + leading) / 2) < 7.5 && (trailing < SickUltrasonic.Mode.INCHES.getCap() || leading < SickUltrasonic.Mode.INCHES.getCap());
        //Lift Raised
        boolean liftRaised = Math.abs(robot.getSpoolerTicks()) > 8525;

        //Raised Lift
        robot.getLights().setPower(hitWall || liftRaised ? 1 : 0);
    }

    @Override
    public void stop() {
        service.shutdown();
    }
}
