package cn.cqs.http;

import android.text.TextUtils;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bingo on 2020/12/18.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 网络相关的配置
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/18
 */
public class HttpConfig {
    private Config defaultConfig;
    //数据响应拦截器接口
    private ResponseListener mResponseListener;

    private static class HttpConfigSingletonHolder {
        private static final HttpConfig INSTANCE = new HttpConfig();
    }
    public static HttpConfig getHttpConfig(){
        return HttpConfigSingletonHolder.INSTANCE;
    }
    private HttpConfig (){
        this.defaultConfig = new Config();
    }

    public static class Config{
        /**
         * 请填写项目的域名地址
         */
        public String host;
        public String baseUrl;
        public String token = "token";
        public long timeout = 10;
        public boolean openLog = true;
        public Retrofit retrofit;
        public OkHttpClient okHttpClient;
        public Converter.Factory converterFactory = GsonConverterFactory.create();
        public CallAdapter.Factory adapterFactory = RxJava2CallAdapterFactory.create();
        //OkHttp拦截器
        public List<Interceptor> interceptors = new ArrayList<>();

        public Config setHost(String HOST) {
            this.host = HOST;
            return this;
        }

        public Config setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Config setToken(String token) {
            this.token = token;
            return this;
        }

        public Config setTimeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Config setOpenLog(boolean openLog) {
            this.openLog = openLog;
            return this;
        }

        public Config setRetrofit(Retrofit retrofit) {
            this.retrofit = retrofit;
            return this;
        }

        public Config setOkHttpClient(OkHttpClient okHttpClient) {
            this.okHttpClient = okHttpClient;
            return this;
        }

        public Config setConverterFactory(Converter.Factory converterFactory) {
            this.converterFactory = converterFactory;
            return this;
        }

        public Config setAdapterFactory(CallAdapter.Factory adapterFactory) {
            this.adapterFactory = adapterFactory;
            return this;
        }

        public Config addInterceptor(Interceptor interceptor) {
            this.interceptors.add(interceptor);
            return this;
        }

        public String getBaseUrl() {
            if (baseUrl != null){
                return baseUrl;
            } else {
                if (TextUtils.isEmpty(host)) throw new IllegalArgumentException("请先配置HttpConfig中的host域名");
                return host + "/";
            }
        }
    }

    public Config getConfig() {
        return defaultConfig;
    }

    /**
     * 默认构建 Retrofit
     * @return
     */
    public Retrofit getRetrofit(){
        return new Retrofit.Builder()
                .baseUrl(defaultConfig.getBaseUrl())
                .client(getOkHttpClient(defaultConfig.timeout,defaultConfig.openLog,defaultConfig.interceptors))
                .addConverterFactory(defaultConfig.converterFactory)
                .addCallAdapterFactory(defaultConfig.adapterFactory)
                .build();
    }

    /**
     *  通过Config 构建 Retrofit
     * @param config
     * @return
     */
    public Retrofit getRetrofit(Config config){
        return new Retrofit.Builder()
                .baseUrl(config.getBaseUrl())
                .client(getOkHttpClient(config.timeout,config.openLog))
                .addConverterFactory(config.converterFactory)
                .addCallAdapterFactory(config.adapterFactory)
                .build();
    }

    /**
     * 完全原始创建Retrofit
     * @param baseUrl
     * @param okHttpClient
     * @return
     */
    public Retrofit getRetrofit(String baseUrl, OkHttpClient okHttpClient){
        if (TextUtils.isEmpty(baseUrl)) throw new IllegalArgumentException("baseUrl is null");
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public OkHttpClient getOkHttpClient(long timeout, boolean openLog){
        return getOkHttpClient(timeout,true,null);
    }
    public OkHttpClient getOkHttpClient(Interceptor interceptor){
        return getOkHttpClient(defaultConfig.timeout,true, Collections.singletonList(interceptor));
    }
    /**
     * 自定义拦截器
     * @param timeout
     * @param openLog
     * @param interceptors
     * @return
     */
    public OkHttpClient getOkHttpClient(long timeout, boolean openLog, List<Interceptor> interceptors){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(timeout,TimeUnit.SECONDS)
                .writeTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS);
        if (openLog){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        if (interceptors != null && !interceptors.isEmpty()){
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }
        return builder.build();
    }

    public String getHost() {
        return defaultConfig.host;
    }

    public void setHost(String host) {
        defaultConfig.setHost(host);
    }
    public void addInterceptor(Interceptor interceptor) {
        defaultConfig.addInterceptor(interceptor);
    }
    /**
     * BaseUrl 必须以'/'结尾
     * @return
     */
    public String getBaseUrl() {
        return defaultConfig.getBaseUrl();
    }


    public String getToken() {
        return defaultConfig.token;
    }

    public void setToken(String token) {
        defaultConfig.setToken(token);
    }

    public long getTimeout() {
        return defaultConfig.timeout;
    }

    public void setTimeout(long timeout) {
        defaultConfig.setTimeout(timeout);
    }

    public boolean isOpenLog() {
        return defaultConfig.openLog;
    }

    public void setOpenLog(boolean openLog) {
        defaultConfig.setOpenLog(openLog);
    }

    public ResponseListener getResponseListener() {
        return mResponseListener;
    }

    /**
     * 设置拦截器
     * @param responseListener
     */
    public void setResponseHandler(ResponseListener responseListener) {
        this.mResponseListener = responseListener;
    }
}
