package clientechat;

import interfaces.TelaChat;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;


public class ClienteChat{
    final static int VER = 0, TIPO = 3, FROM = 23, TO = 43, TAMANHO = 48, DADOS = 20048, PORT = 45000, ALL_LEN = 20049;
    final String ACK = "ACK", REG = "REG", UPD = "UPD", MEN = "MEN", ERA = "ERA", LIV = "LIV", FILL_CHAR = " ";
    public DefaultListModel list;
    String ver = "1", tipo, from, to, tam, dados, mensagem = new String(), menBuffer = new String(), IP;
    public String nickname;    
    Socket cliente;
    Thread ouvMen;
    boolean flagPrim = true, flagEnv = true, flagLiv = true, ackFlag = false, regFlag = false, eraFlag = false;
    public boolean on = true;
    Lock lockSai, lockEnt;
    PrintStream saida1;
    InputStream entrada;
    File arq = null;    
    OutputStream arquivoNicks = null;
    TelaChat tela;
        
        
    public boolean conectar(String IP) throws IOException {      
        try {
            this.IP = IP;
            cliente = new Socket(IP,PORT);
            saida1 = new PrintStream(cliente.getOutputStream());            
            JOptionPane.showMessageDialog(null,"Conexão estabelecida com sucesso!");
            lockSai = new ReentrantLock();
            lockEnt = new ReentrantLock(); 
            list = new DefaultListModel();
            ouvir();
            return true;
        }catch(IOException error){           
            JOptionPane.showMessageDialog(null,"Erro ao estabelecer conexão com o servidor!","Erro",JOptionPane.ERROR_MESSAGE);
            return false;
        }       
    } 
    
    public boolean erase(){             
        String saidaBuffer = ver + ERA + nickname;        
        while(saidaBuffer.length() < 44){           
                saidaBuffer += FILL_CHAR;
        }            
        saidaBuffer += "0    ";
        saida1.print(saidaBuffer);
        while(!eraFlag){   
            lockEnt.lock();
            try {                
            } finally {
                lockEnt.unlock();
            }
        }        
        return ackFlag;        
    }
           
    public void desconectar() throws IOException{
        ouvMen.interrupt();
        on = false;
        entrada.close();
        saida1.close();        
        cliente.close();        
    }
    
    private void ouvir() throws IOException{       
        ouvMen = new Thread(() -> {
            while(on){
                try {
                    entrada = cliente.getInputStream();
                    while(on){
                        byte[] buffer = new byte[ALL_LEN];
                        entrada.read(buffer,0,ALL_LEN);
                        menBuffer =  new String(buffer, "UTF-8");
                        tratarMensagem(menBuffer);
                    }
                } catch (IOException ex) {
                    if(on){
                        boolean teste = true;
                        int tent = 3;
                        while(tent != 0){
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex1) {
                                Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex1);
                            }
                            if(!reconectar()){
                                if(teste){
                                    JOptionPane.showMessageDialog(null,"Conexão com o servidor foi perdida. Tentando reestabelecer conexão!","Erro",JOptionPane.ERROR_MESSAGE);
                                    teste = false;
                                }
                                tent--;
                                if(tent == 0){
                                    on = false;
                                    tela.reconectar();
                                }
                            }else{
                                JOptionPane.showMessageDialog(null, "Conexão com o servidor reestabelecida!", "Aviso", JOptionPane.PLAIN_MESSAGE);
                                tent = 0;
                                regFlag = false;
                                try {
                                    ouvir();
                                } catch (IOException ex1) {
                                    Logger.getLogger(ClienteChat.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                                if(!registrarNickname(nickname)) {
                                    eraFlag = false;
                                    tela.reconectar();
                                } else{
                                    eraFlag = false;
                                }                                
                            }
                        }
                    }
                    break;
                }
            }
        });
        ouvMen.start();        
    }  
    
    private boolean reconectar(){
        try{
            cliente = new Socket(IP,PORT);
            saida1 = new PrintStream(cliente.getOutputStream());            
            return true;               
        }catch(IOException ex){            
            return false;
        }        
    }
       
    public boolean registrarNickname(String nickname1){
        nickname = nickname1;        
        int nicktam = nickname.length();
        if(nicktam < 20){
            for(int i = 0; i < 20 - nicktam; i++)
                nickname += FILL_CHAR;
        }               
        String buffer = "1REG"+nickname+"                    "+"0    ";
        saida1.print(buffer);
        while(!regFlag){   
            try {                
                lockEnt.lock();                
            } finally {
                lockEnt.unlock();
            }
        }          
        return ackFlag;
    }
    
    private void tratarMensagem(String mensagem1){       
        ver = mensagem1.substring(VER, 1);
        tipo = mensagem1.substring(VER+1,TIPO+1);
        from = mensagem1.substring(TIPO+1, FROM+1);
        to = mensagem1.substring(FROM+1,TO+1);
        tam = mensagem1.substring(TO+1,TAMANHO+1);
        dados = mensagem1.substring(TAMANHO+1); 
         
        switch(tipo){
            case LIV:                
                respLiv();
                break;
            case ACK:                
                menACK(dados);
                break;  
            case UPD:               
                updMen(dados);                 
                break; 
            case MEN:                  
                tela.recvMen(from, tam, dados);                
                break;
        }
        mensagem = mensagem1;
    } 
    
    public void setTela(TelaChat tela){
        this.tela = tela;
    }
   
    private void menACK(String dadosAck){         
        try {            
            lockEnt.lock();
            dadosAck = dadosAck.replace(FILL_CHAR, "");
            ackFlag = !dadosAck.equals("FALSE");
            regFlag = true;
            eraFlag = true;
        } finally {
            lockEnt.unlock();            
        }
    }
    
    private void respLiv(){
        String saidaBuffer = ver + ACK + nickname;
        for(int i = 0; i < 20; i++)
            saidaBuffer += FILL_CHAR;
        saidaBuffer += "4    TRUE";
        for(int i = 0; i < 19996; i++)
            saidaBuffer += FILL_CHAR;
        lockSai.lock();
        try{            
            saida1.print(saidaBuffer);
        }finally{
            lockSai.unlock();
        }
    }   
    
    private void updMen(String lista){ 
        list.clear();             
        String nickname1 = nickname.trim();
        String[] quebra = lista.split(";"); 
        for(String nick : quebra){
            nick = nick.trim();
            if(!nick.equals(nickname1)){                
                list.addElement(nick);
            }
        }        
    }
    
    
    public boolean envMen(String to, String mensagem){
        try{                   
            String taman = Integer.toUnsignedString(mensagem.length());
            int totam =  to.length(), tamantam = taman.length();
            if(totam < 20){
                for(int i = 0; i < (20 - totam); i++)
                    to += FILL_CHAR;
            }
            if(tamantam < 5){
                for(int i = 0; i < (5 - tamantam); i++)
                    taman += FILL_CHAR;
            }                        
            String saidaBuffer = ver + MEN + nickname + to + taman + mensagem;            
            lockSai.lock();
            try{
                saida1.print(saidaBuffer);
            }finally{
                lockSai.unlock();
            }
        }catch(Exception ex){
            return false;
        }
        return true;
    }
}
