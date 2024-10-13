package com.project.ordernote.ui.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.ordernote.R;
import com.project.ordernote.utils.Constants;

public class Share_Or_View_BottomSheetDialogFragment extends BottomSheetDialogFragment {

    LinearLayout viewFileButton,shareFileButton ,fileTypeLayout;
    private Share_Or_View_Listener onDownloadingTypeSelectedListener;
    TextView pdf_fileTypeText , xls_fileTypeText;

    String fileType;

    public interface Share_Or_View_Listener {
        void onDownloadingTypeSelectedListener(String selectedMode , String selectedType);
    }

    public void setShare_Or_View_Listener(Share_Or_View_BottomSheetDialogFragment.Share_Or_View_Listener onDownloadingTypeSelectedListener) {
        this.onDownloadingTypeSelectedListener = onDownloadingTypeSelectedListener;
    }
    
    
    public Share_Or_View_BottomSheetDialogFragment() {
        // Required empty public constructor
    }

     

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.share__or__view__bottom_sheet_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewFileButton = view.findViewById(R.id.viewFileButton);
        shareFileButton = view.findViewById(R.id.shareFileButton);
        fileTypeLayout  = view.findViewById(R.id.fileTypeLayout);
        xls_fileTypeText = view.findViewById(R.id.xls_fileTypeTextview);
        pdf_fileTypeText = view.findViewById(R.id.pdf_fileTypeTextview);


        pdf_fileTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable backgroundpdf = ContextCompat.getDrawable(requireActivity(), R.drawable.round_redbackground_withoutpadding);
                pdf_fileTypeText.setBackground(backgroundpdf);
                pdf_fileTypeText.setTextColor(getResources().getColor(R.color.white));

                Drawable backgroundxls = ContextCompat.getDrawable(requireActivity(), R.drawable.round_lightredbackground);
                xls_fileTypeText.setBackground(backgroundxls);
                xls_fileTypeText.setTextColor(getResources().getColor(R.color.black));
                fileType = Constants.pdf_filetype;

            }
        });
        pdf_fileTypeText.performClick();
        xls_fileTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable backgroundxls= ContextCompat.getDrawable(requireActivity(), R.drawable.round_redbackground_withoutpadding);
                xls_fileTypeText .setBackground(backgroundxls);
                xls_fileTypeText.setTextColor(getResources().getColor(R.color.white));

                Drawable backgroundpdf = ContextCompat.getDrawable(requireActivity(), R.drawable.round_lightredbackground);
                pdf_fileTypeText.setBackground(backgroundpdf);
                pdf_fileTypeText.setTextColor(getResources().getColor(R.color.black));
                fileType = Constants.xls_filetype;
            }
        });

        shareFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDownloadingTypeSelectedListener.onDownloadingTypeSelectedListener(Constants.share_file , fileType);
                //dismiss();
            }
        });



        viewFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDownloadingTypeSelectedListener.onDownloadingTypeSelectedListener(Constants.view_file , fileType);
              //  dismiss();
            }
        });





    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        onDownloadingTypeSelectedListener.onDownloadingTypeSelectedListener("" , "");


    }
}