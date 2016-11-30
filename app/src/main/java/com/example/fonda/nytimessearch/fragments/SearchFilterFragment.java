package com.example.fonda.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.fonda.nytimessearch.R;
import com.example.fonda.nytimessearch.models.Filters;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.fonda.nytimessearch.models.Filters.CB_ARTS;
import static com.example.fonda.nytimessearch.models.Filters.CB_FASHION;
import static com.example.fonda.nytimessearch.models.Filters.CB_SPORTS;

/**
 * Created by fonda on 11/27/16.
 */

public class SearchFilterFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    final Calendar dateSet = Calendar.getInstance();

    @BindView(R.id.etBeginDate) EditText etBeginDate;
    @BindView(R.id.spSortOrder) Spinner spSortOrder;
    @BindView(R.id.cbArts) CheckBox cbArts;
    @BindView(R.id.cbFashion) CheckBox cbFashion;
    @BindView(R.id.cbSports) CheckBox cbSports;
    @BindView(R.id.btnSave) Button btnSave;
    private Unbinder unbinder;

    String beginDate;
    Filters filters;
    OnSearchFilterActionListener listener;

    /**
     * Listener interface with method to pass data back to activity
     */
    public interface OnSearchFilterActionListener {
        void onFinishedSearchFilterFragment(Parcelable data);
    }

    public SearchFilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SearchFilterFragment newInstance(Filters fil) {
        SearchFilterFragment frag = new SearchFilterFragment();
        Bundle args = new Bundle();
        args.putParcelable("filters", Parcels.wrap(fil));
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View view = inflater.inflate(R.layout.activity_filter, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        filters = (Filters) Parcels.unwrap(getArguments().getParcelable("filters"));

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

        // Handle onClick for the save button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave(view);
            }
        });

        // hide the window title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        etBeginDate.requestFocus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchFilterActionListener) {
            listener = (OnSearchFilterActionListener) context;
        } else {
            throw new ClassCastException(context.toString() +
                " must implement SearchFilterFragment.OnSearchFilterActionListener");
        }
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_order, R.layout.spinner_item1);
        spinner.setAdapter(adapter);
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

        // Pass back filter data
        filters.setDate(beginDate);
        filters.setSortOrder(sortBy);
        filters.setNewsDeskValues(CB_ARTS, cbArts.isChecked());
        filters.setNewsDeskValues(CB_FASHION, cbFashion.isChecked());
        filters.setNewsDeskValues(CB_SPORTS, cbSports.isChecked());

        OnSearchFilterActionListener listener = (OnSearchFilterActionListener) getActivity();
        listener.onFinishedSearchFilterFragment(Parcels.wrap(filters));
        dismiss();
    }

    /**
     * Handler to show the date picker
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this, 300);
        newFragment.show(this.getFragmentManager()/*getSupportFragmentManager()*/, "datePicker");
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
