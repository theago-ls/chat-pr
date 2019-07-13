package interfaces;

import clientechat.ClienteChat;
import java.io.IOException;
import javax.swing.JOptionPane;


public class TelaConectar extends javax.swing.JFrame {
    ClienteChat cliente;
    
    public TelaConectar() {
        initComponents();
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ipLabel = new javax.swing.JLabel();
        ipTextField = new javax.swing.JTextField();
        conectarButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Iniciar conexão");
        setLocation(new java.awt.Point(300, 300));
        setResizable(false);

        ipLabel.setText("IP do servidor:");

        ipTextField.setText("0.0.0.0");

        conectarButton.setText("Conectar");
        conectarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                conectarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ipLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(conectarButton)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipLabel)
                    .addComponent(ipTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(conectarButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void conectarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_conectarButtonActionPerformed
        String IP = ipTextField.getText();
        if(IP.length() <= 15){
            if(!IP.isEmpty()){ 
                cliente = new ClienteChat(); 
                try {
                    if(cliente.conectar(IP)){
                        this.setVisible(false);
                        new TelaNickname(cliente).setVisible(true);
                        this.dispose();
                    }                
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"Erro ao tentar conectar ao servidor!","Erro",JOptionPane.ERROR_MESSAGE);  
                }            
            }else{
                JOptionPane.showMessageDialog(null,"Nenhum IP informado!","Erro",JOptionPane.ERROR_MESSAGE);  
            }
        }else
            JOptionPane.showMessageDialog(null,"IP inválido! Digite um IP válido.","Erro",JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_conectarButtonActionPerformed

    
    public static void main(String args[]) {       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaConectar().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton conectarButton;
    private javax.swing.JLabel ipLabel;
    private javax.swing.JTextField ipTextField;
    // End of variables declaration//GEN-END:variables
}
