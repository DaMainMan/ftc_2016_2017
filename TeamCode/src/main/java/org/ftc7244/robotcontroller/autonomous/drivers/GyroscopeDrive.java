package org.ftc7244.robotcontroller.autonomous.drivers;

import com.qualcomm.robotcore.hardware.LightSensor;
import com.qualcomm.robotcore.util.RobotLog;

import org.ftc7244.robotcontroller.Westcoast;
import org.ftc7244.robotcontroller.autonomous.Status;
import org.ftc7244.robotcontroller.autonomous.controllers.PIDController;
import org.ftc7244.robotcontroller.autonomous.controllers.PIDDriveControl;
import org.ftc7244.robotcontroller.autonomous.terminators.ConditionalTerminator;
import org.ftc7244.robotcontroller.autonomous.terminators.SensitivityTerminator;
import org.ftc7244.robotcontroller.autonomous.terminators.TerminationMode;
import org.ftc7244.robotcontroller.autonomous.terminators.Terminator;
import org.ftc7244.robotcontroller.autonomous.terminators.TimerTerminator;
import org.ftc7244.robotcontroller.sensor.accerometer.AccelerometerProvider;
import org.ftc7244.robotcontroller.sensor.gyroscope.GyroscopeProvider;

/**
 * This class is instrumental in the control of the robot and uses the gyroscope as a frame of
 * reference. It does not require that the gyroscope be the phone or external but uses it to
 * transform robot movement
 */
public class GyroscopeDrive extends PIDDriveControl {

    private static final double LIGHT_TUNING = 0.3;

    private GyroscopeProvider gyroProvider;
    private AccelerometerProvider accelProvider;

    /**
     * Same as the parent constructor but passes a debug as fault by default since most users will
     * not want to debug the code.
     *
     * @param robot access to motors on the robot
     * @param gyro base way to read gyroscope values
     * @param accel tells the robot when to stop
     */
    public GyroscopeDrive(Westcoast robot, GyroscopeProvider gyro, AccelerometerProvider accel) {
        this(robot, gyro, accel, false);
    }

    /**
     * Pass the robot in to get access to the hardware needed to execute and setup the motors. This
     * also comes pre-initalized with the PID tunings for the robot.
     *
     * @param robot access to motors on the robot
     * @param accel tells the robot when to stop
     * @param provider base way to read gyroscope values
     * @param debug whether to log PID result
     */
    public GyroscopeDrive(Westcoast robot, GyroscopeProvider provider, AccelerometerProvider accel, boolean debug) {
        super(new PIDController(-0.02, -0.00003, -3.25, 30, 6, 0.8), robot, debug);
        this.gyroProvider = provider;
        this.accelProvider = accel;
    }

    @Override
    public double getReading() {
        return this.gyroProvider.getZ();
    }

    /**
     * Enhanced the control function to allow the code to wait until the robot has stopped moving. The
     * control function still has the same functions but will wait until the moving status is changed.
     * Or if the code stopped is requested from the outside context.
     *
     * @param target the target value for the sensor
     * @param powerOffset power level from -1 to 1 to convert a rotate function to a drive function
     * @param terminator tells the PID when to end
     * @throws InterruptedException  if code fails to terminate on stop requested
     */
    @Override
    protected void control(double target, double powerOffset, Terminator terminator) throws InterruptedException {
        super.control(target, powerOffset, terminator);
        while (accelProvider.getStatus() == AccelerometerProvider.Status.MOVING && !Status.isStopRequested()) wait();
    }

    /**
     * This will combine the rotate function from the PID loop with a power offset. The power offset
     * then will then be added to the PID to get the drive. It is important to note that both motors
     * are reset before driving is started and will end once it reaches it's target in inches.
     *
     * @param power offset of the PID from -1 to 1
     * @param inches total distance to travel
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public void drive(double power, double inches) throws InterruptedException {
        final double ticks = inches * Westcoast.COUNTS_PER_INCH;
        Westcoast.resetMotors(robot.getDriveLeft(), robot.getDriveRight());
        if (inches <= 0) RobotLog.ee("Error", "Invalid distances!");
        final int offset = getEncoderAverage();
        control(0, power, new Terminator() {
            @Override
            public boolean shouldTerminate() {
                return Math.abs(getEncoderAverage() - offset) >= ticks;
            }
        });
    }

    /**
     * This is similar to the ${@link #drive(double, double)} but is different because it has different
     * termination requirements. This method only takes power and which sensor to read for to know
     * when to terminate driving.
     *
     * @param power offset of the PID from -1 to 1
     * @param mode which sensor to use {@link Sensor#Leading} or {@link Sensor#Trailing}
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public void driveUntilLine(double power, Sensor mode) throws InterruptedException {
        driveUntilLine(power, mode, 0);
    }

    /**
     * This is similar to ${@link #driveUntilLine(double, Sensor)} but allows for one more parameter
     * allowing for more flexible control. The offset will only be triggered once the line is seen
     * and after that the robot will travel that set distance. There is a slight offset because of
     * sensor lag.
     *
     * @param power offset of the PID from -1 to 1
     * @param mode which sensor to use {@link Sensor#Leading} or {@link Sensor#Trailing}
     * @param offsetDistance distance after line to travel in inches
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public void driveUntilLine(double power, Sensor mode, double offsetDistance) throws InterruptedException {
        driveUntilLine(power, mode, offsetDistance, 0, 0);
    }

    /**
     * Both {@link #driveUntilLine(double, Sensor)} and {@link #driveUntilLine(double, Sensor, double)}
     * feet into this function but the main difference is that this will only execute parts of the
     * code if the paramters are within a certain range value range.
     *
     * @param power offset of the PID from -1 to 1
     * @param mode which sensor to use {@link Sensor#Leading} or {@link Sensor#Trailing}
     * @param offsetDistance distance after line to travel in inches
     * @param minDistance minimum distance before searching for line to prevent early triggering in inches
     * @param maxDistance maximum distance for searching to prevent overshoot in inches
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public void driveUntilLine(double power, Sensor mode, double offsetDistance, final double minDistance, final double maxDistance) throws InterruptedException {
        Westcoast.resetMotors(robot.getDriveLeft(), robot.getDriveRight());
        if (offsetDistance <= 0) RobotLog.ee("Error", "Invalid distances!");
        final double ticks = offsetDistance * Westcoast.COUNTS_PER_INCH,
                maxTicks = maxDistance * Westcoast.COUNTS_PER_INCH,
                minTicks = minDistance * Westcoast.COUNTS_PER_INCH;
        final int encoderError = getEncoderAverage();

        control(0, power, new ConditionalTerminator(
                        new Terminator() {
                            @Override
                            public boolean shouldTerminate() {
                                return Math.abs(getEncoderAverage() - encoderError) >= maxTicks && maxTicks > 0;
                            }
                        },
                        new ConditionalTerminator(TerminationMode.AND,
                                new LineTerminator(mode == Sensor.Trailing ? robot.getTrailingLight() : robot.getLeadingLight(), encoderError, ticks),
                                new Terminator() {
                                    @Override
                                    public boolean shouldTerminate() {
                                        return Math.abs(getEncoderAverage() - encoderError) > minTicks || minTicks <= 0;
                                    }
                                }
                        )
                )
        );
    }

    /**
     * This will rotate the robot until it is within 2 degrees for 300 milliseconds. It will also
     * manually terminate if the rotate takes longer than two seconds. This is important because
     * in certain scenarios the robot can be stuck and be unable to complete the rotation.
     *
     * @param degrees target orientation in degrees
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public void rotate(double degrees) throws InterruptedException {
        double target = degrees + gyroProvider.getZ();
        control(target, 0, new ConditionalTerminator(new SensitivityTerminator(this, degrees, 2, 300), new TimerTerminator(2000)));
        resetOrientation();
    }

    /**
     * Resets the current orientation to 0 and waits till the change occurs
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public void resetOrientation() throws InterruptedException {
        gyroProvider.setZToZero();
        while (Math.abs(Math.round(gyroProvider.getZ())) > 1) Thread.yield();
    }

    private int getEncoderAverage() {
        return (robot.getDriveLeft().getCurrentPosition() + robot.getDriveRight().getCurrentPosition()) / 2;
    }

    /**
     * Which light sensor to use based off the the user input. This only allows the code to be more
     * streamlined in choice of sensor
     */
    public enum Sensor {
        Leading,
        Trailing
    }

    /**
     * Controls how far after the robot should drive if it passes the line and when to trigger that.
     * It also respects if the request to terminate was ignored and continues to only drive until
     * a specific set of parameters were fulfilled;
     */
    private class LineTerminator extends Terminator {

        private double driveAfterDistance, offset, encoderError;
        private LightSensor sensor;

        public LineTerminator(LightSensor sensor, double encoderError, double driveAfterDistance) {
            this.sensor = sensor;
            this.driveAfterDistance = driveAfterDistance;
            this.offset = 0;
            this.encoderError = encoderError;
        }

        @Override
        public boolean shouldTerminate() {
            if (sensor.getLightDetected() > LIGHT_TUNING) {
                offset = getEncoderAverage();
            } else sensor.enableLed(true);

            return status();
        }

        private boolean status() {
            return Math.abs(getEncoderAverage() - encoderError - offset) >= driveAfterDistance && offset != 0;
        }

        @Override
        public void terminated(boolean status) {
            if (!status && status()) offset = 0;
            if (status) sensor.enableLed(false);

        }
    }
}