package com.example.cafemanagement;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    Context context;
    SQLiteDatabase db;

    public MenuDAO(Context context) {
        this.context = context;
    }

    public SQLiteDatabase dbConn() {
        db = context.openOrCreateDatabase("management.db", Context.MODE_PRIVATE, null);
        //SQLite에서 foreign key 설정 on
        String PRAGMA = "PRAGMA foreign_keys = ON";

        //메뉴테이블(menuList) 생성
        String createML = "CREATE TABLE if not exists menuList(" +
                "category_id TEXT, menu_no integer primary key AUTOINCREMENT, " +
                "menu_name text UNIQUE, menu_id TEXT AS (printf('%s%03d', category_id, menu_no)) stored, " +
                "price integer default 0 not null, " +
                "run integer not null check (run in (0,1)) default 0, " +
                "foreign key (category_id) references categoryTab (category_id))";

        //뷰(menuView) 생성 → 리사이클러뷰에서 테이블 목록 조회 시 활용
        String createView = "create view if not exists menuView as select category, menu_id, menu_name, price, run " +
                "from categoryTab c, menuList m where c.category_id=m.category_id";

        db.execSQL(PRAGMA);
        db.execSQL(createML);
        db.execSQL(createView);
        return db;
    }

    //신규메뉴 임시저장 시, 상품명 중복검사 실행ㅅ
    public String checkName(String name) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String result = "";

        try {
            db = dbConn();
            String sqlCheck = "select exists (select * from menuList where menu_name ='" + name + "')";
            //기존에 동일한 정보(분류코드)로 등록된 데이터가 있는지 확인 → 1(true) or 0(false)로 반환
            cursor = db.rawQuery(sqlCheck, null);
            cursor.moveToFirst();

            if (cursor.getInt(0) == 1) {
                result = "isUsed";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return result;
    }


    //신규메뉴 등록 쿼리(menuList에 insert)
    public void insertDB(MenuDTO dto) {
        SQLiteDatabase db = null;
        try {
            db = dbConn();
            String sql = String.format("Insert into menuList (category_id, menu_name, price, run) " +
                            "values((select category_id from categoryTab where category='%s'), '%s', %d, %d)",
                    dto.getCategory(), dto.getMenuName(), dto.getPrice(), dto.getRun());
            //category_id → 스피너에서 분류명을 선택하면 서브쿼리로 그에 해당하는 분류코드를 가져옴
            db.execSQL(sql);
        } catch (
                Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
    }

    //상품명, 판매가, 운영여부만 update 쿼리로 수정가능하도록 설정
    public String update(MenuDTO dto) {
        SQLiteDatabase db = null;
        String result = "";
        try {
            db = dbConn();
            String sql = String.format("update menuList set " +
                    "menu_name='%s', price=%d, run=%d " +
                    "where menu_id='%s'", dto.getMenuName(), dto.getPrice(), dto.getRun(), dto.getMenuId());
            db.execSQL(sql);
            result = "succeed";
            //SQLiteConstraintException: UNIQUE constraint failed
            //기존 등록된 다른 상품명과 동일하게 수정하는 경우 발생하는 에러 처리를 위해 코드 추가
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
        return result;
    }

    //menuEdit 액티비티에서 상품코드 → delete 실행
    public void delete(MenuDTO dto) {
        SQLiteDatabase db = null;
        try {
            db = dbConn();
            String sql = "delete from menuList where menu_id='" + dto.getMenuId() + "'";
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) db.close();
        }
    }

    //menuEdit 액티비티에서, DB에 저장된 상품정보 중 상품분류 찾아서 반환
    public String findCategory(MenuDTO dto) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String category = null;

        try {
            db = dbConn();
            String sql = "select category from menuView where menu_id='" + dto.getMenuId() + "'";
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToNext()) {
                category = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return category;
    }

    //리사이클러뷰에서 보이는 상품목록을 리스트로 만드는 메소드
    //categoryTab 과 menuList를 조인해서 만든 menuView 를 활용해 필요한 내용만 전달
    public List<MenuDTO> list() {
        List<MenuDTO> items = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbConn();
            String sql = "select * from menuView";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String category = cursor.getString(0);
                String menuId = cursor.getString(1);
                String menuName = cursor.getString(2);
                int price = cursor.getInt(3);
                int run = cursor.getInt(4);
                items.add(new MenuDTO(category, menuId, menuName, price, run));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return items;
    }

    //list()메소드 오버로딩 → 상품분류/상품명(키워드) 검색 쿼리 시 목록 반환
    public List<MenuDTO> list(String keyword) {
        List<MenuDTO> items = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String sql = "";

        try {
            db = dbConn();
            String sqlCheck = String.format("select exists (select * from categoryTab where category='%s')", keyword);
            //기존에 동일한 정보로 등록된 데이터가 있는지 확인 → 1(true) or 0(false)로 반환
            cursor = db.rawQuery(sqlCheck, null);
            cursor.moveToFirst();

            if (cursor.getInt(0) == 1) {
                sql = "Select * from menuView where category='" + keyword + "' order by menu_id";
                //상품분류로 검색
            } else {
                sql = "Select * from menuView where menu_name like '%" + keyword + "%' order by category, menu_id";
                //상품명 키워드 검색
            }
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                String category = cursor.getString(0);
                String menuId = cursor.getString(1);
                String menuName = cursor.getString(2);
                int price = cursor.getInt(3);
                int run = cursor.getInt(4);
                items.add(new MenuDTO(category, menuId, menuName, price, run));
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return items;
    }
}