package com.example.vickey;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.File;
import java.util.List;

public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {
    private List<String> videoKeys; // S3 비디오 키 리스트
    private Context context;
    private S3Helper s3Helper;

    public VideoPagerAdapter(Context context, List<String> videoKeys) {
        this.context = context;
        this.videoKeys = videoKeys;
        this.s3Helper = new S3Helper(context);
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shorts, parent, false); // video_item.xml 레이아웃을 인플레이트하여 새로운 뷰를 생성
        return new VideoViewHolder(view);
    }

    //RecyclerView가 내부적으로 항목을 배치할 때 LayoutManager를 통해 onBindViewHolder()를 자동으로 호출
    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder holder, int position) {
        String videoKey = videoKeys.get(position);
        File videoFile = new File(context.getCacheDir(), "video_" + position + ".mp4"); //캐시 디렉토리에 순서대로 저장

        // S3에서 파일 다운로드 후 VideoView에 재생
        s3Helper.downloadFile(context.getString(R.string.AWSS3BucketName), videoKey, videoFile, new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (state == TransferState.COMPLETED) {
                    holder.videoView.setVideoURI(Uri.fromFile(videoFile)); //다운로드 된 videoFile로 holder에 담긴 video view의 경로를 set up
                    holder.videoView.start();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                // 진행 상황 표시 가능
            }

            @Override
            public void onError(int id, Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // RecyclerView가 처음 화면에 나타날 때, 이 메소드를 호출해서 전체 항목의 수 (표시할 항목의 총 개수)를 확인
    // 데이터가 변경되거나 항목이 추가/삭제될 때도 이 메소드가 호출됨
    @Override
    public int getItemCount() {
        return videoKeys.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;

        //생성자에서 해당 항목의 뷰를 초기화
        //itemView(각 항목의 루트 뷰)로부터 findViewById()를 사용해 VideoView를 찾고 할당
        //이후 videoView는 계속 재사용됨
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }
}
