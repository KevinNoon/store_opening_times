package com.optimised.views.clients;

import com.optimised.model.Client;
import com.optimised.services.ClientService;
import com.optimised.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;


@RolesAllowed({"ADMIN","USER"})
@PageTitle("Clients")
@Route(value = "clients", layout = MainLayout.class)
public class ClientsView extends VerticalLayout {

    Grid<Client> grid = new Grid<>(Client.class);
    ClientsForm form;

    ClientService ClientService;

    public ClientsView(ClientService ClientService){
        this.ClientService = ClientService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(GetToolbar(),getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("Client-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editClient(event.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void configureForm() {
        form = new ClientsForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveClient);
        form.addDeleteListener(this::deleteClient);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout GetToolbar() {
        Button addClientButton = new Button("Add Client");
        addClientButton.addClickListener(click -> addClient());
        var toolbar = new HorizontalLayout(addClientButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(ClientService.findAll());
    }

    private void closeEditor() {
        form.setClient(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addClient() {
        grid.asSingleSelect().clear();
        editClient(new Client());
    }

    private void saveClient(ClientsForm.SaveEvent event) {
        ClientService.save(event.getClient());
        updateList();
        // closeEditor();
        grid.asSingleSelect().clear();
        editClient(new Client());
    }

    public void editClient(Client Client) {
        if (Client == null) {
            closeEditor();
        } else {
            form.setClient(Client);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void deleteClient(ClientsForm.DeleteEvent event) {
        ClientService.delete(event.getClient().getId());
        updateList();
        closeEditor();
    }

}
