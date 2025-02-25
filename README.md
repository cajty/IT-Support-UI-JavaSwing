# IT Support Ticket System - Java Swing UI

This is the desktop UI component of the IT Support Ticket System, built with Java Swing to provide a user-friendly interface for managing IT support tickets.

## Overview

The IT Support UI is a Java Swing application that connects to the backend REST API to provide a desktop client for employees and IT support staff to manage support tickets. The UI is built with a modern approach to Java Swing development, utilizing MigLayout for responsive layouts.

## Project Structure

```
IT-Support-UI-JavaSwing/
├── .idea/                # IntelliJ IDEA configuration files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org.ably/
│   │   │       ├── di/              # Dependency injection container
│   │   │       ├── guards/          # Navigation guards / permission checks
│   │   │       ├── interceptors/    # HTTP request interceptors
│   │   │       ├── models/          # Data models and DTOs
│   │   │       ├── pages/           # UI screens and dialogs
│   │   │       ├── router/          # Navigation handling
│   │   │       ├── services/        # API service clients
│   │   │       ├── utils/           # Utility classes
│   │   │       └── MainApp.java     # Application entry point
│   │   └── resources/              # Resources (images, icons, etc.)
│   └── test/                       # Unit tests
├── target/                         # Build output
├── .gitignore
├── apiDoc.json                     # API documentation
└── pom.xml                         # Maven build file
```

## Features

- **Modern UI**: Clean, intuitive interface using Swing with MigLayout
- **Role-based Interface**: Different views for employees and IT support staff
- **Ticket Management**: Create, view, and track support tickets
- **Real-time Updates**: Periodic refreshing of ticket data
- **Authentication**: Secure login and session management
- **Dark/Light Mode**: Support for different visual themes
- **Offline Capability**: Basic functionality when network is unavailable
- **Responsive Design**: Adapts to different screen sizes
- **Notifications**: Alert system for ticket updates

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- IT Support Backend service running

## Setup and Installation

### Option 1: Using the pre-built JAR (Recommended)

1. Download the latest release JAR file from the releases page
2. Run the application:
   ```
   java -jar IT-Support-UI.jar
   ```

### Option 2: Building from source

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd IT-Support-UI-JavaSwing
   ```

2. Configure backend API URL in `src/main/resources/application.properties`

3. Build the application:
   ```bash
   mvn clean package
   ```

4. Run the application:
   ```bash
   java -jar target/it-support-ui.jar
   ```

## Usage Guide

### Login

1. Launch the application
2. Enter your username and password
3. Select your role (Employee or IT Support)
4. Click "Login"

### For Employees

- **Creating a Ticket**:
  1. Click "New Ticket" button
  2. Fill in the ticket details (title, description, priority, category)
  3. Submit the ticket

- **Viewing Tickets**:
  1. All your tickets appear in the main dashboard
  2. Click on a ticket to view details and track progress

- **Adding Comments**:
  1. Open a ticket
  2. Use the comment field at the bottom of the ticket detail view
  3. Click "Add Comment"

### For IT Support Staff

- **Managing Tickets**:
  1. View all tickets in the system from the dashboard
  2. Filter tickets by status, priority, or category
  3. Search for specific tickets by ID or keywords

- **Updating Ticket Status**:
  1. Open a ticket
  2. Use the status dropdown to change status
  3. Add a comment explaining the update

- **Viewing Audit Logs**:
  1. Navigate to the Audit section
  2. Filter logs by ticket ID, user, or action type

## Development

### Adding a New Screen

1. Create a new JPanel class in the pages package
2. Register the page in the router
3. Add navigation controls as needed

### Connecting to an API Endpoint

1. Add the endpoint to the appropriate service in the services package
2. Use the service in your page or component
3. Handle API responses with appropriate UI updates

### Building a Custom Component

1. Create a new class in the components package
2. Implement the component using MigLayout for layout
3. Use the component in your pages

## Troubleshooting

Common issues:

- **Connection Errors**: Verify backend URL in application.properties
- **Authentication Issues**: Ensure correct credentials and backend availability
- **UI Display Problems**: Check Java version compatibility
- **Performance Issues**: Review network calls and UI update frequency

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

