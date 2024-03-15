package com.optimised.views.coreTimes;

import com.optimised.model.CoreTimes;
import com.optimised.services.CoreTimesService;
import com.optimised.services.ExceptionTimeService;
import com.optimised.tools.Excel;
import com.optimised.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

@RolesAllowed({"ADMIN","USER"})
@PageTitle("Core Hours")
@Route(value = "core-hours", layout = MainLayout.class)
public class CoreTimesView extends VerticalLayout {
    Excel excel;

    Grid<CoreTimes> grid = new Grid<>(CoreTimes.class);
    CoreTimesForm form;
    final CoreTimesService coreTimesService;
    final ExceptionTimeService exceptionTimeService;
    Button updateButton = new Button("Update");
    Button addCoreTimeButton = new Button("Add Core Time");
    private final Span errorField;
    private final Span updateCompleteField;

    CoreTimesView(CoreTimesService coreTimesService, ExceptionTimeService exceptionTimeService){
        this.coreTimesService = coreTimesService;
        this.exceptionTimeService = exceptionTimeService;
        excel = new Excel(this.coreTimesService, this.exceptionTimeService);
        errorField = new Span();
        errorField.setVisible(false);
        errorField.getStyle().set("color", "red");
        updateCompleteField = new Span();
        updateCompleteField.setVisible(false);
        updateCompleteField.getStyle().set("color", "green");
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolBar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid(){
        grid.addClassNames("core_times-grid");
        grid.setSizeFull();
        grid.setColumns("storeNo","storeName",
            "sunOpen", "sunClose",
            "monOpen", "monClose",
            "tueOpen", "tueClose",
            "wedOpen", "wedClose",
            "thuOpen", "thuClose",
            "friOpen", "friClose",
            "satOpen", "satClose");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editCoreTimes(event.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void configureForm() {
        form = new CoreTimesForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveCoreTimes);
        form.addDeleteListener(this::deleteCoreTimes);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolBar(){
        addCoreTimeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addCoreTimeButton.addClickListener(click -> addCoreTime());
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Update core times");
        dialog.add(excel.setCoreTimes(errorField,updateCompleteField),errorField,updateCompleteField);
        Button cancelButton = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButton.addClickListener(e -> {
            errorField.setVisible(false);
            updateCompleteField.setVisible(false);
            dialog.open();
        });
        var toolbar = new HorizontalLayout(addCoreTimeButton,updateButton);
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
        grid.setItems(coreTimesService.findAll());
    }

    private void closeEditor() {
        form.setCoreTimes(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addCoreTime() {
        grid.asSingleSelect().clear();
        editCoreTimes(new CoreTimes());
    }

    private void saveCoreTimes(CoreTimesForm.SaveEvent event) {
        coreTimesService.save(event.getCoreTimes());
        updateList();
        closeEditor();
        grid.asSingleSelect().clear();
//        editCoreTimes(new CoreTimes());
    }

    public void editCoreTimes(CoreTimes coreTimes) {
        if (coreTimes == null) {
            closeEditor();
        } else {
            form.setCoreTimes(coreTimes);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void deleteCoreTimes(CoreTimesForm.DeleteEvent event) {
        coreTimesService.delete(event.getCoreTimes().getId());
        updateList();
        closeEditor();
    }

};
