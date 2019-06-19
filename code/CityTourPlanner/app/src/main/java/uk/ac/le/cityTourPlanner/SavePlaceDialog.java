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

public class SavePlaceDialog extends AppCompatDialogFragment {
    private EditText mEditTextTripName;
    private EditText mEditTextTripDate;
    private SaveTripDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.save_place_selection_dialog,null);

        builder.setView(view)
                .setTitle("Enter trip name and date")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
        .setPositiveButton("Save trip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tripName = mEditTextTripName.getText().toString();
                String tripDate = mEditTextTripDate.getText().toString();

                mListener.passDataToActivity(tripName,tripDate);
            }
        });

        mEditTextTripName = view.findViewById(R.id.UserNameEditText);
        mEditTextTripDate = view.findViewById(R.id.UserEmailEditText);

        mEditTextTripDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    //close keyboard
                    hideSoftKeyboardUsingView(getContext(),mEditTextTripDate);

                    DatePickerDialog.OnDateSetListener dpd = new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {

                            int month=monthOfYear+1;
                            String date = dayOfMonth+"/"+month+"/"+year;
                            mEditTextTripDate.setText(""+date);
                            hideSoftKeyboardUsingView(getContext(),mEditTextTripDate);
                        }
                    };

                    Time date = new Time();
                    date.setToNow();
                    DatePickerDialog d = new DatePickerDialog(getContext(), dpd, date.year ,date.month, date.monthDay);
                    d.show();
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (SaveTripDialogListener) context;
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString()+"must implement SavePlaceDialogListemer");
        }
    }

    public interface SaveTripDialogListener {
        void passDataToActivity(String tripName, String tripDate);
    }

    public static void hideSoftKeyboardUsingView(Context context,View view) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

    }


}
