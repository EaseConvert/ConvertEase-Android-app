package com.example.convertease;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton settingButton = view.findViewById(R.id.settingBtn);
        settingButton.setOnClickListener(v -> openSettingPage());

        Button imgBtn = view.findViewById(R.id.img_btn);
        Button pdfBtn = view.findViewById(R.id.pdf_btn);
        Button videoBtn = view.findViewById(R.id.video_btn);
        Button musicBtn = view.findViewById(R.id.music_btn);
        Button textBtn = view.findViewById(R.id.text_btn);
        Button folderBtn = view.findViewById(R.id.folder_btn);

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the ImageOptionsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new ImageOptionsFragment());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the ImageOptionsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new ImageOptionsFragment());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });
        pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the ImageOptionsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new PdfOptionsFragment());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });
        musicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the ImageOptionsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new MusicOptionsFragment());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });
        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the ImageOptionsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new VideoOptionsFragment());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });
        folderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the ImageOptionsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new FolderOptionsFragment());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });
        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Switch to the ImageOptionsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new TextOptionsFragment());
                transaction.addToBackStack(null); // Optionally, add to the back stack
                transaction.commit();
            }
        });

        return view;
    }
    private void openSettingPage() {
        SettingFragment secondFragment = new SettingFragment();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, secondFragment);
        transaction.addToBackStack(null); // Optional, for back navigation
        transaction.commit();
    }
}