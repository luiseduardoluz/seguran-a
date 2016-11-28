/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServidorIPs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ServidorIPs implements Runnable {

    public Socket conexao;
    public Hashtable <String, String> ipModel = new Hashtable<String, String>();
    public static ServerSocket servidor;
    public static ObjectInputStream entrada;
    public static ObjectOutputStream saida;

    public ServidorIPs() {
    }

    public ServidorIPs(Socket c) {
        this.conexao = c;
        
    }

    public static void main(String[] args) {

        try {
            //estabelecer porta
            servidor = new ServerSocket(3000);
            
            while (true) {
                //esperar conexao
                Socket conexao = servidor.accept();
    
                Thread s = new Thread(new ServidorIPs(conexao));
                s.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorIPs.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void regIP(String ip,String username){
        this.ipModel.put(ip, username);
    }

    @Override
    public void run() {
        try {

            Socket conexao = this.conexao;
            //saida = new ObjectOutputStream(conexao.getOutputStream());
            //saida.writeObject(chavePublica);
            
            boolean ativo = true;
            
            while (ativo) {
                
                //recebe o username do cliente
                entrada = new ObjectInputStream(conexao.getInputStream());

                String ip = conexao.getInetAddress().toString();
                //String username = (String)entrada.readObject();
                
                this.ipModel.put(ip,"luis");
                
                //byte[] resposta = (byte[])entrada.readObject();
                //System.out.println(decriptografa(resposta, chavePrivada));
                
                //String msg = JOptionPane.showInputDialog("Digite uma msg para o cliente");
                //saida.writeObject(criptografa(msg, pubKeyCliente));
            }
            
            conexao.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorIPs.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServidorIPs.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
