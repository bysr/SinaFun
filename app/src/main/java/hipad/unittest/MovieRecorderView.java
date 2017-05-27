package hipad.unittest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 视频播放控件
 * Created by Wood on 2016/4/6.
 */
public class MovieRecorderView extends LinearLayout implements OnErrorListener {
    private static final String LOG_TAG = "MovieRecorderView";

    private Context context;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    //private ProgressBar progressBar;

    private MediaRecorder mediaRecorder;
    private Camera camera;
    private Timer timer;//计时器

    private int mWidth;//视频录制分辨率宽度
    private int mHeight;//视频录制分辨率高度
    private boolean isOpenCamera;//是否一开始就打开摄像头
    private int recordMaxTime;//最长拍摄时间
    private int timeCount;//时间计数
    private File recordFile = null;//视频文件
    private long sizePicture = 0;

    public MovieRecorderView(Context context) {
        this(context, null);
    }

    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MovieRecorderView, defStyle, 0);
        mWidth = a.getInteger(R.styleable.MovieRecorderView_record_width, 640);//默认640
        mHeight = a.getInteger(R.styleable.MovieRecorderView_record_height, 360);//默认360

        isOpenCamera = a.getBoolean(R.styleable.MovieRecorderView_is_open_camera, true);//默认打开摄像头
        recordMaxTime = a.getInteger(R.styleable.MovieRecorderView_record_max_time, 10);//默认最大拍摄时间为10s

        LayoutInflater.from(context).inflate(R.layout.movie_recorder_view, this);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        //TODO 需要用到进度条，打开此处，也可以自己定义自己需要的进度条，提供了拍摄进度的接口
        //progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        //progressBar.setMax(recordMaxTime);//设置进度条最大量
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new CustomCallBack());
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        a.recycle();
    }

    /**
     * SurfaceHolder回调
     */
    private class CustomCallBack implements Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            try {
                initCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }
    }

    /**
     * 初始化摄像头
     */
    public void initCamera() throws IOException {
        if (camera != null) {
            freeCameraResource();
        }
        try {
            if (checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);//TODO 默认打开前置摄像头
            } else if (checkCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
            ((Activity) context).finish();
        }
        if (camera == null)
            return;

        setCameraParams();
        camera.setDisplayOrientation(90);
        camera.setPreviewDisplay(surfaceHolder);
        camera.startPreview();
        camera.unlock();
    }

    /**
     * 检查是否有摄像头
     *
     * @param facing 前置还是后置
     * @return
     */
    private boolean checkCameraFacing(int facing) {
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (facing == info.facing) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置摄像头为竖屏
     */
    private void setCameraParams() {
        if (camera != null) {
            Parameters params = camera.getParameters();
            params.set("orientation", "portrait");
            List<Camera.Size> supportedPictureSizes = params.getSupportedPictureSizes();
            for (Camera.Size size : supportedPictureSizes) {
                sizePicture = (size.height * size.width) > sizePicture ? size.height * size.width : sizePicture;
            }
//            LogUtil.e(LOG_TAG,"手机支持的最大像素supportedPictureSizes===="+sizePicture);
            setPreviewSize(params);
            camera.setParameters(params);
        }
    }

    /**
     * 根据手机支持的视频分辨率，设置预览尺寸
     *
     * @param params
     */
    private void setPreviewSize(Parameters params) {
        if (camera == null) {
            return;
        }
        //获取手机支持的分辨率集合，并以宽度为基准降序排序
        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width > rhs.width) {
                    return -1;
                } else if (lhs.width == rhs.width) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        float tmp = 0f;
        float minDiff = 100f;
        float ratio = 3.0f / 4.0f;//TODO 高宽比率3:4，且最接近屏幕宽度的分辨率，可以自己选择合适的想要的分辨率
        Camera.Size best = null;
        for (Camera.Size s : previewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - ratio);
            Log.e(LOG_TAG, "setPreviewSize: width:" + s.width + "...height:" + s.height);
//            LogUtil.e(LOG_TAG,"tmp:" + tmp);
            if (tmp < minDiff) {
                minDiff = tmp;
                best = s;
            }
        }
//        LogUtil.e(LOG_TAG, "BestSize: width:" + best.width + "...height:" + best.height);
//        List<int[]> range = params.getSupportedPreviewFpsRange();
//        int[] fps = range.get(0);
//        LogUtil.e(LOG_TAG,"min="+fps[0]+",max="+fps[1]);
//        params.setPreviewFpsRange(3,7);

        params.setPreviewSize(best.width, best.height);//预览比率

//        params.setPictureSize(480, 720);//拍照保存比率

        Log.e(LOG_TAG, "setPreviewSize BestSize: width:" + best.width + "...height:" + best.height);

        //TODO 大部分手机支持的预览尺寸和录制尺寸是一样的，也有特例，有些手机获取不到，那就把设置录制尺寸放到设置预览的方法里面
        if (params.getSupportedVideoSizes() == null || params.getSupportedVideoSizes().size() == 0) {
            mWidth = best.width;
            mHeight = best.height;
        } else {
            setVideoSize(params);
        }
    }

    /**
     * 根据手机支持的视频分辨率，设置录制尺寸
     *
     * @param params
     */
    private void setVideoSize(Parameters params) {
        if (camera == null) {
            return;
        }
        //获取手机支持的分辨率集合，并以宽度为基准降序排序
        List<Camera.Size> previewSizes = params.getSupportedVideoSizes();
        Collections.sort(previewSizes, new Comparator<Camera.Size>() {
            @Override
            public int compare(Camera.Size lhs, Camera.Size rhs) {
                if (lhs.width > rhs.width) {
                    return -1;
                } else if (lhs.width == rhs.width) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        float tmp = 0f;
        float minDiff = 100f;
        float ratio = 3.0f / 4.0f;//高宽比率3:4，且最接近屏幕宽度的分辨率
        Camera.Size best = null;
        for (Camera.Size s : previewSizes) {
            tmp = Math.abs(((float) s.height / (float) s.width) - ratio);
            Log.e(LOG_TAG, "setVideoSize: width:" + s.width + "...height:" + s.height);
            if (tmp < minDiff) {
                minDiff = tmp;
                best = s;
            }
        }
        Log.e(LOG_TAG, "setVideoSize BestSize: width:" + best.width + "...height:" + best.height);
        //设置录制尺寸
        mWidth = best.width;
        mHeight = best.height;
    }

    /**
     * 释放摄像头资源
     */
    private void freeCameraResource() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.lock();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            camera = null;
        }
    }

    /**
     * 创建视频文件
     */
    private void createRecordDir() {
        File sampleDir = new File(Environment.getExternalStorageDirectory() + File.separator + "SampleVideo/video/");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        try {
            //TODO 文件名用的时间戳，可根据需要自己设置，格式也可以选择3gp，在初始化设置里也需要修改
            recordFile = new File(sampleDir, System.currentTimeMillis() + ".mp4");
//            recordFile = new File(sampleDir, System.currentTimeMillis() + ".mp4");
//            File.createTempFile(AccountInfo.userId, ".mp4", sampleDir);
//            LogUtil.e(LOG_TAG, recordFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 录制视频初始化
     */
    private void initRecord() throws Exception {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        if (camera != null)
            mediaRecorder.setCamera(camera);
        mediaRecorder.setOnErrorListener(this);
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        mediaRecorder.setVideoSource(VideoSource.CAMERA);//视频源
        mediaRecorder.setAudioSource(AudioSource.MIC);//音频源
        mediaRecorder.setOutputFormat(OutputFormat.MPEG_4);//TODO 视频输出格式 也可设为3gp等其他格式
        mediaRecorder.setAudioEncoder(AudioEncoder.AMR_NB);//音频格式
        mediaRecorder.setVideoSize(mWidth, mHeight);//设置分辨率
//        mediaRecorder.setVideoFrameRate(25);//TODO 设置每秒帧数 这个设置有可能会出问题，有的手机不支持这种帧率就会录制失败，这里使用默认的帧率，当然视频的大小肯定会受影响
//        LogUtil.e(LOG_TAG,"手机支持的最大像素supportedPictureSizes===="+sizePicture);
        if (sizePicture < 3000000) {//这里设置可以调整清晰度
            mediaRecorder.setVideoEncodingBitRate(3 * 1024 * 512);
        } else if (sizePicture <= 5000000) {
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 512);
        } else {
            mediaRecorder.setVideoEncodingBitRate(1 * 1024 * 512);
        }
        mediaRecorder.setOrientationHint(270);//输出旋转90度，保持竖屏录制
        mediaRecorder.setVideoEncoder(VideoEncoder.H264);//视频录制格式
        //mediaRecorder.setMaxDuration(Constant.MAXVEDIOTIME * 1000);
        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    /**
     * 开始录制视频
     *
     * @param onRecordFinishListener 达到指定时间之后回调接口
     */
    public void record(final OnRecordFinishListener onRecordFinishListener) {
        this.onRecordFinishListener = onRecordFinishListener;
        createRecordDir();
        try {
            //如果未打开摄像头，则打开
            if (!isOpenCamera)
                initCamera();
            initRecord();
            timeCount = 0;//时间计数器重新赋值
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    timeCount++;
                    //progressBar.setProgress(timeCount);//设置进度条
                    if (onRecordProgressListener != null) {
                        onRecordProgressListener.onProgressChanged(recordMaxTime, timeCount);
                    }

                    //达到指定时间，停止拍摄
                    if (timeCount == recordMaxTime) {
                        stop();
                        if (MovieRecorderView.this.onRecordFinishListener != null)
                            MovieRecorderView.this.onRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
            if (mediaRecorder != null) {
                mediaRecorder.release();
            }
            freeCameraResource();
        }
    }

    /**
     * 停止拍摄
     */
    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        //progressBar.setProgress(0);
        if (timer != null)
            timer.cancel();
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);//设置后防止崩溃
            mediaRecorder.setPreviewDisplay(null);
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 释放资源
     */
    private void releaseRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.setOnErrorListener(null);
            try {
                mediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mediaRecorder = null;
    }

    /**
     * 获取当前录像时间
     *
     * @return timeCount
     */
    public int getTimeCount() {
        return timeCount;
    }

    /**
     * 设置最大录像时间
     *
     * @param recordMaxTime
     */
    public void setRecordMaxTime(int recordMaxTime) {
        this.recordMaxTime = recordMaxTime;
    }

    /**
     * 返回录像文件
     *
     * @return recordFile
     */
    public File getRecordFile() {
        return recordFile;
    }

    /**
     * 录制完成监听
     */
    private OnRecordFinishListener onRecordFinishListener;

    /**
     * 录制完成接口
     */
    public interface OnRecordFinishListener {
        void onRecordFinish();
    }

    /**
     * 录制进度监听
     */
    private OnRecordProgressListener onRecordProgressListener;

    /**
     * 设置录制进度监听
     *
     * @param onRecordProgressListener
     */
    public void setOnRecordProgressListener(OnRecordProgressListener onRecordProgressListener) {
        this.onRecordProgressListener = onRecordProgressListener;
    }

    /**
     * 录制进度接口
     */
    public interface OnRecordProgressListener {
        /**
         * 进度变化
         *
         * @param maxTime     最大时间，单位秒
         * @param currentTime 当前进度
         */
        void onProgressChanged(int maxTime, int currentTime);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}