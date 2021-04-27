package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ShootingSystem {
    //hardware map
    private HardwareMap map;
    //motors
    private DcMotor input;
    private DcMotor transfer;
    private DcMotor output;
    //motor powers
    private final double INPUT_POWER;
    private final double TRANSFER_POWER;
    private final double OUTPUT_POWER;
    //on/off values
    private boolean outputOn;
    private boolean transferOn;
    private boolean inputOn;

    //constructor given all values
    public ShootingSystem(HardwareMap map, String input, String transfer, String output,
                          double inputPower, double transferPower, double outputPower) {
        //pretty basic setting values
        this.map = map;
        this.input = map.get(DcMotor.class, input);
        this.transfer = map.get(DcMotor.class, transfer);
        this.output = map.get(DcMotor.class, output);
        INPUT_POWER = inputPower;
        TRANSFER_POWER = transferPower;
        OUTPUT_POWER = outputPower;
    }

    //constructor only given map and names
    public ShootingSystem(HardwareMap map, String input, String transfer, String output) {
        this(map, input, transfer, output, 1, -1, -1);
    }

    //constructor only given map
    public ShootingSystem(HardwareMap map) {
        this(map, "intake", "feed", "out");
    }

    //turn on input
    public void inputOn() {
        input.setPower(INPUT_POWER);
        inputOn = true;
    }

    //turn on transfer
    public void transferOn() {
        transfer.setPower(TRANSFER_POWER);
        transferOn = true;
    }

    //turn on output
    public void outputOn() {
        output.setPower(OUTPUT_POWER);
        outputOn = true;
    }
}
