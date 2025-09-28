package lld.practice.process.task.processor;

import lld.practice.process.task.entity.EmailTask;
import lld.practice.process.task.entity.Task;
import lld.practice.process.task.entity.TaskType;
import lld.practice.process.task.retry.RetryPolicy;
import lld.practice.process.task.store.TaskStore;

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
