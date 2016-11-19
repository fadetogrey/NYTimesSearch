package com.example.fonda.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
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

import static com.example.fonda.nytimessearch.models.Filters.CB_ARTS;
import static com.example.fonda.nytimessearch.models.Filters.CB_FASHION;
import static com.example.fonda.nytimessearch.models.Filters.CB_SPORTS;

public class FilterActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;
    String beginDate;
    EditText etBeginDate;
    Spinner spSortOrder;
    CheckBox cbArts;
    CheckBox cbFashion;
    CheckBox cbSports;
    final Calendar dateSet = Calendar.getInstance();
    Filters filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        // getSupportActionBar().hide();

        // Find the Views from the layout
        etBeginDate = (EditText) findViewById(R.id.etBeginDate);
        spSortOrder = (Spinner) findViewById(R.id.spSortOrder);
        cbArts = (CheckBox) findViewById(R.id.cbArts);
        cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        cbSports = (CheckBox) findViewById(R.id.cbSports);

        // Get the data passed in for the intent
        filters = (Filters) Parcels.unwrap(getIntent().getParcelableExtra("filters"));

        // Populate the Views with the passed in data
        etBeginDate.setText(filters.getDate());
        setSpinnerToValue(spSortOrder, filters.getSortOrder());
        cbArts.setChecked(filters.getNewsDeskValues(CB_ARTS));
        cbFashion.setChecked(filters.getNewsDeskValues(Filters.CB_FASHION));
        cbSports.setChecked(filters.getNewsDeskValues(CB_SPORTS));

        // Handle onClick for the date field
        etBeginDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
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

    /**
     * Handler for the Save button in the Filters dialog
     * @param view
     */
    public void onSave(View view) {
        String sortBy = spSortOrder.getSelectedItem().toString();
        // Prepare data intent
        Intent intent = new Intent();

        // Pass back filter data
        filters.setDate(beginDate);
        filters.setSortOrder(sortBy);
        filters.setNewsDeskValues(CB_ARTS, cbArts.isChecked());
        filters.setNewsDeskValues(CB_FASHION, cbFashion.isChecked());
        filters.setNewsDeskValues(CB_SPORTS, cbSports.isChecked());

        // Pass back the intent
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

    /**
     * Handler for the DatePickerFragment
     * Save off the user selected values
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
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
