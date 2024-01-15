package mogakco.StudyManagement.service.study.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.domain.StudyInfo;
import mogakco.StudyManagement.dto.ScheduleCreateReq;
import mogakco.StudyManagement.dto.StudyCreateReq;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.StudyInfoRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.study.StudyService;

@Service
public class StudyServiceImpl implements StudyService {

    private final StudyInfoRepository studyInfoRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ScheduleRepository scheduleRepository;

    public StudyServiceImpl(StudyInfoRepository studyInfoRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
            ScheduleRepository scheduleRepository) {
        this.studyInfoRepository = studyInfoRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.scheduleRepository = scheduleRepository;
    }

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    // protected byte[] getStudyLogoBytes(MultipartFile studyLogo) throws
    // java.io.IOException {
    // if (studyLogo == null) {
    // return null;
    // }
    // try {
    // return studyLogo.getBytes();
    // } catch (IOException e) {
    // throw new RuntimeException("Failed to process study logo file", e);
    // }
    // }

    @Override
    public void createStudy(StudyCreateReq studyCreateReq, LoggingService lo) {

        if (studyInfoRepository.existsByStudyName(studyCreateReq.getStudyName())) {
            throw new IllegalArgumentException("스터디이름이 이미 존재합니다.");
        }
        byte[] studyLogoBytes = studyCreateReq.getStudyLogo();
        StudyInfo studyInfo = StudyInfo.builder().studyName(studyCreateReq.getStudyName())
                .studyLogo(studyLogoBytes)
                .db_url(dbUrl).db_user(dbUser).db_password(bCryptPasswordEncoder.encode(dbPassword))
                .build();
        lo.setDBStart();
        studyInfoRepository.save(studyInfo);
        for (ScheduleCreateReq scheduleCreateReq : studyCreateReq.getSchedules()) {

            if (scheduleRepository.existsByEventName(scheduleCreateReq.getEventName())) {
                throw new IllegalArgumentException("event_name이 이미 존재합니다.");
            }
            Schedule schedule = Schedule.builder().eventName(scheduleCreateReq.getEventName())
                    .startTime(scheduleCreateReq.getStartTime())
                    .endTime(scheduleCreateReq.getEndTime())
                    .build();

            scheduleRepository.save(schedule);

            lo.setDBEnd();

        }
    }
}