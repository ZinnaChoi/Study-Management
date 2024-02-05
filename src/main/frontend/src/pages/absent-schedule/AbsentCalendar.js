import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";

const AbsentCalendar = ({ events, getYearMonth, renderEventContent }) => {
  return (
    <FullCalendar
      plugins={[dayGridPlugin, interactionPlugin]}
      initialView="dayGridMonth"
      events={events}
      datesSet={getYearMonth}
      eventContent={renderEventContent}
    />
  );
};
export default AbsentCalendar;
