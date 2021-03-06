package com.wdysolutions.www.rf_scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wdysolutions.www.rf_scanner.AuditPen.AuditPen_model_pig;
import com.wdysolutions.www.rf_scanner.MultiAction.Transfer_model_pig;
import com.wdysolutions.www.rf_scanner.SwineSales.SwineSales_scan_model;

import java.util.ArrayList;

/**
 * Created by Ron on 9/8/2018.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private Context context;

    //If you want to add more questions or wanna update table values
    //or any kind of modification in db just increment version no.
    private static final int DB_VERSION = 3;

    // Database Name
    private static final String DB_NAME = "rf_scanner_multi_action.db";


    //table name
    private static final String TABLE_MA_PIG = "tbl_ma_pig";
    private static final String SS_TABLE_MA_PIG = "ss_tbl_ma_pig";
    private static final String AUDIT_TABLE_PIG = "audit_table_pig";


    //table field
    private static final String ID_MA_PIG = "id_ma_pig";
    private static final String SWINE_ID = "swine_id";
    private static final String SWINE_CODE = "swine_code";
    private static final String BRANCH_ID = "branch_id";
    private static final String BUILDING_ID = "building_id";
    private static final String PEN_ID = "pen_id";
    private static final String CHECK_STATUS = "check_status";
    private static final String GENDER = "gender";
    private static final String CREATE_TABLE_MC = "CREATE TABLE " + TABLE_MA_PIG + " ( " + ID_MA_PIG + " INTEGER PRIMARY KEY AUTOINCREMENT , "+ SWINE_ID + " INTEGER(255), "  + SWINE_CODE + " VARCHAR(255), " + BRANCH_ID + " INTEGER(255), " + BUILDING_ID + " INTEGER(255), " + PEN_ID + " INTEGER(255), " + CHECK_STATUS + " INTEGER(255), " + GENDER + " INTEGER(255));";


    //tbl_audit_pigs
    private static final String AUDIT_ID = "audit_id";
    private static final String AUDIT_SWINE_ID = "audit_swine_id";
    private static final String AUDIT_SWINE_CODE = "audit_swine_code";
    private static final String AUDIT_ACTUAL_ID = "audit_actual_id";
    private static final String CREATE_AUDIT_TABLE = "CREATE TABLE " + AUDIT_TABLE_PIG + " ( " + AUDIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "+ AUDIT_SWINE_ID + " INTEGER(255), "+ AUDIT_ACTUAL_ID + " VARCHAR(255), "  + AUDIT_SWINE_CODE + " VARCHAR(255));";


    private static final String SS_ID_MA_PIG = "ss_id_ma_pig";
    private static final String SS_DELIVERY_NUMBER = "ss_delivery_number";
    private static final String SS_SWINE_ID = "ss_swine_id";
    private static final String SS_SWINE_CODE = "ss_swine_code";
    private static final String SS_AGE = "ss_age";
    private static final String SS_FEED_CONS = "ss_feed_cons";
    private static final String SS_WEIGHT = "ss_weight";
    private static final String SS_PRICE = "ss_price";
    private static final String SS_SUBTOTAL = "ss_subtotal";
    //private static final String SS_CREATE_TABLE_MC = "";


    private static final String SS_CREATE_TABLE_MC = "CREATE TABLE " + SS_TABLE_MA_PIG + " ( " + SS_ID_MA_PIG
            + " INTEGER PRIMARY KEY AUTOINCREMENT , "+ SS_SWINE_ID
            + " INTEGER(255), "  + SS_SWINE_CODE + " VARCHAR(255), " + SS_AGE + " VARCHAR(255), " + SS_FEED_CONS
            + " INTEGER(255), " + SS_WEIGHT + " VARCHAR(255), " + SS_PRICE + " VARCHAR(255), "  +SS_SUBTOTAL + " VARCHAR(255), "  + SS_DELIVERY_NUMBER + " VARCHAR(255));";



    //Drop table query
    private static final String DROP_TABLE_MC = "DROP TABLE IF EXISTS " + TABLE_MA_PIG;
    private static final String SS_DROP_TABLE_MC = "DROP TABLE IF EXISTS " + SS_TABLE_MA_PIG;
    private static final String AUDIT_DROB_TABLE = "DROP TABLE IF EXISTS " + AUDIT_TABLE_PIG;

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //OnCreate is called only once
        sqLiteDatabase.execSQL(CREATE_TABLE_MC);
        sqLiteDatabase.execSQL(SS_CREATE_TABLE_MC);
        sqLiteDatabase.execSQL(CREATE_AUDIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //OnUpgrade is called when ever we upgrade or increment our database version no
        sqLiteDatabase.execSQL(DROP_TABLE_MC);
        sqLiteDatabase.execSQL(SS_DROP_TABLE_MC);
        sqLiteDatabase.execSQL(AUDIT_DROB_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void saveUser(String name, String score){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(NAME_USER, name);
//        contentValues.put(SCORE_MC_EASY, score);
//        contentValues.put(SCORE_TF_EASY, score);
//        contentValues.put(SCORE_IQ_EASY, score);
//        contentValues.put(SCORE_MC_MEDIUM, score);
//        contentValues.put(SCORE_TF_MEDIUM, score);
//        contentValues.put(SCORE_IQ_MEDIUM, score);
//        contentValues.put(SCORE_MC_HARD, score);
//        contentValues.put(SCORE_TF_HARD, score);
//        contentValues.put(SCORE_IQ_HARD, score);
//
//        db.insert(TABLE_USER, null, contentValues);
//        db.close();
    }

    public void updateScore(String score, String category, String difficulty){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        if (category.equals("mc")){
//
//            if (difficulty.equals("easy")){
//                contentValues.put(SCORE_MC_EASY, score);
//            } else if (difficulty.equals("medium")){
//                contentValues.put(SCORE_MC_MEDIUM, score);
//            } else if (difficulty.equals("hard")){
//                contentValues.put(SCORE_MC_HARD, score);
//            }
//        } else if (category.equals("tf")){
//
//            if (difficulty.equals("easy")){
//                contentValues.put(SCORE_TF_EASY, score);
//            } else if (difficulty.equals("medium")){
//                contentValues.put(SCORE_TF_MEDIUM, score);
//            } else if (difficulty.equals("hard")){
//                contentValues.put(SCORE_TF_HARD, score);
//            }
//        } else if (category.equals("iq")){
//
//            if (difficulty.equals("easy")){
//                contentValues.put(SCORE_IQ_EASY, score);
//            } else if (difficulty.equals("medium")){
//                contentValues.put(SCORE_IQ_MEDIUM, score);
//            } else if (difficulty.equals("hard")){
//                contentValues.put(SCORE_IQ_HARD, score);
//            }
//        }

//        db.update(TABLE_USER, contentValues, ID_USER+"=1", null);
        //   db.close();
    }

    public void clearScore(){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(SCORE_MC_EASY, "0");
//        contentValues.put(SCORE_MC_MEDIUM, "0");
//        contentValues.put(SCORE_MC_HARD, "0");
//        contentValues.put(SCORE_IQ_EASY, "0");
//        contentValues.put(SCORE_IQ_MEDIUM, "0");
//        contentValues.put(SCORE_IQ_HARD, "0");
//        contentValues.put(SCORE_TF_EASY, "0");
//        contentValues.put(SCORE_TF_MEDIUM, "0");
//        contentValues.put(SCORE_TF_HARD, "0");
//
//        db.update(TABLE_USER, contentValues, ID_USER+"=1", null);
//        db.close();
//
//         SQLiteDatabase db = this.getWritableDatabase();
//         db.update(TABLE_MA_PIG, contentValues, ID_USER+"=1", null);
//         db.close();
    }


    public String getScoreIQ(String difficulty){
//        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_USER, null);
//        cursor.moveToFirst();
//
//        if (cursor.moveToFirst()) {
//            if (difficulty.equals("easy")){
//                return cursor.getString(cursor.getColumnIndex(SCORE_IQ_EASY));
//            } else if (difficulty.equals("medium")){
//                return cursor.getString(cursor.getColumnIndex(SCORE_IQ_MEDIUM));
//            } else if (difficulty.equals("hard")){
//                return cursor.getString(cursor.getColumnIndex(SCORE_IQ_HARD));
//            }
//        }
//        cursor.close();
        return null;
    }

    public String getName(){
//        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_USER, null);
//        cursor.moveToFirst();
//        if (cursor.moveToFirst()) {
//            return cursor.getString(cursor.getColumnIndex(NAME_USER));
//        }
//        cursor.close();
        return null;
    }

    public void delete_table_pig(String swine_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_MA_PIG +" where swine_id= "+swine_id);
        db.close();
    }


    public void delete_all_table_pig(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_MA_PIG);
        db.close();
    }


    public void add_pigs_sqlite(ArrayList<Transfer_model_pig> allPigs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (Transfer_model_pig pigs : allPigs) {
                values.put(SWINE_ID, pigs.getSwine_id());
                values.put(SWINE_CODE, pigs.getSwine_code());
                values.put(BRANCH_ID, pigs.getBranch_id());
                values.put(BUILDING_ID, pigs.getBuilding_id());
                values.put(PEN_ID, pigs.getPen_id());
                values.put(CHECK_STATUS, pigs.getCheck_status());
                values.put(GENDER, pigs.getGender());
                db.insert(TABLE_MA_PIG, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<Transfer_model_pig> get_pigs_sqlite() {
        ArrayList<Transfer_model_pig> questionsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String coloumn[] = {SWINE_ID, SWINE_CODE, BRANCH_ID, BUILDING_ID, PEN_ID, CHECK_STATUS, GENDER};
        Cursor cursor = db.query(TABLE_MA_PIG, coloumn, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Transfer_model_pig question = new Transfer_model_pig(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    cursor.getInt(4),
                    cursor.getInt(5),
                    cursor.getString(6)
            );
            questionsList.add(question);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        cursor.close();
        db.close();
        return questionsList;
    }

    public void ss_delete_all_table_pig(String dr_num){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ SS_TABLE_MA_PIG+" where "+SS_DELIVERY_NUMBER+" = "+"'"+dr_num+"'");
        db.close();
    }

    public void ss_add_pigs_sqlite(ArrayList<SwineSales_scan_model> allPigs) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for (SwineSales_scan_model pigs : allPigs) {
                values.put(SS_SWINE_ID, pigs.getSwine_id());
                values.put(SS_SWINE_CODE, pigs.getSwine_code());
                values.put(SS_AGE, pigs.getAge());
                values.put(SS_FEED_CONS, pigs.getFeeds_cons());
                values.put(SS_WEIGHT, pigs.getWeight());
                values.put(SS_PRICE, pigs.getPrice());
                values.put(SS_SUBTOTAL, pigs.getSubtotal());
                values.put(SS_DELIVERY_NUMBER, pigs.getDelivery_number());
                db.insert(SS_TABLE_MA_PIG, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public String checkSwine(String swine_id){
        Cursor cursor = this.getReadableDatabase().rawQuery("SELECT * FROM " + SS_TABLE_MA_PIG+" where ss_swine_id = '"+swine_id+"'", null);
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(SS_SWINE_ID));
        }
        cursor.close();
        return "";
    }

    public ArrayList<SwineSales_scan_model> ss_get_pigs_sqlite(String dr_num) {
        ArrayList<SwineSales_scan_model> questionsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String coloumn[] = {SS_SWINE_ID, SS_SWINE_CODE, SS_AGE, SS_FEED_CONS, SS_WEIGHT, SS_PRICE,SS_SUBTOTAL,SS_DELIVERY_NUMBER};
        Cursor cursor = db.query(SS_TABLE_MA_PIG, coloumn, SS_DELIVERY_NUMBER+" = "+"'"+dr_num+"'", null, null, null, null);

        while (cursor.moveToNext()) {
            SwineSales_scan_model question = new SwineSales_scan_model(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getDouble(4),
                    cursor.getDouble(5),
                    cursor.getDouble(6),
                    cursor.getString(7)
            );
            questionsList.add(question);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        cursor.close();
        db.close();
        return questionsList;
    }

    public void ss_delete_table_pig(String swine_id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ SS_TABLE_MA_PIG +" where ss_swine_id= "+swine_id);
        db.close();
    }

    public void ss_update_table_pig(String swine_id,String weight,String price,String subtotal){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SS_WEIGHT, weight);
        contentValues.put(SS_PRICE, price);
        contentValues.put(SS_SUBTOTAL, subtotal);

        db.update(SS_TABLE_MA_PIG, contentValues, SS_SWINE_ID+" = "+"'"+swine_id+"'", null);
        db.close();
    }

    public int audit_add_pigs_sqlite(int audit_swine_id,String audit_swine_code,String actual_id ) {
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor= db.rawQuery("SELECT COUNT (*) FROM " + AUDIT_TABLE_PIG +" where audit_actual_id = "+actual_id  ,null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);

        if(count>0){

            cursor.close();
            db.close();
            return count;
        }else{
            String query = "INSERT INTO " +AUDIT_TABLE_PIG +" (audit_swine_id,audit_actual_id,audit_swine_code) VALUES (?,?,?)";
            db.execSQL(query, new String[]{String.valueOf(audit_swine_id), actual_id,audit_swine_code});


            cursor.close();
            db.close();
            return count;
        }
    }

    public int audit_count_data(String audit_swine_id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor= db.rawQuery("SELECT COUNT (*) FROM " + AUDIT_TABLE_PIG +" where audit_swine_id = "+audit_swine_id  ,null);
        cursor.moveToFirst();
        int count= cursor.getInt(0);
        cursor.close();
        return count;
    }

    public void audit_delete_all_table_pig(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ AUDIT_TABLE_PIG);
        db.close();
    }
}
