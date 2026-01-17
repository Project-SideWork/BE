import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.application.port.in.RecruitRole;
import com.sidework.project.application.port.in.SkillLevel;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.project.persistence.adapter.ProjectPersistenceAdapter;
import com.sidework.project.persistence.entity.ProjectEntity;
import com.sidework.project.persistence.mapper.ProjectMapper;
import com.sidework.project.persistence.repository.ProjectJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProjectPersistenceAdapterTest {
    @Mock
    private ProjectJpaRepository repo;
    private final ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);
    private ProjectPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProjectPersistenceAdapter(repo, mapper);
    }


    @Test
    void save는_도메인_객체를_영속성_객체로_변환해_저장한다() {
        Project domain = createProject(createCommand());
        adapter.save(domain);
        verify(repo).save(any(ProjectEntity.class));
    }

    private ProjectCommand createCommand() {
        return new ProjectCommand(
                "버스 실시간 위치 서비스",                 // title
                "WebSocket 기반 실시간 위치 공유 프로젝트", // description
                List.of(
                        new RecruitPosition(
                                RecruitRole.BACKEND,
                                1,
                                SkillLevel.JUNIOR
                        ),
                        new RecruitPosition(
                                RecruitRole.FRONTEND,
                                2,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2025, 3, 31),  // endDt
                MeetingType.HYBRID,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of("Spring Boot", "MySQL"), // requiredStacks
                List.of("Redis", "Kafka"),       // preferredStacks
                ProjectStatus.RECRUITING          // status
        );
    }
    private Project createProject(
            ProjectCommand command
    ) {
        return new Project(
                null,
                command.title(),
                command.description(),
                command.startDt(),
                command.endDt(),
                command.meetingType(),
                command.status()
        );
    }
}
