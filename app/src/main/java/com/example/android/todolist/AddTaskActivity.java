package com.example.android.todolist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;
import com.example.android.todolist.factory.AddTaskViewModelFactory;
import com.example.android.todolist.viewmodels.AddTaskViewModel;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extraTaskId";
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    private static final int DEFAULT_TASK_ID = -1;

    private EditText mEditText;
    private RadioGroup mRadioGroup;
    private Button mButton;
    private AppDatabase mDb;

    private int mTaskId = DEFAULT_TASK_ID;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);

            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);

                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
                final AddTaskViewModel viewModel = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);

                viewModel.getTasks().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(@Nullable TaskEntry taskEntry) {
                        viewModel.getTasks().removeObserver(this);
                        populateUI(taskEntry);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    public void onSaveButtonClicked() {
        String description = mEditText.getText().toString();
        int priority = getPriorityFromViews();
        Date date = new Date();

        final TaskEntry taskEntry = new TaskEntry(description, priority, date);

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mTaskId == DEFAULT_TASK_ID) {
                    mDb.taskDao().insertTask(taskEntry);
                } else {
                    taskEntry.setId(mTaskId);
                    mDb.taskDao().updateTask(taskEntry);
                }

                finish();
            }
        });
    }

    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }

    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        mRadioGroup = findViewById(R.id.radioGroup);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
    }

    private void populateUI(TaskEntry task) {
        if (task == null) {
            return;
        }

        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
    }
}