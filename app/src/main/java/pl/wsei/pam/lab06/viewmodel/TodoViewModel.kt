package pl.wsei.pam.lab06.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.wsei.pam.lab06.TodoTask
import pl.wsei.pam.lab06.data.TodoTaskDao
import pl.wsei.pam.lab06.data.TodoTaskEntity

class TodoViewModel(private val dao: TodoTaskDao) : ViewModel() {

    private val _tasks = MutableStateFlow<List<TodoTask>>(emptyList())
    val tasks: StateFlow<List<TodoTask>> = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAll().collect { entities ->
                _tasks.value = entities.map { it.toModel() }
            }
        }
    }

    fun addTask(task: TodoTask) {
        viewModelScope.launch {
            dao.insert(TodoTaskEntity.fromModel(task))
        }
    }

    fun updateTask(task: TodoTask) {
        viewModelScope.launch {
            dao.update(TodoTaskEntity.fromModel(task))
        }
    }

    fun deleteTask(task: TodoTask) {
        viewModelScope.launch {
            dao.delete(task.id)
        }
    }
}
