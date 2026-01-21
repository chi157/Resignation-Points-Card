package com.chi157.resignationpointscard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import com.chi157.resignationpointscard.data.StampRecord
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ResignationRecordScreen(
    viewModel: AppViewModel,
    navController: androidx.navigation.NavHostController
) {
    val allStamps by viewModel.allStamps.collectAsState()
    val commonReasonsList by viewModel.allCommonReasons.collectAsState()
    
    var editingRecord by remember { mutableStateOf<StampRecord?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    
    // 計算統計數據
    val totalCount = allStamps.size
    val cardCount = allStamps.map { it.cardIndex }.distinct().size
    
    // 找出最常見的理由
    val mostCommonReason = remember(allStamps) {
        allStamps.groupBy { it.reason }
            .maxByOrNull { it.value.size }?.key ?: "尚未有紀錄"
    }

    // 依照卡片索引分組紀錄
    val stampsByCard = remember(allStamps) {
        allStamps.groupBy { it.cardIndex }.toSortedMap(compareByDescending { it })
    }

    Scaffold(
        bottomBar = {
            MainBottomNavigation(currentRoute = Screen.Record.route, onNavigate = { route ->
                navController.navigate(route)
            })
        },
        containerColor = Color(0xFF2C3E50) // 設計稿底色
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "離職紀錄",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            if (allStamps.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📭", fontSize = 60.sp)
                        Text("尚無集點紀錄", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // 1. 頂部統計卡片
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "總蓋章數",
                                value = "$totalCount",
                                icon = Icons.Default.CheckCircle,
                                iconColor = Color(0xFF3498DB),
                                borderColor = Color(0xFF3498DB)
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "使用卡片",
                                value = "$cardCount",
                                icon = Icons.Default.Menu,
                                iconColor = Color(0xFFF1C40F),
                                borderColor = Color(0xFFF1C40F)
                            )
                        }
                    }

                    // 2. 最常見原因卡片
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE74C3C)), // 紅色底
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "最常見的原因", color = Color.White, fontSize = 14.sp)
                                }
                                Text(
                                    text = mostCommonReason,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }

                    // 3. 遍歷每一張卡片的紀錄
                    stampsByCard.forEach { (cardIndex, stamps) ->
                        item {
                            CardHeader(cardIndex = cardIndex, count = stamps.size)
                        }
                        items(stamps.sortedByDescending { it.dateMillis }) { record ->
                            RecordItem(
                                record = record,
                                onEdit = {
                                    editingRecord = record
                                    showEditDialog = true
                                }
                            )
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }

        if (showEditDialog && editingRecord != null) {
            EditRecordDialog(
                record = editingRecord!!,
                onDismiss = { showEditDialog = false },
                onSave = { updatedReason ->
                    viewModel.updateStamp(editingRecord!!.copy(reason = updatedReason))
                    showEditDialog = false
                },
                onDelete = {
                    viewModel.deleteStamp(editingRecord!!)
                    showEditDialog = false
                },
                commonReasons = commonReasonsList,
                onAddCommonReason = { viewModel.addCommonReason(it) }
            )
        }
    }
}

private fun Arrangement.spacedCorner(dp: androidx.compose.ui.unit.Dp) = spacedBy(dp)

@Composable
fun StatCard(
    modifier: Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    borderColor: Color
) {
    Surface(
        modifier = modifier.height(120.dp).border(2.dp, borderColor, RoundedCornerShape(4.dp)),
        color = Color.White,
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CardHeader(cardIndex: Int, count: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).border(2.dp, Color(0xFF3498DB), RoundedCornerShape(4.dp)),
        color = Color(0xFF2C3E50),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(24.dp).background(Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$cardIndex", color = Color.White, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "第 $cardIndex 張卡", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "初次萌生退意", color = Color.Gray, fontSize = 11.sp)
                }
            }
            Box(
                modifier = Modifier.size(32.dp).background(Color(0xFF3498DB), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$count", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun RecordItem(record: StampRecord, onEdit: () -> Unit) {
    val dateSdf = SimpleDateFormat("MMMM\ndd, yyyy", Locale.ENGLISH)
    val timeSdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
    val date = Date(record.dateMillis)

    Surface(
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, RoundedCornerShape(4.dp)),
        color = Color.White,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(100.dp)) {
                Text(text = dateSdf.format(date), fontSize = 14.sp, fontWeight = FontWeight.Bold, lineHeight = 16.sp)
                Text(text = timeSdf.format(date), fontSize = 11.sp, color = Color.LightGray)
            }
            
            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.Black))
            
            Text(
                text = record.reason,
                modifier = Modifier.padding(start = 16.dp).weight(1f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun EditRecordDialog(
    record: StampRecord,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit,
    commonReasons: List<com.chi157.resignationpointscard.data.CommonReason>,
    onAddCommonReason: (String) -> Unit
) {
    var reason by remember { mutableStateOf(record.reason) }
    var addToCommonReasons by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val dateSdf = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
    val timeSdf = SimpleDateFormat("HH:mm a", Locale.ENGLISH)
    val date = Date(record.dateMillis)
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val focusManager = LocalFocusManager.current
        val context = LocalContext.current
        val view = LocalView.current
        val imm = remember { context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
        
        fun hideKeyboard() {
            focusManager.clearFocus()
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF2C3E50) // 深藍色底
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { 
                            hideKeyboard()
                        })
                    }
            ) {
                    // 自定義 TopBar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("取消", color = Color.White, fontSize = 18.sp)
                        }
                        Text("編輯紀錄", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = { 
                                onSave(reason)
                                if (addToCommonReasons && reason.isNotBlank()) {
                                    onAddCommonReason(reason)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD166)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text("儲存", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 日期時間顯示
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "${dateSdf.format(date)} ",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = timeSdf.format(date),
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(text = "蓋章原因", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // 常用原因選單
                    if (commonReasons.isNotEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { isExpanded = !isExpanded },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Text("選擇常用原因...")
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            
                            DropdownMenu(
                                expanded = isExpanded,
                                onDismissRequest = { isExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.7f)
                            ) {
                                commonReasons.forEach { commonReason ->
                                    DropdownMenuItem(
                                        text = { Text(commonReason.text) },
                                        onClick = {
                                            reason = commonReason.text
                                            isExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 輸入框
                    Surface(
                        modifier = Modifier.fillMaxWidth().height(200.dp).border(2.dp, Color.Black, RoundedCornerShape(4.dp)),
                        color = Color.White,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        BasicTextField(
                            value = reason,
                            onValueChange = { reason = it },
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
                            cursorBrush = SolidColor(Color.Black),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { 
                                hideKeyboard()
                            })
                        )
                    }
                    
                    // 加入常用列表 Checkbox
                    val isKnownReason = commonReasons.any { it.text == reason }
                    if (reason.isNotBlank() && !isKnownReason) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable { addToCommonReasons = !addToCommonReasons }
                        ) {
                            Checkbox(
                                checked = addToCommonReasons,
                                onCheckedChange = { addToCommonReasons = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFFFFD166),
                                    uncheckedColor = Color.White,
                                    checkmarkColor = Color.Black
                                )
                            )
                            Text("加入常用蓋章原因", fontSize = 14.sp, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 刪除按鈕
                    Button(
                        onClick = onDelete,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(0.dp))
                            .navigationBarsPadding(), // 加入底部導覽列間距
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("刪除此記錄", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
        }
    }
}
