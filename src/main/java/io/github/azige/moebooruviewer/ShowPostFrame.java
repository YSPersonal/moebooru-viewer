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

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import io.github.azige.moebooruviewer.ShowPostPanel.LoadingEvent;
import io.github.azige.moebooruviewer.ShowPostPanel.LoadingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ShowPostFrame extends javax.swing.JFrame{

    private static final Logger logger = LoggerFactory.getLogger(ShowPostFrame.class);

    @Autowired
    private ApplicationContext context;

    private Map<Long, ShowPostPanel> postPanelMap = new HashMap<>();

    /**
     * Creates new form PostFrame
     */
    public ShowPostFrame(){
        initComponents();
        tabbedPane.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e){
                ShowPostPanel postPanel = (ShowPostPanel)tabbedPane.getSelectedComponent();
                if (postPanel != null){
                    postPanel.updateImage();
                }
            }
        });
        tabbedPane.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e){
                if (SwingUtilities.isRightMouseButton(e)){
                    ShowPostPanel postPanel = (ShowPostPanel)tabbedPane.getSelectedComponent();
                    tabbedPane.remove(postPanel);
                    postPanelMap.values().remove(postPanel);
                    if (postPanelMap.isEmpty()){
                        setVisible(false);
                    }
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

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setTitle("Post");
        setMinimumSize(new java.awt.Dimension(800, 650));
        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(800, 650));
        getContentPane().add(tabbedPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void showPost(Post post){
        ShowPostPanel postPanel = postPanelMap.get(post.getId());
        if (postPanel == null){
            postPanel = context.getBean(ShowPostPanel.class);
            postPanel.addLoadingListener(new LoadingListener(){

                @Override
                public void loading(LoadingEvent event){
                    int index = tabbedPane.indexOfComponent(event.getSource());
                    tabbedPane.setTitleAt(index, post.getId() + "*");
                    tabbedPane.setIconAt(index, null);
                }

                @Override
                public void done(LoadingEvent event){
                    int index = tabbedPane.indexOfComponent(event.getSource());
                    if (tabbedPane.getIconAt(index) == null && event.getSource().getImage() != null){
                        tabbedPane.setIconAt(index, new ImageIcon(MoebooruViewer.resizeImage(event.getSource().getImage(), 32, 32)));
                    }
                    tabbedPane.setTitleAt(index, String.valueOf(post.getId()));
                }

            });
            tabbedPane.addTab(null, null, postPanel, post.getTags());
            postPanelMap.put(post.getId(), postPanel);
            postPanel.showPost(post);
        }
        tabbedPane.setSelectedComponent(postPanel);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
