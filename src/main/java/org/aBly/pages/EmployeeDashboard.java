package org.aBly.pages;

import com.google.inject.Inject;
import net.miginfocom.swing.MigLayout;
import org.aBly.models.category.Category;
import org.aBly.models.ticket.PageTicketResponse;
import org.aBly.models.ticket.TicketRequest;
import org.aBly.models.ticket.TicketResponse;
import org.aBly.router.RouteManager;
import org.aBly.services.CategoryService;
import org.aBly.services.TicketService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDashboard extends JPanel {
    private final TicketService ticketService;
    private final CategoryService categoryService;

    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> priorityFilter, statusFilter, categoryFilter;
    private JButton searchButton, createTicketButton, refreshButton, logoutButton;
    private Map<String, Long> categoryMap = new HashMap<>();

    // Color scheme
    private final Color BG_COLOR = new Color(25, 25, 25);
    private final Color PANEL_COLOR = new Color(35, 35, 35);
    private final Color ACCENT_COLOR = new Color(0, 173, 239); // Blue accent
    private final Color SECONDARY_COLOR = new Color(70, 70, 70);
    private final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private final Color TEXT_COLOR = new Color(240, 240, 240);
    private final Color HOVER_COLOR = new Color(0, 140, 200);
    private final Color LOGOUT_COLOR = new Color(231, 76, 60); // Red for logout button

    @Inject
    public EmployeeDashboard(TicketService ticketService, CategoryService categoryService) {
        this.ticketService = ticketService;
        this.categoryService = categoryService;
        initializeUI();
        loadCategories();
        loadTickets(null, null, null, null);
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("IT Support Dashboard");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create buttons panel for right side of header
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(BG_COLOR);

        // Create ticket button
        createTicketButton = createStyledButton("New Ticket", ACCENT_COLOR);
        createTicketButton.addActionListener(e -> createTicket());
        buttonsPanel.add(createTicketButton);

        // Logout button
        logoutButton = createStyledButton("Logout", LOGOUT_COLOR);
        logoutButton.addActionListener(e -> logout());
        buttonsPanel.add(logoutButton);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(BG_COLOR);

        // Filter panel
        JPanel filterPanel = new JPanel(new MigLayout("fillx, insets 15", "[grow][grow][grow][grow][100]", "[][]"));
        filterPanel.setBackground(PANEL_COLOR);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel filterLabel = new JLabel("Filters");
        filterLabel.setForeground(ACCENT_COLOR);
        filterLabel.setFont(new Font("Arial", Font.BOLD, 16));
        filterPanel.add(filterLabel, "span, gapbottom 10");

        searchField = createStyledTextField("Search tickets...");
        filterPanel.add(searchField, "span 4, growx");

        searchButton = createStyledButton("Search", ACCENT_COLOR);
        searchButton.addActionListener(e -> applyFilters());
        filterPanel.add(searchButton, "wrap");

        filterPanel.add(new JLabel("Priority:"), "gaptop 10");
        filterPanel.add(new JLabel("Status:"), "gaptop 10");
        filterPanel.add(new JLabel("Category:"), "gaptop 10");
        filterPanel.add(new JLabel(""), "span 2, gaptop 10, wrap");

        priorityFilter = createStyledComboBox(new String[]{"All", "Low", "Medium", "High"});
        statusFilter = createStyledComboBox(new String[]{"All", "NEW", "IN_PROGRESS", "RESOLVED"});
        categoryFilter = createStyledComboBox(new String[]{"All Categories"});

        filterPanel.add(priorityFilter, "growx");
        filterPanel.add(statusFilter, "growx");
        filterPanel.add(categoryFilter, "growx");

        refreshButton = createStyledButton("Reset", SECONDARY_COLOR);
        refreshButton.addActionListener(e -> resetFilters());
        filterPanel.add(refreshButton, "span 2, growx");

        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(PANEL_COLOR);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel tableTitle = new JLabel("My Tickets");
        tableTitle.setForeground(ACCENT_COLOR);
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        // Create and style the table
        String[] columns = {"ID", "Title", "Priority", "Status", "Category", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only make the Actions column editable
            }
        };

        ticketTable = new JTable(tableModel);
        styleTable(ticketTable);

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(PANEL_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BG_COLOR);
        statusPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel statusLabel = new JLabel("Ready to assist with your IT issues");
        statusLabel.setForeground(TEXT_COLOR);
        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    // Implementation of logout functionality
    private void logout() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            RouteManager.navigate("auth");
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setBackground(PANEL_COLOR);
        table.setForeground(TEXT_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(Color.WHITE);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFocusable(false);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Style header
        JTableHeader header = table.getTableHeader();
        header.setBackground(SECONDARY_COLOR);
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Style cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new CustomCellRenderer());
        }

        // Add action buttons to table
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Priority
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Category
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Actions
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setBackground(SECONDARY_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));

        // Add placeholder text functionality if needed

        return field;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(SECONDARY_COLOR);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        comboBox.setFocusable(false);

        // Custom renderer for consistent styling
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT_COLOR : SECONDARY_COLOR);
                setForeground(isSelected ? Color.WHITE : TEXT_COLOR);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });

        return comboBox;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void loadTickets(String query, String priority, String status, String category) {
        // Show loading indicator
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        ticketService.searchTickets(query, priority, status, 0, 10, "createdAt", "desc")
                .enqueue(new Callback<PageTicketResponse>() {
                    @Override
                    public void onResponse(Call<PageTicketResponse> call, Response<PageTicketResponse> response) {
                        setCursor(Cursor.getDefaultCursor());

                        if (response.isSuccessful() && response.body() != null) {
                            tableModel.setRowCount(0);
                            List<TicketResponse> tickets = response.body().getContent();
                            for (TicketResponse ticket : tickets) {
                                tableModel.addRow(new Object[]{
                                        ticket.getId(),
                                        ticket.getTitle(),
                                        ticket.getTicketPriority(),
                                        ticket.getStatus(),
                                        ticket.getCategoryName(),
                                        "View"
                                });
                            }

                            // Show empty state message if no tickets
                            if (tickets.isEmpty()) {
                                tableModel.addRow(new Object[]{"No tickets found", "", "", "", "", ""});
                            }
                        } else {
                            showErrorDialog("Failed to load tickets: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PageTicketResponse> call, Throwable t) {
                        setCursor(Cursor.getDefaultCursor());
                        showErrorDialog("Network error: " + t.getMessage());
                    }
                });
    }

    private void loadCategories() {
        categoryService.getAllCategories().enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryFilter.removeAllItems();
                    categoryFilter.addItem("All Categories");
                    categoryMap.clear();

                    for (Category category : response.body()) {
                        categoryMap.put(category.getName(), category.getId());
                        categoryFilter.addItem(category.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                showErrorDialog("Failed to load categories: " + t.getMessage());
            }
        });
    }

    private void createTicket() {
        JDialog ticketDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Support Ticket", true);
        ticketDialog.setSize(500, 500);
        ticketDialog.setLayout(new BorderLayout());
        ticketDialog.getContentPane().setBackground(PANEL_COLOR);

        // Dialog header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ACCENT_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel dialogTitle = new JLabel("Create New Support Ticket");
        dialogTitle.setForeground(Color.WHITE);
        dialogTitle.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(dialogTitle, BorderLayout.CENTER);

        ticketDialog.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new MigLayout("fillx, insets 20", "[right][grow]", "[]15[]15[]15[]"));
        formPanel.setBackground(PANEL_COLOR);

        // Title
        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(titleLabel, "cell 0 0");

        JTextField titleField = createStyledTextField("Enter a brief title");
        formPanel.add(titleField, "cell 1 0, growx");

        // Category
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(TEXT_COLOR);
        categoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(categoryLabel, "cell 0 1");

        JComboBox<String> categoryBox = createStyledComboBox(categoryMap.keySet().toArray(new String[0]));
        categoryBox.insertItemAt("Select Category", 0);
        categoryBox.setSelectedIndex(0);
        formPanel.add(categoryBox, "cell 1 1, growx");

        // Priority
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setForeground(TEXT_COLOR);
        priorityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(priorityLabel, "cell 0 2");

        JComboBox<String> priorityBox = createStyledComboBox(new String[]{"Low", "Medium", "High"});
        formPanel.add(priorityBox, "cell 1 2, growx");

        // Description
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(descLabel, "cell 0 3, aligny top");

        JTextArea descArea = new JTextArea(6, 20);
        descArea.setBackground(SECONDARY_COLOR);
        descArea.setForeground(TEXT_COLOR);
        descArea.setCaretColor(TEXT_COLOR);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SECONDARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        descArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
        descScroll.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        formPanel.add(descScroll, "cell 1 3, growx, growy");

        ticketDialog.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(PANEL_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JButton cancelButton = createStyledButton("Cancel", SECONDARY_COLOR);
        cancelButton.addActionListener(e -> ticketDialog.dispose());

        JButton submitButton = createStyledButton("Submit Ticket", SUCCESS_COLOR);
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();
            String category = (String) categoryBox.getSelectedItem();

            if (title.isEmpty() || desc.isEmpty() || category.equals("Select Category")) {
                showErrorDialog("All fields are required!");
                return;
            }

            // Show loading state
            submitButton.setEnabled(false);
            submitButton.setText("Submitting...");

            TicketRequest request = new TicketRequest(title, desc, priority, categoryMap.get(category));
            ticketService.createTicket(request).enqueue(new Callback<TicketResponse>() {
                @Override
                public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                    if (response.isSuccessful()) {
                        ticketDialog.dispose();
                        showSuccessDialog("Ticket created successfully!");
                        loadTickets(null, null, null, null);
                    } else {
                        showErrorDialog("Failed to create ticket: " + response.message());
                        submitButton.setEnabled(true);
                        submitButton.setText("Submit Ticket");
                    }
                }

                @Override
                public void onFailure(Call<TicketResponse> call, Throwable t) {
                    showErrorDialog("Network error: " + t.getMessage());
                    submitButton.setEnabled(true);
                    submitButton.setText("Submit Ticket");
                }
            });
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(submitButton);

        ticketDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Center dialog on screen
        ticketDialog.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        ticketDialog.setVisible(true);
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Success",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void applyFilters() {
        loadTickets(
            searchField.getText(),
            priorityFilter.getSelectedItem().equals("All") ? null : (String) priorityFilter.getSelectedItem(),
            statusFilter.getSelectedItem().equals("All") ? null : (String) statusFilter.getSelectedItem(),
            categoryFilter.getSelectedItem().equals("All Categories") ? null : (String) categoryFilter.getSelectedItem()
        );
    }

    private void resetFilters() {
        searchField.setText("");
        priorityFilter.setSelectedIndex(0);
        statusFilter.setSelectedIndex(0);
        categoryFilter.setSelectedIndex(0);
        loadTickets(null, null, null, null);
    }

    // Custom renderer for table cells
    class CustomCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                c.setBackground(ACCENT_COLOR);
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(row % 2 == 0 ? PANEL_COLOR : new Color(40, 40, 40));
                c.setForeground(TEXT_COLOR);
            }

            // Format priority and status cells
            if (column == 2 && value != null) { // Priority column
                String priority = value.toString();
                if (priority.equalsIgnoreCase("High")) {
                    setForeground(new Color(231, 76, 60)); // Red for high priority
                } else if (priority.equalsIgnoreCase("Medium")) {
                    setForeground(new Color(241, 196, 15)); // Yellow for medium priority
                } else {
                    setForeground(new Color(46, 204, 113)); // Green for low priority
                }
                setHorizontalAlignment(JLabel.CENTER);
            } else if (column == 3 && value != null) { // Status column
                String status = value.toString();
                if (status.equalsIgnoreCase("NEW")) {
                    setForeground(new Color(52, 152, 219)); // Blue for new
                } else if (status.equalsIgnoreCase("IN_PROGRESS")) {
                    setForeground(new Color(230, 126, 34)); // Orange for in progress
                } else if (status.equalsIgnoreCase("RESOLVED")) {
                    setForeground(new Color(46, 204, 113)); // Green for resolved
                }
                setHorizontalAlignment(JLabel.CENTER);
            } else {
                setHorizontalAlignment(column == 0 ? JLabel.CENTER : JLabel.LEFT);
            }

            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            return c;
        }
    }

    // Button renderer for action column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(ACCENT_COLOR);
            setFont(new Font("Arial", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            return this;
        }
    }


    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setForeground(ACCENT_COLOR);
            button.setFont(new Font("Arial", Font.BOLD, 12));

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {

                viewTicketDetails((Long) ticketTable.getValueAt(ticketTable.getSelectedRow(), 0));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void viewTicketDetails(Long ticketId) {
        // Implement ticket details view dialog
        JOptionPane.showMessageDialog(this, "Viewing details for ticket #" + ticketId, "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
    }


    class ModernScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = ACCENT_COLOR;
            this.trackColor = SECONDARY_COLOR;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Create rounded rectangle
            int arc = 8;
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, arc, arc);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            g.setColor(trackColor);
            g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
        }
    }
}