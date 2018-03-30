package cz.muni.fi.pv239.testmeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cz.muni.fi.pv239.testmeapp.R;
import cz.muni.fi.pv239.testmeapp.TestMeApp;

public class MainActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    private String actualLanguage;
    private boolean isMenuOpen = false;
    private Animation rotate_backward_45, rotate_forward_45, menu_open, menu_close;

    @BindView(R.id.floatingAddMenu)
    protected FloatingActionButton addMenuButton;

    @BindView(R.id.addByURL)
    protected Button addByURLButton;

    @BindView(R.id.addByQR)
    protected Button addByQRButton;

    @BindView(R.id.addByList)
    protected Button addByListButton;

    @NonNull
    public static Intent newIntent(@NonNull Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualLanguage = TestMeApp.getLang(this);
        TestMeApp.changeLang(this, TestMeApp.getLang(TestMeApp.appContext));
        TestMeApp.changeLang(getBaseContext(), TestMeApp.getLang(TestMeApp.appContext));
        TestMeApp.changeLang(getApplicationContext(), TestMeApp.getLang(TestMeApp.appContext));
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        rotate_backward_45 = AnimationUtils.loadAnimation(this, R.anim.rotate_backward_45);
        rotate_forward_45 = AnimationUtils.loadAnimation(this, R.anim.rotate_forward_45);
        menu_open = AnimationUtils.loadAnimation(this, R.anim.menu_open);
        menu_close = AnimationUtils.loadAnimation(this, R.anim.menu_close);
    }

    @OnClick(R.id.addByURL)
    protected void downloadTestByUrl(){
        downloadTests();
    }

    @OnClick(R.id.downloadTest)
    protected void downloadTests(){
        Intent intent = GetTestActivity.newIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.addByQR)
    protected void downloadTestByQr(){
        Intent intent = ScanQRCodeActivity.newIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.showTests)
    protected void showTests(){
        Intent intent = ListTestsActivity.newIntent(this);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.main_activity_head);
        if (!(actualLanguage.equals(TestMeApp.getLang(TestMeApp.appContext)))){
            actualLanguage = TestMeApp.getLang(TestMeApp.appContext);
            recreate();
        }
    }

    @OnClick(R.id.floatingAddMenu)
    public void animateMenu(){
        if(isMenuOpen){
            addMenuButton.startAnimation(rotate_backward_45);
            addByURLButton.startAnimation(menu_close);
            addByURLButton.setClickable(false);
            addByQRButton.startAnimation(menu_close);
            addByQRButton.setClickable(false);
            addByListButton.startAnimation(menu_close);
            addByListButton.setClickable(false);
            isMenuOpen = false;
        } else {
            addMenuButton.startAnimation(rotate_forward_45);
            addByURLButton.startAnimation(menu_open);
            addByURLButton.setClickable(true);
            addByQRButton.startAnimation(menu_open);
            addByQRButton.setClickable(true);
            addByListButton.startAnimation(menu_open);
            addByListButton.setClickable(true);
            isMenuOpen = true;
        }
    }
}
