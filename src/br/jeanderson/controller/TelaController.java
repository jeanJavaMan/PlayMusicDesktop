/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.jeanderson.controller;

import br.jeanderson.servidor.Conexao;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;

/**
 *
 * @author jeand
 */
public class TelaController implements Initializable {

    @FXML
    private Label txt_ip;
    @FXML
    private TextArea txt_tocando;
    @FXML
    private Button btnParar;
    private AudioClip audioClip;
    private Conexao conexao;
    private double volume = 1.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            this.txt_ip.setText(InetAddress.getLocalHost().getHostAddress());
            this.btnParar.setOnAction(evento -> btnPararOnAction());
        } catch (UnknownHostException ex) {
            Logger.getLogger(TelaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.conexao = new Conexao(this);
    }

    /**
     * Evento do botão Parar.
     *
     */
    public void btnPararOnAction() {
        if (audioClip != null) {
            if (audioClip.isPlaying()) {
                audioClip.stop();
            }
        }
        txt_tocando.setText("Nada");
    }

    /**
     * Iniciar a Musica.
     *
     * @param nomeDamusica
     */
    public void tocarMusica(String nomeDamusica) {
        if (audioClip != null) {
            if (audioClip.isPlaying()) {
                audioClip.stop();
                System.out.println("chegou no stop");
            }
        }
        AudioClip clip = new AudioClip(conexao.getMusicaPath());
        System.out.println(clip.getSource());
        audioClip = clip;
        audioClip.play();
        txt_tocando.setText(nomeDamusica);
    }

    /**
     * Aumenta o volume da aplicação.
     */
    public void aumentarVolume() {
        if (volume < 1.0) {
            volume += 0.05;
            setVolume(volume);
        }
        System.out.println(volume);
    }

    /**
     * Diminui o volume da aplicação.
     */
    public void diminuirVolume() {
        if (volume > 0.0) {
            volume -= 0.05;
            setVolume(volume);
        }
        System.out.println(volume);
    }

    /**
     * Altera o volume da aplicação.
     *
     * @param volume
     */
    private void setVolume(double volume) {
        try {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            for (Mixer.Info info : mixerInfo) {
                Mixer mixer = AudioSystem.getMixer(info);
                if (mixer.isLineSupported(Port.Info.SPEAKER)) {
                    try (Port port = (Port) mixer.getLine(Port.Info.SPEAKER)) {
                        port.open();
                        if (port.isControlSupported(FloatControl.Type.VOLUME)) {
                            FloatControl vol = (FloatControl) port.getControl(FloatControl.Type.VOLUME);
                            vol.setValue((float) volume);
                        }
                    }
                }
            }
        } catch (LineUnavailableException ex) {
            Logger.getLogger(TelaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método que apaga arquivos temporarios utilizando a classe conexão.
     */
    public void apagarArqTemp(){
        this.conexao.apagarArqTemp();
    }

    public void iniciarServidor() {
        this.conexao.iniciarServidor();
    }
}
