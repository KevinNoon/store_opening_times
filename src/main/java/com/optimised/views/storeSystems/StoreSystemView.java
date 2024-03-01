package com.optimised.views.storeSystems;


import com.optimised.model.StoreSystem;
import com.optimised.services.StoreSystemService;
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
@PageTitle("StoreSystem")
@Route(value = "storesystem", layout = MainLayout.class)
public class StoreSystemView extends VerticalLayout {

    Grid<StoreSystem> grid = new Grid<>(StoreSystem.class);
    StoreSystemForm form;

    StoreSystemService storeSystemService;

    public StoreSystemView(StoreSystemService storeSystemService){
        this.storeSystemService = storeSystemService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(GetToolbar(),getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("SystemSystem-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editStoreSystem(event.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void configureForm() {
        form = new StoreSystemForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveStoreSystem);
        form.addDeleteListener(this::deleteStoreSystem);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout GetToolbar() {
        Button addClientButton = new Button("Add System");
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
        grid.setItems(storeSystemService.findAll());
    }

    private void closeEditor() {
        form.setStoreSystem(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addClient() {
        grid.asSingleSelect().clear();
        editStoreSystem(new StoreSystem());
    }

    private void saveStoreSystem(StoreSystemForm.SaveEvent event) {
        storeSystemService.save(event.getStoreSystem());
        updateList();
        // closeEditor();
        grid.asSingleSelect().clear();
        editStoreSystem(new StoreSystem());
    }

    public void editStoreSystem(StoreSystem storeSystem) {
        if (storeSystem == null) {
            closeEditor();
        } else {
            form.setStoreSystem(storeSystem);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void deleteStoreSystem(StoreSystemForm.DeleteEvent event) {
        storeSystemService.delete(event.getStoreSystem().getId());
        updateList();
        closeEditor();
    }

}
