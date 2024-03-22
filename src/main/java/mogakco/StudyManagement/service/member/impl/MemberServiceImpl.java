package mogakco.StudyManagement.service.member.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
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
import mogakco.StudyManagement.dto.CommonRes;
import mogakco.StudyManagement.dto.MemberDetails;
import mogakco.StudyManagement.dto.MemberIdDuplReq;
import mogakco.StudyManagement.dto.MemberInfo;
import mogakco.StudyManagement.dto.MemberInfoRes;
import mogakco.StudyManagement.dto.MemberInfoUpdateReq;
import mogakco.StudyManagement.dto.MemberInfosReq;
import mogakco.StudyManagement.dto.MemberInfosRes;
import mogakco.StudyManagement.dto.MemberJoinReq;
import mogakco.StudyManagement.dto.MemberListRes;
import mogakco.StudyManagement.dto.MemberLoginReq;
import mogakco.StudyManagement.dto.MemberLoginRes;
import mogakco.StudyManagement.dto.RegistedSchedule;
import mogakco.StudyManagement.dto.RegistedScheduleRes;
import mogakco.StudyManagement.dto.RegistedWakeupRes;
import mogakco.StudyManagement.dto.SimplePageable;
import mogakco.StudyManagement.dto.StudyMembersRes;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.MemberRole;
import mogakco.StudyManagement.enums.MemberSearchType;
import mogakco.StudyManagement.repository.MemberRepository;
import mogakco.StudyManagement.repository.MemberScheduleRepository;
import mogakco.StudyManagement.repository.NoticeRepository;
import mogakco.StudyManagement.repository.ScheduleRepository;
import mogakco.StudyManagement.repository.StudyInfoRepository;
import mogakco.StudyManagement.repository.WakeUpRepository;
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
    private final NoticeRepository noticeRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.expired.time}")
    private Long expiredTime;

    @Value("${study.systemId}")
    protected String systemId;

    @Value("${viewer-id}")
    protected String viewerId;

    public MemberServiceImpl(MemberRepository memberRepository,
            MemberScheduleRepository memberScheduleRepository,
            WakeUpRepository wakeUpRepository,
            ScheduleRepository scheduleRepository,
            StudyInfoRepository studyInfoRepository,
            NoticeRepository noticeRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            JWTUtil jwtUtil,
            RedisTemplate<String, Object> redisTemplate) {
        this.memberRepository = memberRepository;
        this.memberScheduleRepository = memberScheduleRepository;
        this.wakeUpRepository = wakeUpRepository;
        this.scheduleRepository = scheduleRepository;
        this.studyInfoRepository = studyInfoRepository;
        this.noticeRepository = noticeRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
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
    public MemberLoginRes login(MemberLoginReq loginInfo) {
        MemberLoginRes response = new MemberLoginRes();

        Member member = memberRepository.findById(loginInfo.getId());

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

        MemberRole role = member.getRole();
        String token = jwtUtil.createJwt(member.getId(), role.toString(), expiredTime);
        try {
            // redis에 JWT:admin(key) / 23jijiofj2io3hi32hiongiodsninioda(value) 형태로 저장
            redisTemplate.opsForValue().set("JWT:" + member.getId(), token, jwtUtil.getExpiration(token),
                    TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setRetMsg(ErrorCode.OK.getMessage());
        response.setRetCode(ErrorCode.OK.getCode());
        response.setToken(token);
        response.setRole(role.toString());
        return response;
    }

    @Override
    public CommonRes logout() {
        String loginUserId = SecurityUtil.getLoginUserId();

        // redis에 접속한 아이디 정보가 있다면 삭제(정보가 있다는 것은 아직 로그아웃 하지 않은 것)
        if (redisTemplate.opsForValue().get("JWT:" + loginUserId) != null) {
            redisTemplate.delete("JWT:" + loginUserId); // redis 내 Token 삭제
        }

        return new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
    }

    @Override
    @Transactional
    public CommonRes join(MemberJoinReq joinInfo) {
        CommonRes result = new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        Member member = Member.builder()
                .id(joinInfo.getId())
                .password(bCryptPasswordEncoder.encode(joinInfo.getPassword()))
                .name(joinInfo.getName())
                .email(joinInfo.getEmail())
                .role(MemberRole.USER) // 회원가입시 무조건 USER 권한
                .createdAt(DateUtil.getCurrentDateTime())
                .updatedAt(DateUtil.getCurrentDateTime())
                .build();
        WakeUp wakeUp = WakeUp.builder()
                .member(member)
                .wakeupTime(joinInfo.getWakeupTime())
                .build();

        List<MemberSchedule> mSchedules = new ArrayList<>();

        List<Schedule> schedules = scheduleRepository.findAllByScheduleNameIn(joinInfo.getScheduleNames());

        if (schedules.size() != 0) {
            for (Schedule s : schedules) {
                mSchedules.add(MemberSchedule.builder()
                        .member(member)
                        .schedule(s)
                        .createdAt(DateUtil.getCurrentDateTime())
                        .updatedAt(DateUtil.getCurrentDateTime())
                        .build());
            }

            memberRepository.save(member);
            wakeUpRepository.save(wakeUp);
            memberScheduleRepository.saveAll(mSchedules);
            noticeRepository.insertNoticeForMember(member);

        } else {
            result = new CommonRes(systemId, ErrorCode.BAD_REQUEST.getCode(),
                    ErrorCode.BAD_REQUEST.getMessage(joinInfo.getScheduleNames() + " Schedule is Not Regist"));
        }

        return result;
    }

    @Override
    @Transactional
    public CommonRes withdraw() {
        try {
            String loginUserId = SecurityUtil.getLoginUserId();
            Member member = memberRepository.findById(loginUserId);

            if (loginUserId.equals(viewerId)) {
                throw new InvalidRequestException(viewerId + " 계정은 회원 탈퇴가 불가능 합니다.");
            }

            if (loginUserId == null || member == null) {
                throw new NullPointerException("삭제할 회원 아이디가 올바르지 않습니다.");
            }

            // redis에 접속한 아이디 정보가 있다면 삭제(회원 탈퇴 시 JWT 만료시키기 위함)
            if (redisTemplate.opsForValue().get("JWT:" + loginUserId) != null) {
                redisTemplate.delete("JWT:" + loginUserId);
            }
            memberRepository.delete(member);

        } catch (NullPointerException | InvalidRequestException e) {
            return ExceptionUtil.handleException(new InvalidRequestException(e.getMessage()));
        }

        return new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
    }

    @Override
    public boolean isIdDuplicated(MemberIdDuplReq idInfo) {

        boolean isExist = memberRepository.existsById(idInfo.getId());
        return isExist;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberInfoRes getMemberInfo() {
        MemberInfoRes result = new MemberInfoRes();

        result.setSystemId(systemId);
        result.setRetCode(ErrorCode.OK.getCode());
        result.setRetMsg(ErrorCode.OK.getMessage());
        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        result.setId(member.getId());
        result.setName(member.getName());
        result.setEmail(member.getEmail());
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
    @Transactional(readOnly = true)
    public MemberInfosRes getMembersInfo(MemberInfosReq memberInfosReq, Pageable pageable) {

        String searchKeyWord = memberInfosReq.getSearchKeyWord().trim();
        Page<Object[]> members;

        if (searchKeyWord.length() == 0) {
            members = memberRepository.findMembersWithSchedulesAndWakeupTime(pageable);
        } else {
            if (memberInfosReq.getSearchType() == MemberSearchType.PARTICIPATION) {
                members = memberRepository.findMembersWithSchedulesAndWakeupTimeByScheduleName(searchKeyWord,
                        pageable);
            } else {
                String comparisonOperators = memberInfosReq.getComparisonOperators();
                members = memberRepository.findMembersWithSchedulesAndWakeupTimeByWakeupTime(searchKeyWord,
                        comparisonOperators,
                        pageable);
            }
        }

        List<Object[]> queryResults = members.getContent();
        List<MemberInfo> membersInfo = new ArrayList<>();

        for (Object[] row : queryResults) {
            // 각 row에서 필요한 데이터 추출
            String id = (String) row[0];
            String name = (String) row[1];
            String[] scheduleNamesArray = ((String) row[2]).split(", ");
            List<String> scheduleNames = Arrays.asList(scheduleNamesArray);
            String wakeupTime = (String) row[3];

            membersInfo.add(
                    MemberInfo.builder().id(id).name(name).scheduleNames(scheduleNames).wakeupTime(wakeupTime).build());
        }

        return new MemberInfosRes(systemId, ErrorCode.OK.getCode(),
                ErrorCode.OK.getMessage(), membersInfo,
                PageUtil.createSimplePageable(members));
    }

    @Override
    @Transactional(readOnly = true)
    public MemberListRes getMemberList() {
        List<Member> content = memberRepository.findAll();
        return new MemberListRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), content);
    }

    @Override
    @Transactional
    public CommonRes setMemberInfo(MemberInfoUpdateReq updateInfo) {
        CommonRes result = new CommonRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());

        Member member = memberRepository.findById(SecurityUtil.getLoginUserId());
        switch (updateInfo.getType()) {
            case NAME:
                // member 테이블 업데이트
                String changedName = updateInfo.getName();
                if (changedName == null || changedName.isBlank()) {
                    return ExceptionUtil.handleException(new InvalidRequestException("변경할 이름을 입력해주세요"));
                }
                member.updateName(changedName);

                memberRepository.save(member);

                break;
            case SCHEDULE_NAMES:
                // member_schedule 테이블 업데이트
                List<String> userScheduleNames = updateInfo.getScheduleName();
                if (userScheduleNames == null || userScheduleNames.size() == 0) {
                    return ExceptionUtil.handleException(new InvalidRequestException("참여 스케줄을 선택해주세요"));
                }

                List<MemberSchedule> mSchedules = memberScheduleRepository.findAllByMember(member);
                List<Schedule> userSchedules = scheduleRepository.findAllByScheduleNameIn(userScheduleNames);

                List<String> dbScheduleNames = mSchedules.stream().map(m -> m.getSchedule().getScheduleName())
                        .collect(Collectors.toList());

                List<MemberSchedule> toInsert = calculateInserts(userSchedules, dbScheduleNames, member);
                List<MemberSchedule> toUpdate = calculateUpdates(userSchedules, dbScheduleNames, mSchedules);
                List<MemberSchedule> toDelete = calculateDeletes(userScheduleNames, mSchedules);

                memberScheduleRepository.deleteAll(toDelete);
                memberScheduleRepository.saveAll(toInsert);
                memberScheduleRepository.saveAll(toUpdate);

                break;
            case WAKEUP:
                // wakeup 테이블 업데이트
                String changedWakeupTime = updateInfo.getWakeupTime();
                if (changedWakeupTime == null || changedWakeupTime.isBlank()) {
                    return ExceptionUtil.handleException(new InvalidRequestException("변경할 기상 시간을 선택해주세요"));
                }
                WakeUp wakeUp = wakeUpRepository.findByMember(member);
                wakeUp.updateWakeupTime(changedWakeupTime);

                wakeUpRepository.save(wakeUp);

                break;

            case EMAIL:
                // member 테이블 업데이트
                String changedEmail = updateInfo.getEmail();
                member.updateEmail(changedEmail);

                memberRepository.save(member);

                break;
            case PASSWORD:
                // member 테이블 업데이트
                String prePassword = updateInfo.getPrePassword();
                String changedPassword = updateInfo.getPassword();
                String originPassword = member.getPassword();

                // 비밀번호 현재 비밀번호랑 일치 여부 확인
                if (bCryptPasswordEncoder.matches(changedPassword, originPassword)) {
                    return ExceptionUtil.handleException(new InvalidRequestException("현재 비밀번호와 동일합니다."));
                } else if (!bCryptPasswordEncoder.matches(prePassword, originPassword)) {
                    return ExceptionUtil.handleException(new InvalidRequestException("현재 비밀번호가 일치하지 않습니다."));
                }
                // 비밀번호 암호화
                member.updatePassword(bCryptPasswordEncoder.encode(changedPassword));

                memberRepository.save(member);

                break;
            default:
                break;
        }

        return result;

    }

    @Override
    public StudyMembersRes getMembersBySchedule(String sName, Pageable pageable) {

        List<MemberInfo> memberInfos = new ArrayList<>();
        List<Member> members = new ArrayList<>();
        SimplePageable simplePageable;

        if (sName == null) { // 전체 조회

            // admin 계정은 조회 X
            Page<Member> pMembers = memberRepository.findAllByRoleNot(MemberRole.ADMIN,
                    pageable);

            members = pMembers.getContent();
            simplePageable = PageUtil.createSimplePageable(pMembers);
        } else {
            // 멤버 스케줄에서 스케줄 이름으로 조건 걸어서 가져온 member_id로 member에서 id, name 조회

            Schedule schedule = scheduleRepository.findByScheduleName(sName);
            Page<MemberSchedule> pSchedules = memberScheduleRepository.findAllBySchedule(schedule, pageable);

            List<Member> mSchedules = pSchedules.getContent().stream().map(MemberSchedule::getMember)
                    .collect(Collectors.toList());
            Set<Long> ids = mSchedules.stream().map(Member::getMemberId).collect(Collectors.toSet());

            members = memberRepository.findAllById(ids);

            simplePageable = PageUtil.createSimplePageable(pSchedules);
        }

        members.stream().map(m -> {
            List<MemberSchedule> memberSchedules = memberScheduleRepository.findAllByMember(m);
            List<String> scheduleNames = memberSchedules.stream().map(ms -> ms.getSchedule().getScheduleName())
                    .collect(Collectors.toList());
            memberInfos.add(MemberInfo.builder().id(m.getId()).name(m.getName()).scheduleNames(scheduleNames).build());
            return m;
        }).collect(Collectors.toList());

        return new StudyMembersRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), memberInfos,
                simplePageable);
    }

    @Override
    public StudyMembersRes getMembersByWakeupTime(String time, Pageable pageable) {

        Page<WakeUp> pWakeUp;
        List<MemberInfo> memberInfos = new ArrayList<>();

        pWakeUp = time == null ? wakeUpRepository.findAll(pageable)
                : wakeUpRepository.findAllByWakeupTime(time, pageable);

        List<Member> mWakeUp = pWakeUp.getContent().stream().map(WakeUp::getMember)
                .collect(Collectors.toList());
        List<Long> ids = mWakeUp.stream().map(Member::getMemberId).collect(Collectors.toList());

        List<Member> members = memberRepository.findAllById(ids);

        members.stream().map(m -> {
            WakeUp wakeUp = wakeUpRepository.findByMember(m);
            memberInfos.add(
                    MemberInfo.builder().id(m.getId()).name(m.getName()).wakeupTime(wakeUp.getWakeupTime()).build());
            return m;
        }).collect(Collectors.toList());

        SimplePageable simplePageable = PageUtil.createSimplePageable(pWakeUp);
        return new StudyMembersRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), memberInfos,
                simplePageable);
    }

    @Override
    public RegistedScheduleRes getRegistedSchedule() {

        // 스케줄 테이블에서 전체 조회 후 set

        List<Schedule> schedules = scheduleRepository.findAll();

        List<RegistedSchedule> result = new ArrayList<>();
        for (Schedule sch : schedules) {
            RegistedSchedule registedSchedule = new RegistedSchedule();
            registedSchedule.setScheduleId(sch.getScheduleId());
            registedSchedule.setScheduleName(sch.getScheduleName());
            registedSchedule.setStartTime(sch.getStartTime());
            registedSchedule.setEndTime(sch.getEndTime());

            result.add(registedSchedule);
        }

        return new RegistedScheduleRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), result);
    }

    @Override
    public RegistedWakeupRes getRegistedWakeup() {
        // 기상 시간 테이블에서 전체 조회 후 set

        List<WakeUp> wakeUps = wakeUpRepository.findAll();

        Set<String> result = wakeUps.stream().map(WakeUp::getWakeupTime).sorted()
                .collect(Collectors.toCollection(TreeSet::new));

        return new RegistedWakeupRes(systemId, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), result);
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
