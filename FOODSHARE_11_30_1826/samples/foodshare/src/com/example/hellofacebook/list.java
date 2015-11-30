package com.example.hellofacebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JSR on 2015-11-18.
 */
public class list extends Activity implements Spinner.OnItemSelectedListener {

    String[] foodstylelist = {"전체", "한식", "중식", "양식", "일식"};
    String[] arealist = {"전체", "서울", "경기"};
    String foodstyleInpo=""; String areaInpo="";

    ArrayList<Story> al = new ArrayList<Story>(); // 어뎁터 생성

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);

        ////// 리스트뷰 어뎁터 임시 ///////
        // 1. 다량의 데이터
        // 2. Adapter
        // 3. AdapterView
        al.add(new Story("서울","카레 덮밥",R.drawable.icon));
        al.add(new Story("경기","스테이크",R.drawable.icon));
        al.add(new Story("서울", "치킨", R.drawable.icon));
        al.add(new Story("서울", "떡볶이", R.drawable.icon));
        al.add(new Story("경기","탕수육",R.drawable.icon));

        // adapter
        MyAdapter adapter_list = new MyAdapter(
                getApplicationContext(), // 현재화면의 제어권자
                R.layout.item,
                al);

        // adapterView - ListView, GridView
        ListView lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(adapter_list);
        ////// 리스트뷰 어뎁터 임시 끝 /////////


        // 스피너
        Spinner foodstyleListButton = (Spinner) findViewById(R.id.foodstylelist);
        foodstyleListButton.setOnItemSelectedListener(this); // 스피너에 아이템 클릭 시 실행될 리스너 foodstyle
        Spinner areaListButton = (Spinner) findViewById(R.id.arealist);
        areaListButton.setOnItemSelectedListener(this); // 스피너에 아이템 클릭 시 실행될 리스너 legion


         /* 어댑터 배열 객체 생성(this는 Context 메인 액티비티를 가리킴,
         * 안드로이드에 있는 어댑터 객체 에서 지원해주는 스피너 레이아웃,아이템 배열)
         * android는 안드로이드가 가지고 있는 전역 변수*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, foodstylelist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 리스트 형태로 늘림
        foodstyleListButton.setAdapter(adapter); // 어댑터 설정

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, arealist);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 리스트 형태로 늘림
        areaListButton.setAdapter(adapter3); // 어댑터 설정
    }
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.foodstyle:
                foodstyleInpo = foodstylelist[position];
                break;
            case R.id.area:
                areaInpo = arealist[position];
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()) {
            case R.id.foodstyle:
                foodstyleInpo = foodstylelist[0];
                break;
            case R.id.area:
                areaInpo = arealist[0];
                break;
        }
    }
    // 스피너 끝

    // 확인 버튼
    public void serch(View v) {
    }

    public void writebutton2(View v) {
        Intent intent = new Intent(list.this, write.class);
        startActivity(intent);
    }
}


// 리스트뷰 커스텀 어댑터 //
class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<Story> al;
    LayoutInflater inf;
    public MyAdapter(Context context, int layout, ArrayList<Story> al) {
        this.context = context;
        this.layout = layout;
        this.al = al;
        this.inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() { // 총 데이터의 개수
        return al.size();
    }
    @Override
    public Object getItem(int position) { // 해당 행의 데이터
        return al.get(position);
    }
    @Override
    public long getItemId(int position) { // 해당 행의 유니크한 id
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);

        TextView tv1 = (TextView) convertView.findViewById(R.id.textView1);
        TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView1);

        Story s = al.get(position);
        tv1.setText(s.date);
        tv2.setText(s.message);
        iv.setImageResource(s.img);
        return convertView;
    }
}

class Story { // 자바빈
    String date = "";
    String message = "";
    int img; // 이미지
    public Story(String date, String message, int img) {
        this.date = date;
        this.message = message;
        this.img = img;
    }
    public Story() {} // 기본생성자 : 생성자 작업시 함께 추가하자
}