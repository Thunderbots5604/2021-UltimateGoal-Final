/*
Copyright 2019

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this opmode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */
@TeleOp(name = "Old Tele Op", group = "")
public class OldTeleDrive extends LinearOpMode {
    private Servo armServo;
    private Servo clawServo;
    private Blinker expansion_Hub_10;
    private Blinker expansion_Hub_173;
    private Servo ringServo;
    private DcMotor flyWheel;
    private Gyroscope imu_1;
    private Gyroscope imu;
    private DcMotor flMotor;
    private DcMotor frMotor;
    private DcMotor blMotor;
    private DcMotor brMotor;
    private DigitalChannel webcam_1;

    private double powerFRBL = 0;
    private double powerFLBR = 0;
    private double multiplier = 1;


    @Override
    public void runOpMode() {
        armServo = hardwareMap.get(Servo.class, "ArmServo");
        clawServo = hardwareMap.get(Servo.class, "ClawServo");
        expansion_Hub_10 = hardwareMap.get(Blinker.class, "Expansion Hub 10");
        expansion_Hub_173 = hardwareMap.get(Blinker.class, "Expansion Hub 173");
        ringServo = hardwareMap.get(Servo.class, "RingServo");
        flyWheel = hardwareMap.get(DcMotor.class, "flyWheel");
        imu_1 = hardwareMap.get(Gyroscope.class, "imu 1");
        imu = hardwareMap.get(Gyroscope.class, "imu");
        blMotor = hardwareMap.get(DcMotor.class, "lmb");
        flMotor = hardwareMap.get(DcMotor.class, "lmf");
        brMotor= hardwareMap.get(DcMotor.class, "rmb");
        frMotor = hardwareMap.get(DcMotor.class, "rmf");
        webcam_1 = hardwareMap.get(DigitalChannel.class, "webcam 1");

        flMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        blMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        gamepad1.setJoystickDeadzone(0.1f);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.update();

            if (gamepad1.left_stick_y != 0 || gamepad1.left_stick_x != 0) {
                //FR BL pair
                powerFRBL = (gamepad1.left_stick_x + gamepad1.left_stick_y) / Math.sqrt(2);
                blMotor.setPower(multiplier * powerFRBL);
                frMotor.setPower(multiplier * powerFRBL);
                //FL BR pair
                powerFLBR = (gamepad1.left_stick_y - gamepad1.left_stick_x) / Math.sqrt(2);
                flMotor.setPower(multiplier * powerFLBR);
                brMotor.setPower(multiplier * powerFLBR);

            }
            else if (gamepad1.left_stick_y == 0 && gamepad1.right_stick_x != 0) {
                flMotor.setPower(-Math.abs(multiplier) * gamepad1.right_stick_x * .7);
                blMotor.setPower(-Math.abs(multiplier) * gamepad1.right_stick_x * .7);
                frMotor.setPower(Math.abs(multiplier) * gamepad1.right_stick_x * .7);
                brMotor.setPower(Math.abs(multiplier) * gamepad1.right_stick_x * .7);
                telemetry.addData("Right stick", gamepad1.right_stick_x);
            }


            //Don't move if no joysticks moved
            else {
                flMotor.setPower(0);
                blMotor.setPower(0);
                frMotor.setPower(0);
                brMotor.setPower(0);
            }
        }
    }
}
