package se.github.closebitcon;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import se.github.closebitcon.extra.Toasters;

/**
 *  Saves SharedPreference k&v pairs to PREF_KEY = "Bitcon_LocalPref"
 *
 *
 */
public class InitFormActivity extends AppCompatActivity
{
    private final static String PREF_KEY = "Bitcon_LocalPref";
    private EditText firstNameInput = null;
    private EditText lastNameInput = null;
    private Map<String, ?> prefMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Toasters.setContext(getApplicationContext());
        setContentView(R.layout.activity_init_form);
    }

    public void sendForm(View view)
    {
        String firstnameKey = "FIRST_NAME";
        String lastnameKey  = "LAST_NAME";

        firstNameInput = (EditText) findViewById(R.id.input_firstname);
        lastNameInput = (EditText) findViewById(R.id.input_lastname);
        saveName(firstnameKey, firstNameInput.getText().toString());
        saveName(lastnameKey, lastNameInput.getText().toString());

        if(prefMap.isEmpty())
        {
            Toasters.showQuick(R.string.stringNoResult);
        }
        Toasters.show(getResources().getString(R.string.stringWelcomeUser, getSavedName(lastnameKey)));
    }

    private void saveName(String key, String value)
    {
        SharedPreferences preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
        prefMap = new HashMap<>(preferences.getAll());

    }

    private String getSavedName(String key)
    {
        return (String) prefMap.get(key);
    }

    public static String getPrefKey()
    {
        return PREF_KEY;
    }
}
