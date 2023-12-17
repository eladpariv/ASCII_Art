package image;


import java.awt.*;
import java.io.IOException;
import java.util.Vector;
import java.util.function.BiFunction;

/**
 * Facade for the image module and an interface representing an image.
 * @author Dan Nirel
 */
public interface Image {
    Color getPixel(int x, int y);
    int getWidth();
    int getHeight();

    /**
     * Open an image from file. Each dimensions of the returned image is guaranteed
     * to be a power of 2, but the dimensions may be different.
     * @param filename a path to an image file on disk
     * @return an object implementing Image if the operation was successful,
     * null otherwise
     */
    static Image fromFile(String filename) {
        try {
            return new FileImage(filename);
        } catch(IOException ioe) {
            return null;
        }
    }

    /**
     * Allows iterating the pixels' colors by order (first row, second row and so on).
     * @return an Iterable<Color> that can be traversed with a foreach loop
     */
    default Iterable<Color> pixels() {
        return new ImageIterableProperty<>(
                this, this::getPixel);
    }

    default Vector<FileImage.SubPicture> makeSubPictures(int sizeOfSubPicture) {
        Vector<FileImage.SubPicture> vectorOfSubPictures = new Vector<FileImage.SubPicture>();
        for (int i = 0; i <= getHeight() - 1; i += sizeOfSubPicture) {
            for (int j = 0; j <= getWidth() - 1; j += sizeOfSubPicture) {
                vectorOfSubPictures.add(createAndFillSubPicture(sizeOfSubPicture, i, j));
            }
        }
        return vectorOfSubPictures;
    }

    default FileImage.SubPicture createAndFillSubPicture(int sizeOfSubPicture, int startIndRowOfSubPicInOrgPic, int startIndColOfSubPicInOrgPic) {
        FileImage.SubPicture subPicture = new FileImage.SubPicture(sizeOfSubPicture, sizeOfSubPicture);
        for (int i = 0; i < sizeOfSubPicture; ++i) {
            for (int j = 0; j < sizeOfSubPicture; ++j) {
                subPicture.set(i, j, getPixel(j + startIndColOfSubPicInOrgPic, i + startIndRowOfSubPicInOrgPic));
            }
        }
        return subPicture;
    }

    default SubPicturesIterableProperty<Color> getIteratorOnSubPictures(Image img, int sizeOfSubPicture, BiFunction<Integer, Integer, Color> propertySupplier){
        return new SubPicturesIterableProperty<Color>( img, sizeOfSubPicture, propertySupplier);
    }
}
