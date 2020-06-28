package com.example.datproject.recordlist;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.datproject.R;
import com.example.datproject.databinding.FragmentRecordListBinding;
import com.example.datproject.main.MainActivity;
import com.example.datproject.room.entity.RecordAudio;

import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;

/**
 * Fragment có chức năng hiển thị danh sách các bản ghi âm
 * Fragment dùng RecyclerView để hiển thị
 */
public class RecordListFragment extends Fragment implements ItemClickListener {
    private FragmentRecordListBinding binding;
    private RecordListAdapter listAdapter;
    //private List<RecordAudio> recordSearchList;
    private List<RecordAudio> recordAudioList;
    private int position;
    private final int REQUEST_CODE = 0;

    public RecordListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecordListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);
        initData();
        binding.recyclerView.setItemAnimator(new SlideInDownAnimator());
        DividerItemDecoration dividerHorizontal =
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        dividerHorizontal.setDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.devider_recycler_view));
        binding.recyclerView.addItemDecoration(dividerHorizontal);

        listAdapter = new RecordListAdapter(getActivity().getApplicationContext(), this, recordAudioList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeControllerList(listAdapter));

        binding.recyclerView.setAdapter(listAdapter);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
        return view;
    }

    private void initData() {
        recordAudioList = MainActivity.database.recordAudioDao().getAllRecord();
    }

    /*
    Khi click vào 1 item
     */
    @Override
    public void clickItem(int pos) {
        position = pos;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DialogPlayRecord dialog = DialogPlayRecord.newInstance(recordAudioList.get(position).getRecordName(),
                                                            recordAudioList.get(position).getRecordTime());
        dialog.setTargetFragment(this, REQUEST_CODE);
        dialog.setCancelable(false);
        dialog.show(fm, null);
    }

    /*
    Nhận kết quả trả về từ Dialog
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            String name = data.getStringExtra("STRING_RESULT").trim();
            boolean contain = false;
            for(RecordAudio record : recordAudioList)
                if (record.getRecordName().equals(name)) {
                    contain = true;
                    break;
                }

            if (!contain) {
                if (!name.equals(recordAudioList.get(position).getRecordName()))
                    listAdapter.changeItem(position, name);
                Toast.makeText(getActivity(), "Complete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Tên bản ghi đã tồn tại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.recordlist_menu, menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listAdapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
}
