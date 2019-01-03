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

import java.util.Calendar;
import java.util.Date;

import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;

/**
 * Created by AZR on 26-11-2018.
 */

public class ScreenMachineProductivityAuditFilter extends CustomAppCompatActivity {

    public int mYear_f, mMonth_f, mDay_f, mHour_f, mMinute_f;

    private int mYear, mMonth, mDay, mHour, mMinute;
    LinearLayout submit_ly;

    String fl_product_name = "";
    String fl_machine_name = "";
    String fl_job_order_id = "";
    String fl_machine_slug = "";
    String fl_product_slug = "";
    String fl_audit_date_from = "";
    String fl_audit_date_to = "";
    String fl_audit_time_from = "";
    String fl_audit_time_to = "";

    EditText product_name_et, machine_name_et, audit_time_from_et, audit_date_from_et, job_order_id_et, audit_time_to_et, audit_date_to_et;
//    String product_name, product_slug, machine_name, machine_slug, audit_time_from, audit_date_from, process_description, job_order_id, audit_time_to, audit_date_to;


    final public static int REQ_CODE_PRODUCT = 1, REQ_CODE_MACHINE = 2;
    final public static int CAL_TYPE_FROM_TIME = 1, CAL_TYPE_TO_TIME = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_productivity_audit_filter);
        getIntents();
        initializeViews();
        setupValues();
        setupListeners();

    }


    public void getIntents() {

        fl_product_name = getIntent().getStringExtra("fl_product_name");
        fl_machine_name = getIntent().getStringExtra("fl_machine_name");
        fl_job_order_id = getIntent().getStringExtra("fl_job_order_id");
        fl_machine_slug = getIntent().getStringExtra("fl_machine_slug");
        fl_product_slug = getIntent().getStringExtra("fl_product_slug");
        fl_audit_date_from = getIntent().getStringExtra("fl_audit_date_from");
        fl_audit_date_to = getIntent().getStringExtra("fl_audit_date_to");
        fl_audit_time_from = getIntent().getStringExtra("fl_audit_time_from");
        fl_audit_time_to = getIntent().getStringExtra("fl_audit_time_to");


    }

    public void initializeViews() {


        submit_ly = findViewById(R.id.submit_ly);


        product_name_et = (EditText) findViewById(R.id.product_name_et);
        machine_name_et = (EditText) findViewById(R.id.machine_name_et);
        audit_time_from_et = (EditText) findViewById(R.id.audit_time_from_et);
        audit_date_from_et = (EditText) findViewById(R.id.audit_date_from_et);

        job_order_id_et = (EditText) findViewById(R.id.job_order_id_et);
        audit_time_to_et = (EditText) findViewById(R.id.audit_time_to_et);
        audit_date_to_et = (EditText) findViewById(R.id.audit_date_to_et);


    }

    public void setupValues() {

        product_name_et.setText(fl_product_name);
        machine_name_et.setText(fl_machine_name);
        job_order_id_et.setText(fl_job_order_id);
        audit_time_from_et.setText(fl_audit_time_from);
        audit_date_from_et.setText(fl_audit_date_from);
        audit_time_to_et.setText(fl_audit_time_to);
        audit_date_to_et.setText(fl_audit_date_to);


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
                returnResult();
            }
        });


    }


    public void getProduct() {
        Intent i = new Intent(ScreenMachineProductivityAuditFilter.this, ScreenProductSearch.class);
        startActivityForResult(i, REQ_CODE_PRODUCT);
    }

    public void getMachine() {
        Intent i = new Intent(ScreenMachineProductivityAuditFilter.this, ScreenMachineSearch.class);
        startActivityForResult(i, REQ_CODE_MACHINE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_CODE_PRODUCT) {


                Log.e("recc_S", "===" + data.getStringExtra("product_slug") + "==" + data.getStringExtra("product_name"));

                fl_product_name = data.getStringExtra("product_name");
                fl_product_slug = data.getStringExtra("product_slug");

                product_name_et.setText(fl_product_name);

            } else if (requestCode == REQ_CODE_MACHINE) {

                Log.e("recc_S_ee", "===" + data.getStringExtra("machine_slug") + "==" + data.getStringExtra("machine_name"));
                fl_machine_name = data.getStringExtra("machine_name");
                fl_machine_slug = data.getStringExtra("machine_slug");

                machine_name_et.setText(fl_machine_name);

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


        DatePickerDialog datePickerDialog = new DatePickerDialog(ScreenMachineProductivityAuditFilter.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
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
                                fl_audit_date_from = date_s;
                                audit_date_from_et.setText(fl_audit_date_from);
                            }
                            break;
                            case CAL_TYPE_TO_TIME: {
                                fl_audit_date_to = date_s ;
                                audit_date_to_et.setText(fl_audit_date_to);
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(ScreenMachineProductivityAuditFilter.this,
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
                                fl_audit_time_from = time_s;
                                audit_time_from_et.setText(fl_audit_time_from);

                            }
                            break;
                            case CAL_TYPE_TO_TIME: {
                                fl_audit_time_to = time_s;
                                audit_time_to_et.setText(fl_audit_time_to);
                            }
                            break;

                        }


                    }
                }, mHour_f, mMinute_f, false);
        timePickerDialog.show();
    }


    public void returnResult() {


        fl_job_order_id = job_order_id_et.getText().toString();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("fl_product_name", fl_product_name);
        returnIntent.putExtra("fl_machine_name", fl_machine_name);
        returnIntent.putExtra("fl_job_order_id", fl_job_order_id);
        returnIntent.putExtra("fl_machine_slug", fl_machine_slug);
        returnIntent.putExtra("fl_product_slug", fl_product_slug);
        returnIntent.putExtra("fl_audit_time_from", fl_audit_time_from);
        returnIntent.putExtra("fl_audit_time_to", fl_audit_time_to);
        returnIntent.putExtra("fl_audit_date_from", fl_audit_date_from);
        returnIntent.putExtra("fl_audit_date_to", fl_audit_date_to);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
