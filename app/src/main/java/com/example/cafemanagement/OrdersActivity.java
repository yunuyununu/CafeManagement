package com.example.cafemanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.WindowDecorActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    EditText txtnumSearch;
    EditText txtdateSearch;
    Button btnnum;
    Button btndate;
    RecyclerView rv;
    ImageView iv2;
    OrdersDAO dao;
    RecyclerView.Adapter AllAdapter;
    RecyclerView.Adapter OrderAdapter;
    RecyclerView.Adapter DateAdapter;
    List<OrdersDTO> items;
    List<OrdersDTO> items1;
    List<OrdersDTO> items2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        btnnum = findViewById(R.id.btnnum);
        btndate = findViewById(R.id.btndate);
        txtnumSearch = findViewById(R.id.txtnumSearch);
        txtdateSearch = findViewById(R.id.txtdateSearch);
        rv = findViewById(R.id.rv);
        iv2 = findViewById(R.id.iv2);
        rv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        dao = new OrdersDAO(this);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rv.getContext(), new LinearLayoutManager(this).getOrientation());
//        rv.addItemDecoration(dividerItemDecoration); //경계선설정


        //커피콩 아이콘
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items = dao.list();
                AllAdapter = new AllAdapter();
                rv.setAdapter(AllAdapter); //리사이클러뷰에 전체어댑터
            }
        });

        //주문번호 검색버튼
        btnnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String order_num = txtnumSearch.getText().toString();

                if (order_num.length() != 3) {
                    Toast.makeText(OrdersActivity.this, "주문번호는 3자리입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    items1 = dao.list1(order_num);
                    if (items1.size() == 0) {
                        Toast.makeText(OrdersActivity.this, "없는 주문번호입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        OrderAdapter = new OrderAdapter();
                        rv.setAdapter(OrderAdapter); // 리사이클러뷰에 주문번호어댑터
                        txtnumSearch.setText(null); // 검색 후 검색칸 초기화
                    }
                }
                hidekeyboard();
            }
        });

        //날짜검색버튼
        btndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String order_date = txtdateSearch.getText().toString();
                SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd");
                dateForm.setLenient(false); //엄격한 모드로 설정

                try {
                    Date parseDate = dateForm.parse(order_date);
                    items2 = dao.list2(order_date);
                    if (items2.size() == 0) {
                        Toast.makeText(OrdersActivity.this, "해당날짜의 주문내역은 없습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        DateAdapter = new DateAdapter();
                        rv.setAdapter(DateAdapter); // 리사이클러뷰에 날짜어댑터
                        txtdateSearch.setText(null); // 검색 후 검색칸 초기화
                    }
                } catch (ParseException e) {
                    Toast.makeText(OrdersActivity.this, "형식에 맞지 않습니다.", Toast.LENGTH_SHORT).show();
                }
                hidekeyboard();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        items = dao.list();
//        Log.i("test", "상품목록:" + items);
        AllAdapter = new AllAdapter(); //아답터 생성
        rv.setAdapter(AllAdapter); //리사이클러뷰에 아답터 연결
    }
    // 전체리스트 어댑터
    class AllAdapter extends RecyclerView.Adapter<AllAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
            return new ViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OrdersDTO dto = items.get(position);
            holder.txtDate.setText(dto.getOrder_date());
            holder.txtOrdernum.setText(String.valueOf(dto.getOrder_num())+"번");
            holder.txtMenuno.setText(dto.getMenu_name());
            holder.txtAmount.setText(String.valueOf(dto.getAmount())+"개");
            holder.txtTotal.setText(numberFormat(String.valueOf(dto.getTotal_price()))+"원");
        }

        @Override
        public int getItemCount() {
//            Log.i("test","자료개수:" + items.size() + "");
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder  {

            private TextView txtDate,txtOrdernum,txtMenuno,txtAmount,txtTotal;

            public ViewHolder(View view) {
                super(view);

                txtDate = view.findViewById(R.id.txtDate);
                txtOrdernum = view.findViewById(R.id.txtOrdernum);
                txtMenuno = view.findViewById(R.id.txtMenuno);
                txtAmount = view.findViewById(R.id.txtAmount);
                txtTotal = view.findViewById(R.id.txtTotal);
            }
        }
    }
    //주문번호검색 어댑터
    class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row,parent,false);
            return new OrderViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            OrdersDTO dto = items1.get(position);
            holder.txtDate.setText(dto.getOrder_date());
            holder.txtOrdernum.setText(String.valueOf(dto.getOrder_num())+"번");
            holder.txtMenuno.setText(dto.getMenu_name());
            holder.txtAmount.setText(String.valueOf(dto.getAmount())+"개");
            holder.txtTotal.setText(String.valueOf(dto.getTotal_price())+"원");
        }

        @Override
        public int getItemCount() {
            return items1.size();
        }
        public class OrderViewHolder extends RecyclerView.ViewHolder {
            private TextView txtDate,txtOrdernum,txtMenuno,txtAmount,txtTotal;

            public OrderViewHolder(View view1) {
                super(view1);

                txtDate = view1.findViewById(R.id.txtDate);
                txtOrdernum = view1.findViewById(R.id.txtOrdernum);
                txtMenuno = view1.findViewById(R.id.txtMenuno);
                txtAmount = view1.findViewById(R.id.txtAmount);
                txtTotal = view1.findViewById(R.id.txtTotal);
            }
        }
    }
    // 날짜검색 어댑터
    class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

        @NonNull
        @Override
        public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row, parent, false);
            return new DateViewHolder(rowItem);
        }

        @Override
        public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
            OrdersDTO dto = items2.get(position);
            holder.txtDate.setText(dto.getOrder_date());
            holder.txtOrdernum.setText(String.valueOf(dto.getOrder_num()) + "번");
            holder.txtMenuno.setText(dto.getMenu_name());
            holder.txtAmount.setText(String.valueOf(dto.getAmount()) + "개");
            holder.txtTotal.setText(String.valueOf(dto.getTotal_price()) + "원");

        }

        @Override
        public int getItemCount() {
            return items2.size();
        }

        public class DateViewHolder extends RecyclerView.ViewHolder {
            private TextView txtDate, txtOrdernum, txtMenuno, txtAmount, txtTotal;


            public DateViewHolder(View view2) {
                super(view2);

                txtDate = view2.findViewById(R.id.txtDate);
                txtOrdernum = view2.findViewById(R.id.txtOrdernum);
                txtMenuno = view2.findViewById(R.id.txtMenuno);
                txtAmount = view2.findViewById(R.id.txtAmount);
                txtTotal = view2.findViewById(R.id.txtTotal);
            }
        }
    }
    // 키보드 자동 내리기
    void hidekeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(rv.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
    // 금액단위 콤마설정
    public String numberFormat(String str) {
        if (str.length() == 0) {
            return "";
        }
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }
}