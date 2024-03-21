
package cliente;


import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


public class Cliente {
    
    static File carpeta= new File("Carpeta local");
    static String direccion="127.0.0.1";
    static int puerto=4500;

    public static void main(String[] args) throws ClassNotFoundException {
                
        
        if(!carpeta.exists()){
            if(carpeta.mkdirs()){
                System.out.println("Directorio local creado");
            }
        }
        boolean salida=true;
        while(salida){
            System.out.println("\nMenu principal");
            System.out.println("1-. Listado de directorios");
            System.out.println("2-. Borrado de archivos");
            System.out.println("3-. Enviar archivos al servidor");
            System.out.println("4-. Recibir archivos del servidor");
            System.out.println("5-. Cambio de carpeta base");
            System.out.println("6-. Crear carpetas");
            System.out.println("7-. Salir");
            System.out.print("Digite el numero de la opcion a realizar:");
            int op=verificarNumero(7);
            
            switch (op){
                case 1:{
                    System.out.println("\nListado");
                    System.out.println("1-. Listado de directorio local");
                    System.out.println("2-. Listado de directorio remoto");
                    System.out.print("Digite el numero de la opcion a realizar:");
                    
                    listado(verificarNumero(2));
                    break;
                }
                case 2:{
                    System.out.println("\nBorrado");
                    System.out.println("1-. Borrar de directorio local");
                    System.out.println("2-. Borrar de directorio remoto");
                    System.out.print("Digite el numero de la opcion a realizar:");
                    borrado(verificarNumero(2));
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
                    salida=false;
                    break;
                }
            }
            
        }
    }
    
    static void listado(int op) throws ClassNotFoundException{
        System.out.println("\n");
        if(op==1){
            
           Cliente cliente=new Cliente();
           cliente.listar(carpeta,"");
        }
        else{
           try (Socket socket = new Socket(direccion, puerto)) {
                System.out.println("Servidor conectado");

                int opM = 1;

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeInt(opM);
                oos.flush();
                
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                StringBuilder recibido = (StringBuilder) ois.readObject();
                System.out.println("\n" + recibido);
                
            } catch (IOException | ClassNotFoundException e) {
            }
        }
        
    }
    
    static void borrado(int op) throws ClassNotFoundException{
        if(op==1){
            Cliente cliente=new Cliente();
            cliente.borradoOp(carpeta);
        }
        else{
            System.out.println("\nRemoto\n");
        }
        
    }
    
    public void listar(File directorio, String prefijo) {
        if (directorio.isDirectory()) {
            System.out.println(prefijo + "└───" + directorio.getName());
            File[] archivos = directorio.listFiles();
            if (archivos != null) {
                for (int i = 0; i < archivos.length; i++) {
                    if (i == archivos.length - 1) {
                        listar(archivos[i], prefijo + "    ");
                    } else {
                        listar(archivos[i], prefijo + "    ");
                    }
                }
            }
        } 
        else {
            System.out.println(prefijo + "├───" + directorio.getName()+permisos(directorio));
        }
    }
    
    public void borradoOp(File carpeta) throws ClassNotFoundException{
        File[] lista=carpeta.listFiles();
        
        System.out.println("\nSe encuentra en: "+carpeta.getName()+"\n");
        
        System.out.print("\nQuiere eliminar algun archivo o carpeta de este directorio [s/n]:");
        if(new Scanner(System.in).nextLine().equals("s")){
            for(int i=0;i<lista.length;i++){
                System.out.println((i+1)+"-. "+lista[i].getName()+permisos(lista[i]));
            }
            
            System.out.print("\nIngrese numero del archivo o carpeta que desea eliminar: ");
            int t=verificarNumero(lista.length);
            if(t!=0){
                System.out.print("\nSeguro quiere eliminar "+lista[t-1]+" [s/n]:");
                if(new Scanner(System.in).nextLine().equals("s")){
                    if(lista[t-1].isDirectory()){
                        eliminarDirectorio(lista[t-1]);
                    }
                    else{
                        if(lista[0].canWrite()){
                            eliminarArchivo(lista[t-1]);
                        }
                        else{
                            System.out.println("No se puede eliminar por permiso de escritura");
                        }
                    }
                }
                else{
                    System.out.print("\nRegresando al menu\nPresione un tecla para continuar");
                    new Scanner(System.in).nextLine();
                }
            }
            
        }
        /////Entrar a directorio
        else{
            System.out.print("\nQuiere entrar a un directorio [s/n]:");
            
            ArrayList<File> directorios=new ArrayList();
            
            if(new Scanner(System.in).nextLine().equals("s")){
                int j=0;
                for (File lista1 : lista) {
                    if (lista1.isDirectory()) {
                        j++;
                        directorios.add(lista1);
                        System.out.println(j+"-. " + lista1.getName());
                    }
                }
                
                System.out.print("\nIngrese numero del directorio que desea ingresar: ");
                int t=verificarNumero(j);
                if(t!=0)
                    borradoOp(directorios.get(t-1));
                
            }
            //////Salir
            else{
                System.out.print("\nRegresando al menu\nPresione un tecla para continuar");
                new Scanner(System.in).nextLine();
            }
        }
    }
    
    public static void eliminarArchivo(File archivo) {
        
        if (archivo.delete()) {
            System.out.println("Archivo eliminado: " + archivo.getName());
        } else {
            System.out.println("No se pudo eliminar el archivo: " + archivo.getName());
        }
    }

    public static void eliminarDirectorio(File directorio) {
        File[] archivos = directorio.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isDirectory()) {
                    eliminarDirectorio(archivo);
                }
                else {
                    if(archivo.canWrite()){
                        eliminarArchivo(archivo);
                    }
                    else{
                        System.out.println("No se puede eliminar por permiso de escritura");
                        break;
                    }
                }
            }
        }
        
        if (directorio.delete()) {
            System.out.println("Directorio eliminado: " + directorio.getAbsolutePath());
        } else {
            System.out.println("No se pudo eliminar el directorio: " + directorio.getAbsolutePath());
        }
        new Scanner(System.in).nextLine();
    }

    static String permisos(File file){
        String execute=file.canExecute() && file.isFile()?"x":"-";
        String read=file.canExecute()  && file.isFile()?"r":"-";
        String write=file.canWrite()  && file.isFile()?"w":"-";
        return file.isDirectory()?"":" - "+read+write+execute;
    }
    
    static int verificarNumero(int limite){
        try{
            int opT=new Scanner(System.in).nextInt();
            if(opT>0 && opT<=limite){
                return opT;
            }
            else{
                throw new Exception();
            }
        }
        catch (Exception e){
            System.out.println("\nDigito incorrecto\nPresione cualquier tecla para continuar\n");
            new Scanner(System.in).nextLine();
            
        }
        return 0;
    }
    
}
