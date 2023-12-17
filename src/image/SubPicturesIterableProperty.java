package image;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

// SubPicturesIterableProperty - Class implementing iterator for iterate over all sub pictures in the main
// picture by order of the sub pictures
public class SubPicturesIterableProperty<T> implements Iterable<T> {
    private Image img;
    private final BiFunction<Integer, Integer, T> propertySupplier;
    private final int sizeOfSubPicture;

    private int endIndexHeightOfCurSubPic, endIndexWidthOfCurSubPic,
            startIndexHeightOfCurSubPicInclude, startIndexWidthOfCurSubPicInclude;


    public SubPicturesIterableProperty(Image img, int sizeOfSubPicture, BiFunction<Integer, Integer, T> propertySupplier) {
        this.img = img;
        this.sizeOfSubPicture = sizeOfSubPicture;
        this.propertySupplier = propertySupplier;

        endIndexHeightOfCurSubPic = sizeOfSubPicture - 1;
        endIndexWidthOfCurSubPic = sizeOfSubPicture - 1;
        startIndexHeightOfCurSubPicInclude = 0;
        startIndexWidthOfCurSubPicInclude = 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int x = 0, y = 0;

            @Override
            public boolean hasNext() {
                return y < img.getHeight();
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                // If we can proceed in the cur row
                // of the cur sub picture:
                if (x <= endIndexWidthOfCurSubPic) {
                    return propertySupplier.apply(x++, y);
//                    return img.getPixel(y,x++);
                }

                // If we need to jump to the next row of the
                // cur sub picture or to jump to next sub picture:
                else {
                    // Option one:
                    // We can't proceed more to the next line of the cur sub
                    // picture because we are in the last row of the cur sub pic
                    if (y == endIndexHeightOfCurSubPic) {
                        if (x == img.getHeight()){
                            startIndexWidthOfCurSubPicInclude = 0;
                            x = startIndexWidthOfCurSubPicInclude;
                            startIndexHeightOfCurSubPicInclude += sizeOfSubPicture;
                        }
                        else {
                            startIndexWidthOfCurSubPicInclude += sizeOfSubPicture;
                            x = startIndexWidthOfCurSubPicInclude;
                        }
                        y = startIndexHeightOfCurSubPicInclude;
                    }
                    // Option two:
                    // We can continue to the next
                    // line of the cur sub picture
                    else {
                        ++y;
                        x = startIndexWidthOfCurSubPicInclude;
                    }
                    var next = propertySupplier.apply(x++, y);
                    return next;
                }

            }
        };
    }
}
