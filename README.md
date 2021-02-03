# xhttp
基于Retrofit2+rxjava二次封装可直接使用

#### 使用
```gradle
  //添加依赖
  implementation 'com.github.bingoloves:xhttp:xxx'
```
```java
   //网络请求框架
   HttpConfig.getHttpConfig().setHost("http://www.baidu.com");//配置域名或者setBaseUrl()
   HttpConfig.getHttpConfig().addInterceptor(new TokenInterceptor());//配置okhttp拦截器
   //添加数据响应二次封装处理，不用可以不配置
   HttpConfig.getHttpConfig().setResponseHandler(new ResponseListener() {
        @Override
        public ResponseResult handle(Object o) {
  //        return new ResponseResult(ResponseResult.TYPE_PASS,0,null);//这种是不拦截直接通行
  //        return new ResponseResult(ResponseResult.TYPE_ERROR,0,"测试拦截处理");//这种是给出一些统一处理
          return new ResponseResult(ResponseResult.TYPE_INTERCEPT,100,null);//这种是统一拦截
         }
  
         @Override
         public void intercept(int code) {
            if (code == 100){
                ToastUtils.show("拦截到数据异常情况");
  //            Intent intent = new Intent(App.this, MainActivity.class);
  //            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
  //            App.this.startActivity(intent);
            }
          }
   });
   
   //请求示例
   RetrofitUtil.getService(ApiService.class).testApi("top")
        .subscribeOn(Schedulers.io())
   //   .observeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new BaseObserver<BaseResponse>(this,true) {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                   LogUtils.e("onSuccess");
            }
   
            @Override
            public void onFailure(String s) {
                 LogUtils.e("onFailure");
                 ToastUtils.show(s);
            }
        });
```
