package lld.practice.infra.taskprocessor.processor;

import lld.practice.infra.taskprocessor.entity.EmailTask;
import lld.practice.infra.taskprocessor.entity.Task;
import lld.practice.infra.taskprocessor.entity.TaskType;
import lld.practice.infra.taskprocessor.retry.RetryPolicy;
import lld.practice.infra.taskprocessor.store.TaskStore;

public class EmailTaskProcessor extends AbstractTaskProcessor<EmailTask> {

    public EmailTaskProcessor(RetryPolicy retryPolicy, TaskStore taskStore) {
        super(retryPolicy, taskStore);
    }

    @Override
    public void doProcessTask(EmailTask emailTask) {
        System.out.println("Sent Email SuccessFully " + emailTask);
    }


    @Override
    public TaskType getTaskType() {
        return TaskType.EMAIL;
    }
}
