package com.example.convertease;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConvertTextToUppercase#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConvertTextToUppercase extends Fragment {
    String textFilePath;
    Context thiscontext;
    String fileData;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConvertTextToUppercase() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConvertTextToUppercase.
     */
    // TODO: Rename and change types and number of parameters
    public static ConvertTextToUppercase newInstance(String param1, String param2) {
        ConvertTextToUppercase fragment = new ConvertTextToUppercase();
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
        View   view =inflater.inflate(R.layout.fragment_convert_text_to_uppercase, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        ImageButton selectFileBtn , convertToUpperbtn;
        selectFileBtn = view.findViewById(R.id.selectTxtFileBtn);
        convertToUpperbtn = view.findViewById(R.id.ConvertCaseBtn);
        thiscontext = container.getContext();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment

                pickTextFile();
            }
        });
        convertToUpperbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
               String textOfFile = readTextFromFile(thiscontext,textFilePath);
               Log.d("Filetext","text" + textOfFile.isEmpty());
            }
        });


        return view;
    }

    private void pickTextFile() {
        Intent iGallery = new Intent(ACTION_OPEN_DOCUMENT);
        iGallery.addCategory(Intent.CATEGORY_OPENABLE);
        iGallery.setType("text/*");
        iGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(iGallery, "Select Text File"), 42);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                textFilePath = getPathFromUri(uri);
                Log.d("textdata", "File path is " + uri);
            }
        }
    }
    private String getPathFromUri(Uri fileUri) {
        String filePath = null;
        if (fileUri != null) {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            Cursor cursor = contentResolver.query(fileUri, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    if (columnIndex != -1) {
                        filePath = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
        }
        return filePath;
    }
    public String readTextFromFile(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        FileReader fr = null;
        String fileContents;

        try {
            // Open the file from the assets folder
            fr = new FileReader(fileName);

            // Create a BufferedReader to read the file
            BufferedReader bufferedReader = new BufferedReader(fr);

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(thiscontext, "File Not Found!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the FileReader object
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        fileContents = stringBuilder.toString();
        return fileContents;
    }

}