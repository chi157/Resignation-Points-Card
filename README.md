# 離職集點卡 (Resignation Points Card) - Android App🚀

![License](https://img.shields.io/badge/License-AGPL%20v3-blue.svg)
![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple.svg)

## 🌟 應用程式簡介

**「離職集點卡」** 是一款充滿幽默感且兼具實用性的壓力管理與職涯規劃工具。

你是否每天起床都想遞辭呈？或是老闆畫的大餅讓你消化不良？本應用程式將「離職」這個沉重的話題轉化為有趣的「集點」機制。每當你感到心力交瘁、職場崩壞時，就為自己蓋上一枚印章。當集點卡滿格時，或許就是你邁向自由、開啟人生新篇章的最佳時機。

這不僅僅是一個計數器，它更是一個完整的「脫困計畫」工具，幫助你在情緒宣洩之餘，也能踏實地準備好存款與履歷。

---

## ✨ 核心功能

### 1. 離職集點系統 (Stamp System)
*   **趣味蓋章**：提供多種目標格數（10, 20, 30格）供每張新卡片選擇。
*   **離職原因追蹤**：每次蓋章可記錄當下的情緒或原因，系統會自動學習您的「常用原因」。
*   **「真的很生氣」彩蛋**：小彩蛋，自己找找吧！
*   **歷史鎖定**：已完成的集點卡紀錄將會被「封存鎖定」，作為你職場奮鬥的不可抹滅見證。

### 2. 資料視覺化與紀錄 (Analytics & Records)
*   **原因分佈圖**：使用直覺的甜甜圈圓餅圖（Donut Chart）統計你的離職原因。
*   **卡片分組**：詳盡記錄每一張卡的歷史，並標註當時的卡片容量與鎖定狀態。

### 3. 離職計畫 (Resignation Plan)
*   **自由預備金**：追蹤設定的存款進度，具備快速增加按鈕與自訂金額功能。
*   **準備清單**：內建履歷確認勾選與全功能的待辦事項（Todo List），支援時間提醒設定。

### 4. 桌面小工具 (Homescreen Widget)
*   **勵志/毒雞湯語錄**：隨機顯示職場金句，隨時提醒你保持冷靜或堅定目標。
*   **高度自訂化**：支援三組背景與文字顏色自訂，並可調整語錄刷新頻率。
*   **開發者模式**：內建隱藏彩蛋，解鎖後可設定極速刷新。

### 5. 多樣化主題 (Themes)
*   提供「經典 RPG」、「系統錯誤」以及「度假模式」三種深度設計的主題，改變整個 APP 的視覺氛圍。

---

## 🛠️ 使用技術 (Technical Stack)

本專案採用現代 Android 開發的最佳實踐與技術：

*   **UI 框架**：[Jetpack Compose](https://developer.android.com/jetpack/compose) - 全聲明式 UI 架構，打造流暢且現代的互動體驗。
*   **開發語言**：[Kotlin](https://kotlinlang.org/) - 100% 使用 Kotlin 編寫。
*   **架構模式**：**MVVM (Model-View-ViewModel)** 結合 **Repository Pattern**，確保代碼的高可維護性與關注點分離。
*   **資料庫**：[Room Persistence Library](https://developer.android.com/training/data-storage/room) - 處理本地資料持久化，支援 Flow 實時監聽資料異動。
*   **桌面小工具**：[Jetpack Glance](https://developer.android.com/jetpack/compose/glance) - 使用 Compose 語法構建原生桌面 App Widget。
*   **異步處理**：[Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) - 優雅地處理背景任務與反應式資料流。
*   **排程任務**：[WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) - 負責小工具語錄的背景刷新任務。
*   **繪圖 API**：使用 Compose **Canvas API** 手繪圓餅圖與自定義 UI 組件。
*   **外觀設計**：Material 3 設計規範，搭配自定義 HSV/RGB 顏色選擇器。

---

## 📜 授權協議 (License)

本專案採用 **GNU Affero General Public License v3.0 (AGPL v3)** 授權。

*   任何使用本軟體提供網路服務的行為，均須依照 AGPL v3 的要求，提供使用者完整的原始碼。
*   **請勿用於商業用途**。

---
© 2026 Neil Company. Developed by Cynthia Chang. Licensed under AGPL v3.
