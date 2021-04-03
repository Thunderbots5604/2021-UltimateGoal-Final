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
    private double power = Values.power;

    public Move(Telemetry telemetry) {
        this.telemetry = telemetry;
    }
    Others motors = new Others(telemetry);

    @Override
    public void runOpMode() {}

    public void startToZone(int zone) {
        switch (zone) {
            case 0:
                move(halfOfField, power, "forward");
                break;
            case 1:
                move(halfOfField, power, "forward");
                sleep(100);
                move((int) (tileLength * (zone + .5)), power * .9, "forward");
                sleep(100);
                move((int) (tileLength * .8), power, "strafe right");
                break;
            case 2:
                move(halfOfField, power, "forward");
                sleep(100);
                move((int) (tileLength * (zone + .5)), power * .9, "forward");
                break;
            default:
                break;
        }
        move(100, power, "strafe left");
        motors.resetArm();
    }
    public void zoneToCorner(int zone, boolean corner) {
        move(tileLength / 2, power, "backward");
        move(tileLength / 2, .5, "strafe left");
        if (zone == 1) {
            move(tileLength, .5, "strafe left");
        }
        if (corner) {
            move(halfOfField, power, "backward");
        }
        move((tileLength * zone) - 150, power * .7, "backward");
    }

    public void secondWobble(int zone){
        motors.resetArm();
        move((int) (tileLength * 2.75), power, "strafe right");
        sleep(300);
        motors.dropArm();
        sleep(1000);
        move((int) (tileLength * 2.8), power, "strafe left");
        
        //Goes back to zone
        startToZone(zone);
        motors.resetArm();
    }

    public void initialize(HardwareMap hardwareMap) {

        leftMotorFront = hardwareMap.get(DcMotor.class, "lmf");
        leftMotorBack = hardwareMap.get(DcMotor.class, "lmb");
        rightMotorFront = hardwareMap.get(DcMotor.class, "rmf");
        rightMotorBack = hardwareMap.get(DcMotor.class, "rmb");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleLocker");
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
        resetEncoders();
        setTicks();

        setTargets(target, 1, 1, 1, 1);

        double initialAngle = gyro.getAngle();

        double angleAdjuster;
        double multiplier = 1;
        while(((lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget))) {
            setTicks();

            angleAdjuster = gyro.angleAdjust(initialAngle);
            multiplier = 1 - .15 * ((double) lfTicks / lfTarget);

            if (angleAdjuster > power / 10) {
                angleAdjuster = power / 10;
            }
            if (gyro.getAngleDifference(gyro.getAngle(), initialAngle) > 40) {
                move((int) initialAngle, power, "gyro turn");
            }

            leftMotorFront.setPower((power + angleAdjuster) * multiplier);
            leftMotorBack.setPower((power + angleAdjuster) * multiplier);
            rightMotorFront.setPower((power - angleAdjuster) * multiplier);
            rightMotorBack.setPower((power - angleAdjuster) * multiplier);

            telemetry.addData("lfTicks: ", lfTicks);
            telemetry.addData("lfTarget: ", lfTarget);
            telemetry.addData("multiplier: ", multiplier);
            telemetry.update();
        }
        stopMotors();
    }
    public void moveBackward(int target, double power) {
        resetEncoders();
        setTicks();

        setTargets(target, -1, -1, -1, -1);

        double initialAngle = gyro.getAngle();

        double angleAdjuster;
        double multiplier = 1;
        while(((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget))) {
            setTicks();

            angleAdjuster = gyro.angleAdjust(initialAngle) * power;
            multiplier = 1 - .15 * (lfTicks / lfTarget);

            if (angleAdjuster > power / 10) {
                angleAdjuster = power / 10;
            }
            if (gyro.getAngleDifference(gyro.getAngle(), initialAngle) > 40) {
                move((int) initialAngle, power, "gyro turn");
            }

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
        while(((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget))) {
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
        while(((lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget))) {
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
        while(((lfTicks > lfTarget) && (lbTicks < lbTarget) && (rfTicks < rfTarget) && (rbTicks > rbTarget))) {
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
        while((lfTicks < lfTarget) && (lbTicks > lbTarget) && (rfTicks > rfTarget) && (rbTicks < rbTarget)) {
            setTicks();

            leftMotorFront.setPower(power * multiplier);
            leftMotorBack.setPower(-power * multiplier);
            rightMotorFront.setPower(-power * multiplier);
            rightMotorBack.setPower(power * multiplier);
        }
        stopMotors();
    }
    public void setTicks() {
        lfTicks = leftMotorFront.getCurrentPosition();
        lbTicks = leftMotorBack.getCurrentPosition();
        rfTicks = rightMotorFront.getCurrentPosition();
        rbTicks = rightMotorBack.getCurrentPosition();
    }
    //lf, lb, rf, rb are multipliers for target distance
    public void setTargets(int target, double lf, double lb, double rf, double rb) {
        lfTarget = lfTicks + (int) (lf * target);
        lbTarget = lbTicks + (int) (lb * target);
        rfTarget = rfTicks + (int) (rf * target);
        rbTarget = rbTicks + (int) (rb * target);
    }

    public void resetEncoders() {
        leftMotorFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftMotorBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorFront.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotorBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftMotorFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftMotorBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotorFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotorBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void stopMotors() {
        leftMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorFront.setPower(0);
        rightMotorBack.setPower(0);
    }
}
