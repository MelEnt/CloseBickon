package se.github.closebitcon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import se.github.closebitcon.extra.AutoLog;
import se.github.closebitcon.extra.Toasters;

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences preferences;
    private Map<String, ?> prefMap;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(InitFormActivity.getPrefKey(), MODE_PRIVATE);
        AutoLog.introduce();
        setContentView(R.layout.activity_main);
        if(!preferences.getAll().isEmpty())
        {
            Intent intent = new Intent(MainActivity.this, InitFormActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //Debug
        prefMap = new HashMap<>(preferences.getAll());
        for(Map.Entry entry : prefMap.entrySet())
        {
            AutoLog.debug("key: "+entry.getKey() + " value: " +entry.getValue());
        }
    }

    public void clearPreference(View view)
    {
        preferences.edit().clear().apply();
    }
}
