package local.hal.st32.android.todo45008;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by fei on 2016/04/15.
 */
public class DataAccess
{
    private static String sql = "SELECT _id, name, deadline, note FROM tasks order by deadline asc";

    public void setSql(String sql)
    {
        this.sql = sql;
    }

    /**
     * 全データ検索メソッド

     *
     * @param context コンテキスト
     * @return 検索結果のCursorオブジェクト
     */
    public static Cursor findAll(Context context)
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        //String sql = "SELECT _id, name, deadline, note FROM tasks order by done asc ,_id desc";
        //sql = "SELECT _id, name, deadline, note FROM tasks order by done asc ,_id desc";

        Cursor cursor = db.rawQuery(sql,null);
        return cursor;
    }

    /**
     * 主キーによる検索
     *
     * @param  context コンテキスト
     * @param id 主キー値
     * return
     * 主キーに対応するデータを格納したMemoオブジェクト。対応するデータが存在しない場合はnull.
     */
    public static ToDo findByPK(Context context, int id)
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = null;
        ToDo result = null;
        String sql = "SELECT _id,name, deadline, done, note FROM tasks WHERE _id =" + id;
        try
        {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst())
            {
                int idxName = cursor.getColumnIndex("name");
                int idxDeadline = cursor.getColumnIndex("deadline");
                int idxDone = cursor.getColumnIndex("done");
                int idxNote = cursor.getColumnIndex("note");

                String name = cursor.getString(idxName);
                String deadline = cursor.getString(idxDeadline);
                int done = cursor.getInt(idxDone);
                String note = cursor.getString(idxNote);

                result = new ToDo();
                result.setId(id);
                result.setName(name);
                result.setDeadline(deadline);
                result.setDone(done);
                result.setNote(note);

            }

        }
        catch(Exception ex)
        {
            Log.e("ERROR", ex.toString());
        }
        finally
        {
            db.close();
        }
        return result;
    }

    /**
     * 情報を更新するメソッド
     * @param context
     * @param id
     * @param name
     * @param deadline
     * @param done
     * @param note
     */
    public static void update(Context context,int id,String name,String deadline,int done ,String note)
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "UPDATE tasks SET name = ?, deadline = ?, done = ?,note = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, name);
        stmt.bindString(2, deadline);
        stmt.bindLong(3, done);
        stmt.bindString(4, note);
        stmt.bindLong(5, id);


        db.beginTransaction();
        try
        {

            stmt.executeInsert();
            db.setTransactionSuccessful();
        }
        catch(Exception ex)
        {
            Log.e("ERROR", ex.toString());
        }
        finally
        {
            db.endTransaction();
            db.close();
        }
    }

    /**
     *完了未完了の切り替え
     * @param context
     * @param id
     */
    public static void updateDone(Context context,int id,int done)
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "UPDATE tasks SET done = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);

        stmt.bindLong(1, done);
        stmt.bindLong(2, id);


        db.beginTransaction();
        try
        {

            stmt.executeInsert();
            db.setTransactionSuccessful();
        }
        catch(Exception ex)
        {
            Log.e("ERROR", ex.toString());
        }
        finally
        {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 情報を新規登録するメソッド
     * @param context
     * @param name
     * @param deadline
     * @param done
     * @param note
     */
    public static void insert(Context context,String name,String deadline,int done ,String note)
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "INSERT INTO tasks (name,deadline,done,note) VALUES (?,?,?,?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, name);
        stmt.bindString(2, deadline);
        stmt.bindLong(3, done);
        stmt.bindString(4, note);


        db.beginTransaction();
        try
        {
            stmt.executeInsert();
            db.setTransactionSuccessful();
        } catch (Exception ex)
        {
            Log.e("ERROR", ex.toString());
        }
        finally
        {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * 情報を削除するメソッド
     *
     * @param context コンテキスト
     * @param id 主キー値
     */
    public static void delete(Context context, int id)
    {
        DatabaseHelper helper = new DatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "DELETE FROM tasks WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1, id);

        db.beginTransaction();
        try
        {
            stmt.executeInsert();
            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.e("ERROR",ex.toString());
        }
        finally
        {
            db.endTransaction();
            db.close();
        }
    }


}
