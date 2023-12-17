package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.*;

public class Shell {
    // This is my data structure of saving char for future purposes.
    // I choose this data structure mostly because of search and add
    // run time in ~ O(1)
    private HashSet<Character> MySetOfCharsTORenderWith;
    // This is a very simple data structure for mostly storing all possible chars by order of ascii.
    // Mostly needed for storing all possible chars and saving the right order between them.
    private ArrayList<Character> AllPossibleChars;
    private final String ILLEGAL_COMMAND = "illegal command";
    private final String EXIT = "exit";
    private final String CHARS_COMMAND = "chars";
    private final String ADD_COMMAND = "add";
    private final String REMOVE_COMMAND = "remove";
    private final String ADD_SPACE = "add space";
    private final String ADD_ALL = "add all";
    private final String REMOVE_ALL = "remove all";
    private final String REMOVE_SPACE = "remove space";
    private final String CONSOLE_COMMAND = "console";
    private final String RENDER_COMMAND = "render";

    private final String RES_UP = "res up";
    private final String RES_DOWN = "res down";
    private final String INCORRECT_INPUT_MASSAGE = "Did not executed due to incorrect command";
    private final String RES_ERROR_MASSAGE = "Did not change due to exceeding boundaries";
    private final String ADD_ERROR_MASSAGE = "Did not add due to incorrect format";
    private final String REMOVE_ERROR_MASSAGE = "Did not remove due to incorrect format";

    private final String COURIER_NEW_FONT = "Courier New";
    private final int START_ASCII_POSSIBLE_VAL = 32;
    private final int END_ASCII_POSSIBLE_VAL = 126;
    private final int GROWTH_RES = 2;

    private boolean renderToConsole;
    private int minCharsInRow,maxCharsInRow;
    private int charsInRow;
    private Image image;
    private AsciiOutput htmlAsciiOutput;

    static private final int MIN_PIXELS_PER_CHAR = 2;
    static private final int INITIAL_CHARS_IN_ROW = 64;

    public Shell(Image img){
        image = img;

        MySetOfCharsTORenderWith = new HashSet<Character>();
        // Adding default values to MySetOfChars ( 0 - 9 )
        int REDIX = 10;// Redix 10 is for decimal number
        for (int i = 0; i < 10; i++) { MySetOfCharsTORenderWith.add(Character.forDigit(i,REDIX)); }
        // Initializing AllPossibleChars to be all the char value in ascii between
        // START_ASCII_POSSIBLE_VAL and END_ASCII_POSSIBLE_VAL mean 32 to 125 inclusive
        AllPossibleChars = new ArrayList<Character>();
        for (int i = START_ASCII_POSSIBLE_VAL; i <= END_ASCII_POSSIBLE_VAL; ++i){ AllPossibleChars.add((char)i); }

        htmlAsciiOutput = new HtmlAsciiOutput("out.html",COURIER_NEW_FONT);
        renderToConsole = false;
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);

    }
    public void run(){
        Scanner scanner = new Scanner(System.in);
        System.out.print(">>>");
        String curCommand = scanner.nextLine();

        while (!Objects.equals(curCommand, EXIT)){
            // For "chars" command
            if (Objects.equals(curCommand, CHARS_COMMAND)){
                handleCharsCommand();
            }
            // For "add" / "remove" command
            else if (curCommand.startsWith(ADD_COMMAND) || curCommand.startsWith(REMOVE_COMMAND)) {
                handleRemoveOrAddCommand(curCommand);
            }
            // For "res" command
            else if (Objects.equals(curCommand, RES_UP) ||  Objects.equals(curCommand, RES_DOWN)){
                handleResCommand(curCommand);
            }
            // For "render" command - Initializing the process of
            // converting the image to its Ascii representation
            else if (Objects.equals(curCommand, RENDER_COMMAND)){
                handleRenderCommand(curCommand);
            }
            // For "console" command - Setting renderToConsole to true boolean value so that
            // next time the user will call the render command, the image will be printed to console
            else if (Objects.equals(curCommand, CONSOLE_COMMAND)){
                handleConsoleCommand();
            }
            // Handling ILLEGAL_COMMAND
            else {
                // Handling illegal command
                System.out.println(INCORRECT_INPUT_MASSAGE);
            }
            // Request for the next command
            System.out.print(">>>");
            curCommand = scanner.nextLine();
        }

    }


    // Checking if an add command is legal command:
    // If so - return "all" /
    // "space" /
    // string representing the range of chars to add /
    // string representing a single char
    // Else - return ILLEGAL_COMMAND
    private String CheckIfLegalAddRemoveCommand(String someAddCommand){
        String command;
        // Mean ADD_COMMAND
        if (someAddCommand.startsWith(ADD_COMMAND + ' ')) {
            command = ADD_COMMAND;}
        // Mean REMOVE_COMMAND
        else if (someAddCommand.startsWith(REMOVE_COMMAND + ' ')) {
            command = REMOVE_COMMAND; }
        // Mean ILLEGAL_COMMAND
        else {
            return ILLEGAL_COMMAND;
        }

        String valuesToAdd = someAddCommand.substring(command.length() + 1);
        if (someAddCommand.equals(ADD_SPACE)){
            return ADD_SPACE; }

        else if (someAddCommand.equals(ADD_ALL)){
            return ADD_ALL; }

        else if (someAddCommand.equals(REMOVE_SPACE)){
            return REMOVE_SPACE; }

        else if (someAddCommand.equals(REMOVE_ALL)){
            return REMOVE_ALL; }

        else if ( someAddCommand.length() - command.length() == 4 &&
                someAddCommand.charAt(command.length() + 2) == '-' &&
                AllPossibleChars.contains(someAddCommand.charAt(command.length() + 1)) &&
                AllPossibleChars.contains(someAddCommand.charAt(command.length() + 3)) ){
            return someAddCommand.substring(command.length() + 1); }

        else if ((command.startsWith(ADD_COMMAND) && valuesToAdd.length() == 1 &&
                AllPossibleChars.contains(valuesToAdd.charAt(0)))
                ||
                (command.startsWith(REMOVE_COMMAND) && valuesToAdd.length() == 1 &&
                        AllPossibleChars.contains(valuesToAdd.charAt(0))) ){
            return someAddCommand.substring(command.length() + 1);
        }
        return ILLEGAL_COMMAND;
    }

    private void handleRemoveOrAddCommand(String curCommand){
        String command;
        if (curCommand.startsWith(ADD_COMMAND)){
            command = ADD_COMMAND; }
        else{ command = REMOVE_COMMAND; }

        String valuesToAddRemove = CheckIfLegalAddRemoveCommand(curCommand);

        // In case we need to handle command of type
        // "remove" / "add" of some range of possible chars:
        if (!Objects.equals(valuesToAddRemove, ILLEGAL_COMMAND) && valuesToAddRemove.length() == 3){
            int startIndOfRange, endIndOfRange;
            if ((int) valuesToAddRemove.charAt(0) < (int) valuesToAddRemove.charAt(2)){
                startIndOfRange = (int) valuesToAddRemove.charAt(0) - START_ASCII_POSSIBLE_VAL;
                endIndOfRange = (int) valuesToAddRemove.charAt(2) - START_ASCII_POSSIBLE_VAL;
            }
            else {
                startIndOfRange = (int) valuesToAddRemove.charAt(2) - START_ASCII_POSSIBLE_VAL;
                endIndOfRange = (int) valuesToAddRemove.charAt(0) - START_ASCII_POSSIBLE_VAL + 1;
            }

            List<Character> arrayOfCharsToAdd =
                     AllPossibleChars.subList(startIndOfRange , endIndOfRange);

            Iterator<Character> it = arrayOfCharsToAdd.iterator();
            while (it.hasNext()) {
                if (command.equals(ADD_COMMAND)){
                    MySetOfCharsTORenderWith.add(it.next()); }
                else{
                    MySetOfCharsTORenderWith.remove(it.next()); }
            }
        }
        // In case we need to handle command of type
        // "remove" / "add" of space:
        else if (Objects.equals(valuesToAddRemove, ADD_SPACE) || Objects.equals(valuesToAddRemove, REMOVE_SPACE)) {
            if (command.equals(ADD_COMMAND)){
                MySetOfCharsTORenderWith.add(' '); }
            else{
                MySetOfCharsTORenderWith.remove(' '); }
        }
        // In case we need to handle command of type
        // "remove" / "add" of all possible chars:
        else if (Objects.equals(valuesToAddRemove, ADD_ALL) || Objects.equals(valuesToAddRemove, REMOVE_ALL)) {
            Iterator<Character> it = AllPossibleChars.iterator();
            while (it.hasNext()) {
                if (command.equals(ADD_COMMAND)){
                    MySetOfCharsTORenderWith.add(it.next()); }
                else{
                    MySetOfCharsTORenderWith.remove(it.next()); }
            }
        }
        // In case of removing or adding one legal char
        else if (valuesToAddRemove.length() == 1){
            if (command.equals(ADD_COMMAND)){
                MySetOfCharsTORenderWith.add(valuesToAddRemove.charAt(0)); }
            else{
                MySetOfCharsTORenderWith.remove(valuesToAddRemove.charAt(0)); }
        }
        // Handling wrong add / remove command
        else {
            if (curCommand.startsWith(ADD_COMMAND + ' ')){
                System.out.println(ADD_ERROR_MASSAGE);
            }
            else if (curCommand.startsWith(REMOVE_COMMAND + ' ')){
                System.out.println(REMOVE_ERROR_MASSAGE);
            }
            else{
                System.out.println(INCORRECT_INPUT_MASSAGE);

            }
        }
    }

    private void handleResCommand(String curCommand){
        if (curCommand.startsWith(RES_UP) && GROWTH_RES * charsInRow <= maxCharsInRow){
            charsInRow *= GROWTH_RES;
            System.out.println("Width set to " + charsInRow);
        }
        else if (curCommand.startsWith(RES_DOWN) && charsInRow / GROWTH_RES >= minCharsInRow){
            charsInRow /= GROWTH_RES;
            System.out.println("Width set to " + charsInRow);
        }
        else {
            System.out.println(RES_ERROR_MASSAGE);
        }
    }

    private void handleRenderCommand(String curCommand) {
        BrightnessImgCharMatcher brightnessImgCharMatcher = new BrightnessImgCharMatcher(image,COURIER_NEW_FONT);
        Character[] arrayOfChars = new Character[MySetOfCharsTORenderWith.size()];
        Iterator<Character> it = MySetOfCharsTORenderWith.iterator();
        int index = 0;
        while (it.hasNext()) {
            arrayOfChars[index] = it.next();
            ++index;
        }
        char[][] asciiImage = brightnessImgCharMatcher.chooseChars(charsInRow, arrayOfChars);
        htmlAsciiOutput.output(asciiImage);
    }

    private void handleCharsCommand() {
        Iterator<Character> it = MySetOfCharsTORenderWith.iterator();
        while (it.hasNext()) {
            System.out.print(it.next());
            System.out.print(" ");
        }
        System.out.println();
    }

    private void handleConsoleCommand() {
        htmlAsciiOutput = new ConsoleAsciiOutput();
    }
}
