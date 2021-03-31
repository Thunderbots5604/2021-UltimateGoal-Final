
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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

    private ElapsedTime runtime = new ElapsedTime();

    public Others(Telemetry telemetry) {
        this.telemetry = telemetry;
    }


    @Override
    public void runOpMode() {}
    public void initMotors(HardwareMap hardwareMap) {

        //Still need to reverse one of them
        LiftL = hardwareMap.get(DcMotor.class, "LiftMechL");
        LiftR = hardwareMap.get(DcMotor.class, "LiftMechR");

        WobbleLocker = hardwareMap.get(Servo.class, "WobbleGoalLocker" );
        RingArm = hardwareMap.get(Servo.class, "RingGrabber");
        WobbleArm = hardwareMap.get(Servo.class, "WobbleGoalArm");
    }
    public void lift() {

    }
    public void dropArm() {

    }
    public void resetArm() {

    }
    public void fireRing() {

    }
    public void getRing(){

    }
    public void ringLG() {

    }
}
