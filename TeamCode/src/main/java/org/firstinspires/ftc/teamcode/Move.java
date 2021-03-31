package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "Move", group = "")
@Disabled
public class Move extends LinearOpMode {

    public DcMotor leftMotorFront = null;
    public DcMotor leftMotorBack = null;
    public DcMotor rightMotorFront = null;
    public DcMotor rightMotorBack = null;
    public Gyro gyro = new Gyro();

    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    private boolean engageArm = false;
    private boolean engageLock = false;

    private final int RATIO = 1;
    private final double TICKS_PER_DEGREE = Values.ticksPerDegree;

    private Telemetry telemetry;

    private int lfTarget;
    private int lbTarget;
    private int rfTarget;
    private int rbTarget;

    private int lfTicks;
    private int lbTicks;
    private int rfTicks;
    private int rbTicks;

    private int halfOfField = Values.halfOfField;
    private int tileLength = Values.tileLength;



    public Move(Telemetry telemetry) {
        this.telemetry = telemetry;
    }
    Others motors = new Others(telemetry);

    @Override
    public void runOpMode() {}

    public void startToZone(int zone) {

        move(10, 0.5, "turn left");
        sleep(100);
        move(tileLength, 0.5, "forward");
        sleep(100);

        switch (zone) {
            case 0:
                move(7, 0.5, "turn right");
                sleep(100);
                move(tileLength * 2, 0.6, "forward");
                sleep(100);
                break;
            case 1:
                move(20, .5, "turn right");
                sleep(100);
                move(halfOfField, 0.6, "forward");
                sleep(100);
                break;
            case 2:
                move(7, 0.5, "turn right");
                sleep(100);
                move(halfOfField + tileLength * 2, 0.6, "forward");
                sleep(100);
                break;
        }
        move(0, 0.5, "gyro turn");

    }
    public void zoneToCorner(int zone, boolean corner) {
        move(tileLength / 2, .4, "backward");
        move(tileLength / 2, .5, "strafe left");
        if (zone == 1) {
            move(tileLength, .5, "strafe left");
        }
        if (corner) {
            move(halfOfField + tileLength * zone, .5, "backward");
        }
        else {
            move(tileLength * zone, .5, "backward");
        }
    }

    public void secondWobble(int zone){
        move((int) (tileLength * 1.5), 0.3, "strafe right");
        motors.dropArm();
        move(tileLength, 0.3, "strafe left");


        //Do more Stuff
        startToZone(zone);
        motors.resetArm();
    }

    public void initialize(HardwareMap hardwareMap) {

        leftMotorFront = hardwareMap.get(DcMotor.class, "lmf");
        leftMotorBack = hardwareMap.get(DcMotor.class, "lmb");
        rightMotorFront = hardwareMap.get(DcMotor.class, "rmf");
        rightMotorBack = hardwareMap.get(DcMotor.class, "rmb");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleLocker" );
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleArm");

        leftMotorFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftMotorBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotorFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotorBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightMotorFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotorBack.setDirection(DcMotorSimple.Direction.REVERSE);

        gyro.initGyro(hardwareMap);
        motors.initMotors(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }
    public void move(int distance, double power, String direction) {
        switch (direction.toLowerCase()) {
            case "forward":
                moveForward(distance, power);
                break;
            case "backward":
                moveBackward(distance, power);
                break;
            case "turn left":
                turnLeft((int) (distance * TICKS_PER_DEGREE), power);
                break;
            case "turn right":
                turnRight((int) (distance * TICKS_PER_DEGREE), power);
                break;
            case "strafe left":
                strafeLeft(distance, power);
                break;
            case "strafe right":
                strafeRight(distance, power);
                break;
            case "gyro turn":
                turnTo(distance, power);
            default:
                break;
        }
    }

    public void turnTo(double finalAngle, double power) {
        double currentAngle = gyro.getAngle();
        double turnAmount = gyro.getAngleDifference(currentAngle, finalAngle);
        boolean turnTowards = gyro.closerSide(currentAngle, finalAngle);

        if (!turnTowards) {
            telemetry.addData("Turning", "Left");
            telemetry.update();
            move((int) turnAmount, power, "turn left");
        }
        else {
            telemetry.addData("Turning", "Right");
            telemetry.update();
            move((int) turnAmount, power, "turn right");
        }
    }

    public void moveForward(int target, double power) {
        setTicks();

        setTargets(target, 1, 1, 1, 1);

        double initialAngle = gyro.getAngle();

        double angleAdjuster = 0;
        double multiplier = 1;
        while(opModeIsActive() && (lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget)) {
            setTicks();

            angleAdjuster = gyro.angleAdjust(initialAngle) * power * 0;
            multiplier = 1 - .2 * ((lfTicks/lfTarget + lbTicks/lbTarget + rfTicks / rfTarget + rbTicks / rbTarget) / 4);

            leftMotorFront.setPower((power + angleAdjuster) * multiplier);
            leftMotorBack.setPower((power + angleAdjuster) * multiplier);
            rightMotorFront.setPower((power - angleAdjuster) * multiplier);
            rightMotorBack.setPower((power - angleAdjuster) * multiplier);
        }

        stopMotors();
    }
    public void moveBackward(int target, double power) {
        setTicks();

        setTargets(target, -1, -1, -1, -1);

        double initialAngle = gyro.getAngle();

        double angleAdjuster;
        double multiplier = 1;
        while(opModeIsActive() && (lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget)) {
            setTicks();

            angleAdjuster = gyro.angleAdjust(initialAngle) * power;
            multiplier = 1 - .2 * ((lfTicks/lfTarget + lbTicks/lbTarget + rfTicks / rfTarget + rbTicks / rbTarget) / 4);

            leftMotorFront.setPower(-(power + angleAdjuster) * multiplier);
            leftMotorBack.setPower(-(power + angleAdjuster) * multiplier);
            rightMotorFront.setPower(-(power - angleAdjuster) * multiplier);
            rightMotorBack.setPower(-(power - angleAdjuster) * multiplier);
        }

        stopMotors();
    }
    public void turnLeft(int target, double power) {
        setTicks();

        setTargets(target, -1, -1, 1, 1);

        double angleAdjuster;
        double multiplier = 1;
        while(opModeIsActive() && (lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget)) {
            setTicks();

            leftMotorFront.setPower(-power * multiplier);
            leftMotorBack.setPower(-power * multiplier);
            rightMotorFront.setPower(power * multiplier);
            rightMotorBack.setPower(power * multiplier);
        }
        stopMotors();
    }
    public void turnRight(int target, double power) {
        setTicks();

        setTargets(target, .91, .91, -.91, -.91);

        double angleAdjuster;
        double multiplier = 1;
        while(opModeIsActive() && (lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget)) {
            setTicks();

            leftMotorFront.setPower(power * multiplier);
            leftMotorBack.setPower(power * multiplier);
            rightMotorFront.setPower(-power * multiplier);
            rightMotorBack.setPower(-power * multiplier);
        }
        stopMotors();
    }
    public void strafeLeft(int target, double power) {
        setTicks();

        setTargets(target, -1, 1, 1, -1);

        double angleAdjuster;
        double multiplier = 1;
        while(opModeIsActive() && (lfTicks > lfTarget) || (lbTicks < lbTarget) || (rfTicks < rfTarget) || (rbTicks > rbTarget)) {
            setTicks();

            leftMotorFront.setPower(-power * multiplier);
            leftMotorBack.setPower(power * multiplier);
            rightMotorFront.setPower(power * multiplier);
            rightMotorBack.setPower(-power * multiplier);
        }
        stopMotors();
    }
    public void strafeRight(int target, double power) {
        setTicks();

        setTargets(target, 1, -1, -1, 1);

        double angleAdjuster;
        double multiplier = 1;
        while(opModeIsActive() && (lfTicks < lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks < rbTarget)) {
            setTicks();

            leftMotorFront.setPower(power * multiplier);
            leftMotorBack.setPower(-power * multiplier);
            rightMotorFront.setPower(-power * multiplier);
            rightMotorBack.setPower(power * multiplier);
        }
        stopMotors();
    }
    public void setTicks() {
        lfTicks = leftMotorFront.getCurrentPosition() + 1;
        lbTicks = leftMotorBack.getCurrentPosition() + 1;
        rfTicks = rightMotorFront.getCurrentPosition() + 1;
        rbTicks = rightMotorBack.getCurrentPosition() + 1;
        if (lfTicks == 0) {

        }
    }
    //lf, lb, rf, rb are multipliers for target distance
    public void setTargets(int target, double lf, double lb, double rf, double rb) {
        lfTarget = lfTicks + (int) (lf * target) + 1;
        lbTarget = lbTicks + (int) (lb * target) + 1;
        rfTarget = rfTicks + (int) (rf * target) + 1;
        rbTarget = rbTicks + (int) (rb * target) + 1;
    }


    public void stopMotors() {
        leftMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorFront.setPower(0);
        rightMotorBack.setPower(0);
    }
}