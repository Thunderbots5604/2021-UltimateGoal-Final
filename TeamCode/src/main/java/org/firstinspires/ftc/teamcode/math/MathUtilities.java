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
}