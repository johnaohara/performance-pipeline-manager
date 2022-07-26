package io.hyperfoil.tools.pipelineManager.pipeline;

import io.hyperfoil.tools.pipelineManager.api.PipelineContext;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PipelineTests extends BasePipelineTest {

    @Test
    public void runSimplePipelineTest() {
        var testPipelineDef = """
                    test-simple-pipeline:
                      - test-receiver:
                          receiver-id: 10
                      - test-processor:
                          processor-id: 12
                      - test-latch-release:
                """;
        executePipeline("test-simple-pipeline", testPipelineDef);
    }

    @Test
    public void simpleContextPropagrationTest() {
        var testPipelineDef = """
                    test-propagation-pipeline:
                      - context-setter:
                          variable: test-counter
                          initial-value: 10
                      - test-context-incrementer:
                          variable: test-counter
                      - test-context-incrementer:
                          variable: test-counter
                      - test-context-incrementer:
                          variable: test-counter
                      - test-latch-release:
                """;

        PipelineContext pipelineContext = executePipeline("test-propagation-pipeline", testPipelineDef);

        if( pipelineContext == null){
            fail("Returned null PipelineContext");
        }
        Integer counter = pipelineContext.<Integer>getObject("test-counter");

        assertEquals(13, counter);

    }

    @Test
    public void simpleInputOutputTest() {
        var testPipelineDef = """
                    test-propagation-pipeline:
                      - context-setter:
                          variable: test-counter
                          initial-value: 10
                      - test-context-incrementer:
                          variable: test-counter
                      - test-context-output:
                          variable: test-counter
                      - input-logger:
                      - test-latch-release:
                """;

        PipelineContext pipelineContext = executePipeline("test-propagation-pipeline", testPipelineDef);

        if( pipelineContext == null){
            fail("Returned null PipelineContext");
        }
        Integer counter = pipelineContext.<Integer>getObject("test-counter");

    }

    @Test
    public void simpleOutputFactoryTest() {
        var testPipelineDef = """
                    test-propagation-pipeline:
                      - test-output-factory:
                          variable: test-counter
                          initial-value: 10
                      - input-logger:
                      - test-latch-release:
                """;

        PipelineContext pipelineContext = executePipeline("test-propagation-pipeline", testPipelineDef);

        if( pipelineContext == null){
            fail("Returned null PipelineContext");
        }
        assertTrue(pipelineContext.getResult().containsKey("variable"));
        assertEquals(10, pipelineContext.getResult().get("initial-value"));

    }
}
