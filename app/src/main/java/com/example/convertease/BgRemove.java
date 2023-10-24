package com.example.convertease;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.ACTION_PICK;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BgRemove#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BgRemove extends Fragment {
    ImageButton imgPickButton;
    String imagePath;
    private final int REQUEST_CODE_PICK_IMAGES = 1000;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ArrayList<String> selectedImagePath = new ArrayList<>();
    private Context thiscontext;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BgRemove() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CropImage.
     */
    // TODO: Rename and change types and number of parameters
    public static BgRemove newInstance(String param1, String param2) {
        BgRemove fragment = new BgRemove();
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
        View   view =inflater.inflate(R.layout.bg_remove, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        imgPickButton = view.findViewById(R.id.selectImgBtn);
        ImageButton bgRemovebtn = view.findViewById(R.id.bgRemoveBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        imgPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImages();
            }
        });

        bgRemovebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeBg();
                updateHistory();
            }
        });
        return view;
    }

    private void removeBg() {

    }

    @SuppressLint("IntentReset")
    private void pickImages() {
        Intent iGallery = new Intent(ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        iGallery.setType("image/*");
        iGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(iGallery, REQUEST_CODE_PICK_IMAGES);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGES && data != null) {
            Uri imageUri = data.getData();
            String imagePath = getImagePathFromUri(imageUri);
            if (imagePath != null) {
                selectedImagePath.add(imagePath);
            }
        }
    }

    private String getImagePathFromUri(Uri imageUri) {
        String imagePath = null;

        if (imageUri != null) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = requireActivity().getContentResolver().query(imageUri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                imagePath = cursor.getString(columnIndex);
                cursor.close();
            }
        }

        return imagePath;
    }
    private void updateHistory() {
        myDBHandler db = new myDBHandler (thiscontext);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("Image To PDF");
        history.setPath(imagePath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }
}