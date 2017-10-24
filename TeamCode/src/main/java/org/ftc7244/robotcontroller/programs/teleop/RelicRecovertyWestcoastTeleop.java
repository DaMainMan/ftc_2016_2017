package org.ftc7244.robotcontroller.programs.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.ftc7244.robotcontroller.hardware.RelicRecoveryWestcoast;
import org.ftc7244.robotcontroller.input.Button;
import org.ftc7244.robotcontroller.input.ButtonType;

/**
 * Created by Eeshwar Laptop on 10/16/2017.
 */

@TeleOp(name = "Relic Recovery Westcoast")
public class RelicRecovertyWestcoastTeleop extends OpMode {
    RelicRecoveryWestcoast robot;
    boolean isSlowed = false;
    private Button left_trigger;
    private Button right_trigger;
    public void init(){
        robot = new RelicRecoveryWestcoast(this);
        robot.init();
        left_trigger = new Button(gamepad1, ButtonType.LEFT_TRIGGER);
        right_trigger = new Button(gamepad1, ButtonType.RIGHT_TRIGGER);
    }
    @Override
    public void loop(){
        if(left_trigger.isPressed()){
            isSlowed = false;
        }
        if(right_trigger.isPressed()){
            isSlowed = true;
        }
        if(isSlowed) {
            robot.getDriveBackLeft().setPower(-gamepad1.left_stick_y / 2);
            robot.getDriveFrontLeft().setPower(-gamepad1.left_stick_y / 2);
            robot.getDriveBackRight().setPower(gamepad1.right_stick_y / 2);
            robot.getDriveFrontRight().setPower(gamepad1.right_stick_y / 2);
        }
        if(!isSlowed) {
            robot.getDriveBackLeft().setPower(-gamepad1.left_stick_y);
            robot.getDriveFrontLeft().setPower(-gamepad1.left_stick_y);
            robot.getDriveBackRight().setPower(gamepad1.right_stick_y);
            robot.getDriveFrontRight().setPower(gamepad1.right_stick_y);
        }
    }
}