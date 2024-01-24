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
import mogakco.StudyManagement.dto.ScheduleCUReq;
import mogakco.StudyManagement.dto.StudyCUReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.NotFoundException;
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
        public DTOResCommon createStudy(StudyCUReq studyCUReq, MultipartFile imageFile, LoggingService lo)
                        throws IOException {
                DTOResCommon result = new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
                if (studyInfoRepository.existsByStudyName(studyCUReq.getStudyName())) {
                        return new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                                        ErrorCode.BAD_REQUEST.getMessage(
                                                        studyCUReq.getStudyName() + "스터디 이름이 이미 존재합니다."));
                }
                List<ScheduleCUReq> schedules = studyCUReq.getSchedules();
                List<String> scheduleNames = schedules.stream()
                                .map(ScheduleCUReq::getScheduleName)
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
                StudyInfo studyInfo = StudyInfo.builder().studyName(studyCUReq.getStudyName())
                                .studyLogo(studyLogoBytes)
                                .db_url(dbUrl).db_user(dbUser).db_password(bCryptPasswordEncoder.encode(dbPassword))
                                .build();
                lo.setDBStart();
                studyInfoRepository.save(studyInfo);
                lo.setDBEnd();

                for (ScheduleCUReq scheduleCreateReq : studyCUReq.getSchedules()) {
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
        public DTOResCommon updateStudy(StudyCUReq studyCUReq, MultipartFile imageFile, LoggingService lo)
                        throws IOException {

                List<String> scheduleNames = studyCUReq.getSchedules().stream()
                                .map(ScheduleCUReq::getScheduleName).collect(Collectors.toList());
                lo.setDBStart();
                StudyInfo sInfo = studyInfoRepository.findByStudyName(studyCUReq.getStudyName());
                List<Schedule> schedules = scheduleRepository.findAllByScheduleNameIn(scheduleNames);
                lo.setDBEnd();
                // study_info 객체에 study_name, img set
                if (sInfo == null) {
                        return ExceptionUtil.handleException(new NotFoundException("등록된 스터디가 없어 수정할 수 없습니다"));
                }
                sInfo.updateStudyName(studyCUReq.getStudyName());
                sInfo.updateStudyLogo(imageFile == null ? null : imageFile.getBytes());

                List<ScheduleCUReq> req = studyCUReq.getSchedules();
                List<Schedule> upsertCandidates = new ArrayList<>();
                int maxLoopSize = Math.max(req.size(), schedules.size());
                for (int i = 0; i < maxLoopSize; i++) {
                        // 업데이트 해야 한다면
                        if ((i < schedules.size() && i < req.size())) {
                                if (schedules.get(i).getScheduleName().equals(req.get(i).getScheduleName())) {
                                        upsertCandidates.add(
                                                        Schedule.builder().scheduleId(schedules.get(i).getScheduleId())
                                                                        .scheduleName(req.get(i).getScheduleName())
                                                                        .startTime(req.get(i).getStartTime())
                                                                        .endTime(req.get(i).getEndTime())
                                                                        .build());
                                }
                                // 새로운 것을 인서트 해야 한다면
                        } else if ((i >= schedules.size() && i < req.size())) {
                                upsertCandidates.add(Schedule.builder()
                                                .scheduleName(req.get(i).getScheduleName())
                                                .startTime(req.get(i).getStartTime()).endTime(req.get(i).getEndTime())
                                                .build());
                                // 인서트나 업데이트 둘다 안해도 된다면
                        } else {
                                break;
                        }
                }
                // do upsert
                lo.setDBStart();
                studyInfoRepository.save(sInfo);
                scheduleRepository.saveAll(upsertCandidates);
                lo.setDBEnd();

                return new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        }
}