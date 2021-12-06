package com.example.gugudan2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static final int PROGRESSBAR_START=1;
    static final int WHAT_HANDLER_MSG_COUNT =2;

    ProgressBar pb;
    TextView tv_last;
    Button bt_ok, bt_cancel, bt_start;
    EditText et_count;

    int result;  // 결과 출력 (정답 or 오답)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar);
        tv_last=(TextView)findViewById(R.id.tv_last);
        bt_start = (Button)findViewById(R.id.btn_start);
        bt_ok = (Button) findViewById(R.id.btn_ok);
        bt_cancel = (Button) findViewById(R.id.btn_cancel);
        et_count = (EditText) findViewById(R.id.et_count);

        bt_ok.setOnClickListener(listener);
        bt_cancel.setOnClickListener(listener);
        bt_ok.setClickable(false);
    }


    // 버튼으로 답 입력
    public void onclick(View view) {
        EditText et_a = (EditText) findViewById(R.id.et_a);
        et_a.append(((Button)view).getText());
    }

    // makeQuiz(): 랜덤 값으로 문제 출력하고 결과 return
    public int makeQuiz(){
        Random random = new Random();
        int a = random.nextInt(9) + 1;
        int b = random.nextInt(9) + 1;
        int result = a * b;

        EditText et_q = (EditText) findViewById(R.id.et_q);
        et_q.setText(a+" * "+b);

        return result;
    }

    // bt_ok의 onClickListener
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText et_a = (EditText) findViewById(R.id.et_a);
            EditText et_result = (EditText) findViewById(R.id.et_result);
            EditText et_count = (EditText) findViewById(R.id.et_count);
            int id = view.getId();

            if(id==R.id.btn_ok){
                int answer = Integer.parseInt((et_a.getText()).toString());

                if(result == answer){
                    et_result.setText("정답!");

                    int count = Integer.parseInt((et_count.getText()).toString()) + 1;
                    et_count.setText(""+count);
                }
                else{
                    et_result.setText("오답!");
                }

                result = makeQuiz();
            }

            et_a.setText("");
        }
    };

    public void onClickStart(View view) {
        pb.setProgress(0);
        handler.sendEmptyMessage(PROGRESSBAR_START);

        // 시작과 동시에 게임 세팅
        et_count.setText("0");
        result = makeQuiz();

        // 프로그래스 바 Thread
        Thread th_count = new Thread("count thread"){
            @Override
            public void run(){
                for(int i=60; i>=0; i--){
                    Message msg = handler.obtainMessage(WHAT_HANDLER_MSG_COUNT, i, 0);
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){}

                }
            }
        };

        th_count.start();
    }

    Handler handler =  new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            // tv: Count & 점수 출력
            if(msg.what==PROGRESSBAR_START){
                if(pb.getProgress() < pb.getMax()){
                    bt_ok.setClickable(true);
                    bt_start.setClickable(false);
                    pb.setProgress(pb.getProgress()+1);
                    sendEmptyMessageDelayed(PROGRESSBAR_START, 1000);
                }
                else
                {
                    bt_ok.setClickable(false);
                    bt_start.setClickable(true);
                }
            }

            // tv_last: 남은 시간 출력
            if(msg.what==WHAT_HANDLER_MSG_COUNT){
                tv_last.setTextColor(Color.rgb(0, 0, 0));
                tv_last.setText(msg.arg1 + "초");

                // 10초 이하면 글씨 빨강색으로
                if(msg.arg1==0) {
                    tv_last.setText("당신의 점수는 " + et_count.getText().toString() + "점!!");
                    bt_ok.setClickable(false);
                }

                else if(msg.arg1<=10) {
                    tv_last.setTextColor(Color.rgb(255, 0, 0));
                }
            }
        }
    };

}


