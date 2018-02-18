package org.ftc7244.robotcontroller.programs.autonomous;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.ftc7244.robotcontroller.autonomous.PIDAutonomous;


/**
 * Created by Eeshwar Laptop on 10/29/2017.
 */
@Autonomous(name = "Red Right1")
public class RedRight extends PIDAutonomous {

    public void run() throws InterruptedException{
        robot.knockOverJewel(Color.BLUE);//Check color sensor
        robot.getIntakeTop().setPower(-1);
        robot.driveToInch(.2, 36);//Drive off balancing stone
        gyroscope.rotate(0);//Re-Center the robot
        sleep(200);
        robot.getSpring().setPosition(0.5);//Spring out glyph
        sleep(200);
        RelicRecoveryVuMark image = imageProvider.getImageReading();
        telemetry.addData("Image", image);
        telemetry.update();
        sleep(1000);
        robot.getIntakeServo().setPosition(0.8);
        gyroscope.rotate(45);//Rotate to face glyph pit
        robot.getIntakeBottom().setPower(-1);
        robot.driveIntakeVertical(0.5);
        sleep(200);
        robot.driveIntakeVertical(0);
        gyroscope.drive(0.6, 11);//Drive to glyph pit
        gyroscope.drive(0.6, 12);// Drive into glyph pit
        //0.2 : 0.75
        robot.getIntakeServo().setPosition(0.2);
        sleep(1500);
        gyroscope.drive(-0.6, 10);
        switch(image){
            case LEFT:
                gyroscope.rotate(-151.1);
                gyroscope.drive(0.85, 40);
                break;
            case RIGHT:
                gyroscope.rotate(-166);
                gyroscope.drive(0.85, 37);
                break;
            default:
                gyroscope.rotate(-158);
                gyroscope.drive(0.85, 42);
        }
        robot.getIntakeBottom().setPower(1);
        robot.getIntakeTop().setPower(1);
        gyroscope.drive(-0.6, 8);
        gyroscope.drive(0.6, 6);
        gyroscope.drive(-0.6, 5);
    }
}