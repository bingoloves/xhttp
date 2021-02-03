package cn.cqs.http;

/**
 * Created by bingo on 2020/12/18.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 响应数据拦截器
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/18
 */

public interface ResponseListener<T>{
    /**
     * 处理响应数据
     * @param data
     * @return
     */
    ResponseResult handle(T data);

    /**
     * 统一拦截后的处理
     */
    void intercept(int code);
}
