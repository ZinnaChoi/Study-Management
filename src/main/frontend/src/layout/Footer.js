import React from "react";
import Typography from "@mui/material/Typography";

const classes = (theme) => ({
  footer: {
    backgroundColor: theme.palette.background.paper,
    marginTop: theme.spacing.unit * 8,
    padding: `${theme.spacing.unit * 6}px 0`,
  },
});

// Footer에 해당
const Footer = () => {
  return (
    <React.Fragment>
      <hr />
      <footer className={classes.footer}>
        <Typography variant="h6" align="center" gutterBottom>
          Footer
        </Typography>
        <Typography
          variant="subtitle1"
          align="center"
          color="textSecondary"
          component="p"
        >
          This is footer
        </Typography>
      </footer>
    </React.Fragment>
  );
};

export default Footer;
