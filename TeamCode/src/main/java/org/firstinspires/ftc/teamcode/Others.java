
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Other Motors", group="")
@Disabled

public class Others extends LinearOpMode {

    //Declare Motors
    private DcMotor LiftL = null;
    private DcMotor LiftR = null;

    //Declare Servos
    private Servo WobbleLocker;
    private Servo WobbleArm;
    private Servo RingArm;

    //Moving stuff between classes
    private Telemetry telemetry;
    private HardwareMap hardwareMap;

    private ElapsedTime runtime = new ElapsedTime();

    public Others(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
    }


    @Override
    public void runOpMode() {}
    public void initMotors() {

        //Still need to reverse one of them
        LiftL = hardwareMap.get(DcMotor.class, "LiftMechL");
        LiftR = hardwareMap.get(DcMotor.class, "LiftMechR");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleGoalLocker" );
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleGoalArm");
    }
    public void lift() {

    }
    public void dropWobbleGoal() {

    }
    public void pickUpWobble() {

    }
    public void fireRing() {

    }
}
