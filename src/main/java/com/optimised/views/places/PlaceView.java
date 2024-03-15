package com.optimised.views.places;

import com.optimised.googleApi.GooglePlaces;
import com.optimised.model.Place;
import com.optimised.services.PlaceService;
import com.optimised.services.StoreSystemService;
import com.optimised.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

@RolesAllowed({"ADMIN","USER"})
@PageTitle("Stores")
@Route(value = "stores", layout = MainLayout.class)


public class PlaceView extends VerticalLayout {

    @Autowired
    final StoreSystemService storeSystemService;

    Grid<Place> grid= new Grid<>(Place.class);
    TextField filterByName = new TextField();
    TextField filterByAddress = new TextField();
    IntegerField filterByStoreNo = new IntegerField();
    Button update = new Button("Update Places");
    Checkbox showNotInUse = new Checkbox("Show Not In Use");
    PlaceForm form;
    PlaceService placeService;
    GooglePlaces googlePlaces;

    public PlaceView(StoreSystemService storeSystemService, PlaceService placeService){
        this.storeSystemService = storeSystemService;
        this.placeService = placeService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid(){
        grid.addClassNames("site-grid");
        grid.setSizeFull();

        grid.setColumns("storeNo","storeSystem","name","address","phoneNo","locationLat","locationLng");

        String LIT_TEMPLATE_WEB = """
            <vaadin-button title="Go to ..."
                           @click="${clickHandler}"
                           theme="tertiary-inline small link">
                <vaadin-icon icon="vaadin:globe"></vaadin-icon>
            </vaadin-button>""";

        String LIT_TEMPLATE_MAP = """
            <vaadin-button title="Go to ..."
                           @click="${clickHandler}"
                           theme="tertiary-inline small link">
                <vaadin-icon icon="vaadin:car"></vaadin-icon>
            </vaadin-button>""";

        grid.addColumn(
                LitRenderer.<Place>of(LIT_TEMPLATE_WEB)
                    .withProperty("id", Place::getWebSite)

                    .withFunction("clickHandler", place -> {
                        UI.getCurrent().getPage().open(place.getWebSite());
                    }))
            .setHeader("Web Page").setSortable(false).getElement();

        grid.addColumn(
                LitRenderer.<Place>of(LIT_TEMPLATE_MAP)
                    .withProperty("id", Place::getGoogleUrl)

                    .withFunction("clickHandler", place -> {
                        UI.getCurrent().getPage().open(place.getGoogleUrl());
                    }))
            .setHeader("Map").setSortable(false).getElement();

        grid.addComponentColumn((place) -> {
            Checkbox checkBox = new Checkbox();
            checkBox.setValue(place.getInuse());
            checkBox.addValueChangeListener(event -> {place.setInuse(event.getValue());
                place.setInuse(event.getValue());
                placeService.save(place);
                updateList();});
            return checkBox;}).setHeader("In use");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editStore(event.getValue()));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,GridVariant.LUMO_WRAP_CELL_CONTENT);

        //grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);;
    }

    private void configureForm() {

        form = new PlaceForm(storeSystemService);
        form.setWidth("25em");
        form.addSaveListener(this::saveStore);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        filterByName.setPlaceholder("Filter by name...");
        filterByName.setClearButtonVisible(true);
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        filterByName.addValueChangeListener(e -> updateList());
        filterByAddress.setPlaceholder("Filter by address...");
        filterByAddress.setClearButtonVisible(true);
        filterByAddress.setValueChangeMode(ValueChangeMode.LAZY);
        filterByAddress.addValueChangeListener(e -> updateList());
        filterByStoreNo.setPlaceholder("Filter by store no...");
        filterByStoreNo.setClearButtonVisible(true);
        filterByStoreNo.setValueChangeMode(ValueChangeMode.LAZY);
        filterByStoreNo.addValueChangeListener(e -> updateList());
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.addClickListener(e -> {
            googlePlaces.runGetPlace();
        });
        showNotInUse.addClickListener(e -> {
            updateList();
        });

        var toolbar = new HorizontalLayout(filterByStoreNo,filterByName,filterByAddress,update,showNotInUse);
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
        if (!filterByStoreNo.isEmpty()){
            grid.setItems(placeService.findPlaceBySiteNo(filterByStoreNo.getValue()));
        } else {
            if (showNotInUse.getValue()) {
                grid.setItems(placeService.findAllPlacesByNameAndAddress(filterByName.getValue(), filterByAddress.getValue()));
            } else {
                grid.setItems(placeService.findAllPlacesByNameAndAddressAndInuse(filterByName.getValue(), filterByAddress.getValue()));
            }
        }
    }

    private void closeEditor() {
        form.setStore(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    public void editStore(Place place) {
        if (place == null) {
            closeEditor();
        } else {
            form.setStore(place);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void saveStore(PlaceForm.SaveEvent event) {
        placeService.save(event.getPlace());
        updateList();
        closeEditor();
    }

}
