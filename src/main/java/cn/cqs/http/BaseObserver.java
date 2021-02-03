package cn.cqs.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import cn.cqs.http.loading.LoadingDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 优化处理通用观察者，将数据统一处理逻辑已接口的形式供外部处理
 * @param <T>:原始对象
 */
public abstract class BaseObserver<T> implements Observer<T> {
    private Disposable d;
    private LoadingDialog progressDialog;
    private String message;
    private Context context;
    //持续时间最小1500ms保证体验效果
    private static final int MIN_TIME = 1500;
    private long startTime = 0;
    /**
     * 是否显示加载框
     */
    private boolean showLoading = false;
    /**
     * 是否可取消
     */
    private boolean isCancelable = true;
    /**
     * 主线程Handler,主要负责加载框的UI显示
     */
    private Handler mainHandler;

    /**
     * 数据响应接口
     * <p>
     *     doneAfter:只负责响应拦截类型
     * </p>
     */
    private ResponseListener mResponseListener;

    public abstract void onSuccess(T data);

    public abstract void onFailure(String error);

    public BaseObserver(Context context) {
        this(context,false);
    }
    public BaseObserver(Context context,boolean showLoading) {
        this(context,showLoading,true);
    }
    public BaseObserver(Context context,boolean showLoading,String message) {
        this(context,showLoading,message,true);
    }

    public BaseObserver(Context context,boolean showLoading,boolean isCancelable) {
        this(context,showLoading,"加载中...",isCancelable);
    }

    public BaseObserver(Context context,boolean showLoading,String message,boolean isCancelable) {
        this.context = context;
        this.showLoading = showLoading;
        this.isCancelable = isCancelable;
        this.message = message;
    }
    /**
     * 订阅在执行请求的线程中，执行的结果回调却是在主线程中
     * @param d
     */
    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
        this.mainHandler = getMainHandler();
        if (showLoading && progressDialog == null){
            progressDialog = new LoadingDialog(context,message,isCancelable);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                }
            });
        }
        startTime = System.currentTimeMillis();
        mResponseListener = HttpConfig.getHttpConfig().getResponseListener();
    }

    /**
     * 这里对数据进行二次处理，减少onSuccess回调中的去层判断
     * @param data
     */
    @Override
    public void onNext(final T data) {
        if (mResponseListener != null && mResponseListener.handle(data) != null){
            final ResponseResult responseResult = mResponseListener.handle(data);
            @ResponseResult.Type final int type = responseResult.type;
            switch (type){
                case ResponseResult.TYPE_ERROR:
                    onError(new Throwable(responseResult.msg));
                    break;
                case ResponseResult.TYPE_PASS:
                    done(data);
                    break;
                case ResponseResult.TYPE_INTERCEPT://拦截状态,外部处理，这里只负责关闭当前请求,将处理方式用接口抛出
                    intercept(responseResult.code);
                    break;
                default:
                    break;
            }
        } else {
            done(data);
        }
    }
    /**
     * 进入到onError 不会进入到 onComplete
     * @param e
     */
    @Override
    public void onError(final Throwable e) {
        handleDialogEvent(new Runnable() {
            @Override
            public void run() {
                hideDialog();
                onFailure(exceptionHandler(e));
            }
        });
    }

    /**
     * 处理真正的返回数据，过滤后的可用数据
     * @param data
     */
    public void done(final T data){
        handleDialogEvent(new Runnable() {
            @Override
            public void run() {
                hideDialog();
                onSuccess(data);
            }
        });
    }
    /**
     * 处理真正的返回数据，过滤后拦截操作
     * @param code
     */
    public void intercept(final int code){
        handleDialogEvent(new Runnable() {
            @Override
            public void run() {
                hideDialog();
                mResponseListener.intercept(code);
            }
        });
    }

    @Override
    public void onComplete() {
        if (d.isDisposed()) {
            d.dispose();
        }
        handleDialogEvent(new Runnable() {
            @Override
            public void run() {
                hideDialog();
            }
        });
    }

    /**
     * 处理dialog 显示效果
     */
    private void handleDialogEvent(Runnable runnable){
         long endTime = System.currentTimeMillis();
         long timeDiff = endTime - startTime;
         if (timeDiff > MIN_TIME){
             mainHandler.post(runnable);
         } else {
             mainHandler.postDelayed(runnable,MIN_TIME - timeDiff);
         }
    }

    /**
     * 隐藏dialog
     */
    private void hideDialog(){
        if (showLoading && progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    /**
     * 异常处理
     * @param e
     * @return 返回错误信息
     */
    private String exceptionHandler(Throwable e){
        String errorMsg = "未知错误";
        if (e instanceof UnknownHostException) {
            errorMsg = "网络不可用";
        } else if (e instanceof SocketTimeoutException) {
            errorMsg = "请求网络超时";
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            errorMsg = convertStatusCode(httpException);
        } else if (e instanceof ParseException || e instanceof JSONException) {
            errorMsg =  "数据解析错误";
        } else {
            errorMsg = e.getMessage();
        }
        return errorMsg;
    }

    private String convertStatusCode(HttpException httpException) {
        String msg;
        if (httpException.code() >= 500 && httpException.code() < 600) {
            msg =  "服务器处理请求出错";
        } else if (httpException.code() >= 400 && httpException.code() < 500) {
            msg =  "服务器无法处理请求";
        } else if (httpException.code() >= 300 && httpException.code() < 400) {
            msg =  "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }
    /**
     * 判断是否是主线程
     * @return
     */
    public boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 获取主线程handler
     * @return
     */
    public Handler getMainHandler(){
        return new Handler(Looper.getMainLooper());
    }
}
