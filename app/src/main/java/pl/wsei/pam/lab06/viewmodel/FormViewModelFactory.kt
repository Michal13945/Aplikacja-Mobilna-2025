package pl.wsei.pam.lab06.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.wsei.pam.lab06.data.TodoRepository
import pl.wsei.pam.lab06.util.CurrentDateProvider

class FormViewModelFactory(
    private val repository: TodoRepository,
    private val currentDateProvider: CurrentDateProvider
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            return FormViewModel(repository, currentDateProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
