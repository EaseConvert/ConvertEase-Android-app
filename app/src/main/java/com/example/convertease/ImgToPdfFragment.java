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
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ImgToPdfFragment extends Fragment {
    private Context thiscontext;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ArrayList<String> selectedImagePath = new ArrayList<>();
    private final int REQUEST_CODE_PICK_IMAGES = 1000;
    String pdfFilePath;
    ImageButton imgPickButton;

    public ImgToPdfFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.img_to_pdf_fragment, container, false);
        imgPickButton = view.findViewById(R.id.selectImgBtn);
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
                updateHistory();
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

    private void updateHistory() {
        myDBHandler db = new myDBHandler (thiscontext);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("Image To PDF");
        history.setPath(pdfFilePath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }


    @SuppressLint("IntentReset")
    private void pickImages() {
        Intent iGallery = new Intent(ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        iGallery.setType("image/*");
        iGallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(iGallery, REQUEST_CODE_PICK_IMAGES);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PICK_IMAGES) {
                if (data != null) {
                    ClipData clipData = data.getClipData();

                    if (clipData != null) {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            Uri imageUri = clipData.getItemAt(i).getUri();
                            String imagePath = getImagePathFromUri(imageUri);
                            if (imagePath != null) {
                                selectedImagePath.add(imagePath);
                            }
                        }
                    } else {
                        Uri imageUri = data.getData();
                        String imagePath = getImagePathFromUri(imageUri);
                        Log.d("datais", "data is  " + imagePath);
                        if (imagePath != null) {
                            selectedImagePath.add(imagePath);
                        }
                    }
                }
            }
        }
    }

    private String getImagePathFromUri(Uri imageUri) {
        String imagePath = null;
        if (imageUri != null) {
            ContentResolver contentResolver = requireActivity().getContentResolver();
            Cursor cursor = contentResolver.query(imageUri, null, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    if (columnIndex != -1) {
                        imagePath = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
        }

        return imagePath;

    }


    private void generatePdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size page

        for (int i = 0; i < selectedImagePath.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath.get(i));

            if (bitmap != null) {
                PdfDocument.Page page = document.startPage(pageInfo);

                Canvas canvas = page.getCanvas();
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                canvas.drawPaint(paint);

                // Calculate the scaling factor to fit the image to the A4 page
                float scaleFactor = Math.min(
                        (float) pageInfo.getPageWidth() / bitmap.getWidth(),
                        (float) pageInfo.getPageHeight() / bitmap.getHeight()
                );

                // Apply the scaling factor to the bitmap
                bitmap = Bitmap.createScaledBitmap(bitmap,
                        (int) (bitmap.getWidth() * scaleFactor),
                        (int) (bitmap.getHeight() * scaleFactor),
                        true);

                // Calculate the position to center the image on the page
                float x = (pageInfo.getPageWidth() - bitmap.getWidth()) / 2;
                float y = (pageInfo.getPageHeight() - bitmap.getHeight()) / 2;

                canvas.drawBitmap(bitmap, x, y, null);
                document.finishPage(page);
            } else {
                Log.e("PDF Generation", "Failed to decode bitmap for image " + i);
            }
        }

        File sdcard = Environment.getExternalStorageDirectory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String formattedDate = sdf.format(new Date());
        String fileName = formattedDate + ".pdf";
        File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");

        if (dir.exists() || dir.mkdirs()) {
            File file = new File(dir, fileName);

            try {
                document.writeTo(Files.newOutputStream(file.toPath()));
                document.close();
                Toast.makeText(thiscontext, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
                pdfFilePath = file.getAbsolutePath();
            } catch (Exception e) {
                Log.e("PDF Generation", "Error in creating PDF: " + e.getMessage());
                Toast.makeText(thiscontext, "Error in creating PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("PDF Generation", "Failed to create directory.");
            Toast.makeText(thiscontext, "Failed to create directory for PDF.", Toast.LENGTH_SHORT).show();
        }
    }


}
