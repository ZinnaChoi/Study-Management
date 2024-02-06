const Table = ({ columns, contents }) => {
  const thStyle = {
    backgroundColor: "#375582",
    color: "white",
    padding: "10px",
    border: "1px solid #dee2e6",
  };

  const tdStyle = {
    padding: "10px",
    border: "1px solid #dee2e6",
    textAlign: "center",
  };

  return (
    <table
      style={{ width: "100%", borderCollapse: "collapse", marginTop: "20px" }}
    >
      <thead>
        <tr>
          {columns.map((column) => (
            <th key={column.accessor} style={thStyle}>
              {column.Header}
            </th>
          ))}
        </tr>
      </thead>
      <tbody>
        {contents.map((content, index) => (
          <tr key={index}>
            {columns.map((column) => (
              <td key={column.accessor} style={tdStyle}>
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
