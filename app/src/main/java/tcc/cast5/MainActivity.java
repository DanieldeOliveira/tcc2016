package tcc.cast5;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumerImpl;
import com.google.android.libraries.cast.companionlibrary.widgets.IntroductoryOverlay;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

import tcc.cast5.Servidor.webserver;
import tcc.cast5.media.MoverImagem;
import tcc.cast5.tools.Acesso;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Daniel";
    public static final int IMAGEM_INTERNA = 1;
    public static final int VIDEO_INTERNO = 2;
    public static final int MOVER_IMAGEM = 3;


    private VideoCastManager mCastManager;
    private VideoCastConsumer mCastConsumer;
    private Acesso mAcesso;
    private boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    private MenuItem mediaRouteMenuItem;
    private MiniController mMiniController;


    private Button btnSelVid;
    private Button btnSelImg;
    private Button btnAddImg;

    private webserver mediaserver;

    String ipdevice;
    String path = "";
    String urlFinal = "";
    Uri selectedUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoCastManager.checkGooglePlayServices(this);
        setContentView(R.layout.activity_main);
        mCastManager = VideoCastManager.getInstance();
        mAcesso = Acesso.initialize();


        mCastConsumer = new VideoCastConsumerImpl() {

            @Override
            public void onCastAvailabilityChanged(boolean castPresent) {
                if (castPresent && mIsHoneyCombOrAbove) {
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (mediaRouteMenuItem.isVisible()) {
                                showOverlay();
                            }
                        }
                    }, 1000);
                }
            }
        };


        btnSelVid = (Button) findViewById(R.id.btn_sel_vid);
        btnSelImg = (Button) findViewById(R.id.btn_sel_img);
        btnAddImg = (Button) findViewById(R.id.btn_add_img);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        ipdevice = String.format("http://%d.%d.%d.%d:8080", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));



        mediaserver = new webserver();

        try {
            mediaserver.start();
        } catch (Exception e) {

        }


        btnSelVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(Intent.createChooser(intent, "Selecione o video"), VIDEO_INTERNO);

            }
        });

        btnAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selecione a foto"), IMAGEM_INTERNA);

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        mCastManager = VideoCastManager.getInstance();
        mCastManager.incrementUiCounter();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCastManager.decrementUiCounter();
    }

    @Override
    protected void onDestroy() {
        mediaserver.stop();
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mediaRouteMenuItem = mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOverlay() {
        IntroductoryOverlay overlay = new IntroductoryOverlay.Builder(this)
                .setMenuItem(mediaRouteMenuItem)
                .setTitleText(R.string.intro_overlay_text)
                .setSingleTime()
                .build();
        overlay.show();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mCastManager.onDispatchVolumeKeyEvent(event, CastApplication.VOLUME_INCREMENT) || super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String menssagem = "";


        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == VIDEO_INTERNO) {

                selectedUri = data.getData();
                path = mAcesso.getPathVideo(selectedUri, getApplicationContext());
                urlFinal = ipdevice.toString() + path.toString() + "?tipo=vdo";
                mAcesso.startVideo(urlFinal);
            } else if (requestCode == IMAGEM_INTERNA) {

                selectedUri = data.getData();
                path = mAcesso.getUrlImagem(selectedUri, getApplicationContext());
                urlFinal = ipdevice.toString() + path.toString() + "?tipo=img";



                Intent intent = new Intent(MainActivity.this, MoverImagem.class);
                intent.putExtra("path", path);
                intent.putExtra("urlFinal",urlFinal);
                startActivityForResult(intent, MOVER_IMAGEM);

                //mCastManager.sendDataMessage(menssagem);



            }


            if (requestCode == MOVER_IMAGEM) {

               /* Log.d(TAG, "retorno do mover Imagem");
                try {
                    menssagem = mAcesso.criaJson("addImg", urlFinal);
                    Log.d(TAG, "Valor mensagem = " + menssagem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mAcesso.enviarMensagemCast(menssagem);*/


                //String msg = data.getExtras().getString("moverImg");
                //JSONObject resposta = new JSONObject(msg);


            }

        }


    }
}
