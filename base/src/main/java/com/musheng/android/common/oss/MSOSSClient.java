package com.musheng.android.common.oss;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.musheng.android.common.log.MSLog;

import java.io.File;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Author      : FJ
 * CreateDate  : 2019/04/24 13:44
 * Description :
 */

public class MSOSSClient {

    private static final String TAG = "OSS";

    private static MSOSSClient mInstance = new MSOSSClient();

    private OSSClient ossClient;

    public static String PREFIX_HTTP = "http://";
    public static String ENDPOINT = "oss-cn-shenzhen.aliyuncs.com";
    public static String BUCKET = "hippo-app";
    public static String ACCESS_KEY_ID = "LTAI4FcHV3Hs2zV71H11aaSM";
    public static String ACCESS_KEY_SECRET = "vvWBYGA9TXnjmqFMotJMwdakmictpu";
    public static String FOLDER_NAME_HEAD_IMAGE = "headImg/%1$s";

    private Context context;

    private MSOSSClient(){

    }

    public static MSOSSClient getInstance(){
        return mInstance;
    }

    public void init(Context context){
        this.context = context;
    }

    public void uploadHeadImage(String tag, String filePath, OnUploadResponseListener listener) {
        String objectKey = String.format(FOLDER_NAME_HEAD_IMAGE, tag) + UUID.randomUUID().toString() + ".jpg";
        OSSUploadFile ossFile = new OSSUploadFile(filePath, objectKey);
        asyncPutObject(ossFile, listener);
    }


    public boolean putObject(OSSUploadFile ossFile){
        if (ossClient == null) {
            ossClient = COSSClient.getInstance().getOSSClient(context);
        }

        if (ossFile == null) {
            MSLog.e(TAG, "putObject() ossFile is null.");
            return false;
        }

        String filePath = ossFile.getFilePath();
        if (TextUtils.isEmpty(filePath)) {
            MSLog.e(TAG, "putObject() filePath is empty.");
            return false;
        } else {
            // 如果文件路径带有file:// 则替换成空字符串
            if (filePath.contains("file://")) {
                filePath = filePath.replace("file://", "");
            }
        }

        final String bucketName = ossFile.getBucketName();
        if (TextUtils.isEmpty(bucketName)) {
            MSLog.e(TAG, "putObject() bucketName is empty.");
            return false;
        }

        final String objectKey = ossFile.getObjectKey();
        if (TextUtils.isEmpty(objectKey)) {
            MSLog.e(TAG, "putObject() objectKey is empty.");
            return false;
        }

        // 文件元信息的设置是可选的
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("application/octet-stream"); // 设置content-type
        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5
        // put.setMetadata(metadata);
        final File uploadFile = new File(filePath);
        if (!uploadFile.exists()) {
            MSLog.d(TAG, "putObject() 文件不存在.");
            return false;
        }


        final PutObjectRequest request = new PutObjectRequest(bucketName, objectKey,
                filePath/*, metadata*/);

        try {
            PutObjectResult putObjectResult = ossClient.putObject(request);
            MSLog.e(TAG, "putObject() result " + putObjectResult);
            if(putObjectResult.getStatusCode() == 200){
                return true;
            }
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * oss上传文件
     *
     * @param ossFile
     * @param listener
     */
    public void asyncPutObject(final OSSUploadFile ossFile, final OnUploadResponseListener listener) {
        
        if (ossClient == null) {
            ossClient = COSSClient.getInstance().getOSSClient(context);
        }

        if (ossFile == null) {
            MSLog.e(TAG, "asyncPutObject() ossFile is null.");
            return;
        }

        String filePath = ossFile.getFilePath();
        if (TextUtils.isEmpty(filePath)) {
            MSLog.e(TAG, "asyncPutObject() filePath is empty.");
            return;
        } else {
            // 如果文件路径带有file:// 则替换成空字符串
            if (filePath.contains("file://")) {
                filePath = filePath.replace("file://", "");
            }
        }

        final String bucketName = ossFile.getBucketName();
        if (TextUtils.isEmpty(bucketName)) {
            MSLog.e(TAG, "asyncPutObject() bucketName is empty.");
            return;
        }

        final String objectKey = ossFile.getObjectKey();
        if (TextUtils.isEmpty(objectKey)) {
            MSLog.e(TAG, "asyncPutObject() objectKey is empty.");
            return;
        }

        // 文件元信息的设置是可选的
        // ObjectMetadata metadata = new ObjectMetadata();
        // metadata.setContentType("application/octet-stream"); // 设置content-type
        // metadata.setContentMD5(BinaryUtil.calculateBase64Md5(uploadFilePath)); // 校验MD5
        // put.setMetadata(metadata);
        final File uploadFile = new File(filePath);
        if (!uploadFile.exists()) {
            MSLog.d(TAG, "asyncPutObject() 文件不存在.");
            return;
        }

        final String serverUrl = ossFile.getUploadUrl();
        if (listener != null) {
            listener.onStart(filePath, serverUrl);
        }

        final PutObjectRequest request = new PutObjectRequest(bucketName, objectKey,
                filePath/*, metadata*/);

        final String finalFilePath = filePath;
        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                
                request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {

                    int lastProgress = 0;
                    @Override
                    public void onProgress(PutObjectRequest request, final long currentSize, final long totalSize) {
                        MSLog.d(TAG, "asyncPutObject() onProgress[currentSize=" + currentSize + "\ttotalSize=" + totalSize + "]");
                        int progress = (int) ((currentSize * 1.0f / totalSize) * 100);
                        if(progress >= lastProgress + 7) {
                            emitter.onNext(String.valueOf(progress));
                            lastProgress = progress;
                        }
                    }
                });
                ossClient.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {

                    @Override
                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                        MSLog.d(TAG, "asyncPutObject() onSuccess[tag=" + result.getETag() + "\trequestId=" + result.getRequestId() + "\tfileSize=" + uploadFile.length() + "\turl=" + serverUrl + "]");
                        emitter.onNext(serverUrl);
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                        String errorMsg = null;
                        // 请求异常
                        if (clientException != null) {
                            // 本地异常如网络异常等
                            errorMsg = clientException.getMessage();
                            clientException.printStackTrace();
                        } else if (serviceException != null) {
                            // 服务异常
                            errorMsg = serviceException.getMessage();
                            serviceException.printStackTrace();
                            MSLog.e(TAG, "asyncPutObject() onFailure[errorCode=" + serviceException.getErrorCode() + "]");
                            MSLog.e(TAG, "asyncPutObject() onFailure[requestId=" + serviceException.getRequestId() + "]");
                            MSLog.e(TAG, "asyncPutObject() onFailure[hostId=" + serviceException.getHostId() + "]");
                            MSLog.e(TAG, "asyncPutObject() onFailure[rawMessage=" + serviceException.getRawMessage() + "]");
                        }

                        Throwable t = null;
                        if (!TextUtils.isEmpty(errorMsg)) {
                            t = new Throwable(errorMsg);
                        } else {
                            t = new Throwable("unknown exception.");
                        }

                        emitter.onError(t);
                        emitter.onComplete();
                    }
                });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {

                    private Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        this.disposable = d;
                    }

                    @Override
                    public void onNext(String s) {
                        if (listener != null) {
                            if(s.startsWith("http")){
                                listener.onSuccess(finalFilePath, s);
                            } else {
                                try{
                                    listener.onProgress(finalFilePath, Integer.valueOf(s));
                                } catch (Exception e){
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                        disposable = null;

                        if (listener != null) {
                            listener.onFailure(finalFilePath, e.getMessage());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (listener != null) {
                            listener.onFinish();
                        }
                    }
                });
    }
    
    public static class COSSClient {

        private static COSSClient instance;
        private OSSClient ossClient;

        private COSSClient() {

        }

        public static COSSClient getInstance() {
            if(instance == null) {
                synchronized (COSSClient.class) {
                    if(instance == null) {
                        instance = new COSSClient();
                    }
                }
            }

            return instance;
        }

        public void init(Context context) {
            OSSCredentialProvider credentialProvider = new StsGetter();
            
            ClientConfiguration configuration = new ClientConfiguration();
            configuration.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
            configuration.setSocketTimeout(15 * 1000); // socket超时，默认15秒
            configuration.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
            configuration.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
            ossClient = new OSSClient(context,
                    PREFIX_HTTP+ENDPOINT,
                    credentialProvider, 
                    configuration);
        }

        public OSSClient getOSSClient(Context context) {
            if(ossClient == null) {
                init(context);
            }
            return ossClient;
        }
    }

    public static class StsGetter extends OSSFederationCredentialProvider {

        @Override
        public OSSFederationToken getFederationToken() {
            String accessKeyId = ACCESS_KEY_ID;
            String accessKeySecret = ACCESS_KEY_SECRET;
            String securityToken = "";
            return new OSSFederationToken(accessKeyId, accessKeySecret, securityToken, 24 * 60 * 60);
        }
    }
    
    public static class OSSUploadFile {

        private String bucketName;
        private String objectKey;
        private String filePath;
        private String endpoint;
        private String uploadUrl;

        public OSSUploadFile() {

        }

        public OSSUploadFile(String filePath, String objectKey) {
            this(filePath, objectKey, BUCKET, ENDPOINT);
        }

        public OSSUploadFile(String filePath, String objectKey, String bucketName, String endpoint) {
            this.filePath = filePath;
            this.objectKey = objectKey;
            this.bucketName = bucketName;
            this.endpoint = endpoint;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public void setObjectKey(String objectKey) {
            this.objectKey = objectKey;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getUploadUrl() {
            this.uploadUrl = PREFIX_HTTP + BUCKET + "." + endpoint + "/" + objectKey;
            return uploadUrl;
        }

        @Override
        public String toString() {
            return "OSSUploadFile:{"
                    + "\"bucketName\":" + this.bucketName + ","
                    + "\"objectKey\":" + this.objectKey + ","
                    + "\"filePath\":" + this.filePath + ","
                    + "\"endpoint\":" + this.endpoint + "}";
        }
    }

    public static abstract class OnUploadResponseListener {

        public void onStart(String filePath, String url) {

        }

        public void onProgress(String filePath, int progress) {

        }

        public void onSuccess(String filePath, String url) {

        }

        public void onFailure(String filePath, String errorMsg) {

        }

        public void onFinish() {

        }
    }
}
