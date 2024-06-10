package com.example.cafemanagement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    EditText editId;
    EditText editPw;
    Button buttonLogin;
    ManagerDAO dao;
    CheckBox autoLogin;
    boolean saveLogin;
    SharedPreferences auto;
    String id, pw;
    SharedPreferences.Editor autoLoginEdit;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogin = findViewById(R.id.buttonLogin);
        editId = findViewById(R.id.editId);
        editPw = findViewById(R.id.editpw);
        autoLogin = findViewById(R.id.autoLogin);
        context = this;
        auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        autoLoginEdit = auto.edit();
        dao = new ManagerDAO(this);
        load();
        if (saveLogin) {
            editId.setText(id);
            editPw.setText(pw);
            autoLogin.setChecked(saveLogin);
        }

        //로그인 버튼 생성
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editId.getText().toString();
                String pw = editPw.getText().toString();
                String firstCheck = dao.firstCheck(id, pw);

                //데이터 베이스를 넣지 않고 NEW 프로젝트를 생성했을때 초기 비밀번호 설정 코드
                if (firstCheck.equals("notFirst")) { // 처음 로그인이 아닐경우 디비에 있는 id: manager, pw: 1234 입력 후 로그인

                    int result = dao.insert(id, pw);
                    if (result == 1) {
                        save();
                        custom_dialog();
                    } else if (editId.length() == 0 || editPw.length() == 0) {
                        custom_dialogA("아이디와 비밀번호를 입력해주세요.");
                    } else {
                        custom_dialogA("로그인 정보가 맞지 않습니다.\n아이디와 비밀번호를 다시 확인해주세요.");
                    }
                } else if (firstCheck.equals("first")) { // 처음 로그인할 경우 id: manager pw:0000 임의로 설정한 값으로 로그인
                    int result = dao.insert(id, pw);
                    if (result == 1) {
                        save();
                        custom_dialog();
                    } else if (editId.length() == 0 || editPw.length() == 0) {
                        custom_dialogA("아이디와 비밀번호를 입력해주세요.");
                    } else {
                        custom_dialogA("로그인 정보가 맞지 않습니다.\n 아이디와 비밀번호를 다시 확인해주세요.");
                        // 아이디, 비밀번호  입력 초기화
                        editId.setText("");
                        editPw.setText("");
                    }
                }
            }

        });
    }


    // as 버튼 생성(연락처 화면으로 이동)
    public void onClick(View v) {
        Intent intent = null;
        if (v.getId() == R.id.btn_as) {
            intent = new Intent(this, PhoneActivity.class);
            startActivity(intent);
        }
    }

    //커스텀 다이얼로그
    public void custom_dialog() {
        View dialog = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        ab.setView(dialog);

        TextView textContent = dialog.findViewById(R.id.textContent);
        textContent.setText("관리자 계정에 로그인 하시겠습니까?");


        final androidx.appcompat.app.AlertDialog alertDialog = ab.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // yes 누르면 로그인 성공 다이어로그 창이 띄워지는 동시에 다음화면으로 이동
        Button btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "관리자 계정에 로그인 되었습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ManagerActivity.class);
                startActivity(intent);
            }
        });
        // no 버튼 누르면 아이디와 비밀번호 입력값 자동 삭제
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 아이디, 비밀번호 초기화
                editId.setText("");
                editPw.setText("");
                alertDialog.dismiss();
            }
        });
    }

    // 로그인 yes 클릭시 다이얼로그 창 띄우기
    public void custom_dialogA(String a) {
        View dialog = getLayoutInflater().inflate(R.layout.custom_dialoga, null);
        androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
        ab.setView(dialog);

        TextView textContent = dialog.findViewById(R.id.textContent);
        textContent.setText(a);


        final androidx.appcompat.app.AlertDialog alertDialog = ab.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            // yes 누르면 아이디,비밀번호 입력값 자동 삭제
            @Override
            public void onClick(View v) {
                editId.setText("");
                editPw.setText("");
                alertDialog.dismiss();
            }
        });
    }

    //자동로그인
    private void save() {
        autoLoginEdit.putBoolean("save_auto", autoLogin.isChecked());
        autoLoginEdit.putString("userId", editId.getText().toString());
        autoLoginEdit.putString("userPw", editPw.getText().toString());
        autoLoginEdit.commit();
    }


    public void load() {
        saveLogin = auto.getBoolean("save_auto", false);
        id = auto.getString("userId", "");
        pw = auto.getString("userPw", "");
    }

    // 정보 삭제, 저장
    public void delete() {
        autoLoginEdit.clear();
        autoLoginEdit.commit();
    }

    public String send(String id) {
        id = auto.getString("userId", "");
        return id;
    }

    // 비밀번호 변경 저장
    public String confirm(String pw) {
        pw = auto.getString("userPw", "");
        return pw;
    }
}