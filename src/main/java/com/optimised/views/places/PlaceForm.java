package com.optimised.views.places;

import com.optimised.model.Place;
import com.optimised.services.StoreSystemService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

public class PlaceForm extends VerticalLayout {

  @Autowired
  final StoreSystemService storeSystemService;


  public void setStore(Place place) {
    binder.setBean(place);
  }

  TextField name = new TextField("Name");
  IntegerField storeNo = new IntegerField("Store Number");
  ComboBox<String> storeSystem = new ComboBox<>("StoreSystem");
  Button save = new Button("Save");
  Button close = new Button("Cancel");
  Binder<Place> binder = new BeanValidationBinder<>(Place.class);

  public PlaceForm(StoreSystemService storeSystemService) {
    this.storeSystemService = storeSystemService;
    addClassName("store-form");
    binder.bindInstanceFields(this);
    storeSystem.setItems(this.storeSystemService.findAllSystems());
    name.setReadOnly(true);
    add(
        name,
        storeNo,
        storeSystem,
        createButtonsLayout());
  }

  private HorizontalLayout createButtonsLayout() {
    save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
    close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

    save.addClickShortcut(Key.ENTER);
    close.addClickShortcut(Key.ESCAPE);

    save.addClickListener(event -> validateAndSave());
    close.addClickListener(event -> fireEvent(new CloseEvent(this)));

    binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
    return new HorizontalLayout(save, close);
  }

  private void validateAndSave() {
    if (binder.isValid()) {
      fireEvent(new SaveEvent(this, binder.getBean()));
    }
  }
  public static abstract class PlaceFormViewFormEvent extends ComponentEvent<PlaceForm> {
    private Place place;

    protected PlaceFormViewFormEvent(PlaceForm source, Place place) {
      super(source, false);
      this.place = place;
    }

    public Place getPlace() {
      return place;
    }
  }

  public static class SaveEvent extends PlaceFormViewFormEvent {
    SaveEvent(PlaceForm source, Place place) {
      super(source, place);
    }
  }

  public static class DeleteEvent extends PlaceFormViewFormEvent {
    DeleteEvent(PlaceForm source, Place place) {
      super(source, place);
    }

  }

  public static class CloseEvent extends PlaceFormViewFormEvent {
    CloseEvent(PlaceForm source) {
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
