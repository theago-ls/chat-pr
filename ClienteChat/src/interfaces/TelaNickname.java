package interfaces;

import clientechat.ClienteChat;
import java.io.IOException;
import javax.swing.JOptionPane;


public class TelaNickname extends javax.swing.JFrame {
    static ClienteChat cliente;
    boolean flag = true;
   
    public TelaNickname(ClienteChat cliente) {
        initComponents();
        this.cliente = cliente;       
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nicknameButton = new javax.swing.JButton();
        nicknameTextField = new javax.swing.JTextField();
        nicknameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Nickname");
        setLocation(new java.awt.Point(300, 300));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        nicknameButton.setText("Salvar");
        nicknameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nicknameButtonActionPerformed(evt);
            }
        });

        nicknameLabel.setText("Nickname:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(nicknameLabel)
                .addGap(10, 10, 10)
                .addComponent(nicknameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(nicknameButton, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(nicknameLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nicknameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nicknameButton))))
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nicknameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nicknameButtonActionPerformed
        String nickname = nicknameTextField.getText();
        if(!nickname.isEmpty()){
            if(nickname.length() <=20){
                if(cliente.registrarNickname(nickname)){           
                    new TelaChat(cliente).setVisible(true);
                    this.setVisible(false);            
                    flag = false;
                    JOptionPane.showMessageDialog(null,"Nickname resgistrado no servidor com sucesso!");
                    this.dispose();
                }
                else            
                    JOptionPane.showMessageDialog(null,"Nickname negado pelo servidor!");
            }else
                JOptionPane.showMessageDialog(null,"Nickname maior que 20 caracteres! Digite um nickname com tamanho válido.");
        }else
            JOptionPane.showMessageDialog(null,"Digite um nickname válido!"); 
    }//GEN-LAST:event_nicknameButtonActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        if(flag){
            try {
                cliente.desconectar();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_formWindowClosed

  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton nicknameButton;
    private javax.swing.JLabel nicknameLabel;
    private javax.swing.JTextField nicknameTextField;
    // End of variables declaration//GEN-END:variables
}
