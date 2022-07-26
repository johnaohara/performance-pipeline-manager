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
import java.util.concurrent.CountDownLatch;

@Singleton
public class TestLatchRelease implements Plugin<Map<String, Object>> {

    Logger logger = Logger.getLogger(TestLatchRelease.class);

    @Override
    public String getName() {
        return "test-latch-release";
    }

    @Override
    public Type getType() {
        return Type.PROCESSOR;
    }

    @Override
    public ConfigurationParser.SectionParser getParser() {
        return new ConfigurationParser.SectionParser<Map<String, Map>>(
                (section, builder) -> {
                    if (section == null) {
                        return null;
                    }
                    List<String> errors = new ArrayList<>();
                    return errors; //TODO: Fill out error handling
                }, //validation
                (config, section, builder) -> {
                    logger.debug("Parsing test-latch-release section");
                    config.addExecutable(TestLatchReleaseExecutable.instance());
                });
    }

    public static class TestLatchReleaseExecutable extends BasePipelineExecutable {

        Logger logger = Logger.getLogger(TestLatchReleaseExecutable.class);

        private TestLatchReleaseExecutable() {
        }

        public static TestLatchReleaseExecutable instance(){
            return new TestLatchReleaseExecutable();
        }

        @Override
        public void run(PipelineContext context) {
            if(context.hasObject("test-latch")){
                logger.info("Releasing countdown latch");
                context.<CountDownLatch>getObject("test-latch").countDown();
            } else {
                logger.warn("Could not find CountdownLatch in context!");
            }
        }

        @Override
        public void initialize() throws ExecutableInitializationException {
            //do nothing - this executable does not need to initialize any remote service
        }
    }

}
