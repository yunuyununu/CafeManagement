package com.example.cafemanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.regex.Pattern;

public class CategoryPopupActivity extends AppCompatActivity {
    LinearLayout layout;
    ImageView iv;
    Spinner spinner;
    EditText editCategory, editId;
    Button btnDelete, btnAdd;
    MenuDAO menuDao;
    CategoryDAO dao;
    List<String> group;
    private boolean checkFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //타이틀바 보이지 않게 설정(setContentView 이전에 선언해야 한다.)

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        //커스텀 다이얼로그 밖의 화면을 흐리게 설정
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //다이얼로그 밖 배경을 투명하게 설정

        setContentView(R.layout.activity_category_popup);
        //this.setFinishOnTouchOutside(false);
        //Dialog 이외의 영역을 터치 했을 때, true(화면종료) or false(화면종료되지 않음)

        layout = findViewById(R.id.layout);
        iv = findViewById(R.id.iv);
        spinner = findViewById(R.id.spinner);
        editCategory = findViewById(R.id.editCategory);
        editId = findViewById(R.id.editId);
        btnDelete = findViewById(R.id.btnDelete);
        btnAdd = findViewById(R.id.btnAdd);
        //각 리소스 아이디 연결

        menuDao = new MenuDAO(this);
        dao = new CategoryDAO(this);
        //객체 시작주소값 연결

        group = dao.readCategoryFromDB();
        //상품분류 테이블(categoryTab)의 분류명을 DB에서 읽어와서 리스트로 저장
        //리스트의 제일 마지막인덱스 : 안내문구("분류를 추가~")

        //각 텍스트뷰에 입력 필터 설정
        editCategory.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-힣\\s]+$");
                //분류명에 문자(한글, 영어)만 입력되도록 제한. 공백허용
                if (!ps.matcher(source).matches()) {
                    return "";
                }
                return null;
            }
        }});

        editId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    editId.setText(s.subSequence(0, 2));
                    editId.setSelection(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        editId.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Pattern ps = Pattern.compile("^[a-zA-Z]+$");
                //분류코드에 문자(영어)만 입력되도록 제한
                if (!ps.matcher(source).matches()) {
                    return "";
                }
                return null;
            }
        }});

        //스피너에 연결할 어댑터 설정(기본제공 레이아웃 사용)
        ArrayAdapter adapterS = new ArrayAdapter(this, android.R.layout.simple_spinner_item, group) {
            @Override
            public int getCount() {
                return (group.size() >= 2 ? group.size() - 1 : group.size());
                //분류테이블에 데이터가 있는 경우, 사용자에게 스피너항목으로 분류명만 보이도록 마지막인덱스(안내문) 제외
                //분류데이블에 데이터가 없는 경우, 안내문으로 추가한 마지막인덱스(0번 인덱스)만
            }
        };
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //드롭다운으로 보여지는 부분. 기본제공 레이아웃 사용
        spinner.setAdapter(adapterS);
        //스피너에 어댑터 연결

        //스피너 항목 선택 이벤트 처리
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (checkFirst) {
                    spinner.setSelection(group.size() - 1);
                    checkFirst = false;
                }
                editCategory.setText(spinner.getSelectedItem().toString());
                //기존분류 확인 후 선택 시 텍스트 자동셋팅되게 설정
                editId.setText(dao.readCategoryIdFromDB(editCategory.getText().toString()).toString());
                //분류코드는 기존항목 선택 시, 메소드를 호출해서 결과값을 받아온다.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(group.size() - 1);
                //액티비티를 처음 실행했을 때 보여지는 값 → group의 마지막인덱스(안내문구)
            }
        });

        //버튼 클릭 시 상품분류 추가
        btnAdd.setOnClickListener(new View.OnClickListener() {
            String categoryId;
            String category;
            MenuDTO dto = new MenuDTO(categoryId, category);
            String result;

            @Override
            public void onClick(View v) {
                category = editCategory.getText().toString();
                categoryId = editId.getText().toString();

                if (TextUtils.isEmpty(editCategory.getText()) || TextUtils.isEmpty(editId.getText())) {
                    Toast.makeText(CategoryPopupActivity.this, "입력하신 정보를 확인해주세요.", Toast.LENGTH_SHORT).show();

                    if (TextUtils.isEmpty(editCategory.getText())) {
                        editCategory.requestFocus();
                    } else if (TextUtils.isEmpty(editId.getText()))
                        editId.requestFocus();
                    //텍스트뷰가 빈값인 곳으로 커서 이동
                } else {
                    dto.setCategory(category);
                    dto.setCategoryId(categoryId);
                    result = dao.addCategory(dto);   //CategoryDAO의 insert쿼리 실행
                    switch (result) {
                        case "succeed":
                            Toast.makeText(CategoryPopupActivity.this, "상품분류를 추가했습니다.", Toast.LENGTH_SHORT).show();
                            finish();
                            break;
                    }
                }
            }
        });

        //버튼 클릭 시 상품분류 삭제
        btnDelete.setOnClickListener(new View.OnClickListener() {
            String categoryId;
            String category;
            MenuDTO dto = new MenuDTO(categoryId, category);
            String result;

            @Override
            public void onClick(View v) {
                category = editCategory.getText().toString();
                categoryId = editId.getText().toString();
                if (TextUtils.isEmpty(editId.getText())) {
                    Toast.makeText(CategoryPopupActivity.this, "입력하신 정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    if (TextUtils.isEmpty(editCategory.getText())) {
                        editCategory.requestFocus();
                    } else if (TextUtils.isEmpty(editId.getText()))
                        editId.requestFocus();
                    //텍스트뷰가 빈값인 곳으로 커서 이동
                } else {
                    dto.setCategory(category);
                    dto.setCategoryId(categoryId);
                    result = dao.deleteCategory(dto);   //CategoryDAO의 delete쿼리 실행
                    switch (result) {
                        case "succeed":
                            Toast.makeText(CategoryPopupActivity.this, "상품분류를 삭제했습니다.(" + editCategory.getText().toString() + ")", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CategoryPopupActivity.this, MenuAddActivity.class);
                            finish();
                            startActivity(intent);
                            break;
                        case "fail":
                            Toast.makeText(CategoryPopupActivity.this, "입력하신 정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            editCategory.requestFocus();
                            break;
                    }
                }
            }
        });

        //다이얼로그 내부 화면 터치했을 때, 소프트키보드 내리기
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hidekeyboard();
                return false;
            }
        });

        //아이콘 클릭 시 입력한 내용 초기화
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setSelection(group.size() - 1);
                editCategory.setText("");
                editId.setText("");
            }
        });
    } //onCreate의 끝

    public void hidekeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
