package lt.vilnius.tvarkau;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import lt.vilnius.tvarkau.fragments.MyProfileFragment;
import lt.vilnius.tvarkau.utils.KeyboardUtils;

public class ProfileEditActivity extends BaseActivity {

    private MyProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_activity);

        if (savedInstanceState == null) {
            profileFragment = MyProfileFragment.getInstance();
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.profile_frame, profileFragment)
                .commit();
        }
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            KeyboardUtils.closeSoftKeyboard(this, view);
        }
        if (profileFragment != null && profileFragment.isEditedByUser()) {
            new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setMessage(getString(R.string.discard_changes_title))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.discard_changes_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ProfileEditActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.discard_changes_negative, null).show();
        } else {
            super.onBackPressed();
        }
    }
}
