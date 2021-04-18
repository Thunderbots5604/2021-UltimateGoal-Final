package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.math.Point;

@TeleOp(name = "Jek's testing teleop", group = "testing")

public class JackTeleOpTest extends OpMode{
    private MecanumDrive drive;

    @Override
    public void init() {
        drive = new MecanumDrive(hardwareMap, "lmf", "rmf", "lmb", "rmb");

        drive.resetPowerValues();

        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());

        telemetry.addData("All booted up and ready to go!", null);
        telemetry.update();
    }

    @Override
    public void loop() {
        drive.resetPowerValues();
        cheat(gamepad1.left_stick_x, gamepad1.left_stick_y);
        drive.radialMove(gamepad1.right_stick_x);
        drive.updateMotorPowers();
        telemetry.addData("lmf power", drive.getFrontLeftMotorPower());
        telemetry.addData("rmf power", drive.getFrontRightMotorPower());
        telemetry.addData("lmb power", drive.getBackLeftMotorPower());
        telemetry.addData("rmb power", drive.getBackRightMotorPower());
    }

    private void cheat(double x, double y) {
        if (x == 0 && y == 0) {
            return;
        }
        Point direction = new Point(x, y);
        double power = Math.sqrt(x*x + y*y);
        drive.linearMove(direction, power);
    }

    @Override
    public void stop() {
        drive.stop();
    }
}
