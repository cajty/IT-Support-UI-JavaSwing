package org.aBly.pages;



import com.google.inject.Inject;
import org.aBly.models.ticket.TicketRequest;
import org.aBly.models.ticket.TicketResponse;
import org.aBly.services.TicketService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class EmployeeDashboard extends JPanel {
    private final TicketService ticketService;
    private JTable ticketTable;
    private DefaultTableModel tableModel;

    @Inject
    public EmployeeDashboard(TicketService ticketService) {
        this.ticketService = ticketService;
        initializeUI();
        loadTickets();
    }

    private void initializeUI() {
        setLayout(null);

        JLabel titleLabel = new JLabel("Employee Dashboard");
        titleLabel.setBounds(10, 10, 200, 25);
        add(titleLabel);

        JButton createTicketButton = new JButton("Create Ticket");
        createTicketButton.setBounds(10, 40, 150, 25);
        add(createTicketButton);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Status", "Priority"}, 0);
        ticketTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBounds(10, 80, 400, 200);
        add(scrollPane);

        createTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateTicketDialog();
            }
        });
    }

    private void loadTickets() {
        ticketService.searchTickets("", "", "", 0, 10, "createdAt", "desc")
                .enqueue(new Callback<List<TicketResponse>>() {
                    @Override
                    public void onResponse(Call<List<TicketResponse>> call, Response<List<TicketResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tableModel.setRowCount(0);
                            for (TicketResponse ticket : response.body()) {
                                tableModel.addRow(new Object[]{ticket.getId(), ticket.getTitle(), ticket.getStatus(), ticket.getTicketPriority()});
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to load tickets.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TicketResponse>> call, Throwable t) {
                        JOptionPane.showMessageDialog(null, "Error: " + t.getMessage());
                    }
                });
    }

    private void openCreateTicketDialog() {
        JTextField titleField = new JTextField();
        JTextField descriptionField = new JTextField();
        String[] priorities = {"Low", "Medium", "High"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);

        Object[] inputFields = {"Title:", titleField, "Description:", descriptionField, "Priority:", priorityBox};
        int option = JOptionPane.showConfirmDialog(null, inputFields, "Create Ticket", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            TicketRequest request = new TicketRequest(titleField.getText(), descriptionField.getText(), (String) priorityBox.getSelectedItem(), 1L);
            createTicket(request);
        }
    }

    private void createTicket(TicketRequest request) {
        ticketService.createTicket(request).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                if (response.isSuccessful()) {
                    JOptionPane.showMessageDialog(null, "Ticket created successfully.");
                    loadTickets();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create ticket.");
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                JOptionPane.showMessageDialog(null, "Error: " + t.getMessage());
            }
        });
    }
}

