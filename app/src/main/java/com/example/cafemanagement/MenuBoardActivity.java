package com.example.cafemanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MenuBoardActivity extends AppCompatActivity {
    Context context;
    LinearLayout layout;
    ImageView iv;
    AutoCompleteTextView auto;
    Button btnAdd, btnHome, btnConfirm;
    RecyclerView rv;
    MenuDAO menuDao;
    CategoryDAO dao;
    List<MenuDTO> items;
    //  String[] group = {"all", "coffee", "beverage", "tea", "food"};    → 상품분류 테이블을 DB에서 유동적으로 가져올 수 있도록 최종변경
    List<String> group;
    MyRecyclerAdapter adapterR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_board);

        context = this;
        layout = findViewById(R.id.layout2);
        iv = findViewById(R.id.iv);
        auto = findViewById(R.id.auto);
        btnAdd = findViewById(R.id.btnAdd);
        btnHome = findViewById(R.id.btnHome);
        btnConfirm = findViewById(R.id.btnConfirm);
        rv = findViewById(R.id.rv);
        //리소스 연결

        menuDao = new MenuDAO(this);
        dao = new CategoryDAO(this);
        //객체 시작주소값 연결

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(), DividerItemDecoration.VERTICAL));
        //리사이클러뷰의 레이아웃 설정
        // adapterR = new MyRecyclerAdapter(MenuBoardActivity.this, items, "edit");
        // rv.setAdapter(adapterR);
        //커스터마이징한 리사이클러어댑터 연결

        items = menuDao.list();
        //리사이클러뷰에 담을 리스트 생성, initialize → menuView의 모든 목록 띄우기

        group = dao.readCategoryFromDB();
        //상품분류 테이블(categoryTab)의 분류명을 DB에서 가져와 리스트로 저장

        //자동완성텍스트에 연결할 어댑터 셋팅
        ArrayAdapter<String> adapterA = new ArrayAdapter<>(MenuBoardActivity.this, android.R.layout.simple_spinner_dropdown_item, group);
        auto.setAdapter(adapterA);
        //AutoCompleteTextView 위젯과 어댑터 연결
        auto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hidekeyboard();
                onResume();
                //분류명으로 검색해서 자동완성으로 보여지는 키워드를 선택했을 때, onResume 에서 list()메소드 호출 → menuView 에서 select한 결과값을 리스트를 가리킴
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = auto.getText().toString();
                if (TextUtils.isEmpty(auto.getText())) {
                    Toast.makeText(MenuBoardActivity.this, "상품분류 또는 상품명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    //검색어 입력란이 공란일 때 작동하지 않게. 안내메세지 토스트
                } else {
                    items = menuDao.list(keyword);
                    //분류명으로 검색해서 자동완성으로 보여지는 키워드를 선택했을 때, list()메소드 호출 → menuView 에서 select한 결과값을 리스트를 가리킴

                    adapterR = new MyRecyclerAdapter(MenuBoardActivity.this, items, "edit");
                    adapterR.notifyDataSetChanged();
                    rv.setAdapter(adapterR);
                    hidekeyboard();
                }
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuBoardActivity.this, "메인화면으로 들아갑니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MenuBoardActivity.this, ManagerActivity.class);
                finish();
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuBoardActivity.this, MenuAddActivity.class);
                Toast.makeText(MenuBoardActivity.this, "신규등록을 위해 상품정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });
        //상품등록 화면으로 이동

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hidekeyboard();
                return false;
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto.setText("");
                auto.requestFocus();
                hidekeyboard();
//                items = menuDao.list();
//                adapterR = new MyRecyclerAdapter(MenuBoardActivity.this, items, "edit");
//                rv.setAdapter(adapterR);
                onResume();
                //이미지 클릭 시 menuView 전체 리스트가 보이게 리사이클러뷰 화면 리셋
            }
        });
    }//onCreate의 끝

    protected void onResume() {
        super.onResume();
        items = menuDao.list(auto.getText().toString());
        //검색어로 입력한 텍스트 → list() 호출 → 반환된 목록(items)을 가리킴

        adapterR = new MyRecyclerAdapter(MenuBoardActivity.this, items, "edit");
        //"edit" → 커스텀어댑터 클래스에서 뷰에 연결된 setOnClickListener를 통해 동작하게되는 옵션값 설정(메뉴수정화면으로 연결)
        rv.setAdapter(adapterR);
    }

    public void hidekeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(rv.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}