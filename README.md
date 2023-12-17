# ASCII Art




<table>
  <tr>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/981788dc-edf4-47df-b096-33104beadb15" alt="Image 5"></td>
  </tr>
</table>

<table>
  <tr>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/78ba9343-ef3d-4f40-bcd9-e83b1fc88bbc" alt="Image 5"></td>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/54d81ff7-89f8-401c-a5ae-e2b3186c8664" alt="Image 5"></td>
  </tr>
</table>

<table>
  <tr>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/fec07ebe-ce0b-496d-8e41-3ffbeaaed16b" alt="Image 5"></td>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/1eedeb5b-b8d6-4dd6-92cc-3ecf9d5a53e4" alt="Image 5"></td>
  </tr>
</table>

<table>
  <tr>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/7c13816c-2f5b-4e8c-8b75-199b5914453e" alt="Image 5"></td>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/20758c77-11ea-475c-af23-8ef1f4fd5167" alt="Image 5"></td>
  </tr>
</table>

<table>
  <tr>
    <td align="center"><img src="https://github.com/eladpariv/ASCII_Art/assets/96910425/e2da4e51-2fb1-4bec-a5c6-43359b908fab" alt="Image 5"></td>
  </tr>
</table>



Explanation of all the need about this project:

class BrightnessImgCharMatcher:

Explanation - In this department, I take care of the whole issue of calculating the brightness of
each character and finding an average brightness value of a sub-image.

    (complexity of time and space)
    In class BrightnessImgCharMatcher we are using O(1) in place complexity:
          * dictCharAscii_ -> for saving the chars we are using in the render and their
            brightness, and there is permanent number from these chars so in max O(1)
          * brightnessOfAlreadyCalculatedChars -> for saving all the calculations of the
            chars in the first time, and there is permanent number from these chars so in max O(1)
    In class BrightnessImgCharMatcher we are using in time complexity as:
          * chooseChars -> in this method, with the help of brightnessOfAlreadyCalculatedChars,
            i assure the calculating of each possible char getting done only once and thus
            in all renders command in max we will make in max O(1) time running as the number
            of possible chars to render with is permanent and in this strategy i make the chooseChars method run in
            and less running time in each render command.
            Moreover, is this method i calculating the average brightness of each subPicture - O(n) and searching
            in HashMap in O(1) - in summary, chooseChars running in O(n) as n is equal num_rows * num_col
          * calculateAverageBrightness -> running on the rows and cols of the sub pictures and thus
            running in O(n) as n is equal num_rows * num_col
          * createHashMapOfCharsBrightness -> running in max on all possible and thus running in O(1)

    (explanation of the special data structure and variable in this class)
    private HashMap<Character, Float> dictCharAscii_ -> I choosed this data structure because i needed to search very often
        a brightness of char and this enable me to do it in O(1) run time complexity
    static HashMap<Character, Float> brightnessOfAlreadyCalculatedChars -> Here i make all the process of calculating the
        brightness of the chars very efficient:
        I make this HashMap as static variable of this class as responsible
        for saving all the calculations of the chars in the first time so
        that for different renders we make all the calculations of the char's
        brightness only once and save time running: O(n) for all renders
        as n equals to the number of possible chars


class FileImage:

Explanation - In this department, I take care of the initial processing of the image file and
the upholstery of the white cells on the sides.

    (complexity of time and space)
    In class FileImage there is use of O(n) in place complexity due to
          * private final Color[][] pixelArray -> 2D array for saving the color of
            each pixel in the new image by [row][col]
          * private Color[] colorPixelsOfOrigImageByOrder -> Array for saving the
            color of each pixel in the origin image by order
    In class FileImage there is use of O(n) in time complexity due to constructing
    pixelArray in O(2n) maximum and to constructing colorPixelsOfOrigImageByOrder in O(n) max


    class SubPicture:
    Nested class of FileImage class for handling the sub picture in the separated image for sub images


Interface Image:

(explanation of the special data structure and variable in this class)
In interface Image i implemented two default methods responsible for making the new picture that split to sub`
pictures: makeSubPictures -> method using createAndFillSubPicture default method for running on the origin image by
the size of each sub picture and in this strategy to create object subPicture for all indexes from the origin image
including in the same sub picture. in the end of calling makeSubPictures we get 2D array of subPicture items.


class Shell:

Explanation - In this department, I handle all the processing of commands from the user with
regard to the processing of the current image and the characters

    (explanation of the special data structure and variable in this class)
    private HashSet<Character> MySetOfCharsTORenderWith - This is my data structure of saving char for future purposes.
        I choose this data structure mostly because of search and add run time in ~ O(1).
    private ArrayList<Character> AllPossibleChars - This is a very simple data structure for mostly storing all possible
        chars by order of ascii.
        Mostly needed for storing all possible chars and saving the right order between them.


class SubPicturesIterableProperty:

Explanation - I add SubPicturesIterableProperty class that implementing iterator for iterate over all
sub pictures in the main picture by order of the sub pictures
I chose to implement the iterator as follows:
To go through each sub-image in the internal order and in the order of the sub-images, i using two central loops,
I move through rows and columns of the overall image that contains all the sub-images, Skipping the size of each
sub-image.
In each iteration I begin to go in order on the organs of the current sub-image and when I finish going through it,
i proceed to the next iteration in the outer loop to go over the next sub-image
