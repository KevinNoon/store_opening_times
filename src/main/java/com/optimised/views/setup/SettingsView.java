package com.optimised.views.setup;

import com.optimised.googleApi.GooglePlaces;
import com.optimised.model.settings.Setting;
import com.optimised.services.settings.SettingsService;
import com.optimised.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
import java.util.List;

@RolesAllowed({"ADMIN"})
@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends VerticalLayout {
  Grid<Setting> grid = new Grid<>(Setting.class);
  SettingsForms form;

  SettingsService settingsService;
  GooglePlaces googlePlaces;

  public SettingsView(SettingsService settingsService, GooglePlaces googlePlaces){
    this.settingsService = settingsService;
    this.googlePlaces = googlePlaces;
    addClassName("list-view");
    setSizeFull();
    configureGrid();
    configureForm();
    add(getContent());
    add(createButtons());
    updateList();
    closeEditor();
  }

  private void configureGrid() {
    grid.addClassNames("Setting-grid");
    grid.setSizeFull();
    grid.setColumns("updateTime","enableAutoUpdate","csvChgName","csvChgSuffix","csvChgTempDir","changeFlagReset","apiKey");
    grid.getColumns().forEach(col -> col.setAutoWidth(true));
    grid.asSingleSelect().addValueChangeListener(event -> editSetting(event.getValue()));
    grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
  }

  private void configureForm() {
    form = new SettingsForms();
    form.setWidth("25em");
    form.addSaveListener(this::saveSetting);
    form.addCloseListener(e -> closeEditor());
  }

  private Component getContent() {
    HorizontalLayout content = new HorizontalLayout(grid, form);
    content.setFlexGrow(2, grid);
    content.setFlexGrow(1, form);
    content.addClassNames("content");
    content.setSizeFull();
    return content;
  }

  private HorizontalLayout createButtons(){
    Button updateStores = new Button("Update Stores");
    updateStores.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    updateStores.addClickListener(event -> {
      googlePlaces.runGetPlace();
    });
    Button updateStoreDetails = new Button("Update Store Details");
    updateStoreDetails.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    updateStoreDetails.addClickListener(event -> {
      googlePlaces.runGetPlaceDetails();
    } );

    HorizontalLayout buttons = new HorizontalLayout(updateStores,updateStoreDetails);
    return buttons;
  }

  private void updateList() {
    List<Setting> settings = new ArrayList<>();
    settings.add(settingsService.getSettings());
    grid.setItems(settings);
  }

  private void closeEditor() {
    form.setSettings(null);
    form.setVisible(false);
    removeClassName("editing");
  }

  private void saveSetting(SettingsForms.SaveEvent event) {
    settingsService.save(event.getSettings());
    updateList();
     closeEditor();
    grid.asSingleSelect().clear();
    editSetting(new Setting());
  }

  public void editSetting(Setting settings) {
    if (settings == null) {
      closeEditor();
    } else {
      form.setSettings(settings);
      form.setVisible(true);
      addClassName("editing");
    }
  }
}
