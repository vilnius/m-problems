package lt.vilnius.tvarkau;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import lt.vilnius.tvarkau.activity.ReportRegistrationActivity;
import lt.vilnius.tvarkau.utils.GlobalConsts;
import lt.vilnius.tvarkau.utils.PermissionUtils;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static lt.vilnius.tvarkau.ProblemsListActivity.ALL_PROBLEMS;
import static lt.vilnius.tvarkau.ProblemsListActivity.MY_PROBLEMS;


/**
 * An activity representing a main activity home screen
 */
public class MainActivity extends BaseActivity {

    public static final int MAP_PERMISSION_REQUEST_CODE = 11;
    public static final int NEW_ISSUE_REQUEST_CODE = 12;
    public static final String[] MAP_PERMISSIONS = new String[]{ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);

    }

    protected void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);

        startActivity(intent);
    }

    protected void startNewActivity(Class<?> cls, Bundle data) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(data);

        startActivity(intent);
    }

    @OnClick(R.id.home_report_problem)
    protected void onNewIssueClicked() {
        Intent intent = new Intent(this, ReportRegistrationActivity.class);
        startActivityForResult(intent, NEW_ISSUE_REQUEST_CODE);
    }

    @OnClick(R.id.home_list_of_problems)
    protected void onProblemsListClicked() {
        Intent intent = ProblemsListActivity.Companion.getStartActivityIntent(this, ALL_PROBLEMS);

        startActivity(intent);
    }

    @OnClick(R.id.home_my_problems)
    protected void onMyProblemsClicked() {
        showMyProblemsList();
    }

    private void showMyProblemsList() {
        Intent intent = ProblemsListActivity.Companion.getStartActivityIntent(this, MY_PROBLEMS);
        startActivity(intent);
    }

    @OnClick(R.id.home_map_of_problems)
    protected void onProblemsMapClicked() {

        if ((PermissionUtils.isAllPermissionsGranted(this, MAP_PERMISSIONS))) {
            startProblemActivity();
        } else {
            requestPermissions(MAP_PERMISSIONS, MAP_PERMISSION_REQUEST_CODE);
        }
    }

    private void startProblemActivity() {
        Bundle data = new Bundle();
        data.putString(GlobalConsts.KEY_MAP_FRAGMENT, GlobalConsts.TAG_MULTIPLE_PROBLEMS_MAP_FRAGMENT);
        startNewActivity(ProblemsMapActivity.class, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MAP_PERMISSION_REQUEST_CODE && PermissionUtils.isAllPermissionsGranted(this, MAP_PERMISSIONS)) {
            startProblemActivity();
        } else {
            Toast.makeText(this, R.string.error_need_location_permission, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.home_settings)
    protected void onSettingsClicked() {
        startNewActivity(SettingsActivity.class);
    }

    @OnClick(R.id.home_about)
    protected void onAboutClicked() {
        startNewActivity(AboutActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == NEW_ISSUE_REQUEST_CODE) {
            showMyProblemsList();
        }
    }
}
