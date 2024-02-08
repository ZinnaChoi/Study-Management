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
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.ScheduleReq;
import mogakco.StudyManagement.dto.ScheduleRes;
import mogakco.StudyManagement.dto.StudyInfoRes;
import mogakco.StudyManagement.dto.StudyReq;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.exception.InvalidRequestException;
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
        @Transactional(readOnly = true)
        public StudyInfoRes getStudy(LoggingService lo) {

                // study_info 테이블에서 스터디 조회
                lo.setDBStart();
                // TODO: 중앙 - 스터디 DB 분리 시 알맞은 study_info 가져올 것
                StudyInfo studyInfo = studyInfoRepository.findTopBy();
                lo.setDBEnd();
                if (studyInfo == null) {
                        CommonRes res = ExceptionUtil.handleException(
                                        new NotFoundException("등록된 스터디가 없습니다."));
                        return new StudyInfoRes(systemId, res.getRetCode(), res.getRetMsg(), null, null,
                                        null, null);
                }

                Long studyId = studyInfo.getStudyId();
                String studyName = studyInfo.getStudyName();
                byte[] logo = studyInfo.getStudyLogo();

                // schedule 테이블에서 전체 스케줄 조회
                lo.setDBStart();
                List<Schedule> schedules = scheduleRepository.findAll();
                lo.setDBEnd();
                List<ScheduleRes> scheduleResList = schedules.stream().map(s -> new ScheduleRes(s.getScheduleId(),
                                s.getScheduleName(), s.getStartTime(), s.getEndTime())).collect(Collectors.toList());

                return new StudyInfoRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), studyId, studyName,
                                logo, scheduleResList);
        }

        @Override
        @Transactional
        public CommonRes createStudy(StudyReq studyReq, MultipartFile imageFile, LoggingService lo)
                        throws IOException {
                CommonRes result = new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
                if (studyInfoRepository.existsByStudyName(studyReq.getStudyName())) {
                        return new CommonRes(systemId, ErrorCode.BAD_REQUEST.getCode(),
                                        ErrorCode.BAD_REQUEST.getMessage(
                                                        studyReq.getStudyName() + "스터디 이름이 이미 존재합니다."));
                }
                List<ScheduleReq> schedules = studyReq.getSchedules();
                List<String> scheduleNames = schedules.stream()
                                .map(ScheduleReq::getScheduleName)
                                .collect(Collectors.toList());

                List<Schedule> existSchedules = scheduleRepository.findAllByScheduleNameIn(scheduleNames);
                if (existSchedules.size() != 0) {
                        List<String> existScheduleNames = existSchedules.stream()
                                        .map(Schedule::getScheduleName)
                                        .collect(Collectors.toList());
                        return new CommonRes(systemId, ErrorCode.BAD_REQUEST.getCode(),
                                        ErrorCode.BAD_REQUEST.getMessage(
                                                        existScheduleNames.toString() + " schedule_name이 이미 존재합니다."));
                }
                byte[] studyLogoBytes = imageFile == null ? null : imageFile.getBytes();
                StudyInfo studyInfo = StudyInfo.builder().studyName(studyReq.getStudyName())
                                .studyLogo(studyLogoBytes)
                                .db_url(dbUrl).db_user(dbUser).db_password(bCryptPasswordEncoder.encode(dbPassword))
                                .build();
                lo.setDBStart();
                studyInfoRepository.save(studyInfo);
                lo.setDBEnd();

                for (ScheduleReq scheduleCreateReq : studyReq.getSchedules()) {
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
        public CommonRes updateStudy(StudyReq studyReq, MultipartFile imageFile, LoggingService lo) {

                List<Schedule> allSchedules = scheduleRepository.findAll();
                List<Schedule> reqSchedules = studyReq.getSchedules().stream()
                                .map(req -> Schedule.builder().scheduleName(req.getScheduleName())
                                                .startTime(req.getStartTime()).endTime(req.getEndTime()).build())
                                .collect(Collectors.toList());

                // 삽입
                List<Schedule> schedulesToInsert = reqSchedules.stream()
                                .filter(reqSchedule -> allSchedules.stream()
                                                .noneMatch(schedule -> schedule.getScheduleName()
                                                                .equals(reqSchedule.getScheduleName())))
                                .map(reqSchedule -> Schedule.builder().scheduleName(reqSchedule.getScheduleName())
                                                .startTime(reqSchedule.getStartTime()).endTime(reqSchedule.getEndTime())
                                                .build())
                                .collect(Collectors.toList());

                // 삭제
                List<Schedule> schedulesToDelete = allSchedules.stream()
                                .filter(schedule -> reqSchedules.stream()
                                                .noneMatch(reqSchedule -> reqSchedule.getScheduleName()
                                                                .equals(schedule.getScheduleName())))
                                .collect(Collectors.toList());

                // 업데이트
                List<Schedule> schedulesToUpdate = new ArrayList<>();
                for (Schedule aSchedule : allSchedules) {
                        for (Schedule rSchedule : reqSchedules) {
                                if (aSchedule.getScheduleName().equals(rSchedule.getScheduleName())) {
                                        schedulesToUpdate.add(
                                                        Schedule.builder()
                                                                        .scheduleId(aSchedule.getScheduleId())
                                                                        .scheduleName(rSchedule.getScheduleName())
                                                                        .startTime(rSchedule.getStartTime())
                                                                        .endTime(rSchedule.getEndTime())
                                                                        .build());
                                }
                        }
                }

                lo.setDBStart();
                StudyInfo sInfo = studyInfoRepository.findByStudyName(studyReq.getStudyName());
                lo.setDBEnd();
                // study_info 객체에 study_name, img set
                try {
                        sInfo.updateStudyName(studyReq.getUpdateStudyName());
                        sInfo.updateStudyLogo(imageFile == null ? null : imageFile.getBytes());
                } catch (NullPointerException e) {
                        // 수정인데 등록된거 체크하면 안됨!! 바꿔라잉
                        return ExceptionUtil.handleException(new NotFoundException("등록된 스터디가 없어 수정할 수 없습니다"));
                } catch (IOException e) {
                        return ExceptionUtil.handleException(new InvalidRequestException("이미지 파일을 변환 중 문제가 발생했습니다."));
                }
                // do query
                lo.setDBStart();
                studyInfoRepository.save(sInfo);
                scheduleRepository.deleteAll(schedulesToDelete);
                scheduleRepository.saveAll(schedulesToUpdate);
                scheduleRepository.saveAll(schedulesToInsert);
                lo.setDBEnd();

                return new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        }

        @Override
        @Transactional
        public CommonRes deleteStudy(Long studyId, LoggingService lo) {

                lo.setDBStart();
                StudyInfo studyInfo = studyInfoRepository.findByStudyId(studyId);
                lo.setDBEnd();
                if (studyInfo == null) {
                        return ExceptionUtil.handleException(
                                        new NotFoundException(studyId + " 아이디로 등록된 스터디가 존재하지 않아 삭제할 수 없습니다."));
                }

                // study_info 및 schedule 삭제(member_schedule, absent_schedule 테이블도 cascade 삭제됨)
                lo.setDBStart();
                studyInfoRepository.delete(studyInfo);
                scheduleRepository.deleteAll();
                lo.setDBEnd();

                return new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        }
}