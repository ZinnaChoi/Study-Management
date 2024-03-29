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
        public StudyInfoRes getStudy() {
                // study_info 테이블에서 스터디 조회
                // TODO: 중앙 - 스터디 DB 분리 시 알맞은 study_info 가져올 것
                StudyInfo studyInfo = studyInfoRepository.findTopBy();

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
                List<Schedule> schedules = scheduleRepository.findAll();
                List<ScheduleRes> scheduleResList = schedules.stream().map(s -> new ScheduleRes(s.getScheduleId(),
                                s.getScheduleName(), s.getStartTime(), s.getEndTime())).collect(Collectors.toList());

                return new StudyInfoRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), studyId, studyName,
                                logo, scheduleResList);
        }

        @Override
        @Transactional
        public CommonRes createStudy(StudyReq studyReq, MultipartFile imageFile)
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

                studyInfoRepository.save(studyInfo);
                for (ScheduleReq scheduleCreateReq : studyReq.getSchedules()) {
                        Schedule schedule = Schedule.builder().scheduleName(scheduleCreateReq.getScheduleName())
                                        .startTime(scheduleCreateReq.getStartTime())
                                        .endTime(scheduleCreateReq.getEndTime())
                                        .build();

                        scheduleRepository.save(schedule);
                }
                return result;
        }

        @Override
        @Transactional
        public CommonRes updateStudy(StudyReq studyReq, MultipartFile imageFile) {

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

                List<StudyInfo> sInfos = studyInfoRepository.findAll();
                StudyInfo sInfo = studyInfoRepository.findByStudyName(studyReq.getStudyName());

                try {
                        if (sInfos.isEmpty()) {
                                throw new NullPointerException("등록된 스터디가 없어 수정할 수 없습니다");
                        } else if (sInfo == null) {
                                throw new NullPointerException(
                                                studyReq.getStudyName() + " 이름으로 등록된 스터디가 없어 수정할 수 없습니다");
                        }
                        for (StudyInfo info : sInfos) {
                                if (info.getStudyName().equals(studyReq.getUpdateStudyName())
                                                && !studyReq.getStudyName().equals(studyReq.getUpdateStudyName())) {
                                        throw new InvalidRequestException(
                                                        "업데이트 할 " + studyReq.getUpdateStudyName()
                                                                        + " 스터디 이름이 이미 존재합니다.");
                                }
                                if (info.getStudyName().equals(studyReq.getStudyName())) {
                                        sInfo = info;
                                }
                        }
                        // study_info 객체에 study_name, img set
                        sInfo.updateStudyName(studyReq.getUpdateStudyName());
                        if (!studyReq.isUseCurrentLogo()) {
                                sInfo.updateStudyLogo(imageFile == null ? null : imageFile.getBytes());
                        }
                } catch (NullPointerException e) {
                        return ExceptionUtil.handleException(new NotFoundException(e.getMessage()));
                } catch (IOException e) {
                        return ExceptionUtil.handleException(new InvalidRequestException("이미지 파일을 변환 중 문제가 발생했습니다."));
                } catch (InvalidRequestException ie) {
                        return ExceptionUtil.handleException(new InvalidRequestException(ie.getMessage()));
                }
                // do query
                studyInfoRepository.save(sInfo);
                scheduleRepository.deleteAll(schedulesToDelete);
                scheduleRepository.saveAll(schedulesToUpdate);
                scheduleRepository.saveAll(schedulesToInsert);

                return new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        }

        @Override
        @Transactional
        public CommonRes deleteStudy(Long studyId) {

                StudyInfo studyInfo = studyInfoRepository.findByStudyId(studyId);

                if (studyInfo == null) {
                        return ExceptionUtil.handleException(
                                        new NotFoundException(studyId + " 아이디로 등록된 스터디가 존재하지 않아 삭제할 수 없습니다."));
                }

                // study_info 및 schedule 삭제(member_schedule, absent_schedule 테이블도 cascade 삭제됨)
                studyInfoRepository.delete(studyInfo);
                scheduleRepository.deleteAll();

                return new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        }
}