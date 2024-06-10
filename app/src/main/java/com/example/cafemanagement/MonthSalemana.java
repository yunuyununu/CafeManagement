package com.example.cafemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MonthSalemana extends AppCompatActivity {

    Button btnSale;
    ImageButton btnExcel;
    EditText txtDateSearch;
    TextView txtSale;
    RecyclerView rv1;
    RecyclerView.Adapter MonAdapter;
//   @ static List<SaleDTO> items6;
    static List<OrdersDTO> items6;
    OrdersDAO dao;
    int monresult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_salemana);

        btnSale = findViewById(R.id.btnSale);
        btnExcel = findViewById(R.id.btnExcel);
        txtDateSearch = findViewById(R.id.txtDateSearch);
        txtSale = findViewById(R.id.txtSale);
        rv1 = findViewById(R.id.rv1);
        rv1.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        dao = new OrdersDAO(this);
        // txtSale.addTextChangedListener(watcher); //콤마설정
        //경계선 설정
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv1.getContext(), new LinearLayoutManager(this).getOrientation());
        rv1.addItemDecoration(dividerItemDecoration);

        //엑셀아이콘버튼
        btnExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(txtDateSearch.getText())) {
                    Toast.makeText(MonthSalemana.this, "날짜입력 후 조회하세요.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = null;
                    intent = new Intent(MonthSalemana.this, ExcelCustomDialog.class);
                    startActivity(intent);
                }
            }
        });

        btnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String order_date = txtDateSearch.getText().toString();
                SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM");
                dateForm.setLenient(false); //엄격한 모드로 설정
                try {
                    Date parseDate = dateForm.parse(order_date);
                    monresult = dao.mon_sale(order_date);
                    if (monresult == 0) {
                        Toast.makeText(MonthSalemana.this, "해당날짜는 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        items6 = dao.list6(order_date);
                        txtSale.setText(numberFormat(String.valueOf(monresult)));
                        MonAdapter = new MonAdapter();
                        rv1.setAdapter(MonAdapter);
                    }
                } catch (ParseException e) {
                    Toast.makeText(MonthSalemana.this, "형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                hidekeyboard();
            }
        });
    }
    // 월별 날짜검색 어댑터

    class MonAdapter extends RecyclerView.Adapter<MonthSalemana.MonAdapter.MonViewHolder> {

        @NonNull
        @Override
        public MonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.monsale_row, parent, false);
            return new MonViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull MonViewHolder holder, int position) {
//           @ SaleDTO items = items6.get(position);
            OrdersDTO items = items6.get(position);

            holder.txtViewDatem.setText(items.getOrder_date());
            holder.txtViewMenum.setText(items.getMenu_name());
            holder.txtViewAmountm.setText(String.valueOf(items.getMonth_amount()));
            holder.txtViewSalem.setText(numberFormat(String.valueOf(items.getMonth_sales())));
        }

        @Override
        public int getItemCount() {
            return items6.size();
        }

        public class MonViewHolder extends RecyclerView.ViewHolder {
            private TextView txtViewDatem, txtViewMenum, txtViewAmountm, txtViewSalem;

            public MonViewHolder(View view5) {
                super(view5);
                txtViewDatem = view5.findViewById(R.id.txtViewDatem);
                txtViewMenum = view5.findViewById(R.id.txtViewMenum);
                txtViewAmountm = view5.findViewById(R.id.txtViewAmountm);
                txtViewSalem = view5.findViewById(R.id.txtViewSalem);
            }
        }
    }
    // 키보드 자동내리기
    void hidekeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(rv1.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    public String numberFormat(String str) { //금액단위 콤마설정
        if (str.length() == 0) {
            return "";
        }
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }
}