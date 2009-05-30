package test.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPasswordField;

import org.junit.Ignore;

/**
 * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6801620
 * and
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6703772
 * 
 * @author kutzi
 */
@Ignore
public class JPasswordFieldTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        JPasswordField field = new JPasswordField();
        frame.add(field);
        
        addFieldListener(field);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static void addFieldListener(JPasswordField field) {
        field.addKeyListener(new KeyAdapter() {

            @Override
            public void keyTyped(KeyEvent e) {
                System.out.println(e.getKeyChar());
            }
        });
//        field.getDocument().addDocumentListener(new DocumentListener() {
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//            }
//
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                Document doc = e.getDocument();
//                try {
//                    System.out.println(doc.getText(0, doc.getLength()));
//                } catch (BadLocationException e1) {
//                    e1.printStackTrace();
//                }
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//            }
//            
//        });
    }
}
