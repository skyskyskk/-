// src/view/BackgroundPanel.java
package view;

import java.awt.*;
import javax.swing.*;

/**
 * 自定义背景面板类，用于显示背景图片
 */
public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    /**
     * 构造方法
     * @param layout 面板布局
     */
    public BackgroundPanel(LayoutManager layout) {
        super(layout);
        this.setOpaque(true); // 设置面板不透明，这样才能绘制背景图片
    }

    /**
     * 设置背景图片
     * @param imagePath 图片路径
     */
    public void setBackgroundImage(String imagePath) {
        // 尝试使用不同的方式加载图片
        try {
            // 方式1：直接使用文件路径
            java.io.File file = new java.io.File(imagePath);
            if (file.exists()) {
                System.out.println("找到图片文件: " + file.getAbsolutePath());
                backgroundImage = new ImageIcon(file.getAbsolutePath()).getImage();
            } else {
                System.out.println("图片文件不存在: " + file.getAbsolutePath());
                // 方式2：尝试从类路径加载
                java.net.URL url = getClass().getClassLoader().getResource(imagePath);
                if (url != null) {
                    System.out.println("从类路径找到图片: " + url);
                    backgroundImage = new ImageIcon(url).getImage();
                } else {
                    System.out.println("无法从类路径找到图片: " + imagePath);
                    // 方式3：尝试从当前目录加载
                    java.io.File currentDirFile = new java.io.File("./" + imagePath);
                    if (currentDirFile.exists()) {
                        System.out.println("从当前目录找到图片: " + currentDirFile.getAbsolutePath());
                        backgroundImage = new ImageIcon(currentDirFile.getAbsolutePath()).getImage();
                    } else {
                        System.out.println("所有图片加载方式都失败了: " + imagePath);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("加载图片时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        repaint(); // 重新绘制面板以显示新图片
    }

    /**
     * 重写绘制方法，绘制背景图片
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // 绘制背景图片，填充整个面板
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}