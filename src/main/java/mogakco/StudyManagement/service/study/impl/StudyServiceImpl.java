package mogakco.StudyManagement.service.study.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.domain.StudyInfo;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.ScheduleCreateReq;
import mogakco.StudyManagement.dto.StudyCreateReq;
import mogakco.StudyManagement.enums.ErrorCode;
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

        @Value("${study.systemId}")
        protected String systemId;

        @Override
        @Transactional
        public DTOResCommon createStudy(StudyCreateReq studyCreateReq, MultipartFile imageFile, LoggingService lo)
                        throws IOException {
                DTOResCommon result = new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
                if (studyInfoRepository.existsByStudyName(studyCreateReq.getStudyName())) {
                        return new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                                        ErrorCode.BAD_REQUEST.getMessage(
                                                        studyCreateReq.getStudyName() + "스터디 이름이 이미 존재합니다."));
                }
                List<ScheduleCreateReq> schedules = studyCreateReq.getSchedules();
                List<String> scheduleNames = schedules.stream()
                                .map(ScheduleCreateReq::getScheduleName)
                                .collect(Collectors.toList());

                List<Schedule> existSchedules = scheduleRepository.findAllByScheduleNameIn(scheduleNames);
                if (existSchedules.size() != 0) {
                        List<String> existScheduleNames = existSchedules.stream()
                                        .map(Schedule::getScheduleName)
                                        .collect(Collectors.toList());
                        return new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                                        ErrorCode.BAD_REQUEST.getMessage(
                                                        existScheduleNames.toString() + " schedule_name이 이미 존재합니다."));
                }
                byte[] studyLogoBytes = imageFile == null ? null : imageFile.getBytes();
                StudyInfo studyInfo = StudyInfo.builder().studyName(studyCreateReq.getStudyName())
                                .studyLogo(studyLogoBytes)
                                .db_url(dbUrl).db_user(dbUser).db_password(bCryptPasswordEncoder.encode(dbPassword))
                                .build();
                lo.setDBStart();
                studyInfoRepository.save(studyInfo);
                lo.setDBEnd();

                for (ScheduleCreateReq scheduleCreateReq : studyCreateReq.getSchedules()) {
                        Schedule schedule = Schedule.builder().scheduleName(scheduleCreateReq.getScheduleName())
                                        .startTime(scheduleCreateReq.getStartTime())
                                        .endTime(scheduleCreateReq.getEndTime())
                                        .build();

                        lo.setDBStart();
                        scheduleRepository.save(schedule);
                        lo.setDBEnd();
                }
                return result;
        }

        @Override
        @Transactional
        public DTOResCommon updateStudy(StudyCreateReq studyCreateReq, MultipartFile imageFile, LoggingService lo)
                        throws IOException {

                DTOResCommon result = new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
                // 스터디 인포 테이블에 study_name, img 업데이트
                StudyInfo sInfo = studyInfoRepository.findByStudyName(studyCreateReq.getStudyName());
                sInfo.updateStudyName(studyCreateReq.getStudyName());
                sInfo.updateStudyLogo(imageFile == null ? null : imageFile.getBytes());
                lo.setDBStart();
                studyInfoRepository.save(sInfo);
                lo.setDBEnd();

                // 멤버 스케줄 테이블, 엡센트 스케줄 테이블에 해당 스케줄이 둘 중 하나 존재한다
                // true - 업데이트 못한다고 리턴
                // false - 스케줄 테이블에 schedule(event_name, start_time, end_time) 업데이트
                return new DTOResCommon();
        }
}