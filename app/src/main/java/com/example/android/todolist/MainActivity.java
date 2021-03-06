package com.example.android.todolist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;
import com.example.android.todolist.viewmodels.MainViewModel;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRvMainActivity;
    private TaskAdapter mAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRvMainActivity = findViewById(R.id.rvMainActivity);
        mRvMainActivity.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new TaskAdapter(this, this);
        mRvMainActivity.setAdapter(mAdapter);
        mRvMainActivity.addItemDecoration(new DividerItemDecoration(getApplicationContext(), VERTICAL));

        mDb = AppDatabase.getInstance(getApplicationContext());

        setItemTouchHelper();

        setUpViewModel();

    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);

        startActivity(intent);
    }

    private void setItemTouchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntry> tasks = mAdapter.getTasks();
                        mDb.taskDao().deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRvMainActivity);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });
    }

    private void setUpViewModel() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {
                mAdapter.setTasks(taskEntries);
            }
        });
    }
}
