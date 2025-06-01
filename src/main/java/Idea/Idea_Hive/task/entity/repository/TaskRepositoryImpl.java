package Idea.Idea_Hive.task.entity.repository;

import Idea.Idea_Hive.task.entity.QProjectTask;
import Idea.Idea_Hive.task.entity.QTask;
import Idea.Idea_Hive.task.entity.Task;
import Idea.Idea_Hive.task.entity.TaskType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Task> findTasksByProjectId(Long projectId) {
        QProjectTask projectTask = QProjectTask.projectTask;
        QTask task = QTask.task;

        return jpaQueryFactory
                .select(task)
                .from(projectTask)
                .join(task)
                .on(projectTask.id.taskId.eq(task.id))
                .where(projectTask.id.projectId.eq(projectId))
                .fetch();
    }

    @Override
    public List<Task> findTasksByProjectIdAndTaskType(Long projectId, TaskType taskType) {
        QProjectTask projectTask = QProjectTask.projectTask;
        QTask task = QTask.task;

        return jpaQueryFactory
                .select(task)
                .from(projectTask)
                .join(task)
                .on(projectTask.id.taskId.eq(task.id))
                .where(projectTask.id.projectId.eq(projectId), task.taskType.eq(taskType))
                .fetch();
    }
}
