package com.example.cafemanagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelCustomDialog extends AppCompatActivity {

    ProgressDialog pd;
    String path;
//  @  ArrayList<SaleDTO> items6;
    ArrayList<OrdersDTO> items6;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        path = getExternalFilesDir(null).getAbsolutePath(); //외부메모리
        // storage-emulated-0-Android-data-com.cafemanagement-files
        custom_dialog();
    }

    public void custom_dialog() {
        View dialog = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(ExcelCustomDialog.this);
        ab.setView(dialog);

        TextView textContent = dialog.findViewById(R.id.textContent);
        textContent.setText("자료를 엑셀 파일로 저장하시겠습니까?");


        final androidx.appcompat.app.AlertDialog alertDialog = ab.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        // yes 누르면 로그인 성공 다이어로그 창이 띄워지는 동시에 다음화면으로 이동
        Button btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backup();
            }
        });
        // no 버튼 누르면 아이디와 비밀번호 입력값 자동 삭제
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void backup() {
        pd = ProgressDialog.show(this,"작업중","자료를 엑셀로 저장하고 있습니다. 잠시만 기다려 주세요.");
        // 파일 저장될 동안 진행바
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare(); //스레드 실행하는 기능
                copy();
                handler.sendEmptyMessage(0); // 화면 고치는 작업
                Looper.loop();
            }
        });
        thread.start();
    }
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            pd.dismiss(); //프로그레스 종료
            Toast.makeText(ExcelCustomDialog.this, "작업이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    };
    public void copy() {
        WritableWorkbook workbook = null; // 엑셀파일
        WritableSheet sheet = null; // 시트
        String fileName = path + "/exceltest.xls";
        try {
            File file = new File(fileName); //java.io.*
            workbook = Workbook.createWorkbook(file);
            workbook.createSheet("Month_Sales",0); // 시트이름,인덱스
            sheet = workbook.getSheet(0); //첫번째시트
//          @  List<SaleDTO> list = MonthSalemana.items6;
            List<OrdersDTO> list = MonthSalemana.items6;
//            Log.i("test", "상품목록:" + items6);
            Label label = null;
            WritableCellFormat cf = new WritableCellFormat(); // 셀의형식
            cf.setBorder(Border.ALL, BorderLineStyle.THIN);

            label = new Label(0,0,"상품별 매출현황",cf);
            sheet.addCell(label);
            label = new Label(0,1,"날짜",cf); // 열과 행 - Columns and rows
            sheet.addCell(label); // 시트에 셀 추가
            label = new Label(1,1,"상품명",cf);
            sheet.addCell(label);
            label = new Label(2,1,"수량",cf);
            sheet.addCell(label);
            label = new Label(3,1,"매출",cf);
            sheet.addCell(label);


            for(int i=1;i< list.size();i++) {

//               @ SaleDTO dto = list.get(i);
                OrdersDTO dto = list.get(i);
                label = new Label(0,i+1, dto.getOrder_date(),cf);
                sheet.addCell(label);
                label = new Label(1,i+1, dto.getMenu_name(),cf);
                sheet.addCell(label);
                label = new Label(2,i+1,String.valueOf(dto.getMonth_amount()),cf);
                sheet.addCell(label);
                label = new Label(3,i+1,String.valueOf(dto.getMonth_sales()),cf);
                sheet.addCell(label);

            }
            workbook.write(); // 엑셀파일 저장
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (workbook != null)
                    workbook.close(); // 리소스 정리
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}