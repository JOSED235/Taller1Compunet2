import java.io.*;
import java.net.*;
import java.util.*;

final class SolicitudHttp implements Runnable {

    // Secuencia CRLF utilizada por HTTP
    private static final String CRLF = "\r\n";

    private Socket socket;

    public SolicitudHttp(Socket socket) throws Exception {
        this.socket = socket;
    }


    @Override
    public void run() {
        try {
            proceseSolicitud();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Procesa la solicitud HTTP
    private void proceseSolicitud() throws Exception {

        // Referencia al stream de salida del socket
        DataOutputStream os =
                new DataOutputStream(socket.getOutputStream());

        // Referencia al stream de entrada del socket
        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));

        // Recoge la línea de solicitud HTTP
        String lineaDeSolicitud = br.readLine();

        // Muestra la línea de solicitud
        System.out.println();
        System.out.println(lineaDeSolicitud);

        // Recoge y muestra las líneas del header
        String lineaDelHeader = null;

        while ((lineaDelHeader = br.readLine()).length() != 0) {
            System.out.println(lineaDelHeader);
        }

        
        // PARTE II
        // Extrae el nombre del archivo
        StringTokenizer partesLinea =
                new StringTokenizer(lineaDeSolicitud);

        partesLinea.nextToken();

        String nombreArchivo =
                partesLinea.nextToken();

        // Buscar dentro de la carpeta public
        nombreArchivo = "." + nombreArchivo;

        // Intenta abrir el archivo
        FileInputStream fis = null;
        boolean existeArchivo = true;
        try {
            fis = new FileInputStream(nombreArchivo);
        } catch (FileNotFoundException e) {
            existeArchivo = false;
        }
        // Construye la respuesta HTTP
        String lineaDeEstado;
        String lineaDeTipoContenido;
        String cuerpoMensaje = null;
        if (existeArchivo) {

            lineaDeEstado = "HTTP/1.0 200 OK" + CRLF;

            lineaDeTipoContenido =
                    "Content-type: "
                            + contentType(nombreArchivo)
                            + CRLF;
        } else {

            lineaDeEstado =
                    "HTTP/1.0 404 Not Found" + CRLF;

            lineaDeTipoContenido =
                    "Content-type: text/html" + CRLF;

            cuerpoMensaje =
                    "<HTML>"
                            + "<HEAD><TITLE>404 Not Found</TITLE></HEAD>"
                            + "<BODY><H1>404 Not Found</H1></BODY>"
                            + "</HTML>";
        }

        // Envía la línea de estado
        os.writeBytes(lineaDeEstado);
        // Envía Content-Type
        os.writeBytes(lineaDeTipoContenido);
        // Línea en blanco
        os.writeBytes(CRLF);

        // Envía el cuerpo
        if (existeArchivo) {

            enviarBytes(fis, os);

            fis.close();

        } else {

            os.writeBytes(cuerpoMensaje);

        }

        // Cierra streams y socket
        os.close();
        br.close();
        socket.close();
    }

    // Envía el archivo solicitado
    private static void enviarBytes(
            FileInputStream fis,
            OutputStream os) throws Exception {

        byte[] buffer = new byte[1024];

        int bytes = 0;

        while ((bytes = fis.read(buffer)) != -1) {

            os.write(buffer, 0, bytes);

        }

    }

    // Determina el tipo MIME
    private static String contentType(String nombreArchivo) {

        if (nombreArchivo.endsWith(".htm")
                || nombreArchivo.endsWith(".html")) {

            return "text/html";

        }

        if (nombreArchivo.endsWith(".gif")) {

            return "image/gif";

        }

        if (nombreArchivo.endsWith(".jpg")
                || nombreArchivo.endsWith(".jpeg")) {

            return "image/jpeg";

        }

        return "application/octet-stream";

    }

}