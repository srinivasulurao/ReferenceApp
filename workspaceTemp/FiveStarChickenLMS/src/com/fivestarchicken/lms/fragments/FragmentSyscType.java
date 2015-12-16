package com.fivestarchicken.lms.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivestarchicken.lms.R;
import com.fivestarchicken.lms.utils.Commons;

public class FragmentSyscType extends DialogFragment {

    String type;
    TextView tvCancel;
    RelativeLayout rrRestore, rrUpdate;
    SyncDialogListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Full_Screen);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {

        	 mListener = (SyncDialogListener) activity;
           // mListener = (SyncDialogListener) getTargetFragment();
            


        } catch (ClassCastException e) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sysc_type, container,
                false);
        
        rrRestore=(RelativeLayout)root.findViewById(R.id.rlrestoredata);
        rrUpdate=(RelativeLayout)root.findViewById(R.id.rlupdatedata);

        rrRestore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 mListener.onSyncSelect(Commons.SYNC_TYPE_RESTORE);
				 
				 FragmentSyscType.this.dismiss();
				
				
			}
		});
        
        rrUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				 mListener.onSyncSelect(Commons.SYNC_TYPE_UPDATE);
				
				 FragmentSyscType.this.dismiss();
				
			}
		});


        return root;
    }

   
    public static interface SyncDialogListener {
        void onSyncSelect(String type);
    }

}
