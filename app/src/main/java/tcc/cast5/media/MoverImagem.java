package tcc.cast5.media;


import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import org.json.JSONException;
import org.json.JSONObject;

import tcc.cast5.R;
import tcc.cast5.tools.Acesso;

/**
 * Created by admin on 23/12/2015.
 */
public class MoverImagem extends AppCompatActivity {
    private static final  String TAG = "Daniel";
    private boolean imagemClicada = false;
    private ImageView imagem;
    private DisplayMetrics metrics;
    private float widthTela,heightTela;
    private AlertDialog alerta;
    private JSONObject resposta;
    private float x,y;
    private String pathImg;
    private  Bundle extras;
    private VideoCastManager mCastManager;
    private Acesso mAcesso;
    private String mensagem = "";
    private String urlFinal = "";
    private int widthRealImg, heightRealImg;
    private LinearLayout mLayout;
    private LayoutParams dimensaoImagem;
    float mEscala;
    int mLarguraOrigImg;
    int mAlturaOrigImg;
    ScaleGestureDetector mScaleDetector;


    ScaleGestureDetector.OnScaleGestureListener mScaleListener = new ScaleGestureDetector.SimpleOnScaleGestureListener(){

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mEscala *= detector.getScaleFactor();
            mEscala = Math.max(1.0f, Math.min(mEscala, 3.0f));
            ViewGroup.LayoutParams lp = imagem.getLayoutParams();
            lp.width = (int)(mLarguraOrigImg * mEscala);
            lp.height = (int)(mAlturaOrigImg * mEscala);
            imagem.setLayoutParams(lp);
            return true;

        }
    };

    ViewTreeObserver.OnGlobalLayoutListener mImageViewGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mLarguraOrigImg = dimensaoImagem.width;
            mAlturaOrigImg = dimensaoImagem.height;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                imagem.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }else{
                imagem.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        }
    };
    View.OnTouchListener mImageViewTouchListener =  new  View.OnTouchListener(){

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return mScaleDetector.onTouchEvent(event);
        }
    };








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_teste);
        mCastManager = VideoCastManager.getInstance();
        mAcesso = Acesso.getsInstance();
        imagem = new ImageView(this);
        mLayout = (LinearLayout)findViewById(R.id.teste);
        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        extras = getIntent().getExtras();
        pathImg = extras.getString("path");
        urlFinal = extras.getString("urlFinal");
        Bitmap bitmap = BitmapFactory.decodeFile(pathImg);
        widthRealImg = bitmap.getWidth();
        heightRealImg = bitmap.getHeight();
        dimensaoImagem = new LayoutParams(((int)(widthRealImg * 0.1)),((int)(heightRealImg * 0.1)));
        imagem.setLayoutParams(dimensaoImagem);
        imagem.setImageBitmap(bitmap);
        imagem.setVisibility(View.VISIBLE);
        mLayout.addView(imagem);
        resposta = new JSONObject();
        widthTela = metrics.widthPixels;
        heightTela = metrics.heightPixels;
        Log.d(TAG,"Valor da metade da tela = " + widthTela/2);
        Log.d(TAG,"Valor da metade da imagem = " + widthRealImg * 0.1);
        imagem.setX(((widthTela / 2) - (float)(widthRealImg * 0.05)));
        imagem.setY(((heightTela/2) - (float)(heightRealImg * 0.05)));
        /*mScaleDetector = new ScaleGestureDetector(this,mScaleListener);
        imagem.setOnTouchListener(mImageViewTouchListener);
        imagem.getViewTreeObserver().addOnGlobalLayoutListener(mImageViewGlobalLayoutListener);*/









    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                imagemClicada = isImagemClicada(event.getX(),event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                if (imagemClicada){
                    if(event.getX() >= (widthTela - imagem.getWidth())){
                        x = (widthTela - imagem.getWidth());
                    }
                    else if(event.getY() >= (heightTela - imagem.getHeight())){
                        y = (heightTela - imagem.getHeight());
                    }
                    else if(event.getX() < 0.0){
                        x = 0.0f;
                    }
                    else if(event.getY() < 0.0){
                        y = 0.0f;
                    }
                    else {
                        x = event.getX();
                        y = event.getY();
                    }
                    imagem.setX(x);
                    imagem.setY(y);


                }
                break;
            case MotionEvent.ACTION_UP:

                     mostrar_alerta();
                    Log.d(TAG, "Valor de X ao soltar a imagem = " + x);
                   /*if (imagemClicada){
                       mostrar_alerta();
                   }*/



                break;
        }
        return true;
    }

    private void mostrar_alerta(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Envio para o Chromecast");
        builder.setMessage("Enviar a imagem para o chromecast?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                try {

                    resposta.put("idImg",mAcesso.getContador() + 1);
                    resposta.put("url",urlFinal);
                    resposta.put("widthImagem", String.valueOf(imagem.getWidth()));
                    resposta.put("heightImagem", String.valueOf(imagem.getHeight()));
                    resposta.put("widthTela", String.valueOf(widthTela));
                    resposta.put("heightTela", String.valueOf(heightTela));
                    resposta.put("xImagem",x);
                    resposta.put("yImagem",y);
                    resposta.put("widthRealImg",String.valueOf(widthRealImg));
                    resposta.put("heightRealImg",String.valueOf(heightRealImg));
                    mensagem = mAcesso.criaJson("addImg", urlFinal);
                    mAcesso.incrementaContador();
                    Log.d(TAG,"Valor da mensagem = " + mensagem);
                    Log.d(TAG,"Valor da variavel resposta = " + resposta.toString());
                    mCastManager.sendDataMessage(mensagem);
                    onBackPressed();

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (TransientNetworkDisconnectionException e) {
                    e.printStackTrace();
                } catch (NoConnectionException e) {
                    e.printStackTrace();
                }
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                onBackPressed();
            }
        });

        alerta = builder.create();
        alerta.show();
    }

    /*@Override
    public void onBackPressed() {
        Intent it = new Intent();
        it.putExtra("moverImg", resposta.toString());
        setResult(3,it);
        super.onBackPressed();
    }*/

    private boolean isImagemClicada(float x, float y) {

        if ((x >= imagem.getX() && x <= (imagem.getX() + imagem.getWidth())) && (y >= imagem.getY() && y <= (imagem.getY() + imagem.getHeight()))) {

            return true;
        }
        return false;
    }



    /*
     Evento de escala

    */


}
