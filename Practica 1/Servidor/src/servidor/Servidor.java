
package servidor;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Servidor {

    static int puerto=4500;
    
    public static void main(String[] args) throws ClassNotFoundException {
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
                        Servidor servidor=new Servidor();
                        ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
                        DataInputStream oiss=new DataInputStream(socket.getInputStream());
                        servidor.borradoOp(carpeta, socket,oos,oiss);
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
                socket.close();
                
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
        }
        else {
            System.out.println(prefijo + "├───" + directorio.getName()+permisos(directorio));
            listado.append(prefijo).append("├───").append(directorio.getName()).append(permisos(directorio)).append("\n");
        }
        return listado;
    }
    
    public void borradoOp(File carpeta,Socket socket, ObjectOutputStream oos, DataInputStream ois) throws ClassNotFoundException, IOException{
        File[] lista=carpeta.listFiles();
        
        
        /*System.out.println("\nSe encuentra en: "+carpeta.getName()+"\n");
        System.out.print("\nQuiere eliminar algun archivo o carpeta de este directorio \n1-. Si  2-. No: \n");*/
        
        StringBuilder s=new StringBuilder();
        s.append("\nSe encuentra en: ").append(carpeta.getName()).append("\n\n");
        s.append("\nQuiere eliminar algun archivo o carpeta de este directorio \n1-. Si  2-. No:");
        oos.writeObject(s);
        oos.flush();
        
        s=new StringBuilder();
        if(verificarNumero(2,ois)==1){
            for(int i=0;i<lista.length;i++){
                //System.out.println((i+1)+"-. "+lista[i].getName()+permisos(lista[i]));
                s.append((i+1)).append("-. ").append(lista[i].getName()).append(permisos(lista[i])).append("\n");
            }
            
            //System.out.print("\nIngrese numero del archivo o carpeta que desea eliminar: ");
            s.append("\n\nIngrese numero del archivo o carpeta que desea eliminar: ");
            oos.writeObject(s);
            oos.flush();
            
            int t=verificarNumero(lista.length,ois);
            System.out.println(t);
            if(t!=0){
                s=new StringBuilder();
                //System.out.print("\nSeguro quiere eliminar "+lista[t-1]+" \n1-. Si  2-. No: \n");
                s.append("\nSeguro quiere eliminar ").append(lista[t-1].getName()).append(" \n1-. Si  2-. No: \n");
                oos.writeObject(s);
                oos.flush();
                if(verificarNumero(2,ois)==1){
                    if(lista[t-1].isDirectory()){
                        String mensajes="";
                        s=new StringBuilder();
                        s.append(eliminarDirectorio(lista[t-1],mensajes));
                        StringBuilder enviar[]=new StringBuilder[2];
                        enviar[0]=s;
                        enviar[1]=new StringBuilder();
                        enviar[1].append("regresando al menu");
                        
                        oos.writeObject(enviar);
                        
                        oos.close();
                        ois.close();
                    }
                    else{
                        if(lista[t-1].canWrite()){
                            s=new StringBuilder();
                            s.append(eliminarArchivo(lista[t-1]));
                            StringBuilder enviar[]=new StringBuilder[2];
                            
                            enviar[0]=s;
                            enviar[1]=new StringBuilder();
                            enviar[1].append("regresando al menu");

                            oos.writeObject(enviar);

                            oos.close();
                            ois.close();
                        }
                        else{
                            System.out.println("No se puede eliminar por permiso de escritura");
                            s=new StringBuilder();
                            s.append("No se puede eliminar por permiso de escritura");
                            
                            StringBuilder enviar[]=new StringBuilder[2];
                            enviar[0]=s;
                            enviar[1]=new StringBuilder();
                            enviar[1].append("regresando al menu");
                            
                            oos.writeObject(enviar);
                            oos.flush();
                            
                            oos.close();
                            ois.close();
                        }
                    }
                }
                else{
                    s=new StringBuilder();
                    s.append("Opcion invalida");
                    oos.writeObject(s);
                    oos.flush();

                    oos.close();
                    ois.close();
                }
            }
            else{
                s=new StringBuilder();
                s.append("Opcion invalida");
                oos.writeObject(s);
                oos.flush();
                
                oos.close();
                ois.close();
            }
        }
        /////Entrar a directorio
        else {
            //System.out.print("\nQuiere entrar a un directorio \n1-. Si  2-. No: ");
            s=new StringBuilder();
            s.append("\nQuiere entrar a un directorio \n1-. Si  2-. No: ");
            oos.writeObject(s);
            
            ArrayList<File> directorios=new ArrayList();
            
            
            s=new StringBuilder();
            if(verificarNumero(2,ois)==1){
                int j=0;
                for (File lista1 : lista) {
                    if (lista1.isDirectory()) {
                        j++;
                        directorios.add(lista1);
                        //System.out.println(j+"-. " + lista1.getName());
                        s.append(j).append("-.").append(lista1.getName()).append("\n");
                    }
                }
                if(j!=0){
                    System.out.print("\nIngrese numero del directorio que desea ingresar: ");
                    s.append("\nIngrese numero del directorio que desea ingresar: ");
                    oos.writeObject(s);
                    oos.flush();
                    int t=verificarNumero(j,ois);
                    if(t!=0)
                        borradoOp(directorios.get(t-1),socket,oos,ois);
                }else{
                    System.out.println("No hay directorios");
                }
            }
            //////Salir
            else{
                s=new StringBuilder();
                s.append("Regresando al menu");
                oos.writeObject(s);
                oos.flush();
                
                oos.close();
                ois.close();
                
            }
        }
        oos.close();
        ois.close();
        
    }
    
    public static String eliminarArchivo(File archivo) {
        
        if (archivo.delete()) {
            System.out.println("Archivo eliminado: " + archivo.getName());
            return "Archivo eliminado: " + archivo.getName();
        } else {
            System.out.println("No se pudo eliminar el archivo: " + archivo.getName());
            return "No se pudo eliminar el archivo: " + archivo.getName();
        }
    }

    public static String eliminarDirectorio(File directorio, String mensajes) {
        File[] archivos = directorio.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    mensajes+=eliminarDirectorio(archivo, mensajes);
                }
                else {
                    if(archivo.canWrite()){
                        mensajes+=eliminarArchivo(archivo)+"\n";
                    }
                    else{
                        System.out.println("No se puede eliminar por permiso de escritura");
                        return mensajes+"No se puede eliminar por permiso de escritura\n";
                    }
                }
            }
        }
        
        if (directorio.delete()) {
            System.out.println("Directorio eliminado: " + directorio.getName());
            return mensajes+"Directorio eliminado: " + directorio.getName()+"\n";
        } else {
            System.out.println("No se pudo eliminar el directorio: " + directorio.getName());
            return mensajes+"No se pudo eliminar el directorio: " + directorio.getName()+"\n";
        }
    }
    
    static String permisos(File file){
        String execute=file.canExecute() && file.isFile()?"x":"-";
        String read=file.canExecute()  && file.isFile()?"r":"-";
        String write=file.canWrite()  && file.isFile()?"w":"-";
        return file.isDirectory()?"":" - "+read+write+execute;
    }
    
    static int verificarNumero(int limite, DataInputStream ois){
        
        try{
            int opT=ois.readInt();
            if(opT>0 && opT<=limite){
                return opT;
            }
            else{
                throw new Exception();
            }
        }
        catch (Exception e){
        }
        return 0;
    }
        
}
