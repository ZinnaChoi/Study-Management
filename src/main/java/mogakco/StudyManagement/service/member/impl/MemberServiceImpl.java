package mogakco.StudyManagement.service.member.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.exception.InvalidRequestException;
import org.springframework.transaction.annotation.Transactional;
import mogakco.StudyManagement.domain.Member;
import mogakco.StudyManagement.domain.MemberSchedule;
import mogakco.StudyManagement.domain.Schedule;
import mogakco.StudyManagement.domain.StudyInfo;
import mogakco.StudyManagement.domain.WakeUp;
import mogakco.StudyManagement.dto.DTOResCommon;
import mogakco.StudyManagement.dto.MemberDetails;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberInfo;
import mogakco.StudyManagement.dto.MemberInfoRes;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.dto.SimplePageable;
import mogakco.StudyManagement.dto.StudyMembersRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.MemberRole;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.StudyInfoRepository;
import mogakco.StudyManagement.repository.WakeUpRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.member.MemberService;
import mogakco.StudyManagement.util.DateUtil;
import mogakco.StudyManagement.util.ExceptionUtil;
import mogakco.StudyManagement.util.JWTUtil;
import mogakco.StudyManagement.util.PageUtil;
import mogakco.StudyManagement.util.SecurityUtil;

@Service
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;
    private final MemberScheduleRepository memberScheduleRepository;
    private final WakeUpRepository wakeUpRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudyInfoRepository studyInfoRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;

    @Value("${jwt.expired.time}")
    private Long expiredTime;

    @Value("${study.systemId}")
    protected String systemId;

    public MemberServiceImpl(MemberRepository memberRepository,
            MemberScheduleRepository memberScheduleRepository,
            WakeUpRepository wakeUpRepository,
            ScheduleRepository scheduleRepository,
            StudyInfoRepository studyInfoRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JWTUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.wakeUpRepository = wakeUpRepository;
        this.scheduleRepository = scheduleRepository;
        this.studyInfoRepository = studyInfoRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username);
        if (member != null) {
            return new MemberDetails(member);
        }
        return null;
    }

    @Override
    public MemberLoginRes login(MemberLoginReq loginInfo, LoggingService lo) {
        MemberLoginRes response = new MemberLoginRes();
        lo.setDBStart();
        Member member = memberRepository.findById(loginInfo.getId());
        lo.setDBEnd();

        if (member == null) {
            response.setRetMsg(ErrorCode.NOT_FOUND.getMessage("Member"));
            response.setRetCode(ErrorCode.NOT_FOUND.getCode());
            response.setToken("");
            return response;
        }

        String targetPwd = loginInfo.getPassword();
        String originPwd = member.getPassword();

        if (!bCryptPasswordEncoder.matches(targetPwd, originPwd)) {
            response.setRetMsg(ErrorCode.BAD_REQUEST.getMessage("비밀번호가 맞지 않습니다"));
            response.setRetCode(ErrorCode.BAD_REQUEST.getCode());
            response.setToken("");
            return response;
        }

        response.setRetMsg(ErrorCode.OK.getMessage());
        response.setRetCode(ErrorCode.OK.getCode());
        response.setToken(jwtUtil.createJwt(member.getId(), member.getRole().toString(), expiredTime));
        return response;
    }

    @Override
    @Transactional
    public DTOResCommon join(MemberJoinReq joinInfo, LoggingService lo) {
        DTOResCommon result = new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        Member member = Member.builder()
                .id(joinInfo.getId())
                .password(bCryptPasswordEncoder.encode(joinInfo.getPassword()))
                .name(joinInfo.getName())
                .contact(joinInfo.getContact())
                .role(MemberRole.USER) // 회원가입시 무조건 USER 권한
                .createdAt(DateUtil.getCurrentDateTime())
                .updatedAt(DateUtil.getCurrentDateTime())
                .build();
        WakeUp wakeUp = WakeUp.builder()
                .member(member)
                .wakeupTime(joinInfo.getWakeupTime())
                .build();

        List<MemberSchedule> mSchedules = new ArrayList<>();
        lo.setDBStart();
        List<Schedule> schedules = scheduleRepository.findAllByScheduleNameIn(joinInfo.getScheduleNames());
        lo.setDBEnd();
        if (schedules.size() != 0) {
            for (Schedule s : schedules) {
                mSchedules.add(MemberSchedule.builder()
                        .member(member)
                        .schedule(s)
                        .createdAt(DateUtil.getCurrentDateTime())
                        .updatedAt(DateUtil.getCurrentDateTime())
                        .build());
            }
            lo.setDBStart();
            memberRepository.save(member);
            wakeUpRepository.save(wakeUp);
            memberScheduleRepository.saveAll(mSchedules);
            lo.setDBEnd();
        } else {
            result = new DTOResCommon(systemId, ErrorCode.BAD_REQUEST.getCode(),
                    ErrorCode.BAD_REQUEST.getMessage(joinInfo.getScheduleNames() + " Schedule is Not Regist"));
        }

        return result;
    }

    @Override
    public boolean isIdDuplicated(MemberIdDuplReq idInfo, LoggingService lo) {
        lo.setDBStart();
        boolean isExist = memberRepository.existsById(idInfo.getId());
        lo.setDBEnd();

        return isExist;
    }

    @Override
    @Transactional
    public MemberInfoRes getMemberInfo(LoggingService lo) {
        MemberInfoRes result = new MemberInfoRes();

        result.setSystemId(systemId);
        result.setRetCode(ErrorCode.OK.getCode());
        result.setRetMsg(ErrorCode.OK.getMessage());
        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        result.setId(member.getId());
        result.setName(member.getName());
        result.setContact(member.getContact());
        result.setRole(member.getRole());

        List<StudyInfo> studyInfos = studyInfoRepository.findAll();
        // DB별 하나의 study 밖에 없으므로 0번 인덱스에서 조회
        result.setStudyName(studyInfos.size() == 0 ? null : studyInfos.get(0).getStudyName());

        List<MemberSchedule> memberSchedules = memberScheduleRepository.findAllByMember(member);
        List<Schedule> schedules = memberSchedules.stream()
                .map(MemberSchedule::getSchedule)
                .collect(Collectors.toList());
        List<String> scheduleNames = schedules.stream()
                .map(Schedule::getScheduleName)
                .collect(Collectors.toList());

        result.setScheduleName(scheduleNames);
        WakeUp wakeUp = wakeUpRepository.findByMember(member);
        result.setWakeupTime(wakeUp == null ? null : wakeUp.getWakeupTime());

        return result;

    }

    @Override
    @Transactional
    public DTOResCommon setMemberInfo(MemberInfoUpdateReq updateInfo, LoggingService lo) {
        DTOResCommon result = new DTOResCommon(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());

        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        switch (updateInfo.getType()) {
            case NAME:
                // member 테이블 업데이트
                String changedName = updateInfo.getName();
                if (changedName == null || changedName.isBlank()) {
                    return ExceptionUtil.handleException(new InvalidRequestException("변경할 이름을 입력해주세요"));
                }
                member.updateName(changedName);
                lo.setDBStart();
                memberRepository.save(member);
                lo.setDBEnd();
                break;
            case SCHEDULE_NAMES:
                // member_schedule 테이블 업데이트
                List<String> userScheduleNames = updateInfo.getScheduleName();
                if (userScheduleNames == null || userScheduleNames.size() == 0) {
                    return ExceptionUtil.handleException(new InvalidRequestException("참여 스터디 스케줄을 선택해주세요"));
                }
                lo.setDBStart();
                List<MemberSchedule> mSchedules = memberScheduleRepository.findAllByMember(member);
                List<Schedule> userSchedules = scheduleRepository.findAllByScheduleNameIn(userScheduleNames);
                lo.setDBEnd();

                List<String> dbScheduleNames = mSchedules.stream().map(m -> m.getSchedule().getScheduleName())
                        .collect(Collectors.toList());

                List<MemberSchedule> toInsert = calculateInserts(userSchedules, dbScheduleNames, member);
                List<MemberSchedule> toUpdate = calculateUpdates(userSchedules, dbScheduleNames, mSchedules);
                List<MemberSchedule> toDelete = calculateDeletes(userScheduleNames, mSchedules);
                lo.setDBStart();
                memberScheduleRepository.deleteAll(toDelete);
                memberScheduleRepository.saveAll(toInsert);
                memberScheduleRepository.saveAll(toUpdate);
                lo.setDBEnd();
                break;
            case WAKEUP:
                // wakeup 테이블 업데이트
                String changedWakeupTime = updateInfo.getWakeupTime();
                if (changedWakeupTime == null || changedWakeupTime.isBlank()) {
                    return ExceptionUtil.handleException(new InvalidRequestException("변경할 기상 시간을 선택해주세요"));
                }
                WakeUp wakeUp = wakeUpRepository.findByMember(member);
                wakeUp.updateWakeupTime(changedWakeupTime);
                lo.setDBStart();
                wakeUpRepository.save(wakeUp);
                lo.setDBEnd();
                break;

            case CONTACT:
                // member 테이블 업데이트
                String changedContact = updateInfo.getContact();
                member.updateContact(changedContact);
                lo.setDBStart();
                memberRepository.save(member);
                lo.setDBEnd();
                break;
            case PASSWORD:
                // member 테이블 업데이트
                String changedPassword = updateInfo.getPassword();
                String originPassword = member.getPassword();
                // 비밀번호 현재 비밀번호랑 일치 여부 확인
                if (bCryptPasswordEncoder.matches(changedPassword, originPassword)) {
                    return ExceptionUtil.handleException(new InvalidRequestException("현재 비밀번호와 동일합니다."));
                }
                // 비밀번호 암호화
                member.updatePassword(bCryptPasswordEncoder.encode(changedPassword));
                lo.setDBStart();
                memberRepository.save(member);
                lo.setDBEnd();
                break;
            default:
                break;
        }

        return result;

    }

    @Override
    public StudyMembersRes getMembersBySchedule(LoggingService lo, String sName, Pageable pageable) {

        List<MemberInfo> memberInfos = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        SimplePageable simplePageable;

        if (sName == null) { // 전체 조회
            lo.setDBStart();
            // admin 계정은 조회 X
            Page<Member> pMembers = memberRepository.findAllByRoleNot(MemberRole.ADMIN,
                    pageable);
            lo.setDBEnd();
            members = pMembers.getContent();
            simplePageable = PageUtil.createSimplePageable(pMembers);
        } else {
            // 멤버 스케줄에서 스케줄 이름으로 조건 걸어서 가져온 member_id로 member에서 id, name 조회
            lo.setDBStart();
            Schedule schedule = scheduleRepository.findByScheduleName(sName);
            Page<MemberSchedule> pSchedules = memberScheduleRepository.findAllBySchedule(schedule, pageable);
            lo.setDBEnd();
            List<Member> mSchedules = pSchedules.getContent().stream().map(MemberSchedule::getMember)
                    .collect(Collectors.toList());
            Set<Long> ids = mSchedules.stream().map(Member::getMemberId).collect(Collectors.toSet());
            lo.setDBStart();
            members = memberRepository.findAllById(ids);
            lo.setDBEnd();
            simplePageable = PageUtil.createSimplePageable(pSchedules);
        }

        members.stream().map(m -> {
            memberInfos.add(new MemberInfo(m.getId(), m.getName()));
            return m;
        }).collect(Collectors.toList());

        return new StudyMembersRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), memberInfos,
                simplePageable);
    }

    @Override
    public StudyMembersRes getMembersByWakeupTime(LoggingService lo, String time, Pageable pageable) {

        Page<WakeUp> pWakeUp;
        List<MemberInfo> memberInfos = new ArrayList<>();
        lo.setDBStart();
        pWakeUp = time == null ? wakeUpRepository.findAll(pageable)
                : wakeUpRepository.findAllByWakeupTime(time, pageable);
        lo.setDBEnd();
        List<Member> mWakeUp = pWakeUp.getContent().stream().map(WakeUp::getMember)
                .collect(Collectors.toList());
        List<Long> ids = mWakeUp.stream().map(Member::getMemberId).collect(Collectors.toList());
        lo.setDBStart();
        List<Member> members = memberRepository.findAllById(ids);
        lo.setDBEnd();
        members.stream().map(m -> {
            memberInfos.add(new MemberInfo(m.getId(), m.getName()));
            return m;
        }).collect(Collectors.toList());

        SimplePageable simplePageable = PageUtil.createSimplePageable(pWakeUp);
        return new StudyMembersRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), memberInfos,
                simplePageable);
    }

    private List<MemberSchedule> calculateInserts(List<Schedule> userSchedules, List<String> dbScheduleNames,
            Member member) {
        List<Schedule> insertCandidates = userSchedules.stream()
                .filter(u -> !dbScheduleNames.contains(u.getScheduleName()))
                .collect(Collectors.toList());

        List<MemberSchedule> insertObj = new ArrayList<>();
        for (Schedule iCdd : insertCandidates) {
            insertObj.add(MemberSchedule.builder().member(member).schedule(iCdd)
                    .createdAt(DateUtil.getCurrentDateTime()).updatedAt(DateUtil.getCurrentDateTime()).build());
        }
        return insertObj;
    }

    private List<MemberSchedule> calculateUpdates(List<Schedule> userSchedules, List<String> dbScheduleNames,
            List<MemberSchedule> mSchedules) {
        List<Schedule> updateCandidates = userSchedules.stream()
                .filter(u -> dbScheduleNames.contains(u.getScheduleName()))
                .collect(Collectors.toList());
        List<String> scheduleNamesToUpdate = updateCandidates.stream()
                .map(u -> u.getScheduleName())
                .collect(Collectors.toList());

        List<MemberSchedule> updateObj = mSchedules.stream()
                .filter(schedule -> scheduleNamesToUpdate.contains(schedule.getSchedule().getScheduleName()))
                .map(schedule -> {
                    schedule.updateUpdatedAt(DateUtil.getCurrentDateTime());
                    return schedule;
                })
                .collect(Collectors.toList());

        return updateObj;
    }

    private List<MemberSchedule> calculateDeletes(List<String> userScheduleNames, List<MemberSchedule> mSchedules) {
        List<MemberSchedule> deleteObj = mSchedules.stream()
                .filter(d -> !userScheduleNames.contains(d.getSchedule().getScheduleName()))
                .collect(Collectors.toList());
        return deleteObj;
    }

}
