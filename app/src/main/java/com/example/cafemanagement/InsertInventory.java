package com.example.cafemanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class InsertInventory extends AppCompatActivity {
    ConstraintLayout addLayout;
    EditText editId, editName, editPrice, editAmount, editPurchaseDate;
    Button btnInsert, btnBack;
    ImageButton btnDate;
    private Spinner spinnerCategory;
    String[] category = {"카테고리를 선택해주세요", "음료", "디저트", "비품"};
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    long date = System.currentTimeMillis();
    String today = df.format(date);
    InventoryDAO dao;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_inventory);

        //위젯 연결작업
        btnInsert = findViewById(R.id.btnInsert);
        btnBack = findViewById(R.id.btnBack);
        editId = findViewById(R.id.editId);
        editId.setFilters(new InputFilter[]{filterCode});
        editName = findViewById(R.id.editName);
        editName.setFilters(new InputFilter[]{filterKor});
        editPrice = findViewById(R.id.editPrice);
        editAmount = findViewById(R.id.editAmount);
        editPurchaseDate = findViewById(R.id.editPurchaseDate);
        //등록 구입일자 오늘 날짜로 기본 셋팅
        editPurchaseDate.setText(today);
        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        addLayout = findViewById(R.id.addLayout);
        btnDate = findViewById(R.id.btnDate);

        //DatePicker
        btnDate.setOnClickListener(new View.OnClickListener() {
            //월 데이터 두자리수 맞추기
            public String getMonth(int month) {
                if (month > 0 && month < 10) {
                    return "0" + String.valueOf(month);
                } else {
                    return String.valueOf(month);
                }
            }

            //일 데이터 두자리수 맞추기
            public String getDay(int day) {
                if (day > 0 && day < 10) {
                    return "0" + String.valueOf(day);
                } else {
                    return String.valueOf(day);
                }
            }

            @Override
            public void onClick(View v) {
                //오늘 날짜 변수에 담기
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                datePickerDialog = new DatePickerDialog(InsertInventory.this, R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        //1월은 0부터 시작하므로 +1
                        month = month + 1;
                        String calDate = year + "-" + getMonth(month) + "-" + getDay(day);
                        editPurchaseDate.setText(calDate);
                    }
                }, year, month, day);

                datePickerDialog.show();
            }

        });

        /* 스피너 어댑터 */
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
        sAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(sAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        dao = new InventoryDAO(this);

        btnInsert.setOnClickListener(new View.OnClickListener() {
            Intent intent = new Intent(InsertInventory.this, InventoryManagement.class);
            Pattern pattern = Pattern.compile("^\\d{4}\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])$");

            @Override
            public void onClick(View v) {
                String product_id = editId.getText().toString();
                String product_category = spinnerCategory.getSelectedItem().toString();
                String product_name = editName.getText().toString();
                String priceS = editPrice.getText().toString();
                String amountS = editAmount.getText().toString();
                String purchase_date = editPurchaseDate.getText().toString();
                int price;
                int amount;

                try {
                    //날짜 형식 확인
                    df.setLenient(false);
                    df.parse(purchase_date);
                } catch (Exception e) {
                    Toast.makeText(InsertInventory.this, "날짜형식이 맞지않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(product_id)) {
                    Toast.makeText(InsertInventory.this, "상품코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (product_category.equals("카테고리를 선택해주세요")) {
                    Toast.makeText(InsertInventory.this, "상품분류를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(product_name)) {
                    Toast.makeText(InsertInventory.this, "상품명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(priceS)) {
                    Toast.makeText(InsertInventory.this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(amountS)) {
                    Toast.makeText(InsertInventory.this, "수량을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    price = Integer.parseInt(priceS);
                    amount = Integer.parseInt(amountS);

                    InventoryDTO dto = new InventoryDTO(product_id, product_category, product_name, price, amount, purchase_date);

                    dao.insert(dto);
                }
                Toast.makeText(InsertInventory.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertInventory.this, InventoryManagement.class);
                startActivity(intent);
            }
        });
    }
    
    //영어,숫자만 입력가능하도록 제한
    protected InputFilter filterCode = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //한글, 공백만 입력가능하도록 제한
    protected InputFilter filterKor = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[ㄱ-ㅣ가-힣\\s]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //키보드 자동
    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(addLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
