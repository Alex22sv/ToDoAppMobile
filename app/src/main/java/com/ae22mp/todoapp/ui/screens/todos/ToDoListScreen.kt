package com.ae22mp.todoapp.ui.screens.todos


import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ae22mp.todoapp.model.ToDo
import com.ae22mp.todoapp.ui.theme.*
import com.ae22mp.todoapp.ui.viemodels.ToDoViewModel
import com.ae22mp.todoapp.ui.viemodels.ToDoViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.Boolean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoListScreen(
    /*navController: NavController,*/
    viewModel: ToDoViewModel = viewModel(factory = ToDoViewModelFactory(LocalContext.current.applicationContext as Application))
) {
    val todos by viewModel.allToDos.observeAsState(initial = emptyList())
    var showNewToDoDialog: MutableState<Boolean> = remember { mutableStateOf(true) }
    var showDatePickerDialog: MutableState<Boolean> = remember { mutableStateOf(true) }
    var toDoTitle = remember { mutableStateOf("") }
    var toDoDescription = remember { mutableStateOf("") }
    var toDoDate = remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showNewToDoDialog.value = false
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .background(GrayColor),
        ) {
            Text(
                modifier = Modifier
                    .padding(16.dp),
                style = TextStyle(
                    color = LightGrayColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    textDecoration = TextDecoration.Underline
                ),
                text = "To Do List"
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                val sortedTodos = todos.sortedWith(
                    compareBy<ToDo> { it.isDone }.thenBy { it.finishDate }
                )
                items(sortedTodos) { todo ->
                    ToDoCard(
                        todo = todo,
                        viewModel = viewModel,
                        onCheckChange = {
                            viewModel.toggleIsDone(todo)
                        }
                    )

                }
            }
            if (!showNewToDoDialog.value) {
                AddNewToDoDialog(
                    showNewToDoDialog,
                    viewModel,
                    toDoTitle,
                    toDoDescription,
                    toDoDate,
                    showDatePickerDialog
                )
            }
            if (!showDatePickerDialog.value) {
                val datePickerState =
                    rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog.value = true },
                    confirmButton = {
                        Button(
                            onClick = {
                                showDatePickerDialog.value = true
                                toDoDate.value =
                                    datePickerState.selectedDateMillis?.toString() ?: ""
                            }
                        ) {
                            Text("Confirm")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToDoCard(todo: ToDo, viewModel: ToDoViewModel, onCheckChange: (Boolean) -> Unit) {
    var backgroundColor = if (todo.isDone) DarkGreenColor else DarkRedColor
    var showDeleteConfirmation = remember { mutableStateOf(false) }
    var showUpdateDialog: MutableState<Boolean> = remember { mutableStateOf(true) }
    var showDatePicker: MutableState<Boolean> = remember { mutableStateOf(true) }
    var toDoTitle = remember { mutableStateOf(todo.title) }
    var toDoDescription = remember { mutableStateOf(todo.description) }
    var toDoDate = remember { mutableStateOf(todo.finishDate.toString()) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .fillMaxHeight(1.0f),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = todo.title,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)
            )
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = {
                    onCheckChange(it)
                }
            )
        }
        Text(
            text = todo.description,
            style = TextStyle(fontStyle = FontStyle.Italic, fontSize = 16.sp),
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 0.dp, bottom = 8.dp)
        )

        var formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC-6")
        Text(
            text = "Ending date: " + formatter.format(todo.finishDate),
            style = TextStyle(fontSize = 12.sp),
            modifier = Modifier.padding(start = 16.dp, end = 0.dp, top = 0.dp, bottom = 0.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 16.dp, top = 0.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Created at " + formatter.format(todo.creationDate),
                style = TextStyle(fontSize = 12.sp, fontStyle = FontStyle.Italic),
                modifier = Modifier.padding(start = 4.dp, end = 0.dp, top = 32.dp, bottom = 0.dp)
            )
            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Blue
                ),
                onClick = {
                    toDoTitle.value = todo.title
                    toDoDescription.value = todo.description
                    toDoDate.value = todo.finishDate.toString()
                    showUpdateDialog.value = false
                }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Update")
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red
                ),
                onClick = {
                    /*viewModel.delete(todo)*/
                    showDeleteConfirmation.value = true
                }
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
        if (showDeleteConfirmation.value) {
            ConfirmToDoDeleteDialog(showDeleteConfirmation,
                viewModel,
                todo
            )
        }
        if (!showUpdateDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    showUpdateDialog.value = true
                    toDoTitle.value = todo.title
                    toDoDescription.value = todo.description
                    toDoDate.value = todo.finishDate.toString()
                },
                title = { Text(text = "Update To Do") },
                text = {
                    Column {
                        TextField(
                            value = toDoTitle.value,
                            onValueChange = { toDoTitle.value = it },
                            label = { Text("Title") },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                        )
                        TextField(
                            value = toDoDescription.value,
                            onValueChange = { toDoDescription.value = it },
                            label = { Text("Description") },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                        )
                        Button(
                            onClick = { showDatePicker.value = false }
                        ) {
                            Text("Update Date")
                        }
                        if (toDoDate.value != "") {
                            var formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC-6")
                            Text(text = "Selected date: " + formatter.format(toDoDate.value.toLong()))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (toDoDate.value == "") {
                            toDoDate.value = System.currentTimeMillis().toString()
                        }
                        viewModel.update(
                            todo.copy(
                                title = toDoTitle.value,
                                description = toDoDescription.value,
                                finishDate = toDoDate.value.toLong()
                            )
                        )
                        showUpdateDialog.value = true
                        clearToDoInputs(toDoTitle, toDoDescription, toDoDate)
                        /*LaunchedEffect(true) {
                            Toast.makeText(LocalContext.current, "To Do updated successfully", Toast.LENGTH_SHORT).show()
                        }*/
                    }) {
                        Text("Update")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showUpdateDialog.value = true
                        clearToDoInputs(toDoTitle, toDoDescription, toDoDate)
                    }) {
                        Text("Cancel")
                    }
                }

            )
        }
        if (!showDatePicker.value) {
            val datePickerState =
                rememberDatePickerState(initialSelectedDateMillis = todo.finishDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = true },
                confirmButton = {
                    Button(
                        onClick = {
                            showDatePicker.value = true
                            toDoDate.value = datePickerState.selectedDateMillis?.toString() ?: ""
                        }
                    ) {
                        Text("Confirm")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun AddNewToDoDialog(
    showNewToDoDialog: MutableState<Boolean>,
    viewModel: ToDoViewModel,
    toDoTitle: MutableState<String>,
    toDoDescription: MutableState<String>,
    toDoDate: MutableState<String>,
    showDatePickerDialog: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            showNewToDoDialog.value = true
            clearToDoInputs(toDoTitle, toDoDescription, toDoDate)
        },
        title = { Text(text = "New To Do") },
        text = {
            Column {
                TextField(
                    value = toDoTitle.value,
                    onValueChange = { toDoTitle.value = it },
                    label = { Text("Title") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                TextField(
                    value = toDoDescription.value,
                    onValueChange = { toDoDescription.value = it },
                    label = { Text("Description") },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                )
                Button(
                    onClick = { showDatePickerDialog.value = false }
                ) {
                    Text("Select Date")
                }
                if (toDoDate.value != "") {
                    var formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    formatter.timeZone = TimeZone.getTimeZone("UTC-6")
                    Text(text = "Selected date: " + formatter.format(toDoDate.value.toLong()))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (toDoDate.value == "") {
                    toDoDate.value = System.currentTimeMillis().toString()
                }
                viewModel.insert(
                    ToDo(
                        title = toDoTitle.value,
                        description = toDoDescription.value,
                        finishDate = toDoDate.value.toLong()
                    )
                )
                showNewToDoDialog.value = true
                clearToDoInputs(toDoTitle, toDoDescription, toDoDate)
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showNewToDoDialog.value = true
                clearToDoInputs(toDoTitle, toDoDescription, toDoDate)
            }) {
                Text("Cancel")
            }
        }

    )
}

@Composable
fun ConfirmToDoDeleteDialog(
    showDeleteConfirmation: MutableState<Boolean>,
    viewModel: ToDoViewModel,
    todo: ToDo
) {
    AlertDialog(
        onDismissRequest = {
            showDeleteConfirmation.value = false
        },
        title = { Text(text = "Delete?") },
        text = {
            Text(text = "Are you sure you want to delete this to do?")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.delete(todo)
                    showDeleteConfirmation.value = false
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                showDeleteConfirmation.value = false
            }) {
                Text("Cancel")
            }
        }

    )
}

fun clearToDoInputs(
    toDoTitle: MutableState<String>,
    toDoDescription: MutableState<String>,
    toDoDate: MutableState<String>
) {
    toDoTitle.value = ""
    toDoDescription.value = ""
    toDoDate.value = ""
}