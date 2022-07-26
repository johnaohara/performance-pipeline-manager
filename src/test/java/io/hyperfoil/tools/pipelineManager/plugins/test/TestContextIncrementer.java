package io.hyperfoil.tools.pipelineManager.plugins.test;

import io.hyperfoil.tools.pipelineManager.api.BasePipelineExecutable;
import io.hyperfoil.tools.pipelineManager.api.ExecutableInitializationException;
import io.hyperfoil.tools.pipelineManager.api.PipelineContext;
import io.hyperfoil.tools.pipelineManager.api.Plugin;
import io.hyperfoil.tools.pipelineManager.parser.ConfigurationParser;
import org.jboss.logging.Logger;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.hyperfoil.tools.pipelineManager.parser.PipelineFactory.containsKeys;
import static io.hyperfoil.tools.pipelineManager.parser.PipelineFactory.numElements;

@Singleton
public class TestContextIncrementer implements Plugin<Map<String, Object>> {

    @Override
    public String getName() {
        return "test-context-incrementer";
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public ConfigurationParser.SectionParser getParser() {
        return new ConfigurationParser.SectionParser<Map<String, Object>>(
                (section, builder) -> {
                    if (!section.isEmpty() && numElements(section, 1) && containsKeys(section, "variable")) {
                        return null;
                    }
                    List<String> errors = new ArrayList<>();
                    return errors; //TODO: Fill out error handling
                }, //validation
                (config, section, builder) -> {
                    config.addExecutable(
                            TestContextIncrementerExecutable.instance().variable((String) section.get("variable"))
                    );
                });
    }

    public static class TestContextIncrementerExecutable extends BasePipelineExecutable {
        Logger logger = Logger.getLogger(TestContextIncrementerExecutable.class);

        private String contextVariableName;

        private TestContextIncrementerExecutable() {

        }

        public TestContextIncrementerExecutable variable(String name) {
            this.contextVariableName = name;
            return this;
        }

        public static TestContextIncrementerExecutable instance() {
            return new TestContextIncrementerExecutable();
        }

        @Override
        public void run(PipelineContext context) {
            if (context.hasObject(this.contextVariableName)) {
                Integer counter = context.<Integer>getObject(this.contextVariableName);
                counter = counter + 1;
                context.<Integer>setObject(this.contextVariableName, counter);
            } else {
                logger.errorf("Missing context payload; %s", this.contextVariableName);
            }
        }

        @Override
        public void initialize() throws ExecutableInitializationException {
            //do nothing - this executable does not need to initialize any remote service
        }

    }


}
