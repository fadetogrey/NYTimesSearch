package com.example.fonda.nytimessearch.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import java.util.Calendar;

;
/**
 * Created by fonda on 11/17/16.
 */

public class DatePickerFragment extends android.support.v4.app.DialogFragment {

    //public interface DatePickerDialogListener {
    //    void onFinishDatePickerDialog(DialogFragment fragment, int year, int month, int day);
    //}

    public static DatePickerFragment newInstance() {
        DatePickerFragment frag = new DatePickerFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Parent filters fragment needs to implement this interface
        //DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getTargetFragment();

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

}