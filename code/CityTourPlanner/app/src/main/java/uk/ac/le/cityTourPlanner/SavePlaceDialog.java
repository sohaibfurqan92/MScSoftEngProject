package uk.ac.le.cityTourPlanner;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;

import static android.support.v4.content.ContextCompat.getSystemService;

public class SavePlaceDialog extends AppCompatDialogFragment {
    private EditText mEditTextTripName;
    private EditText mEditTextTripDate;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_place_selection_dialog,null);

        builder.setView(view)
                .setTitle("Enter trip name and date")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
        .setPositiveButton("Save trip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        mEditTextTripName = view.findViewById(R.id.TripNameEditText);
        mEditTextTripDate = view.findViewById(R.id.TripDateEditText);

        //EditText editText = (EditText)findViewById(R.id.maped);
        mEditTextTripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close keyboard
                hideSoftKeyboardUsingView(getContext(),mEditTextTripDate);

                DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {

                        int s=monthOfYear+1;
                        String a = dayOfMonth+"/"+s+"/"+year;
                        mEditTextTripDate.setText(""+a);
                    }
                };

                Time date = new Time();
                date.setToNow();
                DatePickerDialog d = new DatePickerDialog(getContext(), dpd, date.year ,date.month, date.monthDay);
                d.show();

            }
        });


        return builder.create();
    }

    public static void hideSoftKeyboardUsingView(Context context,View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }


}
