package com.example.cafemanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class EditInventory extends AppCompatActivity implements View.OnClickListener {
    EditText editProduct_id, editProduct_name, editPrice, editAmount, editPurchase_date;
    Button btnUpdate, btnBack, btnDelete;
    ImageButton btnDate;
    Spinner editCategorySpinner;
    String[] category = {"카테고리를 선택해주세요", "음료", "디저트", "비품"};
    InventoryDAO dao;
    InventoryDTO dto;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_inventory);
        //기존에 선택된 스피너 데이터값 가져오기
        String selectedCt = getIntent().getStringExtra("selectedCategory");
        editProduct_id = findViewById(R.id.editId);
        editCategorySpinner = (Spinner) findViewById(R.id.spinnerCategory);
        editProduct_name = findViewById(R.id.editName);
        editPrice = findViewById(R.id.editPrice);
        editAmount = findViewById(R.id.editAmount);
        editPurchase_date = findViewById(R.id.editPurchaseDate);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);
        btnDelete = findViewById(R.id.btnDelete);

        /* 스피너 어댑터 */
        ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, category);
        sAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        editCategorySpinner.setAdapter(sAdapter);

        dao = new InventoryDAO(this);

        Intent intent = getIntent();
        dto = (InventoryDTO) intent.getSerializableExtra("dto");
        editProduct_id.setText(dto.getProduct_id());
        editCategorySpinner.setSelection(sAdapter.getPosition(selectedCt));
        editProduct_name.setText(dto.getProduct_name());
        editProduct_name.setFilters(new InputFilter[]{filterKor});
        editPrice.setText(Integer.toString(dto.getPrice()));
        editAmount.setText(dto.getAmount() + "");
        editPurchase_date.setText(dto.getPurchase_date());
        btnUpdate.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        editCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editCategorySpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(EditInventory.this, "상품분류를 선택해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        //datePicker
        btnDate = findViewById(R.id.btnDate);
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

                datePickerDialog = new DatePickerDialog(EditInventory.this, R.style.datePickerStyle, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        //1월은 0부터 시작하므로 +1
                        month = month + 1;
                        String date = year + "-" + getMonth(month) + "-" + getDay(day);
                        editPurchase_date.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    //커스텀 다이얼로그
    public void custom_dialog() {
        Intent intent = new Intent(EditInventory.this, InventoryManagement.class);
        View dialog = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(EditInventory.this);
        ab.setView(dialog);

        TextView textContent = dialog.findViewById(R.id.textContent);
        textContent.setText("선택하신 재고를 삭제하시겠 습니까?");

        final AlertDialog alertDialog = ab.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        Button btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dao.delete(dto.getProduct_id());
                Toast.makeText(EditInventory.this, dto.getProduct_name() + " 이/가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });

        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(EditInventory.this, InventoryManagement.class);

        if (v.getId() == R.id.btnUpdate) {
            String product_id = editProduct_id.getText().toString();
            String product_category = editCategorySpinner.getSelectedItem().toString();
            String product_name = editProduct_name.getText().toString();
            String priceS = editPrice.getText().toString();
            String amountS = editAmount.getText().toString();
            String purchase_date = editPurchase_date.getText().toString();
            int price;
            int amount;

            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                df.setLenient(false);
                df.parse(purchase_date);
            } catch (Exception e) {
                Toast.makeText(EditInventory.this, "날짜형식이 맞지않습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(product_id)) {
                Toast.makeText(EditInventory.this, "상품코드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            } else if (product_category.equals("카테고리를 선택해주세요")) {
                Toast.makeText(EditInventory.this, "상품분류를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(product_name)) {
                Toast.makeText(EditInventory.this, "상품명을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(priceS)) {
                Toast.makeText(EditInventory.this, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(amountS)) {
                Toast.makeText(EditInventory.this, "수량을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                price = Integer.parseInt(priceS);
                amount = Integer.parseInt(amountS);

                dto.setProduct_id(product_id);
                dto.setProduct_category(product_category);
                dto.setProduct_name(product_name);
                dto.setPrice(price);
                dto.setAmount(amount);
                dto.setPurchase_date(purchase_date);

                dao.update(dto);
            }

            Toast.makeText(this, dto.getProduct_name() + " 이/가 수정되었습니다.", Toast.LENGTH_SHORT).show();
            //현재 화면 종료
            startActivity(intent);
        } else if (v.getId() == R.id.btnDelete) {
            custom_dialog();
        } else if (v.getId() == R.id.btnBack) {
            startActivity(intent);
        }
    }

    //한글, 공백만 입력가능하도록 제한
    protected InputFilter filterKor = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[ㄱ-ㅣ가-힣\\s]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return source;
        }
    };
    
    //천단위 구분표시
    public String numberFormat(String str) {
        if (str.length() == 0) {
            return "";
        }
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }
}