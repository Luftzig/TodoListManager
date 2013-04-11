package il.ac.huji.todolist;

import java.util.Date;

public class TodoTuple {

    private String title;
    private Date date;

    /**
     * Create new tuple.
     * @param title Todo item's title
     * @param date Todo item's due date
     */
    public TodoTuple(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
