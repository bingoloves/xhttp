package cn.cqs.http.loading;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.cqs.http.R;

/**
 * Created by bingo on 2021/2/3.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/2/3
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(@NonNull Context context,String message,boolean isCancelable) {
        super(context);
        init(context,message,isCancelable);
    }

    protected void init(Context context, String message, boolean isCancelable) {
        View loadingView = LayoutInflater.from(context).inflate(R.layout.progress_loading,null);
        ChrysanthemumView chrysanthemumView = loadingView.findViewById(R.id.loading_view);
        TextView messageTv = loadingView.findViewById(R.id.loading_message);
        chrysanthemumView.startAnimation(1000);
        if (!TextUtils.isEmpty(message)){
            messageTv.setText(message);
            messageTv.setVisibility(View.VISIBLE);
        } else {
            messageTv.setVisibility(View.GONE);
        }
        setContentView(loadingView);
        Window dialogWindow = getWindow();
//        if (dialogWindow != null) {
//            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
//            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//            layoutParams.dimAmount = 0f;
//            dialogWindow.setAttributes(layoutParams);
//            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
//            //核心代码 解决了无法去除遮罩问题
            dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
            dialogWindow.setDimAmount(0f);
//        }
        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isCancelable);
    }
}
