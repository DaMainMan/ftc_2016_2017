package org.ftc7244.robotcontroller.programs.debug;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.ftc7244.robotcontroller.Westcoast;
import org.ftc7244.robotcontroller.autonomous.PIDAutonomous;
import org.ftc7244.robotcontroller.sensor.vuforia.ImageTransformProvider;

/**
 * Created by BeaverDuck on 9/20/17.
 */
@Autonomous(name = "Vuforia Debug")
public class VuforiaDebug extends PIDAutonomous {
    @Override
    public void run() throws InterruptedException {
        while (opModeIsActive()){
            telemetry.addLine("X: " + imageProvider.getImageDistance(ImageTransformProvider.Axis.X));
            telemetry.addLine("IMG: " + imageProvider.getImageReading());
            telemetry.update();
        }
        while (!isStopRequested()) idle();
    }
}