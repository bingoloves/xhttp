package cn.cqs.http;

import retrofit2.Retrofit;

/**
 * retrofit 工具封装
 */
public class RetrofitUtil {

    private RetrofitUtil() {
    }

    public static RetrofitUtil getInstance() {
        return RetrofitManager.retrofitManage;
    }

    private static class RetrofitManager {
        private static final RetrofitUtil retrofitManage = new RetrofitUtil();
    }
    public static Retrofit getRetrofit(){
        return HttpConfig.getHttpConfig().getRetrofit();
    }
    /**
     * 使用前，请先配置HttpConfig中的Host域名
     * @return
     */
    public static <T> T getService(Class<T> service) {
        return getRetrofit().create(service);
    }
}
