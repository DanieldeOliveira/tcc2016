package tcc.cast5.Servidor;

import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by admin on 09/12/2015.
 */
public class webserver extends NanoHTTPD{

    private static final String TAG = "Daniel";
    public webserver(){
        super(8080);
    }

    @Override
    public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms, Map<String, String> files) {

        String mediasend = "";



        String tipo = parms.get("tipo");

        Log.d(TAG,"Tipo = " + tipo.toString());

        if(tipo.equals("img")){
            FileInputStream fileInputStream = null;
            try {
                mediasend = "image/jpeg";
                fileInputStream = new FileInputStream(uri);

            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
            return new NanoHTTPD.Response(Response.Status.OK,mediasend,fileInputStream);
        }
        else if (tipo.equals("vdo")){



            FileInputStream fileInputStream = null;
            try {
                mediasend = "video/mp4";
                Log.d(TAG,"Valor da uri " + uri.toString());
                fileInputStream = new FileInputStream(uri);
                Log.d(TAG,"Valor do fileinputstream = " + fileInputStream.toString());

            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
            return new NanoHTTPD.Response(Response.Status.OK,mediasend,fileInputStream);

        }
        else {
            mediasend = "text/html";
            String resposta = "Tamo ai";
            return new NanoHTTPD.Response(Response.Status.OK,mediasend,resposta);
        }


        //String resposta = "<html><head><title>Teste</title></head><body><p>Ola servidor</p></body></html> ";
        //

    }
}

