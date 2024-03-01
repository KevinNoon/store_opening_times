package com.optimised.views.counties;

import com.optimised.model.County;
import com.optimised.services.CountyService;
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
@PageTitle("Counties")
@Route(value = "counties", layout = MainLayout.class)
public class CountiesView extends VerticalLayout {
    Grid<County> grid = new Grid<>(County.class);
    CountiesForm form;
    CountyService countyService;

    public CountiesView(CountyService countyService){
        this.countyService = countyService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(GetToolbar(),getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("county-grid");
        grid.setSizeFull();
        grid.setColumns("name");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editCounty(event.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void configureForm() {
        form = new CountiesForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveCounty);
        form.addDeleteListener(this::deleteCounty);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout GetToolbar() {
        Button addCountyButton = new Button("Add County");
        addCountyButton.addClickListener(click -> addCounty());
        var toolbar = new HorizontalLayout(addCountyButton);
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
        grid.setItems(countyService.findAll());
    }

    private void closeEditor() {
        form.setCounty(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addCounty() {
        grid.asSingleSelect().clear();
        editCounty(new County());
    }

    private void saveCounty(CountiesForm.SaveEvent event) {
        countyService.save(event.getCounty());
        updateList();
        //closeEditor();
        grid.asSingleSelect().clear();
        editCounty(new County());
    }

    public void editCounty(County county) {
        if (county == null) {
            closeEditor();
        } else {
            form.setCounty(county);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void deleteCounty(CountiesForm.DeleteEvent event) {
        countyService.delete(event.getCounty().getId());
        updateList();
        closeEditor();
    }
}
