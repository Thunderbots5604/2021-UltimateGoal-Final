package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import java.util.Objects;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

@Autonomous(name = "Cam", group = "TensorFlow")
public class Cam extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    //Put in the Vuforia Key
    private static String VUFORIA_KEY = "Aev2uJj/////AAABmXZLlevRVUibu3ft/8eoZ+p3zmNO/qYTRunRCIvDriYoZlMUcvJWFcEhvD1bCA6j/KWPlsVQyzCyh983kmfZN03G5bBJXhDh4fSgT4yyHL4PScYi5aG1UaxLa38X2vqzrbx9jpUqE3ESk6wYg8enXTPzp8R6+0SnrFoRLa7yobzCbBIfzAIpsGO33F9PVbXV+zsf0jqg0KA9OG24I6WkLZll0YPy1fDkR1okXL4pv2pm7eiKaZa2EXIYE/lGfkOAO42vxFMO8rAqA46/YeX/QPPTrCow0dE81FGSS6Wp9v3z45lqQ/kg+0TnSDkOJFrGKUYD1v6zTkfJLhF6DDAuW1TwPcdof0349IOncpuCpcz9";

    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;

    private ElapsedTime runTime = new ElapsedTime();

    private double[] coordinates = null;

    private List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();

    @Override
    public void runOpMode() {
        int zone = 1;
        waitForStart();
        initVuforia();
        initTfod();
        sleep(3000);
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        //Load Trackables
        //VuforiaTrackables objects = this.vuforia.loadTrackablesFromAsset("Ultimate Goal");
        //VuforiaTrackable target0 = objects.get(0);
        //target0.setName("get = 0");

        //VuforiaTrackable target1 = objects.get(0);
        //target1.setName("get = 1");

        /** For convenience, gather together all the trackable objects in one easily-iterable collection */
        //allTrackables.addAll(objects);
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters();
        tfodParameters.minimumConfidence = 0.85;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
    public int tfod() {

        int ringCount = 0;
        //Initialize camera
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
        }
        runTime.reset();
        int i = 0;
        while(opModeIsActive() && runTime.milliseconds() < 3000 && ringCount < 4) {
            List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
            i = 0;
            ringCount = 0;
            for (Recognition recognition : updatedRecognitions) {
                //telemetry.addData("label: ", recognition.getLabel());
                //telemetry.update();
                sleep(2000);
                if (recognition.getLabel() == "ring") {
                    ringCount++;
                }
                i++;
            }
        }
        sleep(2000);
        telemetry.addData("Objects Detected: ", i);
        telemetry.update();
        return ringCount;
    }
    public double[] getCoords() {
        return coordinates;
    }
    public void updateCoords() {
        if (allTrackables.size() > 0) {

        }
    }
    public int getZone() {
        return tfod();
    }
}