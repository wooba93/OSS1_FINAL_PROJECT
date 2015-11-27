package com.example.hellofacebook;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.hellofacebook.R;
import com.facebook.Profile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by josungryong on 2015-11-10.
 */
public class write extends Activity implements Spinner.OnItemSelectedListener {

    private Profile profile; // 작성자 정보

    // 카메라 관련 변수
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private Uri fileUri; // 이미지 파일 리턴 값 저장
    public static final int MEDIA_TYPE_IMAGE = 1;
    private ImageView picture;
    private Button takepicutre;

    //////////////////////////////////////////////////////////////////////////////

    String[] foodstyle = {"전체", "한식", "중식", "양식", "일식"};
    String[] area = {"전체", "서울", "경기"};
    String foodstyleInpo="";   String foodstyleDetailInpo=""; String areaInpo=""; // 선택에 따른  값 저장 서버로 보낼 것
    EditText text_foodname;
    String foodname = "";
    EditText text_price;
    String price="";
    EditText text_location;
    String location="";
    EditText text_comment;
    String comment="";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write);

        // 카메라
        picture = (ImageView) findViewById(R.id.picture);
        takepicutre = (Button) findViewById(R.id.takepicture);

        // 스트링
        EditText text_foodname = (EditText) findViewById(R.id.foodname);
        EditText text_price = (EditText) findViewById(R.id.price);
        EditText text_location = (EditText) findViewById(R.id.location);
        EditText text_comment = (EditText) findViewById(R.id.comment);

        // 스피너
        Spinner foodstyleButton = (Spinner) findViewById(R.id.foodstyle);
        foodstyleButton.setOnItemSelectedListener(this); // 스피너에 아이템 클릭 시 실행될 리스너 foodstyle
        Spinner areaButton = (Spinner) findViewById(R.id.area);
        areaButton.setOnItemSelectedListener(this); // 스피너에 아이템 클릭 시 실행될 리스너 legion


         /* 어댑터 배열 객체 생성(this는 Context 메인 액티비티를 가리킴,
         * 안드로이드에 있는 어댑터 객체 에서 지원해주는 스피너 레이아웃,아이템 배열)
         * android는 안드로이드가 가지고 있는 전역 변수*/
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, foodstyle);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 리스트 형태로 늘림
        foodstyleButton.setAdapter(adapter); // 어댑터 설정

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, area);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 리스트 형태로 늘림
        areaButton.setAdapter(adapter3); // 어댑터 설정
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()) {
            case R.id.foodstyle:
                foodstyleInpo = foodstyle[position];
                break;
            case R.id.area:
                areaInpo = area[position];
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        switch (parent.getId()) {
            case R.id.foodstyle:
                foodstyleInpo = foodstyle[0];
                break;
            case R.id.area:
                areaInpo = area[0];
                break;
        }
    }


    /////////////////////// 카메라 ///////////////////////
    public void takepicture(View v) { //버튼 부
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name , 경로에 이미지 저장하기 떄문에 리턴값 필요없음

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); // start the image capture Intent
        if (fileUri != null) {
            //camera.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "FoodShare");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("FoodShare", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.e("com", "result_ok");
                // Image captured and saved to fileUri specified in the Intent

                // 이미지뷰로 바로 출력
                if (data != null) {
                    picture.setImageURI(data.getData());
                    takepicutre.setVisibility(View.INVISIBLE);
                } else {
                    takepicutre.setVisibility(View.VISIBLE);
                    Log.e("onActivityResult", fileUri.getPath());
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("com", "result_canceled");
                takepicutre.setVisibility(View.VISIBLE);
                // User cancelled the image capture
            } else {
                takepicutre.setVisibility(View.VISIBLE);
                // Image capture failed, advise user
            }
        }
    }

    // 이미지 사이즈 조절 부


    /////////////////////// 카메라 ///////////////////////

    //취소 버튼//
    public void cancleButton(View v) {
        finish();
    }

    //전송 버튼//
    public void writeButton(View v) {

        foodname = text_foodname.getText().toString();
        price = text_price.getText().toString();
        location = text_location.getText().toString();
        comment = text_comment.getText().toString();

        DataInputStream in;
        DataOutputStream out;
        String data = "";

        try {
            in = new DataInputStream(GlobalSocket.socket.getInputStream());
            out = new DataOutputStream(GlobalSocket.socket.getOutputStream());

            //data.(foodstyleInpo); // + "/" + foodstyleDetailInpo + "/" + areaInpo);


            //out.writeUTF("WRITE/" + data);//.concat(data));
            out.write(("WRITE/" + profile.getName() + "/" + foodname + "/" + foodstyleInpo + "/" + areaInpo + "/" + price).getBytes()); // 작성자, 음식이름, 음식분류, 지역, 가격, 평가, 위치
            byte[] instrA = new byte[1000];
            in.read(instrA);

        }
        catch (IOException e) {
        }
    }
}


