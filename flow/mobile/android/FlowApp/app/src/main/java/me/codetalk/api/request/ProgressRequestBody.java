package me.codetalk.api.request;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import me.codetalk.event.Event;
import me.codetalk.event.EventBus;
import me.codetalk.flowapp.AppConstants;
import me.codetalk.util.FileUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by guobxu on 2018/1/9.
 */

public class ProgressRequestBody extends RequestBody {

    private File file;
    private boolean mediaParsed = false;
    private MediaType mediaType;

    private String uploadType;

    public static final int DEFAULT_BUFFER_SIZE = 4096;

    public ProgressRequestBody(File file, String uploadType) {
        this.file = file;

        this.uploadType = uploadType;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        try(FileInputStream in = new FileInputStream(file)) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                sink.write(buffer, 0, read);
                try {
                    Thread.sleep(10);
                } catch(Exception ex) {}

                Event e = Event.builder().name(AppConstants.EVENT_FILEUPLOAD_BYTES).extra1(uploadType).extra2(read).build();
                EventBus.publish(e);
            }
        }
    }

    @Override
    public MediaType contentType() {
        if(!mediaParsed) {
            String mimeType = FileUtils.getMimeType(file.getPath());
            mediaType = ( mimeType == null ? null : MediaType.parse(mimeType) );

            mediaParsed = true;
        }

        return mediaType;
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }

}
