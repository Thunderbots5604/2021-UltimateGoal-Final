package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Autonomous(name = "Gyro", group = "")
@Disabled
public class Gyro extends LinearOpMode {

    public BNO055IMU imu;
    public Orientation angles;
    public double heading = 0;

    public ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {}
    public void initGyro(HardwareMap hardwareMap) {

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
    }
    public double formatAngle(AngleUnit angleUnit, double angle) {
        return AngleUnit.DEGREES.fromUnit(angleUnit, angle);
    }

    public double getAngle() {
        heading = 0;
        runtime.reset();
        while (runtime.milliseconds() < 1500 && heading == 0) {
            angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            heading = formatAngle(angles.angleUnit, angles.firstAngle);
        }
        return heading;
    }

    public double getAngleDifference(double angle1, double angle2) {
        double tempAngle = angle1;
        double angleCheck;
        //Set angle1 as smaller
        if (angle1 > angle2) {
            angle1 = angle2;
            angle2 = tempAngle;
        }
        angleCheck = angle2 - angle1;
        if (angleCheck > 180) {
            angleCheck = 360 - angleCheck;
        }
        angleCheck = Math.abs(angleCheck);
        return angleCheck;
    }

    public boolean closerSide (double currentAngle, double targetAngle) {
        int side = 1;
        if (currentAngle > targetAngle) {
            side = 2;
        }
        if (side == 1) {
            if (targetAngle - currentAngle < 180) {
                //Left
                return false;
            }
            else {
                //Right
                return true;
            }
        }
        else {
            if (currentAngle - targetAngle < 180) {
                //Right
                return true;
            }
            else {
                //Left
                return false;
            }
        }
    }
    public double angleAdjust(double initialAngle) {
        double currentAngle = this.getAngle();

        double angleDiff = this.getAngleDifference(initialAngle, currentAngle);
        boolean side = this.closerSide(initialAngle, currentAngle);

        //TODO: make this make sense
        double backOnTrackMultiplier = angleDiff * Values.gyroAdjust;
        if (side) {
            backOnTrackMultiplier *= -1;
        }
        return backOnTrackMultiplier;
    }
}