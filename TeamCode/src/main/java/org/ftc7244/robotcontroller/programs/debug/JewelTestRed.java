package org.ftc7244.robotcontroller.programs.debug;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.ftc7244.robotcontroller.autonomous.ControlSystemAutonomous;

/**
 * Created by ftc72 on 3/17/2018.
 */
@Autonomous(name = "Jewel Red Test")
//@Disabled
public class JewelTestRed extends ControlSystemAutonomous {
    @Override
    public void run(){
        robot.init();
        robot.initServos();
        waitForStart();
        robot.knockOverJewel(Color.RED);
    }
}
