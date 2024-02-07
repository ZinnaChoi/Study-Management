import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";

const AbsentCalendar = ({
  events,
  getYearMonth,
  renderEventContent,
  onDateClick,
}) => {
  function renderEventContent(eventInfo) {
    return (
      <>
        <div style={{ color: "black" }}>{eventInfo.event.title}</div>
      </>
    );
  }

  return (
    <FullCalendar
      plugins={[dayGridPlugin, interactionPlugin]}
      initialView="dayGridMonth"
      events={events}
      datesSet={getYearMonth}
      eventContent={renderEventContent}
      dateClick={(event) => onDateClick(event.dateStr)}
    />
  );
};
export default AbsentCalendar;
