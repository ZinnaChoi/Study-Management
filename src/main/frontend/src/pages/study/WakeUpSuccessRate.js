import React, { useState, useEffect, useRef } from "react";
import "react-datepicker/dist/react-datepicker.css";
import { authClient } from "../../services/APIService";
import DatePickerComponent from "../../components/DatePicker";
import { Bar } from "react-chartjs-2";
import Chart from "chart.js/auto";
import TableComponent from "../../components/Table";

const WakeUpSuccessRate = () => {
  const [content, setContent] = useState([]);
  const [endDate, setEndDate] = useState(new Date());
  const oneWeekAgo = new Date();
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);
  const [startDate, setStartDate] = useState(oneWeekAgo);
  const [dataFetched, setDataFetched] = useState(false);
  const chartRef = useRef(null);
  const [chartInstance, setChartInstance] = useState(null);
  const [highestSuccessRateMember, setHighestSuccessRateMember] =
    useState(null);
  const [selectedPeriod, setSelectedPeriod] = useState("주간");

  const handlePeriodChange = (event) => {
    setSelectedPeriod(event.target.value);
    fetchData();
  };

  const formatDateString = (date) => {
    return date.toISOString().slice(0, 10).replace(/-/g, "");
  };

  const refreshWakeupStat = () => {
    if (startDate.getTime() === endDate.getTime()) {
      alert("시작일과 종료일은 같을 수 없습니다.");
      return;
    }
    if (startDate.getTime() > endDate.getTime()) {
      alert("종료일은 시작일보다 빠를 수 없습니다.");
      return;
    }
    fetchWakeupStat();
  };

  const fetchWakeupStat = (type = "WAKEUP") => {
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
          if (chartInstance) {
            chartInstance.destroy();
          }
        }
        console.log("기상 성공률 조회 성공");
        console.log(response.data.content);
      })
      .catch((error) => {
        alert(
          "기상 성공률 조회 실패: " +
            (error.response?.data.retMsg || "Unknown error")
        );
      });
  };

  const fetchData = async () => {
    let newStartDate, newEndDate;

    if (selectedPeriod === "주간") {
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
    } else if (selectedPeriod === "월간") {
      newStartDate = startDate;
      newEndDate = endDate;
    }

    try {
      const params = {
        type: "WAKEUP",
        startDate: formatDateString(newStartDate),
        endDate: formatDateString(newEndDate),
      };

      const response = await authClient.get("/stat", { params });

      if (response && response.data) {
        setContent(response.data.content);
        setDataFetched(true);
      }
    } catch (error) {
      alert(
        "기상 통계 조회 실패: " +
          (error.response?.data.retMsg || "Unknown error")
      );
    }
  };

  const renderChart = () => {
    if (chartRef.current) {
      const newChartInstance = new Chart(chartRef.current, {
        type: "bar",
        data: data,
        options: {
          maintainAspectRatio: false,
          responsive: true,
          scales: {
            y: {
              beginAtZero: true,
            },
          },
          elements: {
            bar: {
              borderWidth: 1,
            },
          },
          plugins: {
            legend: {
              display: true,
            },
          },
        },
      });
      setChartInstance(newChartInstance);
    }
  };

  const calculateSuccessRate = (memberData) => {
    const totalEntries = memberData.length;
    const successCount = memberData.filter((item) => item.score === 1).length;
    return (successCount / totalEntries) * 100;
  };

  useEffect(() => {
    fetchWakeupStat();
    renderChart();
  }, [startDate, endDate]);

  const groupDataByMember = (data) => {
    return (data || []).reduce((acc, item) => {
      const { memberName } = item;
      if (!acc[memberName]) {
        acc[memberName] = [];
      }
      acc[memberName].push(item);
      return acc;
    }, {});
  };

  const groupedData = groupDataByMember(content);

  const chartLabels = Object.keys(groupedData);

  const chartData = chartLabels.map((memberName) => {
    const userData = groupedData[memberName] || [];
    const successRate = calculateSuccessRate(userData);
    if (
      !highestSuccessRateMember ||
      successRate > highestSuccessRateMember.successRate
    ) {
      setHighestSuccessRateMember({
        memberName,
        successRate: successRate.toFixed(2),
      });
    }
    return {
      memberName,
      successRate: successRate.toFixed(2),
    };
  });

  const constantColor = "rgba(75, 192, 192, 0.8)";
  const data = {
    labels: chartData.map((item) => item.memberName),
    datasets: [
      {
        label: "기상 성공률",
        backgroundColor: constantColor,
        borderWidth: 1,
        data: chartData.map((item) => item.successRate),
      },
    ],
  };

  const generateColumns = (content) => {
    const uniqueMemberNames = [
      ...new Set(content.map((item) => item.memberName)),
    ];

    const dynamicColumns = uniqueMemberNames.map((memberName) => ({
      Header: memberName,
      accessor: memberName.toLowerCase(),
    }));

    return [{ Header: "날짜", accessor: "date" }, ...dynamicColumns];
  };

  const columns = generateColumns(content);

  const getTableCell = (content, memberName) => {
    const memberData = content.find((item) => item.memberName === memberName);
    return memberData ? (memberData.score === 1 ? "O" : "X") : "-";
  };

  const uniqueDates = [...new Set(content.map((item) => item.date))].sort();
  const tableData = uniqueDates.map((date) => {
    const tableRow = {
      date: date,
      ...Object.fromEntries(
        columns.slice(1).map((col) => [
          col.accessor,
          getTableCell(
            content.filter((item) => item.date === date),
            col.accessor
          ),
        ])
      ),
    };
    return tableRow;
  });

  return (
    <React.Fragment>
      <div>
        <div className="select-date">
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
          </div>

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
                onClick={refreshWakeupStat}
                className="searchButton search-btn"
              >
                검색
              </button>
            </div>
          </div>
        </div>

        <div>
          <div className="chart-container">
            <Bar
              data={data}
              options={{
                maintainAspectRatio: false,
                responsive: true,
                scales: {
                  y: {
                    beginAtZero: true,
                  },
                },
                elements: {
                  bar: {
                    borderWidth: 1,
                  },
                },
                plugins: {
                  legend: {
                    display: true,
                  },
                },
              }}
            />
          </div>
          <div>
            {highestSuccessRateMember && (
              <p className="attendance-king">
                기상왕은 {highestSuccessRateMember.memberName} 입니다.
              </p>
            )}
          </div>
          <TableComponent columns={columns} contents={tableData} />
        </div>
      </div>
    </React.Fragment>
  );
};

export default WakeUpSuccessRate;
