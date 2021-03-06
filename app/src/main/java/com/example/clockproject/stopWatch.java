package com.example.clockproject;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class stopWatch extends Fragment {

    TextView outputTxt;
    TextView recordTxt;
    Button startBtn;
    Button recordBtn;

    final static int Init =0;
    final static int Run =1;
    final static int Pause =2;

    int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.
    int myCount=1;
    long myBaseTime;
    long myPauseTime;


    public stopWatch() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stopwatch_fragment, container, false);
        outputTxt = (TextView) view.findViewById(R.id.time_out);
        recordTxt = (TextView) view.findViewById(R.id.record);
        recordTxt.setMovementMethod(new ScrollingMovementMethod());
        startBtn = (Button) view.findViewById(R.id.btn_start);
        recordBtn = (Button) view.findViewById(R.id.btn_rec);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnClick(view);
            }
        });

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myOnClick(view);
            }
        });


        return view;
    }


    public void myOnClick(View v){
        switch(v.getId()){
            case R.id.btn_start: //시작버튼을 클릭했을때 현재 상태값에 따라 다른 동작을 할수있게끔 구현.
                switch(cur_Status){
                    case Init:
                        myBaseTime = SystemClock.elapsedRealtime();
                        System.out.println(myBaseTime);
                        //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
                        myTimer.sendEmptyMessage(0);
                        startBtn.setTextColor(Color.RED);
                        recordBtn.setTextColor(Color.BLUE);
                        startBtn.setText("중지"); //버튼의 문자 변경
                        recordBtn.setEnabled(true); //기록버튼 활성
                        cur_Status = Run; //현재상태를 런상태로 변경
                        break;
                    case Run:
                        myTimer.removeMessages(0); //핸들러 메세지 제거
                        myPauseTime = SystemClock.elapsedRealtime();
                        startBtn.setTextColor(Color.GREEN);
                        startBtn.setText("계속");
                        recordBtn.setText("초기화");
                        cur_Status = Pause;
                        break;
                    case Pause:
                        long now = SystemClock.elapsedRealtime();
                        myTimer.sendEmptyMessage(0);
                        myBaseTime += (now- myPauseTime);
                        startBtn.setTextColor(Color.RED);
                        startBtn.setText("중지");
                        recordBtn.setText("기록");
                        cur_Status = Run;
                        break;
                }
                break;
            case R.id.btn_rec:
                switch(cur_Status){
                    case Run:
                        String str = recordTxt.getText().toString();
                        str +=  String.format("%d. %s\n",myCount,getTimeOut());
                        recordTxt.setText(str);
                        myCount++; //카운트 증가
                        break;
                    case Pause:
                        //핸들러를 멈춤
                        myTimer.removeMessages(0);
                        startBtn.setTextColor(Color.BLACK);
                        recordBtn.setTextColor(Color.GRAY);
                        startBtn.setText("시작");
                        recordBtn.setText("기록");
                        outputTxt.setText("00:00:00");
                        cur_Status = Init;
                        myCount = 1;
                        recordTxt.setText("");
                        recordBtn.setEnabled(false);
                        break;
                }
                break;

        }
    }

    Handler myTimer = new Handler(){
        public void handleMessage(Message msg){
            outputTxt.setText(getTimeOut());

            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            myTimer.sendEmptyMessage(0);
        }
    };
    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime(); //애플리케이션이 실행되고나서 실제로 경과된 시간
        long outTime = now - myBaseTime;
        String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 / 60, (outTime/1000)%60,(outTime%1000)/10);
        return easy_outTime;
    }


}
