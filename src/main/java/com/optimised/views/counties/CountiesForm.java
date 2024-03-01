package com.optimised.views.counties;

import com.optimised.model.County;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;


public class CountiesForm extends FormLayout {
  TextField name = new TextField("Name");
  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button close = new Button("Cancel");
  Binder<County> binder = new BeanValidationBinder<>(County.class);

  public void setCounty(County county){
    binder.setBean(county);
  }

  public CountiesForm(){
    addClassName("county-form");
    binder.bindInstanceFields(this);
    add(name,
        createButtonsLayout());
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
      String county_name = name.getValue().trim();
        fireEvent(new SaveEvent(this, binder.getBean()));
    }
  }

  private void validateAndDelete() {
    if (binder.isValid()) {
      fireEvent(new DeleteEvent(this, binder.getBean()));
    }
  }

  public static abstract class CountyFormEvent extends ComponentEvent<CountiesForm> {
    private County county;

    protected CountyFormEvent(CountiesForm source, County county) {
      super(source, false);
      this.county = county;
    }

    public County getCounty() {
      return county;
    }
  }

  public static class SaveEvent extends CountyFormEvent {
    SaveEvent(CountiesForm source, County county) {
      super(source, county);
    }
  }

  public static class DeleteEvent extends CountyFormEvent {
    DeleteEvent(CountiesForm source, County county) {
      super(source, county);
    }

  }

  public static class CloseEvent extends CountyFormEvent {
    CloseEvent(CountiesForm source) {
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

