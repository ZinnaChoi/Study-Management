import React from "react";
import ReactDOM from "react-dom/client";
import Header from "./layout/Header";
import Footer from "./layout/Footer";
import App from "./App";
import CssBaseline from "@mui/material/CssBaseline";

const root = ReactDOM.createRoot(document.getElementById("root"));
const styles = (theme) => ({
  layout: {
    width: "auto",
    marginLeft: theme.spacing.unit * 3,
    marginRight: theme.spacing.unit * 3,
    [theme.breakpoints.up(1100 + theme.spacing.unit * 3 * 2)]: {
      width: 1100,
      marginLeft: "auto",
      marginRight: "auto",
    },
  },
});
root.render(
  <React.Fragment>
    <CssBaseline />
    <React.StrictMode>
      <div className={styles.layout}>
        <Header />
        <App />
        <Footer />
      </div>
    </React.StrictMode>
  </React.Fragment>
);
