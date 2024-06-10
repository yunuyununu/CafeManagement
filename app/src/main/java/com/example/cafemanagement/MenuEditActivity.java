package com.example.cafemanagement;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import java.util.regex.Pattern;

public class MenuEditActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout layout;
    ImageView iv;
    EditText editCategory, editMenuId, editMenuName, editPrice;
    Chip switchRun;
    Button btnUpdate, btnDelete;
    MenuDAO menuDao;
    MenuDTO menuDto;
    int run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_edit);

        layout = findViewById(R.id.layout2);
        iv = findViewById(R.id.iv);
        editCategory = findViewById(R.id.editCategory);
        editMenuId = findViewById(R.id.editMenuId);
        editMenuName = findViewById(R.id.editMenuName);
        editPrice = findViewById(R.id.editPrice);
        switchRun = findViewById(R.id.switchRun);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        editMenuName.setFilters(new InputFilter[]{filterAlpha});
        btnUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!TextUtils.isEmpty(editMenuName.getText()) || !TextUtils.isEmpty(editPrice.getText())) {
                    //edittext에 입력된 값이 있을 경우 초기화
                    hidekeyboard();
                    editMenuName.requestFocus();
                }
                return false;
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuEditActivity.this, "이전 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        switchRun.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switchRun.isChecked()) {
                    run = 1;
                    switchRun.setText("판매중");
                } else {
                    run = 0;
                    switchRun.setText("임시중단");
                }
            }
        });

        menuDao = new MenuDAO(this);
        Intent intent = getIntent();
        menuDto = (MenuDTO) intent.getSerializableExtra("dto");
        //인텐트와 같이 사용자가 선택한 상품의 상품정보를 전달

        //상품분류, 상품코드 변경 못하게. 그외 기존내용을 화면에 세팅
        assert menuDto != null;
        editCategory.setText(menuDao.findCategory(menuDto));
        editMenuId.setText(menuDto.getMenuId());
        editMenuName.setText(menuDto.getMenuName());
        editPrice.setText(Integer.toString(menuDto.getPrice()));
        if (menuDto.getRun() == 1) {
            //1 : 판매중인 상품
            switchRun.setChecked(true);
        } else {
            //0 : 임시중단인 상품
            switchRun.setChecked(false);
        }
    }//onCreate의 끝

    //버튼 클릭 이벤트
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            try {
                String category = editCategory.getText().toString();    //기존정보
                String menuId = editMenuId.getText().toString();    //기존정보
                String menuName = editMenuName.getText().toString();    //입력내용
                if (TextUtils.isEmpty(editMenuName.getText())) {
                    //edittext가 공란일 때 처리
                    Toast.makeText(MenuEditActivity.this, "상품정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    editMenuName.requestFocus();
                    return;
                }
                int price = Integer.parseInt(editPrice.getText().toString());   //입력내용
                int run = (switchRun.getText() == "판매중" ? 1 : 0);   //입력내용

                menuDto.setCategory(category);
                menuDto.setMenuId(menuId);
                menuDto.setMenuName(menuName);
                menuDto.setPrice(price);
                menuDto.setRun(run);

                //dto에 담아 쿼리(메소드) 실행 → DB반영
                if (menuDao.update(menuDto).equals("succeed")) {
                    //DB에 정상적으로 업데이트 실행 완료 된 경우
                    Toast.makeText(MenuEditActivity.this, "상품정보가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    finish(); //액티비티 종료
                } else {
                    //예외 발생 시 사용자에게 알림
                    Toast.makeText(MenuEditActivity.this, "상품정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    editMenuName.requestFocus();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MenuEditActivity.this, "상품정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.btnDelete) {
            customDialog();
        }
    }

    public void customDialog() {
        View dialog = getLayoutInflater().inflate(R.layout.custom_dialog, null);
        androidx.appcompat.app.AlertDialog.Builder ab = new androidx.appcompat.app.AlertDialog.Builder(MenuEditActivity.this);
        ab.setView(dialog);

        TextView textContent = dialog.findViewById(R.id.textContent);
        textContent.setText("선택하신 메뉴를 삭제하시겠습니까?");

        final androidx.appcompat.app.AlertDialog alertDialog = ab.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //다이얼로그 바깥 배경을 투명하게
        alertDialog.show();

        Button btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDao.delete(menuDto);
                //메소드 호출 → 세팅된 상품코드(menuId)로 삭제쿼리 실행
                Toast.makeText(MenuEditActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();

                Intent intent = new Intent(MenuEditActivity.this, MenuBoardActivity.class);
                startActivity(intent);
                //화면이동
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

    public void hidekeyboard() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(layout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    protected InputFilter filterAlpha = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Zㄱ-ㅎ가-힣\\s]+$");
            //상품명에 문자(한글, 영어)만 입력되도록 제한. 공백허용
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
}