package leora.com.baseapp.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;

import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;

/**
 * Created by AZR on 26-11-2018.
 */

public class ScreenAuditFilter extends CustomAppCompatActivity {


    private int mYear, mMonth, mDay, mHour, mMinute;
    EditText rm_tv, audit_from_date_tv, audit_to_date_tv, audit_type_tv;
    LinearLayout submit_ly;

    String fl_rm_name;
    String fl_audit_date_from = "";
    String fl_audit_date_to = "";
    String fl_audit_type = "";
    String fl_rm_ref_id = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_rm_audit_filter);
        getIntents();
        initializeViews();
        setupValues();
        setupListeners();

    }


    public void getIntents() {

        fl_rm_name = getIntent().getStringExtra("fl_rm_name");
        fl_audit_date_from = getIntent().getStringExtra("fl_audit_date_from");
        fl_audit_date_to = getIntent().getStringExtra("fl_audit_date_to");
        fl_audit_type = getIntent().getStringExtra("fl_audit_type");
        fl_rm_ref_id = getIntent().getStringExtra("fl_rm_ref_id");

    }

    public void initializeViews() {

        rm_tv = (EditText) findViewById(R.id.rm_tv);
        audit_from_date_tv = (EditText) findViewById(R.id.audit_from_date_tv);
        audit_to_date_tv = (EditText) findViewById(R.id.audit_to_date_tv);
        audit_type_tv = (EditText) findViewById(R.id.audit_type_tv);
        submit_ly = findViewById(R.id.submit_ly);


    }

    public void setupValues() {

     rm_tv.setText(fl_rm_name);
     audit_from_date_tv.setText(fl_audit_date_from);
     audit_to_date_tv.setText(fl_audit_date_to);
     audit_type_tv.setText(fl_audit_type);

    }

    public void setupListeners() {

        rm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRm();
            }
        });

        audit_from_date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedDatePickerFrom();
            }
        });

        audit_to_date_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedDatePickerTo();
            }
        });

        submit_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResult();
            }
        });


    }


    public void getRm() {
        Intent i = new Intent(ScreenAuditFilter.this, ScreenRmSearch.class);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                fl_rm_ref_id = data.getStringExtra("rm_ref_id");
                fl_rm_name = data.getStringExtra("rm_name");
                Log.e("came_res", "aa===" + fl_rm_ref_id + "===" + fl_rm_name);

                rm_tv.setText(fl_rm_name + " ( " + fl_rm_ref_id + " )");


            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("came_res", "failure");
                //Write your code if there's no result
            }
        }
    }

    public void proceedDatePickerFrom() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(ScreenAuditFilter.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Log.e("rec_date", dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        fl_audit_date_from = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        audit_from_date_tv.setText(fl_audit_date_from);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    public void proceedDatePickerTo() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(ScreenAuditFilter.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Log.e("rec_date", dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                        fl_audit_date_to = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        audit_to_date_tv.setText(fl_audit_date_to );

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
        datePickerDialog.show();
    }

    public void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("fl_audit_date_from", fl_audit_date_from);
        returnIntent.putExtra("fl_audit_date_to", fl_audit_date_to);
        returnIntent.putExtra("fl_audit_type", fl_audit_type);
        returnIntent.putExtra("fl_rm_ref_id", fl_rm_ref_id);
        returnIntent.putExtra("fl_rm_name", fl_rm_name);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
