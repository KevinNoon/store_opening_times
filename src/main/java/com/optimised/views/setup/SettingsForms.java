package com.optimised.views.setup;

import com.optimised.googleApi.Constants;
import com.optimised.model.settings.Setting;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class SettingsForms extends FormLayout {

  TextField client = new TextField("Client");
  Checkbox enableAutoUpdate = new Checkbox("AutoUpdate");
  TimePicker updateTime = new TimePicker("updateTime");
  TextField csvChgName = new TextField("CsvChg Name");
  TextField csvChgSuffix = new TextField("CsvChg Suffix");
  TextField csvChgTempDir = new TextField("CsvChg Dir");
  TextField apiKey = new TextField("API Key");

  ComboBox<String> changeFlagReset = new ComboBox<String>("changeFlagReset",Constants.changeFlagResetValue.CSV.toString(),
      Constants.changeFlagResetValue.API_OPEN.toString(),Constants.changeFlagResetValue.API_CLOSED.toString());
  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");
  Binder<Setting> binder = new BeanValidationBinder<>(Setting.class);

  public void setSettings(Setting settings){
    binder.setBean(settings);
  }

  public SettingsForms(){
    addClassName("Settings-form");
    binder.bindInstanceFields(this);
    add(client,enableAutoUpdate,updateTime,csvChgName,csvChgSuffix,csvChgTempDir,changeFlagReset,apiKey, createButtonsLayout());
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    delete.addClickShortcut(Key.DELETE);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> validateAndDelete());
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
    return new HorizontalLayout(save,delete, close);
  }

  private void validateAndSave() {
    if (binder.isValid()) {
        fireEvent(new SaveEvent(this, binder.getBean()));
  }
  }
  private void validateAndDelete() {
    if (binder.isValid()) {
      fireEvent(new DeleteEvent(this, binder.getBean()));
    }
  }

  public static abstract class SettingsFormEvent extends ComponentEvent<SettingsForms> {
    private Setting settings;

    protected SettingsFormEvent(SettingsForms source, Setting settings) {
      super(source, false);
      this.settings = settings;
    }

    public Setting getSettings() {
      return settings;
    }
  }

  public static class SaveEvent extends SettingsFormEvent {
    SaveEvent(SettingsForms source, Setting settings) {super(source, settings);}
  }

  public static class DeleteEvent extends SettingsFormEvent {
    DeleteEvent(SettingsForms source, Setting settings) {
      super(source, settings);
    }
  }

  public static class CloseEvent extends SettingsFormEvent {
    CloseEvent(SettingsForms source) {
      super(source, null);
    }
  }

  public Registration addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
    return addListener(DeleteEvent.class, listener);
  }
  public Registration addSaveListener(ComponentEventListener<SaveEvent> listener) {
    return addListener(SaveEvent.class, listener);
  }
  public Registration addCloseListener(ComponentEventListener<CloseEvent> listener) {
    return addListener(CloseEvent.class, listener);
  }
}
