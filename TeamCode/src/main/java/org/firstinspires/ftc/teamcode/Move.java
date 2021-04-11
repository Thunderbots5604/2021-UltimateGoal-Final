package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "Move", group = "")
@Disabled
public class Move extends LinearOpMode {

    public ElapsedTime runtime = new ElapsedTime();

    public DcMotor leftMotorFront = null;
    public DcMotor leftMotorBack = null;
    public DcMotor rightMotorFront = null;
    public DcMotor rightMotorBack = null;
    public Gyro gyro = new Gyro();

    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    private ColorSensor frontColor;
    private ColorSensor backColor;

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
        if (zone == 2) {
            this.power += .15;
        }
        motors.dropArm();
        move(halfOfField, power, "forward");
        sleep(100);
        switch (zone) {
            case 0:
                move(100, power, "forward");
                break;
            case 1:
                move(10, power * .8, "turn right");
                sleep(100);
                move(200, power, "forward");
                sleep(100);
                move(15, power * .8, "turn right");
                move((int) (tileLength * (zone + .5)) - 200, power, "forward");
                break;
            case 2:
                move((int) (tileLength * (zone + .5)), power, "forward");
                break;
        }
        motors.resetArm();
        sleep(1500);
        move(0, power, "gyro turn");
    }
    public void zoneToCorner(int zone) {
        move(100, power, "backward");
        move((tileLength / 2) - 100, power, "strafe left");
        if(zone == 1) {
            move(tileLength / 2, power, "strafe left");
        }
        move(100, power, "strafe right");
        if (zone != 0) {
            move(200, power, "strafe right");
            parkToWhite(hardwareMap, false);
            move(0, power, "gyro turn");
            move(halfOfField + 375 - (zone * 50), power, "backward");
        }
        else {
            move(halfOfField - 150, power * .75, "backward");
        }
        sleep(500);
        move(0, power, "gyro turn");
    }

    public void secondWobble(int zone){
        if (zone == 2) {
            this.power -= .15;
        }
        move(tileLength, power, "strafe right");
        move(30, power * .8, "backward");
        sleep(300);
        sideToBlue(false);
        move(10, power * .8, "strafe right");
        sleep(300);
        motors.dropArm();
        move(tileLength / 2, power * .8, "forward");
        if (zone == 0) {
            move(31, power * .9, "turn left");
            move((int) (tileLength * 3.2), power, "forward");
        }
        else if (zone == 1) {
            move(tileLength, power, "forward");
            sleep(500);
            move(10, power * .9, "gyro turn");
            move((int) (tileLength * 2.5), power, "forward");
        }
        else if (zone == 2) {
            move(3, power, "turn left");
            move(tileLength * 2, power, "forward");
            sleep(500);
            move(38, power * .9, "gyro turn");
            move((int) (tileLength * 3.7), power + .05, "forward");
        }
        sleep(500);
        
        if (zone != 2) {
            move(tileLength / 2, power, "backward");
            move(tileLength, power, "strafe right");
            move(0, power, "gyro turn");
            move(tileLength, power, "forward");
        }
        else {
            move(tileLength / 2, power, "backward");
            move(0, power, "gyro turn");
        }
        
        parkToWhite(hardwareMap, false);
    }
    public void parkToWhite(HardwareMap hardwaremap, boolean direction) {
        //true is forwards, false is backwards
        boolean running = true;
        int[] reading = new int[5];
        if (direction) {
            leftMotorFront.setPower(0.3);
            leftMotorBack.setPower(0.3);
            rightMotorFront.setPower(0.3);
            rightMotorBack.setPower(0.3);
        } else {
            leftMotorFront.setPower(-0.3);
            leftMotorBack.setPower(-0.3);
            rightMotorFront.setPower(-0.3);
            rightMotorBack.setPower(-0.3);
        }
        runtime.reset();
        while (running && runtime.milliseconds() < 2000) {
            if (direction) {
                reading = motors.getReading(backColor);
            } else {
                reading = motors.getReading(frontColor);
            }
            if (reading[3] > 100) {
                running = false;
            }
        }
        if (!direction) {
            moveForward((int) (tileLength * 0.15), power);
        } else {
            moveBackward((int) (tileLength * 0.15), power);
        }
    }
    public void sideToBlue(boolean direction) {
        //true is left, false is right
        boolean running = true;
        int[] reading = new int[5];
        if (direction) {
            leftMotorFront.setPower(-0.3);
            leftMotorBack.setPower(0.3);
            rightMotorFront.setPower(0.3);
            rightMotorBack.setPower(-0.3);
        } else {
            leftMotorFront.setPower(0.3);
            leftMotorBack.setPower(-0.3);
            rightMotorFront.setPower(-0.3);
            rightMotorBack.setPower(0.3);
        }
        runtime.reset();
        while (running && runtime.milliseconds() < 2500) {
            reading = motors.getReading(backColor);
            if (reading[2] > 100) {
                running = false;
            }
        }
        stopMotors();
    }
    public void initialize(HardwareMap hardwareMap) {

        leftMotorFront = hardwareMap.get(DcMotor.class, "lmf");
        leftMotorBack = hardwareMap.get(DcMotor.class, "lmb");
        rightMotorFront = hardwareMap.get(DcMotor.class, "rmf");
        rightMotorBack = hardwareMap.get(DcMotor.class, "rmb");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleLocker");
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleArm");

        frontColor = hardwareMap.get(ColorSensor.class, "fc");
        backColor = hardwareMap.get(ColorSensor.class, "bc");

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

        if (turnAmount < 3) {
            return;
        }
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
        runtime.reset();
        while(runtime.milliseconds() < 3000 && ((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget))) {
            setTicks();

            angleAdjuster = gyro.angleAdjust(initialAngle) * power;
            multiplier = 1 - .15 * (lfTicks / lfTarget);

            if (angleAdjuster > power / 10) {
                angleAdjuster = power / 10;
            }
            if (gyro.getAngleDifference(gyro.getAngle(), initialAngle) > 40) {
                move((int) initialAngle, power, "gyro turn");
            }

            leftMotorFront.setPower(-(power - angleAdjuster) * multiplier);
            leftMotorBack.setPower(-(power - angleAdjuster) * multiplier);
            rightMotorFront.setPower(-(power + angleAdjuster) * multiplier);
            rightMotorBack.setPower(-(power + angleAdjuster) * multiplier);
        }

        stopMotors();
    }
    public void turnLeft(int target, double power) {
        resetEncoders();
        setTicks();

        setTargets(target, -1, -1, 1, 1);

        double angleAdjuster;
        double multiplier = 1;
        runtime.reset();
        while(runtime.milliseconds() < 3000 && ((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget))) {
            setTicks();

            leftMotorFront.setPower(-power * multiplier);
            leftMotorBack.setPower(-power * multiplier);
            rightMotorFront.setPower(power * multiplier);
            rightMotorBack.setPower(power * multiplier);
        }
        stopMotors();
    }
    public void turnRight(int target, double power) {
        resetEncoders();
        setTicks();

        setTargets(target, .91, .91, -.91, -.91);

        double angleAdjuster;
        double multiplier = 1;
        runtime.reset();
        while(runtime.milliseconds() < 3000 && ((lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget))) {
            setTicks();

            leftMotorFront.setPower(power * multiplier);
            leftMotorBack.setPower(power * multiplier);
            rightMotorFront.setPower(-power * multiplier);
            rightMotorBack.setPower(-power * multiplier);
        }
        stopMotors();
    }
    public void strafeLeft(int target, double power) {
        resetEncoders();
        setTicks();

        setTargets(target, -1, 1, 1, -1);

        double angleAdjuster;
        double multiplier = 1;
        runtime.reset();
        while(runtime.milliseconds() < 3000 && ((lfTicks > lfTarget) && (lbTicks < lbTarget) && (rfTicks < rfTarget) && (rbTicks > rbTarget))) {
            setTicks();

            leftMotorFront.setPower(-power * multiplier);
            leftMotorBack.setPower(power * multiplier);
            rightMotorFront.setPower(power * multiplier);
            rightMotorBack.setPower(-power * multiplier);
        }
        stopMotors();
    }
    public void strafeRight(int target, double power) {
        resetEncoders();
        setTicks();

        setTargets(target, 1, -1, -1, 1);

        double angleAdjuster;
        double multiplier = 1;
        runtime.reset();
        while(runtime.milliseconds() < 3000 && ((lfTicks < lfTarget) && (lbTicks > lbTarget) && (rfTicks > rfTarget) && (rbTicks < rbTarget))) {
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