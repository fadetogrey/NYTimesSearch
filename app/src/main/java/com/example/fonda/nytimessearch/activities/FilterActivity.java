package com.example.fonda.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.example.fonda.nytimessearch.R;
import com.example.fonda.nytimessearch.fragments.DatePickerFragment;
import com.example.fonda.nytimessearch.models.Filters;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;
    String beginDate;
    EditText etBeginDate;
    Spinner spSortOrder;
    final Calendar dateSet = Calendar.getInstance();
    Filters filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        // getSupportActionBar().hide();

        etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        spSortOrder = (Spinner) findViewById(R.id.spSortOrder);

        filters = (Filters) Parcels.unwrap(getIntent().getParcelableExtra("filters"));
        etBeginDate.setText(filters.getDate());
        setSpinnerToValue(spSortOrder, filters.getSortOrder());

        etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        // TODO checkbox array
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break;
            }
        }
        spinner.setSelection(index);
    }

    public void onSave(View view) {
        String sortBy = spSortOrder.getSelectedItem().toString();
        // Prepare data intent
        Intent intent = new Intent();
        // Pass back filter data
        filters.setDate(beginDate);
        filters.setSortOrder(sortBy);

        intent.putExtra("filters", Parcels.wrap(filters));
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Handler to show the date picker
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        // Store the values selected into a Calendar instance
        dateSet.set(Calendar.YEAR, year);
        dateSet.set(Calendar.MONTH, month);
        dateSet.set(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        beginDate = dateFormat.format(dateSet.getTime());
        etBeginDate.setText(beginDate);
    }


}
