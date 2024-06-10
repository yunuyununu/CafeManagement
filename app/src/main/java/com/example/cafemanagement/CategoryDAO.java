package com.example.cafemanagement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    Context context;
    SQLiteDatabase db;

    //생성자
    public CategoryDAO(Context context) {
        this.context = context;
    }

    //상품분류 테이블 생성
    public SQLiteDatabase dbConn() {
        db = context.openOrCreateDatabase("management.db", Context.MODE_PRIVATE, null);
        String createCT = "CREATE TABLE if not exists categoryTab(category_id TEXT(2) PRIMARY KEY, category text UNIQUE)";
        db.execSQL(createCT);
        //SQL 실행 → 상품분류 테이블 생성
        return db;
    }

    //DB에 있는 분류테이블에서 분류명을 리스트로 가져옴
    public List<String> readCategoryFromDB() {
        List<String> group = new ArrayList<>(); //분류명(category)을 담을 리스트
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbConn();
            String sql = "select category from categoryTab group by category order by rowid";
            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    group.add(cursor.getString(0));
                    //order by rowid → DB에 입력한 순서대로 조회, group에 저장
                }
                group.add("분류를 선택하세요.");
                //제일 마지막인덱스에 추가 → 스피너에서 보여줄 안내문으로 활용
            } else {
                group.add("상품분류를 먼저 등록해주세요.");
                //select 결과가 0일 때, 인덱스에 추가 → 스피너에서 보여줄 안내문으로 활용
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return group;
    }

    //DB에 있는 분류테이블에서 분류코드를 가져옴
    public String readCategoryIdFromDB(String category) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String categoryId = "";

        try {
            db = dbConn();
            String selectId = "select category_id from categoryTab where category='" + category + "'";
            cursor = db.rawQuery(selectId, null);

            if (cursor.moveToNext()) {
                categoryId = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return categoryId;
    }

    //상품분류 추가
    public String addCategory(MenuDTO dto) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String result = "";
        try {
            db = dbConn();
            String sqlCheck = String.format("select exists (select * from categoryTab where category='%s' or category_id='%s')", dto.getCategoryId(), dto.getCategory());
            //기존에 동일한 정보로 등록된 데이터가 있는지 확인 → 1(true) or 0(false)로 반환
            cursor = db.rawQuery(sqlCheck, null);
            cursor.moveToFirst();
            if (cursor.getInt(0)==0) {
                String sqlAdd = String.format("insert into categoryTab (category_id, category) values ('%s', '%s')", dto.getCategoryId(), dto.getCategory());
                db.execSQL(sqlAdd);
                result = "succeed";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "이미 사용 중인 분류명/분류코드가 있습니다.", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    //상품분류 삭제
    public String deleteCategory(MenuDTO dto) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String result = "";
        try {
            db = dbConn();
            String sqlCheck = String.format("select exists (select * from categoryTab where category_id ='%s' and category='%s')", dto.getCategoryId(), dto.getCategory());
            //기존에 동일한 정보(분류코드)로 등록된 데이터가 있는지 확인 → 1(true) or 0(false)로 반환
            cursor = db.rawQuery(sqlCheck, null);
            cursor.moveToFirst();

            if (cursor.getInt(0) == 1) {
                String sql = String.format("delete from categoryTab where (category_id ='%s' and category='%s')", dto.getCategoryId(), dto.getCategory());
                db.execSQL(sql);
                result = "succeed";
            } else {
                result = "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return result;
    }
}
