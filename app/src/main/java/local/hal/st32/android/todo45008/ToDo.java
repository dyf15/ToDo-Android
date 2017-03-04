package local.hal.st32.android.todo45008;

/**
 * Created by fei on 2016/04/13.
 */
public class ToDo
{
    /**
     * 主キーのID値
     */
    private int _id;

    /**
     * タスク名
     */
    private String _name;

    /**
     * 期限
     */
    private String _deadline;

    /**
     * 完了、未完了
     */
    private int _done;

    /**
     * メモ
     */
    private String _note;




    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDeadline() {
        return _deadline;
    }

    public void setDeadline(String deadline) {
        _deadline = deadline;
    }

    public int getDone() {
        return _done;
    }

    public void setDone(int done) {
        _done = done;
    }

    public String getNote() {
        return _note;
    }

    public void setNote(String note) {
        _note = note;
    }
}
