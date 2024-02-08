import "../styles/Table.css";

const Table = ({ columns, contents }) => {
  return (
    <table
      style={{ width: "100%", borderCollapse: "collapse", marginTop: "20px" }}
    >
      <thead>
        <tr>
          {columns.map((column) => (
            <th key={column.accessor} className="th">
              {column.Header}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {contents.map((content, index) => (
          <tr key={index}>
            {columns.map((column) => (
              <td key={column.accessor} className="td">
                {column.Cell ? column.Cell(content) : content[column.accessor]}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default Table;
