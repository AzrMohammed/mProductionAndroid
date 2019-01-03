package leora.com.baseapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.R;
import leora.com.baseapp.fragments.MaterialAuditFragment;
import leora.com.baseapp.fragments.MaterialDetailFragment;
import leora.com.baseapp.model.HomeListModel;
import leora.com.baseapp.model.dbmodel.RawMaterialModel;

public class ScreenHome extends CustomAppCompatActivity {

    RecyclerView list_rv;
    ArrayList<HomeListModel> homeListModels;

    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_home);

        getIntents();
        initializeViews();
        setupValues();
        setupListeners();

    }


    public void getIntents() {

    }

    public void initializeViews() {
        list_rv = findViewById(R.id.list_rv);
    }

    public void setupValues() {

        mLayoutManager = new LinearLayoutManager(ScreenHome.this);
        list_rv.setLayoutManager(mLayoutManager);

        homeListModels = new ArrayList<HomeListModel>();

        homeListModels.add(new HomeListModel("Raw Materials", "Contains the list of raw materials irrespective of its availability.", R.drawable.ic_rm, ScreenRmBase.class));
        homeListModels.add(new HomeListModel("Raw Material Audits", "Contains all the transactions of the raw materials.", R.drawable.ic_rm_audit, ScreenRmAuditBase.class));
        homeListModels.add(new HomeListModel("Machine Meta Data", "Contains list of available machinery details.", R.drawable.ic_rm_audit, ScreenMachineBase.class));
        homeListModels.add(new HomeListModel("Product Meta Data", "Contains product process and production demand/supply details", R.drawable.ic_rm_audit, ScreenProductBase.class));
        homeListModels.add(new HomeListModel("Process Meta Data", "Contains process details", R.drawable.ic_rm_audit, ScreenProcessBase.class));
        homeListModels.add(new HomeListModel("Machinery Timeline details", "Contains hourly basis machinery utilization details", R.drawable.ic_rm_audit, ScreenMachineAuditBase.class));


        list_rv.setAdapter(new RecyclerViewMaterial());

    }

    public void setupListeners() {

    }


    public class RecyclerViewMaterial extends RecyclerView.Adapter<RecyclerViewHolder> {


        public RecyclerViewMaterial() {

        }


        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_list, null);
            RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
            return rcv;
        }

        @Override
        public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

            final HomeListModel homeListModel = homeListModels.get(position);

            holder.name_tv.setText(homeListModel.name);
            holder.description_tv.setText(homeListModel.description);
            holder.img_iv.setImageDrawable(getResources().getDrawable(homeListModel.img_id));

            holder.parent_ly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(ScreenHome.this, homeListModel.to_class);
                    startActivity(intent);

                }
            });


        }

        @Override
        public int getItemCount() {
            return homeListModels.size();
        }
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv, description_tv;
        ImageView img_iv;
        LinearLayout parent_ly;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            parent_ly = itemView.findViewById(R.id.parent_ly);
            img_iv = itemView.findViewById(R.id.img_iv);
            description_tv = itemView.findViewById(R.id.description_tv);
            name_tv = itemView.findViewById(R.id.name_tv);
        }

    }


}
