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
