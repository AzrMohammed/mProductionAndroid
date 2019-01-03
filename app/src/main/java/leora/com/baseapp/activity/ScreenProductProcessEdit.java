package leora.com.baseapp.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.model.DbSQLiteHelper;
import leora.com.baseapp.model.dbmodel.ProductModel;
import leora.com.baseapp.supportfiles.CommonMethods;
import leora.com.baseapp.utils.DataUtils;
import leora.com.baseapp.utils.ViewUtils;

/**
 * Created by AZR on 28-12-2018.
 */

public class ScreenProductProcessEdit extends CustomAppCompatActivity {

    ProductModel productModel = new ProductModel();
    String product_slug = "";
    DbSQLiteHelper dbSQLiteHelper;
    EditText product_ref_id_tv;
    ImageView edit_process_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_product_detail);

        getIntents();
        initializeViews();
        setupValues();
        setupListeners();

    }


    public void getIntents()
    {

        try {
            product_slug = getIntent().getStringExtra("product_slug");

        } catch (Exception e) {
            product_slug = "";
            e.printStackTrace();
        }



    }

    public void initializeViews()
    {

        edit_process_iv = findViewById(R.id.edit_process_iv);
        product_ref_id_tv = findViewById(R.id.product_ref_id_tv);

    }

    public void setupValues()
    {


        if(!(DataUtils.isStringValueExist(product_slug)))
        {
            finish();
        }
        else {

            dbSQLiteHelper = CommonMethods.getDbModel(ScreenProductProcessEdit.this);

            productModel = dbSQLiteHelper.getProductModel(product_slug);

            product_ref_id_tv.setText(productModel.ref_id);
            ViewUtils.setHeaderC1(ScreenProductProcessEdit.this, findViewById(R.id.top_bar_header_ly), productModel.name);

        }
    }

    public void setupListeners()
    {

    }

}
