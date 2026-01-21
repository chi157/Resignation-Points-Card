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
    val settings by viewModel.settings.collectAsState()
    val targetStamps = settings?.targetStamps?.takeIf { it > 0 } ?: 30
    
    var editingRecord by remember { mutableStateOf<StampRecord?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    
    // 計算統計數據
    val totalCount = allStamps.size
    val cardCount = if (totalCount == 0) 0 else (totalCount - 1) / targetStamps + 1
    
    // 找出最常見的理由
    val mostCommonReason = remember(allStamps) {
        allStamps.groupBy { it.reason }
            .maxByOrNull { it.value.size }?.key ?: "尚未有紀錄"
    }

    // 依照虛擬卡片索引分組紀錄
    val stampsByCard = remember(allStamps) {
        allStamps.groupBy { it.cardIndex }
            .toSortedMap(compareByDescending { it })
    }

    // 計算自定義的圓餅圖數據


    // 計算自定義的圓餅圖數據
    val chartData = remember(allStamps) {
        val stats = allStamps.groupBy { it.reason }
            .map { it.key to it.value.size }
            .sortedByDescending { it.second }
        
        if (stats.size <= 4) {
            stats
        } else {
            val top3 = stats.take(3)
            val othersCount = stats.drop(3).sumOf { it.second }
            top3 + ("其他" to othersCount)
        }
    }

    val chartColors = listOf(
        Color(0xFF3498DB), // 藍
        Color(0xFFE74C3C), // 紅
        Color(0xFF2ECC71), // 綠
        Color(0xFFF1C40F), // 黃
        Color(0xFF9B59B6), // 紫
    )

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

                    // 2. 原因統計圓餅圖
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF34495E)),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Color(0xFF5D6D7E))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = "離職原因分佈", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                
                                ReasonPieChart(
                                    data = chartData,
                                    colors = chartColors,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                        }
                    }

                    // 3. 最常見原因卡片
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
                        val capacity = stamps.firstOrNull()?.cardCapacity ?: 30
                        item {
                            CardHeader(cardIndex = cardIndex, count = stamps.size, capacity = capacity)
                        }
                        items(stamps.sortedByDescending { it.dateMillis }) { record ->
                            RecordItem(
                                record = record,
                                isLocked = record.isLocked(settings?.lastCompletedCardIndex ?: 0),
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
                    showDeleteConfirmDialog = true
                },
                commonReasons = commonReasonsList,
                onAddCommonReason = { viewModel.addCommonReason(it) }
            )
        }

        if (showDeleteConfirmDialog && editingRecord != null) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("確定要刪除嗎？") },
                text = { Text("刪除後這枚印章將會消失，且無法還原喔！") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteStamp(editingRecord!!)
                            showDeleteConfirmDialog = false
                            showEditDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFE74C3C))
                    ) {
                        Text("確定刪除", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("取消")
                    }
                }
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
fun CardHeader(cardIndex: Int, count: Int, capacity: Int = 30) {
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
                    Text(text = "卡片容量: $capacity", color = Color.Gray, fontSize = 11.sp)
                }
            }
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .widthIn(min = 32.dp)
                    .background(Color(0xFF3498DB), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count/$capacity", 
                    color = Color.White, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun RecordItem(record: StampRecord, isLocked: Boolean, onEdit: () -> Unit) {
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

            if (!isLocked) {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF3498DB), modifier = Modifier.size(18.dp))
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "已鎖定",
                    tint = Color.LightGray,
                    modifier = Modifier.size(18.dp).padding(4.dp)
                )
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
@Composable
fun ReasonPieChart(
    data: List<Pair<String, Int>>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.second }.toFloat()
    if (total == 0f) return

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Canvas(modifier = Modifier.size(120.dp)) {
            var startAngle = -90f
            data.forEachIndexed { index, pair ->
                val sweepAngle = (pair.second / total) * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true
                )
                startAngle += sweepAngle
            }
            // 繪製中間的圓洞，製作成甜甜圈圖案
            drawCircle(
                color = Color(0xFF34495E),
                radius = size.minDimension / 4f
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            data.forEachIndexed { index, (reason, count) ->
                val percentage = (count / total * 100).toInt()
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(colors[index % colors.size], CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = reason,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$percentage%",
                        color = Color.LightGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}
