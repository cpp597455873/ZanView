package simonw.view.zandemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import simonw.view.zan.LikeView;

public class DemoActivity extends AppCompatActivity implements View.OnClickListener {
    public RelativeLayout layout_main;
    public Button btn_brgin;
    public Button btn_end;
    public Button btn_add;
    public Button btn_close;
    public LikeView likeView;
    public TextView tv_close;

    private int mWidth;
    private int mHeight;
    private boolean mIsFirst = true;
    private float mLastX;
    private float mLastY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);

        btn_brgin = findViewById(R.id.btn_brgin);
        btn_end = findViewById(R.id.btn_end);
        btn_add = findViewById(R.id.btn_add);
        btn_close = findViewById(R.id.btn_close);
        tv_close = findViewById(R.id.tv_close);
        btn_brgin.setOnClickListener(this);
        btn_end.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        layout_main = findViewById(R.id.layout_main);
        likeView = findViewById(R.id.like_view);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_brgin) {
//            zanView.autoAddZan();
            likeView.autoAddLike();
        } else if (id == R.id.btn_end) {
            //停止
//            zanView.stopAddHeart();
            likeView.stopAddLike();
        } else if (id == R.id.btn_add) {
            //增加一个飘赞
            //zanView.addZanXin();
            likeView.addLike();
        }
    }
}
