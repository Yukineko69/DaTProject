package com.example.datproject.recordlist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datproject.R;
import com.example.datproject.main.MainActivity;
import com.example.datproject.room.entity.RecordAudio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Tạo adapter cho RecyclerView
 */
public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> implements Filterable {
    private List<RecordAudio> recordAudioList;
    private List<RecordAudio> recordAudioListFull;
    private Context context;
    private ItemClickListener clickListener;
    private String AudioPathInDevice;
    private String EDIT_NAME = "edit_record";
    public Context getContext() {
        return context;
    }

    public RecordListAdapter(Context context, ItemClickListener clickListener, List<RecordAudio> recordAudioList) {
        this.clickListener = clickListener;
        this.recordAudioList = recordAudioList;
        recordAudioListFull = new ArrayList<>(recordAudioList);
        this.context = context;
        AudioPathInDevice = context.getApplicationContext().getExternalFilesDir("file_audio").getAbsolutePath() + "/";
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_record_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mName.setText(recordAudioList.get(position).getRecordName());
        holder.mDateTime.setText(recordAudioList.get(position).getDateTime());
        holder.mTime.setText(recordAudioList.get(position).getRecordTime());
    }

    @Override
    public int getItemCount() {
        return recordAudioList.size();
    }


    /*
    Lọc các bản ghi khi search tên
     */
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<RecordAudio> searchList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    searchList.addAll(recordAudioListFull);
                } else {
                    String searchName = constraint.toString().toLowerCase().trim();
                    for (RecordAudio record : recordAudioListFull) {
                        if (record.getRecordName().toLowerCase().contains(searchName)) {
                            searchList.add(record);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = searchList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                recordAudioList.clear();
                recordAudioList.addAll((List)results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView mName, mDateTime, mTime;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.lbl_name_record);
            mDateTime = itemView.findViewById(R.id.lbl_date_time);
            mTime = itemView.findViewById(R.id.lbl_final_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.clickItem(getLayoutPosition());
                }
            });
        }

    }

    //xóa record
    public void deleteItem(int position) {

        try{
            File file = new File(AudioPathInDevice + recordAudioList.get(position).getRecordName().replaceAll("\\s+", "") + ".3gp");
            if(file.exists()){
                boolean result = file.delete();
                Log.e(EDIT_NAME, "Application able to delete the file: " + result);
                if (result)
                    file.delete();
            }else{
                Log.e(EDIT_NAME, "Application doesn't able to delete the file");
            }
        }catch (Exception e){
            Log.e(EDIT_NAME, "Exception while deleting file " + e.getMessage());
        }
        MainActivity.database.recordAudioDao().deleteRecord(recordAudioList.get(position));
        recordAudioList.remove(position);
        notifyItemRemoved(position);
    }

    //Đổi tên record
    public void changeItem(int position, String name) {
        try {
            File currentFile = new File(AudioPathInDevice + recordAudioList.get(position)
                    .getRecordName().replaceAll("\\s+", "") + ".3gp");

            File editFile = new File(AudioPathInDevice + name.replaceAll("\\s+", "") + ".3gp");
            if (currentFile.exists()) {
                boolean result = currentFile.renameTo(editFile);
                Log.e(EDIT_NAME, "Application able to rename the file: " + result);
                if (result)
                    currentFile.renameTo(editFile);
            } else {
                Log.e(EDIT_NAME, "Application doesn't able to rename the file");
            }
        } catch (Exception e) {
            Log.e(EDIT_NAME, "Exception while rename file " + e.getMessage()); }

        recordAudioList.get(position).setRecordName(name);
        RecordAudio newRecord = recordAudioList.get(position);
        MainActivity.database.recordAudioDao().updateRecord(newRecord);
        notifyItemChanged(position);
    }
}
