package hipad.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hss01248.dialog.StyledDialog;
import com.hss01248.dialog.Tool;
import com.hss01248.dialog.adapter.SuperRcvAdapter;
import com.hss01248.dialog.adapter.SuperRcvHolder;
import com.hss01248.dialog.bottomsheet.BottomSheetBean;
import com.hss01248.dialog.config.ConfigBean;
import com.hss01248.dialog.interfaces.MyDialogListener;
import com.hss01248.dialog.interfaces.MyItemDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import hipad.unittest.R;

public class DialogActivity extends AppCompatActivity {
    Handler handler;
    Activity activity;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        handler = new Handler();
        activity = this;
        context = getApplication();
        StyledDialog.init(getApplicationContext());
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onBackPressed() {

        if (gloablDialog != null && gloablDialog.isShowing()) {
            gloablDialog.dismiss();

        } else {
            super.onBackPressed();
        }
    }

    Dialog gloablDialog;

    String msg = "如果你有心理咨询师般的敏锐，你会进一步发现——这个姑娘企图用考研来掩饰自己对于毕业的恐惧。";
           /* "\n" +
            "像琴姑娘这样的毕业生很多，她们一段时间内会认真地复习考研。可用不了多久，她们便会动摇，便会找出诸多借口给自己开脱，最后考研一事半途而废。\n" +
            "\n" +
            "原因，当事人根本不相信这件事能改变她的命运，能带给她想要的生活。她们相信自己不够努力，也愿意别人骂自己不努力。\n" +
            "\n" +
            "他们不愿意思考自己到底该干什么？他们抱着一个幻想，假如我真的努力就能解决问题了吧！于是他们把一个不可控的事件，在心理变成了可控，从而增加安全感。\n" +
            "\n" +
            "人真的可以为了逃避真正的思考，而做出任何你想象不到的事。\n" +
            "\n" +
            "这种目标是不重结果的，其实它跟刷微博是一个道理，它通过获取无用信息来给自己的生活制造一点喘息。\n" +
            "\n" +
            "只不过陷在“学习”中，要比陷在微博上更能安慰自己的内心，那个已经破了个大洞的内心。\n" +
            "\n" +
            "作者：剑圣喵大师\n" +
            "链接：https://www.zhihu.com/question/50126427/answer/119551026\n" +
            "来源：知乎\n" +
            "著作权归作者所有，转载请联系作者获得授权。";*/

    @Override
    public void onStop() {
        super.onStop();

    }

    public void OnClickStart(View v) {
        switch (v.getId()) {
            case R.id.btn_dismiss:
                StyledDialog.dismissLoading();
                break;
            case R.id.btn_common_progress:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StyledDialog.buildLoading("加载中...").show();
                    }
                }).run();
                StyledDialog.dismissLoading();
                showToast("dismissLoading() called ");

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        StyledDialog.updateLoadingMsg("jjjjj" + new Random().nextInt(100));
                    }
                }, 50, 2000);


                break;
            case R.id.btn_context_progress:
                gloablDialog = StyledDialog.buildMdLoading().show();


                //StyledDialog.dismissLoading();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StyledDialog.updateLoadingMsg("jjjjj" + new Random().nextInt(100));
                    }
                }, 3000);
                break;
            case R.id.btn_context_progress_h:
                final ProgressDialog dialog = (ProgressDialog) StyledDialog.buildProgress("下载中...", true).show();
                final int[] progress = {0};
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        progress[0] += 10;
                        StyledDialog.updateProgress(dialog, progress[0], 100, "progress", true);
                        if (progress[0] > 100) {
                            timer.cancel();
                        }
                    }
                }, 500, 500);


                break;
            case R.id.btn_context_progress_c:
                final ProgressDialog dialog2 = (ProgressDialog) StyledDialog.buildProgress("下载中...", false).show();
                final int[] progress2 = {0};

                final Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        progress2[0] += 10;
                        StyledDialog.updateProgress(dialog2, progress2[0], 100, "progress", false);
                        if (progress2[0] > 100) {
                            timer2.cancel();
                        }
                    }
                }, 500, 500);

                break;
            case R.id.btn_material_alert:
                StyledDialog.buildMdAlert("title", msg, new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        showToast("onFirst");
                    }

                    @Override
                    public void onSecond() {
                        showToast("onSecond");
                    }

                    @Override
                    public void onThird() {
                        showToast("onThird");
                    }


                })
                        .setBtnSize(29)
                        .setBtnText("i", "b", "3")
                        .setBtnColor(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.text_black)
                        .show();
                break;
            case R.id.btn_ios_alert:
                StyledDialog.buildIosAlert("title", msg, new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        showToast("onFirst");
                    }

                    @Override
                    public void onSecond() {
                        showToast("onSecond");
                    }

                    @Override
                    public void onThird() {
                        showToast("onThird");
                    }


                }).setBtnText("sure", "cancle", "hhhh").show();
                break;
            case R.id.btn_ios_alert_vertical:
                StyledDialog.buildIosAlertVertical("title", msg, new MyDialogListener() {
                    @Override
                    public void onFirst() {
                        showToast("onFirst");
                    }

                    @Override
                    public void onSecond() {
                        showToast("onSecond");
                    }

                    @Override
                    public void onThird() {
                        showToast("onThird");
                    }


                }).show();
                break;
            case R.id.btn_ios_bottom_sheet: {
                final List<String> strings = new ArrayList<>();
                strings.add("1");
                strings.add("2");
                strings.add(msg);
                strings.add("4");
                strings.add("5");
               /* strings.add(msg);
                strings.add("6");
                strings.add("7");
                strings.add(msg);
                strings.add("8");
                strings.add("9");
                strings.add(msg);

                strings.add("10");
                strings.add("11");
                strings.add(msg);
                strings.add("12");
                strings.add("13");
                strings.add(msg);*/

                StyledDialog.buildBottomItemDialog(strings, "cancle", new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        showToast(text);
                    }

                    @Override
                    public void onBottomBtnClick() {
                        showToast("onItemClick");
                    }
                }).show();
            }
            break;
            case R.id.btn_ios_center_list:

                final List<String> strings = new ArrayList<>();
                strings.add("1");
                strings.add("2");
                strings.add(msg);
                strings.add("4");
                strings.add("5");
                strings.add(msg);
             /*   strings.add("6");
                strings.add("7");
                strings.add(msg);
                strings.add("8");
                strings.add("9");
                strings.add(msg);

                strings.add("10");
                strings.add("11");
                strings.add(msg);
                strings.add("12");
                strings.add("13");
                strings.add(msg);*/

                StyledDialog.buildIosSingleChoose(strings, new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        showToast(text);
                    }

                    @Override
                    public void onBottomBtnClick() {
                        showToast("onItemClick");
                    }
                }).show();

                break;
            case R.id.btn_input:
                StyledDialog.buildNormalInput("登录", "请输入用户名", "请输入密码", "登录", "取消", new MyDialogListener() {
                    @Override
                    public void onFirst() {

                    }

                    @Override
                    public void onSecond() {

                    }

                    @Override
                    public void onGetInput(CharSequence input1, CharSequence input2) {
                        super.onGetInput(input1, input2);
                        showToast("input1:" + input1 + "--input2:" + input2);
                    }
                }).show();

                break;
            case R.id.btn_multichoose:
                String[] words = new String[]{"12", "78", "45", "89", "88", "00"};


                //boolean[] choseDefault = new boolean[]{false,false,false,false,true,false};

                StyledDialog.buildMdMultiChoose("xuanze", words, new ArrayList<Integer>(), new MyDialogListener() {
                    @Override
                    public void onFirst() {

                    }

                    @Override
                    public void onSecond() {

                    }
                }).show();
                break;
            case R.id.btn_singlechoose:
                String[] words2 = new String[]{"12", "78", "45", "89", "88", "00"};
                StyledDialog.buildMdSingleChoose("单选", 2, words2, new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        showToast(text + "--" + position);
                    }
                }).show();

                break;
            case R.id.btn_md_bs:
                String[] words3 = new String[]{"12", "78", "45", "89", "88", "00"};
                List<String> datas = Arrays.asList(words3);

                // final BottomSheetDialog dialog = new BottomSheetDialog(this);
                RecyclerView recyclerView = new RecyclerView(this);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                SuperRcvAdapter adapter = new SuperRcvAdapter(this) {
                    @Override
                    protected SuperRcvHolder generateCoustomViewHolder(int viewType) {

                        return new SuperRcvHolder<String>(inflate(R.layout.item_text)) {

                            Button mButton;

                            @Override
                            public void assignDatasAndEvents(Activity context, final String data) {
                                if (mButton == null) {
                                    mButton = (Button) itemView.findViewById(R.id.btnee);
                                }
                                mButton.setText(data);
                                mButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showToast(data);
                                    }
                                });
                            }
                        };
                    }
                };
                recyclerView.setAdapter(adapter);
                adapter.addAll(datas);
                adapter.addAll(datas);
                adapter.addAll(datas);

                StyledDialog.buildCustomBottomSheet(recyclerView).show();//不好建立回调


                break;

            case R.id.btn_md_bs_listview: {
                List<BottomSheetBean> datas2 = new ArrayList<>();

                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "1"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "222"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "333333"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "444"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "55"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "666"));

                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "7777"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "fddsf"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "67gfhfg"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "oooooppp"));


                StyledDialog.buildBottomSheetLv("拉出来溜溜", datas2, "this is cancle button", new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        showToast(text + "---" + position);
                    }
                }).show();
            }
            break;

            case R.id.btn_md_bs_Gridview:
                List<BottomSheetBean> datas2 = new ArrayList<>();

                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "1"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "222"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "333333"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "444"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "55"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "666"));

                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "7777"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "fddsf"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "67gfhfg"));
                datas2.add(new BottomSheetBean(R.mipmap.ic_launcher, "oooooppp"));


                StyledDialog.buildBottomSheetGv("拉出来溜溜", datas2, "this is cancle button", 3, new MyItemDialogListener() {
                    @Override
                    public void onItemClick(CharSequence text, int position) {
                        showToast(text + "---" + position);
                    }
                }).show();
                break;
            case R.id.btn_customview:
                ViewGroup customView = (ViewGroup) View.inflate(this, R.layout.customview, null);
                final ConfigBean bean = StyledDialog.buildCustom(customView, Gravity.CENTER).setHasShadow(false);
                final Dialog dialog1 = bean.show();
                WebView webView = (WebView) customView.findViewById(R.id.webview);
                final TextView textView = (TextView) customView.findViewById(R.id.tv_title);
                webView.loadUrl("http://www.jianshu.com/p/bcdee5821a7f");

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        Tool.adjustWH(dialog1, bean);
                    }
                });
                webView.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        super.onReceivedTitle(view, title);
                        textView.setText(title);
                    }
                });

                break;


        }
    }


    public void showToast(CharSequence msg) {
        Toast.makeText(DialogActivity.this, msg, Toast.LENGTH_SHORT).show();

    }
}
