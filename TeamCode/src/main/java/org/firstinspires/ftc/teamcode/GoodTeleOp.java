
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "TeleOp", group = "Tele")
public class GoodTeleOp extends OpMode {

    DcMotor flMotor = null;
    DcMotor frMotor = null;
    DcMotor blMotor = null;
    DcMotor brMotor = null;
    Servo RingServo = null;
    Servo ClawServo = null;
    Servo ArmServo = null;

    double multiplier = 0.5;

    @Override
    public void init() {


        // Wait for the game to start (driver presses PLAY)
        flMotor = hardwareMap.get(DcMotor.class, "lmf" );
        frMotor = hardwareMap.get(DcMotor.class, "rmf" );
        blMotor = hardwareMap.get(DcMotor.class, "lmb" );
        brMotor = hardwareMap.get(DcMotor.class, "rmb" );

        flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        blMotor.setDirection(DcMotorSimple.Direction.REVERSE);


        gamepad1.setJoystickDeadzone(0.1f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

    }

    @Override
    public void loop() {
        telemetry.addData("Status", "Running");
        telemetry.update();

        if (Math.abs(gamepad1.left_stick_y) < .1) {
            flMotor.setPower(-gamepad1.left_stick_x * multiplier);
            frMotor.setPower(gamepad1.left_stick_x * multiplier);
            blMotor.setPower(-gamepad1.left_stick_x * multiplier);
            brMotor.setPower(gamepad1.left_stick_x * multiplier);
        }
        else if (Math.abs(gamepad1.left_stick_x) < .1) {
            flMotor.setPower(gamepad1.left_stick_y * multiplier);
            frMotor.setPower(gamepad1.left_stick_y * multiplier);
            blMotor.setPower(gamepad1.left_stick_y * multiplier);
            brMotor.setPower(gamepad1.left_stick_y * multiplier);
        }


        if (Math.abs(gamepad1.right_stick_y) < .1) {
            flMotor.setPower(-gamepad1.right_stick_x * multiplier);
            frMotor.setPower(gamepad1.right_stick_x * multiplier);
            blMotor.setPower(gamepad1.right_stick_x * multiplier);
            brMotor.setPower(-gamepad1.right_stick_x * multiplier);
        }

    }
    public void stop() {
        flMotor.setPower(0);
        frMotor.setPower(0);
        blMotor.setPower(0);
        brMotor.setPower(0);
    }
}
