package leora.com.baseapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;

/**
 * Created by AZR on 26-11-2018.
 */

public class ScreenSplash extends CustomAppCompatActivity {

    DbSQLiteHelper dbSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_splash);

        getIntents();
        initializeViews();
        setupValues();
        setupListeners();

    }


    public void getIntents() {

    }

    public void initializeViews() {

    }

    public void setupValues() {
        dbSQLiteHelper = CommonMethods.getDbModel(ScreenSplash.this);
        proceedSyncData();
    }

    public void setupListeners() {

    }

    public void proceedSyncData() {
        final String url = Constants.URL_SYNC_DATA;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("param_key1", "param_val1");

        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(ScreenSplash.this, false, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {


                        try {
                            {
                                JSONObject jsonObject = response;
                                String server_time = jsonObject.getString("server_time");

                                HashMap<String, String> row_config = new HashMap<String, String>();
                                row_config.put("id", "1");
                                row_config.put("syncdata_last_sync_time", server_time);

                                dbSQLiteHelper.commonInsertUpdate(Constants.TBL_CONFIG, "id", row_config);

                                try {
                                    JSONArray tables_arr = jsonObject.optJSONArray("tables");
                                    for (int k = 0; k < tables_arr.length(); k++) {

                                        JSONObject each_table_arr = tables_arr.getJSONObject(k);
                                        String table_name = each_table_arr.getString("name");
                                        String uk = each_table_arr.getString("uk");


                                        try {
                                            JSONArray table_value_arr = each_table_arr.optJSONArray("values");

                                            for (int j = 0; j < table_value_arr.length(); j++) {
                                                try {
                                                    JSONObject table_values_each = table_value_arr.getJSONObject(j);

                                                    try {
                                                        HashMap<String, String> each_row = new HashMap<String, String>();
                                                        Iterator<String> iter = table_values_each.keys();
                                                        while (iter.hasNext()) {
                                                            String key = iter.next();
                                                            String value = table_values_each.optString(key);
                                                            each_row.put(key, value);
                                                        }

                                                        each_row.put("last_sync_time", System.currentTimeMillis() + "");
                                                        Log.e("inserrtt", "==" + table_name + "==" + uk + "==" + each_row.toString());
                                                        dbSQLiteHelper.commonInsertUpdate(table_name, uk, each_row);

                                                    } catch (Exception e) {
                                                        Log.e("jobj_err5", "===" + e);
                                                        e.printStackTrace();
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("jobj_err4", "===" + e);
                                                    e.printStackTrace();
                                                }
                                            }


                                        } catch (Exception e) {
                                            Log.e("jobj_err3", "===" + e);
                                            e.printStackTrace();
                                        }

                                    }
                                } catch (Exception e) {
                                    Log.e("jobj_err2", "===" + e);
                                    e.printStackTrace();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        proceedNext(true);

                    } else {
                        DisplayUtils.showMessage(ScreenSplash.this, "Sync Failed");
                        proceedNext(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    proceedNext(false);

                }
            }

            @Override
            public void responseFailure(JSONObject response) {
                proceedNext(false);

            }

            @Override
            public void responseError(String message) {
                Log.e("cameonerr", "===" + message);
                proceedNext(false);

            }
        });
    }


    public void proceedNext(Boolean status) {
        if (status) {
            Intent intent = new Intent(ScreenSplash.this, ScreenHome.class);
            startActivity(intent);
            finish();
        } else {

            DisplayUtils.showMessageError(ScreenSplash.this, "Error occurred. Please try after some time.");
            finish();

        }

    }
}
