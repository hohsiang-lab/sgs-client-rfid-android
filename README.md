# Bluebird RFID SLED Android 應用程式

這是一個用於連接和操作 Bluebird RFID SLED 裝置的 Android 應用程式。

## 功能特點

- 🔗 連接/斷開 Bluebird RFID SLED 裝置
- 🏷️ 掃描和讀取 RFID 標籤
- 📊 顯示標籤資訊（ID、RSSI、時間戳）
- 🔄 即時更新掃描狀態
- 📱 友好的用戶界面

## 安裝要求

### 系統要求
- Android 5.0 (API level 21) 或更高版本
- 支援藍牙 4.0 或更高版本
- 位置權限（用於藍牙掃描）

### Bluebird RFID SDK
1. 將 Bluebird RFID SDK jar 文件放置在 `app/libs/` 目錄下
2. SDK 版本：V1.2.7 (LIB_VERSION_5_77_00_00)

## 安裝步驟

1. **下載 SDK**
   - 從 Bluebird 官方獲取 RFID SLED Android SDK
   - 將 jar 文件複製到 `app/libs/` 目錄

2. **導入項目**
   ```bash
   git clone <repository-url>
   cd bluetooth
   ```

3. **在 Android Studio 中打開項目**
   - 開啟 Android Studio
   - 選擇 "Open an existing Android Studio project"
   - 選擇項目根目錄

4. **配置 SDK**
   - 確保 Bluebird SDK jar 文件在 `app/libs/` 目錄中
   - 根據實際 SDK API 更新 `RFIDManager.java` 中的實現

## 使用方法

### 基本操作流程

1. **啟動應用程式**
   - 應用會自動請求必要的權限（藍牙、位置等）

2. **連接裝置**
   - 點擊 "連接 RFID" 按鈕
   - 等待連接成功

3. **開始掃描**
   - 連接成功後，點擊 "開始掃描"
   - 應用會開始搜尋附近的 RFID 標籤

4. **查看結果**
   - 掃描到的標籤會即時顯示在界面上
   - 每個標籤顯示 ID、RSSI 強度和發現時間

### 按鈕功能說明

- **連接 RFID**: 建立與 RFID SLED 裝置的連接
- **斷開 RFID**: 斷開與裝置的連接
- **開始掃描**: 開始掃描 RFID 標籤
- **停止掃描**: 停止掃描操作
- **清除標籤**: 清空已發現的標籤列表

## 程式碼結構

```
app/src/main/java/com/hohsiang/rfid/
├── MainActivity.java          # 主活動，處理 UI 交互
├── RFIDManager.java          # RFID 連接和操作管理
└── RFIDConnectionListener    # RFID 事件回調接口

app/src/main/res/
├── layout/
│   └── activity_main.xml     # 主界面布局
├── values/
│   ├── strings.xml           # 字符串資源
│   └── themes.xml           # 主題配置
└── AndroidManifest.xml       # 應用配置和權限
```

## 重要說明

### SDK 集成
當前程式碼包含模擬實現，用於測試界面和基本流程。要與真實的 Bluebird RFID SLED 裝置工作，需要：

1. 將真實的 Bluebird SDK jar 文件放置在 `app/libs/` 目錄
2. 更新 `RFIDManager.java` 中標記為 "實際實現時需要使用真實的 Bluebird SDK API" 的部分
3. 根據 SDK 文檔調用正確的 API 方法

### 權限要求
應用需要以下權限：
- `BLUETOOTH`: 基本藍牙功能
- `BLUETOOTH_ADMIN`: 藍牙管理
- `ACCESS_FINE_LOCATION`: 精確位置（藍牙掃描需要）
- `ACCESS_COARSE_LOCATION`: 粗略位置
- `BLUETOOTH_SCAN`: Android 12+ 藍牙掃描
- `BLUETOOTH_CONNECT`: Android 12+ 藍牙連接

## 故障排除

### 常見問題

1. **連接失敗**
   - 確保 RFID SLED 裝置已開機且處於可連接狀態
   - 檢查藍牙是否已開啟
   - 確認裝置配對狀態

2. **掃描無結果**
   - 確保附近有 RFID 標籤
   - 檢查 RFID 標籤是否支援的頻率範圍內
   - 嘗試調整掃描距離

3. **權限被拒絕**
   - 前往系統設置 > 應用管理 > RFID Reader > 權限
   - 手動授予所需權限

## 開發者注意事項

- 在實際部署前，必須集成真實的 Bluebird SDK
- 測試時建議使用真實的 RFID 標籤和 SLED 裝置
- 可根據需要調整 UI 布局和功能

## 授權

本項目僅供學習和開發參考使用。Bluebird RFID SDK 的使用需遵循 Bluebird 的授權條款。