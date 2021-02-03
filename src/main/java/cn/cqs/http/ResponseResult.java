package cn.cqs.http;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by bingo on 2020/12/18.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 数据响应后的处理结果
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/18
 */

public class ResponseResult {
    /**
     * 错误提示类型
     */
    public static final int TYPE_ERROR = 1;
    /**
     * 直接通过类型
     */
    public static final int TYPE_PASS = 2;
    /**
     * 拦截类型
     */
    public static final int TYPE_INTERCEPT = 3;

    @IntDef({TYPE_ERROR, TYPE_PASS,TYPE_INTERCEPT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {}

    public int type;
    /**
     * 拦截时自定义处理的Code
     */
    public int code;
    /**
     * 返回的数据提示
     */
    public String msg;

    /**
     * 处理拦截时
     * @param msg
     */
    public ResponseResult(@ResponseResult.Type int type,int code, String msg) {
        this.type = type;
        this.code = code;
        this.msg = msg;
    }
}
