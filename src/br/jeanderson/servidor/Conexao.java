/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jeanderson.servidor;

import br.jeanderson.controller.TelaController;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author jeand
 */
public class Conexao {

    private final TelaController controller;
    private File arqMusica;
    private final File pasta;
    private ServerSocket servidor;
    private final int TOCAR_MUSICA = 1;
    private final int PARAR_MUSICA = 2;
    private final int AUMENTAR_VOLUME = 3;
    private final int DIMINUIR_VOLUME = 4;
    private final int BUFFER_LIMIT = 4096;

    public Conexao(TelaController controller) {
        this.controller = controller;
        pasta = new File("/Users/" + System.getProperty("user.name") + "/Documents/PlayMusic/");
        if (!pasta.exists()) {
            pasta.mkdir();
        }
        
    }

    /*PUBLIC METHOD*/
    /**
     * Inicializa o Servidor.
     */
    public void iniciarServidor() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    servidor = new ServerSocket(8485);
                    while (!servidor.isClosed()) {
                        Socket cliente = servidor.accept();
                        ObjectInputStream recebe = new ObjectInputStream(cliente.getInputStream());
                        int motivo = recebe.readInt();
                        switch (motivo) {
                            case TOCAR_MUSICA:
                                tocarMusica(recebe, cliente);
                                break;
                            case PARAR_MUSICA:
                                controller.btnPararOnAction();
                                recebe.close();
                                cliente.close();
                                break;
                            case AUMENTAR_VOLUME:
                                controller.aumentarVolume();
                                recebe.close();
                                cliente.close();
                                break;
                            case DIMINUIR_VOLUME:
                                controller.diminuirVolume();
                                recebe.close();
                                cliente.close();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
                }
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Retorna a pasta da Musica.
     *
     * @return
     */
    public String getMusicaPath() {
        return this.arqMusica.toURI().toString();
    }

    /*PRIVATE METHOD*/
    /**
     * Torna a musica em arquivo fisico e executa.
     *
     * @param recebe
     * @param cliente
     */
    private void tocarMusica(ObjectInputStream recebe, Socket cliente) {
        try {
            String nomeDamusica = recebe.readUTF();
            arqMusica = new File(pasta+"/"+nomeDamusica);
            FileOutputStream gravar = new FileOutputStream(arqMusica);
            byte[] buf = new byte[BUFFER_LIMIT];
            while (true) {
                int len = recebe.read(buf);
                if (len == -1) {
                    break;
                }
                gravar.write(buf, 0, len);
            }
            gravar.flush();
            gravar.close();
            recebe.close();
            cliente.close();
            this.controller.tocarMusica(nomeDamusica);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Conexao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Apaga todas as musicas temporarias.
     */
    public void apagarArqTemp(){
        for (File musicTemp1 : pasta.listFiles()) {
            musicTemp1.delete();
        }
    }

}
