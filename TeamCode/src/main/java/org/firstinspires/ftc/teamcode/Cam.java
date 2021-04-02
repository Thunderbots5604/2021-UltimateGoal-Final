package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import org.firstinspires.ftc.robotcore.external.Telemetry;
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
@Disabled
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

    private Telemetry telemetry;

    public Cam(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    @Override
    public void runOpMode() {
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
    public String tfod() {

        String ringCount = "";
        List<Recognition> updatedRecognitions;
        Boolean detected = false;

        runTime.reset();
        while(runTime.milliseconds() < 3000) {
            telemetry.addData("Scanning", " for objects");
            telemetry.update();
            updatedRecognitions = tfod.getUpdatedRecognitions();
            if (updatedRecognitions != null) {
                for (Recognition recognition : updatedRecognitions) {
                    ringCount = recognition.getLabel();
                }
            }
        }
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
        String ringCount = tfod();
        switch (ringCount.toLowerCase()) {
            case "single":
                return 1;
            case "quad":
                return 2;
            default:
                return 0;
        }
    }
    public void startCam() {
        initVuforia();
        initTfod();
        if (tfod != null) {
            tfod.activate();
        }
    }
    public void endCam() {
        tfod.shutdown();
    }
}