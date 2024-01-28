package duke.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDateTime;

import duke.tasks.Deadline;
import duke.tasks.Event;
import duke.tasks.Task;
import duke.tasks.Todo;

import duke.exceptions.TaskCreationException;


public class Storage {
    private File f;
    
    public Storage(String filePath) throws IOException {
        this.f = new File(filePath);
        checkAndCreateFile();
    }

    private void checkAndCreateFile() throws IOException {
        // Reading and creating data save file
        // making data folder
        if (!f.getParentFile().exists()) {
            if (!f.getParentFile().mkdir()) {
                throw new IOException("Unable to make directory");
            };
        }

        // making data file
        if (!f.exists()) {
            f.createNewFile();
         }
    }

    public TaskList readSaveData(Ui ui) throws FileNotFoundException{
        TaskList taskList = new TaskList();

        Scanner s = new Scanner(f);
        int count = 0;
        while (s.hasNext()) {
            try {
                taskList.add(parseTaskFromSave(s.nextLine()));
                count++;
            } catch (TaskCreationException e) {
                System.out.println("Error in reading task: " + e.getMessage());
            }
        }
        ui.botPrint(count + " tasks loaded from save");
        s.close();

        return taskList;

    }

    public void saveTodoData(TaskList data, Ui ui) throws IOException {
        this.checkAndCreateFile();

        FileWriter fw = new FileWriter(f);
        
        String dataString = "";
        
        for (int i = 1; i <= data.size(); i++) {
            dataString = dataString + data.get(i).toSave() + "\n";
        }
        
        fw.write(dataString);
        fw.close();

        ui.botPrint(data.size() + " tasks saved");
    }

    private Task parseTaskFromSave(String task) throws TaskCreationException {
        String[] taskSplit = task.split("\\|");
        boolean isDone;
        if (taskSplit[1].equals("[X]")) {
            isDone = true;
        } else if (taskSplit[1].equals("[ ]")) {
            isDone = false;
        } else {
            throw new TaskCreationException("Unable to determine if task (" + task + ") is done or not");
        }
        switch (taskSplit[0]) {
        case "[T]":
            return new Todo(isDone, taskSplit[2]);
        case "[D]":
            return new Deadline(isDone, taskSplit[2], 
                                LocalDateTime.parse(taskSplit[3], Parser.INPUT_DT_FORMATTER));
        case "[E]":        
            return new Event(isDone, taskSplit[2],  
                             LocalDateTime.parse(taskSplit[3], Parser.INPUT_DT_FORMATTER),  
                             LocalDateTime.parse(taskSplit[4], Parser.INPUT_DT_FORMATTER));
        default:
            throw new TaskCreationException("No such task: " + taskSplit[0] + " for " + task);
        }
    }

}
