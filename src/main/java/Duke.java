import java.util.Scanner;
import java.util.ArrayList;
import java.util.Locale;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Duke {
    private static final String spacer = "    ____________________________________________________________\n";
    private static ArrayList<Task> toDoList = new ArrayList<>();
    public static final DateTimeFormatter inputdtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm");
    public static final DateTimeFormatter outputdtFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mma", Locale.ENGLISH);
    private static void startupMessage() {
        String name = "CBBW";
        botPrint("Hello! I'm " + name 
                           + "\nWhat can I do for you?");
    }

    private static void goodbyeMessage() {
        botPrint("See you again soon!");
    }

    public static void botPrint(String s) {
        s = s.replace("\n", "\n    ");
        System.out.println(spacer + "    " + s + "\n" + spacer);
    }

    private static void printList() {
        System.out.println(spacer);
        for (int i = 1; i <= toDoList.size(); i++) {
            System.out.println("    " + i + "." + toDoList.get(i - 1) + "\n");
        }
        System.out.println(spacer);
    }

    private static void createTodo(String input) throws MissingTaskInformationException {
        String description = input.split(" ", 2)[1];
        if (description.equals("")) {
            throw new MissingTaskInformationException("\"description\" ");
        }
        Todo t = new Todo(false, description);
        toDoList.add(t);
        botPrint("Todo Task added!\n" + t.toString() + "\n" + "You now have " + toDoList.size() + " tasks in the list.");
    }

    private static void createEvent(String input) throws MissingTaskInformationException, MissingTaskParameterException, BadTaskOrderException {

        // Check missing parameters
        String missingParams = "";

        if (!input.contains("/from")) {
            missingParams = missingParams + "/from ";
        }
        if (!input.contains("/to")) {
            missingParams = missingParams + "/to ";
        }
        if (!missingParams.equals("")) {
            throw new MissingTaskParameterException(missingParams);
        }

        // Check order of parameters
        if (input.indexOf("/from") > input.indexOf("/to")) {
            throw new BadTaskOrderException("/from, /to");
        }


        String description = input.substring(5, input.indexOf("/from")).trim();

        String startString = input.substring(input.indexOf("/from") + 5, input.indexOf("/to")).trim();
        String endString = input.substring(input.indexOf("/to") + 3).trim();
        
        // Check if inputs are blank
        String missingInfo = "";

        if (description.equals("")) {
            missingInfo = missingInfo + "\"description\" ";
        }
        // if (from.equals("")) {
        //     missingInfo = missingInfo + "\"from\"  ";
        // }
        // if (to.equals("")) {
        //     missingInfo = missingInfo + "\"to\" ";
        // }
        
        if (!missingInfo.equals("")) {
            throw new MissingTaskInformationException(missingInfo);
        }

        try {
            LocalDateTime startDateTime = LocalDateTime.parse(startString, inputdtFormatter);
            LocalDateTime endDateTime = LocalDateTime.parse(endString, inputdtFormatter);
            
            Event e = new Event(false, description, startDateTime, endDateTime);
            toDoList.add(e);
            botPrint("Event Task added!\n" + e.toString() + "\n" + "You now have " + toDoList.size() + " tasks in the list.");

        } catch (DateTimeParseException e) {
            System.out.println("Error parsing datetime: " + e.getMessage());
            System.out.println("Use the format \"DD/MM/YYYY, HH:MM\" to enter date and time." );
        }
    }

    private static void createDeadline(String input) throws MissingTaskInformationException, MissingTaskParameterException {
        // Check missing parameters
        if (!input.contains("/by")) {
            throw new MissingTaskParameterException("/by");
        }

        String description = input.substring(8, input.indexOf("/by")).trim();
        String deadlineString = input.substring(input.indexOf("/by") + 3).trim();
        // Check if inputs are blank
        String missingInfo = "";

        if (description.equals("")) {
            missingInfo = missingInfo + "\"description\" ";
        }
        // if (by.equals("")) {
        //     missingInfo = missingInfo + "\"by\" ";
        // }
        if (!missingInfo.equals("")) {
            throw new MissingTaskInformationException(missingInfo);
        }

        try {
            LocalDateTime deadlineDateTime = LocalDateTime.parse(deadlineString, inputdtFormatter);
                
            Deadline d = new Deadline(false, description, deadlineDateTime);
            toDoList.add(d);
            botPrint("Deadline Task added!\n" + d.toString() + "\n" + "You now have " + toDoList.size() + " tasks in the list.");
            
        } catch (DateTimeParseException e) {
            System.out.println("Error parsing datetime: " + e.getMessage());
            System.out.println("Use the format \"DD/MM/YYYY, HH:MM\" to enter date and time." );
        }
    }

    private static void markTask(String input) throws IndexOutOfBoundsException {
        int index = Integer.parseInt(input.split(" ")[1]);
        if (index > 0 && index <= toDoList.size()) {
            Task t = toDoList.get(index - 1);
            t.doTask();
            botPrint("Good job on finishing your task!:\n  " + t);
        } else {
            throw new IndexOutOfBoundsException("Invalid Index " + index + " for current list\nList is of current length: " + toDoList.size());
        }
    }

    private static void unmarkTask(String input) throws IndexOutOfBoundsException {
        int index = Integer.parseInt(input.split(" ")[1]);
        if (index > 0 && index <= toDoList.size()) {
            Task t = toDoList.get(index - 1);
            t.undoTask();
            botPrint("I've marked this task as undone:\n  " + t);
        } else {
            throw new IndexOutOfBoundsException("Invalid Index " + index + " for current list\nList is of current length: " + toDoList.size());
        }
    }

    private static void deleteTask(String input) throws IndexOutOfBoundsException {
        int index = Integer.parseInt(input.split(" ")[1]);
        if (index > 0 && index <= toDoList.size()) {
            Task t = toDoList.get(index - 1);
            toDoList.remove(index - 1);
            botPrint("I've removed this task:\n  " + t);
        } else {
            throw new IndexOutOfBoundsException("Invalid Index " + index + " for current list\nList is of current length: " + toDoList.size());
        }
    }

    private static void checkAndCreateFile(File f) {
        // Reading and creating data save file
        try {
            // making data folder
            if (!f.getParentFile().exists()) {
                if (!f.getParentFile().mkdir()) {
                    throw new IOException("Unable to make directory");
                };
            }

            // making data file
            if (f.exists()) {
                toDoList = Storage.readTodoData(f);
            } else {
                f.createNewFile();

            }

        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        startupMessage();

        File f = new File("data/data.txt");

        checkAndCreateFile(f);

        // reading user inputs
        Scanner s = new Scanner(System.in);
        while (true) {

            String input = s.nextLine();
            if (input.equals("bye")) {
                s.close();
                goodbyeMessage();
                try {
                    Storage.saveTodoData(toDoList, f);
                } catch (IOException e) {
                    System.out.println("Error reading file: " + e.getMessage() + "\nAttemping to create new save file");
                    checkAndCreateFile(f);
                    try {
                        Storage.saveTodoData(toDoList, f);
                    } catch (IOException e2) {
                        System.out.println("Data not saved: " + e.getMessage());
                    }
                }
                break;
            }
            String action = input.split(" ")[0].toLowerCase();

            try {
                switch (action) {
                case "list":
                    printList();
                    break;
                case "todo":
                    createTodo(input);
                    break;
                case "event":
                    createEvent(input);
                    break;
                case "deadline":
                    createDeadline(input);
                    break;
                case "mark":
                    markTask(input);
                    break;
                case "unmark":
                    unmarkTask(input);
                    break;
                case "delete":
                    deleteTask(input);
                default:
                    throw new NoSuchCommandException(input);
                }
            } catch (BadTaskOrderException e) {
                botPrint(e.getMessage());
            } catch (MissingTaskParameterException e) {
                botPrint(e.getMessage());
            } catch (MissingTaskInformationException e) {
                botPrint(e.getMessage());
            } catch (NoSuchCommandException e) {
                botPrint(e.getMessage());
            } catch (IndexOutOfBoundsException e) {
                botPrint(e.getMessage());
            } catch (NumberFormatException e) {
                botPrint("Invalid selection for marking or deletion");
            }

        }
    }
}
