package com.wontlost.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.wontlost.jdenticon.JdenticonVaadin;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@PWA(name = "Jdenticon", shortName = "Jdenticon")
@Route(value = "")
@PageTitle("Jdenticon")
public class JdenticonView extends VerticalLayout {

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    JdenticonView() {
        super();
        TextField value = new TextField("value");
        value.setPlaceholder("wontlost");
        TextField size = new TextField("size");
        size.setPlaceholder("100");
        add(new H3("Try it yourself"));
        add(value, size);
        JdenticonVaadin jdenticonVaadin = new JdenticonVaadin("wontlost", 100);
        add(jdenticonVaadin);
        value.setValueChangeMode(ValueChangeMode.EAGER);
        value.addValueChangeListener(e->jdenticonVaadin.setValue(e.getValue()));
        size.setValueChangeMode(ValueChangeMode.EAGER);
        size.addValueChangeListener(e-> {
                    if(isNumeric(e.getValue())) {
                        jdenticonVaadin.setSize(Integer.parseInt(e.getValue()));
                    } else {
                        Div content = new Div();
                        content.addClassName("size-style");
                        content.setText("Input size is not a number!");

                        Notification notification = new Notification(content);
                        notification.setDuration(3000);

// @formatter:off
                        String styles = ".size-style { "
                                + "  color: red;"
                                + " }";
// @formatter:on

                        /*
                         * The code below register the style file dynamically. Normally you
                         * use @StyleSheet annotation for the component class. This way is
                         * chosen just to show the style file source code.
                         */
                        StreamRegistration resource = UI.getCurrent().getSession()
                                .getResourceRegistry()
                                .registerResource(new StreamResource("styles.css", () -> {
                                    byte[] bytes = styles.getBytes(StandardCharsets.UTF_8);
                                    return new ByteArrayInputStream(bytes);
                                }));
                        UI.getCurrent().getPage().addStyleSheet(
                                "base://" + resource.getResourceUri().toString());
                        notification.open();
                    }
                });
        setAlignItems(Alignment.CENTER);
    }

}
