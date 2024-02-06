export const format = (num, length = 2) => String(num).padStart(length, "0");

export const getCurrentYearMonth = (dateInfo) => {
  const start = new Date(dateInfo.start);
  let year = start.getFullYear();
  let month =
    start.getDate() > 20 ? start.getMonth() + 2 : start.getMonth() + 1;
  if (month === 13) {
    month = 1;
    year++;
  }
  return `${year}${format(month)}`;
};

export const getCurrentDateTime = () => {
  const now = new Date();

  const year = now.getFullYear();
  const month = format(now.getMonth() + 1);
  const day = format(now.getDate());
  const hours = format(now.getHours());
  const minutes = format(now.getMinutes());
  const seconds = format(now.getSeconds());
  const milliseconds = format(now.getMilliseconds(), 3);

  return `${year}${month}${day}${hours}${minutes}${seconds}${milliseconds}`;
};

export const parseDate = (dateString) => {
  const dateComponents = [
    { start: 0, end: 4, offset: 0 }, // Year
    { start: 4, end: 6, offset: -1 }, // Month
    { start: 6, end: 8, offset: 0 }, // Day
    { start: 8, end: 10, offset: 0 }, // Hour
    { start: 10, end: 12, offset: 0 }, // Minute
    { start: 12, end: 14, offset: 0 }, // Second
    { start: 14, end: 17, offset: 0 }, // Millisecond
  ];

  const args = dateComponents.map(
    ({ start, end, offset }) =>
      parseInt(dateString.substring(start, end), 10) + offset
  );

  return new Date(...args);
};
