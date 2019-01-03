package leora.com.baseapp.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.activity.ScreenMachineProductivityAuditAdd;
import leora.com.baseapp.activity.ScreenMachineProductivityAuditFilter;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.model.dbmodel.MachineProductivityAuditModel;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.CalendarUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;
import leora.com.baseapp.utils.ViewUtils;

public class MachineProductivityAuditListFragment extends Fragment {

    private static final String TAG = "MachineProductivityAudi";

    RecyclerView recycler_view;
    DbSQLiteHelper dbSQLiteHelper;
    LinearLayout fab_ly;
    ArrayList<MachineProductivityAuditModel> machineProductivityAuditModel = new ArrayList<MachineProductivityAuditModel>();
    ArrayList<MachineProductivityAuditModel> filtered_lists = new ArrayList<MachineProductivityAuditModel>();
    RecyclerView.LayoutManager mLayoutManager;

    View filter_ly;
    boolean is_dialog_showing = false;
    RecyclerViewMaterial recyclerViewMaterial;

    String fl_machine_slug, fl_product_slug, fl_job_order_id;

    String fl_audit_date_from = "";
    String fl_audit_date_to = "";
    String fl_audit_time_from = "";
    String fl_audit_time_to = "";
    String fl_product_name = "";
    String fl_machine_name = "";
    TextView total_time_et, quantity_et;

    public static MachineProductivityAuditListFragment newInstance() {

        final MachineProductivityAuditListFragment fragment = new MachineProductivityAuditListFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_machine_audit_list, container, false);
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

        recycler_view = view.findViewById(R.id.recycler_view);

        fab_ly = view.findViewById(R.id.fab_ly);
        filter_ly = view.findViewById(R.id.filter_ly);

        quantity_et = view.findViewById(R.id.quantity_et);
        total_time_et = view.findViewById(R.id.total_time_et);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);


    }

    private void setupValues() {

        dbSQLiteHelper = CommonMethods.getDbModel(getActivity());
        proceedSampleRequest();
        recyclerViewMaterial = new RecyclerViewMaterial(machineProductivityAuditModel);

        ViewUtils.hideKeyboard(getActivity());


    }

    private void setupListeners() {


        filter_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedFilter();

            }
        });


        ;


        fab_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAddAlert(getActivity(),"","");
                Intent intent = new Intent(getActivity(), ScreenMachineProductivityAuditAdd.class);

                startActivity(intent);

            }
        });

    }


    public void proceedFilter() {
        Intent i = new Intent(getActivity(), ScreenMachineProductivityAuditFilter.class);

        i.putExtra("fl_product_name", fl_product_name);
        i.putExtra("fl_machine_name", fl_machine_name);
        i.putExtra("fl_job_order_id", fl_job_order_id);
        i.putExtra("fl_machine_slug", fl_machine_slug);
        i.putExtra("fl_product_slug", fl_product_slug);
        i.putExtra("fl_audit_time_from", fl_audit_time_from);
        i.putExtra("fl_audit_time_to", fl_audit_time_to);
        i.putExtra("fl_audit_date_from", fl_audit_date_from);
        i.putExtra("fl_audit_date_to", fl_audit_date_to);
        startActivityForResult(i, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == Activity.RESULT_OK) {
                Log.e("camer1", "stt");

                fl_machine_name = data.getStringExtra("fl_machine_name");
                fl_product_name = data.getStringExtra("fl_product_name");
                fl_job_order_id = data.getStringExtra("fl_job_order_id");
                fl_machine_slug = data.getStringExtra("fl_machine_slug");
                fl_product_slug = data.getStringExtra("fl_product_slug");
                fl_audit_date_from = data.getStringExtra("fl_audit_date_from");
                fl_audit_date_to = data.getStringExtra("fl_audit_date_to");
                fl_audit_time_from = data.getStringExtra("fl_audit_time_from");
                fl_audit_time_to = data.getStringExtra("fl_audit_time_to");

            }

            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("came_res", "failure");
                //Write your code if there's no result
            }

        }
    }


    public void processListData() {

        String work_from = "";

        if (DataUtils.isStringValueExist(fl_audit_date_from)) {
            work_from = fl_audit_date_from;
            if (DataUtils.isStringValueExist(fl_audit_time_from))
                work_from += " " + fl_audit_time_from;
        }

        String work_to = "";

        if (DataUtils.isStringValueExist(fl_audit_date_to)) {
            work_to = fl_audit_date_to;
            if (DataUtils.isStringValueExist(fl_audit_time_to))
                work_to += " " + fl_audit_time_to;
        }


        machineProductivityAuditModel = dbSQLiteHelper.getMachineProductivityAuditModels(fl_machine_slug, fl_product_slug, fl_job_order_id, work_from, work_to);
        setHeaderValues(machineProductivityAuditModel);
        Log.e("array_size ", machineProductivityAuditModel.size() + "===" + dbSQLiteHelper.getMaterialAuditModels().size());

        recycler_view.setAdapter(new RecyclerViewMaterial(machineProductivityAuditModel));

    }


    public void setHeaderValues(ArrayList<MachineProductivityAuditModel> machineProductivityAuditModel) {

        int total_count = 0;
        long total_time = 0;
        for (MachineProductivityAuditModel each_model :
                machineProductivityAuditModel) {
            total_count = total_count + Integer.parseInt(each_model.quantity_approved);
            total_time = total_time + Long.parseLong(printDifference(each_model.work_done_from_time, each_model.work_done_to_time));
        }


        quantity_et.setText("" + total_count);
        total_time_et.setText(total_time + " mins");

    }

    public class RecyclerViewMaterial extends RecyclerView.Adapter<RecyclerViewHolder> {

        ArrayList<MachineProductivityAuditModel> machineProductivityAuditModel_final = new ArrayList<MachineProductivityAuditModel>();


        public RecyclerViewMaterial(ArrayList<MachineProductivityAuditModel> machineProductivityAuditModel) {
            this.machineProductivityAuditModel_final = machineProductivityAuditModel;

        }


        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_machine_productivity_audit, parent, false);
            RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

            final MachineProductivityAuditModel materialModel = machineProductivityAuditModel_final.get(position);

            holder.product_name_et.setText(materialModel.productModel.name + "( " + materialModel.productModel.ref_id + " ) ");
            holder.machine_name_et.setText(materialModel.pMachineModel.name + "( " + materialModel.pMachineModel.ref_id + " )");

            holder.job_order_id_et.setText(materialModel.job_order_id);

            holder.audit_date_from_et.setText(CalendarUtils.getDisplayDateFrom(materialModel.work_done_from_time, CalendarUtils.tfs_yeardatetime_server));
            holder.audit_date_to_et.setText(materialModel.work_done_to_time);


            holder.approved_et.setText(materialModel.quantity_approved);
            holder.rejected_et.setText(materialModel.quantity_rejected);
            holder.rework_et.setText(materialModel.quantity_rework);

            holder.total_time_et.setText(printDifference(materialModel.work_done_from_time, materialModel.work_done_to_time) + " mins");
            Log.e(TAG, "onBindViewHolder: ");

            if (DataUtils.isStringValueExist(materialModel.process_description)) {
                holder.process_description_et.setText(materialModel.process_description);
                holder.process_description_et.setVisibility(View.VISIBLE);
            } else {
                holder.process_description_et.setVisibility(View.GONE);
            }

            if (DataUtils.isStringValueExist(materialModel.process_description)) {
                holder.process_description_et.setText(materialModel.process_description);
                holder.process_description_et.setVisibility(View.VISIBLE);
            } else {
                holder.process_description_et.setVisibility(View.GONE);
            }


        }

        @Override
        public int getItemCount() {
            return machineProductivityAuditModel_final.size();
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView comment_tv, total_time_et, process_description_et, job_order_id_et, product_name_et, machine_name_et, audit_date_from_et, audit_date_to_et, approved_et, rejected_et, rework_et;
        TextView reject;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            audit_date_to_et = itemView.findViewById(R.id.audit_date_to_et);
            audit_date_from_et = itemView.findViewById(R.id.audit_date_from_et);
            process_description_et = itemView.findViewById(R.id.process_description_et);
            machine_name_et = itemView.findViewById(R.id.machine_name_et);
            product_name_et = itemView.findViewById(R.id.product_name_et);
            product_name_et = itemView.findViewById(R.id.product_name_et);
            approved_et = itemView.findViewById(R.id.approved_et);
            rejected_et = itemView.findViewById(R.id.rejected_et);
            rework_et = itemView.findViewById(R.id.rework_et);
            comment_tv = itemView.findViewById(R.id.comment_tv);
            job_order_id_et = itemView.findViewById(R.id.job_order_id_et);
            total_time_et = itemView.findViewById(R.id.total_time_et);
            reject = itemView.findViewById(R.id.reject);

        }

    }


    public void proceedSampleRequest() {
        final String url = Constants.URL_SYNC_DATA;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("param_key1", "param_val1");

        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(getActivity(), true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
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


                                            processListData();

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


                    } else {
                        DisplayUtils.showMessage(getActivity(), "Sync Failed");
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

    @Override
    public void onResume() {
        super.onResume();
        proceedSampleRequest();
    }

    public void addDataRequest(String name, String ref_id, final AlertDialog alertDialog) {
        final String url = Constants.URL_ADD_RM;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("ref_id", ref_id);
        params.put("name", name);

        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(getActivity(), true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {

                        proceedSampleRequest();

                        alertDialog.cancel();

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


    public void showAddAlert(Activity activity, String upadte_name, String upadte_id) {
        if (!is_dialog_showing) {
            is_dialog_showing = true;

            {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.secondary_card_dialog, null);
                dialogBuilder.setView(dialogView);

                final EditText add_ref_id_et = dialogView.findViewById(R.id.add_ref_id_et);
                final EditText add_name_et = dialogView.findViewById(R.id.add_name_et);


                TextView add_tv = dialogView.findViewById(R.id.add_tv);

                add_ref_id_et.setText(upadte_id);
                add_name_et.setText(upadte_name);


                final AlertDialog alertDialog = dialogBuilder.create();

                add_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String name_et = add_name_et.getText().toString().trim();
                        String ref_id_et = add_ref_id_et.getText().toString().trim();


                        if (DataUtils.isStringValueExist(name_et) && DataUtils.isStringValueExist(ref_id_et)) {
                            addDataRequest(name_et, ref_id_et, alertDialog);
                        } else {
                            DisplayUtils.showMessage(getActivity(), "Pls Enter Details");
                        }


                    }
                });


                alertDialog.getWindow().

                        setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


                alertDialog.setCancelable(true);

                alertDialog.getWindow().

                        setSoftInputMode(
                                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                alertDialog.show();
                alertDialog.getWindow().

                        setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()

                {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK &&
                                event.getAction() == KeyEvent.ACTION_UP &&
                                !event.isCanceled()) {
                            alertDialog.cancel();
                            is_dialog_showing = false;
                            return true;
                        }
                        return false;
                    }
                });

                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()

                {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        is_dialog_showing = false;
                    }
                });
            }

        }
    }


    public String printDifference(String startDate, String endDate) {

        String dayDifference = "";

        try {

            Date date1, date2;


            SimpleDateFormat dates = CalendarUtils.getSimpleDateFormat(CalendarUtils.tfs_yeardatetime_server);

            //Setting dates
            date1 = dates.parse(startDate);
            date2 = dates.parse(endDate);

            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            Log.e("printDifference: ", difference + "====");
            long differenceDates = ((difference / 1000) / 60);

            //Convert long to String
            dayDifference = Long.toString(differenceDates);

            Log.e("HERE", "HERE: " + dayDifference);

        } catch (Exception exception) {

            Log.e("DIDN'T WORK", "exception " + exception);
            dayDifference = "Error";
        }
        return dayDifference;
    }
}
