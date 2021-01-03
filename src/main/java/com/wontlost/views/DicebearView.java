package com.wontlost.views;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.wontlost.dicebear.Constants.*;
import com.wontlost.dicebear.DicebearVaadin;
import com.wontlost.dicebear.Options;
import org.vaadin.addon.sliders.PaperSlider;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@Viewport("width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover")
@PWA(name = "Dicebear", shortName = "Dicebear")
@Route(value = "")
@PageTitle("Dicebear")
public class DicebearView extends VerticalLayout {

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    private void changeColor(HasValue.ValueChangeEvent event, Options options, DicebearVaadin dicebearVaadin) {
        if(event.getValue().equals(Style.initials)) {
            options.setBackground(nextColor());
        }else{
            options.setBackground("transparent");
        }
        dicebearVaadin.setOptions(options);
    }

    private String nextColor() {
        Random random = new Random();

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(0xffffff + 1);

        // format it as hexadecimal string (with hashtag and leading zeros)
        return String.format("#%06x", nextInt);
    }

    DicebearView() {
        super();
        Options options = new Options();
        TextField value = new TextField("value");
        value.setPlaceholder("wontlost");
        TextField size = new TextField("size");
        size.setPlaceholder("100");
        Select<Style> select = new Select<>();
        select.setLabel("Style");
        List<Style> styleList = Arrays.asList(Style.values());
        select.setItemLabelGenerator(Style::name);
        select.setItems(styleList);
        select.setValue(Style.avataaars);
        PaperSlider radius = new PaperSlider(0, 50, 0);

        add(new H3("Try it yourself"));
        DicebearVaadin dicebearVaadin = new DicebearVaadin();
        dicebearVaadin.setValue("wontlost");
        dicebearVaadin.setStyle(Style.avataaars);
//        options.setBackground("white").setDataUri(false)
//                .setWidth(100).setHeight(100).setMargin(0).setRadius(50);

        add(select, value, size, radius, dicebearVaadin);
        value.setValueChangeMode(ValueChangeMode.EAGER);
        value.addValueChangeListener(e-> {
            dicebearVaadin.setValue(e.getValue());
            changeColor(e, options, dicebearVaadin);
        });
        radius.addValueChangeListener(e->{
            options.setRadius(radius.getValue());
            dicebearVaadin.setOptions(options);
        });
        size.setValueChangeMode(ValueChangeMode.EAGER);
        size.addValueChangeListener(e-> {
                    if(isNumeric(e.getValue())) {
                        options.setWidth(Integer.parseInt(e.getValue()));
                        options.setHeight(Integer.parseInt(e.getValue()));
                        changeColor(e, options, dicebearVaadin);
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

        select.addValueChangeListener(event -> {
            dicebearVaadin.setStyle(event.getValue());
            changeColor(event, options, dicebearVaadin);
        });
        setAlignItems(Alignment.CENTER);
    }

}
