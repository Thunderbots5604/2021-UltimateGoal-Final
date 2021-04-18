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

    private double[] motorTicks = new double[4];
    private double[] currentTicks = new double[4];

    private int halfOfField = Values.halfOfField;
    private int tileLength = Values.tileLength;
    private double power = Values.power;

    public Move(Telemetry telemetry) {
        this.telemetry = telemetry;
    }
    Others motors = new Others(telemetry);

    @Override
    public void runOpMode() {}

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
    public void move(double distance, double power, String direction) {
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
            move(turnAmount, power, "turn left");
        }
        else {
            move(turnAmount, power, "turn right");
        }
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
    public void moveForward(double distance, double power) {
        moveToTicks(new double[]{distance, distance, distance, distance}, power);
    }
    public void moveBackward(double distance, double power) {
        moveToTicks(new double[]{-distance, -distance, -distance, -distance}, power);
    }
    public void turnLeft(double distance, double power) {
        moveToTicks(new double[]{-distance, -distance, distance, distance}, power);
    }
    public void turnRight(double distance, double power) {
        moveToTicks(new double[]{distance, distance, -distance, -distance}, power);
    }
    public void strafeLeft(double distance, double power) {
        moveToTicks(new double[]{-distance, distance, distance, -distance}, power);
    }
    public void strafeRight(double distance, double power) {
        moveToTicks(new double[]{distance, -distance, -distance, distance}, power);
    }
    public void moveToTicks(double[] targets, double power) {
        resetEncoders();
        double max = Math.max(targets[0], targets[1]);max = Math.max(max, targets[2]); max = Math.max(max, targets[3]);
        setPowers(power * (targets[0] / max), power * (targets[1] / max), power * (targets[2] / max), power * (targets[3] / max));
        while (true/*opModeIsActive()*/) {
            setTicks();
            for (int i = 0; i < motorTicks.length; i++) {
                currentTicks[i] = Math.abs(motorTicks[i]);
                if (currentTicks[i] > Math.abs(targets[i])) {
                    stopMotors();
                    break;
                }
            }
        }
    }
    public void setTicks() {
        motorTicks[0] = leftMotorFront.getCurrentPosition();
        motorTicks[1] = leftMotorBack.getCurrentPosition();
        motorTicks[2] = rightMotorFront.getCurrentPosition();
        motorTicks[3] = rightMotorBack.getCurrentPosition();
    }
    public void stopMotors() {
        setPowers(0, 0, 0, 0);
    }
    public void setPowers(double lmf, double lmb, double rmf, double rmb) {
        leftMotorFront.setPower(lmf);
        leftMotorBack.setPower(lmb);
        rightMotorFront.setPower(rmf);
        rightMotorBack.setPower(rmb);
    }
}