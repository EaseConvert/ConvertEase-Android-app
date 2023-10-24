package com.example.convertease;

import static android.app.Activity.RESULT_OK;
import static android.app.ProgressDialog.show;
import static android.content.Intent.ACTION_PICK;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import java.io.ByteArrayOutputStream;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompressImage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompressImage extends Fragment {
    ImageButton imgPickButton;
    static Context thiscontext;
    private final int REQUEST_CODE_PICK_IMAGES = 1000;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ArrayList<String> selectedImagePath = new ArrayList<>();
    String imagePath;

    String NewImgPath;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static String compressedImagePath = null;
    public CompressImage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompressImage.
     */
    // TODO: Rename and change types and number of parameters
    public static CompressImage newInstance(String param1, String param2) {
        CompressImage fragment = new CompressImage();
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
        View   view =inflater.inflate(R.layout.fragment_compress_image, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        imgPickButton = view.findViewById(R.id.selectImgBtn);
        ImageButton compressImageBtn = view.findViewById(R.id.compressImageBtn);
        thiscontext = container.getContext();
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

        compressImageBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (imagePath != null) {
                    Bitmap bitmap = decodeFile(imagePath);
                    int targetFileSize = 200 * 1024;
                    NewImgPath =  compressImage(bitmap, targetFileSize);
                    Log.d("thislog","path " + NewImgPath);
                    updateHistory();
                } else {
                    Toast.makeText(thiscontext, "Please select an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private void updateHistory() {
        myDBHandler db = new myDBHandler (thiscontext);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("Compress Image");
        history.setPath(compressedImagePath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
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
                Log.d("imgPath","path is   " + imagePath);
            }
        }
    }

    private String getImagePathFromUri(Uri imageUri) {
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
    public static Bitmap decodeFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // You can choose another config if needed
        return BitmapFactory.decodeFile(filePath, options);
    }
    private String compressImage(Bitmap originalBitmap, int targetQuality) {
        String compressedImagePath = null;
        try {
            Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
            int quality = 80;
            int targetSizeKB = 1000 * 1024; // Target size in kilobytes (e.g., 50KB)
            File sdcard = Environment.getExternalStorageDirectory();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String formattedDate = sdf.format(new Date());
            String fileName = formattedDate + ".jpeg";
            File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");
            File imageFile = new File(dir, fileName);

            FileOutputStream fos = new FileOutputStream(imageFile);

            do {
                originalBitmap.compress(format, quality, fos);
                quality -= 10;
            } while (imageFile.length() > targetSizeKB && quality > targetQuality);

            fos.close();
            compressedImagePath = imageFile.getAbsolutePath();

            if (imageFile.length() <= targetSizeKB) {
                // Notify success
                Toast.makeText(thiscontext, "Image compression successful", Toast.LENGTH_SHORT).show();

            } else {
                // Notify failure

                Toast.makeText(thiscontext, "Image compression successful",Toast.LENGTH_SHORT).show();
                compressedImagePath = null; // Set to null to indicate failure
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Notify failure
            Toast.makeText(thiscontext, "Image compression failed",Toast.LENGTH_SHORT).show();
            compressedImagePath = null; // Set to null to indicate failure
        }
        Log.d("thislog","path " + compressedImagePath);
        return compressedImagePath;

    }

}