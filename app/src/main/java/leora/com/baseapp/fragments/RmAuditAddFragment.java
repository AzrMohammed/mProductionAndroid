package leora.com.baseapp.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.android.volley.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.activity.ScreenRmAuditAddBase;
import leora.com.baseapp.activity.ScreenRmAuditBase;
import leora.com.baseapp.activity.ScreenRmSearch;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;

public class RmAuditAddFragment extends android.support.v4.app.Fragment {


    EditText rm_tv, metric_count_tv, supplier_name_tv, comment_tv, audit_time_tv, audit_date_tv;
    String metric_count, supplier_name, comment, audit_date, audit_time;

    DbSQLiteHelper dbSQLiteHelper;
    String rm_ref_id, rm_name;

    LinearLayout submit_ly;

    private int mYear, mMonth, mDay, mHour, mMinute;

    public static RmAuditAddFragment newInstance() {
        final RmAuditAddFragment fragment = new RmAuditAddFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rn_audit_add, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeView(view);
        setupValues();
        setupListeners();
    }

    private void initializeView(View view) {
        rm_tv = (EditText) view.findViewById(R.id.rm_tv);
        comment_tv = (EditText) view.findViewById(R.id.comment_tv);
        supplier_name_tv = (EditText) view.findViewById(R.id.supplier_name_tv);
        metric_count_tv = (EditText) view.findViewById(R.id.metric_count_tv);

        audit_time_tv = (EditText) view.findViewById(R.id.audit_time_tv);
        audit_date_tv = (EditText) view.findViewById(R.id.audit_date_tv);
        submit_ly = view.findViewById(R.id.submit_ly);


    }

    private void setupValues() {
        dbSQLiteHelper = CommonMethods.getDbModel(getActivity());


    }

    private void setupListeners() {
        rm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRm();
            }
        });

        submit_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateParams();
            }
        });

        audit_date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedDatePicker();
            }
        });

        audit_time_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedTimePicker();
            }
        });

    }

    public void getRm() {
        Intent i = new Intent(getActivity(), ScreenRmSearch.class);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                rm_ref_id = data.getStringExtra("rm_ref_id");
                rm_name = data.getStringExtra("rm_name");
                Log.e("came_res", "aa===" + rm_ref_id + "===" + rm_name);

                rm_tv.setText(rm_name + " ( " + rm_ref_id + " )");


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("came_res", "failure");
                //Write your code if there's no result
            }
        }
    }


    public void proceedDatePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Log.e("rec_date", dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        audit_date_tv.setText(year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth );

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    public void proceedTimePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        Log.e("rec_mmm", hourOfDay + ":" + minute);

                        audit_time_tv.setText(hourOfDay + ":" + minute);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    public void validateParams() {
        metric_count = metric_count_tv.getText().toString();
        supplier_name = supplier_name_tv.getText().toString();
        comment = comment_tv.getText().toString();
        audit_date = audit_date_tv.getText().toString();
        audit_time = audit_time_tv.getText().toString();

        if (DataUtils.isStringValueExist(getActivity(), rm_name, "Raw material Name", true))
            if (DataUtils.isStringValueExist(getActivity(), metric_count, "Metric count", true))
                if (DataUtils.isStringValueExist(getActivity(), audit_date, "Audit Date", true))
                    if (DataUtils.isStringValueExist(getActivity(), audit_time, "Audit Time", true))

                        proceedAddApi();

    }


    public void proceedAddApi() {
        final String url = Constants.URL_ADD_RM_AUDIT;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("param_key1", "param_val1");

        params.put("raw_material_ref_id", rm_ref_id);
        params.put("metric_count", metric_count_tv.getText().toString());
        params.put("audit_type", "" + ((ScreenRmAuditAddBase) getActivity()).audit_type);
        params.put("audit_date", audit_date + " " +audit_time+":00");

        if(DataUtils.isStringValueExist(comment))
        params.put("comment", comment);

        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(getActivity(), true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {
                        getActivity().finish();
                    } else {
                        DisplayUtils.showMessage(getActivity(), "Error occurred. Try after some time.");
                    }

                } catch (Exception e) {

                    Log.e("get_rm_audits_Ex", "===" + e);

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
