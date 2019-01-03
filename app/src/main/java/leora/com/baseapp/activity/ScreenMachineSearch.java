package leora.com.baseapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.model.dbmodel.PMachineModel;
import leora.com.baseapp.supportfiles.CommonMethods;

/**
 * Created by AZR on 25-11-2018.
 */

public class ScreenMachineSearch extends CustomAppCompatActivity {


    RecyclerView recycler_view;
    DbSQLiteHelper dbSQLiteHelper;

    ArrayList<PMachineModel> materialModels = new ArrayList<PMachineModel>();
    ArrayList<PMachineModel> filtered_lists = new ArrayList<PMachineModel>();
    RecyclerView.LayoutManager mLayoutManager;
    EditText search_et;
    LayoutInflater layoutInflater;
    boolean is_dialog_showing = false;
    RecyclerViewMaterial recyclerViewMaterial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_rm_search);


        initializeView();
        setupValues();
        setupListeners();

    }


    private void initializeView() {

        recycler_view = findViewById(R.id.recycler_view);
        search_et = findViewById(R.id.search_et);


        mLayoutManager = new LinearLayoutManager(ScreenMachineSearch.this);
        recycler_view.setLayoutManager(mLayoutManager);


    }

    private void setupValues() {
        layoutInflater = LayoutInflater.from(ScreenMachineSearch.this) ;
        dbSQLiteHelper = CommonMethods.getDbModel(ScreenMachineSearch.this);
        materialModels = dbSQLiteHelper.getPMachineModels();

        Log.e("array_size ", materialModels.size() + "===" + dbSQLiteHelper.getMaterialAuditModels().size());

        recycler_view.setAdapter(new RecyclerViewMaterial(materialModels));

        recyclerViewMaterial = new RecyclerViewMaterial(materialModels);


    }

    private void setupListeners() {


        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String str = charSequence.toString();

                filtered_lists = new ArrayList<PMachineModel>();

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


    }


    public class RecyclerViewMaterial extends RecyclerView.Adapter<RecyclerViewHolder> {

        ArrayList<PMachineModel> materialModels_final = new ArrayList<PMachineModel>();


        public RecyclerViewMaterial(ArrayList<PMachineModel> materialModels) {
            this.materialModels_final = materialModels;

        }


        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = layoutInflater.inflate(R.layout.item_rm_search, parent, false);
            RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

            final PMachineModel materialModel = materialModels_final.get(position);

            holder.material_name_tv.setText(materialModel.name);
            holder.material_ref_id_tv.setText(materialModel.ref_id);

            holder.parent_ly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    returnResult(materialModel.slug, materialModel.name);
                }
            });

        }

        @Override
        public int getItemCount() {
            return materialModels_final.size();
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView material_ref_id_tv, material_name_tv;
        View parent_ly;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            material_ref_id_tv = itemView.findViewById(R.id.material_ref_id_tv);
            material_name_tv = itemView.findViewById(R.id.material_name_tv);
            parent_ly = itemView.findViewById(R.id.parent_ly);

        }

    }


    public void returnResult(String slug, String rm_name) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("machine_slug", slug);
        returnIntent.putExtra("machine_name", rm_name);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

}
