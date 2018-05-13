package com.bhagyashreebagwe.converter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public List<String> history = new ArrayList<String>();
    private double output_temp;
    private double input_temp;
    private int selectedId;
    private DecimalFormat form = new DecimalFormat("0.0");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void doConversion(View v)
    {
        Log.d(TAG, "Retrieving input data... ");
        RadioGroup radioTempGroup = (RadioGroup) findViewById(R.id.radioGroup);
        selectedId = radioTempGroup.getCheckedRadioButtonId();
        RadioButton radioTempButton = (RadioButton) findViewById(selectedId);
        EditText input = findViewById(R.id.input1);
        input_temp = Double.parseDouble(input.getText().toString());
        Log.d(TAG, "Calculating output...");
        if(radioTempButton.getText().equals("Fahrenheit to Celsius")) {
            output_temp = (input_temp - 32.0) * 5.0 / 9.0;
            history.add("F to C:  "+input_temp+" --> "+form.format(output_temp));
        }else
        {
            output_temp = (input_temp * 9.0/5.0) + 32.0;
            history.add("C to F:  "+input_temp+" --> "+form.format(output_temp));
        }
        Log.d(TAG, "Setting the output..");
        TextView output1 = findViewById(R.id.output1);
        TextView output2 = findViewById(R.id.output2);
        output1.setText(form.format(output_temp)+"");
        output2.setText("");
        for(int i=history.size()-1;i>=0;i--)
        {
            output2.append(history.get(i)+"\n");
        }
    }
}

