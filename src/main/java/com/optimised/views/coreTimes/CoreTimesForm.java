package com.optimised.views.coreTimes;

import com.optimised.model.CoreTimes;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.time.Duration;


public class CoreTimesForm extends FormLayout {
    TextField storeNo = new TextField("Store No");
    TextField storeName = new TextField("Store Name");
    TimePicker monOpen = new TimePicker();
    TimePicker monClose = new TimePicker();
    TimePicker tueOpen = new TimePicker();
    TimePicker tueClose = new TimePicker();
    TimePicker wedOpen = new TimePicker();
    TimePicker wedClose = new TimePicker();
    TimePicker thuOpen = new TimePicker();
    TimePicker thuClose = new TimePicker();
    TimePicker friOpen = new TimePicker();
    TimePicker friClose = new TimePicker();
    TimePicker satOpen = new TimePicker();
    TimePicker satClose = new TimePicker();
    TimePicker sunOpen = new TimePicker();
    TimePicker sunClose = new TimePicker();

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<CoreTimes> binder = new BeanValidationBinder<>(CoreTimes.class);

    public void setCoreTimes(CoreTimes coreTimes) {
        binder.setBean(coreTimes);
    }

   // private final CoreTimes coreTimes;

    public CoreTimesForm() {
        addClassName("core_times-form");
        storeNo.setReadOnly(true);
        storeName.setReadOnly(true);
        setTimes();
        HorizontalLayout monday = new HorizontalLayout(monOpen,monClose);
        HorizontalLayout tuesday = new HorizontalLayout(tueOpen,tueClose);
        HorizontalLayout wednesday = new HorizontalLayout(wedOpen,wedClose);
        HorizontalLayout thursday = new HorizontalLayout(thuOpen,thuClose);
        HorizontalLayout friday = new HorizontalLayout(friOpen,friClose);
        HorizontalLayout saturday = new HorizontalLayout(satOpen,satClose);
        HorizontalLayout sunday = new HorizontalLayout(sunOpen,sunClose);
        binder.bindInstanceFields(this);
        add(  storeNo,storeName, monday,tuesday,wednesday,thursday,friday,sunday,sunday,
                createButtonsLayout());
    }

    private void setTimes(){
        duration(monOpen, monClose, tueOpen, tueClose, wedOpen, wedClose);
        duration(thuOpen, thuClose, friOpen, friClose, satOpen, satClose);
        sunOpen.setStep(Duration.ofMinutes(30));
        sunOpen.setLabel("Sun Open");
        sunClose.setStep(Duration.ofMinutes(30));
        sunClose.setLabel("Sun Close");
    }

    private void duration(TimePicker monOpen, TimePicker monClose, TimePicker tueOpen, TimePicker tueClose, TimePicker wedOpen, TimePicker wedClose) {
        monOpen.setStep(Duration.ofMinutes(30));
        monOpen.setLabel("Sun Open");
        monClose.setStep(Duration.ofMinutes(30));
        monClose.setLabel("Sun Close");
        tueOpen.setStep(Duration.ofMinutes(30));
        tueOpen.setLabel("Sun Open");
        tueClose.setStep(Duration.ofMinutes(30));
        tueClose.setLabel("Sun Close");
        wedOpen.setStep(Duration.ofMinutes(30));
        wedOpen.setLabel("Sun Open");
        wedClose.setStep(Duration.ofMinutes(30));
        wedClose.setLabel("Sun Close");
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
            CoreTimes coreTimes = binder.getBean();
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }


    private void validateAndDelete(){
        if (binder.isValid()){
                    fireEvent(new DeleteEvent(this,binder.getBean()));
        }
    }

    public static abstract class CoreTimesFormEvent extends ComponentEvent<CoreTimesForm> {
        private CoreTimes coreTimes;

        protected CoreTimesFormEvent(CoreTimesForm source, CoreTimes coreTimes) {
            super(source, false);
            this.coreTimes = coreTimes;
        }
        public CoreTimes getCoreTimes() {
            return coreTimes;
        }
    }

    public static class SaveEvent extends CoreTimesFormEvent {
        SaveEvent(CoreTimesForm source, CoreTimes coreTimes) {
            super(source , coreTimes);
        }
    }

    public static class DeleteEvent extends CoreTimesFormEvent {
        DeleteEvent(CoreTimesForm source, CoreTimes coreTimes) {
            super(source, coreTimes);
        }
    }

    public static class CloseEvent extends CoreTimesFormEvent {
        CloseEvent(CoreTimesForm source) {
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
