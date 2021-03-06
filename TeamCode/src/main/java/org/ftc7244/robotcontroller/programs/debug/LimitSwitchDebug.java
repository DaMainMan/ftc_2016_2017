package org.ftc7244.robotcontroller.programs.debug;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.ftc7244.robotcontroller.autonomous.ControlSystemAutonomous;

@Autonomous(name = "Limit Switch Debug")
@Disabled
public class LimitSwitchDebug extends ControlSystemAutonomous{
    @Override
    public void run(){
        waitForStart();
        while(opModeIsActive()){
            telemetry.addData("Pressed?", robot.getBottomIntakeSwitch().getVoltage());
            telemetry.update();
        }
    }
}
