package com.example.convertease;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectImageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SelectImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SelectImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectImageFragment newInstance(String param1, String param2) {
        SelectImageFragment fragment = new SelectImageFragment();
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
        View view = inflater.inflate(R.layout.fragment_select_image, container, false);

        ImageButton selectImgBtn;
        selectImgBtn = view.findViewById(R.id.selectImgBtn);
        selectImgBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Context thiscontext;
                thiscontext = container.getContext();
                imgPicker(thiscontext);
            }
        });
        return view;
    }
static class DialogConfigss extends  DialogConfigs{
    public static final int SINGLE_MODE = 1;
    public static final int MULTI_MODE = 0;
    public static final int FILE_SELECT = 0;
    public static final int DIR_SELECT = 1;
    public static final int FILE_AND_DIR_SELECT = 2;
    private static final String DIRECTORY_SEPARATOR = "/";
    private static final String STORAGE_DIR = "sdcard";
    public static final String DEFAULT_DIR = "/sdcard";
}
    private void imgPicker(Context Context ) {

        DialogProperties properties = new DialogProperties();
        DialogConfigss config = new DialogConfigss();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        //If you want to view files of all extensions then pass null to properties.extensions
        properties.extensions = null;
        //If you want to view files with specific type of extensions the pass string array to properties.extensions
        properties.extensions = new String[]{"zip","jpg","mp3","csv"};
        properties.show_hidden_files = false;

        FilePickerDialog dialog = new FilePickerDialog(Context, properties);
        dialog.setTitle("Select a File");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
            }
        });
        dialog.show();
    }
}