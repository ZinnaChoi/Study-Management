export const getCurrentDateTime = () => {
  const now = new Date();
  const format = (num, length = 2) => String(num).padStart(length, "0");

  const year = now.getFullYear();
  const month = format(now.getMonth() + 1);
  const day = format(now.getDate());
  const hours = format(now.getHours());
  const minutes = format(now.getMinutes());
  const seconds = format(now.getSeconds());
  const milliseconds = format(now.getMilliseconds(), 3);

  return `${year}${month}${day}${hours}${minutes}${seconds}${milliseconds}`;
};
