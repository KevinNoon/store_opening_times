package com.optimised.views.setup.email;

import com.optimised.model.settings.EmailSettings;
import com.optimised.services.settings.EmailSettingsService;
import com.optimised.views.MainLayout;
import com.vaadin.flow.component.Component;
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
@PageTitle("Email")
@Route(value = "email", layout = MainLayout.class)
public class EmailView extends VerticalLayout {
  Grid<EmailSettings> grid = new Grid<>(EmailSettings.class);
  EmailForm form;

  EmailSettingsService emailSettingsService;

  public EmailView(EmailSettingsService emailSettingsService){
    this.emailSettingsService = emailSettingsService;
    addClassName("list-view");
    setSizeFull();
    configureGrid();
    configureForm();
    add(getContent());
    updateList();
    closeEditor();
  }

  private void configureGrid() {
    grid.addClassNames("Setting-grid");
    grid.setSizeFull();
    grid.setColumns("mailHost","mailPort","mailUser","mailPass","mailAuth","mailStartTLS");
    grid.getColumns().forEach(col -> col.setAutoWidth(true));
    grid.asSingleSelect().addValueChangeListener(event -> editSetting(event.getValue()));
    grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
  }

  private void configureForm() {
    form = new EmailForm();
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

  private void updateList() {
    List<EmailSettings> emailSettings = new ArrayList<>();
    emailSettings.add(emailSettingsService.getSettings());
    grid.setItems(emailSettings);
  }

  private void closeEditor() {
    form.setSettings(null);
    form.setVisible(false);
    removeClassName("editing");
  }

  private void saveSetting(EmailForm.SaveEvent event) {
    emailSettingsService.save(event.getSettings());
    updateList();
     closeEditor();
    grid.asSingleSelect().clear();
    editSetting(new EmailSettings());
  }

  public void editSetting(EmailSettings emailSettings) {
    if (emailSettings == null) {
      closeEditor();
    } else {
      form.setSettings(emailSettings);
      form.setVisible(true);
      addClassName("editing");
    }
  }
}
