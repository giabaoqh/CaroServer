package view;

import dao.UserDAO;
import model.User;
import controller.Room;
import controller.Server;
import controller.ServerThread;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AdminNew extends javax.swing.JFrame implements Runnable {
    private final UserDAO userDAO;

    // Components
    private JTextArea threadRoomListView;
    private JTextArea messageView;
    private JTextField noticeTextField;
    private JTextField userIdTextField;
    private JComboBox<String> reasonComboBox;
    
    private JButton viewThreadButton;
    private JButton viewRoomListButton;
    private JButton publishMessageButton;
    private JButton banButton;
    private JButton warnButton;
    private JButton cancelBanButton;

    public AdminNew() {
        initDashboardUI(); // Gọi hàm khởi tạo giao diện mới
        
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        
        userDAO = new UserDAO();
    }

    private void initDashboardUI() {
        setTitle("Hệ Thống Quản Trị Game Caro - Dashboard");
        setSize(1000, 720); // Mở rộng chiều ngang một chút cho thoáng

        // 1. Panel Nền chính (Chứa ảnh)
        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20)); // Căn lề bao quanh
        setContentPane(mainPanel);

        // --- HEADER ---
        JLabel lblHeader = new JLabel("ADMIN CONTROL PANEL", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblHeader.setForeground(Color.WHITE);
        mainPanel.add(lblHeader, BorderLayout.NORTH);

        // --- BODY (Dùng GridBagLayout để chia 3 phần dọc) ---
        JPanel bodyPanel = new JPanel(new GridBagLayout());
        bodyPanel.setOpaque(false); // Để lộ hình nền
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 15, 0); // Khoảng cách giữa các khối
        gbc.weightx = 1.0;

        // KHỐI 1: GIÁM SÁT (Monitor Section)
        JPanel monitorPanel = createSectionPanel("Giám sát hệ thống");
        monitorPanel.setLayout(new BorderLayout(10, 10));
        
        // Thanh công cụ nút bấm (Top của Monitor)
        JPanel monitorTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        monitorTools.setOpaque(false);
        viewThreadButton = createStyledButton("Danh sách Luồng", new Color(52, 152, 219));
        viewRoomListButton = createStyledButton("Danh sách Phòng", new Color(52, 152, 219));
        monitorTools.add(viewThreadButton);
        monitorTools.add(viewRoomListButton);
        
        // Bảng hiển thị (Trắng chữ đen)
        threadRoomListView = createWhiteTextArea();
        JScrollPane scrollList = new JScrollPane(threadRoomListView);
        scrollList.setPreferredSize(new Dimension(800, 180));
        
        monitorPanel.add(monitorTools, BorderLayout.NORTH);
        monitorPanel.add(scrollList, BorderLayout.CENTER);

        // Add Khối 1 vào Body
        gbc.gridy = 0; gbc.weighty = 0.45;
        bodyPanel.add(monitorPanel, gbc);

        // KHỐI 2: GIAO TIẾP (Communication Section)
        JPanel chatPanel = createSectionPanel("Nhật ký & Thông báo");
        chatPanel.setLayout(new BorderLayout(10, 10));

        // Bảng Log Chat (Trắng chữ đen)
        messageView = createWhiteTextArea();
        JScrollPane scrollChat = new JScrollPane(messageView);
        scrollChat.setPreferredSize(new Dimension(800, 140));

        // Thanh nhập thông báo (Bottom của Chat)
        JPanel noticeInputPanel = new JPanel(new BorderLayout(10, 0));
        noticeInputPanel.setOpaque(false);
        noticeTextField = createStyledTextField();
        publishMessageButton = createStyledButton("Gửi thông báo", new Color(46, 204, 113));
        
        noticeInputPanel.add(noticeTextField, BorderLayout.CENTER);
        noticeInputPanel.add(publishMessageButton, BorderLayout.EAST);

        chatPanel.add(scrollChat, BorderLayout.CENTER);
        chatPanel.add(noticeInputPanel, BorderLayout.SOUTH);

        // Add Khối 2 vào Body
        gbc.gridy = 1; gbc.weighty = 0.45;
        bodyPanel.add(chatPanel, gbc);

        // KHỐI 3: TÁC VỤ USER (User Actions)
        JPanel actionPanel = createSectionPanel("Quản lý người dùng (Xử lý vi phạm)");
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        
        userIdTextField = createStyledTextField();
        userIdTextField.setPreferredSize(new Dimension(80, 35));
        userIdTextField.setToolTipText("Nhập ID User tại đây");
        
        reasonComboBox = new JComboBox<>(new String[] { 
            "-- Chọn lý do vi phạm --", 
            "Ngôn ngữ thô tục / Xúc phạm", 
            "Spam / Quấy rối", 
            "Gian lận / Hack", 
            "Mục đích xấu khác" 
        });
        reasonComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reasonComboBox.setBackground(Color.WHITE);
        reasonComboBox.setPreferredSize(new Dimension(250, 35));

        cancelBanButton = createStyledButton("Gỡ Ban", new Color(149, 165, 166));
        warnButton = createStyledButton("Cảnh cáo", new Color(230, 126, 34));
        banButton = createStyledButton("BAN NGAY", new Color(231, 76, 60));

        // Label nhỏ
        JLabel lblId = new JLabel("ID User:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblId.setForeground(Color.WHITE); // Chữ trắng trên nền tối panel

        actionPanel.add(lblId);
        actionPanel.add(userIdTextField);
        actionPanel.add(reasonComboBox);
        actionPanel.add(Box.createHorizontalStrut(20)); // Khoảng cách
        actionPanel.add(cancelBanButton);
        actionPanel.add(warnButton);
        actionPanel.add(banButton);

        // Add Khối 3 vào Body
        gbc.gridy = 2; gbc.weighty = 0.1;
        gbc.insets = new Insets(0, 0, 0, 0); // Reset margin đáy
        bodyPanel.add(actionPanel, gbc);

        mainPanel.add(bodyPanel, BorderLayout.CENTER);

        setupEvents();
    }

    // --- HELPER METHODS CHO GIAO DIỆN ---

    // Tạo các khối panel trong suốt có tiêu đề
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false); // Trong suốt để thấy nền
        TitledBorder border = BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200), 1), title);
        border.setTitleColor(Color.WHITE);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        // Thêm padding bên trong border
        panel.setBorder(new CompoundBorder(border, new EmptyBorder(10, 10, 10, 10)));
        return panel;
    }

    // Tạo TextArea màu TRẮNG, chữ ĐEN
    private JTextArea createWhiteTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBackground(Color.WHITE);
        area.setForeground(Color.BLACK);
        area.setCaretColor(Color.BLACK);
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        // Padding text bên trong
        area.setBorder(new EmptyBorder(5, 8, 5, 8));
        return area;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 180, 180)), 
            new EmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // --- LOGIC XỬ LÝ SỰ KIỆN (GIỮ NGUYÊN) ---
    private void setupEvents() {
        viewThreadButton.addActionListener(this::viewThreadButtonActionPerformed);
        viewRoomListButton.addActionListener(this::viewRoomListButtonActionPerformed);
        publishMessageButton.addActionListener(this::publishMessageButtonActionPerformed);
        banButton.addActionListener(this::banButtonActionPerformed);
        warnButton.addActionListener(this::warnButtonActionPerformed);
        cancelBanButton.addActionListener(this::cancelBanButtonActionPerformed);
        
        noticeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                noticeTextFieldKeyPressed(evt);
            }
        });
    }

    private void viewThreadButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                 
        StringBuilder res = new StringBuilder();
        String room;
        int i = 1;
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            if (serverThread.getRoom() == null) room = null;
            else room = "" + serverThread.getRoom().getId();
            
            if (serverThread.getUser() != null) {
                res.append(i).append(". Client: ").append(serverThread.getClientNumber())
                   .append(" | ID: ").append(serverThread.getUser().getID())
                   .append(" | Phòng: ").append(room).append("\n");
            } else {
                res.append(i).append(". Client: ").append(serverThread.getClientNumber())
                   .append(" | ID: --- | Phòng: ").append(room).append("\n");
            }
            i++;
        }
        threadRoomListView.setText(res.toString());
    }                                                

    private void viewRoomListButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        StringBuilder res = new StringBuilder();
        int i = 1;
        for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
            Room room1 = serverThread.getRoom();
            if (room1 != null) {
                String listUser = (room1.getNumberOfUser() == 1) 
                        ? "" + room1.getUser1().getUser().getID()
                        : room1.getUser1().getUser().getID() + ", " + room1.getUser2().getUser().getID();
                
                res.append(i).append(". Phòng: ").append(room1.getId())
                   .append(" | Số người: ").append(room1.getNumberOfUser())
                   .append(" | Players: [").append(listUser).append("]\n");
                i++;
            }
        }
        threadRoomListView.setText(res.toString());
    }                                                  

    private void publishMessageButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        sendMessage();
    }                                                    

    private void noticeTextFieldKeyPressed(java.awt.event.KeyEvent evt) {                                           
        if (evt.getKeyCode() == 10) {
            sendMessage();
        }
    }                                          

    private void banButtonActionPerformed(java.awt.event.ActionEvent evt) {                                          
        try {
            if (isInvalidForm()) return;
            int userId = Integer.parseInt(userIdTextField.getText());
            User user = new User();
            user.setID(userId);
            userDAO.updateBannedStatus(user, true);
            ServerThread serverThread = Server.serverThreadBus.getServerThreadByUserID(userId);
            if (serverThread != null) {
                serverThread.write("banned-notice," + reasonComboBox.getSelectedItem());
                if (serverThread.getRoom() != null) {
                    Room room = serverThread.getRoom();
                    ServerThread competitorThread = room.getCompetitor(serverThread.getClientNumber());
                    room.setUsersToNotPlaying();
                    if (competitorThread != null) {
                        room.decreaseNumberOfGame();
                        competitorThread.write("left-room,");
                        competitorThread.setRoom(null);
                    }
                    serverThread.setRoom(null);
                }
                serverThread.setUser(null);
            }
            Server.admin.addMessage("User ID " + userId + " đã bị BAN");
            Server.serverThreadBus.boardCast(-1, "chat-server," + "User ID " + userId + " đã bị BAN");
            JOptionPane.showMessageDialog(this, "Đã BAN user " + userId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra: " + e.getMessage());
        }
    }                                         

    private void cancelBanButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                
        try {
            if (userIdTextField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ID của User");
                return;
            }
            int userId = Integer.parseInt(userIdTextField.getText());
            User user = new User();
            user.setID(userId);
            userDAO.updateBannedStatus(user, false);
            userIdTextField.setText("");
            JOptionPane.showMessageDialog(this, "Đã huỷ BAN user " + userId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
        }
    }                                               

    private void warnButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           
        try {
            if (isInvalidForm()) return;
            int userId = Integer.parseInt(userIdTextField.getText());
            Server.serverThreadBus.sendMessageToUserID(userId, "warning-notice," + reasonComboBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Đã cảnh cáo user " + userId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra");
        }
    }                                          

    private void sendMessage() {
        String message = noticeTextField.getText();
        if (message.isEmpty()) return;
        String temp = messageView.getText();
        temp += "Server: " + message + "\n";
        messageView.setText(temp);
        messageView.setCaretPosition(messageView.getDocument().getLength());
        Server.serverThreadBus.boardCast(-1, "chat-server,Thông báo từ máy chủ: " + message);
        noticeTextField.setText("");
    }

    public void addMessage(String message) {
        String tmp = messageView.getText();
        tmp = tmp + message + "\n";
        messageView.setText(tmp);
        messageView.setCaretPosition(messageView.getDocument().getLength());
    }

    private boolean isInvalidForm() {
        if (userIdTextField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập ID của User");
            return true;
        }
        if (reasonComboBox.getSelectedIndex() < 1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lý do");
            return true;
        }
        return false;
    }

    @Override
    public void run() {
         new AdminNew().setVisible(true);
    }

    // --- CLASS NỀN (TỰ XỬ LÝ MÀU NẾU KHÔNG CÓ ẢNH) ---
    class BackgroundPanel extends JPanel {
        private BufferedImage backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                java.net.URL imgURL = getClass().getResource(imagePath);
                if (imgURL != null) {
                    backgroundImage = ImageIO.read(imgURL);
                } else {
                    System.err.println("Không tìm thấy ảnh nền. Sử dụng màu mặc định.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // Gradient Đẹp nếu quên chép ảnh
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 30), 0, getHeight(), new Color(10, 10, 10));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}