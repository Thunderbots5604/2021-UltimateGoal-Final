/*A set of various utilities used for things in our math package
 * everything here should be static*/

package org.firstinspires.ftc.teamcode.math;

public class MathUtilities {
    //variables
    //very small number used for comparisons
    public static final double SMALL_NUMBER = 1e-6;

    //methods
    //figure out if the two values are close to equal
    //use if slightly imprecise values might be created
    public static boolean closeEnough(double firstNumber, double secondNumber) {
        //check for actual equality
        if (firstNumber == secondNumber) {
            return true;
        }
        //check for being close
        else if (Math.abs(firstNumber - secondNumber) <= SMALL_NUMBER) {
            return true;
        }
        //otherwise return false
        else {
            return false;
        }
    }

    //check if a number is within a certain range
    public static boolean within(double range, double firstNumber, double secondNumber) {
        //check for actual equality
        if (firstNumber == secondNumber) {
            return true;
        }
        //check for being close
        else if (Math.abs(firstNumber - secondNumber) <= range) {
            return true;
        }
        //otherwise return false
        else {
            return false;
        }
    }

    //get the difference between two angles, making sure that the value is between -180 and 180
    public static double angleDifference(double angle1, double angle2) {
        //get the actual difference
        double angleDifference = angle1 - angle2;
        //check if angle magnitude is greater than 360
        if (Math.abs(angleDifference) >= 360) {
            angleDifference %= 360;
        }
        //check if the angleDifference's value is greater than 180
        if (angleDifference > 180) {
            //subtract 360
            angleDifference -= 360;
        }
        //check if the angleDifference's value is less than 180
        if (angleDifference < -180) {
            //add 360
            angleDifference += 360;
        }
        return angleDifference;
    }
}