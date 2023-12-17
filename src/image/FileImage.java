package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */

// In class FileImage there is use of O(n) in place complexity due to
//      * private final Color[][] pixelArray -> 2D array for saving the color of
//        each pixel in the new image by [row][col]
//      * private Color[] colorPixelsOfOrigImageByOrder -> Array for saving the
//        color of each pixel in the origin image by order
//
// In class FileImage there is use of O(n) in time complexity due to constructing
// pixelArray in O(2n) maximum and to constructing colorPixelsOfOrigImageByOrder in O(n) max
public class FileImage implements Image {
    // Default color as white for Line up the image
    private static final Color DEFAULT_COLOR = Color.WHITE;
    // 2D array for saving the color of each pixel in the new image by [row][col]
    private final Color[][] pixelArray;
    // Array for saving the color of each pixel in the origin image by order
    private Color[] colorPixelsOfOrigImageByOrder;

    private final int TWO_FOR_DIVISION = 2;


    // The main constructor of the FileImage class
    public FileImage(String filename) throws IOException {
        java.awt.image.BufferedImage im = ImageIO.read(new File(filename));
        int origWidth = im.getWidth(), origHeight = im.getHeight();
        int newWidth = getNewSizeDimension(origWidth), newHeight = getNewSizeDimension(origHeight);

        colorPixelsOfOrigImageByOrder = new Color[ origWidth * origHeight ];
        fillColorPixelsOfOrigImageByOrder(im);

        pixelArray = new Color[newHeight][newWidth];
        int nextPosColorFromOrigImage = 0;
        for (int i = 0; i < newHeight; ++i){
            for (int j = 0; j < newWidth; ++j){
                if ( j >= origWidth + ((newWidth - origWidth)/TWO_FOR_DIVISION) ||
                     j < ((newWidth - origWidth)/TWO_FOR_DIVISION )||
                     i >= origHeight + ((newHeight - origHeight)/2) ||
                     i < ((newHeight - origHeight)/TWO_FOR_DIVISION) ) {
                    pixelArray[i][j] = DEFAULT_COLOR;
                }
                else {
                        pixelArray[i][j] = colorPixelsOfOrigImageByOrder[nextPosColorFromOrigImage++]; }
            }
        }
    }
    @Override
    public int getWidth() {
        if (pixelArray.length > 0){
            return pixelArray[0].length;
        }
        return 0;
    }
    @Override
    public int getHeight() {
        return pixelArray.length;
    }
    @Override
    public Color getPixel(int x, int y) {
        return new Color(pixelArray[y][x].getRed(), pixelArray[y][x].getGreen(), pixelArray[y][x].getBlue()); }

    // Method for getting the new size of the origin image line up with white at sides
    private int getNewSizeDimension(int curDimension){
        int curNumber = 1;
        while (curNumber < curDimension){ curNumber *= 2; }
        return curNumber;
    }
    private void fillColorPixelsOfOrigImageByOrder(java.awt.image.BufferedImage image){
        int counter = 0;
        for ( int i = 0; i < image.getHeight(); i++ ) {
            for ( int j = 0; j < image.getWidth(); j++ ) {
                colorPixelsOfOrigImageByOrder[counter] = new Color(image.getRGB(j,i));
                ++counter;
            }
        }
    }



//     Nested class for handling the sub picture
//     in the separated image for sub images
    public static class SubPicture {
        private Color[][] mySubPixelArray;
        public SubPicture(int numRows, int numCols) {
            mySubPixelArray = new Color[numRows][numCols];
        }
        public void set(int indRow, int indCol, Color color) {
            mySubPixelArray[indRow][indCol] = color;
        }
        public Color get(int indRow, int indCol) {
            return mySubPixelArray[indRow][indCol];
        }
        public int getSize() {
            return mySubPixelArray.length;
        }
    }
    // End of nested class SubPicture

}










