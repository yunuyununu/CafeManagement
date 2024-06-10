package com.example.cafemanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MenuAddActivity extends AppCompatActivity {
    LinearLayout layout;
    ImageView iv;
    Spinner spinner;
    EditText addMenuName, addPrice;
    Chip switchRun;
    RecyclerView rv;
    Button btnSave, btnComplete;
    MenuDAO menuDao = new MenuDAO(this);
    CategoryDAO dao = new CategoryDAO(this);
    MyRecyclerAdapter adapterR;
    List<MenuDTO> items;
    List<String> group;
    private boolean checkFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_add);

        layout = findViewById(R.id.layout2);
        iv = findViewById(R.id.iv);
        spinner = findViewById(R.id.spinner);
        addMenuName = findViewById(R.id.addMenuName);
        addPrice = findViewById(R.id.addPrice);
        switchRun = findViewById(R.id.switchRun);
        btnSave = findViewById(R.id.btnSave);
        btnComplete = findViewById(R.id.btnComplete);
        rv = findViewById(R.id.rv);
        //리소스 연결

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        //리사이클러뷰 레이아웃 설정

        group = dao.readCategoryFromDB();
        //상품분류 테이블(categoryTab)의 분류명을 DB에서 읽어와서 리스트로 저장
        //리스트의 제일 마지막인덱스 → 안내문구("분류를 추가~")

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
                //액티비티를 처음 실행 했을 때 스피너에 보여지는 항목 설정
                if (checkFirst) {
                    spinner.setSelection(group.size() - 1);
                    checkFirst = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spinner.setSelection(group.size() - 1);
            }
        });

        //리사이클러뷰와 어댑터
        adapterR = new MyRecyclerAdapter(MenuAddActivity.this, items, "delete");
        rv.setAdapter(adapterR);
        items = new ArrayList<>();  //리사이클러뷰에서 보이는 임시저장 목록

        switchRun.setChecked(true);
        switchRun.setText("판매중");
        //초기설정 : "판매중" 상태 & 체크표시 true
        switchRun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchRun.isChecked()) {
                    switchRun.setText("판매중");
                } else {
                    switchRun.setText("임시중단");
                }
            }
        });

        switchRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(addMenuName.getText()) || !TextUtils.isEmpty(addPrice.getText())) {
                    hidekeyboard();
                }
            }
        });

        //상품명 입력란에 입력필터 설정
        addMenuName.setFilters(new InputFilter[]{new InputFilter() {
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            //리사이클러뷰에 임시저장 목로으로 데이터(리스트) 전달
            @Override
            public void onClick(View v) {
                String category = spinner.getSelectedItem().toString();
                String menuName = addMenuName.getText().toString();
                int price = Integer.parseInt(addPrice.getText().toString().equals("") ? "0" : addPrice.getText().toString());
                int run = (switchRun.getText() == "판매중" ? 1 : 0);

                try {
                    if (category.equals("분류를 선택하세요.") || category.equals("상품분류를 먼저 등록해주세요.")) {
                        Toast.makeText(MenuAddActivity.this, "상품정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(addMenuName.getText()) || category.equals("")) {
                        Toast.makeText(MenuAddActivity.this, "상품정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        addMenuName.requestFocus();
                        return;
                    }

                    if (menuDao.checkName(menuName).equals("isUsed")) {
                        Toast.makeText(MenuAddActivity.this, "이미 사용 중인 상품명이 있습니다.", Toast.LENGTH_SHORT).show();
                        addMenuName.requestFocus();
                        return;
                    } else {
                        adapterR.items.add(new MenuDTO(category, menuName, price, run));
                        //목록(items)에 신규 데이터셋 추가
                        adapterR.notifyDataSetChanged();
                        Toast.makeText(MenuAddActivity.this, "임시저장 되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    //각 항목 초기화
                    spinner.setSelection(group.size() - 1);
                    addMenuName.setText("");
                    addPrice.setText("");
                } catch (Exception e) {
                    e.printStackTrace();

                    if (TextUtils.isEmpty(addMenuName.getText())) {
                        addMenuName.requestFocus();
                    } else {
                        addPrice.requestFocus();
                    }
                    //공란인 곳으로 커서 이동
                    Toast.makeText(MenuAddActivity.this, "상품정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (items.size() == 0) {
                        Toast.makeText(MenuAddActivity.this, "새로 추가한 메뉴가 없습니다.\n임시저장 목록을 확인해주세요.", Toast.LENGTH_SHORT).show();
                        if (TextUtils.isEmpty(addMenuName.getText())) {
                            addMenuName.requestFocus();
                        } else {
                            addPrice.requestFocus();
                        }
                    } else {
                        for (int i = 0; i < items.size(); i++) {
                            menuDao.insertDB(items.get(i));
                            //반복문으로 리사이클러뷰에 있는 임시저장목록(items) 전체 insert쿼리 실행
                        }
                        Toast.makeText(MenuAddActivity.this, items.size() + "개 품목이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();   //액티비티 종료
                        Intent intent = new Intent(MenuAddActivity.this, MenuBoardActivity.class);
                        startActivity(intent);
                        //액티비티 종료 → MenuBoard로 돌아감(액티비티 재시작. 화면reset)
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //아이콘 짧게 클릭, 이전화면으로 이동(취소기능)
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAddActivity.this, MenuBoardActivity.class);
                finish();
                startActivity(intent);
                Toast.makeText(MenuAddActivity.this, "이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
            }
        });

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hidekeyboard();
                return false;
            }
        });

        //아이콘 롱클릭, 상품분류 추가-삭제 액티비티를 다이얼로그 형태로 연결
        iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(MenuAddActivity.this, CategoryPopupActivity.class);
                startActivity(intent);
                return true;
                //동일한 view에 OnClickListener 와 OnLongClickListener 를 모두 정의 → 각각에 걸린 이벤트가 같이 실행되는 것을 방지하기 위해 true로 반환
            }
        });
    }//onCreate의 끝

    @Override
    protected void onResume() {
        super.onResume();
        adapterR = new MyRecyclerAdapter(MenuAddActivity.this, items, "delete");
        rv.setAdapter(adapterR);
        //임시저장한 상품목록 리스트(items)의 데이터셋에 변화가 있을 경우(임시저장 or 삭제한 경우) → 리사이클러뷰 화면 새로고침
    }

    public void hidekeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(rv.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}