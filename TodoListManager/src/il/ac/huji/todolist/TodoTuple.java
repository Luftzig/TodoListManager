package il.ac.huji.todolist;

import java.util.Date;

public class TodoTuple implements ITodoItem {

    private String title;
    private Date date;
    private String path;

    /**
     * Create new tuple.
     * @param title Todo item's title
     * @param date Todo item's due date
     */
    public TodoTuple(String title, Date date) {
        this.title = title;
        this.date = date;
        this.path = null;
    }

    /**
     * @param title
     * @param date
     * @param path
     */
    public TodoTuple(String title, Date date, String path) {
        this.title = title;
        this.date = date;
        this.path = path;
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

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    /**
     * Needless alias.
     */
    public Date getDueDate() {
        return getDate();
    }

    @Override
    public String getThumbPath() {
        return this.getPath();
    }

    @Override
    public boolean hasThumb() {
        return (path != null);
    }
}
