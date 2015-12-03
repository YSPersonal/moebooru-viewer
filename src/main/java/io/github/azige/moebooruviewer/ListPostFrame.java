/*
 * Created 2015-11-27 23:32:57
 */
package io.github.azige.moebooruviewer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author Azige
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ListPostFrame extends javax.swing.JFrame{

    private static final Logger logger = LoggerFactory.getLogger(ListPostFrame.class);
    private static final int SAMPLE_WIDTH = 150;
    private static final int SAMPLE_HEIGHT = 100;

    @Autowired
    private SiteConfig siteConfig;
    @Autowired
    private MoebooruViewer moebooruViewer;
    @Autowired
    private NetIO netIO;
    @Autowired
    private ExecutorService executor;
    @Autowired
    private MoebooruAPI mapi;
    @Autowired
    private ShowPostFrame postFrame;

    private int pageCount = 1;
    private Set<Post> posts = new HashSet<>();
    private final JLabel loadMoreLabel;
    private String[] tags;

    /**
     * Creates new form MainFrame
     */
    public ListPostFrame(){
        initComponents();

        setLocationRelativeTo(null);
        postsPanel.setLayout(new FlowLayout(){

            @Override
            public void layoutContainer(Container target){

                int columnCount = target.getSize().width / (SAMPLE_WIDTH + getHgap());
                int rowCount = posts.size() / columnCount + 1;
                target.setPreferredSize(new Dimension(1,
                    rowCount * (SAMPLE_HEIGHT + getVgap()) + getVgap()
                ));
                super.layoutContainer(target);
            }

        });
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);

        loadMoreLabel = new JLabel("加载更多");
        loadMoreLabel.setForeground(Color.WHITE);
        loadMoreLabel.setHorizontalAlignment(JLabel.CENTER);
        loadMoreLabel.setPreferredSize(new Dimension(SAMPLE_WIDTH, SAMPLE_HEIGHT));
        loadMoreLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loadMoreLabel.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e){
                if (loadMoreLabel.isEnabled()){
                    loadImages();
                }
            }

        });

        postsPanel.add(loadMoreLabel);
    }

    @PostConstruct
    private void init(){
        setTitle(siteConfig.getName() + " Viewer");
    }

    public String[] getTags(){
        return tags;
    }

    public void setTags(String[] tags){
        this.tags = Objects.requireNonNull(tags);
        if (tags.length > 0){
            setTitle(siteConfig.getName() + " Viewer[" + Stream.of(tags).reduce((a, b) -> a + " " + b).get() + "]");
        }
    }

    @Override
    protected void processWindowEvent(WindowEvent e){
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING || e.getID() == WindowEvent.WINDOW_CLOSED){
            postFrame.dispose();
        }
    }

    public void loadImages(){
        loadMoreLabel.setText("加载中……");
        loadMoreLabel.setEnabled(false);
        executor.execute(() -> {
            List<Post> postList = netIO.retry(() -> mapi.listPosts(pageCount, tags));
            SwingUtilities.invokeLater(() -> {
                pageCount++;
                postsPanel.remove(loadMoreLabel);
                for (Post post : postList){
                    if (posts.contains(post)){
                        continue;
                    }
                    posts.add(post);
                    JLabel label = new JLabel("加载中……");
                    label.setForeground(Color.WHITE);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    label.setPreferredSize(new Dimension(SAMPLE_WIDTH, SAMPLE_HEIGHT));
                    label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    class LoadImageTask implements Runnable{

                        boolean force = false;

                        @Override
                        public void run(){
                            Image image = netIO.loadPreview(post, force);
                            SwingUtilities.invokeLater(() -> {
                                if (image != null){
                                    label.setText("");
                                    Dimension size = label.getPreferredSize();
                                    label.setIcon(new ImageIcon(MoebooruViewer.resizeImage(image, size.getWidth(), size.getHeight())));
                                }else{
                                    label.setText("加载失败！");
                                }
                            });
                        }

                    }

                    label.addMouseListener(new MouseAdapter(){

                        @Override
                        public void mouseClicked(MouseEvent e){
                            if (SwingUtilities.isLeftMouseButton(e)){
                                postFrame.setVisible(true);
                                postFrame.showPost(post);
                            }else if (SwingUtilities.isRightMouseButton(e)){
                                label.setIcon(null);
                                label.setText("加载中……");
                                LoadImageTask task = new LoadImageTask();
                                task.force = true;
                                executor.execute(task);
                            }
                        }

                    });
                    postsPanel.add(label);
                    LoadImageTask task = new LoadImageTask();
                    executor.execute(task);
                }
                postsPanel.add(loadMoreLabel);
                loadMoreLabel.setText("加载更多");
                loadMoreLabel.setEnabled(true);
            });
        });
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

        scrollPane = new javax.swing.JScrollPane();
        postsPanel = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        searchTagMenuItem = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        switchKonachanMenuItem = new javax.swing.JMenuItem();
        switchYandereMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Moebooru Viewer");
        setMinimumSize(new java.awt.Dimension(850, 650));
        setPreferredSize(new java.awt.Dimension(850, 650));

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        postsPanel.setBackground(new java.awt.Color(34, 34, 34));
        postsPanel.setPreferredSize(new java.awt.Dimension(800, 10000));
        postsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        scrollPane.setViewportView(postsPanel);

        jMenu1.setText("功能");

        searchTagMenuItem.setText("搜索tag");
        searchTagMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTagMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(searchTagMenuItem);

        jMenu2.setText("切换站点");

        switchKonachanMenuItem.setText("Konachan.com");
        switchKonachanMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchKonachanMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(switchKonachanMenuItem);

        switchYandereMenuItem.setText("yande.re");
        switchYandereMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchYandereMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(switchYandereMenuItem);

        jMenu1.add(jMenu2);

        exitMenuItem.setText("退出");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(exitMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 579, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchTagMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTagMenuItemActionPerformed
        String tags = JOptionPane.showInputDialog(this, "输入要搜索的tag，用空格分隔");
        if (tags != null){
            moebooruViewer.listPosts(tags.split(" "));
        }
    }//GEN-LAST:event_searchTagMenuItemActionPerformed

    private void switchKonachanMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchKonachanMenuItemActionPerformed
        moebooruViewer.switchSite(SiteConfig.KONACHAN);
    }//GEN-LAST:event_switchKonachanMenuItemActionPerformed

    private void switchYandereMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchYandereMenuItemActionPerformed
        moebooruViewer.switchSite(SiteConfig.YANDERE);
    }//GEN-LAST:event_switchYandereMenuItemActionPerformed

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        moebooruViewer.exit();
    }//GEN-LAST:event_exitMenuItemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel postsPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JMenuItem searchTagMenuItem;
    private javax.swing.JMenuItem switchKonachanMenuItem;
    private javax.swing.JMenuItem switchYandereMenuItem;
    // End of variables declaration//GEN-END:variables
}
