package com.hohsiang.rfid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements RFIDManager.RFIDConnectionListener {
    
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    // UI 元件
    private TextView tvConnectionStatus;
    private TextView tvScanStatus;
    private TextView tvTagCount;
    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnStartScan;
    private Button btnStopScan;
    private Button btnClearTags;
    private LinearLayout layoutTags;
    
    // RFID 管理器
    private RFIDManager rfidManager;
    
    // 標籤數據
    private Set<String> foundTags = new HashSet<>();
    private List<TagInfo> tagList = new ArrayList<>();
    
    // 標籤信息類
    private static class TagInfo {
        String tagId;
        int rssi;
        long timestamp;
        
        TagInfo(String tagId, int rssi) {
            this.tagId = tagId;
            this.rssi = rssi;
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        checkPermissions();
        initRFIDManager();
        setupButtonListeners();
    }
    
    private void initViews() {
        tvConnectionStatus = findViewById(R.id.tv_connection_status);
        tvScanStatus = findViewById(R.id.tv_scan_status);
        tvTagCount = findViewById(R.id.tv_tag_count);
        btnConnect = findViewById(R.id.btn_connect);
        btnDisconnect = findViewById(R.id.btn_disconnect);
        btnStartScan = findViewById(R.id.btn_start_scan);
        btnStopScan = findViewById(R.id.btn_stop_scan);
        btnClearTags = findViewById(R.id.btn_clear_tags);
        layoutTags = findViewById(R.id.layout_tags);
    }
    
    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        
        // 檢查藍牙權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_ADMIN);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        
        // Android 12+ 權限
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
        }
        
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                permissionsNeeded.toArray(new String[0]), 
                PERMISSION_REQUEST_CODE);
        }
    }
    
    private void initRFIDManager() {
        rfidManager = new RFIDManager(this);
        rfidManager.setConnectionListener(this);
    }
    
    private void setupButtonListeners() {
        btnConnect.setOnClickListener(v -> connectRFID());
        btnDisconnect.setOnClickListener(v -> disconnectRFID());
        btnStartScan.setOnClickListener(v -> startScanning());
        btnStopScan.setOnClickListener(v -> stopScanning());
        btnClearTags.setOnClickListener(v -> clearTags());
    }
    
    private void connectRFID() {
        btnConnect.setEnabled(false);
        tvConnectionStatus.setText("連接中...");
        rfidManager.connect();
    }
    
    private void disconnectRFID() {
        btnDisconnect.setEnabled(false);
        rfidManager.disconnect();
    }
    
    private void startScanning() {
        btnStartScan.setEnabled(false);
        btnStopScan.setEnabled(true);
        rfidManager.startScanning();
    }
    
    private void stopScanning() {
        btnStopScan.setEnabled(false);
        btnStartScan.setEnabled(true);
        rfidManager.stopScanning();
    }
    
    private void clearTags() {
        foundTags.clear();
        tagList.clear();
        updateTagDisplay();
        updateTagCount();
    }
    
    private void updateTagDisplay() {
        layoutTags.removeAllViews();
        
        if (tagList.isEmpty()) {
            TextView noTagsView = new TextView(this);
            noTagsView.setText("暫無標籤");
            noTagsView.setTextSize(14);
            noTagsView.setTextColor(getColor(android.R.color.darker_gray));
            noTagsView.setPadding(32, 32, 32, 32);
            noTagsView.setGravity(android.view.Gravity.CENTER);
            layoutTags.addView(noTagsView);
        } else {
            for (TagInfo tag : tagList) {
                View tagView = createTagView(tag);
                layoutTags.addView(tagView);
            }
        }
    }
    
    private View createTagView(TagInfo tag) {
        LinearLayout tagContainer = new LinearLayout(this);
        tagContainer.setOrientation(LinearLayout.VERTICAL);
        tagContainer.setPadding(16, 12, 16, 12);
        tagContainer.setBackground(ContextCompat.getDrawable(this, android.R.drawable.dialog_holo_light_frame));
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 8);
        tagContainer.setLayoutParams(params);
        
        // 標籤 ID
        TextView tagIdView = new TextView(this);
        tagIdView.setText("標籤 ID: " + tag.tagId);
        tagIdView.setTextSize(16);
        tagIdView.setTextColor(getColor(android.R.color.black));
        tagIdView.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // RSSI 和時間信息
        TextView infoView = new TextView(this);
        infoView.setText(String.format("RSSI: %d dBm | 時間: %s", 
            tag.rssi, 
            android.text.format.DateFormat.format("HH:mm:ss", tag.timestamp)));
        infoView.setTextSize(12);
        infoView.setTextColor(getColor(android.R.color.darker_gray));
        
        tagContainer.addView(tagIdView);
        tagContainer.addView(infoView);
        
        return tagContainer;
    }
    
    private void updateTagCount() {
        tvTagCount.setText(String.valueOf(foundTags.size()));
    }
    
    // RFIDConnectionListener 回調方法
    @Override
    public void onConnectionStatusChanged(boolean connected) {
        runOnUiThread(() -> {
            if (connected) {
                tvConnectionStatus.setText("連接狀態：已連接");
                tvConnectionStatus.setTextColor(getColor(android.R.color.holo_green_dark));
                btnConnect.setEnabled(false);
                btnDisconnect.setEnabled(true);
                btnStartScan.setEnabled(true);
            } else {
                tvConnectionStatus.setText("連接狀態：未連接");
                tvConnectionStatus.setTextColor(getColor(android.R.color.holo_red_dark));
                btnConnect.setEnabled(true);
                btnDisconnect.setEnabled(false);
                btnStartScan.setEnabled(false);
                btnStopScan.setEnabled(false);
                
                if (tvScanStatus.getVisibility() == View.VISIBLE) {
                    tvScanStatus.setVisibility(View.GONE);
                }
            }
        });
    }
    
    @Override
    public void onTagFound(String tagId, int rssi) {
        runOnUiThread(() -> {
            if (!foundTags.contains(tagId)) {
                foundTags.add(tagId);
                TagInfo newTag = new TagInfo(tagId, rssi);
                tagList.add(0, newTag); // 添加到列表頂部
                updateTagDisplay();
                updateTagCount();
                
                // 顯示通知
                Toast.makeText(this, "發現新標籤: " + tagId, Toast.LENGTH_SHORT).show();
            } else {
                // 更新現有標籤的 RSSI 和時間
                for (int i = 0; i < tagList.size(); i++) {
                    if (tagList.get(i).tagId.equals(tagId)) {
                        tagList.get(i).rssi = rssi;
                        tagList.get(i).timestamp = System.currentTimeMillis();
                        updateTagDisplay();
                        break;
                    }
                }
            }
        });
    }
    
    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "錯誤: " + error, Toast.LENGTH_LONG).show();
            
            // 重置按鈕狀態
            btnConnect.setEnabled(!rfidManager.isConnected());
            btnDisconnect.setEnabled(rfidManager.isConnected());
            btnStartScan.setEnabled(rfidManager.isConnected() && !rfidManager.isScanning());
            btnStopScan.setEnabled(rfidManager.isScanning());
        });
    }
    
    @Override
    public void onScanningStatusChanged(boolean scanning) {
        runOnUiThread(() -> {
            if (scanning) {
                tvScanStatus.setText("掃描狀態：掃描中...");
                tvScanStatus.setTextColor(getColor(android.R.color.holo_orange_dark));
                tvScanStatus.setVisibility(View.VISIBLE);
                btnStartScan.setEnabled(false);
                btnStopScan.setEnabled(true);
            } else {
                tvScanStatus.setText("掃描狀態：已停止");
                tvScanStatus.setTextColor(getColor(android.R.color.darker_gray));
                btnStartScan.setEnabled(true);
                btnStopScan.setEnabled(false);
                
                // 延遲隱藏掃描狀態
                tvScanStatus.postDelayed(() -> tvScanStatus.setVisibility(View.GONE), 2000);
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rfidManager != null) {
            rfidManager.cleanup();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allPermissionsGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            
            if (!allPermissionsGranted) {
                Toast.makeText(this, getString(R.string.error_permission), Toast.LENGTH_LONG).show();
            }
        }
    }
}