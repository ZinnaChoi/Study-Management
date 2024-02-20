import React, { useState, useEffect } from "react";
import { Line } from "react-chartjs-2";
import { authClient } from "../../services/APIService";
import "react-datepicker/dist/react-datepicker.css";
import "../../styles/Button.css";
import "../../styles/Stat.css";
import DatePickerComponent from "../../components/DatePicker";
import TableComponent from "../../components/Table";

const AttendanceStatistics = () => {
  const [content, setContent] = useState([]);
  const [attendanceMaxScore, setAttendanceMaxScore] = useState([]);
  const [endDate, setEndDate] = useState(new Date());
  const oneWeekAgo = new Date();
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);
  const [startDate, setStartDate] = useState(oneWeekAgo);
  const [chartData, setChartData] = useState(null);
  const [attendanceKing, setAttendanceKing] = useState(null);
  const [dataFetched, setDataFetched] = useState(false);
  const memberColors = {};
  const [selectedPeriod, setSelectedPeriod] = useState("주간");

  const handlePeriodChange = (event) => {
    setSelectedPeriod(event.target.value);
    fetchData();
  };

  const formatDateString = (date) => {
    return date.toISOString().slice(0, 10).replace(/-/g, "");
  };

  const refreshAttendanceStat = () => {
    if (startDate.getTime() === endDate.getTime()) {
      alert("시작일과 종료일은 같을 수 없습니다.");
      return;
    }
    if (startDate.getTime() > endDate.getTime()) {
      alert("종료일은 시작일보다 빠를 수 없습니다.");
      return;
    }
  };

  const fetchAttendanceStat = (type = "ABSENT") => {
    const params = {
      type: type,
      startDate: formatDateString(startDate),
      endDate: formatDateString(endDate),
    };

    authClient
      .get("/stat", { params })
      .then((response) => {
        if (response && response.data) {
          setContent(response.data.content);
          setAttendanceMaxScore(response.data.attendanceMaxScore);
          setDataFetched(true);
        }
        console.log("출석 통계 조회 성공");
      })
      .catch((error) => {
        alert(
          "출석 통계 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const fetchData = async () => {
    let newStartDate, newEndDate;

    if (selectedPeriod === "주간") {
      newStartDate = startDate;
      newEndDate = endDate;
    } else if (selectedPeriod === "월간") {
      const currentDate = new Date();
      newStartDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth(),
        1
      );
      newEndDate = new Date(
        currentDate.getFullYear(),
        currentDate.getMonth() + 1,
        0
      );
    }

    try {
      const params = {
        type: "ABSENT",
        startDate: formatDateString(newStartDate),
        endDate: formatDateString(newEndDate),
      };

      const response = await authClient.get("/stat", { params });

      if (response && response.data) {
        setContent(response.data.content);
        setAttendanceMaxScore(response.data.attendanceMaxScore);
        setDataFetched(true);

        updateChartAndTable(response.data.content);
      }
    } catch (error) {
      alert(
        "출석 통계 조회 실패: " +
          (error.response?.data.retMsg || "Unknown error")
      );
    }
  };
  const findAttendanceKing = (groupedData) => {
    let highestRatio = -1;
    let attendanceKing = "";

    const dayDifference = Math.abs(
      (startDate.getTime() - endDate.getTime()) / (1000 * 60 * 60 * 24)
    );

    const attendanceMaxScoreData = attendanceMaxScore.reduce((acc, entry) => {
      if (!acc[entry.memberName]) {
        acc[entry.memberName] = 0;
      }
      acc[entry.memberName] += entry.score;
      return acc;
    }, {});

    Object.entries(groupedData).forEach(([memberName, values]) => {
      const totalScore = values.scores.reduce((acc, score) => acc + score, 0);
      const attendanceMaxScore =
        attendanceMaxScoreData[memberName] * values.scores.length;
      const ratio = totalScore / attendanceMaxScore;

      if (ratio > highestRatio) {
        highestRatio = ratio;
        attendanceKing = memberName;
      }
    });

    return attendanceKing;
  };

  const updateChartAndTable = (content) => {
    const groupedData = content.reduce((acc, entry) => {
      if (!acc[entry.memberName]) {
        acc[entry.memberName] = { labels: [], scores: [] };
      }
      acc[entry.memberName].labels.push(entry.date);
      acc[entry.memberName].scores.push(entry.score);
      return acc;
    }, {});

    const datasets = Object.entries(groupedData).map(
      ([memberName, values]) => ({
        label: memberName,
        data: values.scores,
        fill: false,
        borderColor: memberColors[memberName],
      })
    );

    const labels = Object.values(groupedData)[0]
      .labels.map((date) => date.slice(2))
      .sort();

    const updatedChartData = {
      labels: labels,
      datasets: datasets,
    };

    setChartData(updatedChartData);

    const attendanceKing = findAttendanceKing(groupedData);
    setAttendanceKing(`출석왕은 ${attendanceKing} 입니다.`);
  };

  const generateSummaryData = () => {
    const summaryData = content.reduce((acc, entry) => {
      if (!acc[entry.memberName]) {
        acc[entry.memberName] = { totalScore: 0, count: 0 };
      }
      acc[entry.memberName].totalScore += entry.score;
      acc[entry.memberName].count += 1;
      return acc;
    }, {});

    const attendanceMaxScoreData = attendanceMaxScore.reduce((acc, entry) => {
      if (!acc[entry.memberName]) {
        acc[entry.memberName] = 0;
      }
      acc[entry.memberName] += entry.score;
      return acc;
    }, {});
    const tableData = Object.keys(summaryData).map((memberName) => {
      const totalScore = summaryData[memberName].totalScore;
      const count = summaryData[memberName].count;
      const attendanceMaxScore = attendanceMaxScoreData[memberName] * count;

      return {
        Member: memberName,
        TotalScore: totalScore,
        AttendanceMaxScore: attendanceMaxScore,
      };
    });
    return tableData;
  };

  useEffect(() => {
    const fetchDataAndGenerateChart = async () => {
      try {
        setContent([]);
        setAttendanceMaxScore([]);
        setChartData(null);
        setAttendanceKing(null);

        await fetchAttendanceStat();
        await fetchData();
      } catch (error) {
        alert(
          "출석 통계 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      }
    };

    fetchDataAndGenerateChart();
  }, [startDate, endDate, selectedPeriod, dataFetched]);

  return (
    <React.Fragment>
      <div>
        <div className="radio-button">
          <label>
            <input
              type="radio"
              value="주간"
              checked={selectedPeriod === "주간"}
              onChange={handlePeriodChange}
            />
            주간
          </label>
          <label>
            {" "}
            <input
              type="radio"
              value="월간"
              checked={selectedPeriod === "월간"}
              onChange={handlePeriodChange}
            />
            월간
          </label>
          <div style={{ marginBottom: "20px" }}> </div>
          <div className="date-picker">
            <div>
              <DatePickerComponent
                selectedDate={startDate}
                onChange={(date) => setStartDate(date)}
              />
              ~
              <DatePickerComponent
                selectedDate={endDate}
                onChange={(date) => setEndDate(date)}
              />
            </div>
            <div>
              <button
                onClick={refreshAttendanceStat}
                className="searchButton search-btn"
              >
                검색
              </button>
            </div>
          </div>
        </div>

        {chartData && (
          <>
            <Line
              data={chartData}
              options={{
                scales: {
                  x: {
                    type: "linear",
                    position: "bottom",
                    ticks: {
                      stepSize: 1,
                      callback: (value) => value.toString().slice(2),
                    },
                  },
                  y: {
                    min: 0,
                    max: 10,
                  },
                },
              }}
            />
            <div>
              {attendanceKing && (
                <p className="attendance-king">{attendanceKing}</p>
              )}
            </div>
            <TableComponent
              columns={[
                { Header: "참여 멤버", accessor: "Member" },
                { Header: "출석 점수", accessor: "TotalScore" },
                { Header: "최대 출석 점수", accessor: "AttendanceMaxScore" },
              ]}
              contents={generateSummaryData()}
            />
          </>
        )}
      </div>
    </React.Fragment>
  );
};

export default AttendanceStatistics;
