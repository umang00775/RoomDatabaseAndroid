package com.roomdatabase.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactViewModel(
    private val dao: ContactDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.FIRST_NAME) // State flow of sort type, First name by default

    @OptIn(ExperimentalCoroutinesApi::class)  // Just to remove warning :)
    // Whenever user changes the sort type (onEvent -> SortContacts) this will automatically run
    private val _contacts = _sortType
        // The flat map will basically takes flow (In our case sort type) and when flow changes (Like sort by first name, last name, phone number etc. radio button) it will transform that emission.
        .flatMapLatest { sortType ->
            when(sortType){
                SortType.FIRST_NAME -> dao.getContactsOrderedByFirstName()
                SortType.LAST_NAME -> dao.getContactsOrderedByLastName()
                SortType.PHONE_NUMBER -> dao.getContactsOrderedByPhoneNumber()
            }
        }
        // Helps to cache the flow, WhileSubscribed --> so the upper (flapMapLatest) code only runs when there is a active subscriber, default value is empty contact list
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(ContactState()) // Host the state, State flow of empty contact state


    // This is the public state, exposed to the UI
    // Combine all this three flows in to a single flow
    // If any of these three emits a new value, the code in lambda will be executed
    val state = combine(_state, _sortType, _contacts) { state, sortType, contacts ->
        state.copy(
            contacts = contacts,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())  // 5 sec --> So old data can be deleted, so no bug remains


    // Run function when user do something (event --> Where user clicks? Button, delete, show, hide etc)
    fun onEvent(event: ContactEvent){
        when(event){
            ContactEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingContact = false
                ) }
            }
            ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()){
                    return // Do not add this cases
                }

                // id will automatically be generated
                val contact = Contact(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = phoneNumber
                )

                // Suspend function :)
                viewModelScope.launch {
                    dao.upsertContact(contact)
                }
                _state.update { it.copy(
                    isAddingContact = false, // Already added :)
                    // Reset fields to empty strings
                    firstName = "",
                    lastName = "",
                    phoneNumber = ""
                ) }
            }
            is ContactEvent.SetFirstName -> {
                _state.update { it.copy(
                    firstName = event.firstName
                ) }
            }
            is ContactEvent.SetLastName -> {
                _state.update { it.copy(
                    lastName = event.lastName
                ) }
            }
            is ContactEvent.SetPhoneNumber -> {
                _state.update { it.copy(
                    phoneNumber = event.phoneNumber
                ) }
            }
            ContactEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingContact = true
                ) }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.DeleteContact(event.contact)
                }

            }
        }
    }



    // Added because of crashing
    companion object {
        fun provideFactory(dao: ContactDao): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
                        return ContactViewModel(dao) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }

}