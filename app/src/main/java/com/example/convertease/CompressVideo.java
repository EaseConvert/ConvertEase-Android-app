    package com.example.convertease;

    import static android.app.Activity.RESULT_OK;
    import static android.content.Intent.ACTION_PICK;

    import android.annotation.SuppressLint;
    import android.content.Context;
    import android.content.Intent;
    import android.database.Cursor;
    import android.icu.text.SimpleDateFormat;
    import android.icu.util.Calendar;
    import android.net.Uri;
    import android.os.Bundle;

    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;

    import com.example.convertease.Data.myDBHandler;
    import com.example.convertease.model.History;
    import com.iceteck.silicompressorr.SiliCompressor;

    import android.os.Environment;
    import android.provider.MediaStore;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageButton;
    import android.widget.Toast;

    import java.io.File;
    import java.net.URISyntaxException;
    import java.util.Date;
    import java.util.Locale;

    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link CompressVideo#newInstance} factory method to
     * create an instance of this fragment.
     */
    public class CompressVideo extends Fragment {
        ImageButton pickVideoBtn;
        String videoPath;
        Context thiscontext;
        String newVideoPath;
        File dir;
        String compressVidePath;
        String destinationVideoPath;
        String sourceVideoPath;
        String selectedVideoPath;
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private static final String ARG_PARAM1 = "param1";
        private static final String ARG_PARAM2 = "param2";

        // TODO: Rename and change types of parameters
        private String mParam1;
        private String mParam2;

        public CompressVideo() {
            // Required empty public constructor
        }

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompressVideo.
         */
        // TODO: Rename and change types and number of parameters
        public static CompressVideo newInstance(String param1, String param2) {
            CompressVideo fragment = new CompressVideo();
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
            View view = inflater.inflate(R.layout.fragment_compress_video, container, false);
            ImageButton backButton = view.findViewById(R.id.backBtn);
            ImageButton compressVideoBtn = view.findViewById(R.id.compressVideoBtn);
            pickVideoBtn = view.findViewById(R.id.selectVideoBtn);
            thiscontext = container.getContext();
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate back to the previous fragment
                    getParentFragmentManager().popBackStack();
                }
            });
            compressVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    compressVideo(selectedVideoPath);
                }
            });
            pickVideoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pickVideo();
                }
            });
            return view;
        }

        private void updateHistory() {
            myDBHandler db = new myDBHandler(thiscontext);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String currentDate = dateFormat.format(calendar.getTime());
            History history = new History();
            history.setName("Video Compress");
            history.setPath(destinationVideoPath);
            history.setDate(currentDate);
            db.addHistory(history);
            Log.d("dbHistory", "Name " + history.getName());
        }

        @SuppressLint("IntentReset")
        private void pickVideo() {
            Intent iGallery = new Intent(ACTION_PICK);
            iGallery.setType("video/*");
            iGallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(iGallery, "Select Video"), 100);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

                Uri videoUri = data.getData();
                selectedVideoPath = videoUri != null ? videoUri.getPath() : null;
    //            sourceVideoPath = getRealPathFromUri(videoUri);
                Log.d("video compress"," ddsd"+selectedVideoPath);
                }
            else{
                Toast.makeText(thiscontext,"Permission Denied !", Toast.LENGTH_SHORT).show();
            }
            }



        private void compressVideo(String sourceVideoPath) {
            Log.d("video compress"," cv "+sourceVideoPath);

            try {
                if (sourceVideoPath != null) {
                    // Proceed with video compression
                    File sdcard = Environment.getExternalStorageDirectory();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                    String formattedDate = sdf.format(new Date());
                    String fileName = formattedDate + ".mp4";
                    dir = new File(sdcard.getAbsolutePath() + "/Download/ConvertEase/");
                    destinationVideoPath = dir.getAbsolutePath() + File.separator + fileName;
                    newVideoPath = SiliCompressor.with(thiscontext).compressVideo(sourceVideoPath, destinationVideoPath);
                    Log.d("video compress", "Video compression done. New path: " + newVideoPath);
                    Toast.makeText(thiscontext, "Video Compression Successfully...", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(thiscontext, "Source video path is null", Toast.LENGTH_SHORT).show();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
                Toast.makeText(thiscontext, "Video compression failed", Toast.LENGTH_SHORT).show();
            }
        }

        // Function to get the actual file path from Uri
        private String getRealPathFromUri(Uri uri) {
            if (videoPath != null) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    videoPath = cursor.getString(columnIndex);
                    cursor.close();
                }
            }
            return videoPath;
        }
    }