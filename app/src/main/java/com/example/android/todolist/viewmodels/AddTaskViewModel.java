package com.example.android.todolist.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.android.todolist.database.AppDatabase;
import com.example.android.todolist.database.TaskEntry;

public class AddTaskViewModel extends ViewModel {

    private LiveData<TaskEntry> tasks;

    public AddTaskViewModel(AppDatabase database, int taskId) {
        tasks = database.taskDao().loadTaskById(taskId);
    }

    public LiveData<TaskEntry> getTasks() {
        return tasks;
    }
}
