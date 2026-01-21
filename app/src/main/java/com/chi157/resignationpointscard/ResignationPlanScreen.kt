package com.chi157.resignationpointscard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chi157.resignationpointscard.data.TodoItem
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun ResignationPlanScreen(
    viewModel: AppViewModel,
    navController: androidx.navigation.NavHostController
) {
    val settings by viewModel.settings.collectAsState()
    val allTodos by viewModel.allTodos.collectAsState()
    
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    var showTargetFundDialog by remember { mutableStateOf(false) }
    var showCurrentFundDialog by remember { mutableStateOf(false) }
    
    var showEditorDialog by remember { mutableStateOf(false) }
    var editingTodo by remember { mutableStateOf<TodoItem?>(null) }
    var showCompleted by remember { mutableStateOf(true) } // 預設全部顯示

    Scaffold(
        bottomBar = {
            MainBottomNavigation(currentRoute = Screen.Plan.route, onNavigate = { route ->
                navController.navigate(route)
            })
        },
        containerColor = Color(0xFF2C3E50) // 深色背景 (設計稿色)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                }
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "離職計畫",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 1. 自由需要準備 卡片 (橫向極簡版)
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✈️", fontSize = 28.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                            text = "自由需要準備",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                        Text(
                            text = "好好規劃，邁向更好的未來",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // 2. 自由預備金 卡片
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💵", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(text = "自由預備金", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val target = settings?.targetFund ?: 0L
                    val current = settings?.currentFund ?: 0L
                    val progress = if (target > 0) current.toFloat() / target.toFloat() else 0f
                    val percentage = (progress * 100).toInt()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "目標進度", fontSize = 14.sp, color = Color.Gray)
                        Text(text = "$percentage%", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ECDC4))
                    }
                    
                    LinearProgressIndicator(
                        progress = progress.coerceIn(0f, 1f),
                        modifier = Modifier.fillMaxWidth().height(12.dp).padding(vertical = 4.dp),
                        color = Color(0xFF2C3E50),
                        trackColor = Color(0xFFEEEEEE),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    FundEditField(
                        label = "目標金額",
                        amount = target,
                        onClick = { showTargetFundDialog = true }
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    FundEditField(
                        label = "目前已存",
                        amount = current,
                        onClick = { showCurrentFundDialog = true }
                    )
                }
            }

            // 3. 履歷準備 卡片
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📄", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                        Text(text = "履歷準備", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleResumeReady(!(settings?.isResumeReady ?: false)) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = settings?.isResumeReady ?: false,
                            onCheckedChange = { viewModel.toggleResumeReady(it) },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2C3E50))
                        )
                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(text = "履歷準備好了嗎？", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            Text(text = "記得更新履歷和作品集", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // 4. 待辦事項 卡片
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    val undoneCount = allTodos.count { !it.isDone }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📋", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                            Text(text = "待辦事項", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50))
                        }
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 1. 待辦計數 (黃色)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFFFD166), CircleShape)
                                    .border(1.dp, Color.Black, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "$undoneCount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            // 2. 顯示/隱藏已完成按鈕 (淺灰色)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFF0F0F0), CircleShape)
                                    .border(1.dp, Color(0xFFCCCCCC), CircleShape)
                                    .clickable { showCompleted = !showCompleted },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (showCompleted) Icons.Default.Check else Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    tint = Color(0xFF2C3E50),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            // 3. 新增待辦按鈕 (深藍色)
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFF2C3E50), CircleShape)
                                    .clickable { 
                                        editingTodo = null
                                        showEditorDialog = true 
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 💡 點擊編輯提示
                    if (allTodos.isNotEmpty()) {
                        Text(
                            text = "💡 點擊事項可以編輯內容與時間",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    // 待辦清單 (複雜自定義排序)
                    val filteredTodos = if (showCompleted) allTodos else allTodos.filter { !it.isDone }
                    
                    if (filteredTodos.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📝", fontSize = 40.sp, color = Color.LightGray)
                            val emptyText = if (allTodos.isEmpty()) "還沒有待辦事項" else "沒有未完成事項"
                            Text(text = emptyText, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                        }
                    } else {
                        // 排序邏輯：
                        // 1. 已完成沈底 (isDone)
                        // 2. 類別排序 (categoryRank): 無時間(0) -> 有期限(1) -> 僅開始時間(2)
                        // 3. 時間本身排序 (早到晚)
                        val sortedTodos = filteredTodos.sortedWith(
                            compareBy<TodoItem> { it.isDone }
                                .thenBy { 
                                    when {
                                        it.startTimeMillis == null && it.deadlineTimeMillis == null -> 0
                                        it.deadlineTimeMillis != null -> 1
                                        else -> 2 // 僅有 startTime
                                    }
                                }
                                .thenBy { it.deadlineTimeMillis ?: it.startTimeMillis ?: it.createdAt }
                        )
                        
                        sortedTodos.forEach { item ->
                            TodoRow(
                                item = item,
                                onToggle = { viewModel.updateTodo(item.copy(isDone = it)) },
                                onDelete = { viewModel.deleteTodo(item) },
                                onClick = { 
                                    editingTodo = item
                                    showEditorDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // 金額編輯彈窗
        if (showTargetFundDialog) {
            FundInputDialog(
                title = "目標金額",
                initialValue = settings?.targetFund ?: 0L,
                onConfirm = { viewModel.saveTargetFund(it); showTargetFundDialog = false },
                onDismiss = { showTargetFundDialog = false }
            )
        }
        if (showCurrentFundDialog) {
            FundInputDialog(
                title = "目前已存",
                initialValue = settings?.currentFund ?: 0L,
                onConfirm = { viewModel.saveCurrentFund(it); showCurrentFundDialog = false },
                onDismiss = { showCurrentFundDialog = false }
            )
        }

        if (showEditorDialog) {
            TodoEditorDialog(
                item = editingTodo,
                onDismiss = { showEditorDialog = false },
                onConfirm = { todo ->
                    if (todo.id == 0) {
                        viewModel.addTodo(todo)
                    } else {
                        viewModel.updateTodo(todo)
                    }
                    showEditorDialog = false
                }
            )
        }
    }
}

@Composable
fun FundEditField(label: String, amount: Long, onClick: () -> Unit) {
    Column {
        Text(text = label, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(2.dp, Color.Black, RoundedCornerShape(4.dp))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$ $amount", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun TodoRow(item: TodoItem, onToggle: (Boolean) -> Unit, onDelete: () -> Unit, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(4.dp)).clickable(onClick = onClick),
        color = if (item.isDone) Color(0xFFF9F9F9) else Color.White,
        shape = RoundedCornerShape(4.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4ECDC4))
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = item.content,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else null,
                    color = if (item.isDone) Color.Gray else Color.Black,
                    fontWeight = FontWeight.Medium
                )
                
                if (item.startTimeMillis != null || item.deadlineTimeMillis != null) {
                    val sdf = SimpleDateFormat("MM.dd HH:mm", Locale.TAIWAN)
                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        if (item.startTimeMillis != null) {
                            Text(
                                text = "🚩 ${sdf.format(Date(item.startTimeMillis))} 開始",
                                fontSize = 11.sp,
                                color = Color(0xFF3498DB)
                            )
                        }
                        if (item.deadlineTimeMillis != null) {
                            Text(
                                text = "⌛ ${sdf.format(Date(item.deadlineTimeMillis))} 期限",
                                fontSize = 11.sp,
                                color = Color(0xFFE74C3C),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun TodoEditorDialog(
    item: TodoItem?,
    onDismiss: () -> Unit,
    onConfirm: (TodoItem) -> Unit
) {
    var content by remember { mutableStateOf(item?.content ?: "") }
    var startTime by remember { mutableStateOf(item?.startTimeMillis) }
    var deadlineTime by remember { mutableStateOf(item?.deadlineTimeMillis) }
    
    val context = LocalContext.current
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "新增待辦" else "編輯待辦", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("事項內容") },
                    placeholder = { Text("例如：整理交接文檔") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 開始時間
                DateTimeSelector(
                    label = "開始做時間 (選填)",
                    timeMillis = startTime,
                    onTimeSelected = { startTime = it },
                    onClear = { startTime = null },
                    icon = "🚩",
                    iconColor = Color(0xFF3498DB)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 期限
                DateTimeSelector(
                    label = "預計完工期限 (選填)",
                    timeMillis = deadlineTime,
                    onTimeSelected = { deadlineTime = it },
                    onClear = { deadlineTime = null },
                    icon = "⌛",
                    iconColor = Color(0xFFE74C3C)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        onConfirm(
                            TodoItem(
                                id = item?.id ?: 0,
                                content = content,
                                isDone = item?.isDone ?: false,
                                createdAt = item?.createdAt ?: System.currentTimeMillis(),
                                startTimeMillis = startTime,
                                deadlineTimeMillis = deadlineTime
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C3E50),
                    contentColor = Color.White
                )
            ) {
                Text("儲存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        }
    )
}

@Composable
fun DateTimeSelector(
    label: String,
    timeMillis: Long?,
    onTimeSelected: (Long) -> Unit,
    onClear: () -> Unit,
    icon: String,
    iconColor: Color
) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN)
    
    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable {
                    showDateTimePicker(context, timeMillis ?: System.currentTimeMillis()) { onTimeSelected(it) }
                }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, modifier = Modifier.padding(end = 8.dp))
            Text(
                text = if (timeMillis != null) sdf.format(Date(timeMillis)) else "尚未設定",
                modifier = Modifier.weight(1f),
                color = if (timeMillis != null) Color.Black else Color.LightGray,
                fontSize = 14.sp
            )
            if (timeMillis != null) {
                IconButton(onClick = onClear, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

fun showDateTimePicker(context: android.content.Context, initialTime: Long, onDateTimeSelected: (Long) -> Unit) {
    val calendar = Calendar.getInstance().apply { timeInMillis = initialTime }
    
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    onDateTimeSelected(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
fun FundInputDialog(title: String, initialValue: Long, onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(initialValue.toString()) }
    val focusManager = LocalFocusManager.current
    
    // 使用原生 View 系統來強制控制鍵盤
    val context = LocalContext.current
    val view = LocalView.current
    val imm = remember { context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
    
    fun hideKeyboard() {
        focusManager.clearFocus()
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    AlertDialog(
        onDismissRequest = { 
            hideKeyboard()
            onDismiss() 
        },
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { hideKeyboard() })
                    }
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.all { char -> char.isDigit() }) text = it },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { hideKeyboard() }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    hideKeyboard()
                    onConfirm(text.toLongOrNull() ?: 0L) 
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C3E50),
                    contentColor = Color.White
                )
            ) { Text("確定") }
        },
        dismissButton = {
            TextButton(onClick = { 
                hideKeyboard()
                onDismiss() 
            }) { Text("取消") }
        }
    )
}
