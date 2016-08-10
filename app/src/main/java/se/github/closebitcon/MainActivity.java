package se.github.closebitcon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import se.github.closebitcon.extra.AutoLog;
import se.github.closebitcon.extra.Toasters;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        AutoLog.introduce();
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, InitFormActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(InitFormActivity.getPrefKey(), MODE_PRIVATE);
        Map<String, ?> mappo = new HashMap<>(preferences.getAll());
        for(Map.Entry entry : mappo.entrySet())
        {
            AutoLog.debug("key: "+entry.getKey() + " value: " +entry.getValue());
        }
    }
}
