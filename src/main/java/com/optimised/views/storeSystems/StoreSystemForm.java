package com.optimised.views.storeSystems;

import com.optimised.model.StoreSystem;
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


public class StoreSystemForm extends FormLayout {


  TextField name = new TextField("name");

  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");
  Binder<StoreSystem> binder = new BeanValidationBinder<>(StoreSystem.class);
  Notification notification = new Notification();

  public void setStoreSystem(StoreSystem storeSystem) {
    binder.setBean(storeSystem);
  }

  public StoreSystemForm() {
    addClassName("StoreSystem-form");
    binder.bindInstanceFields(this);

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

  public static abstract class StoreSystemFormEvent extends ComponentEvent<StoreSystemForm> {
    private StoreSystem storeSystem;

    protected StoreSystemFormEvent(StoreSystemForm source, StoreSystem storeSystem) {
      super(source, false);
      this.storeSystem = storeSystem;
    }

    public StoreSystem getStoreSystem() {
      return storeSystem;
    }
  }

  public static class SaveEvent extends StoreSystemFormEvent {
    SaveEvent(StoreSystemForm source, StoreSystem storeSystem) {
      super(source, storeSystem);
    }
  }

  public static class DeleteEvent extends StoreSystemFormEvent {
    DeleteEvent(StoreSystemForm source, StoreSystem storeSystem) {
      super(source, storeSystem);
    }

  }

  public static class CloseEvent extends StoreSystemFormEvent {
    CloseEvent(StoreSystemForm source) {
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

