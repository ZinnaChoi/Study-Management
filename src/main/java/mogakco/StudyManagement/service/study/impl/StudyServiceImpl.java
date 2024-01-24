package mogakco.StudyManagement.service.study.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.domain.StudyInfo;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.ScheduleCreateReq;
import mogakco.StudyManagement.dto.StudyCreateReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.InvalidRequestException;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.StudyInfoRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.study.StudyService;
import mogakco.StudyManagement.util.ExceptionUtil;

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

                // study_info 객체에 study_name, img set
                lo.setDBStart();
                StudyInfo sInfo = studyInfoRepository.findByStudyName(studyCreateReq.getStudyName());
                lo.setDBEnd();
                sInfo.updateStudyName(studyCreateReq.getStudyName());
                sInfo.updateStudyLogo(imageFile == null ? null : imageFile.getBytes());

                // schedule 객체에 데이터 set
                List<String> scheduleNames = studyCreateReq.getSchedules().stream()
                                .map(ScheduleCreateReq::getScheduleName).collect(Collectors.toList());
                // for (ScheduleCreateReq scheduleCreateReq : studyCreateReq.getSchedules()) {
                // String scheduleName = scheduleCreateReq.getScheduleName();
                // String startTime = scheduleCreateReq.getStartTime();
                // String endTime = scheduleCreateReq.getEndTime();
                // if (isStringNull(scheduleName)) {
                // return ExceptionUtil.handleException(new InvalidRequestException("빈 값을 넣을 수
                // 없습니다."));
                // }
                // Schedule schedule =
                // Schedule.builder().scheduleName(scheduleCreateReq.getScheduleName())
                // .startTime(scheduleCreateReq.getStartTime())
                // .endTime(scheduleCreateReq.getEndTime())
                // .build();

                // updateCandidates.add(scheduleName);
                // }
                // do update
                List<ScheduleCreateReq> req = studyCreateReq.getSchedules();
                List<Schedule> schedules = scheduleRepository.findAllByScheduleNameIn(scheduleNames);
                for (int i = 0; i < schedules.size(); i++) {
                        if (schedules.get(i).getScheduleName() == req.get(i).getScheduleName()) {
                                schedules.set(i, Schedule.builder().scheduleId(schedules.get(i).getScheduleId())
                                                .scheduleName(req.get(i).getScheduleName())
                                                .startTime(req.get(i).getStartTime()).endTime(req.get(i).getEndTime())
                                                .build());
                        }
                }
                // schedules = schedules.stream()
                // .filter(sc -> scheduleNames.contains(sc.getScheduleName()))
                // .collect(Collectors.toList());
                // 더티 체킹 해야함..
                lo.setDBStart();
                studyInfoRepository.save(sInfo);
                scheduleRepository.saveAll(schedules);
                lo.setDBEnd();

                return new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        }

        private boolean isStringNull(String str) {
                return str == null || str.isBlank();
        }
}