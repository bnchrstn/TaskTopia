import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.PriorityQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class TaskListManager extends JFrame {
    private PriorityQueue<Task> taskQueue; // priority queue for managing tasks by due date
    private JPanel taskListPanel;
    private static final String TASKS_FILE = "tasks.txt"; // this creates the .txt file that the tasks are being saved 
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy"); // date format 

    private Font robotoFont = new Font("Roboto", Font.PLAIN, 15); // sets font choice 

    // constructor for the main application window 
    public TaskListManager() {
        taskQueue = new PriorityQueue<>(new TaskComparator()); // initialize priority queue with a comparator 
        taskQueue = new PriorityQueue<>((task1, task2) -> task1.getDate().compareTo(task2.getDate())); // sorts tasks by ascending order of the due date 

        // sets the properties and look of the GUI 
        setTitle("TaskTopia");
        setSize(700, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Task Title:");
        JTextField titleField = new JTextField(20);

        JLabel descriptionLabel = new JLabel("Task Description:");
        JTextField descriptionField = new JTextField(20);

        JLabel dateLabel = new JLabel("Due Date (MM-DD-YY):");
        JTextField dateField = new JTextField(10);

        titleLabel.setFont(robotoFont);
        titleLabel.setForeground(Color.WHITE);
        descriptionLabel.setFont(robotoFont);
        descriptionLabel.setForeground(Color.white);
        dateLabel.setFont(robotoFont);
        dateLabel.setForeground(Color.white);

        JButton addButton = new JButton("Add Task");
        addButton.setBackground(Color.green);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // adds a new task with the input data and clear the input fields 
                addTask(titleField.getText(), descriptionField.getText(), dateField.getText());
                titleField.setText("");
                descriptionField.setText("");
                dateField.setText("");
            }
        });

        JButton deleteButton = new JButton("Accomplish Task");
        deleteButton.setBackground(Color.pink);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // deletes certain tasks that are selected in the GUI 
                deleteTopTask();
            }
        });

        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionField);
        inputPanel.add(dateLabel);
        inputPanel.add(dateField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);
        inputPanel.setBackground(Color.darkGray);

        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskListPanel), BorderLayout.CENTER);

        // loads existing tasks from the .txt file and displaying it 
        loadTasks();
        displayTasks();
    }

    private void deleteTopTask() {
        if (!taskQueue.isEmpty()) {
            taskQueue.poll(); // Removes the top task
            updateTasks();
            saveTasks();
        } else {
            JOptionPane.showMessageDialog(this, "No tasks to accomplish.");
        }
    }

    // Display the tasks in the GUI sorted by due date
    private void displayTasks() {
        taskListPanel.removeAll();
        PriorityQueue<Task> sortedTasks = new PriorityQueue<>(taskQueue); // Maintains sorting
        
        while (!sortedTasks.isEmpty()) {
            Task task = sortedTasks.poll();
            String taskText = "Title: " + task.getTitle() + "<br>Description: " + task.getDescription() + "<br>Due Date: " + task.getDate();
            JLabel label = new JLabel("<html>" + taskText + "</html>");
            label.setFont(robotoFont);
            taskListPanel.add(label);

            // Add spacing between tasks
            taskListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    // adds a new task to the taks queue 
    private void addTask(String title, String description, String dateInput) {
        if (!title.isEmpty() && !description.isEmpty() && !dateInput.isEmpty()) {
            try {
                // Parse the date
                dateFormat.setLenient(false);
                dateFormat.parse(dateInput);
                Task task = new Task(title, description, dateInput);
                taskQueue.offer(task);
                updateTasks();
                saveTasks();
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use MM-dd-yy.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please fill out all fields.");
        }
    }    

    private class TaskComparator implements Comparator<Task> {
        @Override
        public int compare(Task task1, Task task2) {
            try {
                // compares the tasks based on their due dates 
                return dateFormat.parse(task1.getDate()).compareTo(dateFormat.parse(task2.getDate()));
            } catch (ParseException e) {
                // handles exception if there is an issue with the date parsing  
                e.printStackTrace();
                return 0;
            }
        }
    }

    // saves the input tasks to a file (.txt) 
    private void saveTasks() {
        try (PrintWriter writer = new PrintWriter(new File(TASKS_FILE))) {
            for (Task task : taskQueue) {
                writer.println(task.toString());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // loads the saved task from the .txt file 
    private void loadTasks() {
        taskQueue.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String title = parts[0];
                    String description = parts[1];
                    String date = parts[2];
                    Task task = new Task(title, description, date);
                    taskQueue.offer(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTasks() {
        displayTasks();
    }
}
