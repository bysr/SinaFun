package hipad.unittest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "MainActivity";

    /**
     * 是否有足够的剩余空间
     */
    private boolean haveEnoughSpace = false;
    private RingProgressBar ringProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long freeSpace = getFreeSpace();
        haveEnoughSpace = !(freeSpace < 5242880);//TODO 检测剩余空间，限制是5M

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (haveEnoughSpace) {
                    checkCameraPermission();
                } else {
                    Toast.makeText(MainActivity.this, "剩余空间不够充足，请清理一下再试一次", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ringProgressBar = (RingProgressBar) findViewById(R.id.ringProgressBar);
        ringProgressBar.setValue(100, 100);
    }

    //视频录制需要的权限(相机，录音，外部存储)
    private String[] VIDEO_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> NO_VIDEO_PERMISSION = new ArrayList<String>();

    /**
     * 检测摄像头权限，具备相关权限才能继续
     */
    private void checkCameraPermission() {
        NO_VIDEO_PERMISSION.clear();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < VIDEO_PERMISSION.length; i++) {
                if (ActivityCompat.checkSelfPermission(this, VIDEO_PERMISSION[i]) != PackageManager.PERMISSION_GRANTED) {
                    NO_VIDEO_PERMISSION.add(VIDEO_PERMISSION[i]);
                }
            }
            if (NO_VIDEO_PERMISSION.size() == 0) {
                Intent intent = new Intent(this, RecordVideoActivity.class);
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, NO_VIDEO_PERMISSION.toArray(new String[NO_VIDEO_PERMISSION.size()]), REQUEST_CAMERA);
            }
        } else {
            Intent intent = new Intent(this, RecordVideoActivity.class);
            startActivity(intent);
        }
    }

    private static final int REQUEST_CAMERA = 0;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            boolean flag = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    flag = true;
                } else {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                Toast.makeText(this, "已授权", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, RecordVideoActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 获得可用存储空间
     *
     * @return 可用存储空间（单位b）
     */
    public long getFreeSpace() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;//区块的大小
        long totalBlocks;//区块总数
        long availableBlocks;//可用区块的数量
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        //String totalSpace = Formatter.formatFileSize(MyApplication.getInstance().getApplicationContext(), blockSize * totalBlocks);
        //String availableSpace = Formatter.formatFileSize(MyApplication.getInstance().getApplicationContext(), blockSize * availableBlocks);
        //Log.e(LOG_TAG, "totalSpace：" + totalSpace + "...availableSpace：" + availableSpace);
        Log.e(LOG_TAG, "totalSpace：" + blockSize * totalBlocks + "...availableSpace：" + blockSize * availableBlocks);
        return blockSize * availableBlocks;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ringProgressBar.clearBitmap();
    }
}
