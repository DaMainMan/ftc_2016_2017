package org.ftc7244.robotcontrol.autonomous.pid;

import com.qualcomm.robotcore.util.RobotLog;

import org.ftc7244.robotcontrol.Westcoast;

/**
 * Created by OOTB on 1/15/2017.
 */

public abstract class PIDDriveControl {

    protected boolean debug;
    protected PIDController controller;
    protected Westcoast robot;

    public PIDDriveControl(PIDController controller, Westcoast robot) {
        this(controller, robot, false);
    }

    public PIDDriveControl(PIDController controller, Westcoast robot, boolean debug) {
        this.controller = controller;
        this.robot = robot;
        this.debug = debug;
    }

    public abstract double getReading();

    protected void control(double val, Handler handler) throws InterruptedException {
        controller.reset();
        do {
            double pid = controller.update(getReading());
            if (debug) {
                RobotLog.ii("PID",
                        "|" + controller.getProportional() * controller.getkP() +
                        "|" + controller.getIntegral() * controller.getkI() +
                        "|" + controller.getDerivative() * controller.getkD() +
                        "|" + getReading());
            }
            double offset = handler.offset();
            robot.getDriveLeft().setPower(offset + pid);
            robot.getDriveRight().setPower(offset - pid);
        } while (!handler.shouldTerminate() && !Thread.interrupted());

        robot.getDriveLeft().setPower(0);
        robot.getDriveRight().setPower(0);
    }

    public static abstract class Handler {
        protected double offset() {
            return 0;
        }

        protected abstract boolean shouldTerminate();
    }
}
