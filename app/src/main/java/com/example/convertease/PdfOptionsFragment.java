package com.example.convertease;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PdfOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PdfOptionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PdfOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PdfOptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PdfOptionsFragment newInstance(String param1, String param2) {
        PdfOptionsFragment fragment = new PdfOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pdf_options, container, false);

        ImageButton backButton = view.findViewById(R.id.backBtn);
//        ImageButton convertToDocBtn = view.findViewById(R.id.convertToDocxBtn);
//        ImageButton splitPdfBtn = view.findViewById(R.id.splitPdfBtn);
        ImageButton mergePdfBtn = view.findViewById(R.id.mergePdfbtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
//        convertToDocBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame_layout, new ConvertToDOCX());
//                transaction.addToBackStack(null); // Optionally, add to the back stack
//                transaction.commit();
//            }
//        });
//        splitPdfBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame_layout, new SplitPDF());
//                transaction.addToBackStack(null); // Optionally, add to the back stack
//                transaction.commit();
//            }
//        });
        mergePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new MergePDF());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });
        return view;
    }
}