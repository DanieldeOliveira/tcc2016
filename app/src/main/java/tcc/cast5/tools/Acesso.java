package tcc.cast5.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.libraries.cast.companionlibrary.cast.VideoCastManager;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 02/01/2016.
 */
public  class Acesso {

    private static final String TAG = "Daniel";
    private int contadorImagem = 0;

    private static Acesso sInstance;
    private VideoCastManager mCastManager;
    private int qtdImg = 0;

    private Acesso(){

        mCastManager = VideoCastManager.getInstance();
    }



    public static Acesso  initialize(){

        sInstance = new Acesso();
        return sInstance;

    }

    public static Acesso getsInstance()
    {

        return sInstance;
    }

    public void incrementaContador(){
        contadorImagem ++;
    }

    public int getContador(){
        return contadorImagem;
    }

    public void startVideo(String url){

        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, "Testando Cast Companion Library");

        MediaInfo mediaInfo = new MediaInfo.Builder(url)
                .setContentType("video/mp4")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .build();

        try {
            mCastManager.loadMedia(mediaInfo, true, 0);


        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();

        }
    }

    public String getPathVideo(Uri uri,Context ctx){

        Context context = ctx;
        Uri contentUri = uri;
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = null;

        try {
            if (Build.VERSION.SDK_INT > 19) {


                String wholeID = DocumentsContract.getDocumentId(contentUri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                // where id is equal to
                //String sel = MediaStore.Images.Media._ID + "=?";
                String sel = MediaStore.Video.Media._ID + "=?";

                cursor = context.getContentResolver().query(
                        // MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        projection, sel, new String[]{id}, null);
                if(cursor == null){

                }

            } else {
                cursor = context.getContentResolver().query(contentUri,
                        projection, null, null, null);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        String path = null;
        try {
            int column_index = cursor
                    //.getColumnIndex(MediaStore.Images.Media.DATA);
                    .getColumnIndex(MediaStore.Video.Media.DATA);

            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return path;
    }


    public String getUrlImagem(Uri uri, Context ctx){

        Context context = ctx;
        Uri contentUri = uri;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        String path = null;
        try{
            if(Build.VERSION.SDK_INT > 19){
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";


                cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, sel, new String[] { id }, null);
            }
            else{
                cursor = context.getContentResolver().query(contentUri,
                        projection, null, null, null);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            int column_index = cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();
            path = cursor.getString(column_index).toString();
            cursor.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return path;

    }

    public String criaJson(String escolha, String url)throws JSONException {
        JSONObject json = new JSONObject();

        json.put("choice", escolha);
        json.put("url",url);
        json.put("id","img" + String.valueOf(qtdImg + 1));
        qtdImg++;
        return json.toString();
    }

    public void enviarMensagemCast(String mensagem){

        try {
            mCastManager.sendDataMessage(mensagem);
            Log.d(TAG,"Enviou a mensagem de dentro do enviarMensagem");
        } catch (TransientNetworkDisconnectionException e) {
            Log.d(TAG,"Excecao TransientNetworkDisconnectionException");
            e.printStackTrace();
        } catch (NoConnectionException e) {
            Log.d(TAG,"Excecao NoConnectionException");
            e.printStackTrace();
        }
    }

}
