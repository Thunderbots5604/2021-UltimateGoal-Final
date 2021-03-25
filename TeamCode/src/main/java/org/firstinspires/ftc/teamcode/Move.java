package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
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

    @Override
    public void runOpMode() {}
    public void initialize() {
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

        gyro.initGyro();
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
        double turnAmount = gyro.getAngleDifference(gyro.getAngle(), finalAngle);
        boolean turnTowards = gyro.closerSide(gyro.getAngle(), finalAngle);

        if (turnTowards) {
            move((int) turnAmount, power, "turn right");
        }
        else {
            move((int) turnAmount, power, "turn left");
        }
    }

    public void moveForward(int target, double power) {
        int lfTicks = leftMotorFront.getCurrentPosition();
        int lbTicks = leftMotorBack.getCurrentPosition();
        int rfTicks = rightMotorFront.getCurrentPosition();
        int rbTicks = rightMotorBack.getCurrentPosition();

        int lfTarget = lfTicks + target;
        int lbTarget = lbTicks + target;
        int rfTarget = rfTicks + target;
        int rbTarget = rbTicks + target;

        double initialAngle = gyro.getAngle();

        double angleAdjuster;
        double multiplier = 1;
        while((lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget)) {
            lfTicks = leftMotorFront.getCurrentPosition();
            lbTicks = leftMotorBack.getCurrentPosition();
            rfTicks = rightMotorFront.getCurrentPosition();
            rbTicks = rightMotorBack.getCurrentPosition();

            angleAdjuster = gyro.angleAdjust(initialAngle) * power;
            multiplier = 1 - .2 * ((lfTicks/lfTarget + lbTicks/lbTarget + rfTicks / rfTarget + rbTicks / rbTarget) / 4);

            leftMotorFront.setPower((power + angleAdjuster) * multiplier);
            leftMotorBack.setPower((power + angleAdjuster) * multiplier);
            rightMotorFront.setPower((power - angleAdjuster) * multiplier);
            rightMotorBack.setPower((power - angleAdjuster) * multiplier);
        }

        leftMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorFront.setPower(0);
        rightMotorBack.setPower(0);
    }
    public void moveBackward(int target, double power) {
        int lfTicks = leftMotorFront.getCurrentPosition();
        int lbTicks = leftMotorBack.getCurrentPosition();
        int rfTicks = rightMotorFront.getCurrentPosition();
        int rbTicks = rightMotorBack.getCurrentPosition();

        int lfTarget = lfTicks - target;
        int lbTarget = lbTicks - target;
        int rfTarget = rfTicks - target;
        int rbTarget = rbTicks - target;

        double initialAngle = gyro.getAngle();

        double angleAdjuster;
        double multiplier = 1;
        while((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget)) {
            lfTicks = leftMotorFront.getCurrentPosition();
            lbTicks = leftMotorBack.getCurrentPosition();
            rfTicks = rightMotorFront.getCurrentPosition();
            rbTicks = rightMotorBack.getCurrentPosition();

            angleAdjuster = gyro.angleAdjust(initialAngle) * power;
            multiplier = 1 - .2 * ((lfTicks/lfTarget + lbTicks/lbTarget + rfTicks / rfTarget + rbTicks / rbTarget) / 4);

            leftMotorFront.setPower(-(power + angleAdjuster) * multiplier);
            leftMotorBack.setPower(-(power + angleAdjuster) * multiplier);
            rightMotorFront.setPower(-(power - angleAdjuster) * multiplier);
            rightMotorBack.setPower(-(power - angleAdjuster) * multiplier);
        }

        leftMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorFront.setPower(0);
        rightMotorBack.setPower(0);
    }
    public void turnLeft(int target, double power) {
            int lfTicks = leftMotorFront.getCurrentPosition();
            int lbTicks = leftMotorBack.getCurrentPosition();
            int rfTicks = rightMotorFront.getCurrentPosition();
            int rbTicks = rightMotorBack.getCurrentPosition();

            int lfTarget = lfTicks - target;
            int lbTarget = lbTicks - target;
            int rfTarget = rfTicks + target;
            int rbTarget = rbTicks + target;

            double angleAdjuster;
            double multiplier = 1;
            while((lfTicks > lfTarget) || (lbTicks > lbTarget) || (rfTicks < rfTarget) || (rbTicks < rbTarget)) {
                lfTicks = leftMotorFront.getCurrentPosition();
                lbTicks = leftMotorBack.getCurrentPosition();
                rfTicks = rightMotorFront.getCurrentPosition();
                rbTicks = rightMotorBack.getCurrentPosition();

                leftMotorFront.setPower(-power * multiplier);
                leftMotorBack.setPower(-power * multiplier);
                rightMotorFront.setPower(power * multiplier);
                rightMotorBack.setPower(power * multiplier);
            }
            leftMotorFront.setPower(0);
            leftMotorBack.setPower(0);
            rightMotorFront.setPower(0);
            rightMotorBack.setPower(0);
    }
    public void turnRight(int target, double power) {
        int lfTicks = leftMotorFront.getCurrentPosition();
        int lbTicks = leftMotorBack.getCurrentPosition();
        int rfTicks = rightMotorFront.getCurrentPosition();
        int rbTicks = rightMotorBack.getCurrentPosition();

        int lfTarget = lfTicks + target;
        int lbTarget = lbTicks + target;
        int rfTarget = rfTicks - target;
        int rbTarget = rbTicks - target;

        double angleAdjuster;
        double multiplier = 1;
        while((lfTicks < lfTarget) || (lbTicks < lbTarget) || (rfTicks > rfTarget) || (rbTicks > rbTarget)) {
            lfTicks = leftMotorFront.getCurrentPosition();
            lbTicks = leftMotorBack.getCurrentPosition();
            rfTicks = rightMotorFront.getCurrentPosition();
            rbTicks = rightMotorBack.getCurrentPosition();

            leftMotorFront.setPower(power * multiplier);
            leftMotorBack.setPower(power * multiplier);
            rightMotorFront.setPower(-power * multiplier);
            rightMotorBack.setPower(-power * multiplier);
        }
        leftMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorFront.setPower(0);
        rightMotorBack.setPower(0);
    }
    public void strafeLeft(int target, double power) {
        int lfTicks = leftMotorFront.getCurrentPosition();
        int lbTicks = leftMotorBack.getCurrentPosition();
        int rfTicks = rightMotorFront.getCurrentPosition();
        int rbTicks = rightMotorBack.getCurrentPosition();

        int lfTarget = lfTicks - target;
        int lbTarget = lbTicks + target;
        int rfTarget = rfTicks + target;
        int rbTarget = rbTicks - target;

        double angleAdjuster;
        double multiplier = 1;
        while((lfTicks > lfTarget) || (lbTicks < lbTarget) || (rfTicks < rfTarget) || (rbTicks > rbTarget)) {
            lfTicks = leftMotorFront.getCurrentPosition();
            lbTicks = leftMotorBack.getCurrentPosition();
            rfTicks = rightMotorFront.getCurrentPosition();
            rbTicks = rightMotorBack.getCurrentPosition();

            multiplier = 1 - .2 * ((lfTarget/lfTicks + lbTicks/lbTarget + rfTicks / rfTarget + rbTarget / rbTicks) / 4);

            leftMotorFront.setPower(-power * multiplier);
            leftMotorBack.setPower(power * multiplier);
            rightMotorFront.setPower(power * multiplier);
            rightMotorBack.setPower(-power * multiplier);
        }
        leftMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorFront.setPower(0);
        rightMotorBack.setPower(0);
    }
    public void strafeRight(int target, double power) {
        int lfTicks = leftMotorFront.getCurrentPosition();
        int lbTicks = leftMotorBack.getCurrentPosition();
        int rfTicks = rightMotorFront.getCurrentPosition();
        int rbTicks = rightMotorBack.getCurrentPosition();

        int lfTarget = lfTicks + target;
        int lbTarget = lbTicks - target;
        int rfTarget = rfTicks - target;
        int rbTarget = rbTicks + target;

        double angleAdjuster;
        double multiplier = 1;
        while((lfTicks < lfTarget) || (lbTicks > lbTarget) || (rfTicks > rfTarget) || (rbTicks < rbTarget)) {
            lfTicks = leftMotorFront.getCurrentPosition();
            lbTicks = leftMotorBack.getCurrentPosition();
            rfTicks = rightMotorFront.getCurrentPosition();
            rbTicks = rightMotorBack.getCurrentPosition();

            multiplier = 1 - .2 * ((lfTicks/lfTarget + lbTarget/lbTicks + rfTarget / rfTicks + rbTicks / rbTarget) / 4);

            leftMotorFront.setPower(power * multiplier);
            leftMotorBack.setPower(-power * multiplier);
            rightMotorFront.setPower(-power * multiplier);
            rightMotorBack.setPower(power * multiplier);
        }
        leftMotorFront.setPower(0);
        leftMotorBack.setPower(0);
        rightMotorFront.setPower(0);
        rightMotorBack.setPower(0);
    }
}