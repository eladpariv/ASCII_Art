package ascii_art.img_to_char;

import image.FileImage;
import image.Image;

import javax.imageio.stream.FileImageInputStream;
import java.awt.*;
import java.util.*;

// In this BrightnessImgCharMatcher we are using O(1) in place complexity:
//      * dictCharAscii_ -> for saving the chars we are using in the render and their
//        brightness, and there is permanent number from these chars so in max O(1)
//      * brightnessOfAlreadyCalculatedChars -> for saving all the calculations of the
//        chars in the first time, and there is permanent number from these chars so in max O(1)
//
// In this BrightnessImgCharMatcher we are using in time complexity as:
//      * chooseChars -> in this method, with the help of brightnessOfAlreadyCalculatedChars,
//        i assure the calculating of each possible char getting done only once and thus
//        in all renders command in max we will make in max O(1) time running as the number
//        of possible chars to render with is permanent and in this strategy i make the chooseChars method run in
//        and less running time in each render command.
//        Moreover, is this method i calculating the average brightness of each subPicture - O(n) and searching
//        in HashMap in O(1) - in summary, chooseChars running in O(n) as n is equal num_rows * num_col
//      * calculateAverageBrightness -> running on the rows and cols of the sub pictures and thus
//        running in O(n) as n is equal num_rows * num_col
//      * createHashMapOfCharsBrightness -> running in max on all possible and thus running in O(1)
public class BrightnessImgCharMatcher {
    private final int DEFAULT_PIXEL_RENDER_CHAR = 16;
    private final float MAX_BRIGHTNESS = 1.0F;
    private final float MIN_BRIGHTNESS = 0.0F;
    private final float MAX_RGB = 255;
    private final int DEFAULT_ABS = -1;


    private HashMap<Character, Float> dictCharAscii_;
    private String font_;
    private Image image_;

//     Here i make all the process of calculating the brightness of
//     the chars very efficient:
//     I make this HashMap as static variable of this class as responsible
//     for saving all the calculations of the chars in the first time so
//     that for different renders we make all the calculations of the char's
//     brightness only once and save time running: O(n) for all renders
//     as n equals to the number of possible chars
    static HashMap<Character, Float> brightnessOfAlreadyCalculatedChars;


    public BrightnessImgCharMatcher(Image img, String font) {
        dictCharAscii_ = new HashMap<Character, Float>();
        brightnessOfAlreadyCalculatedChars = new HashMap<Character, Float>();
        font_ = font;
        image_ = img;
    }

    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        dictCharAscii_ = createHashMapOfCharsBrightness(charSet);
        int sizeOfSubPicture = image_.getWidth() / numCharsInRow;
        int numRowsOfSplitImage = image_.getHeight() / sizeOfSubPicture,
                numColsOfSplitImage = image_.getWidth() / sizeOfSubPicture;

        char[][] arrayCharForReturn = new char[numRowsOfSplitImage][numColsOfSplitImage];

        Vector<FileImage.SubPicture> splitImage = image_.makeSubPictures(sizeOfSubPicture);
        Iterator<FileImage.SubPicture> it = splitImage.iterator();

        for (int i = 0; i < numRowsOfSplitImage; ++i) {
            for (int j = 0; j < numColsOfSplitImage; ++j) {
                arrayCharForReturn[i][j] = getCharClosestToAverageBrightness(calculateAverageBrightness(it.next()));
            }
        }
        return arrayCharForReturn;
    }

    public float calculateAverageBrightness(FileImage.SubPicture subPicture) {
        float sumGreyPixels = 0;
        for (int i = 0; i < subPicture.getSize(); i++) {
            for (int j = 0; j < subPicture.getSize(); j++) {
                sumGreyPixels += (float) (subPicture.get(i, j).getRed() * 0.2126 +
                        subPicture.get(i, j).getGreen() * 0.7152 +
                        subPicture.get(i, j).getBlue() * 0.0722);
            }
        }
        return sumGreyPixels / (MAX_RGB * subPicture.getSize() * subPicture.getSize()); // calculate brightness
    }

    private char getCharClosestToAverageBrightness(float averageBrightness) {
        float curAbs = DEFAULT_ABS;
        char curClosestCharByBrightness = '-';
        for (Map.Entry<Character, Float> set : dictCharAscii_.entrySet()) {
            if (curAbs == DEFAULT_ABS) {
                curAbs = Math.abs(averageBrightness - set.getValue());
                curClosestCharByBrightness = set.getKey();
            } else if (Math.abs(averageBrightness - set.getValue()) < curAbs) {
                curAbs = Math.abs(averageBrightness - set.getValue());
                curClosestCharByBrightness = set.getKey();
            }
        }
        return curClosestCharByBrightness;
    }

    private HashMap<Character, Float> createHashMapOfCharsBrightness(Character[] charSet) {
        float minBrightness = MAX_BRIGHTNESS;
        float maxBrightness = MIN_BRIGHTNESS;

        HashMap<Character, Float> dictCharAscii = new HashMap<Character, Float>();

        // This is the first pass of calculating the
        // brightness of each character in charSet
        for (int i = 0; i < charSet.length; ++i) {
            float brightness;
            // Here i check if the brightness of the specific char
            // was calculated before and ni need to evaluate again
            if (brightnessOfAlreadyCalculatedChars.containsKey(charSet[i])) {
                brightness = brightnessOfAlreadyCalculatedChars.get(charSet[i]);
            }
            // If the brightness wasn't calculate before
            // we do the evaluating and storing the result in
            // brightnessOfAlreadyCalculatedChars for future purposes
            else {
                brightness = CalculateBrightness(charSet[i]);
                brightnessOfAlreadyCalculatedChars.put(charSet[i], brightness);
            }
            dictCharAscii.put(charSet[i], brightness);
            if (brightness > maxBrightness) {
                maxBrightness = brightness;
            }
            if (brightness < minBrightness) {
                minBrightness = brightness;
            }
        }
        // This is the second pass of normalizing the
        // brightness of each character in charSet by
        // linear stretching of edge values
        for (int i = 0; i < charSet.length; ++i) {
            float newCharBrightness = (dictCharAscii.get(charSet[i]) - minBrightness) /
                    (maxBrightness - minBrightness);
            dictCharAscii.replace(charSet[i], newCharBrightness);
        }
        return dictCharAscii;
    }

    private float CalculateBrightness(Character character) {
        boolean[][] booleanArrayOfChar = CharRenderer.getImg(character, DEFAULT_PIXEL_RENDER_CHAR, font_);
        float numberOfWhitePixels = countNumberWhitePixels(booleanArrayOfChar);
        // Now we need to normalize in the number of pixels ,
        // mean numberOfWhitePixels / DEFAULT_PIXEL_RENDER_CHAR (16)
        return numberOfWhitePixels / (DEFAULT_PIXEL_RENDER_CHAR * DEFAULT_PIXEL_RENDER_CHAR);
    }

    private float countNumberWhitePixels(boolean[][] booleanArrayOfChar) {
        int counter = 0;
        for (int i = 0; i < booleanArrayOfChar.length; i++) {
            for (int j = 0; j < booleanArrayOfChar[0].length; j++) {
                if (booleanArrayOfChar[i][j]) {
                    counter += 1;
                }
            }
        }
        return (float) counter;
    }

}




