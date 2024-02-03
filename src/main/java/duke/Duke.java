package duke;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeParseException;

import duke.commands.Command;
import duke.exceptions.NoSuchCommandException;
import duke.exceptions.TaskCreationException;
import duke.exceptions.TaskModificationException;
import duke.utils.Parser;
import duke.utils.Storage;
import duke.utils.TaskList;
import duke.utils.Ui;

/**
 * This class implements the functionality of the Duke bot.
 * @author delishad21
 * @version 0.1
 */
public class Duke {

    private Storage storage;
    private Ui ui;
    private TaskList tasks;


    public Duke() {


    }
    /**
     * Creates Duke object with filepath to data file.
     *
     * @param filePath Filepath to save file.
     */
    public Duke(String filePath) {
        this.ui = new Ui();

        try {
            this.storage = new Storage(filePath);
            this.tasks = storage.readSaveData(ui);
        } catch (FileNotFoundException e) {
            this.ui.botPrint("Error reading file: " + e.getMessage() + "\nMaking new task list");
            this.tasks = new TaskList();
        } catch (IOException e) {
            this.ui.botPrint("Save file could not be generated: " + e.getMessage() + "\nMaking new task list");
            this.tasks = new TaskList();
        }

    }

    /**
     * Starts up Duke chatbot.
     */
    public void run() {
        ui.startupMessage();

        boolean isExit = false;
        while (!isExit) {
            String input = ui.nextCommand();
            try {
                Command c = Parser.parse(input);
                c.execute(tasks, ui, storage);
                isExit = c.isExitCommand();
            } catch (NoSuchCommandException e) {
                ui.botPrint(e.getMessage());
            } catch (TaskCreationException e) {
                ui.botPrint("Error Creating Task: " + e.getMessage());
            } catch (DateTimeParseException e) {
                ui.botPrint("Error parsing datetime: " + e.getMessage()
                            + "\nUse the format \"DD/MM/YYYY, HH:MM\" to enter date and time.");
            } catch (IndexOutOfBoundsException e) {
                ui.botPrint(e.getMessage());
            } catch (NumberFormatException e) {
                ui.botPrint("Invalid selection for marking or deletion: " + e);
            } catch (TaskModificationException e) {
                ui.botPrint("Error Modifying Task: " + e.getMessage());
            } catch (IOException e) {
                ui.botPrint(e.getMessage());
            }
        }

        ui.goodbyeMessage();
        ui.closeUi();
    }

    /**
     * Main method for starting up bot.
     *
     * @param args
     */
    public static void main(String[] args) {
        new Duke("data/data.txt").run();
    }
}
