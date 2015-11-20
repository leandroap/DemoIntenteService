package br.com.leandroap.demointenteservice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import br.com.leandroap.demointenteservice.service.DownloadIntentService;

public class MainActivity extends AppCompatActivity {

    private EditText etUrl;
    private ProgressBar pbDownload;
    private Button btBaixar;
    private ImageView ivImagem;
    private MeuReceiverResult resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultReceiver = new MeuReceiverResult(new Handler());

        etUrl = (EditText)findViewById(R.id.etUrl);
        pbDownload = (ProgressBar)findViewById(R.id.pbDownload);
        btBaixar = (Button)findViewById(R.id.btBaixar);
        ivImagem = (ImageView)findViewById(R.id.ivImagem);

        etUrl.setText("http://www.backupnaweb.com.br/wp-content/uploads/2015/02/android-uninstall.png");

        btBaixar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(MainActivity.this,
                        DownloadIntentService.class);

                startIntent.putExtra("receiver", resultReceiver);
                startIntent.putExtra("url", etUrl.getText().toString());
                startService(startIntent);
                pbDownload.setVisibility(View.VISIBLE);
                pbDownload.setIndeterminate(true);
            }
        });

    }

    private class MeuReceiverResult extends ResultReceiver{

        public MeuReceiverResult(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle data) {
            switch (resultCode){
                case DownloadIntentService.DOWNLOAD_SUCESS:
                    Bitmap bitmap = BitmapFactory.decodeFile(data.getString("filePath"));
                    if (ivImagem != null && bitmap != null){
                        ivImagem.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Erro no decode da imagem", Toast.LENGTH_LONG).show();
                    }

                    break;
                case DownloadIntentService.DOWNLOAD_ERROR:
                    Toast.makeText(getApplicationContext(),
                            "Erro no download da imagem", Toast.LENGTH_LONG).show();
                    break;
            }

            pbDownload.setIndeterminate(false);
            pbDownload.setVisibility(View.INVISIBLE);

            super.onReceiveResult(resultCode, data);
        }
    }

}
