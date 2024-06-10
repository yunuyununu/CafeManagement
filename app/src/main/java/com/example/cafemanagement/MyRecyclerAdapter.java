package com.example.cafemanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    //MenuBoard & MenuAdd 에서 공통으로 활용하는 클래스
    Context context;
    List<MenuDTO> items;
    static String option = "";

    //생성자
    public MyRecyclerAdapter(Context context, List<MenuDTO> menus, String option) {
        this.context = context;
        this.items = menus;
        this.option = option;
        //액티비티에 따라 다른 기능을 추가히가 위해 변수 설정
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rowItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_row, parent, false);
        return new ViewHolder(rowItem);
        //리사이클러뷰 안에 보여질 텍스트뷰 리소스파일 연결
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuDTO menuDto = items.get(position);
        holder.tvCategory.setText(menuDto.getCategory());
        holder.tvMenuName.setText(menuDto.getMenuName());
        holder.tvPrice.setText(menuDto.getPrice() + "");
        if (menuDto.getRun() == 1) {
            holder.tvRun.setText("○");
        } else {
            holder.tvRun.setText("Ⅹ");
        }
        //각 텍스트뷰에 담기는 내용들
    }

    @Override
    public int getItemCount() {
        return (items != null ? items.size() : 0);
        //NullPointerException 처리
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategory, tvMenuName, tvPrice, tvRun;

        // 상품명을 클릭했을 때 발생하는 커스텀다이얼로그 선안
        public void customDialog() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialog = inflater.inflate(R.layout.custom_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(dialog);
            //다이얼로그 레이아웃 설정

            TextView textContent = dialog.findViewById(R.id.textContent);
            Button btnYes = dialog.findViewById(R.id.btnYes);
            Button btnNo = dialog.findViewById(R.id.btnNo);
            //각 리소스 연결

            final AlertDialog alertDialog = builder.create();
            //다이얼로그 생성
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //다이얼로그 바깥 배경을 투명하게
            alertDialog.show();
            //다이얼로그 띄우기

            //어댑터가 사용되는 액티비티에 따라 각각 다른 기능 구현(option)
            switch (MyRecyclerAdapter.option) {
                case "edit":    //MenuBoard 에서
                    textContent.setText("선택하신 메뉴를 수정하시겠습니까?");

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, MenuEditActivity.class);
                            //화면전환: MenuEdit으로 연결되도록 intent 선언
                            MenuDTO menuDto = items.get(getLayoutPosition());
                            //전달할 데이터, 리스트.get(인덱스)
                            intent.putExtra("dto", menuDto);
                            //선택한 데이터를 인텐트에 담아 같이 전달

                            context.startActivity(intent);
                            //화면전환
                            alertDialog.dismiss();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    break;

                case "delete":    //MenuAdd 에서
                    textContent.setText("선택하신 메뉴를 삭제하시겠습니까?");

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            items.remove(getLayoutPosition());
                            //리사이클러뷰에 보여지는 목록 중 사용자가 클릭한 아이템을 제거
                            notifyItemRemoved(getLayoutPosition());
                            notifyItemRangeChanged(getLayoutPosition(), items.size());
                            //데이터 삭제 & 리스트사이즈 변경을 어댑터에 알림 → 액티비티 안의 onResume에서 변경사항 반영
                            alertDialog.dismiss();
                        }
                    });

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    break;
            }
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvMenuName = itemView.findViewById(R.id.tvMenuName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRun = itemView.findViewById(R.id.tvRun);
            //뷰홀더 안에 개별 항목id 연결

            tvMenuName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customDialog();
                    //각 행의 뷰홀더 중 상품명을 담는 부분을 클릭했을 때 다이얼로그 콜
                }
            });
        }
    }
}