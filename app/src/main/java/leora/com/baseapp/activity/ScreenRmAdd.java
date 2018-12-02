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
import leora.com.baseapp.model.dbmodel.RawMaterialModel;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;

/**
 * Created by AZR on 26-11-2018.
 */

public class ScreenRmAdd extends CustomAppCompatActivity {


    LinearLayout submit_ly;
    EditText comment_tv, length_et, ref_id_et, name_et;
    String comment, length, ref_id, name;
    TextView title_tv;

    RawMaterialModel rawMaterialModel = new RawMaterialModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rm_add);

        getIntents();
        initializeViews();
        setupValues();
        setupListeners();

    }


    public void getIntents()
    {

        try {
            rawMaterialModel = (RawMaterialModel) getIntent().getSerializableExtra("rm_obj");
        } catch (Exception e) {
            rawMaterialModel = new RawMaterialModel();
            e.printStackTrace();
        }
    }

    public void initializeViews()
    {

        title_tv = findViewById(R.id.title_tv);
        name_et = findViewById(R.id.name_et);
        ref_id_et = findViewById(R.id.ref_id_et);
        length_et = findViewById(R.id.length_et);
        comment_tv = findViewById(R.id.comment_tv);

        submit_ly = findViewById(R.id.submit_ly);
    }

    public void setupValues()
    {
        if(DataUtils.isStringValueExist(rawMaterialModel.ref_id))
        {
            title_tv.setText("Update Raw Material");

            if(DataUtils.isStringValueExist(rawMaterialModel.name))
            {
                name_et.setText(rawMaterialModel.name);
                name_et.setSelection(name_et.getText().toString().length());
            }

            if(DataUtils.isStringValueExist(rawMaterialModel.ref_id))
            {
                ref_id_et.setText(rawMaterialModel.ref_id);
                ref_id_et.setSelection(ref_id_et.getText().toString().length());
            }

            if(DataUtils.isStringValueExist(rawMaterialModel.length))
            {
                length_et.setText(rawMaterialModel.length);
                length_et.setSelection(length_et.getText().toString().length());
            }

            if(DataUtils.isStringValueExist(rawMaterialModel.comment))
            {
                comment_tv.setText(rawMaterialModel.comment);
                comment_tv.setSelection(comment_tv.getText().toString().length());
            }


        }
        else
        {
            title_tv.setText("Add Raw Material");
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
        ref_id = ref_id_et.getText().toString();
        length = length_et.getText().toString();
        comment = comment_tv.getText().toString();

        if (DataUtils.isStringValueExist(ScreenRmAdd.this, name, "Raw material Name", true))
        if (DataUtils.isStringValueExist(ScreenRmAdd.this, ref_id, "Raw material reference id", true))
            proceedAddRm();

    }

    public void proceedAddRm() {
        final String url = Constants.URL_ADD_RM;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("ref_id", ref_id);
        params.put("name", name);

        if(DataUtils.isStringValueExist(length))
            params.put("length", length);


        if(DataUtils.isStringValueExist(comment))
            params.put("comment", comment);


        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(ScreenRmAdd.this, true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {

                        DisplayUtils.showMessage(ScreenRmAdd.this, "Raw material added successfully.");
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
