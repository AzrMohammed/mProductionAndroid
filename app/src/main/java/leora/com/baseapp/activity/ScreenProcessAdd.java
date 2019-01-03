package leora.com.baseapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.model.dbmodel.ProductionProcessModel;
import leora.com.baseapp.model.dbmodel.ProductionProcessModel;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;

/**
 * Created by AZR on 26-11-2018.
 */

public class ScreenProcessAdd extends CustomAppCompatActivity {


    LinearLayout submit_ly;
    EditText comment_tv, ref_id_et, name_et;
    String comment, ref_id, name;
    TextView title_tv;

    ProductionProcessModel productionProcessModel = new ProductionProcessModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_process_add);

        getIntents();
        initializeViews();
        setupValues();
        setupListeners();

    }


    public void getIntents()
    {

        try {
            productionProcessModel = (ProductionProcessModel) getIntent().getSerializableExtra("process_obj");

            if(productionProcessModel == null)
                productionProcessModel = new ProductionProcessModel();

            Log.e("pro_Add", "camtry"+productionProcessModel.ref_id);
        } catch (Exception e) {
            productionProcessModel = new ProductionProcessModel();
            Log.e("pro_Add", "camecatch");
            e.printStackTrace();
        }
    }

    public void initializeViews()
    {

        title_tv = findViewById(R.id.title_tv);
        name_et = findViewById(R.id.name_et);
        ref_id_et = findViewById(R.id.ref_id_et);
        comment_tv = findViewById(R.id.comment_tv);

        submit_ly = findViewById(R.id.submit_ly);
    }

    public void setupValues()
    {
        if(DataUtils.isStringValueExist(productionProcessModel.ref_id))
        {
            title_tv.setText("Update Process details");

            if(DataUtils.isStringValueExist(productionProcessModel.name))
            {
                name_et.setText(productionProcessModel.name);
                name_et.setSelection(name_et.getText().toString().length());
            }

            if(DataUtils.isStringValueExist(productionProcessModel.ref_id))
            {
                ref_id_et.setText(productionProcessModel.ref_id);
                ref_id_et.setSelection(ref_id_et.getText().toString().length());
            }


            if(DataUtils.isStringValueExist(productionProcessModel.comment))
            {
                comment_tv.setText(productionProcessModel.comment);
                comment_tv.setSelection(comment_tv.getText().toString().length());
            }


        }
        else
        {
            title_tv.setText("Add New Process");
        }
    }

    public void setupListeners()
    {

        submit_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateApiParams();
            }
        });
    }

    public void validateApiParams()
    {
        name = name_et.getText().toString();
        ref_id = productionProcessModel.ref_id;
        comment = comment_tv.getText().toString();

        if (DataUtils.isStringValueExist(ScreenProcessAdd.this, name, "Process Name", true))
//        if (DataUtils.isStringValueExist(ScreenProcessAdd.this, ref_id, "Process reference id", true))
            proceedAddRm();

    }

    public void proceedAddRm() {
        final String url = Constants.URL_ADD_PROCESS;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        if(DataUtils.isStringValueExist(ref_id))
        params.put("ref_id", ref_id);
        params.put("name", name);



        if(DataUtils.isStringValueExist(comment))
            params.put("comment", comment);


        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(ScreenProcessAdd.this, true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {

                        DisplayUtils.showMessage(ScreenProcessAdd.this, response.getString("status_message"));
                        finish();

                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void responseFailure(JSONObject response) {


            }

            @Override
            public void responseError(String message) {
                Log.e("cameonerr", "===" + message);
            }
        });
    }

}
