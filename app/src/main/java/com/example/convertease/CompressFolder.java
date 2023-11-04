package com.example.convertease;


import android.app.Activity;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.example.convertease.Data.myDBHandler;
import com.example.convertease.model.History;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompressFolder#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompressFolder extends Fragment {
    private static final int REQUEST_CODE_PICK_FOLDER = 6;
    ImageButton selectFileBtn , CompressBtn;
    String SelectedFolderPath;
    String filepath;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CompressFolder() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CompressFolder.
     */
    // TODO: Rename and change types and number of parameters
    public static CompressFolder newInstance(String param1, String param2) {
        CompressFolder fragment = new CompressFolder();
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
        View   view =inflater.inflate(R.layout.fragment_compress_folder, container, false);
        ImageButton backButton = view.findViewById(R.id.backBtn);
        selectFileBtn = view.findViewById(R.id.selectFileBtn);
        CompressBtn = view.findViewById(R.id.CompressToZipBtn);
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
                pickFolder();
            }
        });
        CompressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File sdcard = Environment.getExternalStorageDirectory();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String formattedDate = sdf.format(new Date());
                String fileName = formattedDate + ".zip";
                File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");

                String Path = convertSAFUriToValidPath(SelectedFolderPath);
                Log.d("divesh",""+SelectedFolderPath);
                compressFolderToZip("storage/emulated/0/mm");
            }
        });
        return view;
    }
    private void pickFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        activityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                Intent intent = result.getData();
                Uri uri = intent.getData();
                Log.i("Dir:" ,"" + uri);
                DocumentFile df = DocumentFile.fromTreeUri(getContext(), uri);
                Log.i("dir with file:","" + df.getUri());
                SelectedFolderPath = getPathFromSAFUri(uri);
                Log.d("divesh","" + SelectedFolderPath);
            }
        }
    });
    private String getPathFromSAFUri(Uri uri) {
        DocumentFile documentFile = DocumentFile.fromTreeUri(getContext(), uri);

        return documentFile != null ? documentFile.getUri().getPath() : null;

    }

    private String convertSAFUriToValidPath(String safUri) {
        // Split the path into segments
        String[] segments = safUri.split("/");
        // Initialize a StringBuilder to build the final path
        StringBuilder pathBuilder = new StringBuilder();
        // Iterate through the segments to construct the valid path
        for (int i = 3; i < segments.length; i++) {
            pathBuilder.append("/").append(segments[i]);
        }
        return pathBuilder.toString();
    }

    private void compressFolderToZip(String sourceFolderPath) {
        if (sourceFolderPath != null) {
            File sourceFolder = new File(sourceFolderPath);

            if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                File sdcard = Environment.getExternalStorageDirectory();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String formattedDate = sdf.format(new Date());
                String fileName = formattedDate + ".zip";
                File dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");
                File zipFile = new File(dir, fileName);
                filepath = zipFile.getAbsolutePath();

                try {
                    ZipManager.zipFolder(sourceFolderPath, zipFile.getAbsolutePath());
                    Toast.makeText(getContext(), "Folder Compressed Successfully...", Toast.LENGTH_SHORT).show();
                    updateHistory();

                    // The zipFolder function will create a zip file containing the contents of the selected folder.
                    // You can now use the zipFile as needed.
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Zip Process Failed...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Problem With Source Directory...", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public static class ZipManager {
        private static int BUFFER_SIZE = 6 * 1024;

        public static void zipFolder(String sourceFolderPath, String zipFilePath) throws IOException {
            File sourceFolder = new File(sourceFolderPath);
            if (!sourceFolder.exists() || !sourceFolder.isDirectory()) {
                throw new IllegalArgumentException("Invalid source folder path.");
            }
            FileOutputStream fos = new FileOutputStream(zipFilePath);
            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos));
            zipFolder(sourceFolder, zos, "");
            zos.close();
        }

        private static void zipFolder(File folder, ZipOutputStream zos, String baseName) throws IOException {
            File[] files = folder.listFiles();

            for (File file : files) {
                if (file.isDirectory()) {
                    zipFolder(file, zos, baseName + file.getName() + "/");
                } else {
                    FileInputStream fis = new FileInputStream(file);
                    ZipEntry zipEntry = new ZipEntry(baseName + file.getName());
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                    fis.close();
                }
            }
        }
    }
    private void updateHistory() {
        myDBHandler db = new myDBHandler (getContext());
        Calendar calendar = Calendar.getInstance();
        android.icu.text.SimpleDateFormat dateFormat = new android.icu.text.SimpleDateFormat("yyyy/MM/dd");
        String currentDate = dateFormat.format(calendar.getTime());
        History history  = new History();
        history.setName("Folder Compress");
        history.setPath(filepath);
        history.setDate(currentDate);
        db.addHistory(history);
        Log.d("dbHistory","Name "+history.getName());
    }
}