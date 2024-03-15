package com.optimised.views.exceptions;

import com.optimised.controllers.email.CsvController;
import com.optimised.googleApi.GooglePlaces;
import com.optimised.model.ExceptionTime;
import com.optimised.services.CoreTimesService;
import com.optimised.services.ExceptionTimeService;
import com.optimised.services.PlaceService;
import com.optimised.tools.Excel;
import com.optimised.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@RolesAllowed({"ADMIN","USER"})
@PageTitle("Exceptions")
@Route(value = "exceptions", layout = MainLayout.class)
public class ExceptionTimeView extends VerticalLayout {

    Excel excel;
    GooglePlaces googlePlaces;

    Grid<ExceptionTime> grid = new Grid<>(ExceptionTime.class);
    ExceptionTimeForm form;
    final CoreTimesService coreTimesService;
    final ExceptionTimeService exceptionTimeService;
    final PlaceService placeService;
    Button updateButton = new Button("Update from CSV");
    Button addExceptionButton = new Button("Add Exception");
    Button createCsvButton = new Button("Create CSV");
    Button updateFromGoogle = new Button("Update from Google");
    Checkbox showExceptionBeforeNow = new Checkbox("Show Exception Before Now");
    private final Span errorField;
    private final Span updateCompleteField;

    public ExceptionTimeView(CoreTimesService coreTimesService, ExceptionTimeService exceptionTimeService, GooglePlaces googlePlaces, PlaceService placeService){
        this.coreTimesService = coreTimesService;
        this.exceptionTimeService = exceptionTimeService;
        this.googlePlaces = googlePlaces;
        this.placeService = placeService;
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
        add(getToolBar(),getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassNames("exceptionTime-grid");
        grid.setSizeFull();
        grid.setColumns("storeNo","storeName","changeDate","open","close");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editExceptionTime(event.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
    }

    private void configureForm() {
        form = new ExceptionTimeForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveExceptionTime);
        form.addDeleteListener(this::deleteExceptionTime);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolBar(){
        addExceptionButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addExceptionButton.addClickListener(click -> addException());

        createCsvButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        createCsvButton.addClickListener(click -> {
            List<ExceptionTime> exceptionTimes = exceptionTimeService.findByChanged();
            if (exceptionTimes.size() > 0) {
                for (ExceptionTime e:exceptionTimes
                     ) {
                    String systemType = placeService.findPlaceBySiteNo(e.getStoreNo()).getStoreSystem();
                    e.setSystemType(systemType);
                }
                excel.createCsv(exceptionTimes);
            }
        });

        updateFromGoogle.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateFromGoogle.addClickListener(event -> {
            googlePlaces.runGetPlaceDetails();
        } );

        showExceptionBeforeNow.addClickListener(e -> updateList());

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Update exception times");
        dialog.add(excel.setExceptionTimes(errorField,updateCompleteField),errorField,updateCompleteField);
        Button cancelButton = new Button("Close", e -> {
            updateList();
            dialog.close();
        });
        dialog.getFooter().add(cancelButton);
        updateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        updateButton.addClickListener(e -> {
            errorField.setVisible(false);;
            updateCompleteField.setVisible(false);
            dialog.open();
        });
        var toolbar = new HorizontalLayout(addExceptionButton,updateButton,createCsvButton,updateFromGoogle,showExceptionBeforeNow);
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
        if (showExceptionBeforeNow.getValue()) {
            grid.setItems(exceptionTimeService.findAll());
        } else {
            grid.setItems(exceptionTimeService.findByChangeDateAfter());
        }
    }

    private void closeEditor() {
        form.setExceptionTime(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void addException() {
        grid.asSingleSelect().clear();
        editExceptionTime(new ExceptionTime());
    }

    private void saveExceptionTime(ExceptionTimeForm.SaveEvent event) {
        exceptionTimeService.save(event.getExceptionTime());
        updateList();
        closeEditor();
        grid.asSingleSelect().clear();
//        editExceptionTime(new ExceptionTime());
    }

    public void editExceptionTime(ExceptionTime exceptionTime) {
        if (exceptionTime == null) {
            closeEditor();
        } else {
            form.setExceptionTime(exceptionTime);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void deleteExceptionTime(ExceptionTimeForm.DeleteEvent event) {
        exceptionTimeService.delete(event.getExceptionTime().getId());
        updateList();
        closeEditor();
    }
}
