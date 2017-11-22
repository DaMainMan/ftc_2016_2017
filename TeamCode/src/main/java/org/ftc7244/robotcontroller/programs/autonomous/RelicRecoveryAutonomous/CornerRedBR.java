package org.ftc7244.robotcontroller.programs.autonomous.RelicRecoveryAutonomous;

import android.media.MediaPlayer;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.ftc7244.robotcontroller.autonomous.bases.RelicRecoveryPIDAutonamous;
import org.ftc7244.robotcontroller.hardware.RelicRecoveryWestcoast;
import org.ftc7244.robotcontroller.sensor.vuforia.ImageTransformProvider;

/**
 * Created by Eeshwar Laptop on 10/29/2017.
 */
@Autonomous(name="CornerRedBR")
public class CornerRedBR extends RelicRecoveryPIDAutonamous {
    private String image;
    public void run(){
        robot.init();
        robot.getSpring().resetDeviceConfigurationForOpMode();
        waitForStart();
        sleep(1500);
        robot.drive(0.3, 0.3);
        sleep(850);
        robot.drive(0, 0);
        sleep(1500);
        robot.drive(0.3, -0.3);
        sleep(800);
        switch(image) {
            case "R":
                sleep(1000);
                break;
            case "C":
                sleep(900);
                break;
            case "L":
                sleep(800);
                break;
            default:
                sleep(900);
                break;
        }
        robot.drive(0, 0);
        sleep(1000);
        robot.drive(0.3, 0.3);
        sleep(1300);
        robot.drive(0, 0);
        sleep(1500);
        robot.getIntake().setPower(1);
        sleep(700);
        robot.getIntake().setPower(0);
        sleep(1500);
        robot.getSpring().setDirection(DcMotorSimple.Direction.REVERSE);
        robot.getSpring().setPower(1);
        sleep(500);
        robot.getSpring().setPower(0);
        sleep(1000);
        robot.drive(-0.3, -0.3);
        sleep(200);
        robot.drive(0, 0);
        sleep(500);
        while(opModeIsActive()){
            //Loop to prevent crash
        }
    }
}