package com.optimised.views;

import com.optimised.model.User;
import com.optimised.security.AuthenticatedUser;
import com.optimised.services.InfoService;
import com.optimised.services.UserService;
import com.optimised.views.about.AboutView;
import com.optimised.views.clients.ClientsView;
import com.optimised.views.coreTimes.CoreTimesView;
import com.optimised.views.counties.CountiesView;
import com.optimised.views.exceptions.ExceptionTimeView;
import com.optimised.views.logs.LogsView;
import com.optimised.views.places.PlaceView;
import com.optimised.views.setup.SettingsView;
import com.optimised.views.setup.email.EmailView;
import com.optimised.views.storeSystems.StoreSystemView;
import com.optimised.views.users.UsersView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;
import java.io.ByteArrayInputStream;
import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;

@Route(value = "")
@PermitAll
public class MainLayout extends AppLayout {
    @Autowired
    InfoService infoService;
    @Autowired
    UserService userService;

    public static class MenuItemInfo extends ListItem {

        private final Class<? extends Component> view;

        public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
            this.view = view;
            RouterLink link = new RouterLink();
            // Use Lumo classnames for various styling
            link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL,
                    TextColor.BODY);
            link.setRoute(view);

            Span text = new Span(menuTitle);
            // Use Lumo classnames for various styling
            text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

            if (icon != null) {
                link.add(icon);
            }
            link.add(text);
            add(link);
        }

        public Class<?> getView() {
            return view;
        }

    }

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;
    Text updated = new Text("Updated:");
    TextField time = new TextField();
    Checkbox isdarkmode = new Checkbox("Dark theme");

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        addToNavbar(createHeaderContent());
        setDrawerOpened(false);
    }

    private Component createHeaderContent() {
        Header header = new Header();
        header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

        isdarkmode.addValueChangeListener(e -> {
            setTheme(e.getValue());
        });

        Div layout = new Div();
        layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.LARGE);

        H1 appName = new H1("Store Opening Times");
        appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE);
        layout.add(appName);

        layout.add(updated.getText());
        layout.add(time);

        Nav nav = new Nav();
        nav.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Overflow.AUTO, LumoUtility.Padding.Horizontal.MEDIUM, LumoUtility.Padding.Vertical.XSMALL);

        UnorderedList list = new UnorderedList();
        list.addClassNames(LumoUtility.Display.FLEX, LumoUtility.Gap.SMALL, LumoUtility.ListStyleType.NONE, LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
        nav.add(list,isdarkmode);

        for (MenuItemInfo menuItem : createMenuItems()) {
            if (accessChecker.hasAccess(menuItem.getView())) {
                list.add(menuItem);
            }
        }

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();
            if (user.getIsdarkmode()!= null) {
                setTheme(user.getIsdarkmode());
                isdarkmode.setValue(user.getIsdarkmode());
            }
            Avatar avatar = new Avatar(user.getName());
            if (user.getProfilePicture() != null) {
                StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
                avatar.setImageResource(resource);
            }
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        header.add(layout, nav);
        return header;
    }

    private MenuItemInfo[] createMenuItems() {
        return new MenuItemInfo[]{
                new MenuItemInfo("Core Hours", LineAwesomeIcon.TH_SOLID.create(), CoreTimesView.class), //
                new MenuItemInfo("Exceptions", LineAwesomeIcon.USER.create(), ExceptionTimeView.class), //
                new MenuItemInfo("Clients", LineAwesomeIcon.ADDRESS_CARD.create(), ClientsView.class), //
                new MenuItemInfo("Counties", LineAwesomeIcon.GLOBE_EUROPE_SOLID.create(), CountiesView.class),
                new MenuItemInfo("Store Systems", LineAwesomeIcon.COG_SOLID.create(), StoreSystemView.class), //
                new MenuItemInfo("Places", LineAwesomeIcon.SHOPPING_BAG_SOLID.create(), PlaceView.class), //
                new MenuItemInfo("Users", LineAwesomeIcon.USER.create(), UsersView.class), //
                new MenuItemInfo("Settings", LineAwesomeIcon.COG_SOLID.create(), SettingsView.class), //
                new MenuItemInfo("Email Settings", LineAwesomeIcon.COG_SOLID.create(), EmailView.class), //
                new MenuItemInfo("Logs", LineAwesomeIcon.USER.create(), LogsView.class), //
                new MenuItemInfo("About", LineAwesomeIcon.FILE.create(), AboutView.class), //
        };
    }
    @Override
    protected void afterNavigation(){
        super.afterNavigation();
        time.getStyle().set("background-color","#00000000");
        if (infoService.findFirst(1l).isPresent()) {
            String dt = infoService.findFirst(1l).get().getLastUpdateTime();
            dt = dt.substring(0,dt.indexOf("T")) + " " + dt.substring(dt.indexOf("T") + 1, dt.indexOf("."));
            time.setValue(dt);
        }
    }
    private void setTheme(boolean dark) {
        var js = "document.documentElement.setAttribute('theme', $0)";

        getElement().executeJs(js, dark ? Lumo.DARK : Lumo.LIGHT);
    }
}
