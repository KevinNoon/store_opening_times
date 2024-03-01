package com.optimised.views.clients;

import com.optimised.model.Client;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;


public class ClientsForm extends FormLayout {
  TextField name = new TextField("Name");
  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");
  Binder<Client> binder = new BeanValidationBinder<>(Client.class);
  Notification notification = new Notification();

  public void setClient(Client Client) {
    binder.setBean(Client);
  }

  public ClientsForm() {
    addClassName("Client-form");
    binder.bindInstanceFields(this);
    add(name, createButtonsLayout());
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
    return new HorizontalLayout(save, delete, close);
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

  public static abstract class ClientFormEvent extends ComponentEvent<ClientsForm> {
    private Client Client;

    protected ClientFormEvent(ClientsForm source, Client Client) {
      super(source, false);
      this.Client = Client;
    }

    public Client getClient() {
      return Client;
    }
  }

  public static class SaveEvent extends ClientFormEvent {
    SaveEvent(ClientsForm source, Client Client) {
      super(source, Client);
    }
  }

  public static class DeleteEvent extends ClientFormEvent {
    DeleteEvent(ClientsForm source, Client Client) {
      super(source, Client);
    }

  }

  public static class CloseEvent extends ClientFormEvent {
    CloseEvent(ClientsForm source) {
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

