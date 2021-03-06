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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import leora.com.baseapp.Constants;
import leora.com.baseapp.R;
import leora.com.baseapp.activity.ScreenProductAdd;
import leora.com.baseapp.activity.ScreenProductDetails;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.model.dbmodel.ProductModel;
import leora.com.baseapp.network.CustomDeleteResponseListener;
import leora.com.baseapp.network.CustomJsonDeleteRequest;
import leora.com.baseapp.network.CustomJsonObjectRequest;
import leora.com.baseapp.network.CustomResponseListener;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.ApiUtils;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.DisplayUtils;
import leora.com.baseapp.utils.ValueUtils;
import leora.com.baseapp.utils.ViewUtils;

public class ProductBaseFragment extends android.support.v4.app.Fragment {

    RecyclerView recycler_view;
    DbSQLiteHelper dbSQLiteHelper;
    LinearLayout fab_ly;
    ArrayList<ProductModel> materialModels = new ArrayList<ProductModel>();
    ArrayList<ProductModel> filtered_lists = new ArrayList<ProductModel>();
    RecyclerView.LayoutManager mLayoutManager;
    EditText search_et;
    boolean is_dialog_showing = false;
    RecyclerViewMaterial recyclerViewMaterial;

    public static ProductBaseFragment newInstance() {

        final ProductBaseFragment fragment = new ProductBaseFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
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
        search_et = view.findViewById(R.id.search_et);
        fab_ly = view.findViewById(R.id.fab_ly);

        mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);


    }

    private void setupValues() {

        dbSQLiteHelper = CommonMethods.getDbModel(getActivity());
        recyclerViewMaterial = new RecyclerViewMaterial(materialModels);

        ViewUtils.hideKeyboard(getActivity());

    }

    private void setupListeners() {


        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str = charSequence.toString();

                filtered_lists = new ArrayList<ProductModel>();

                for (int j = 0; j < materialModels.size(); j++) {


//                    Log.e("comp1111", materialModels.get(j).name.toLowerCase()+"==="+materialModels.get(j).ref_id.toLowerCase()+"==="+str.toLowerCase());
                    if ((materialModels.get(j).name.toLowerCase().contains(str.toLowerCase())) || (materialModels.get(j).ref_id.toLowerCase().contains(str.toLowerCase()))) {
//Log.e("pass1", "==="+str.toLowerCase());
                        filtered_lists.add(materialModels.get(j));

                    }

                }


                recycler_view.setAdapter(new RecyclerViewMaterial(filtered_lists));


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


        fab_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAddAlert(getActivity(),"","");
                Intent intent = new Intent(getActivity(), ScreenProductAdd.class);

                startActivity(intent);

            }
        });

    }


    public class RecyclerViewMaterial extends RecyclerView.Adapter<RecyclerViewHolder> {

        ArrayList<ProductModel> materialModels_final = new ArrayList<ProductModel>();


        public RecyclerViewMaterial(ArrayList<ProductModel> materialModels) {
            this.materialModels_final = materialModels;

        }


        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_item, null);
            RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

            final ProductModel materialModel = materialModels_final.get(position);

            holder.material_name_tv.setText(materialModel.name);
//            holder.material_name_tv.setText("Test");
            holder.material_ref_id_tv.setText(materialModel.ref_id);


            if (materialModel.status.equals(ValueUtils.RAW_MATERIAL_BG_COLOR_DEFAULT)) {
                holder.parent_ly.setBackground(getResources().getDrawable(R.color.item_default));
                holder.delete_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
            } else {
                holder.parent_ly.setBackground(getResources().getDrawable(R.color.item_deleted));
                holder.delete_iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_revert));
            }

            holder.edit_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    showAddAlert(getActivity(),materialModel.name,materialModel.ref_id);

                    Intent intent = new Intent(getActivity(), ScreenProductAdd.class);

                    intent.putExtra("product_obj", materialModel);

                    startActivity(intent);
                }
            });

            holder.delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog diaBox = AskOption(materialModel.ref_id, materialModel.status);
                    diaBox.show();
                }
            });


            holder.parent_ly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ScreenProductDetails.class);
                    intent.putExtra("product_slug", materialModel.slug);
                    startActivity(intent);

                }
            });

            {
                holder.length_ly.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            return materialModels_final.size();
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView material_ref_id_tv, material_name_tv, length_tv;
        ImageView delete_iv, edit_iv;
        LinearLayout length_ly;
        RelativeLayout parent_ly;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            material_ref_id_tv = itemView.findViewById(R.id.material_ref_id_tv);
            material_name_tv = itemView.findViewById(R.id.material_name_tv);

            edit_iv = itemView.findViewById(R.id.edit_iv);
            delete_iv = itemView.findViewById(R.id.delete_iv);

            length_tv = itemView.findViewById(R.id.length_tv);
            length_ly = itemView.findViewById(R.id.length_ly);
            parent_ly = itemView.findViewById(R.id.parent_ly);

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


                                            materialModels = dbSQLiteHelper.getProductModels();

                                            Log.e("array_size ", materialModels.size() + "===" + dbSQLiteHelper.getMaterialAuditModels().size());

                                            recycler_view.setAdapter(new RecyclerViewMaterial(materialModels));


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



    public AlertDialog AskOption(final String id, final String revert_status) {
        String message = !revert_status.equals(ValueUtils.RAW_MATERIAL_ITEM_DEFAULT) ? "Add back deleted item" : "Delete item";
        String key = !revert_status.equals(ValueUtils.RAW_MATERIAL_ITEM_DEFAULT) ? "Add Back" : "Delete";
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(getActivity())
                //set message, title, and icon
                .setTitle(id)
                .setMessage(message)
                .setPositiveButton(key, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteDataRequest(id, revert_status);
                        dialog.dismiss();
                    }

                })


                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    public void deleteDataRequest(String ref_id, String revert_status) {
        final String url = Constants.URL_DEL_ITTEM;


        String status_to = revert_status.equals(ValueUtils.RAW_MATERIAL_ITEM_DEFAULT) ? ValueUtils.RAW_MATERIAL_ITEM_DELETE : ValueUtils.RAW_MATERIAL_ITEM_DEFAULT;

        Map<String, String> params = ApiUtils.getApiRequestDefaultMap();
        params.put(ValueUtils.RM_DELETE_PARAMS_REF_KEY, ValueUtils.RM_DELETE_PARAMS_REF_KEY_PARAMS);
        params.put(ValueUtils.RM_DELETE_PARAMS_REF_VALUE, ref_id);
        params.put(ValueUtils.RM_DELETE_PARAMS_REF_TABLE, Constants.TBL_PRODUCT);
        params.put(ValueUtils.RM_DELETE_PARAMS_REF_STATUS, status_to);
        JSONObject request_obj = DataUtils.convertMapToJsonObj(params);


        new CustomJsonDeleteRequest(getActivity(), true, "", Request.Method.POST, url, request_obj, new CustomDeleteResponseListener() {
            @Override
            public void responseSuccess(JSONObject response) {
                Log.e("cameonss", "===" + response);
                try {
                    if (response.getBoolean("status")) {
                        DisplayUtils.showMessage(getActivity(), response.getString("status_message"));
                        proceedSampleRequest();


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
