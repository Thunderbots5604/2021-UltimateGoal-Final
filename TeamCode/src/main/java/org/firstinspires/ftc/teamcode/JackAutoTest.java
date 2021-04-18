package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.hardware.MecanumDrive;
import org.firstinspires.ftc.teamcode.pathing.RobotPosition;
import org.firstinspires.ftc.teamcode.math.Point;

@Autonomous(name = "Jek Test")

public class JackAutoTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        MecanumDrive mecanumDrive = new MecanumDrive(hardwareMap);
        waitForStart();
        mecanumDrive.moveOn(RobotPosition.zero(), new RobotPosition(new Point(0, 0), 90), 0.25, telemetry);
        sleep(1000);
        /*mecanumDrive.linearMove(new Point(0, -1), 0.5);
        mecanumDrive.updateMotorPowers();
        sleep(1000);
        mecanumDrive.stop();*/
    }
}