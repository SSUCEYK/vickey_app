package com.example.vickey;

import android.content.Context;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class S3Helper {
    private AmazonS3Client s3Client;
    private TransferUtility transferUtility;

    public S3Helper(Context context) {

        // BasicAWSCredentials를 통해 key, secret key를 설정하고, region을 서울(ap_northeast_2)로 설정함
//        s3Client = new AmazonS3Client(new BasicAWSCredentials(BuildConfig.AWS_KEY, BuildConfig.AWS_SECRET_KEY),Region.getRegion(Regions.AP_NORTHEAST_2));
        s3Client = new AmazonS3Client(new BasicAWSCredentials(context.getString(R.string.AWSKey),
                context.getString(R.string.AWSSecretKey)),Region.getRegion(Regions.AP_NORTHEAST_2));

        // TransferUtility 초기화
        transferUtility = TransferUtility.builder().context(context).s3Client(s3Client).build();

    }

    // S3에서 파일 다운로드하는 메소드
    // bucket name : s3 버킷명
    // key : file key(이름이나 경로) to download
    // file : file path(로컬의 파일 객체) to save
    // listener : download 상태 모니터링
    public void downloadFile(String bucketName, String key, File file, TransferListener listener) {
        transferUtility.download(bucketName, key, file).setTransferListener(listener);
    }
}
