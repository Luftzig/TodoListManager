package il.ac.huji.todolist;

import java.util.Date;

public interface ITodoItem {
    public String getTitle();
    public Date getDueDate();
    public boolean hasThumb();
    public String getThumbPath();
}
