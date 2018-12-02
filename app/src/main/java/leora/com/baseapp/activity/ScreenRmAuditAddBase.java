package leora.com.baseapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import leora.com.baseapp.R;
import leora.com.baseapp.customclass.CustomAppCompatActivity;
import leora.com.baseapp.fragments.MaterialAuditFragment;
import leora.com.baseapp.fragments.RmAuditAddFragment;
import leora.com.baseapp.utils.ValueUtils;

/**
 * Created by AZR on 27-11-2018.
 */

public class ScreenRmAuditAddBase extends CustomAppCompatActivity {

    FrameLayout frame_ly;

    public int audit_type = ValueUtils.NO_VALUE_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_rm_audit_base);

        getIntents();
//        Fragment fragment = MaterialDetailFragment.newInstance();
        Fragment fragment = RmAuditAddFragment.newInstance();
        setFragment(fragment);


    }

    public void getIntents()
    {
        audit_type = getIntent().getIntExtra("audit_type", ValueUtils.NO_VALUE_INT);
    }

    /**
     * sample api request
     */
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frame_ly, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
