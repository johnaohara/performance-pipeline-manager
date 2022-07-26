package io.hyperfoil.tools.pipelineManager.api;

import io.hyperfoil.tools.pipelineManager.PipelineManager;
import io.quarkus.arc.Arc;
import io.quarkus.arc.InstanceHandle;
import org.jboss.logging.Logger;

public abstract class BasePipelineExecutable<T> implements PipelineExecutable {

    Logger logger = Logger.getLogger(BasePipelineExecutable.class);

    PipelineExecutable next;
    protected PipelineContext context;

    @Override
    public void setContext(PipelineContext context) {
        this.context = context;
    }

    public PipelineExecutable next() {
        return this.next;
    }

    public void next(PipelineExecutable nxt) {
        this.next = nxt;
    }

    @Override
    public void run() {
        run(this.context);
        if (this.next() != null) {
            this.next().setContext(this.context);
            //TODO:: inversion of control for this dispatch
            InstanceHandle instanceHandle = Arc.container().instance(PipelineManager.class); //get ref to ExperimentManager instance
            if (instanceHandle.isAvailable()) {
                ((PipelineManager) instanceHandle.get()).schedule(this.next());
            } else {
                logger.error("Could not resolve ExperimentManager");
            }
        }
    }
}
