/*
 * Created 2015-11-28 21:56:59
 */
package io.github.azige.moebooruviewer;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.github.azige.moebooruviewer.ShowPostPanel.LoadingEvent;
import io.github.azige.moebooruviewer.ShowPostPanel.LoadingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
public class ShowPostFrame extends javax.swing.JFrame{

    private static final Logger logger = LoggerFactory.getLogger(ShowPostFrame.class);

    @Autowired
    private ApplicationContext context;

    private Map<Integer, ShowPostPanel> postPanelMap = new HashMap<>();

    /**
     * Creates new form PostFrame
     */
    public ShowPostFrame(){
        initComponents();
        tabbedPane.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e){
                ShowPostPanel postPanel = (ShowPostPanel)tabbedPane.getSelectedComponent();
                if (postPanel != null && postPanel.isNeedResizeImage()){
                    postPanel.updateImage();
                }
            }
        });
        tabbedPane.addMouseListener(new MouseAdapter(){

            @Override
            public void mousePressed(MouseEvent e){
                popupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e){
                popupMenu(e);
            }

            private void popupMenu(MouseEvent e){
                if (e.isPopupTrigger()){
                    postPanePopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        });
        rootPane.addComponentListener(new ComponentAdapter(){

            @Override
            public void componentResized(ComponentEvent e){
                ShowPostPanel postPanel = (ShowPostPanel)tabbedPane.getSelectedComponent();
                if (postPanel != null){
                    postPanel.updateImage();
                }
            }

        });
    }

    @Override
    protected void processWindowEvent(WindowEvent e){
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING){
            tabbedPane.removeAll();
            postPanelMap.clear();
        }
    }

    @PreDestroy
    @Override
    public void dispose(){
        super.dispose();
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        postPanePopupMenu = new javax.swing.JPopupMenu();
        closeMenuItem = new javax.swing.JMenuItem();
        tabbedPane = new javax.swing.JTabbedPane();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("io/github/azige/moebooruviewer/Messages"); // NOI18N
        closeMenuItem.setText(bundle.getString("close_this_page")); // NOI18N
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        postPanePopupMenu.add(closeMenuItem);

        setTitle(Localization.getString("post")); // NOI18N
        setMinimumSize(new java.awt.Dimension(800, 650));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(800, 650));
        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeMenuItemActionPerformed
        ShowPostPanel postPanel = (ShowPostPanel)tabbedPane.getSelectedComponent();
        tabbedPane.remove(postPanel);
        postPanelMap.values().remove(postPanel);
        if (postPanelMap.isEmpty()){
            setVisible(false);
        }
    }//GEN-LAST:event_closeMenuItemActionPerformed

    public void showPost(Post post){
        setVisible(true);
        ShowPostPanel postPanel = postPanelMap.get(post.getId());
        if (postPanel == null){
            postPanel = context.getBean(ShowPostPanel.class);
            postPanel.addLoadingListener(new LoadingListener(){

                @Override
                public void loading(LoadingEvent event){
                    int index = tabbedPane.indexOfComponent(event.getSource());
                    tabbedPane.setTitleAt(index, post.getId() + "*"); //NOI18N
                    tabbedPane.setIconAt(index, null);
                }

                @Override
                public void done(LoadingEvent event){
                    int index = tabbedPane.indexOfComponent(event.getSource());
                    if (tabbedPane.getIconAt(index) == null && event.getSource().getImage() != null){
                        tabbedPane.setIconAt(index, new ImageIcon(Utils.resizeImage(event.getSource().getImage(), 32, 32)));
                    }
                    tabbedPane.setTitleAt(index, String.valueOf(post.getId()));
                }

            });
            tabbedPane.addTab(null, null, postPanel);
            postPanelMap.put(post.getId(), postPanel);
            postPanel.showPost(post);
        }
        tabbedPane.setSelectedComponent(postPanel);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JPopupMenu postPanePopupMenu;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
