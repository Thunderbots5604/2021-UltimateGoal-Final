package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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

    private final int RATIO = 1;
    private final double TICKS_PER_DEGREE = 6.9;

    private Telemetry telemetry;

    private int lfTarget;
    private int lbTarget;
    private int rfTarget;
    private int rbTarget;

    private int lfTicks;
    private int lbTicks;
    private int rfTicks;
    private int rbTicks;

    @Override
    public void runOpMode() {}
    public void initialize(HardwareMap hardwareMap, Telemetry telemetry) {

        this.telemetry = telemetry;

        leftMotorFront = hardwareMap.get(DcMotor.class, "lmf");
        leftMotorBack = hardwareMap.get(DcMotor.class, "lmb");
        rightMotorFront = hardwareMap.get(DcMotor.class, "rmf");
        rightMotorBack = hardwareMap.get(DcMotor.class, "rmb");

        leftMotorFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftMotorBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotorFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotorBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightMotorFront.setDirection(DcMotorSimple.Direction.REVERSE);
        rightMotorBack.setDirection(DcMotorSimple.Direction.REVERSE);

        gyro.initGyro(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }
    public void move(int distance, double power, String direction) {
        switch (direction) {
            case "forward":
                moveForward(distance, power);
                break;
            case "backward":
                moveBackward(distance, power);
                break;
            case "turn left":
                turnLeft(distance, power);
                break;
            case "turn right":
                turnRight(distance, power);
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
        double turnAmount = gyro.getAngleDifference(currentAngle, finalAngle) * TICKS_PER_DEGREE;
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
        while((lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget)) {
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
        while((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget)) {
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
        while((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget)) {
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
        while((lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget)) {
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
        while((lfTicks > lfTarget) || (lbTicks < lbTarget) || (rfTicks < rfTarget) || (rbTicks > rbTarget)) {
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
        while((lfTicks < lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks < rbTarget)) {
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