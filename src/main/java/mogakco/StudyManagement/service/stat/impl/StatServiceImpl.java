package mogakco.StudyManagement.service.stat.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import mogakco.StudyManagement.domain.DailyLog;
import mogakco.StudyManagement.dto.SimplePageable;
import mogakco.StudyManagement.dto.StatGetRes;
import mogakco.StudyManagement.dto.StatList;
import mogakco.StudyManagement.enums.ErrorCode;
import mogakco.StudyManagement.enums.LogType;
import mogakco.StudyManagement.repository.StatRepository;
import mogakco.StudyManagement.service.common.LoggingService;
import mogakco.StudyManagement.service.stat.StatService;
import mogakco.StudyManagement.util.PageUtil;

@Service
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @Override
    public StatGetRes getStat(LogType type, LoggingService lo, Pageable pageable) {

        Page<DailyLog> dailyLogs;

        lo.setDBStart();
        dailyLogs = statRepository.findByType(type, pageable);
        lo.setDBEnd();

        List<StatList> dailyLogLists = dailyLogs.getContent().stream().map(StatList::new).collect(Collectors.toList());

        SimplePageable simplePageable = PageUtil.createSimplePageable(dailyLogs);

        StatGetRes result = new StatGetRes(null, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), dailyLogLists,
                simplePageable);

        return result;
    }

}
