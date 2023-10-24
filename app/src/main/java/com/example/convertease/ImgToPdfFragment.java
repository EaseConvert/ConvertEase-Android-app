package com.example.convertease;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ImgToPdfFragment extends Fragment {
    private Context thiscontext;
    private ArrayList<String> selectedImagePath = new ArrayList<>();
    private final int REQUEST_CODE_PICK_IMAGES = 1;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public ImgToPdfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.img_to_pdf_fragment, container, false);
        ImageButton imgPickButton = view.findViewById(R.id.selectImgBtn);
        ImageButton pdfMakeButton = view.findViewById(R.id.generatePDF);
        thiscontext = container.getContext();
        imgPickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImages();
            }
        });

        pdfMakeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePdf();
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                            Intent data = result.getData();
                            handleImagePickerResult(data);
                        }
                    }
                });

        ImageButton backButton = view.findViewById(R.id.backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        return view;

    }

    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        imagePickerLauncher.launch(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        String imagePath = getImagePathFromUri(imageUri);
                        if (imagePath != null) {
                            selectedImagePath.add(imagePath);
                        } else {
                            Log.d("PDF", "Failed to get image path for image " + i);
                        }
                    }
                } else {
                    Uri imageUri = data.getData();
                    String imagePath = getImagePathFromUri(imageUri);
                    if (imagePath != null) {
                        selectedImagePath.add(imagePath);
                    } else {
                        Log.d("PDF", "Failed to get image path for the selected image." + imageUri);
                    }
                }
            }
        }
    }


    private void generatePdf(){
        PdfDocument document = new PdfDocument();
        for (int i = 0; i<selectedImagePath.size(); i++){
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath.get(i));
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder
                    (bitmap.getWidth(),bitmap.getHeight(),1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.parseColor("#ffffff"));
            canvas.drawPaint(paint);

            bitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
            canvas.drawBitmap(bitmap,0,0,null);
            document.finishPage(page);

        }
        Log.d("PDF","paths"+selectedImagePath);
        File sdcard = Environment.getExternalStorageDirectory();
        File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");

        if (dir.exists()){
            File file = new File(dir,"FileName"+".pdf");

            try {

                document.writeTo(Files.newOutputStream(file.toPath()));
                document.close();
                Toast.makeText(thiscontext, "IMAGES CONVERTED TO PDF...", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(thiscontext, "Error in creating file..." +e, Toast.LENGTH_SHORT).show();
            }
        }else {
            dir.mkdir();
            File file = new File(dir,"FileName"+".pdf");
            try {

                document.writeTo(Files.newOutputStream(file.toPath()));
                document.close();
                Toast.makeText(thiscontext, "IMAGES CONVERTED TO PDF...", Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(thiscontext, "Error in creating file..." +e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getImagePathFromUri(Uri imageUri) {
        String imagePath = null;

        if (imageUri != null) {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            Cursor cursor = contentResolver.query(imageUri, null, null, null, null);

            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                if (columnIndex != -1) {
                    imagePath = cursor.getString(columnIndex);
                }

                cursor.close();
            }
        }
        return imagePath;
    }

    private void handleImagePickerResult(Intent data) {
        // Handle the result of the image picker here
    }
}
