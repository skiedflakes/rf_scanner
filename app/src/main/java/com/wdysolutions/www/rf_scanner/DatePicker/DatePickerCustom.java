package com.wdysolutions.www.rf_scanner.DatePicker;

/**
 * Created by aronandrada on 4/30/18.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerCustom extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public DatePickerSelectionInterfaceCustom delegate = null;

    int year, month, dayOfMonth;
    long selectedMax;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String maxDate = getArguments().getString("maxDate");
        String minDate = getArguments().getString("minDate");
        String maxDate_minus = getArguments().getString("maxDate_minus");
        boolean isMinusDays = getArguments().getBoolean("isMinusDays");
        boolean isSetMinDate = getArguments().getBoolean("isSetMinDate");

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, dayOfMonth);
        datePickerDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        // if null set to current (max) date -------------------------------------------------------
        if (maxDate != null && maxDate.equals("")){
            selectedMax = new Date().getTime();
        }
        // else set to database (max) date
        else {
            try {
                Date mxDate = sdf.parse(isMinusDays ? maxDate_minus : maxDate);
                selectedMax = mxDate.getTime();
            } catch (ParseException e) {}
        }
        // set max date
        datePickerDialog.getDatePicker().setMaxDate(selectedMax);


        // if true set (min) date ------------------------------------------------------------------
        if (isSetMinDate){
            try {
                // add 1 day
                c.setTime(sdf.parse(minDate));
                c.add(Calendar.DATE, 1);

                Date convert = sdf.parse(sdf.format(c.getTime()));
                long selectedMin = convert.getTime();

                // set min date
                datePickerDialog.getDatePicker().setMinDate(selectedMin);
            } catch (ParseException e) {}
        }

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        month ++;
        String mDate = year + "-" + month + "-" + dayOfMonth;
        delegate.onDateSelected(mDate);
    }
}
