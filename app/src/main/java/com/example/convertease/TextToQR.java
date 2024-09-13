package com.example.convertease;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextToQR#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextToQR extends Fragment {
    String QrImagePath;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TextToQR() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TextToQR.
     */
    // TODO: Rename and change types and number of parameters
    public static TextToQR newInstance(String param1, String param2) {
        TextToQR fragment = new TextToQR();
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
        View view = inflater.inflate(R.layout.fragment_text_to_qr, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        ImageButton generateQRBtn = view.findViewById(R.id.generateQR);
        EditText QRtext = view.findViewById(R.id.QRText);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });
        generateQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textValue = QRtext.getText().toString();
                if(!textValue.isEmpty()){
                    try{
                        Bitmap qrCodeBitmap = generateQR(textValue);
                        File sdcard = Environment.getExternalStorageDirectory();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                        String formattedDate = sdf.format(new Date());
                        String fileName = formattedDate + ".jpeg";
                        File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");

                        // Check if the ConvertEase folder exists, if not, create it
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }                        File imageFile = new File(dir, fileName);
                        QrImagePath = imageFile.getAbsolutePath();
                        FileOutputStream out = new FileOutputStream(imageFile);
                        qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        out.flush();
                        out.close();
                        Toast.makeText(getContext(), "QR Generated Successfully...", Toast.LENGTH_SHORT).show();
                        updateHistory();
                    }
                    catch (Exception e){
                        Toast.makeText(getContext(), "Error With QR Generation!!!", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(getContext(), "Enter Text Value!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    private Bitmap generateQR(String data) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);

            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }


            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void updateHistory() {
        myDBHandler db = new myDBHandler (getContext());
        Calendar calendar = Calendar.getInstance();
        android.icu.text.SimpleDateFormat dateFormat = new android.icu.text.SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("Text To QR");
        history.setPath(QrImagePath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }
}