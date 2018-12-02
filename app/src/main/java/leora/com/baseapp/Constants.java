package leora.com.baseapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;

import java.io.File;

import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.utils.ValueUtils;

public class Constants {

    final public static int BUILD_TYPE_LIVE = 1;
    final public static int BUILD_TYPE_LIVE_DEMO = 2;
    final public static int BUILD_TYPE_STAGING = 3;
    final public static int BUILD_TYPE_LOCAL = 4;


    /**
     * By setting it to the particular build type the IP changes automatically and other configurations can be built on top of this environment constant
     * kindly replace the respective ips in line "26"
     */
    final public static int BUILD_TYPE = BUILD_TYPE_LOCAL;
//    http://192.168.0.100/mproduction_api/index.php/api/SyncData
    public static final String SERVER = ((BUILD_TYPE == BUILD_TYPE_LIVE) || (BUILD_TYPE == BUILD_TYPE_LIVE_DEMO)) ? "http://192.168.0.49" : (BUILD_TYPE == BUILD_TYPE_STAGING) ? "staging_ip_here" : "http://192.168.0.49";

    public static final String APP_PACAGENAME = (BUILD_TYPE == BUILD_TYPE_LIVE) ? ValueUtils.APP_ID : "" + ValueUtils.APP_ID ;

    final public static String BASE_URL  = SERVER + "/mproduction_api/index.php/api/";

    final public static String TBL_RAW_MATERIAL = "raw_material";
    final public static String TBL_RAW_MATERIAL_AUDIT = "raw_material_audit";
    final public static String TBL_CONFIG = "confiq";
    final public static String TBL_METRICS = "metrics";

    public static final File app_storageDir = new File(getRootFolder(false));
    public static String CURRENT_ACTIVITY = ValueUtils.NOT_DEFINED;
    public static String FRAGMENT_NAME = "FRAGMENT_NAME";
    public static DbSQLiteHelper dbModel;
    public static Activity networkErrorAct = null;
    public static java.net.CookieManager msCookieManager = new java.net.CookieManager();
    public static String iscurrentChatViewId = "-1";
    public static Boolean ischatTabActive = false;




    final public static int RM_AUDIT_IN = 1, RM_AUDIT_PRODUCTION_PLANNING = 2, RM_AUDIT_OUT_PRODUCTION = 3, RM_AUDIT_OUT_REJECTION = 4;
    final public static int PRODUCT_IN = 1, PRODUCT_PLAN_OUT = 2, PRODUCT_IMP_POUT = 3, PRODUCT_OUT_REJECT = 4;


    public static Typeface font1_light, font1_regular, font1_bold;

    public static String password = null;

    public static String URL_REPORT_ERROR = Constants.BASE_URL + "report_error_sample";
    public static String URL_SYNC_DATA = Constants.BASE_URL + "SyncData";
    public static String URL_ADD_RM = Constants.BASE_URL + "AddRawMaterial";
    public static String URL_GET_RM_AUDITS = Constants.BASE_URL + "GetRmAudits";
    public static String URL_ADD_RM_AUDIT = Constants.BASE_URL + "AddRawMaterialAudit";

    public static String getRootFolder(Boolean isSlashRequired) {
        String path = Environment.getExternalStorageDirectory() + "/" + ValueUtils.ROOT_FOLDER_PREFIX;
        File folder = new File(path);
        if (!folder.exists()) folder.mkdir();
        if (isSlashRequired) return path + "/";
        else return path;
    }



    public static String getAuditName(String status_int)
    {
        String status = ValueUtils.NOT_DEFINED;

        switch (Integer.parseInt(status_int))
        {

            case RM_AUDIT_IN:
                status = "Raw material Store";
                break;

            case RM_AUDIT_PRODUCTION_PLANNING:
                status = "Production planning";
                break;

            case RM_AUDIT_OUT_PRODUCTION:
                status = "SMG Store";
                break;

            case RM_AUDIT_OUT_REJECTION:
                status = "Rejection";
                break;

        }

        return status;


    }

    public static Typeface getFont1Bold(Context context) {
        if (font1_bold == null)
            font1_bold = Typeface.createFromAsset(App.getAppContext().getAssets(), "font1_bold.ttf");
        return font1_bold;
    }


    public static Typeface getFont1Regular(Context context) {
        if (font1_regular == null)
            font1_regular = Typeface.createFromAsset(App.getAppContext().getAssets(), "font1_regular.ttf");
        return font1_regular;
    }


    public static Typeface getFont1Light(Context context) {
        if (font1_light == null)
            font1_light = Typeface.createFromAsset(App.getAppContext().getAssets(), "font1_light.ttf");
        return font1_light;
    }



    }
