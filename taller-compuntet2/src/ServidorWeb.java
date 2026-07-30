import java.io.*;
import java.net.*;

public final class ServidorWeb {

    public static void main(String[] args) throws Exception {

        // Establece el número de puerto.
        int puerto = 6789;

        // Estableciendo el socket de escucha.
        ServerSocket socketEscucha = new ServerSocket(puerto);
        System.out.println("Servidor Web Multihilo iniciado");
        System.out.println("Puerto: " + puerto);
        System.out.println("Esperando conexiones...");

        // Procesando las solicitudes HTTP en un ciclo infinito.
        while (true) {

            // Escuchando las solicitudes de conexión TCP.
            Socket socketConexion = socketEscucha.accept();

            // Construye un objeto para procesar el mensaje de solicitud HTTP.
            SolicitudHttp solicitud = new SolicitudHttp(socketConexion);

            // Crea un nuevo hilo para procesar la solicitud.
            Thread hilo = new Thread(solicitud);

            // Inicia el hilo.
            hilo.start();
        }
    }
}