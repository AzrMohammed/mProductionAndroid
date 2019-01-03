package leora.com.baseapp.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import com.android.volley.Request;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;

/**
 * Created by AZR on 26-11-2018.
 */

public class ScreenMachineProductivityAuditAdd extends CustomAppCompatActivity {


    public int mYear_f, mMonth_f, mDay_f, mHour_f, mMinute_f;
    public int mYear_t, mMonth_t, mDay_t, mHour_t, mMinute_t;


    LinearLayout submit_ly;

    EditText product_name_et, machine_name_et, audit_time_from_et, audit_date_from_et, process_description_et, job_order_id_et, quantity_rejected_et, quantity_rework_et, quantity_approved_et, audit_time_to_et, audit_date_to_et, comment_et, supervisor_name_et, operator_name_et;
    String product_name, product_slug, machine_name, machine_slug, audit_time_from, audit_date_from, process_description, job_order_id, quantity_rejected, quantity_rework, quantity_approved, audit_time_to, audit_date_to, comment, supervisor_name, operator_name;

    //    String audti_from_time = "", audit_to_time= "";
    final public static int REQ_CODE_PRODUCT = 1, REQ_CODE_MACHINE = 2;
    final public static int CAL_TYPE_FROM_TIME = 1, CAL_TYPE_TO_TIME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_machine_audit_add);
        getIntents();
        initializeViews();
        setupValues();
        setupListeners();


    }


    public void getIntents() {


    }

    public void initializeViews() {

        product_name_et = (EditText) findViewById(R.id.product_name_et);
        machine_name_et = (EditText) findViewById(R.id.machine_name_et);
        audit_time_from_et = (EditText) findViewById(R.id.audit_time_from_et);
        audit_date_from_et = (EditText) findViewById(R.id.audit_date_from_et);
        process_description_et = (EditText) findViewById(R.id.process_description_et);
        job_order_id_et = (EditText) findViewById(R.id.job_order_id_et);
        quantity_rejected_et = (EditText) findViewById(R.id.quantity_rejected_et);
        quantity_rework_et = (EditText) findViewById(R.id.quantity_rework_et);
        quantity_approved_et = (EditText) findViewById(R.id.quantity_approved_et);
        audit_time_to_et = (EditText) findViewById(R.id.audit_time_to_et);
        audit_date_to_et = (EditText) findViewById(R.id.audit_date_to_et);
        comment_et = (EditText) findViewById(R.id.comment_et);
        supervisor_name_et = (EditText) findViewById(R.id.supervisor_name_et);
        operator_name_et = (EditText) findViewById(R.id.operator_name_et);

        submit_ly = findViewById(R.id.submit_ly);


    }

    public void setupValues() {


    }

    public void setupListeners() {

        product_name_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProduct();
            }
        });

        machine_name_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMachine();
            }
        });


        audit_time_from_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        audit_date_from_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proceedDatePickerFrom(CAL_TYPE_FROM_TIME);
            }
        });

        audit_date_to_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                proceedDatePickerFrom(CAL_TYPE_TO_TIME);
            }
        });

        audit_time_from_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedTimePicker(CAL_TYPE_FROM_TIME);

            }
        });

        audit_time_to_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedTimePicker(CAL_TYPE_TO_TIME);
            }
        });


        submit_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateParams();
            }
        });


    }

    public void validateParams() {

        product_name = product_name_et.getText().toString();
        machine_name = machine_name_et.getText().toString();
        audit_time_from = audit_time_from_et.getText().toString();
        audit_date_from = audit_date_from_et.getText().toString();
        process_description = process_description_et.getText().toString();
        job_order_id = job_order_id_et.getText().toString();
        quantity_rejected = quantity_rejected_et.getText().toString();
        quantity_rework = quantity_rework_et.getText().toString();
        quantity_approved = quantity_approved_et.getText().toString();
        audit_time_to = audit_time_to_et.getText().toString();
        audit_date_to = audit_date_to_et.getText().toString();
        comment = comment_et.getText().toString();
        supervisor_name = supervisor_name_et.getText().toString();
        operator_name = operator_name_et.getText().toString();


        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, product_slug, "Product name", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, machine_name, "Machine name", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, audit_date_from, "Audit from date", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, audit_time_from, "Audit from time", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, audit_date_to, "Audit to date", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, audit_time_to, "Audit to time", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, audit_time_to, "Process Description", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, job_order_id, "Job Order Id", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, quantity_approved, "Quantity Approved", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, quantity_rejected, "Quantity Rejected", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, quantity_rework, "Quantity Rework", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, supervisor_name, "Supervisor Name", true))
        if (DataUtils.isStringValueExist(ScreenMachineProductivityAuditAdd.this, operator_name, "Operator Name", true))
        proceedAddMachineAudit();
    }


    public void getProduct() {
        Intent i = new Intent(ScreenMachineProductivityAuditAdd.this, ScreenProductSearch.class);
        startActivityForResult(i, REQ_CODE_PRODUCT);
    }

    public void getMachine() {
        Intent i = new Intent(ScreenMachineProductivityAuditAdd.this, ScreenMachineSearch.class);
        startActivityForResult(i, REQ_CODE_MACHINE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_PRODUCT) {


                Log.e("recc_S", "===" + data.getStringExtra("product_slug") + "==" + data.getStringExtra("product_name"));

                product_name = data.getStringExtra("product_name");
                product_slug = data.getStringExtra("product_slug");

                product_name_et.setText(product_name);

            } else if (requestCode == REQ_CODE_MACHINE) {

                Log.e("recc_S_ee", "===" + data.getStringExtra("machine_slug") + "==" + data.getStringExtra("machine_name"));
                machine_name = data.getStringExtra("machine_name");
                machine_slug = data.getStringExtra("machine_slug");

                machine_name_et.setText(machine_name);

            }


        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("came_res_product", "failure");
            //Write your code if there's no result
        }
    }

    public void proceedDatePickerFrom(final int TYPE) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear_f = c.get(Calendar.YEAR);
        mMonth_f = c.get(Calendar.MONTH);
        mDay_f = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(ScreenMachineProductivityAuditAdd.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Log.e("rec_date", dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        String dayOfMonth_s = ""+ dayOfMonth;
                        String monthOfYear_s = ""+ monthOfYear;

                        if(dayOfMonth <10)
                            dayOfMonth_s = "0"+dayOfMonth;

                        if(monthOfYear <10)
                            monthOfYear_s = "0"+monthOfYear;


                        String date_s = year + "-" + monthOfYear_s + "-" + dayOfMonth_s;


                        switch (TYPE) {

                            case CAL_TYPE_FROM_TIME: {
                                audit_date_from = date_s;
                                audit_date_from_et.setText(audit_date_from);
                            }
                            break;
                            case CAL_TYPE_TO_TIME: {
                                audit_date_to = date_s;
                                audit_date_to_et.setText(audit_date_to);
                            }
                            break;

                        }
                        proceedTimePicker(TYPE);

                    }
                }, mYear_f, mMonth_f, mDay_f);

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }


    public void proceedTimePicker(final int TYPE) {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour_f = c.get(Calendar.HOUR_OF_DAY);
        mMinute_f = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(ScreenMachineProductivityAuditAdd.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        Log.e("rec_mmm", hourOfDay + ":" + minute);

                        String hourOfDay_s = hourOfDay+"";
                        if(hourOfDay <10)
                            hourOfDay_s = "0"+hourOfDay;

                        String minute_s = minute+"";
                        if(minute <10)
                            minute_s = "0"+minute;


                        String time_s = hourOfDay_s+":"+minute_s;
                        switch (TYPE) {

                            case CAL_TYPE_FROM_TIME: {
                                audit_time_from = time_s;
                                audit_time_from_et.setText(audit_time_from);

                            }
                            break;
                            case CAL_TYPE_TO_TIME: {
                                audit_time_to = time_s;
                                audit_time_to_et.setText(audit_time_to);
                            }
                            break;

                        }


                    }
                }, mHour_f, mMinute_f, false);
        timePickerDialog.show();
    }


    public void proceedAddMachineAudit() {
        final String url = Constants.URL_ADD_MACHINE_PRODUCTIVITY_AUDIT;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();

        params.put("product_slug", product_slug);
        params.put("machine_slug", machine_slug);


        params.put("work_done_from_time", audit_date_from + " " + audit_time_from);
        params.put("work_done_to_time", audit_date_to + " " + audit_time_to);
        params.put("job_order_id", job_order_id);
        params.put("quantity_rejected", quantity_rejected);
        params.put("quantity_rework", quantity_rework);
        params.put("quantity_approved", quantity_approved);
        params.put("supervisor_name", supervisor_name);
        params.put("operator_name", operator_name);


        if (DataUtils.isStringValueExist(process_description))
            params.put("process_description", process_description);

        if (DataUtils.isStringValueExist(comment))
            params.put("comment", comment);


        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);

        Log.e("final_req_dat", "===" + request_obj);

        new CustomJsonObjectRequest(ScreenMachineProductivityAuditAdd.this, true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {

                        DisplayUtils.showMessage(ScreenMachineProductivityAuditAdd.this, response.getString("status_message"));
                        finish();

                    } else {
                        DisplayUtils.showMessage(ScreenMachineProductivityAuditAdd.this, response.getString("status_message"));
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
