
package servidor;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            
            try (ServerSocket ss = new ServerSocket(puerto)) {
                ss.setReuseAddress(true);
                System.out.println("Servidor iniciado y en espera");

                Socket socket = ss.accept();
                System.out.println("Cliente conectado");

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                int opM = ois.readInt();
                System.out.println("la opcion elegida fue: " + opM);

                switch (opM){
                    case 1:{
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        StringBuilder listado=new StringBuilder();
                        oos.writeObject(new Servidor().listar(carpeta, "",listado));
                        oos.flush();
                        oos.close();
                        break;
                    }
                    case 2:{
                        
                        break;
                    }
                    case 3:{

                        break;
                    }
                    case 4:{
                        break;
                    }
                    case 5:{
                        break;
                    }
                    case 6:{
                        break;
                    }
                    case 7:{
                        
                        break;
                    }
                }
                ois.close();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public StringBuilder listar(File directorio, String prefijo, StringBuilder listado) {
        
        if (directorio.isDirectory()) {
            System.out.println(prefijo + "└───" + directorio.getName());
            listado.append(prefijo).append("└───").append(directorio.getName()).append("\n");
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (int i = 0; i < archivos.length; i++) {
                    if (i == archivos.length - 1) {
                        listar(archivos[i], prefijo + "    ", listado);
                    } else {
                        listar(archivos[i], prefijo + "    ", listado);
                    }
                }
            }
        } else {
            System.out.println(prefijo + "├───" + directorio.getName());
            listado.append(prefijo).append("├───").append(directorio.getName()).append("\n");
        }
        return listado;
    }
}
