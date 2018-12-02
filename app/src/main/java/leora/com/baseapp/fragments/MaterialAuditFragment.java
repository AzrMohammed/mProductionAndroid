package leora.com.baseapp.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.activity.ScreenAuditFilter;
import leora.com.baseapp.activity.ScreenRmAuditAddBase;
import leora.com.baseapp.activity.ScreenRmAuditBase;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.model.apimodel.MaterialAuditModel;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;
import leora.com.baseapp.utils.ValueUtils;

public class MaterialAuditFragment extends android.support.v4.app.Fragment {

    RecyclerView recycler_view;
    DbSQLiteHelper dbSQLiteHelper;
    LinearLayout fab_ly;
    ArrayList<MaterialAuditModel> materialAuditModels = new ArrayList<MaterialAuditModel>();
    ArrayList<MaterialAuditModel> filtered_lists = new ArrayList<MaterialAuditModel>();
    RecyclerView.LayoutManager mLayoutManager;

    boolean is_dialog_showing = false;
    FrameLayout filter_ly, add_ly;
    RecyclerViewMaterial recyclerViewMaterial;


    String fl_audit_date_from = "";
    String fl_audit_date_to = "";
    String fl_audit_type = "";
    String fl_rm_ref_id = "";
    String fl_rm_name = "";

    ImageView back_press_iv;
    TextView screen_name_tv;

    public static MaterialAuditFragment newInstance() {

        final MaterialAuditFragment fragment = new MaterialAuditFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rm_audit, container, false);
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
        add_ly = view.findViewById(R.id.add_ly);

        screen_name_tv = view.findViewById(R.id.screen_name_tv);
        back_press_iv = view.findViewById(R.id.back_press_iv);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);


    }

    private void setupValues() {
        dbSQLiteHelper = CommonMethods.getDbModel(getActivity());
//        proceedSyncData();
        getRmAudits();
        recyclerViewMaterial = new RecyclerViewMaterial(materialAuditModels);

        screen_name_tv.setText("Raw Material Audits");

    }

    private void setupListeners() {

        back_press_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        add_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAlert(getActivity());

            }
        });

        filter_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedFilter();

            }
        });


    }


    public class RecyclerViewMaterial extends RecyclerView.Adapter<RecyclerViewHolder> {

        ArrayList<MaterialAuditModel> materialAuditModels_final = new ArrayList<MaterialAuditModel>();


        public RecyclerViewMaterial(ArrayList<MaterialAuditModel> materialAuditModels) {
            this.materialAuditModels_final = materialAuditModels;

        }


        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_raw_material_audit, null);
            RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

            final MaterialAuditModel materialAuditModel = materialAuditModels_final.get(position);

            holder.material_name_tv.setText(materialAuditModel.raw_material_name);
            holder.audit_date_tv.setText(materialAuditModel.audit_date);
            holder.material_ref_id_tv.setText(materialAuditModel.raw_material_ref_id);
            holder.metric_count_tv.setText(materialAuditModel.metric_count);
            holder.audit_type_tv.setText(Constants.getAuditName(materialAuditModel.audit_type));
            holder.updated_date_tv.setText((materialAuditModel.updated_on));
            if (DataUtils.isStringValueExist(materialAuditModel.comment)) {
                holder.comment_tv.setText(materialAuditModel.comment);
                holder.comment_tv.setVisibility(View.VISIBLE);
            } else
                holder.comment_tv.setVisibility(View.GONE);


        }

        @Override
        public int getItemCount() {
            return materialAuditModels_final.size();
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView material_ref_id_tv, material_name_tv, updated_date_tv, audit_date_tv, metric_count_tv, audit_type_tv, comment_tv;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            material_ref_id_tv = itemView.findViewById(R.id.material_ref_id_tv);
            material_name_tv = itemView.findViewById(R.id.material_name_tv);
            audit_date_tv = itemView.findViewById(R.id.audit_date_tv);
            audit_type_tv = itemView.findViewById(R.id.audit_type_tv);
            updated_date_tv = itemView.findViewById(R.id.updated_date_tv);
            metric_count_tv = itemView.findViewById(R.id.metric_count_tv);
            comment_tv = itemView.findViewById(R.id.comment_tv);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        getRmAudits();
    }


    public void proceedFilter() {
        Intent i = new Intent(getActivity(), ScreenAuditFilter.class);
        i.putExtra("fl_audit_date_from", fl_audit_date_from);
        i.putExtra("fl_audit_date_to", fl_audit_date_to);
        i.putExtra("fl_audit_type", fl_audit_type);
        i.putExtra("fl_rm_ref_id", fl_rm_ref_id);
        i.putExtra("fl_rm_name", fl_rm_name);
        startActivityForResult(i, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Log.e("camer1", "stt");

                fl_audit_date_from = data.getStringExtra("fl_audit_date_from");
                fl_audit_date_to = data.getStringExtra("fl_audit_date_to");
                fl_audit_type = data.getStringExtra("fl_audit_type");
                fl_rm_ref_id = data.getStringExtra("fl_rm_ref_id");
                fl_rm_name = data.getStringExtra("fl_rm_name");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e("came_res", "failure");
                //Write your code if there's no result
            }
        }
    }

    public void getRmAudits() {
        Log.e("camer2", "stt");

        final String url = Constants.URL_GET_RM_AUDITS;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put("param_key1", "param_val1");

        if (DataUtils.isStringValueExist(fl_audit_type))
            if (fl_audit_type.equals("" + ValueUtils.NO_VALUE_INT))
                params.put("audit_type", fl_audit_type);

        if (DataUtils.isStringValueExist(fl_rm_ref_id))
            params.put("rm_ref_id", fl_rm_ref_id);

        if (DataUtils.isStringValueExist(fl_audit_date_from))
            params.put("audit_date_from", fl_audit_date_from);

        if (DataUtils.isStringValueExist(fl_audit_date_to))
            params.put("audit_date_to", fl_audit_date_to);



        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonObjectRequest(getActivity(), true, Request.Method.POST, url, request_obj, new CustomResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {

                        JSONArray values = response.getJSONArray("values");

                        List<MaterialAuditModel> contacts;
                        Type listType = new TypeToken<List<MaterialAuditModel>>() {
                        }.getType();

                        materialAuditModels = new Gson().fromJson(values.toString(), listType);
//
                        Log.e("fin_vww", "==" + materialAuditModels.size() + "==");
                        recycler_view.setAdapter(new RecyclerViewMaterial(materialAuditModels));

//
//                        Gson gson = new GsonBuilder().serializeNulls().create();
//                        materialAuditModels = gson.fromJson(response.toString(), MaterialAuditModel.class);


                    } else {
                        DisplayUtils.showMessage(getActivity(), "Sync Failed");
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


    public void showAddAlert(Activity activity) {
//        if (!is_dialog_showing) {
//            is_dialog_showing = true;

        {
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_add_audit, null);
            dialogBuilder.setView(dialogView);

            final LinearLayout for_rm_store = dialogView.findViewById(R.id.for_rm_store);
            final LinearLayout for_production_planning = dialogView.findViewById(R.id.for_production_planning);
            final LinearLayout for_production_store = dialogView.findViewById(R.id.for_production_store);
            final LinearLayout for_rejection_store = dialogView.findViewById(R.id.for_rejection_store);


            final AlertDialog alertDialog = dialogBuilder.create();

            for_rm_store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    gotToAddAuditScreen(Constants.RM_AUDIT_IN);
                    alertDialog.cancel();


                }
            });

            for_production_planning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    gotToAddAuditScreen(Constants.RM_AUDIT_PRODUCTION_PLANNING);
                    alertDialog.cancel();


                }
            });

            for_production_store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    gotToAddAuditScreen(Constants.RM_AUDIT_OUT_PRODUCTION);
                    alertDialog.cancel();


                }
            });

            for_rejection_store.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    gotToAddAuditScreen(Constants.RM_AUDIT_OUT_REJECTION);
                    alertDialog.cancel();


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

//        }
    }


    public void gotToAddAuditScreen(int rm_audit_type) {
        Intent intent = new Intent(getActivity(), ScreenRmAuditAddBase.class);
        intent.putExtra("audit_type", rm_audit_type);
        startActivity(intent);
    }


}
