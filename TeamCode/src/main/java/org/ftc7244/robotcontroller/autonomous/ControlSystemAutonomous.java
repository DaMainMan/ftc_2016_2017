package org.ftc7244.robotcontroller.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftc7244.datalogger.Logger;
import org.ftc7244.robotcontroller.autonomous.drivers.PIDGyroscopeDrive;
import org.ftc7244.robotcontroller.autonomous.drivers.SPGyroscopeDrive;
import org.ftc7244.robotcontroller.files.FileManager;
import org.ftc7244.robotcontroller.hardware.Westcoast;
import org.ftc7244.robotcontroller.sensor.gyroscope.GyroscopeProvider;
import org.ftc7244.robotcontroller.sensor.gyroscope.RevIMUGyroscopeProvider;
import org.ftc7244.robotcontroller.sensor.vuforia.ImageTransformProvider;

import java.io.IOException;

/**
 * Contains all the code for different drive types including ${@link PIDGyroscopeDrive}.
 * Not much happens here beyond the essentials for each control method. It also
 * automatically handles wait for startImageReading since most of the setup is completed and only driving
 * instructions are needed.
 */
public abstract class ControlSystemAutonomous extends LinearOpMode {

    public final static long AUTONOMOUS_DURATION = 30 * 1000;

    protected final GyroscopeProvider gyroProvider;

    public PIDGyroscopeDrive gyroscopePID;
    protected final SPGyroscopeDrive gyroscopeSP;

    protected final ImageTransformProvider imageProvider;

    protected Westcoast robot;
    private long end;
    public double p, i, d;
    String[] pid;
    byte[] buffer = new byte[64];
    public FileManager fileManager;
    /**
     * Loads hardware, pid drives, and sensor providers
     */
    protected ControlSystemAutonomous() {
        robot = new Westcoast(this);

        gyroProvider = new RevIMUGyroscopeProvider();
        imageProvider = new ImageTransformProvider(robot);

        gyroscopePID = new PIDGyroscopeDrive(robot, gyroProvider);
        gyroscopeSP = new SPGyroscopeDrive(robot, gyroProvider);
    }

    @Override
    public void runOpMode(){
        Logger.init();
        fileManager = new FileManager(hardwareMap.appContext);
        try {
            fileManager.initialize();
        } catch (IOException e) {
            telemetry.addData("ERROR", e.getMessage());
            telemetry.update();
        }
        try {
            fileManager.readFile(buffer);
        } catch (IOException e) {
            telemetry.addData("ERROR", e.getMessage());
            telemetry.update();
        }
        pid = (new String(buffer)).split(",");
        telemetry.addData("List", pid[1]);
        telemetry.update();
        //p = Double.valueOf(pid[0]);
        //i = Double.valueOf(pid[1]);
        //d = Double.valueOf(pid[2]);
        robot.init();
        robot.initServos();
        robot.getDriveBackLeft().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.getDriveBackRight().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.getDriveFrontLeft().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.getDriveFrontRight().setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.getDriveBackLeft().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.getDriveBackRight().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.getDriveFrontLeft().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        robot.getDriveFrontRight().setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        Status.setAutonomous(this);
        //Initializes robot and debug features

        try {
            gyroProvider.start(hardwareMap);
            imageProvider.start(hardwareMap);
            while (!isStarted()) {
                if (gyroProvider.isCalibrated()) {
                    telemetry.addLine("Gyroscope calibrated!");
                    telemetry.update();
                } else  {
                    telemetry.addLine("No Connection");
                    telemetry.update();
                }
                idle();
            }

            gyroscopePID.resetOrientation();
            gyroscopeSP.resetOrientation();
            end = System.currentTimeMillis() + AUTONOMOUS_DURATION;
            //Calibrates and starts providers
            run();
        } catch (Throwable t) {
            RobotLog.e(t.getMessage());
            t.printStackTrace();
            //Logs unexpected errors to prevent app crashing
        } finally {
            gyroProvider.stop();
            imageProvider.stop();
            Status.setAutonomous(null);
            //Stops all providers regardless of error
        }
    }

    public long getAutonomousEnd() {
        return end;
    }

    protected void outtake(){
        robot.getIntakeBottomRight().setPower(0.5);
        robot.getIntakeBottomLeft().setPower(0.5);
        robot.getIntakeTop().setPower(1);
        robot.getIntakeBottom().setPower(1);
        robot.getIntakePusher().setPosition(Westcoast.INTAKE_PUSHER_OUT);
        sleep(750);
        robot.getIntakeBottomRight().setPower(0);
        robot.getIntakeBottomLeft().setPower(0);
    }

    public void updatePID(double p, double i, double d){
        gyroscopePID = new PIDGyroscopeDrive(robot, gyroProvider);
    }

    /**
     * The custom autonomous procedure
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public abstract void run();

}