
package servidor;

import java.io.DataInputStream;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;


public class Servidor {

    static int puerto=4500;
    
    public static void main(String[] args) {
        File carpeta= new File("Carpeta remota");
        if(!carpeta.exists()){
            if(carpeta.mkdirs()){
                System.out.println("Directorio creado");
            }
        }
        for(;;){
            
            try(ServerSocket ss=new ServerSocket(puerto)){
                
                ss.setReuseAddress(true);
                System.out.println("Servidor iniciado y en espera");

                Socket socket=ss.accept();
                System.out.println("Cliente conectado");
                
                DataInputStream dis=new DataInputStream(socket.getInputStream());
                
                System.out.println("la opcion elegida fue: "+dis.readInt()+" "+dis.readLine());
            }
            catch(Exception e){

            }
        }
    }
    
}
