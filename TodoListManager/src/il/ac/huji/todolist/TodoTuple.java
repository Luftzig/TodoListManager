package il.ac.huji.todolist;

import java.util.Calendar;

/**
 * A title-date tuple
 */
public class TodoTuple {

    private String title;
    private Calendar date;

    /**
     * Create new tuple.
     * @param title Todo item's title
     * @param date Todo item's due date
     */
    public TodoTuple(String title, Calendar date) {
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
    public Calendar getCalendar() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setCalendar(Calendar date) {
        this.date = date;
    }
}
