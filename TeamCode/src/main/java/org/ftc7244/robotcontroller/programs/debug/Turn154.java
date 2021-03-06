package org.ftc7244.robotcontroller.programs.debug;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.ftc7244.robotcontroller.autonomous.ControlSystemAutonomous;


@Autonomous(name = "Turn -154")
@Disabled
public class Turn154 extends ControlSystemAutonomous{

    @Override
    public void run(){
        robot.getIntakeLift().setPower(1);
        sleep(100);
        robot.getIntakeLift().setPower(0.1);
        gyroscopePID.rotate(-154);
    }
}
