package com.optimised.views.setup.email;

import com.optimised.model.settings.EmailSettings;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class EmailForm extends FormLayout {

  TextField mailHost = new TextField("Host");
  IntegerField mailPort = new IntegerField("Port");
  TextField mailUser = new TextField("User");
  TextField mailPass = new TextField("Password");
  Checkbox mailAuth = new Checkbox("Auth");
  Checkbox mailStartTLS = new Checkbox("TLS");

  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");
  Binder<EmailSettings> binder = new BeanValidationBinder<>(EmailSettings.class);

  public void setSettings(EmailSettings emailSettings){
    binder.setBean(emailSettings);
  }

  public EmailForm(){
    addClassName("Settings-form");
    binder.bindInstanceFields(this);
    add(mailHost,mailPort,mailUser,mailPass,mailAuth,mailStartTLS, createButtonsLayout());
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
    //  String Client_name = name.getValue().trim();
        fireEvent(new SaveEvent(this, binder.getBean()));
  }
  }
  private void validateAndDelete() {
    if (binder.isValid()) {
      fireEvent(new DeleteEvent(this, binder.getBean()));
    }
  }

  public static abstract class SettingsFormEvent extends ComponentEvent<EmailForm> {
    private EmailSettings emailSettings;

    protected SettingsFormEvent(EmailForm source, EmailSettings emailSettings) {
      super(source, false);
      this.emailSettings = emailSettings;
    }

    public EmailSettings getSettings() {
      return emailSettings;
    }
  }

  public static class SaveEvent extends SettingsFormEvent {
    SaveEvent(EmailForm source, EmailSettings emailSettings) {
      super(source, emailSettings);
    }
  }

  public static class DeleteEvent extends SettingsFormEvent {
    DeleteEvent(EmailForm source, EmailSettings emailSettings) {
      super(source, emailSettings);
    }
  }
  public static class CloseEvent extends SettingsFormEvent {
    CloseEvent(EmailForm source) {
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
