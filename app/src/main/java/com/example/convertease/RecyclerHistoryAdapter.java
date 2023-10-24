package com.example.convertease;

import static androidx.core.content.ContentProviderCompat.requireContext;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.convertease.model.History;

import java.io.File;
import java.util.List;

public class RecyclerHistoryAdapter extends RecyclerView.Adapter<RecyclerHistoryAdapter.ViewHolder> {
    Context context;
    List<History> historyList;

    RecyclerHistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.historyId.setText(String.valueOf(history.getId()));
        holder.historyName.setText(history.getName());
        holder.historyDate.setText(history.getDate());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView historyId, historyName, historyDate;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            historyId = itemView.findViewById(R.id.history_id);
            historyName = itemView.findViewById(R.id.history_name);
            historyDate = itemView.findViewById(R.id.history_date);
        }

        @Override
        public void onClick(View v) {
            Log.d("dbHistoryClick", "History Button is Clicked" + "Name " + historyName + "id " + historyId + "date" + historyDate);

            History history = historyList.get(getAdapterPosition());
            String path = history.getPath();

            openPath(path);
        }
    }

    private void openPath(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(path);
        Log.d("Divesh", "File path: " + path);
        if (context != null && file.exists()) {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            String mimeType = getMimeType(uri);

            // Set the MIME type to open files of various types
            intent.setDataAndType(uri, mimeType);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                Log.d("openFile", "error got " + uri);
                startActivity(context, intent, null);
            } catch (ActivityNotFoundException e) {
                // Handle the case where no app is available to open the file
                Toast.makeText(context, "No app can open this file", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle the case where the file does not exist
            Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to get the MIME type based on the file's URI
    private String getMimeType(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String type = contentResolver.getType(uri);

        // If the type is not null, return it; otherwise, return the default type "*/*"
        if (type != null) {
            return type;
        } else {
            return "*/*";
        }
    }

}