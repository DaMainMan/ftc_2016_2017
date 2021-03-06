package org.ftc7244.robotcontroller.programs.debug;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.ftc7244.robotcontroller.hardware.Westcoast;

/**
 * Created by ftc72 on 4/8/2018.
 */
@Autonomous(name = "Drive Color Debug")
public class DriveColorDebug extends LinearOpMode {
    @Override
    public void runOpMode() {
        Westcoast robot = new Westcoast(this);
        Double grayscale;
        robot.init();
        waitForStart();
        while(opModeIsActive()){
            grayscale = ( (0.3 * robot.getDriveColor().red()) + (0.59 * robot.getDriveColor().green()) + (0.11 * robot.getDriveColor().blue()) );
            telemetry.addData("R", robot.getDriveColor().red());
            telemetry.addData("G", robot.getDriveColor().green());
            telemetry.addData("B", robot.getDriveColor().blue());
            telemetry.addData("Grayscale", grayscale);
            telemetry.addData("NaN", grayscale.byteValue());
            telemetry.update();
        }
    }
}
