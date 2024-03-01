package com.optimised.views.exceptions;

import com.optimised.model.ExceptionTime;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.time.Duration;

public class ExceptionTimeForm extends FormLayout {
  TextField storeNo = new TextField("store No");
  TextField storeName = new TextField("Store Name");
  DatePicker changeDate = new DatePicker("Change Date");
  TimePicker open = new TimePicker("Open Time");
  TimePicker close = new TimePicker("Close Time");

  Button save = new Button("Save");
  Button delete = new Button("Delete");
  Button cancel = new Button("Cancel");
  Binder<ExceptionTime> binder = new BeanValidationBinder<>(ExceptionTime.class);

  public void setExceptionTime(ExceptionTime exceptionTime){
    binder.setBean(exceptionTime);
  }

  public ExceptionTimeForm(){
    storeNo.setReadOnly(true);
    storeName.setReadOnly(true);
    open.setStep(Duration.ofMinutes(30));
    close.setStep(Duration.ofMinutes(30));
    addClassName("exception_time-form");
    binder.bindInstanceFields(this);
    add(storeNo,storeName, changeDate, open, close,
        createButtonsLayout());
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    delete.addClickShortcut(Key.DELETE);
    cancel.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    delete.addClickListener(event -> validateAndDelete());
    cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
    return new HorizontalLayout(save,delete, cancel);
  }

  private void validateAndSave() {
    if (binder.isValid()) {
      ExceptionTime exceptionTime =  binder.getBean();
        fireEvent(new SaveEvent(this, binder.getBean()));
    }
  }

  private void validateAndDelete() {
    if (binder.isValid()) {
      fireEvent(new DeleteEvent(this, binder.getBean()));
    }
  }

  public static abstract class exceptionTimeFormEvent extends ComponentEvent<ExceptionTimeForm> {
    private ExceptionTime exceptionTime;

    protected exceptionTimeFormEvent(ExceptionTimeForm source, ExceptionTime exceptionTime) {
      super(source, false);
      this.exceptionTime = exceptionTime;
    }

    public ExceptionTime getExceptionTime() {
      return exceptionTime;
    }
  }

  public static class SaveEvent extends exceptionTimeFormEvent {
    SaveEvent(ExceptionTimeForm source, ExceptionTime exceptionTime) {
      super(source, exceptionTime);
    }
  }

  public static class DeleteEvent extends exceptionTimeFormEvent {
    DeleteEvent(ExceptionTimeForm source, ExceptionTime exceptionTime) {
      super(source, exceptionTime);
    }

  }

  public static class CloseEvent extends exceptionTimeFormEvent {
    CloseEvent(ExceptionTimeForm source) {
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

