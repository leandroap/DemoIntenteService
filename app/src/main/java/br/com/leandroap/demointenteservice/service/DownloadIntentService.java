package br.com.leandroap.demointenteservice.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DownloadIntentService extends IntentService {

    public static final int DOWNLOAD_ERROR = 10;
    public static final int DOWNLOAD_SUCESS = 11;

    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url = intent.getStringExtra("url");
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        File downloadFile = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath(),
                "intent_image.png");

        if (downloadFile.exists()) {
            downloadFile.delete();

            try {
                downloadFile.createNewFile();
                URL downloadURL = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) downloadURL.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    throw new Exception("Não foi possivel obter a conexao");
                }
                InputStream is = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(downloadFile);
                byte buffer[] = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    os.write(buffer, 0, byteCount);
                }

                os.close();
                ;
                is.close();
                Bundle bundle = new Bundle();
                bundle.putString("filePath", downloadFile.getPath());
                receiver.send(DOWNLOAD_SUCESS, bundle);
            } catch (Exception e) {
                receiver.send(DOWNLOAD_ERROR, Bundle.EMPTY);
            }
        }
    }

}
