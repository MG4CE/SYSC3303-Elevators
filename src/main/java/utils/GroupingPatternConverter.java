package utils;

import java.util.List;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

/**
 * Custom formating option for log4j to allow for consistent spacing
 */
@Plugin(name="GroupingPatternConverter", category="Converter")
@ConverterKeys({"grp"})
public class GroupingPatternConverter extends LogEventPatternConverter {

    private final String pattern;

    public static GroupingPatternConverter newInstance(String[] options) {
        return new GroupingPatternConverter("grp", "grp", options);
    }

    private GroupingPatternConverter(String name, String style, String[] options) {
        super(name, style);
        if (options != null && options.length > 0) {
            this.pattern = options[0];
        } else {
            this.pattern = null;
        }
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        if (this.pattern == null) {
            return;
        }

        PatternParser parser = new PatternParser(null, "Converter", LogEventPatternConverter.class);
        List<PatternFormatter> formatters = parser.parse(this.pattern);

        StringBuilder groupBuilder = new StringBuilder();
        for (PatternFormatter formatter : formatters) {
            formatter.format(event, groupBuilder);
        }

        toAppendTo.append(groupBuilder.toString());
    }
}