package local.hal.st32.android.todo45008;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by fei on 2016/04/13.
 */
public class ToDoEditActivity extends AppCompatActivity
{
    /**
     * 新規登録モードが更新モードかを表すフィールド
     */
    private int _mode = ToDoListActivity.MODE_INSERT;

    /**
     * 更新モードの際、現在表示しているメモ情報のデータベース上に主キー値
     */
    private int _idNo = 0;

    /**
     * 完了、未完了
     */
    private int intDone = 0;

    /**
     * 現在時刻
     */
    private int nowYear = 0;

    private int nowMonth = 0;

    private int nowDayOfMonth = 0;

    /**
     * 変更した日付
     */
    private int _year = 0;

    private int _monthOfYear = 0;

    private int _dayOfMonth = 0;

    /**
     * データベースから取得してきた日付
     */
    private int year = 0;

    private int monthOfYear = 0;

    private int dayOfMonth = 0;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_to_do_edit);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        _mode = intent.getIntExtra("mode",ToDoListActivity.MODE_INSERT);

        Switch switch1 = (Switch) findViewById(R.id.switch1);

        switch1.setOnCheckedChangeListener(new OnCheckedChangeListener()
        {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {

                if(isChecked)
                {
                    //未完了
                    intDone = 0;
                }
                else
                {
                    //完了
                    intDone = 1;
                }

            }
        });

        if(intDone == 0)
        {
            switch1.setChecked(true);
        }
        else
        {
            switch1.setChecked(false);
        }

        if (_mode == ToDoListActivity.MODE_INSERT)
        {
            TextView tvTitleEdit = (TextView) findViewById(R.id.tvTitleEdit);
            tvTitleEdit.setText(R.string.tv_title_insert);

//            Button btnSave = (Button) findViewById(R.id.btnSave);
//            btnSave.setText(R.string.btn_insert);
//
//            Button btnDelete = (Button) findViewById(R.id.btnDelete);
//            btnDelete.setVisibility(View.INVISIBLE);


            TextView etInputDateline = (TextView) findViewById(R.id.etInputDeadline);
            Calendar cal = Calendar.getInstance();
            nowYear = cal.get(Calendar.YEAR);
            nowMonth = cal.get(Calendar.MONTH);
            nowDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            String nowDate = sdf.format(cal.getTime());
           // etInputDateline.setText(nowYear + "年" + (nowMonth + 1) + "月" + nowDayOfMonth + "日");
           // etInputDateline.setText(nowYear + "-" + (nowMonth + 1) + "-" + nowDayOfMonth );

            etInputDateline.setText(nowDate);
        }
        else
        {
            _idNo = intent.getIntExtra("idNo", 0);

            ToDo toDoData = DataAccess.findByPK(ToDoEditActivity.this, _idNo);

            EditText etInputName = (EditText) findViewById(R.id.etInputName);
            etInputName.setText(toDoData.getName());

            TextView etInputDeadline = (TextView) findViewById(R.id.etInputDeadline);
            etInputDeadline.setText(toDoData.getDeadline());


          // データベースから取得した日付
            if (!toDoData.getDeadline().equals(""))
            {

                //年月日型に変換
                String strDate = setTextDate(toDoData.getDeadline());
                TextView _deadLine = (TextView) findViewById(R.id.etInputDeadline);
                _deadLine.setText(strDate);

                year = Integer.parseInt(strDate.substring(0, strDate.indexOf("年")));

               // year = Integer.parseInt(toDoData.getDeadline().substring(0, toDoData.getDeadline().indexOf("年")));

               // monthOfYear = Integer.parseInt(toDoData.getDeadline().substring(toDoData.getDeadline().indexOf("年") + 1, toDoData.getDeadline().indexOf("月")));
                monthOfYear = Integer.parseInt(strDate.substring(strDate.indexOf("年") + 1, strDate.indexOf("月")));

               // dayOfMonth = Integer.parseInt(toDoData.getDeadline().substring(toDoData.getDeadline().indexOf("月") + 1, toDoData.getDeadline().indexOf("日")));
                dayOfMonth = Integer.parseInt(strDate.substring(strDate.indexOf("月") + 1, strDate.indexOf("日")));

            }

            EditText etInputNote = (EditText) findViewById(R.id.etInputNote);
            etInputNote.setText(toDoData.getNote());

            //完了したかどうか取得し、スイッチを表示
            if(toDoData.getDone() == 0)
            {
                switch1.setChecked(true);
            }
            else
            {
                switch1.setChecked(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_edit_to_do, menu);

        if (_mode == ToDoListActivity.MODE_INSERT) {

            menu.getItem(0).setVisible(false);
            menu.getItem(1).setTitle("登録");

        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        EditText etInputName = (EditText) findViewById(R.id.etInputName);
        String inputName = etInputName.getText().toString();
        switch (itemId)
        {
            case android.R.id.home:
                finish();
                return true;

            case R.id.btnChange:
                if (inputName.equals(""))
                {
                    Toast.makeText(ToDoEditActivity.this, R.string.msg_input_name, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    //タスク名

                    TextView etInputDeadline = (TextView) findViewById(R.id.etInputDeadline);

                   String inputDeadline = etInputDeadline.getText().toString();



                    //詳細
                    EditText etInputNote = (EditText) findViewById(R.id.etInputNote);
                    String inputNote = etInputNote.getText().toString();

                    if(_mode == ToDoListActivity.MODE_INSERT)
                    {

                        DataAccess.insert(ToDoEditActivity.this,inputName,replaceDate(inputDeadline),intDone,inputNote);

                    }
                    else
                    {

                        DataAccess.update(ToDoEditActivity.this, _idNo, inputName,replaceDate(inputDeadline),intDone,inputNote);

                    }
                    finish();
                }
                break;

            case R.id.btnDelete:


                AlertDialog.Builder builder = new AlertDialog.Builder(ToDoEditActivity.this);

                builder.setTitle("タスク名：" + inputName);
                builder.setMessage("削除してもよろしいですか？");
                builder.setPositiveButton("Cancel", new DialogButtonClickListener());
                builder.setNegativeButton("OK", new DialogButtonClickListener());
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * 戻るボタンが押された時のイベント処理用メソッド
     *
     * @param view 画面部品
     */
    public void onBackButtonClick(View view)
    {
        finish();
    }

    /**
     * 削除ボタンが押された時のイベント処理用メソッド
     *
     *
     */
//    public void onDeleteButtonClick (View view)
//    {
//        EditText etInputName = (EditText) findViewById(R.id.etInputName);
//        String inputName = etInputName.getText().toString();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(ToDoEditActivity.this);
//
//        builder.setTitle("タスク名：" + inputName);
//        builder.setMessage("削除してもよろしいですか？");
//        builder.setPositiveButton("Cancel", new DialogButtonClickListener());
//        builder.setNegativeButton("OK", new DialogButtonClickListener());
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }


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
                    DataAccess.delete(ToDoEditActivity.this, _idNo);
                    finish();
                    break;
            }
        }
    }

    /**
     * 日付選択ダイアログ表示ボタンが押された時のイベント処理用メソッド
     *
     * @param view 画面部品
     */
    public void showDatePickerDialog(View view)
    {

        DatePickerDialog dialog = new DatePickerDialog(ToDoEditActivity.this, new DatePickerDialogDateSetListener(), nowYear, nowMonth, nowDayOfMonth);
        if (year != 0)
        {
            dialog = new DatePickerDialog(ToDoEditActivity.this, new DatePickerDialogDateSetListener(), year, monthOfYear - 1, dayOfMonth);
        }
        if (_year != 0)
        {
            dialog = new DatePickerDialog(ToDoEditActivity.this, new DatePickerDialogDateSetListener(), _year, _monthOfYear, _dayOfMonth);

        }


        dialog.getDatePicker().setCalendarViewShown(true); //カレンダーを入れる
        dialog.show();
    }
    /**
     * 日付選択ダイアログの完了ボタンが押された時の処理が記述されたメンバクラス
     *
     * @author fei
     */
    private class DatePickerDialogDateSetListener implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {

            _dayOfMonth = dayOfMonth;
            _monthOfYear = monthOfYear;
           //System.out.println(String.format("%1$tm",String.valueOf(monthOfYear)));
            _year = year;
            TextView etInputDateline = (TextView) findViewById(R.id.etInputDeadline);
          //  etInputDateline.setText(year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日");

            String s = year + "年" + String.format("%02d", monthOfYear + 1) + "月" + String.format("%02d",dayOfMonth) + "日";
           // String s = year + "年" + String.format("%02d", monthOfYear + 1) + "月" + dayOfMonth + "日";
            System.out.println("ここ"+s);
            etInputDateline.setText(s);

        }
    }


    /**
     * 日付の表示を年月日の形式に変換
     * @param date
     * @return
     */
    public String setTextDate(String date)
    {
        System.out.println(date.replaceFirst("-","年").replaceFirst("-","月")+"日");
        return date.replaceFirst("-","年").replaceFirst("-","月")+"日";
    }

    public String replaceDate(String date)
    {

        date = date.replaceAll("年","-").replaceAll("月","-").replaceAll("日","");
        System.out.println(date);
        return date;
    }
}
