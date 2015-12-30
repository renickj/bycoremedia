package com.coremedia.blueprint.cae.view;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.objectserver.view.TextView;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Programmed view that renders a given {@link Markup} or map as plain JSON value.
 * Markup String representations will be rendered without quotes.
 */
public class PlainJSONView implements TextView {

    private static final Logger LOG = LoggerFactory.getLogger(PlainJSONView.class);

    @Override
    public void render(Object bean, String view, Writer writer, HttpServletRequest request, HttpServletResponse response) {

        try {
            String jsonValue = null;
            if (bean instanceof Markup) {
                Markup markup = (Markup) bean;
                MarkupToJSONValueConverter converter = new MarkupToJSONValueConverter();
                MarkupUtil.asPlain(markup, converter);
                jsonValue = converter.asJSONString();
            }
            else if (bean instanceof CMLinkable.SettingMap) {
                jsonValue = asJSONValueString(((CMLinkable.SettingMap)bean).getMap());
            }

            if (StringUtils.isNotEmpty(jsonValue)) {
                writer.write(jsonValue);
            }
        }
        catch (IOException e) {
            LOG.error("Cannot write json representation", e);
        }
    }

    protected static String asJSONValueString(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static class MarkupToJSONValueConverter extends Writer {

        private StringBuilder buf = new StringBuilder();

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            buf.append(cbuf, off, len);
        }

        @Override
        public void flush() throws IOException {
            ;
        }

        @Override
        public void close() throws IOException {
            ;
        }

        protected String asJSONString() {
            String s = buf.toString();

            // Replace new lines with space
            s = s.replace("\n"," ");

            String jsonValue = asJSONValueString(s);

            // Trim leading and trailing quotes
            if (jsonValue.startsWith("\"")) {
                jsonValue = jsonValue.substring(1);
            }
            if (jsonValue.endsWith("\"")) {
                jsonValue = jsonValue.substring(0, jsonValue.length()-1);
            }
            return jsonValue;
        }
    }
}
