package kr.co.didimu.sampleapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;


public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    @Override
    public void setContentView(int layoutResID){
        LinearLayout fullView = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = (FrameLayout) fullView.findViewById(R.id.activity_content);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //툴바 사용여부 결정(기본적으로 사용)
        if(useToolbar()){
            setSupportActionBar(toolbar);
            setTitle("(주)디딤유");
        } else {
            toolbar.setVisibility(View.GONE);
        }

    }

    //툴바를 사용할지 말지 정함
    protected boolean useToolbar(){
        return true;
    }

    //메뉴 등록하기
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_bottom, menu);
        return true;
    }

    //앱바 메뉴 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_menu0:
                //Toast.makeText(getApplicationContext(),"메뉴1 클릭", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_menu1:
                //Toast.makeText(getApplicationContext(),"메뉴1 클릭", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(this, SubActivity.class);
                startActivity(intent1);
                return true;

            case R.id.action_menu2:
                //Toast.makeText(getApplicationContext(),"메뉴2 클릭", Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(this, Sub2Activity.class);
                startActivity(intent2);
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Toast.makeText(getApplicationContext(),"앱설정", Toast.LENGTH_SHORT).show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

}
