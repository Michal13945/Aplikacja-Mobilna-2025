package pl.wsei.pam.lab06.viewmodel

import androidx.lifecycle.ViewModel
import pl.wsei.pam.lab06.TodoTask
import pl.wsei.pam.lab06.data.TodoRepository
import pl.wsei.pam.lab06.util.CurrentDateProvider
import java.time.LocalDate

class FormViewModel(
    private val repository: TodoRepository,
    private val currentDateProvider: CurrentDateProvider
) : ViewModel() {

    fun addTask(task: TodoTask) {
        if (validate(task)) {
            repository.insert(task)
        }
    }

    fun validate(task: TodoTask): Boolean {
        return try {
            val taskDate = LocalDate.parse(task.deadline)
            taskDate.isAfter(currentDateProvider.currentDate)
        } catch (e: Exception) {
            false
        }
    }
}
