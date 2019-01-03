package leora.com.baseapp.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.model.apimodel.MaterialAuditModel;
import leora.com.baseapp.model.dbmodel.MachineProductivityAuditModel;
import leora.com.baseapp.model.dbmodel.PMachineModel;
import leora.com.baseapp.model.dbmodel.ProductModel;
import leora.com.baseapp.model.dbmodel.ProductionProcessModel;
import leora.com.baseapp.model.dbmodel.RawMaterialModel;
import leora.com.baseapp.ormfiles.ModelBaseClass;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DbUtils;
import leora.com.baseapp.utils.ValueUtils;

public class DbSQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = (((Constants.BUILD_TYPE == Constants.BUILD_TYPE_LIVE) || (Constants.BUILD_TYPE == Constants.BUILD_TYPE_LIVE_DEMO)) ? "" : ((Constants.BUILD_TYPE == Constants.BUILD_TYPE_STAGING) ? "STAGING_" : "LOCAL_")) + "vc_hospital.db";

    public DbSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists " + Constants.TBL_CONFIG + " (id integer primary key, syncdata_last_sync_time text, table_drop_time text)");
        db.execSQL("create table if not exists " + Constants.TBL_RAW_MATERIAL + " (id integer primary key, slug text, name text DEFAULT '', type text, length text, ref_id text, status integer, is_deleted text, created_on text, updated_on text, last_sync_time text)");
        db.execSQL("create table if not exists " + Constants.TBL_RAW_MATERIAL_AUDIT + " (id integer primary key, slug text, audit_type text, supplier_name text , audit_date text, comment text , status integer, metric_count integer, raw_material_ref_id text, is_deleted text, created_on text, updated_on text, last_sync_time text)");
        db.execSQL("create table if not exists " + Constants.TBL_METRICS+ " (id integer primary key, slug text, name text, category text, is_deleted text, comment text, status text, created_on text, updated_on text, last_sync_time text)");
        db.execSQL("create table if not exists " + Constants.TBL_PRODUCT+ " (id integer primary key, slug text, name text, ref_id text, is_deleted text, comment text, status text, created_on text, updated_on text, last_sync_time text)");
        db.execSQL("create table if not exists " + Constants.TBL_PRODUCTION_PROCESS+ " (id integer primary key, slug text, name text, ref_id text, is_deleted text, comment text, status text, created_on text, updated_on text, last_sync_time text)");
        db.execSQL("create table if not exists " + Constants.TBL_P_MACHINE+ " (id integer primary key, slug text, name text, ref_id text, is_deleted text, comment text, status text, created_on text, updated_on text, last_sync_time text)");
        db.execSQL("create table if not exists " + Constants.TBL_MACHINE_PRODUCTIVITY_AUDIT + " (id integer primary key, slug text, machine_slug text, product_slug text, process_slug text, process_description text, job_order_id text, work_done_from_time text, work_done_to_time text, quantity_approved text, quantity_rejected text, quantity_rework text, operator_name text, supervisor_name text, is_rework text, is_supervisor_approved text, status text, comment text, created_on text, updated_on text, last_sync_time text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable();
        onCreate(db);
    }

    public void dropTable() {
        Log.e("camee_droptt", "tru");
        try {
            SQLiteDatabase db = this.getWritableDatabase();

            try {
                // query to obtain the names of all tables in your database
                Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
                List<String> tables = new ArrayList<>();

                // iterate over the result set, adding every table name to a list
                while (c.moveToNext()) {
                    tables.add(c.getString(0));
                }

                // call DROP TABLE on every table name
                for (String table : tables) {
                    try {
                        //                        Log.e("elld_Ex", "=="+table);
                        String dropQuery = "DELETE FROM " + table;
                        db.execSQL(dropQuery);
                    } catch (SQLException e) {
                        //                        Log.e("elld_Ex3", "=="+e);
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                //                Log.e("elld_Ex2", "=="+e);
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RawMaterialModel getMaterialModelByRefId(String value) {

        RawMaterialModel materialModel = new RawMaterialModel();

        Cursor cursor = getDataByKey(Constants.TBL_RAW_MATERIAL, "ref_id", value);

//        Log.e("refiddget", "==="+cursor.getCount()+"==");

        if(cursor.moveToFirst())
        materialModel = (RawMaterialModel) setModel(RawMaterialModel.class, cursor);

//        Log.e("refidd", "==="+value+"==="+materialModel.name);


        return materialModel;
    }

    public RawMaterialModel getMaterialModel(String value) {

        RawMaterialModel materialModel = new RawMaterialModel();

        Cursor cursor = getDataByKey(Constants.TBL_RAW_MATERIAL, "slug", value);

        if(cursor.moveToFirst())
        materialModel = (RawMaterialModel) setModel(RawMaterialModel.class, cursor);

        return materialModel;
    }


    public ArrayList<RawMaterialModel> getMaterialModels() {
        ArrayList<RawMaterialModel> materialModels = new ArrayList<RawMaterialModel>();
        Cursor cursor = executeQuery("SELECT * FROM " + Constants.TBL_RAW_MATERIAL);
        while (cursor.moveToNext()) materialModels.add(setMaterialModel(cursor));
        cursor.close();

        return materialModels;

    }

    public ProductionProcessModel getProductionProcessModel(String value) {

        ProductionProcessModel materialModel = new ProductionProcessModel();

        Cursor cursor = getDataByKey(Constants.TBL_PRODUCTION_PROCESS, "slug", value);

        if(cursor.moveToFirst())
            materialModel = (ProductionProcessModel) setModel(ProductionProcessModel.class, cursor);

        return materialModel;
    }


    public ArrayList<ProductionProcessModel> getProductionProcessModels() {
        ArrayList<ProductionProcessModel> materialModels = new ArrayList<ProductionProcessModel>();
        Cursor cursor = executeQuery("SELECT * FROM " + Constants.TBL_PRODUCTION_PROCESS);

        Log.e("tot_r_p", ""+cursor.getCount()+"==");

        while (cursor.moveToNext()) materialModels.add(setProductionProcessModel(cursor));
        cursor.close();

        return materialModels;

    }

    public ProductModel getProductModel(String value) {

        ProductModel materialModel = new ProductModel();

        Cursor cursor = getDataByKey(Constants.TBL_PRODUCT, "slug", value);

        if(cursor.moveToFirst())
            materialModel = (ProductModel) setModel(ProductModel.class, cursor);

        return materialModel;
    }


    public ArrayList<ProductModel> getProductModels() {
        ArrayList<ProductModel> materialModels = new ArrayList<ProductModel>();
        Cursor cursor = executeQuery("SELECT * FROM " + Constants.TBL_PRODUCT);

        Log.e("tot_r_p", ""+cursor.getCount()+"==");

        while (cursor.moveToNext()) materialModels.add(setProductModel(cursor));
        cursor.close();

        return materialModels;

    }

    public PMachineModel getPMachineModel(String value) {

        PMachineModel materialModel = new PMachineModel();

        Cursor cursor = getDataByKey(Constants.TBL_P_MACHINE, "slug", value);

        if(cursor.moveToFirst())
            materialModel = (PMachineModel) setModel(PMachineModel.class, cursor);

        return materialModel;
    }

    public ArrayList<PMachineModel> getPMachineModels() {
        ArrayList<PMachineModel> materialModels = new ArrayList<PMachineModel>();
        Cursor cursor = executeQuery("SELECT * FROM " + Constants.TBL_P_MACHINE);

        Log.e("tot_r_p", ""+cursor.getCount()+"==");

        while (cursor.moveToNext()) materialModels.add(setPMachineModel(cursor));
        cursor.close();

        return materialModels;

    }


    public MachineProductivityAuditModel getMachineProductivityAuditModel(String value) {

        MachineProductivityAuditModel materialModel = new MachineProductivityAuditModel();

        Cursor cursor = getDataByKey(Constants.TBL_MACHINE_PRODUCTIVITY_AUDIT, "slug", value);

        if(cursor.moveToFirst())
            materialModel = (MachineProductivityAuditModel) setModel(MachineProductivityAuditModel.class, cursor);

        return materialModel;
    }


    public String addConstrainsAND(String constrains_q, String value)
    {

        if(DataUtils.isStringValueExist(value))
        {

            if(DataUtils.isStringValueExist(constrains_q))
                constrains_q += " AND "+ value;
            else
                constrains_q = " WHERE "+value;

        }

        return constrains_q;
    }

//    machine_slug text, product_slug text, process_slug text, process_description text, job_order_id text,
// work_done_from_time text, work_done_to_time text, quantity_approved text, quantity_rejected text, quantity_rework text,
// operator_name text, supervisor_name text, is_rework text, is_supervisor_approved text, status text, comment text, created_on text, updated_on text, last_sync_time text)");

    public ArrayList<MachineProductivityAuditModel> getMachineProductivityAuditModels(String fl_machine_slug, String fl_product_slug, String fl_job_order_id, String fl_work_start_time, String fl_work_end_time) {

        ArrayList<MachineProductivityAuditModel> materialModels = new ArrayList<MachineProductivityAuditModel>();

        String query_base  = "SELECT * FROM " + Constants.TBL_MACHINE_PRODUCTIVITY_AUDIT;

        String constrains = "";


        if(DataUtils.isStringValueExist(fl_machine_slug))
            constrains = addConstrainsAND(constrains, " machine_slug = '" +fl_machine_slug+ "' ");

        if(DataUtils.isStringValueExist(fl_product_slug))
            constrains = addConstrainsAND(constrains, " product_slug = '" +fl_product_slug+ "' ");

        if(DataUtils.isStringValueExist(fl_job_order_id))
            constrains = addConstrainsAND(constrains, " job_order_id = '" +fl_job_order_id+ "' ");

        if(DataUtils.isStringValueExist(fl_work_start_time))
            constrains = addConstrainsAND(constrains, " work_done_from_time >= '" +fl_work_start_time+ "' ");

        if(DataUtils.isStringValueExist(fl_work_end_time))
            constrains = addConstrainsAND(constrains, " work_done_to_time <= '" +fl_work_end_time+ "' ");



        String query_final = query_base+constrains;
        Log.e("query_final ", "==="+query_final +"===");
        Cursor cursor = executeQuery(query_final );

        Log.e("tot_r_p", ""+cursor.getCount()+"==");

        while (cursor.moveToNext()) {

            MachineProductivityAuditModel machineProductivityAuditModel =setMachineProductivityAuditModel(cursor);
            machineProductivityAuditModel.pMachineModel = getPMachineModel(machineProductivityAuditModel.machine_slug);
            machineProductivityAuditModel.productModel = getProductModel(machineProductivityAuditModel.product_slug);
            materialModels.add(machineProductivityAuditModel);
        }
        cursor.close();

        return materialModels;

    }

    public ArrayList<MaterialAuditModel> getmaterialAuditModels() {
        ArrayList<MaterialAuditModel> materialAuditsModels = new ArrayList<MaterialAuditModel>();
        Cursor cursor = executeQuery("SELECT * FROM " + Constants.TBL_RAW_MATERIAL_AUDIT + " WHERE audit_type<>'"+Constants.RM_AUDIT_PRODUCTION_PLANNING+"'");

//        Log.e("cur_size", "==="+cursor.getCount()+"==");

        while (cursor.moveToNext())
        {
            MaterialAuditModel materialAuditModel = setMaterialAuditModel(cursor);

            RawMaterialModel materialModel = getMaterialModelByRefId(materialAuditModel.raw_material_ref_id);
            materialAuditModel.raw_material_name = materialModel.name;

            materialAuditsModels.add(materialAuditModel);
        }

        cursor.close();

//        Log.e("cur_size_final", "==="+materialAuditsModels.size()+"==");


        return materialAuditsModels;

    }

    public ArrayList<MaterialAuditModel> getMaterialAuditModels() {
        ArrayList<MaterialAuditModel> materialAuditModels = new ArrayList<MaterialAuditModel>();
        Cursor cursor = executeQuery("SELECT * FROM " + Constants.TBL_RAW_MATERIAL_AUDIT);
        while (cursor.moveToNext()) materialAuditModels.add(setMaterialAuditModel(cursor));
        cursor.close();

        return materialAuditModels;

    }

    public MachineProductivityAuditModel setMachineProductivityAuditModel(Cursor cursor) {
        return (MachineProductivityAuditModel) setFieldsValues(MachineProductivityAuditModel.class, cursor);
    }


    public ProductionProcessModel setProductionProcessModel(Cursor cursor) {
        return (ProductionProcessModel) setFieldsValues(ProductionProcessModel.class, cursor);
    }


    public ProductModel setProductModel(Cursor cursor) {
        return (ProductModel) setFieldsValues(ProductModel.class, cursor);
    }


    public PMachineModel setPMachineModel(Cursor cursor) {
        return (PMachineModel) setFieldsValues(PMachineModel.class, cursor);
    }

    public RawMaterialModel setMaterialModel(Cursor cursor) {
        return (RawMaterialModel) setFieldsValues(RawMaterialModel.class, cursor);
    }

    public MaterialAuditModel setMaterialAuditModel(Cursor cursor) {
        return (MaterialAuditModel) setFieldsValues(MaterialAuditModel.class, cursor);
    }


    public Object setModel(Class<?> classx, Cursor cursor) {
        return setFieldsValues(classx, cursor);
    }

    public void putResourceAsBytes(byte[] resource_bytes, String table_name, String column_name, String pk_key, String pk_value) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_name, resource_bytes);

        Cursor cursor = db.rawQuery("select * from " + table_name + " where " + pk_key + " ='" + pk_value + "'", null);

        if (cursor.moveToFirst()) {
            db.update(table_name, contentValues, pk_key + " = ? ", new String[]{pk_value});
        } else {
            db.insert(table_name, null, contentValues);
        }

    }

    //common Methods -------------------------------------------------------------------------------

    public boolean isTableExists(String tableName) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name =" + " '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void commonInsertUpdateByUk(String table_name, HashMap<String, String> values) {
        commonInsertUpdate(table_name, table_name + "_slug", values);
    }

    public void commonInsertUpdate(String table_name, String unique_key, HashMap<String, String> values) {

        String unique_key_value = values.get(unique_key);
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> column_names = getTableColumnDetails(db, table_name);
        if (column_names.size() > 0) {
            ContentValues contentValues = new ContentValues();
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String value = entry.getValue();
                String key = entry.getKey();

                contentValues.put(key, value);

            }
            Cursor cursor = getDataByKey(table_name, unique_key, unique_key_value);
            ArrayList<String> arrayList = new ArrayList<>();
            if (cursor.moveToFirst()) {

                db.update(table_name, contentValues, unique_key + " = ? ", new String[]{unique_key_value});

            } else {

                db.insert(table_name, null, contentValues);
            }
            cursor.close();
        } else {
            //  //Log.e("err_table", "invalid table");
        }
    }

    public ArrayList<String> getTableColumnDetails(SQLiteDatabase db, String table) {
        ArrayList<String> column_names = new ArrayList<String>();
        Cursor table_info = db.rawQuery("PRAGMA table_info('" + table + "') ", null);
        while (table_info.moveToNext()) {
            String col_name = table_info.getString(table_info.getColumnIndex("name"));
            column_names.add(col_name);
        }
        table_info.close();
        return column_names;
    }

    public boolean checkIfExists(String TableName, String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "Select * from " + TableName + " where " + key + " = '" + value + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public Object modelActionLoad(ModelBaseClass modelBaseClass) {

        Cursor cursor = getDataByKey(modelBaseClass.getTableName(), modelBaseClass.getPk(), modelBaseClass.getPkValue());
        Object object;
        if (cursor.moveToFirst()) {
            object = setFieldsValues(modelBaseClass, cursor);
        } else {
            object = modelBaseClass;
        }
        cursor.close();
        return object;

    }

    public int modelActionInsert(ModelBaseClass modelBaseClass) {

        Boolean proceed_insert = true;
        int status = ModelBaseClass.STATUS_FAILURE;

        if (DataUtils.isStringValueExist(modelBaseClass.getPkValue())) {
            Cursor cursor = getDataByKey(modelBaseClass.getTableName(), modelBaseClass.getPk(), modelBaseClass.getPkValue());

            if (cursor.moveToFirst()) proceed_insert = false;
            cursor.close();
        }

        if (proceed_insert) {

            modelBaseClass.setPkValue(generateLocalSlug(modelBaseClass.getTableName(), modelBaseClass.getPk()));
            status = proceedInsertUpdate(modelBaseClass);
            //            modelBaseClass.reset(modelBaseClass);
        }

        return status;


    }

    public int modelActionUpdate(ModelBaseClass modelBaseClass) {

        int status = ModelBaseClass.STATUS_FAILURE;

        if (DataUtils.isStringValueExist(modelBaseClass.getPk())) {

            Cursor cursor = getDataByKey(modelBaseClass.getTableName(), modelBaseClass.getPk(), modelBaseClass.getPkValue());

            if (cursor.moveToFirst()) {
                status = proceedInsertUpdate(modelBaseClass);
                //                modelBaseClass.reset(modelBaseClass);
            }
        }
        return status;
    }

    public int modelActionDelete(ModelBaseClass modelBaseClass) {

        int status = ModelBaseClass.STATUS_FAILURE;

        Boolean proceed = false;

        if (DataUtils.isStringValueExist(modelBaseClass.getPk())) {

            Cursor cursor = getDataByKey(modelBaseClass.getTableName(), modelBaseClass.getPk(), modelBaseClass.getPkValue());

            if (cursor.moveToFirst()) {
                status = proceedInsertUpdate(modelBaseClass);
                modelBaseClass.reset(modelBaseClass);
            }
        }

        return status;

    }

    public int proceedInsertUpdate(ModelBaseClass modelBaseClass) {
        int status = ModelBaseClass.STATUS_FAILURE;
        HashMap<String, String> values_set = new HashMap<String, String>();

        Class<?> classx = modelBaseClass.getClass();
        try {
            for (Field field : classx.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String field_name = field.getName();
                    Object value = field.get(modelBaseClass);

                    if (DataUtils.isStringValueExist((String) value)) {
                        //  //Log.e("recc_valsett", field_name + "===" + value);

                        values_set.put(field_name, "" + value);
                    }
                }
            }
        } catch (Exception e) {
            //  //Log.e("instaexx", "e==" + e);
            e.printStackTrace();
        }

        if (values_set.containsKey(modelBaseClass.getPk())) {
            if (DataUtils.isStringValueExist(values_set.get(modelBaseClass.getPk()))) {
                commonInsertUpdate(modelBaseClass.getTableName(), modelBaseClass.getPk(), values_set);
            }
        }

        return status;
    }

    public Object setFieldsValues(Object objectx, Cursor cursor) {

        Class<?> classx = objectx.getClass();
        //        Object objectx = null;
        try {
            //            objectx = classx.newInstance();

            for (Field field : classx.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String field_name = field.getName();

                    try {
                        //                       //  //Log.e("came", field.getType() + "==st4== " + field_name);
                        String type = "" + field.getType();
                        int index = cursor.getColumnIndex(field_name);
                        if (index != -1) {

                            String val = cursor.getString(index);
                            if (DataUtils.isStringValueExist(val)) if (type.equals("boolean")) {
                                Boolean val_bool = val.equalsIgnoreCase("True");
                                field.set(objectx, val_bool);
                            } else if (type.equals("int")) {
                                if (DataUtils.isNumeric(val)) field.set(objectx, val);
                            } else {
                                field.set(objectx, val);
                            }
                        }
                    } catch (Exception e) {
                        //  //Log.e("essset1", field.getType() + "e==" + field_name + e + "==");
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            //  //Log.e("instaexx", "e==" + e);
            e.printStackTrace();
        }

        return objectx;


    }

    public Object setFieldsValues(Class<?> classx, Cursor cursor) {

        Object objectx = null;
        try {
            objectx = classx.newInstance();

            for (Field field : classx.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    String field_name = field.getName();

                    try {
                        //                       //  //Log.e("came", field.getType() + "==st4== " + field_name);
                        String type = "" + field.getType();
                        int index = cursor.getColumnIndex(field_name);
                        if (index != -1) {

                            String val = cursor.getString(index);
                            if (DataUtils.isStringValueExist(val)) if (type.equals("boolean")) {
                                Boolean val_bool = val.equalsIgnoreCase("True");
                                field.set(objectx, val_bool);
                            } else if (type.equals("int")) {
                                if (DataUtils.isNumeric(val)) field.set(objectx, val);
                            } else {
                                field.set(objectx, val);
                            }
                        }
                    } catch (Exception e) {
                        //  //Log.e("essset1", field.getType() + "e==" + field_name + e + "==");
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            //  //Log.e("instaexx", "e==" + e);
            e.printStackTrace();
        }

        return objectx;


    }

    public String generateLocalSlug(String table_name, String pk_name) {

        SQLiteDatabase db = this.getReadableDatabase();
        Boolean is_slugalready_exist = true;
        String slug = DbUtils.createSlug();

        while (!is_slugalready_exist) {
            Cursor cursor = db.rawQuery("select * from " + table_name + " where " + pk_name + "= " + "'" + table_name + "_" + slug + "'", null);

            if (cursor.moveToFirst()) {
                is_slugalready_exist = true;
                slug = DbUtils.createSlug();
            } else {
                is_slugalready_exist = false;
            }
        }
        return slug;
    }

    public Cursor getDataByPermalink(String table_name, String searchValue) {

        if (!DataUtils.isStringValueExist(searchValue)) searchValue = ValueUtils.NOT_DEFINED;

        return getDataByKey(table_name, table_name + "_permalink", searchValue);
    }

    public Cursor getDataBySlug(String table_name, String searchValue) {

        if (!DataUtils.isStringValueExist(searchValue)) searchValue = ValueUtils.NOT_DEFINED;

        return getDataByKey(table_name, table_name + "_slug", searchValue);
    }

    public Cursor getDataByKey(String table_name, String searchKey, String searchValue) {

        if (!DataUtils.isStringValueExist(searchValue)) searchValue = ValueUtils.NOT_DEFINED;

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "select * from " + table_name + " where " + searchKey + "= '" + searchValue + "'";
        return db.rawQuery(query, null);
    }

    public Cursor getDataCompleteTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + tableName, null);
    }

    public void deleteRow(String tableName, String searchKey, String searchValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from " + tableName + " where " + searchKey + "= '" + searchValue + "'", null);
    }

    public Cursor getUnsyncData(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + tableName + " where LOWER(sync_complete) <> LOWER('true')", null);

    }

    public Cursor executeQuery(String query) {

        SQLiteDatabase db = this.getReadableDatabase();
        //        //  //Log.e("rrr1", db.isOpen() + "===" + db.isReadOnly());
        //
        //        //  //Log.e("rrr2", db.isOpen() + "===" + db.isReadOnly());
        //
        //        //  //Log.e("rrr3", db.isOpen() + "===" + db.isReadOnly());

        return db.rawQuery(query, null);

    }

    public void executeAlter(String query) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

    }

    public Cursor getCompleteTable(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("select * from " + tableName, null);
    }

}
