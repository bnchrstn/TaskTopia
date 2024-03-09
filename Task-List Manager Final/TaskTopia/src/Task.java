public class Task {
    private String title;
    private String description;
    private String date;

    // constructor to create a new task with a title, decription, and due date 
    public Task(String title, String description, String date2) {
        this.title = title;
        this.description = description;
        this.date = date2;
    }

    // getter method for the title
    public String getTitle() {
        return title;
    }

    // getter method the description 
    public String getDescription() {
        return description;
    }

    // getter method for the date
    public String getDate() {
        return date;
    }

    // override the toString method to provide a string representation of the task 
    @Override
    public String toString() {
            return title + "," + description + "," + date;
     }
}
