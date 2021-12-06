package com.cursokotlin.misnotasroom

import androidx.room.*

@Entity(tableName = "task_entity")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    var name: String = "",
    var isDone: Boolean = false
)

@Dao
interface TaskDao{
    @Query("SELECT * FROM task_entity")
    fun getAllTasks(): MutableList<TaskEntity>

    @Insert
    fun addTask(taskEntity : TaskEntity):Long

    @Query("SELECT * FROM task_entity where id like :id")
    fun getTaskById(id: Long): TaskEntity

    @Update
    fun updateTask(taskEntity: TaskEntity):Int

    @Delete
    fun deleteTask(taskEntity: TaskEntity):Int
}

@Database( version = 1, entities = [TaskEntity::class], )
abstract class TasksDatabase: RoomDatabase(){
    abstract fun taskDao():TaskDao
}