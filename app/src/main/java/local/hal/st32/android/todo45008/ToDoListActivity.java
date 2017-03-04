package local.hal.st32.android.todo45008;

import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

//public class ToDoListActivity extends ListActivity
public class ToDoListActivity extends AppCompatActivity
{

    private static final String todo = "todoFile";

    private static String sql = "";

    private static int  id = 0;

    private int _idNo = 0;
    private static String title = "";
    /**
     * 新規登録モードを表す定数フィールド
     */
    static final int MODE_INSERT = 1;

    /**
     *更新モードを表す呈す
     */
    static final int MODE_EDIT = 2;


    /**
     * メニューリストの種類
     */
    private int _menuCategory;

    /**
     * 全タスク
     */
    private static  final int _all = 3;

    /**
     *未完了タスク
     */
    private static  final int _incomplete= 4;

    /**
     *完了タスク
     */
    private static  final int _finish = 5;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_to_do_list);

       // ListView list = (ListView)this.findViewById(R.id.android_list);
       // list.setOnItemClickListener(new ListItemClickListener());
        list = (ListView)this.findViewById(R.id.android_list);

        list.setOnItemClickListener(new ListItemClickListener());

        //コンテキストメニュー
        registerForContextMenu(list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_to_do, menu);
        return true;
    }

    //アクションバー
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuListOptionTitle = menu.findItem(R.id.menuListOptionTitle);
        switch (_menuCategory)
        {
            case _all:
                menuListOptionTitle.setTitle(R.string.menu_all);
                System.out.println("全");
                break;
            case _incomplete:
                menuListOptionTitle.setTitle(R.string.menu_incomplete);
                System.out.println("未");

                break;

            case _finish:
                menuListOptionTitle.setTitle(R.string.menu_finish);
                System.out.println("完");
                break;

        }
        return true;
    }

    /**
     * オプションメニュー選択肢ごとの処理
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

       // TextView tv_title = (TextView) findViewById(R.id.tvTitle);
        //データ保存
        SharedPreferences settings = getSharedPreferences(todo,MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        int itemId = item.getItemId();

        DataAccess db = new DataAccess();
        switch (itemId)
        {
            case R.id.menuALl:
                //db.setSql("SELECT _id, name, deadline, note FROM tasks order by deadline asc");
                sql = "SELECT _id, name, deadline, note, done FROM tasks order by deadline desc";
                //title = "全タスク";
                _menuCategory = _all;
                db.setSql(sql);
               // tv_title.setText(title);
                break;

            case R.id.menuIncomplete:
                //title = "未完了タスク";
                _menuCategory = _incomplete;
                sql = "SELECT _id, name, deadline, note, done FROM tasks where done = 0 order by deadline asc";
                db.setSql(sql);
                //tv_title.setText(title);
                break;

            case R.id.menuFinish:
                //title = "完了タスク";
                _menuCategory = _finish;
                sql ="SELECT _id, name, deadline, note, done FROM tasks where done = 1 order by deadline desc";
                db.setSql(sql);
                //tv_title.setText(title);
                break;
        }

        refreshList();

        id = itemId;
        editor.putInt("id",id);
        editor.commit();
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume()
    {
        //データ保存
        SharedPreferences settings = getSharedPreferences(todo,MODE_PRIVATE);
        id = settings.getInt("id",0);
        //リスト生成するためsqlを渡す
        List<String> _list = prefenceData(id);
        sql = _list.get(0);
        DataAccess db = new DataAccess();
        db.setSql(sql);

        //選択されたオプションメニュの名前を保存
//        TextView tv_title = (TextView) findViewById(R.id.tvTitle);
//        tv_title.setText(_list.get(1));
        _menuCategory = Integer.parseInt(_list.get(1));
        refreshList();
        super.onResume();
        //追加

    }

    /**
     * オプションメニューを選択して、Listを更新
     */
    public void refreshList()
    {
        //ListView list = (ListView)this.findViewById(R.id.android_list);

        Cursor cursor = DataAccess.findAll(ToDoListActivity.this);
        String[] from = {"name","deadline","done","_id"};
        int[] to = {R.id.list_title,R.id.dateText,R.id.checkState,R.id.btnDelete};


       // SimpleCursorAdapter adapter = new SimpleCursorAdapter(ToDoListActivity.this,android.R.layout.simple_list_item_2,cursor,from,to,0);



//        int[] to = {R.id.list_title,R.id.dateText};
       SimpleCursorAdapter adapter = new SimpleCursorAdapter(ToDoListActivity.this,R.layout.row,null,from,to,0);
        adapter.setViewBinder(new CustomViewBinder());
        list.setAdapter(adapter);
        setNewCursor();
        System.out.println(adapter);
    }

    private void setNewCursor()
    {
        Cursor cursor = DataAccess.findAll(ToDoListActivity.this);
        SimpleCursorAdapter adapter = (SimpleCursorAdapter) list.getAdapter();
        adapter.changeCursor(cursor);
    }


    //@Override
//        public void onListItemClick (ListView listView,View view,int position, long id)
//        {
//            ListView list = (ListView)this.findViewById(R.id.android_list);
//
//            //super.onListItemClick(listView, view, position, id);
//            Cursor item = (Cursor) listView.getItemAtPosition(position);
//            int idxId = item.getColumnIndex("_id");
//            int idNo = item.getInt(idxId);
//
//            Intent intent = new Intent(ToDoListActivity.this, ToDoEditActivity.class);
//            intent.putExtra("mode", MODE_EDIT);
//            intent.putExtra("idNo",idNo);
//            intent.putExtra("idNo",(int)id);
//            startActivity(intent);
//        }


    /**
     * リストをクリックする処理
     */
    public class ListItemClickListener implements OnItemClickListener
        {

            @Override
            public void onItemClick (AdapterView<?> parent ,View view,int position, long id)
            {
                //ListView list = (ListView) this.findViewById(R.id.android_list);

                //super.onListItemClick(listView, view, position, id);
                //ListView list = (ListView) findViewById(R.id.android_list);
                list = (ListView) findViewById(R.id.android_list);

                Cursor item = (Cursor) list.getItemAtPosition(position);
                //Cursor test = (Cursor) list.getAdapter();

                int idxId = item.getColumnIndex("_id");
                int idNo = item.getInt(idxId);

                Intent intent = new Intent(ToDoListActivity.this, ToDoEditActivity.class);
                intent.putExtra("mode", MODE_EDIT);
                intent.putExtra("idNo", idNo);
                intent.putExtra("idNo", (int) id);
                startActivity(intent);

            }
        }

    /**
     * 新規ボタンが押された時のイベント処理用メソッド
     *
     * @param view 画面部品
     */
    public void onNewButtonClick(View view)
    {
        Intent intent = new Intent(ToDoListActivity.this, ToDoEditActivity.class);
        intent.putExtra("mode", MODE_INSERT);
        startActivity(intent);
    }

    //コンテキストメニュー処理は以下

    /**
     * コンテキストメニューを表示する
     * @param menu
     * @param view
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu (ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu,view,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_to_do,menu);
        menu.setHeaderTitle(R.string.context_menu_title);
        menu.setHeaderIcon(android.R.drawable.ic_menu_agenda);

    }

    /**
     * コンテキストメニューを選択ごとに違う処理
     * @param item
     * @return
     */

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;
        DataAccess db = new DataAccess();
        int itemId = item.getItemId();
        switch (itemId)
        {
            case R.id.contextFinish:
                db.updateDone(ToDoListActivity.this,id,1);
                break;

            case R.id.contextIncomplete:
                db.updateDone(ToDoListActivity.this,id,0);
                break;

            case R.id.contextOther:
                Intent intent = new Intent(ToDoListActivity.this, ToDoEditActivity.class);
                intent.putExtra("mode", MODE_EDIT);
                intent.putExtra("idNo",id);
                startActivity(intent);
                break;
        }
        refreshList();
        return super.onContextItemSelected(item);
    }

    /**
     * 保存されたitemIdで違うsql、titleリスト返す
     * @param itemId 保存されたitemID
     * @return sql、titleリスト返す
     */
    public List<String> prefenceData (int itemId)
    {
        List<String> _list = new ArrayList<String>();

        switch (itemId)
        {
            case R.id.menuALl:

                sql = "SELECT _id, name, deadline, note, done FROM tasks order by deadline desc";
                //title = "全タスク";
                _menuCategory = _all;
                _list.add(sql);
                _list.add(String.valueOf(_menuCategory));
                break;

            case R.id.menuIncomplete:
               // title = "未完了タスク";
                sql = "SELECT _id, name, deadline, note, done FROM tasks where done = 0 order by deadline asc";
                _menuCategory = _incomplete;
                _list.add(sql);
                _list.add(String.valueOf(_menuCategory));
                break;

            case R.id.menuFinish:
                //title = "完了タスク";
                sql ="SELECT _id, name, deadline, note, done FROM tasks where done = 1 order by deadline desc";
                _menuCategory = _finish;
                _list.add(sql);
                _list.add(String.valueOf(_menuCategory));
                break;

            default:
                sql = "SELECT _id, name, deadline, note, done FROM tasks order by deadline desc";
                //title = "全タスク";
                _menuCategory = _all;
                _list.add(sql);
                _list.add(String.valueOf(_menuCategory));
        }
        return _list;
    }


        private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder
        {
             @Override
            public boolean setViewValue(View view,Cursor cursor,int columnIndex)
            {


                int viewId = view.getId();

                int idIdx = cursor.getColumnIndex("_id");
                long id = cursor.getLong(idIdx);

                //id = (int)id;
                int check = cursor.getInt(columnIndex);
                    switch (viewId)
                    {

                        case R.id.checkState:


                            CheckBox checkState = (CheckBox) view;
                            boolean checked = false;

                            if (check == 1)
                            {
                                checked = true;
                            }

                            checkState.setChecked(checked);
                            checkState.setTag(id);
                            checkState.setOnClickListener(new OnCheckBoxClickListener());

                            return true;

                        case R.id.list_title:
                            int checkIdx = cursor.getColumnIndex("done");
                            check = cursor.getInt(checkIdx);
                            System.out.println( check + "/" + cursor.getInt(checkIdx));
                            TextView list_title = (TextView) view;

                            String listName = cursor.getString(columnIndex);
                            list_title.setText(listName);

                            int tColor = ContextCompat.getColor(ToDoListActivity.this,R.color.list_default);

                            if (check == 1)
                            {

                                tColor = ContextCompat.getColor(ToDoListActivity.this,R.color.list_checked);

                            }
                            list_title.setTextColor(tColor);

                            return true;

                        case R.id.dateText:
                            Calendar cal = Calendar.getInstance();

                            //フォーマットパターンを指定して表示する
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                            String nowDate = sdf.format(cal.getTime());
                            TextView dateText = (TextView) view;
                            String dateString = setTextDate(cursor.getString(columnIndex));
                            int dateColor = ContextCompat.getColor(ToDoListActivity.this,R.color.date_color);
                            if (dateString.equals(nowDate))
                            {
                                dateColor = ContextCompat.getColor(ToDoListActivity.this,R.color.date_color_change);
                                dateText.setText("期限  :  今日                    ");

                            }
                            else
                            {
                                dateText.setText("期限 : "+dateString);
                            }
                            int doneCheck = cursor.getColumnIndex("done");
                            check = cursor.getInt(doneCheck);
                            if (check == 1)
                            {
                                dateColor = ContextCompat.getColor(ToDoListActivity.this,R.color.list_checked);
                            }

                            dateText.setTextColor(dateColor);

                            return true;

                        case R.id.btnDelete:
                            Button btnDelete = (Button) view;
                            btnDelete.setTag(id);
                            return true;
                    }
                 return false;

             }

        }


    //日付表示置換
    public String setTextDate(String date)
    {
        return date.replaceFirst("-","年").replaceFirst("-","月") + "日";
    }

    //削除ボタンの処理
    public void onDeleteButtonClick (View view)
    {
        LinearLayout layout = (LinearLayout)view.getParent();
        TextView text = (TextView)layout.findViewById(R.id.list_title);
        System.out.println("TITLE : " + text.getText());

        _idNo  = (int)(long)view.getTag();
        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoListActivity.this);

        builder.setTitle("タスク名：" + text.getText());
        builder.setMessage("削除してもよろしいですか？");

        builder.setNegativeButton("OK", new DialogButtonClickListener());
        builder.setPositiveButton("Cancel", new DialogButtonClickListener());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public class DialogButtonClickListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case DialogInterface.BUTTON_POSITIVE:
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    System.out.println(_idNo+"削除直前");
                    DataAccess.delete(ToDoListActivity.this,_idNo );
                   refreshList();
                    break;

            }
        }
    }

    //チェックボックスクリックされた処理
    private class OnCheckBoxClickListener implements View.OnClickListener
    {
        @Override
        public void onClick(View view)
        {
            CheckBox checkState = (CheckBox) view;
            boolean isChecked = checkState.isChecked();
            long id = (long) checkState.getTag();
            int idInt = (int)id;
            int check = 0;
            if (isChecked)
            {
                check = 1;
            }
            DataAccess.updateDone(ToDoListActivity.this,idInt,check);

            setNewCursor();
        }
    }


    

}

