package org.ftc7244.robotcontroller.programs.autonomous;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.ftc7244.robotcontroller.autonomous.PIDAutonomous;
import org.ftc7244.robotcontroller.sensor.vuforia.ImageTransformProvider;


/**
 * Created by Eeshwar Laptop on 10/29/2017.
 */
@Autonomous(name = "Red Right")
public class RedRight extends PIDAutonomous {

    public void run() throws InterruptedException{
        robot.knockOverJewel(Color.RED);
        robot.drive(.2, .2, 1400);
        sleep(1000);
        telemetry.addData("Gyro", imageTransformProvider.getImageReading());
        telemetry.addData("Image", imageTransformProvider.getImageRotation(ImageTransformProvider.RotationAxis.PITCH));
        sleep(250);
        telemetry.update();
        gyroscope.rotate(-113);
        gyroscope.drive(0.2, 35);
        robot.getSpring().setPosition(0);
        robot.getIntakeBottom().setPower(1);
        gyroscope.drive(-0.2, 10);
        robot.getIntakeBottom().setPower(0);


        gyroscope.rotate(170);
        telemetry.addData("Done?", "Done");
        telemetry.update();
        gyroscope.drive(-0.3,6.5);
        gyroscope.drive(0.3,12);
        robot.getJewelVerticle().setPosition(.15);


  //      robot.getSpring().setDirection(DcMotorSimple.Direction.REVERSE);
  //      robot.getSpring().setPower(1);
  //      sleep(500);
  //      robot.getSpring().setPower(0);
    }
}