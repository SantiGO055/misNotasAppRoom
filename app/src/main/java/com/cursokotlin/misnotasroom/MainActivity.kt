package com.cursokotlin.misnotasroom

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cursokotlin.misnotasroom.databinding.ActivityMainBinding
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: TasksAdapter
    lateinit var recyclerView: RecyclerView

    //creo lista de tipo TaskEntity que luego inicializare cuando llame a la base
    lateinit var tasks: MutableList<TaskEntity>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tasks = ArrayList()
        getTasks()
        binding.btnAddTask.setOnClickListener{
            addTask(TaskEntity(name = binding.etTask.text.toString()))
        }
    }

    fun addTask(task:TaskEntity){
        doAsync {
            val id = MisNotasApp.database.taskDao().addTask(task)
            val recoveryTask = MisNotasApp.database.taskDao().getTaskById(id)
            uiThread {
                tasks.add(recoveryTask)
                adapter.notifyItemInserted(tasks.size)
                clearFocus()
                hideKeyboard()
            }
        }
    }
    fun clearFocus(){
        binding.etTask.setText("")
    }
    fun Context.hideKeyboard(){
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken,0)
    }
    fun getTasks(){
        //hago asyncrono la llamada a la base
        doAsync {
            //obtengo de la base las tasks
            tasks = MisNotasApp.database.taskDao().getAllTasks()
            uiThread { //vuelvo al hilo principal para actualizar la vista de recyclerView
                setUpRecyclerView(tasks)
            }
        }
    }
    fun setUpRecyclerView(tasks: MutableList<TaskEntity>){
        adapter = TasksAdapter(tasks, { updateTask(it) }, { deleteTask(it) })
        recyclerView = binding.rvTask
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
    fun updateTask(task: TaskEntity){
        doAsync {
            task.isDone = !task.isDone
            MisNotasApp.database.taskDao().updateTask(task)
        }
    }
    fun deleteTask(task: TaskEntity){
        doAsync {
            val position = tasks.indexOf(task)
            MisNotasApp.database.taskDao().deleteTask(task)
            tasks.remove(task) //borro de la lista la task
            uiThread {
                adapter.notifyItemRemoved(position)
            }
        }
    }
}