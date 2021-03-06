package org.ftc7244.robotcontroller.hardware;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.qualcomm.hardware.kauailabs.NavxMicroNavigationSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.ftc7244.robotcontroller.Debug;
import org.ftc7244.robotcontroller.autonomous.Status;

public class Westcoast extends Hardware {
    public static final double COUNTS_PER_INCH = (403.2 / (3.9 * Math.PI)),
                               RELIC_SPOOL_MIN = -1857, RELIC_SPOOL_MAX = 0, INTAKE_PUSHER_OUT = 0.69;

    @Nullable
    private DcMotor driveBackLeft, driveFrontLeft, driveBackRight, driveFrontRight, intakeLift, intakeTop, intakeBottom, relicSpool;
    @Nullable
    private CRServo intakeBottomLeft, intakeBottomRight, intakeTopLeft, intakeTopRight;
    @Nullable
    private Servo jewelVertical, jewelHorizontal, spring, intakeServo, relicWrist, relicFinger, intakePusher;
    @Nullable
    private AnalogInput bottomIntakeSwitch;
    @Nullable
    private ColorSensor jewelSensor, driveColor;
    @Nullable
    private DistanceSensor jewelDistance;
    @Nullable
    private NavxMicroNavigationSensor navX;

    public Westcoast(OpMode opMode) {
        super(opMode, COUNTS_PER_INCH);
    }
    private int blueOffset, redOffset;

    /**
     * Identify hardware and then set it up with different objects. Other initialization properties are
     * mset to ensure that everything is in the default position or correct mode for the robot.
     */
    @Override
    public void init() {
        //Initialize or nullify all hardware
        HardwareMap map = opMode.hardwareMap;

        this.jewelSensor = getOrNull(map.colorSensor, "jewelSensor");
        this.driveColor = getOrNull(map.colorSensor, "driveColor");
        this.jewelDistance = map.get(DistanceSensor.class, "jewelSensor");
        this.spring = getOrNull(map.servo, "spring");
        this.intakeServo = getOrNull(map.servo, "intakeServo");
        this.intakeLift = getOrNull(map.dcMotor, "vertical");

        this.driveBackLeft = getOrNull(map.dcMotor, "driveBackLeft");
        this.driveFrontLeft = getOrNull(map.dcMotor, "driveFrontLeft");
        this.driveBackRight = getOrNull(map.dcMotor, "driveBackRight");
        this.driveFrontRight = getOrNull(map.dcMotor, "driveFrontRight");

        this.intakeBottomLeft = getOrNull(map.crservo, "intakeBLeft");
        this.intakeBottomRight = getOrNull(map.crservo, "intakeBRight");
        this.intakeTopLeft = getOrNull(map.crservo, "intakeTLeft");
        this.intakeTopRight = getOrNull(map.crservo, "intakeTRight");
        this.intakeServo = getOrNull(map.servo, "intakeServo");

        this.intakeTop = getOrNull(map.dcMotor, "intakeT");
        this.intakeBottom = getOrNull(map.dcMotor, "intakeB");

        this.jewelVertical = getOrNull(map.servo, "jewelVertical");
        this.jewelHorizontal = getOrNull(map.servo, "jewelHorizontal");

        this.relicSpool = getOrNull(map.dcMotor, "spooler");
        this.relicWrist = getOrNull(map.servo, "relicArm");
        this.relicFinger = getOrNull(map.servo, "relicClaw");

        this.bottomIntakeSwitch = getOrNull(map.analogInput, "bottomIntakeSwitch");
        this.intakePusher = getOrNull(map.servo, "intakeKicker");


        //Set the default direction for all the hardware and also initialize default positions
        if (driveFrontLeft != null) driveFrontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        if (driveFrontRight != null) driveFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        if (jewelSensor != null) {
            redOffset = jewelSensor.red();
            blueOffset = jewelSensor.blue();
        } else {
            redOffset = 0;
            blueOffset = 0;
        }
        if(intakeTopRight != null) intakeTopRight.setDirection(DcMotorSimple.Direction.REVERSE);
        if(intakeBottomRight != null) intakeBottomRight.setDirection(DcMotorSimple.Direction.REVERSE);
        resetMotors(relicSpool, intakeLift);
    }

    public void initServos(){
        if(jewelVertical != null) jewelVertical.setPosition(0.67);
        if(jewelHorizontal != null) jewelHorizontal.setPosition(0.73);
        if(relicWrist != null) relicWrist.setPosition(0.2);
        if(spring != null){
            spring.setPosition(1);
            spring.setDirection(Servo.Direction.FORWARD);
        }
        if(intakeServo != null) intakeServo.setPosition(0.2);
        if(intakePusher != null) intakePusher.setPosition(0.5);
    }

    public void driveIntakeVertical(double power){
        intakeBottomLeft.setPower(power);
        intakeBottomRight.setPower(power);
        intakeTopLeft.setPower(power);
        intakeTopRight.setPower(power);
    }

    @Override
    public void drive(double leftPower, double rightPower, long timeMillis){
       drive(leftPower, -rightPower);
        try {
            sleep(timeMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        drive(0, 0);
    }

    @Override
    public void drive(double leftPower, double rightPower) {
        driveFrontLeft.setPower(leftPower);
        driveBackLeft.setPower(leftPower);
        driveFrontRight.setPower(-rightPower);
        driveBackRight.setPower(-rightPower);
    }


    @Override
    public void driveToInch(double power, double inches){
        resetDriveMotors();
        double target = inches * COUNTS_PER_INCH;
        drive(power, power);
        while(!Status.isStopRequested() && getDriveEncoderAverage() <= target);
        drive(0, 0);
        driveBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void resetDriveMotors() {
        resetMotors(driveBackLeft, driveBackRight, driveFrontLeft, driveFrontRight);
    }

    @Override
    public int getDriveEncoderAverage() {
        return -(-driveBackLeft.getCurrentPosition()+driveBackRight.getCurrentPosition()-driveFrontLeft.getCurrentPosition()+driveFrontRight.getCurrentPosition())/4;
    }

    @Override
    public void resetDriveEncoders() {
        driveBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        try {
            sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driveBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     *
     * @param color the color to which the sensor input is to be determined
     * @return whether the given color is equal to the sensor input
     */
    public boolean isColor(int color) {
        int blue = jewelSensor.blue() - blueOffset, red = jewelSensor.red() - redOffset;
        if (Debug.STATUS)
            RobotLog.ii("COLOR", blue + "(" + blueOffset + ")" + ":" + red + "(" + redOffset + ")");
        switch (color) {
            case Color.BLUE:
                return blue > red;
            case Color.RED:
                return blue < red;
            default:
                RobotLog.e("Color does not exist!");
                return false;
        }
    }

    /**
     * Lowers the vertical jewel arm, reads the jewel color on the right, and moves
     * the horizontal jewel arm left or right depending on the given parameter
     *
     * @param color color jewel to be knocked off the pedestal
     * @throws InterruptedException if code fails to terminate on stop requested
     */
    public void knockOverJewel(int color) {
        //color we want to get rid of
        getJewelVertical().setPosition(0.16);
        getJewelHorizontal().setPosition(0.45);
        long lastTime = System.currentTimeMillis();
        while (System.currentTimeMillis()-lastTime<=1700){
            double grayscale = ( (0.3 * getJewelSensor().red()) + (0.59 * getJewelSensor().green()) + (0.11 * getJewelSensor().blue()) );
            opMode.telemetry.addData("R", getJewelSensor().red());
            opMode.telemetry.addData("G", getJewelSensor().green());
            opMode.telemetry.addData("B", getJewelSensor().blue());
            opMode.telemetry.addData("Distance (INCH)", getJewelDistance().getDistance(DistanceUnit.INCH));
            opMode.telemetry.addData("Grayscale", grayscale);
            opMode.telemetry.update();
        }
        if(color==Color.RED)
            getJewelHorizontal().setPosition(isColor(Color.RED) ? 0.33 : 0.56);
        else if(color==Color.BLUE)
            getJewelHorizontal().setPosition(isColor(Color.RED) ? 0.56 : 0.33);
        try {
            sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getJewelHorizontal().setPosition(0.73);
        getJewelVertical().setPosition(0.67);

    }

    @Nullable
    public DcMotor getDriveFrontLeft() {
        return this.driveFrontLeft;
    }

    @Nullable
    public DcMotor getDriveBackLeft() {
        return this.driveBackLeft;
    }

    @Nullable
    public DcMotor getIntakeLift(){return this.intakeLift;}

    @Nullable
    public DcMotor getDriveFrontRight() {
        return this.driveFrontRight;
    }

    @Nullable
    public DcMotor getDriveBackRight() {
        return this.driveBackRight;
    }

    @Nullable
    public CRServo getIntakeBottomLeft() {
        return this.intakeBottomLeft;
    }

    @Nullable
    public CRServo getIntakeBottomRight() {
        return this.intakeBottomRight;
    }
    @Nullable
    public CRServo getIntakeTopLeft(){
        return this.intakeTopLeft;
    }

    @Nullable
    public DcMotor getIntakeBottom() {
        return intakeBottom;
    }

    @Nullable
    public DcMotor getIntakeTop() {
        return intakeTop;
    }

    @Nullable
    public CRServo getIntakeTopRight() {
        return intakeTopRight;
    }

    @Nullable
    public Servo getSpring(){
        return this.spring;
    }

    @Nullable
    public Servo getJewelVertical(){
        return this.jewelVertical;
    }

    @Nullable
    public Servo getJewelHorizontal(){
        return this.jewelHorizontal;
    }

    @Nullable
    public AnalogInput getBottomIntakeSwitch(){
        return bottomIntakeSwitch;
    }

    @Nullable
    public ColorSensor getJewelSensor(){
        return jewelSensor;
    }

    @Nullable
    public Servo getIntakeServo(){
        return intakeServo;
    }

    @Nullable
    public DistanceSensor getJewelDistance(){
        return jewelDistance;
    }

    @Nullable
    public NavxMicroNavigationSensor getNavX(){
        return navX;
    }

    @Nullable
    public DcMotor getRelicSpool() {
        return relicSpool;
    }

    @Nullable
    public Servo getRelicWrist() {
        return relicWrist;
    }

    @Nullable
    public Servo getRelicFinger() {
        return relicFinger;
    }

    @Nullable
    public Servo getIntakePusher() {
        return intakePusher;
    }

    @Nullable
    public ColorSensor getDriveColor() {
        return driveColor;
    }
}