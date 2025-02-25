package org.aBly.pages;

import com.google.inject.Inject;
import net.miginfocom.swing.MigLayout;
import org.aBly.models.category.Category;
import org.aBly.models.ticket.PageTicketResponse;
import org.aBly.models.ticket.TicketRequest;
import org.aBly.models.ticket.TicketResponse;
import org.aBly.services.CategoryService;
import org.aBly.services.TicketService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private JButton searchButton, createTicketButton;
    private Map<String, Long> categoryMap = new HashMap<>();

    @Inject
    public EmployeeDashboard(TicketService ticketService, CategoryService categoryService) {
        this.ticketService = ticketService;
        this.categoryService = categoryService;
        initializeUI();
        loadCategories();
        loadTickets(null, null, null, null);
    }

    private void initializeUI() {
        setLayout(new MigLayout("wrap 2, insets 20", "[grow, center]", "[center]20[center]20[center]20[center]20"));
        setBackground(new Color(30, 30, 30));

        JLabel titleLabel = new JLabel("Employee Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, "span, center");

        searchField = new JTextField(20);
        add(searchField, "growx, span");

        priorityFilter = new JComboBox<>(new String[]{"All", "Low", "Medium", "High"});
        add(priorityFilter, "growx, span");

        statusFilter = new JComboBox<>(new String[]{"All", "NEW", "IN_PROGRESS", "RESOLVED"});
        add(statusFilter, "growx, span");

        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All Categories");
        add(categoryFilter, "growx, span");

        searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> loadTickets(
                searchField.getText(),
                priorityFilter.getSelectedItem().equals("All") ? null : (String) priorityFilter.getSelectedItem(),
                statusFilter.getSelectedItem().equals("All") ? null : (String) statusFilter.getSelectedItem(),
                categoryFilter.getSelectedItem().equals("All Categories") ? null : (String) categoryFilter.getSelectedItem()
        ));
        add(searchButton, "growx, span, center");

        createTicketButton = createStyledButton("Create Ticket");
        createTicketButton.addActionListener(e -> createTicket());
        add(createTicketButton, "growx, span, center");

        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Priority", "Status", "Category"}, 0);
        ticketTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        add(scrollPane, "grow, span");

        JButton refreshButton = createStyledButton("Refresh Tickets");
        refreshButton.addActionListener(e -> loadTickets(null, null, null, null));
        add(refreshButton, "growx, span, center");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        return button;
    }

    private void loadTickets(String query, String priority, String status, String category) {
        ticketService.searchTickets(query, priority, status, 0, 10, "createdAt", "desc")
                .enqueue(new Callback<PageTicketResponse>() {
                    @Override
                    public void onResponse(Call<PageTicketResponse> call, Response<PageTicketResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tableModel.setRowCount(0);
                            List<TicketResponse> tickets = response.body().getContent();
                            for (TicketResponse ticket : tickets) {
                                tableModel.addRow(new Object[]{
                                        ticket.getId(),
                                        ticket.getTitle(),
                                        ticket.getTicketPriority(),
                                        ticket.getStatus(),
                                        ticket.getCategoryName()
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PageTicketResponse> call, Throwable t) {
                        JOptionPane.showMessageDialog(null, "Failed to load tickets: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    for (Category category : response.body()) {
                        categoryMap.put(category.getName(), category.getId());
                        categoryFilter.addItem(category.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                JOptionPane.showMessageDialog(null, "Failed to load categories: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void createTicket() {
        JDialog ticketDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create Ticket", true);
        ticketDialog.setSize(400, 400);
        ticketDialog.setLayout(new MigLayout("wrap 2", "[grow,fill]", "[][][][][grow][][]"));
        ticketDialog.getContentPane().setBackground(new Color(30, 30, 30));

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(Color.WHITE);
        JTextField titleField = new JTextField(20);

        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(Color.WHITE);
        JTextArea descArea = new JTextArea(4, 20);
        JScrollPane descScroll = new JScrollPane(descArea);

        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setForeground(Color.WHITE);
        JComboBox<String> priorityBox = new JComboBox<>(new String[]{"Low", "Medium", "High"});

        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setForeground(Color.WHITE);
        JComboBox<String> categoryBox = new JComboBox<>();
        categoryBox.addItem("Select Category");
        for (String category : categoryMap.keySet()) {
            categoryBox.addItem(category);
        }

        JButton submitButton = createStyledButton("Submit");
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String desc = descArea.getText().trim();
            String priority = (String) priorityBox.getSelectedItem();
            String category = (String) categoryBox.getSelectedItem();

            if (title.isEmpty() || desc.isEmpty() || category.equals("Select Category")) {
                JOptionPane.showMessageDialog(ticketDialog, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TicketRequest request = new TicketRequest(title, desc, priority, categoryMap.get(category));
            ticketService.createTicket(request).enqueue(new Callback<TicketResponse>() {
                @Override
                public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                    if (response.isSuccessful()) {
                        JOptionPane.showMessageDialog(ticketDialog, "Ticket Created Successfully!");
                        loadTickets(null, null, null, null);
                        ticketDialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(ticketDialog, "Failed to create ticket", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                @Override
                public void onFailure(Call<TicketResponse> call, Throwable t) {
                    JOptionPane.showMessageDialog(ticketDialog, "Error: " + t.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        ticketDialog.add(titleLabel);
        ticketDialog.add(titleField);
        ticketDialog.add(descLabel);
        ticketDialog.add(descScroll, "span, grow");
        ticketDialog.add(priorityLabel);
        ticketDialog.add(priorityBox);
        ticketDialog.add(categoryLabel);
        ticketDialog.add(categoryBox);
        ticketDialog.add(submitButton, "span, grow");

        ticketDialog.setVisible(true);
    }
}
