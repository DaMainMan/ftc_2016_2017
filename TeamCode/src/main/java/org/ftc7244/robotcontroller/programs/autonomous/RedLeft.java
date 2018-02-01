package org.ftc7244.robotcontroller.programs.autonomous;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.ftc7244.robotcontroller.autonomous.PIDAutonomous;


/**
 * Created by Eeshwar Laptop on 10/29/2017.
 */
@Autonomous(name = "Red Left")
public class RedLeft extends PIDAutonomous {

    public void run() throws InterruptedException{
        robot.knockOverJewel(Color.BLUE);//Check color sensor
        robot.getIntakeTop().setPower(-1);
        robot.driveToInch(.2, 28);//Drive off balancing stone
        sleep(200);
        robot.getSpring().setPosition(0.6);//Spring out glyph
        sleep(200);
        RelicRecoveryVuMark image = imageProvider.getImageReading();
        telemetry.addData("Image", image);
        telemetry.update();
        sleep(1000);
        robot.getIntakeServo().setPosition(0.2);
        gyroscope.rotate(-90);//Rotate to face glyph pit
        robot.getIntakeBottom().setPower(-1);
        robot.driveIntakeVertical(0.5);
        sleep(200);
        robot.driveIntakeVertical(0);
        gyroscope.drive(0.4, 16);//Drive to glyph pit
        gyroscope.drive(0.3, 17);// Drive into glyph pit
        //0.2: 0.75
        robot.getIntakeServo().setPosition(0.75);
        gyroscope.drive(0.3, 3);
        sleep(1500);
        switch(image) {
            case LEFT:
                break;
            case RIGHT:
                gyroscope.rotate(-90);
                gyroscope.drive(0.6, 21);
                break;
            default:
                break;
        }
        gyroscope.drive(0.6, 21);
        robot.getIntakeBottom().setPower(1);
        robot.getIntakeTop().setPower(1);
        gyroscope.drive(-0.2, 8);
        gyroscope.drive(0.5, 6);
        gyroscope.drive(-0.5, 5);
    }
}