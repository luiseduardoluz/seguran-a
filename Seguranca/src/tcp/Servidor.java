/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.swing.JOptionPane;

public class Servidor implements Runnable {

    public Socket conexao;
    public static ServerSocket servidor;
    public static ObjectInputStream entrada;
    public static ObjectOutputStream saida;

    public static final String ALGORITHM = "RSA";

    /**
     * Local da chave privada no sistema de arquivos.
     */
    public static final String PATH_CHAVE_PRIVADA = "private.key";

    /**
     * Local da chave pública no sistema de arquivos.
     */
    public static final String PATH_CHAVE_PUBLICA = "public.key";

    /**
     * Gera a chave que contém um par de chave Privada e Pública usando 1025
     * bytes. Armazena o conjunto de chaves nos arquivos private.key e
     * public.key
     */
    public static void geraChave() {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(1024);
            final KeyPair key = keyGen.generateKeyPair();

            File chavePrivadaFile = new File(PATH_CHAVE_PRIVADA);
            File chavePublicaFile = new File(PATH_CHAVE_PUBLICA);

            // Cria os arquivos para armazenar a chave Privada e a chave Publica
            if (chavePrivadaFile.getParentFile() != null) {
                chavePrivadaFile.getParentFile().mkdirs();
            }

            chavePrivadaFile.createNewFile();

            if (chavePublicaFile.getParentFile() != null) {
                chavePublicaFile.getParentFile().mkdirs();
            }

            chavePublicaFile.createNewFile();

            // Salva a Chave Pública no arquivo
            ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePublicaFile));
            chavePublicaOS.writeObject(key.getPublic());
            chavePublicaOS.close();

            // Salva a Chave Privada no arquivo
            ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
                    new FileOutputStream(chavePrivadaFile));
            chavePrivadaOS.writeObject(key.getPrivate());
            chavePrivadaOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Verifica se o par de chaves Pública e Privada já foram geradas.
     */
    public static boolean verificaSeExisteChavesNoSO() {

        File chavePrivada = new File(PATH_CHAVE_PRIVADA);
        File chavePublica = new File(PATH_CHAVE_PUBLICA);

        if (chavePrivada.exists() && chavePublica.exists()) {
            return true;
        }

        return false;
    }

    /**
     * Criptografa o texto puro usando chave pública.
     */
    public static byte[] criptografa(String texto, PublicKey chave) {
        byte[] cipherText = null;

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Criptografa o texto puro usando a chave Púlica
            cipher.init(Cipher.ENCRYPT_MODE, chave);
            cipherText = cipher.doFinal(texto.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    /**
     * Decriptografa o texto puro usando chave privada.
     */
    public static String decriptografa(byte[] texto, PrivateKey chave) {
        byte[] dectyptedText = null;

        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // Decriptografa o texto puro usando a chave Privada
            cipher.init(Cipher.DECRYPT_MODE, chave);
            dectyptedText = cipher.doFinal(texto);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new String(dectyptedText);
    }

    public Servidor() {
    }

    public Servidor(Socket c) {
        this.conexao = c;
    }

    public static void main(String[] args) {

        try {
            //estabelecer porta
            servidor = new ServerSocket(3000);
            while (true) {
                //esperar conexao
                Socket conexao = servidor.accept();

                Thread s = new Thread(new Servidor(conexao));
                s.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void run() {
        try {

            // Verifica se já existe um par de chaves, caso contrário gera-se as chaves..
            if (!verificaSeExisteChavesNoSO()) {
                // Método responsável por gerar um par de chaves usando o algoritmo RSA e
                // armazena as chaves nos seus respectivos arquivos.
                geraChave();
            }

            final String msgOriginal = "Exemplo de mensagem";
            ObjectInputStream inputStream = null;

            // Criptografa a Mensagem usando a Chave Pública
            inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));
            final PublicKey chavePublica = (PublicKey) inputStream.readObject();
            final byte[] textoCriptografado = criptografa(msgOriginal, chavePublica);

            // Decriptografa a Mensagem usando a Chave Pirvada
            inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
            final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
            final String textoPuro = decriptografa(textoCriptografado, chavePrivada);
            
            Socket conexao = this.conexao;

            //receber chave pública do cliente
            entrada = new ObjectInputStream(conexao.getInputStream());
            PublicKey pubKeyCliente = (PublicKey)entrada.readObject();

            saida = new ObjectOutputStream(conexao.getOutputStream());
            saida.writeObject(chavePublica);
            
            boolean ativo = true;
            
            while (ativo) {
                byte[] resposta = (byte[])entrada.readObject();
                System.out.println(decriptografa(resposta, chavePrivada));
                
                String msg = JOptionPane.showInputDialog("Digite uma msg para o cliente");
                saida.writeObject(criptografa(msg, pubKeyCliente));
            }
            
            conexao.close();
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
