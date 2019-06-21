package uk.ac.le.cityTourPlanner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class FeedbackDialog extends AppCompatDialogFragment {
    private EditText mUserName;
    private EditText mUserEmail;
    private EditText mUserComments;
    private FeedbackDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.feedback_dialog,null);

        builder.setView(view)
                .setTitle("Enter your feedback")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
        .setPositiveButton("Send Feedback", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String NameOfUser = mUserName.getText().toString();
                String UserEmail = mUserEmail.getText().toString();
                String UserComments = mUserComments.getText().toString();

                mListener.passDataToActivity(NameOfUser,UserEmail,UserComments);
            }
        });

        mUserName = view.findViewById(R.id.UserNameEditText);
        mUserEmail = view.findViewById(R.id.UserEmailEditText);
        mUserComments = view.findViewById(R.id.UserCommentsEditText);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (FeedbackDialogListener) context;
        } catch (ClassCastException e) {
           throw new ClassCastException(context.toString()+"must implement feedbackDialogListener");
        }
    }

    public interface FeedbackDialogListener {
        void passDataToActivity(String userName, String userEmail, String userComments);
    }
}
