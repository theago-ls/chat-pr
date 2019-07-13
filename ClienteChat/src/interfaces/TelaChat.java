package interfaces;

import clientechat.ClienteChat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TelaChat extends javax.swing.JFrame {
    ClienteChat cliente;  
    FileWriter fw;
    BufferedReader entrada;
    static PrintWriter saida;
    boolean primFlag = true;
    String filename;  
    
    
    public TelaChat(ClienteChat cliente) {          
        this.cliente = cliente;         
        initComponents();    
        userList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    if(userList.isSelectionEmpty() || (userList.getSelectedIndex() == -1)){
                        ListModel modelo = userList.getModel();
                        if(modelo.getSize() > 0){
                            for(int i = 0; i < modelo.getSize(); i++){
                                try {
                                    String nome = (String) modelo.getElementAt(i);
                                    filename = nome + ".txt";
                                    File arq = new File(filename);
                                    if (!arq.exists()) {
                                        arq.createNewFile();
                                    }
                                    entrada = new BufferedReader(new FileReader(filename));
                                    fw = new FileWriter(filename, true);
                                    saida = new PrintWriter(fw);                                    
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "Erro ao abrir arquivo de conversa!", "Erro", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    }else{
                        try {                        
                            filename = userList.getSelectedValue() + ".txt";
                            File arq = new File(filename);
                            if (!arq.exists()) {
                                arq.createNewFile();
                            }
                            entrada = new BufferedReader(new FileReader(filename));
                            fw = new FileWriter(filename, true);
                            saida = new PrintWriter(fw);
                            setarConversa();                            
                        }catch(IOException ex){
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Erro ao abrir arquivo de conversa!", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }                       
                }  
            }   
        }); 
        cliente.setTela(this);
    }
    
    public void reconectar(){
        this.setVisible(false);
        JOptionPane.showMessageDialog(null,"A conexão com o servidor foi perdida!","Erro",JOptionPane.ERROR_MESSAGE);   
        try{
            cliente.desconectar();
        }catch(IOException ex){
            ex.printStackTrace();
        }
        new TelaConectar().setVisible(true);
        this.dispose();
    }
      
    public void recvMen(String from, String tam, String mensagem){
        from = from.trim();  
        String finalMen = from + ": "+mensagem.substring(0, Integer.parseUnsignedInt(tam.trim()));
        filename = from + ".txt";
        try{
            fw = new FileWriter(filename, true);
            saida = new PrintWriter(fw);
        }catch(IOException ex){
            ex.printStackTrace();
        }          
        saida.println(finalMen);         
        saida.flush();
        if(from.equals(userList.getSelectedValue())){
            try{
                setarConversa();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }  
    
    private void setarConversa() throws IOException{
        String texto = "", linha;
        entrada = new BufferedReader(new FileReader(filename));        
        while((linha = entrada.readLine()) != null){
            texto += "\n" + linha;
        }                                
        conversaTextArea.setText(texto);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList<>();
        userlistLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        conversaTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        enviarButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        atencaoButton = new javax.swing.JButton();
        limparButton = new javax.swing.JButton();
        sairButton = new javax.swing.JButton();
        chatScrollPane = new javax.swing.JScrollPane();
        mensagemTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Zap");
        setLocation(new java.awt.Point(300, 300));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        userList.setModel(cliente.list);
        userList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(userList);

        userlistLabel.setText("Usuários online:");

        conversaTextArea.setEditable(false);
        conversaTextArea.setColumns(20);
        conversaTextArea.setLineWrap(true);
        conversaTextArea.setRows(5);
        jScrollPane2.setViewportView(conversaTextArea);

        jLabel1.setText("Conversa");

        enviarButton.setText("Enviar");
        enviarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enviarButtonActionPerformed(evt);
            }
        });

        atencaoButton.setText("Pedir atenção");
        atencaoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atencaoButtonActionPerformed(evt);
            }
        });

        limparButton.setText("Limpar");
        limparButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limparButtonActionPerformed(evt);
            }
        });

        sairButton.setText("Sair");
        sairButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sairButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(atencaoButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(limparButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(sairButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(atencaoButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(limparButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sairButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chatScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mensagemTextArea.setColumns(20);
        mensagemTextArea.setLineWrap(true);
        mensagemTextArea.setRows(5);
        chatScrollPane.setViewportView(mensagemTextArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(262, 262, 262)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(userlistLabel)
                .addGap(42, 42, 42))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(chatScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(enviarButton))
                    .addComponent(jScrollPane2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userlistLabel)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(enviarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chatScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sairButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sairButtonActionPerformed
        sair(true);
    }//GEN-LAST:event_sairButtonActionPerformed
    
    public void setSelectedIndex(int index){
        userList.setSelectedIndex(index);
    }
    
    private void sair(boolean sair){
        if(sair){
            if(cliente.erase()){
                try {                
                    if(cliente.on)
                        cliente.desconectar();
                    this.setVisible(false);
                    this.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(null,"Servidor não permitiu saida!","Erro",JOptionPane.ERROR_MESSAGE);
            }
        }else{
            if(cliente.erase()){
                try {                
                    if(cliente.on)
                        cliente.desconectar();                   
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }else{
                JOptionPane.showMessageDialog(null,"Servidor não permitiu saida mas ninguem liga pro que o servidor quer!","Erro",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void enviarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enviarButtonActionPerformed
        envMensagem(true);
    }//GEN-LAST:event_enviarButtonActionPerformed

    private void envMensagem(boolean test){
        String mensagem;
        if(test){
            mensagem = mensagemTextArea.getText();        
            if(!userList.isSelectionEmpty()){
                if(cliente.envMen(userList.getSelectedValue(), mensagem)){                    
                    saida.println("Eu: " + mensagem);                
                    mensagemTextArea.setText("");
                    saida.flush();
                    try {
                        setarConversa();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else
                    JOptionPane.showMessageDialog(null,"Erro ao enviar mensagem!","Erro",JOptionPane.ERROR_MESSAGE); 
            }
        }else{
            mensagem = "Preciso de atenção!";        
            if(!userList.isSelectionEmpty()){
                if(cliente.envMen(userList.getSelectedValue(), mensagem)){
                    saida.println();
                    saida.println("Eu: " + mensagem);                
                    mensagemTextArea.setText("");
                    saida.flush();                    
                    try {
                        setarConversa();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }else
                    JOptionPane.showMessageDialog(null,"Erro ao enviar mensagem!","Erro",JOptionPane.ERROR_MESSAGE); 
            }
        }
    }
    
    private void limparButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limparButtonActionPerformed
        if(!userList.isSelectionEmpty()){
            try {                
                saida = new PrintWriter(filename);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TelaChat.class.getName()).log(Level.SEVERE, null, ex);
            }
            mensagemTextArea.setText("");           
            try {            
                setarConversa();
            } catch (IOException ex) {
                Logger.getLogger(TelaChat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            mensagemTextArea.setText("");  
        }
    }//GEN-LAST:event_limparButtonActionPerformed
           
    private void atencaoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atencaoButtonActionPerformed
        envMensagem(false);
    }//GEN-LAST:event_atencaoButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        sair(false);
    }//GEN-LAST:event_formWindowClosing
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton atencaoButton;
    private javax.swing.JScrollPane chatScrollPane;
    private javax.swing.JTextArea conversaTextArea;
    private javax.swing.JButton enviarButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton limparButton;
    private javax.swing.JTextArea mensagemTextArea;
    private javax.swing.JButton sairButton;
    private javax.swing.JList<String> userList;
    private javax.swing.JLabel userlistLabel;
    // End of variables declaration//GEN-END:variables
}
